package br.com.unes.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexão com o MySQL local. Usuário e senha são informados pelo usuário na
 * inicialização (veja {@link #configurar(String, String)}) e ficam apenas em memória.
 */
public class Conexao {
	private static final String URL =
			"jdbc:mysql://localhost:3306/unes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

	private static String usuario;
	private static char[] senha;

	private Conexao() {
	}

	/** Guarda as credenciais e testa a conexão. Lança SQLException se não conectar (credenciais não são mantidas). */
	public static void configurar(String novoUsuario, char[] novaSenha) throws SQLException {
		try (Connection teste = DriverManager.getConnection(URL, novoUsuario, new String(novaSenha))) {
			usuario = novoUsuario;
			senha = novaSenha.clone();
		}
	}

	public static Connection getConexao() {
		if (usuario == null) throw new IllegalStateException("Conexão não configurada: informe usuário e senha do MySQL.");
		try {
			return DriverManager.getConnection(URL, usuario, new String(senha));
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao ligar com banco de dados: " + e.getMessage(), e);
		}
	}
}
