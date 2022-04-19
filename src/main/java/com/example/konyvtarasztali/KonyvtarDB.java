package com.example.konyvtarasztali;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KonyvtarDB {
    Connection conn;
    String DB_DRIVER = "mysql";
    String DB_HOST = "Localhost";
    String DB_PORT = "3306";
    String DB_DATABASE = "konyvtar";
    String DB_USER = "root";
    String DB_PASS = "";
    public KonyvtarDB() throws SQLException {
        String url = String.format("jdbc:%s://%s:%s/%s",DB_DRIVER,DB_HOST,DB_PORT,DB_DATABASE);
        conn = DriverManager.getConnection(url,DB_USER,DB_PASS);
    }

    public List<Konyv> getKonyvek() throws SQLException {
        List<Konyv> konyvek = new ArrayList<>();
        String sql = "SELECT * FROM books";
        Statement statement = conn.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            int publish_year = resultSet.getInt("publish_year");
            int page_count = resultSet.getInt("page_count");
            Konyv konyv = new Konyv(id, title, author, publish_year, page_count);
            konyv.setRental_count(this.getRentalCount(konyv));
            konyvek.add(konyv);
        }

        return konyvek;
    }

    private long getRentalCount(Konyv konyv) throws SQLException {
        String sql = "SELECT COUNT(*) AS rental_count FROM rentals WHERE book_id = ? ";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, konyv.getId());
        ResultSet resultSet = statement.executeQuery();
        long db = 0;
        if (resultSet.next()){
            db = resultSet.getLong("rental_count");
        }
        return db;
    }

    public boolean deleteKonyv(Konyv torlendo) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, torlendo.getId());
        return statement.executeUpdate() > 0;

    }
}
