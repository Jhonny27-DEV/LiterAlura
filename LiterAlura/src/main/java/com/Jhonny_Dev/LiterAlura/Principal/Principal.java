package com.Jhonny_Dev.LiterAlura.Principal;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.Scanner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Principal {

    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/";
    private static final String DATABASE_USER = "username";
    private static final String DATABASE_PASSWORD = "password";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
//        setupDatabase();
        muestraElMenu();
    }

    private static void setupDatabase() {
        String createBooksTable = """
            CREATE TABLE IF NOT EXISTS books (
                id SERIAL PRIMARY KEY, 
                title TEXT, 
                author TEXT, 
                language TEXT
            );
        """;
        String createAuthorsTable = """
            CREATE TABLE IF NOT EXISTS authors (
                id SERIAL PRIMARY KEY, 
                name TEXT, 
                birth_year INTEGER
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createBooksTable);
            stmt.execute(createAuthorsTable);
        } catch (SQLException e) {
            System.err.println("Error al configurar la base de datos: " + e.getMessage());
        }
    }

    public static void muestraElMenu() {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Buscar por título");
            System.out.println("2. Listar libros registrados");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores en un determinado año");
            System.out.println("5. Listar libros por idioma");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (option) {
                case 1 -> buscarPorTitulo(scanner);
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresPorAnio(scanner);
                case 5 -> listarLibrosPorIdioma(scanner);
                case 0 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        } while (option != 0);

        scanner.close();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    private static void buscarPorTitulo(Scanner scanner) {
        System.out.print("Introduce el título: ");
        String titulo = scanner.nextLine();

        try {
            String apiUrl = "https://gutendex.com/books?search=" + titulo.replace(" ", "%20");
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Resultados encontrados:");
                procesarResultados(response.body());
            } else {
                System.err.println("Error al buscar libros: Código " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error al realizar la solicitud: " + e.getMessage());
        }
    }

    private static void procesarResultados(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(json);
            JsonNode results = root.path("results");

            for (JsonNode book : results) {
                String title = book.path("title").asText();
                String author = book.path("authors").get(0).path("name").asText();
                String language = book.path("languages").get(0).asText();

                System.out.printf("Título: %s, Autor: %s, Idioma: %s%n", title, author, language);

                guardarLibro(title, author, language);
            }
        } catch (Exception e) {
            System.err.println("Error al procesar resultados: " + e.getMessage());
        }
    }

    private static void guardarLibro(String title, String author, String language) {
        String sql = "INSERT INTO books (title, author, language) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, language);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar libro: " + e.getMessage());
        }
    }

    private static void listarLibrosRegistrados() {
        String sql = "SELECT * FROM books";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID: %d, Título: %s, Autor: %s, Idioma: %s%n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("language"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
    }

    private static void listarAutoresRegistrados() {
        String sql = "SELECT * FROM authors";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("ID: %d, Nombre: %s, Año de nacimiento: %d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getInt("birth_year"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar autores: " + e.getMessage());
        }
    }

    private static void listarAutoresPorAnio(Scanner scanner) {
        System.out.print("Introduce el año: ");
        int anio = scanner.nextInt();

        String sql = "SELECT * FROM authors WHERE birth_year = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, anio);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("ID: %d, Nombre: %s, Año de nacimiento: %d%n",
                            rs.getInt("id"), rs.getString("name"), rs.getInt("birth_year"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar autores: " + e.getMessage());
        }
    }

    private static void listarLibrosPorIdioma(Scanner scanner) {
        System.out.print("Introduce el idioma (código ISO 639-1, por ejemplo, 'en'): ");
        String idioma = scanner.nextLine();

        String sql = "SELECT * FROM books WHERE language = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, idioma);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("ID: %d, Título: %s, Autor: %s, Idioma: %s%n",
                            rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("language"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }
    }
}
