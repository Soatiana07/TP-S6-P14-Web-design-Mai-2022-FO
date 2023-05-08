/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.springmvc.Connexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author SOATIANA
 */
public class MyConnexion {
    public Connection getConnection(String database, String username, String password) throws Exception {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+database, username, password);

        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        }
        return con;
    }
    
    public Connection getConnection() throws Exception {
        Connection con = null;

        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://containers-us-west-1.railway.app:6655/railway", "postgres", "onkBXlAQyjn8TrpwKrt0");

        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        }
        return con;
    }
}
