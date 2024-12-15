package com.Jhonny_Dev.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record Datos(
        @JsonAlias("results") List<DatosLiboros> liborosResultado
) {

}
