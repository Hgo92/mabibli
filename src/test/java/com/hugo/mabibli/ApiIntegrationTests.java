package com.hugo.mabibli;

import com.hugo.mabibli.repository.BookRepository;
import com.hugo.mabibli.repository.LibraryRepository;
import com.hugo.mabibli.repository.SeriesRepository;
import com.hugo.mabibli.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers // Indique que Testcontainers va gérer les @Containers
@SpringBootTest // Indique qu'il faut lancer SpringBoot avec les tests
@AutoConfigureMockMvc // Indique qu'il faut lancer MockMvc (pour des faux appels HTTP)
@ActiveProfiles("test") // Indique qu'il faut utiliser les applications-properties de test

class ApiIntegrationTests {
    @Container // JUnit va créer le container pour ce test
    @ServiceConnection // Les infos sont transmises à Spring pour le test
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired MockMvc mockMvc; // Spring injecte le simulateur de requêtes http
    @Autowired ObjectMapper objectMapper; // Convertisseur JSON

    // Maintenant j'injecte mes repositories
    @Autowired BookRepository bookRepository;
    @Autowired LibraryRepository libraryRepository;
    @Autowired SeriesRepository seriesRepository;
    @Autowired UserRepository userRepository;

    // Cette méthode efface tout avant chaque test
    @BeforeEach
    void cleanDatabase() {
        bookRepository.deleteAll();
        libraryRepository.deleteAll();
        seriesRepository.deleteAll();
        userRepository.deleteAll();
    }

    // Cette méthode permet de retourner les objets java en JSON
    private String toJson(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    // La méthode crée un utilisateur et récupère le token
    private String registerAndGetToken(String username) throws Exception {
        String response = mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "username", username, "password", "Password123!"
                        )))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.path("token").asString();
    }

    // Méthode pour créer une bibliothèque
    private Long createLibrary(String token, String title) throws Exception {
        String response = mockMvc.perform(
                post("/api/libraries")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("title", title)
                        ))
        )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).path("id").asLong();
    }

    // Méthode pour créer un livre
    private Long addBook(String token, Long libraryId, String openLibraryId, String title) throws Exception {
        String response = mockMvc.perform(
                post("/api/libraries/{libraryId}/books", libraryId)
                .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "openLibraryId", openLibraryId,
                                "title", title,
                                "author", "Patrick Test",
                                "isbn", "9780000000000",
                                "status", "A_LIRE"
                        )))
        )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).path("id").asLong();
    }

    // Ce test va créer le compte, vérifier qu'il y a bien un token et que le mdp a bien été hashé
    @Test
    void registerCreatesUserWithEncryptedPassword() throws Exception {
        String token = registerAndGetToken("hugo");

        assertThat(token).isNotBlank();
        var user = userRepository
                .findByUsername("hugo")
                .orElseThrow();

        assertThat(user.getPassword())
                .isNotEqualTo("Password123!");

        assertThat(user.getPassword())
                .startsWith("$2");
    }

    // Ce test va créer un compte, puis vérifier qu'un nouveau compte avec le même username bloque
    @Test
    void registerRejectDuplicateUsername() throws Exception {
        registerAndGetToken("hugo");

        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "username", "hugo",
                                "password", "Password123!"
                        )))
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    // Ce test crée le compte puis test le login avec le mauvais mot de passe
    @Test
    void loginRejectsInvalidPassword() throws Exception {

        // Crée l'utilisateur avec le bon mot de passe.
        registerAndGetToken("hugo");

        // Tente de se connecter avec un mot de passe différent.
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(Map.of(
                                        "username", "hugo",
                                        "password", "MauvaisMotDePasse"
                                )))
                )

                // Une mauvaise authentification doit répondre 401.
                .andExpect(status().isUnauthorized());
    }

    // Ce test vérifie qu'une route protégée bloque si pas de token
    @Test
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/libraries")).andExpect(status().isUnauthorized());
    }

    // Tests pour les bibliothèques (création, modification, suppression)
    @Test
    void userCanCreateReadUpdateAndDeleteLibrary() throws Exception {

        String token = registerAndGetToken("hugo");
        Long libraryId = createLibrary(token, "Romans");

        // Crée la bibliothèque
        mockMvc.perform(
                        get("/api/libraries/{libraryId}", libraryId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(libraryId))
                .andExpect(jsonPath("$.title").value("Romans"));

        // Modifie le titre
        mockMvc.perform(
                        put("/api/libraries/{libraryId}", libraryId)
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(Map.of(
                                        "title", "Mes romans"
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mes romans"));

        // Supprime la bibliothèque.
        mockMvc.perform(
                        delete("/api/libraries/{libraryId}", libraryId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNoContent());

        // Tente de consulter la bibliothèque supprimée.
        mockMvc.perform(
                        get("/api/libraries/{libraryId}", libraryId)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }

    // Test pour vérifier qu'un utilisateur ne peut pas accéder/modifier à la bibliothèque d'un autre
    @Test
    void userCannotAccessAnotherUsersLibrary() throws Exception {

        String aliceToken = registerAndGetToken("alice");
        String bobToken = registerAndGetToken("bob");

        Long aliceLibraryId = createLibrary(
                aliceToken,
                "Bibliothèque Alice"
        );

        mockMvc.perform(
                        get("/api/libraries/{libraryId}", aliceLibraryId)
                                .header(
                                        "Authorization",
                                        "Bearer " + bobToken
                                )
                )
                .andExpect(status().isNotFound());

        mockMvc.perform(
                        delete("/api/libraries/{libraryId}", aliceLibraryId)
                                .header(
                                        "Authorization",
                                        "Bearer " + bobToken
                                )
                )
                .andExpect(status().isNotFound());

        assertThat(
                libraryRepository.existsById(aliceLibraryId)
        ).isTrue();
    }

    // Ce test vérifie qu'un livre peut être dans plusieurs bibliothèques
    @Test
    void sameBookDifferentLibraries() throws Exception {

        String token = registerAndGetToken("hugo");
        Long firstLibraryId = createLibrary(token, "Romans");
        Long secondLibraryId = createLibrary(token, "Favoris");

        addBook(token, firstLibraryId, "OL123W", "Dune");
        addBook(token, secondLibraryId, "OL123W", "Dune");

        assertThat(bookRepository.count()).isEqualTo(2);
    }

    // Ce test vérifie les doublons dans une bibliothèque
    @Test
    void sameBookCannotBeAddedTwiceToSameLibrary() throws Exception {

        String token = registerAndGetToken("hugo");
        Long libraryId = createLibrary(token, "Romans");
        addBook(token, libraryId, "OL123W", "Dune");

        mockMvc.perform(
                        post(
                                "/api/libraries/{libraryId}/books",
                                libraryId
                        )
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(Map.of(
                                        "openLibraryId", "OL123W",
                                        "title", "Dune",
                                        "author", "Frank Herbert"
                                )))
                )
                .andExpect(status().isConflict());
    }

    // Ce test vérifie la suppression en cascade (suppression de la bibli = suppression des livres)
    @Test
    void deletingLibraryDeletesItsBooks() throws Exception {

        String token = registerAndGetToken("hugo");
        Long libraryId = createLibrary(token, "Romans");
        addBook(token, libraryId, "OL1W", "Livre 1");
        addBook(token, libraryId, "OL2W", "Livre 2");

        assertThat(bookRepository.count()).isEqualTo(2);

        mockMvc.perform(
                        delete("/api/libraries/{libraryId}", libraryId)
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isNoContent());

        assertThat(bookRepository.count()).isZero();
        assertThat(libraryRepository.existsById(libraryId)).isFalse();
    }


}
