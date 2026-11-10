package br.com.unes;

import java.sql.Connection;

import javax.swing.JOptionPane;

public class TesteConexao {

	public static void main(String[] args) {
		try {
			Connection conexao = br.com.unes.util.Conexao.getConexao();
			System.out.println("Conexão Efetuada co Sucesso!");
			JOptionPane.showMessageDialog(null, "Conexão efetuada com sucesso");
		} catch (Exception e) { 
			System.out.println("Falha na ligação: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Conexão não efetuada: " + e.getMessage());
		}

	}
}