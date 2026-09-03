package com.hugo.mabibli.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public record AssignSeriesRequest (
        @NotNull(message="La série est obligatoire")
        Long seriesId,

        @Positive(message="L'index dans la série doit être positif")
        Integer seriesIndex
        ){
}
