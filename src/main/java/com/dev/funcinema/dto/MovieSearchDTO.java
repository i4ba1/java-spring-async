package com.dev.funcinema.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchDTO {

    private String title;
    private String director;
    private String genre;
    private LocalDate releaseYearStart;
    private LocalDate releaseYearEnd;
    private Double minRating;
    private Boolean featured;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "title";

    @Builder.Default
    private boolean ascending = true;
}
