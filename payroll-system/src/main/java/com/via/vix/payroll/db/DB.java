package com.via.vix.payroll.db;

import java.sql.*;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/payroll_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // change if needed
    private static final String PASS = ""; // change if needed

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
