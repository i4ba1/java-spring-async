package com.dev.funcinema.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.dev.funcinema.dto.MovieDTO;
import com.dev.funcinema.dto.MovieSearchDTO;
import com.dev.funcinema.service.MovieService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Asynchronous CRUD endpoints

    @PostMapping
    public CompletableFuture<ResponseEntity<MovieDTO>> createMovieAsync(
            @Valid @RequestBody MovieDTO movieDTO) {
        return movieService.createMovieAsync(movieDTO)
                .thenApply(createdMovie ->
                        new ResponseEntity<>(createdMovie, HttpStatus.CREATED));
    }

    @GetMapping("/getMovieById/{id}")
    public CompletableFuture<ResponseEntity<MovieDTO>> getMovieByIdAsync(
            @PathVariable Long id) {
        return movieService.getMovieByIdAsync(id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<MovieDTO>>> getAllMoviesAsync() {
        return movieService.getAllMoviesAsync()
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<MovieDTO>> updateMovieAsync(
            @PathVariable Long id,
            @Valid @RequestBody MovieDTO movieDTO) {
        return movieService.updateMovieAsync(id, movieDTO)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteMovieAsync(
            @PathVariable Long id) {
        return movieService.deleteMovieAsync(id)
                .thenApply(result -> ResponseEntity.noContent().<Void>build());
    }

    @PostMapping("/search/async")
    public CompletableFuture<ResponseEntity<Page<MovieDTO>>> searchMoviesAsync(
            @RequestBody MovieSearchDTO searchDTO) {
        return movieService.searchMoviesAsync(searchDTO)
                .thenApply(ResponseEntity::ok);
    }
}
