package com.Jhonny_Dev.LiterAlura.model;

public class Libro {
    private long Id;
    private String titulo;
    private String autor;
    private String idioma;
    private Double numeroDeDescarga;

    public Libro(DatosLiboros datosLiboros){
        this.titulo = datosLiboros.titulo ();
        this.autor = datosLiboros.autor ().toString ();
        this.idioma = datosLiboros.idiomas ().toString ();
        this.numeroDeDescarga = datosLiboros.numeroDeDescargas ();
    }

    @Override
    public String toString() {
        return "titulo='" + titulo + '\'' +
                ", autor=" + autor+
                ", idioma=" + idioma +
                ", numeroDeDescarga='" + numeroDeDescarga + '\'';
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDeDescarga() {
        return numeroDeDescarga;
    }

    public void setNumeroDeDescarga(Double numeroDeDescarga) {
        this.numeroDeDescarga = numeroDeDescarga;
    }
}
