package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Disciplina;
import br.com.unes.util.Conexao;

public class DisciplinaDAO {

	public void inserir(Disciplina d) throws SQLException {
		String sql = "INSERT INTO disciplinas (codigo, nome, carga_horaria, semestre_sugerido, curso_id, "
				+ "status_disciplina) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, d);
			ps.executeUpdate();
		}
	}

	public void atualizar(Disciplina d) throws SQLException {
		String sql = "UPDATE disciplinas SET codigo = ?, nome = ?, carga_horaria = ?, semestre_sugerido = ?, "
				+ "curso_id = ?, status_disciplina = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, d);
			ps.setInt(7, d.getId());
			ps.executeUpdate();
		}
	}

	public void inativar(int id) throws SQLException {
		alterarStatus(id, "INATIVO");
	}

	public void ativar(int id) throws SQLException {
		alterarStatus(id, "ATIVO");
	}

	private void alterarStatus(int id, String status) throws SQLException {
		String sql = "UPDATE disciplinas SET status_disciplina = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public List<Disciplina> listar() throws SQLException {
		return consultar("SELECT * FROM disciplinas ORDER BY nome", null);
	}

	public List<Disciplina> listarPorCurso(int cursoId) throws SQLException {
		return consultar("SELECT * FROM disciplinas WHERE curso_id = ? ORDER BY semestre_sugerido, nome", cursoId);
	}

	public Optional<Disciplina> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM disciplinas WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Disciplina> buscarPorCodigo(String codigo) throws SQLException {
		return consultar("SELECT * FROM disciplinas WHERE codigo = ?", codigo).stream().findFirst();
	}

	private List<Disciplina> consultar(String sql, Object parametro) throws SQLException {
		List<Disciplina> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Disciplina d) throws SQLException {
		ps.setString(1, d.getCodigo());
		ps.setString(2, d.getNome());
		ps.setInt(3, d.getCarga_horaria());
		ps.setInt(4, d.getSemestre_sugerido());
		ps.setInt(5, d.getCurso_id());
		ps.setString(6, d.getStatus_disciplina());
	}

	private Disciplina montar(ResultSet rs) throws SQLException {
		Disciplina d = new Disciplina();
		d.setId(rs.getInt("id"));
		d.setCodigo(rs.getString("codigo"));
		d.setNome(rs.getString("nome"));
		d.setCarga_horaria(rs.getInt("carga_horaria"));
		d.setSemestre_sugerido(rs.getInt("semestre_sugerido"));
		d.setCurso_id(rs.getInt("curso_id"));
		d.setStatus_disciplina(rs.getString("status_disciplina"));
		d.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		d.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return d;
	}
}
