package com.dev.funcinema.repository;

import com.dev.funcinema.dto.MovieSearchDTO;
import com.dev.funcinema.model.Movie;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieSpecifications {

    public static Specification<Movie> buildSpecification(MovieSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Title search (case insensitive)
            if (StringUtils.hasText(searchDTO.getTitle())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + searchDTO.getTitle().toLowerCase() + "%"));
            }

            // Director search (case insensitive)
            if (StringUtils.hasText(searchDTO.getDirector())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("director")),
                        "%" + searchDTO.getDirector().toLowerCase() + "%"));
            }

            // Genre search
            if (StringUtils.hasText(searchDTO.getGenre())) {
                // Using JOIN to search in the collection of genres
                Join<Movie, String> genresJoin = root.join("genres");
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(genresJoin),
                        "%" + searchDTO.getGenre().toLowerCase() + "%"));

                // Make sure we don't get duplicate movies due to the join
                query.distinct(true);
            }

            // Release date range
            if (searchDTO.getReleaseYearStart() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("releaseDate"), searchDTO.getReleaseYearStart()));
            }

            if (searchDTO.getReleaseYearEnd() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("releaseDate"), searchDTO.getReleaseYearEnd()));
            }

            // Rating filter
            if (searchDTO.getMinRating() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("rating"), searchDTO.getMinRating()));
            }

            // Featured filter
            if (searchDTO.getFeatured() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("featured"), searchDTO.getFeatured()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
