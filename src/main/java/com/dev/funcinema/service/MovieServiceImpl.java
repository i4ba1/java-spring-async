package com.dev.funcinema.service;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.dev.funcinema.dto.MovieDTO;
import com.dev.funcinema.dto.MovieSearchDTO;
import com.dev.funcinema.model.Movie;
import com.dev.funcinema.repository.MovieRepository;
import com.dev.funcinema.repository.MovieSpecifications;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    // Utility methods for conversion between entity and DTO
    private MovieDTO convertToDTO(Movie movie) {
        return MovieDTO.builder()
                .title(movie.getTitle())
                .director(movie.getDirector())
                .genres(movie.getGenres())
                .releaseDate(movie.getReleaseDate())
                .durationMinutes(movie.getDurationMinutes())
                .rating(movie.getRating())
                .plot(movie.getPlot())
                .featured(movie.isFeatured())
                .build();
    }

    private Movie convertToEntity(MovieDTO movieDTO) {
        return Movie.builder()
                .title(movieDTO.getTitle())
                .director(movieDTO.getDirector())
                .genres(movieDTO.getGenres())
                .releaseDate(movieDTO.getReleaseDate())
                .durationMinutes(movieDTO.getDurationMinutes())
                .rating(movieDTO.getRating())
                .plot(movieDTO.getPlot())
                .featured(movieDTO.isFeatured())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
        return convertToDTO(movie);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        // Check if movie exists
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with ID: " + id);
        }

        movieRepository.deleteById(id);
    }

    // Asynchronous CRUD operations using virtual threads

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<MovieDTO> createMovieAsync(MovieDTO movieDTO) {
        // Directly implement the movie creation logic within the async method
        Movie movie = convertToEntity(movieDTO);
        Movie savedMovie = movieRepository.save(movie);
        MovieDTO createdMovieDTO = convertToDTO(savedMovie);

        // Return the result wrapped in a CompletableFuture
        return CompletableFuture.completedFuture(createdMovieDTO);
    }

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<MovieDTO> getMovieByIdAsync(Long id) {
        // Directly implement the movie retrieval logic
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found with ID: " + id));
        MovieDTO movieDTO = convertToDTO(movie);

        return CompletableFuture.completedFuture(movieDTO);
    }

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<MovieDTO>> getAllMoviesAsync() {
        // Direct implementation of retrieving all movies
        List<MovieDTO> movies = movieRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return CompletableFuture.completedFuture(movies);
    }

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<MovieDTO> updateMovieAsync(Long id, MovieDTO movieDTO) {
        // Direct implementation of movie update
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with ID: " + id);
        }

        Movie movie = convertToEntity(movieDTO);
        movie.setId(id); // Ensure ID is set correctly
        Movie updatedMovie = movieRepository.save(movie);
        MovieDTO updatedMovieDTO = convertToDTO(updatedMovie);

        return CompletableFuture.completedFuture(updatedMovieDTO);
    }

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional
    public CompletableFuture<Void> deleteMovieAsync(Long id) {
        // Direct implementation of movie deletion
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found with ID: " + id);
        }

        movieRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    // Search operations

    @Override
    @Transactional(readOnly = true)
    public Page<MovieDTO> searchMovies(MovieSearchDTO searchDTO) {
        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                searchDTO.getPage(),
                searchDTO.getSize(),
                searchDTO.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC,
                searchDTO.getSortBy()
        );

        // Use specifications for dynamic querying
        Page<Movie> moviesPage = movieRepository.findAll(
                MovieSpecifications.buildSpecification(searchDTO),
                pageable
        );

        // Convert to DTOs
        return moviesPage.map(this::convertToDTO);
    }

    @Override
    @Async("virtualThreadTaskExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Page<MovieDTO>> searchMoviesAsync(MovieSearchDTO searchDTO) {
        // Direct implementation of movie search
        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                searchDTO.getPage(),
                searchDTO.getSize(),
                searchDTO.isAscending() ? Sort.Direction.ASC : Sort.Direction.DESC,
                searchDTO.getSortBy()
        );

        // Use specifications for dynamic querying
        Page<Movie> moviesPage = movieRepository.findAll(
                MovieSpecifications.buildSpecification(searchDTO),
                pageable
        );

        // Convert to DTOs
        Page<MovieDTO> movieDTOPage = moviesPage.map(this::convertToDTO);

        return CompletableFuture.completedFuture(movieDTOPage);
    }
}
