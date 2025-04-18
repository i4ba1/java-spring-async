package com.dev.funcinema.service;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.dev.funcinema.dto.MovieDTO;
import com.dev.funcinema.dto.MovieSearchDTO;
import org.springframework.data.domain.Page;

public interface MovieService {

    // Synchronous CRUD operations
    MovieDTO getMovieById(Long id);
    List<MovieDTO> getAllMovies();
    void deleteMovie(Long id);

    // Asynchronous CRUD operations using virtual threads
    CompletableFuture<MovieDTO> createMovieAsync(MovieDTO movieDTO);
    CompletableFuture<MovieDTO> getMovieByIdAsync(Long id);
    CompletableFuture<List<MovieDTO>> getAllMoviesAsync();
    CompletableFuture<MovieDTO> updateMovieAsync(Long id, MovieDTO movieDTO);
    CompletableFuture<Void> deleteMovieAsync(Long id);

    // Search operations
    Page<MovieDTO> searchMovies(MovieSearchDTO searchDTO);
    CompletableFuture<Page<MovieDTO>> searchMoviesAsync(MovieSearchDTO searchDTO);
}
