package com.hugo.mabibli.controller;

import com.hugo.mabibli.dto.AddSeriesRequest;
import com.hugo.mabibli.dto.SeriesResponse;
import com.hugo.mabibli.dto.UpdateSeriesRequest;
import com.hugo.mabibli.security.UserPrincipal;
import com.hugo.mabibli.service.SeriesService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/series")
public class SeriesController {
    private final SeriesService seriesService;
    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    // Ma route get pour récupérer toutes les séries
    @GetMapping
    public List<SeriesResponse> findAll(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return seriesService.findAll(principal.getUser());
    }

    // Ma route get pour récupérer une série précise
    @GetMapping("/{seriesId}")
    public SeriesResponse findOne(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long seriesId
    ) {
        return seriesService.findOne(principal.getUser(), seriesId);
    }

    @PostMapping
    public ResponseEntity<SeriesResponse> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddSeriesRequest request
    ) {
        SeriesResponse response = seriesService.create(
                principal.getUser(),
                request
        );

        return ResponseEntity
                .created(URI.create("/api/series/" + response.id()))
                .body(response);
    }

    @PatchMapping("/{seriesId}")
    public SeriesResponse update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long seriesId,
            @Valid @RequestBody UpdateSeriesRequest request
    ) {
        return seriesService.update(
                principal.getUser(),
                seriesId,
                request
        );
    }

    @DeleteMapping("/{seriesId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long seriesId
    ) {
        seriesService.delete(
                principal.getUser(),
                seriesId
        );

        return ResponseEntity.noContent().build();
    }


}
