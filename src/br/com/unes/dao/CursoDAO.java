package br.com.unes.dao;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import br.com.unes.model.Curso;

public class CursoDAO {

	public void inserir (Curso curso) throws SQLException	{
		String sql = "INSERT INTO cursos ("
				+ "codigo, nome, carga_horaria, modalidade, "
				+ "quantidade_semestres, status_curso, criado_em, "
				+ "atualizado_em) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql))	{
			ps.setString(1, curso.getCodigo());
			ps.setString(2, curso.getNome());
			ps.setInt(3, curso.getCarga_horaria());
			ps.setString(4, curso.getModalidade());
			ps.setInt(5, curso.getQuantidade_semestres());
			ps.setString(6, curso.getStatus_curso());
			
			ps.setTimestamp(7, Timestamp.valueOf(curso.getCriado_em()));
			ps.setTimestamp(8, Timestamp.valueOf(curso.getAtualizado_em()));
			
			ps.executeUpdate();
		}
	}
	
	public List<Curso> listar() throws SQLException	{
		String slq = "SELECT * FROM cursos ORDER BY nome";
		List<Curso> lista = new ArrayList<>();
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(slq);
				ResultSet rs = ps.executeQuery()){
			
			while (rs.next()) {
				Curso c = new Curso();
				
				c.setCodigo(rs.getString("codigo"));
				c.setNome(rs.getString("nome"));
				c.setCarga_horaria(rs.getInt("carga_horaria"));
				c.setModalidade(rs.getString("modalidade"));
				c.setQuantidade_semestres(rs.getInt("quantidade_semestres"));
				c.setStatus_curso(rs.getString("status_curso"));
				
				c.setCriado_em(rs.getTimestamp("criado_em").toLocalDateTime());
				c.setAtualizado_em(rs.getTimestamp("atualizado_em").toLocalDateTime());
				
				lista.add(c);
			}
			
		}
		
		
		return lista;
	}
	
	
	
	
	
}











