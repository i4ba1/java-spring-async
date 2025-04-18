package com.dev.funcinema.repository;

import java.time.LocalDate;
import java.util.List;

import com.dev.funcinema.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    // Find by exact title
    List<Movie> findByTitle(String title);

    // Find by title containing the search string (case insensitive)
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Find by director
    Page<Movie> findByDirectorContainingIgnoreCase(String director, Pageable pageable);

    // Find by genre (using JPQL to search in a collection)
    @Query("SELECT m FROM Movie m JOIN m.genres g WHERE LOWER(g) LIKE LOWER(CONCAT('%', :genre, '%'))")
    Page<Movie> findByGenreContainingIgnoreCase(@Param("genre") String genre, Pageable pageable);

    // Find movies with rating greater than or equal to the provided value
    Page<Movie> findByRatingGreaterThanEqual(Double minRating, Pageable pageable);

    // Find movies released between two dates
    Page<Movie> findByReleaseDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Find featured movies
    Page<Movie> findByFeaturedTrue(Pageable pageable);

    // Complex query combining multiple search criteria
    @Query("SELECT m FROM Movie m WHERE " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:director IS NULL OR LOWER(m.director) LIKE LOWER(CONCAT('%', :director, '%'))) AND " +
            "(:minRating IS NULL OR m.rating >= :minRating) AND " +
            "(:featured IS NULL OR m.featured = :featured)")
    Page<Movie> findByTitleDirectorRatingAndFeatured(
            @Param("title") String title,
            @Param("director") String director,
            @Param("minRating") Double minRating,
            @Param("featured") Boolean featured,
            Pageable pageable);
}
