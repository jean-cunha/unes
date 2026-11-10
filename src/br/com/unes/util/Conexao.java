package br.com.unes.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
	private static final String URL = 
			 "jdbc:mysql://localhost:3306/unes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String SENHA = "123456";
    
    public static Connection getConexao() {
    	try {
    		return DriverManager.getConnection(URL, USUARIO, SENHA);
    	} catch (SQLException e) { 
    		throw new RuntimeException("Erro ao ligar com banco de dados" + e.getMessage());
    	}
    	
    }
}
