package com.hugo.mabibli.service;

import com.hugo.mabibli.dto.DeleteAccountRequest;
import com.hugo.mabibli.entity.User;
import com.hugo.mabibli.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AccountService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void deleteAccount(
            User user,
            DeleteAccountRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.password()
                )
        );

        User managedUser =
                userRepository.findById(user.getId())
                        .orElseThrow();

        userRepository.delete(managedUser);
    }
}