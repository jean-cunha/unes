package br.com.unes.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Conexão com o MySQL local. Usuário e senha são definidos pela Main ao iniciar. */
public class Conexao {
	public static final String URL =
			"jdbc:mysql://localhost:3306/unes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

	public static String usuario;
	public static String senha;

	public static Connection getConexao() {
		try {
			return DriverManager.getConnection(URL, usuario, senha);
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao ligar com banco de dados: " + e.getMessage(), e);
		}
	}
}
