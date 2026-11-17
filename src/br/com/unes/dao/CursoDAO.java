package br.com.unes.dao;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

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
	
	public void atualizar(Curso curso) throws SQLException {
		String sql = "UPDATE cursos SET codigo = ?, nome = ?, carga_horaria = ?, modalidade = ?, "
				+ "quantidade_semestres = ?, status_curso = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, curso.getCodigo());
			ps.setString(2, curso.getNome());
			ps.setInt(3, curso.getCarga_horaria());
			ps.setString(4, curso.getModalidade());
			ps.setInt(5, curso.getQuantidade_semestres());
			ps.setString(6, curso.getStatus_curso());
			ps.setInt(7, curso.getId());
			ps.executeUpdate();
		}
	}

	/** Curso nunca é apagado: apenas inativado (RN09). */
	public void inativar(int id) throws SQLException {
		alterarStatus(id, "INATIVO");
	}

	public void ativar(int id) throws SQLException {
		alterarStatus(id, "ATIVO");
	}

	private void alterarStatus(int id, String status) throws SQLException {
		String sql = "UPDATE cursos SET status_curso = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public List<Curso> listar() throws SQLException {
		return consultar("SELECT * FROM cursos ORDER BY nome", null);
	}

	public List<Curso> listarAtivos() throws SQLException {
		return consultar("SELECT * FROM cursos WHERE status_curso = 'ATIVO' ORDER BY nome", null);
	}

	public Optional<Curso> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM cursos WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Curso> buscarPorCodigo(String codigo) throws SQLException {
		return consultar("SELECT * FROM cursos WHERE codigo = ?", codigo).stream().findFirst();
	}

	private List<Curso> consultar(String sql, Object parametro) throws SQLException {
		List<Curso> lista = new ArrayList<>();

		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montarCurso(rs));
			}
		}
		return lista;
	}

	private Curso montarCurso(ResultSet rs) throws SQLException {
		Curso c = new Curso();
		c.setId(rs.getInt("id"));
		c.setCodigo(rs.getString("codigo"));
		c.setNome(rs.getString("nome"));
		c.setCarga_horaria(rs.getInt("carga_horaria"));
		c.setModalidade(rs.getString("modalidade"));
		c.setQuantidade_semestres(rs.getInt("quantidade_semestres"));
		c.setStatus_curso(rs.getString("status_curso"));
		c.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		c.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return c;
	}
}
