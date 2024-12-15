package com.Jhonny_Dev.LiterAlura.Servicios;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvierteDatos  implements IConvierteDatos{
    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public <T> T obtenerDatos(String json, Class clase) {
        try {
            return (T) objectMapper.readValue(json,clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
