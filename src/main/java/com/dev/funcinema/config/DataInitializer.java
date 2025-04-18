package com.dev.funcinema.config;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.dev.funcinema.model.Movie;
import com.dev.funcinema.repository.MovieRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@Profile("dev") // Only run in development mode
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(MovieRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                log.info("Preloading sample movie data");

                repository.saveAll(List.of(
                        Movie.builder()
                                .title("The Shawshank Redemption")
                                .director("Frank Darabont")
                                .genres(Set.of("Drama"))
                                .releaseDate(LocalDate.of(1994, 9, 23))
                                .durationMinutes(142)
                                .rating(9.3)
                                .plot("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
                                .featured(true)
                                .build(),

                        Movie.builder()
                                .title("The Godfather")
                                .director("Francis Ford Coppola")
                                .genres(Set.of("Crime", "Drama"))
                                .releaseDate(LocalDate.of(1972, 3, 24))
                                .durationMinutes(175)
                                .rating(9.2)
                                .plot("The aging patriarch of an organized crime dynasty transfers control to his reluctant son.")
                                .featured(true)
                                .build(),

                        Movie.builder()
                                .title("Pulp Fiction")
                                .director("Quentin Tarantino")
                                .genres(Set.of("Crime", "Drama"))
                                .releaseDate(LocalDate.of(1994, 10, 14))
                                .durationMinutes(154)
                                .rating(8.9)
                                .plot("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.")
                                .featured(false)
                                .build(),

                        Movie.builder()
                                .title("The Dark Knight")
                                .director("Christopher Nolan")
                                .genres(Set.of("Action", "Crime", "Drama", "Thriller"))
                                .releaseDate(LocalDate.of(2008, 7, 18))
                                .durationMinutes(152)
                                .rating(9.0)
                                .plot("When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.")
                                .featured(true)
                                .build(),

                        Movie.builder()
                                .title("Forrest Gump")
                                .director("Robert Zemeckis")
                                .genres(Set.of("Drama", "Romance"))
                                .releaseDate(LocalDate.of(1994, 7, 6))
                                .durationMinutes(142)
                                .rating(8.8)
                                .plot("The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.")
                                .featured(false)
                                .build()
                ));

                log.info("Sample data loaded successfully!");
            }
        };
    }
}