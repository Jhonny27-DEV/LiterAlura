package com.Jhonny_Dev.LiterAlura.repositorio;

import com.Jhonny_Dev.LiterAlura.model.Libro;
//import com.Jhonny_Dev.LiterAlura.model.Idiomas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface LibroRepository extends JpaRepository<Libro, Long> {

    // Buscar libro por título
    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Libro> buscarPorTitulo(String nombre);

//    // Obtener libros por idioma
//    @Query("SELECT l FROM Libro l WHERE l.idioma = :idioma")
////    List<Libro> obtenerLibrosPorIdioma(Idiomas idioma);
}
