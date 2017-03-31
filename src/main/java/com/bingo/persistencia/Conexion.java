/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public final class Conexion {
    // JDBC driver name and database URL

    static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    
    static String DB_URL = "jdbc:postgresql://localhost:5432/bingo";
    //  Database credentials
    static String USUARIO = "postgres";
    static String PASSWORD = "toor";
        
    private static Conexion instancia;

    private Conexion() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    public synchronized static Conexion getInstancia() {
        if (instancia == null) {
            try {
                instancia = new Conexion();
            } catch (ClassNotFoundException ex) {
               // //System.out.println("Driver not present on System...");                
            }
        }
        return instancia;
    }

   public synchronized void borrar(String deleteSQL) throws SQLException {

        Connection dbConnection = null;
        Statement stmt = null;

        try {
            dbConnection = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
            stmt = dbConnection.createStatement();
            // execute delete SQL stetement
            stmt.executeUpdate(deleteSQL);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }

        }

    }
   
   public synchronized void limpiarTabla(String nombreTabla) throws SQLException {
       Connection dbConnection = null;
        Statement stmt = null;

        try {
            dbConnection = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
            stmt = dbConnection.createStatement();
            // execute delete SQL stetement
            stmt.executeUpdate("TRUNCATE " + nombreTabla);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }

        }
   }

    public synchronized  boolean insertar(String sql) throws SQLException {

        Connection conn = null;
        Statement stmt = null;

        boolean rs = false;

        try {
            conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);

            stmt = conn.createStatement();

            rs = stmt.execute(sql);

            stmt.close();
            conn.close();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }//end main
    
    public synchronized int actualizar(String sql) throws SQLException {

        Connection conn = null;
        Statement stmt = null;

        int rs;

        try {
            conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);

            stmt = conn.createStatement();

            rs = stmt.executeUpdate(sql);

            stmt.close();
            conn.close();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }//end main
    
    /**
     * Realiza una consulta a la base de datos. Operacion Select
     * @param sql Consulta de tipo SELECT a la base de datos
     * @return Lista de registros
     * @throws SQLException 
     */
    public synchronized List<HashMap<String, Object>> consultar(String sql) throws SQLException {

        Connection conn = null;
        Statement stmt = null;

        List results = new ArrayList();

        try {
            conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Map result = new HashMap();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int numberOfColumns = rsMetaData.getColumnCount();
                // get the column names; column indexes start from 1
                for (int i = 1; i < numberOfColumns + 1; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    int type = rsMetaData.getColumnType(i);
                    result.put(columnName, rs.getObject(columnName));
                }
                results.add(result);
            }

            rs.close();
            stmt.close();
            conn.close();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return results;
    }//end main
    
    public static class ConexionBuilder{
        
        //Aplicacion del patron builder, necesario para cargar los valores
        //de la conexion singleton
        

        public ConexionBuilder() {
        }

        public ConexionBuilder usuario(String usuario){
            USUARIO = usuario;
            return this;
        }

        public ConexionBuilder password(String password){
            PASSWORD = password;
            return this;
        }

        public ConexionBuilder databaseUrl(String url){
            DB_URL = url;
            return this;
        }

        public ConexionBuilder jdbcDriver(String driver){
            JDBC_DRIVER = driver;
            return this;
        }
        
        public Conexion crear(){
            return Conexion.getInstancia();
        }
        
    }
}
