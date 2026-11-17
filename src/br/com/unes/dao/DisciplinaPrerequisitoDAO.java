package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.unes.model.DisciplinaPrerequisito;
import br.com.unes.util.Conexao;

public class DisciplinaPrerequisitoDAO {

	public void inserir(DisciplinaPrerequisito dp) throws SQLException {
		String sql = "INSERT INTO disciplina_prerequisitos (disciplina_id, prerequisito_id) VALUES (?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, dp.getDisciplina_id());
			ps.setInt(2, dp.getPrerequisito_id());
			ps.executeUpdate();
		}
	}

	public void remover(int disciplinaId, int prerequisitoId) throws SQLException {
		String sql = "DELETE FROM disciplina_prerequisitos WHERE disciplina_id = ? AND prerequisito_id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, disciplinaId);
			ps.setInt(2, prerequisitoId);
			ps.executeUpdate();
		}
	}

	/** Pré-requisitos exigidos por uma disciplina. */
	public List<DisciplinaPrerequisito> listarPorDisciplina(int disciplinaId) throws SQLException {
		return consultar("SELECT * FROM disciplina_prerequisitos WHERE disciplina_id = ?", disciplinaId);
	}

	/** Disciplinas que dependem de uma disciplina como pré-requisito. */
	public List<DisciplinaPrerequisito> listarDependentes(int prerequisitoId) throws SQLException {
		return consultar("SELECT * FROM disciplina_prerequisitos WHERE prerequisito_id = ?", prerequisitoId);
	}

	public boolean existe(int disciplinaId, int prerequisitoId) throws SQLException {
		String sql = "SELECT 1 FROM disciplina_prerequisitos WHERE disciplina_id = ? AND prerequisito_id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, disciplinaId);
			ps.setInt(2, prerequisitoId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		}
	}

	private List<DisciplinaPrerequisito> consultar(String sql, int parametro) throws SQLException {
		List<DisciplinaPrerequisito> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					DisciplinaPrerequisito dp = new DisciplinaPrerequisito();
					dp.setDisciplina_id(rs.getInt("disciplina_id"));
					dp.setPrerequisito_id(rs.getInt("prerequisito_id"));
					lista.add(dp);
				}
			}
		}
		return lista;
	}
}
