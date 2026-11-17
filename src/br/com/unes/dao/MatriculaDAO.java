package br.com.unes.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Matricula;
import br.com.unes.util.Conexao;

public class MatriculaDAO {

	public void inserir(Matricula m) throws SQLException {
		String sql = "INSERT INTO matriculas (aluno_id, turma_id, data_matricula, situacao, media_final, "
				+ "frequencia_percentual) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, m);
			ps.executeUpdate();
		}
	}

	public void atualizar(Matricula m) throws SQLException {
		String sql = "UPDATE matriculas SET aluno_id = ?, turma_id = ?, data_matricula = ?, situacao = ?, "
				+ "media_final = ?, frequencia_percentual = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, m);
			ps.setInt(7, m.getId());
			ps.executeUpdate();
		}
	}

	/** Situação: ATIVA, TRANCADA, CANCELADA, APROVADO ou REPROVADO. */
	public void atualizarSituacao(int id, String situacao) throws SQLException {
		String sql = "UPDATE matriculas SET situacao = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, situacao);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public void atualizarResultado(int id, BigDecimal mediaFinal, BigDecimal frequenciaPercentual)
			throws SQLException {
		String sql = "UPDATE matriculas SET media_final = ?, frequencia_percentual = ?, "
				+ "atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setBigDecimal(1, mediaFinal);
			ps.setBigDecimal(2, frequenciaPercentual);
			ps.setInt(3, id);
			ps.executeUpdate();
		}
	}

	public List<Matricula> listar() throws SQLException {
		return consultar("SELECT * FROM matriculas ORDER BY data_matricula DESC", null);
	}

	public List<Matricula> listarPorAluno(int alunoId) throws SQLException {
		return consultar("SELECT * FROM matriculas WHERE aluno_id = ? ORDER BY data_matricula DESC", alunoId);
	}

	public List<Matricula> listarPorTurma(int turmaId) throws SQLException {
		return consultar("SELECT * FROM matriculas WHERE turma_id = ? ORDER BY id", turmaId);
	}

	public Optional<Matricula> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM matriculas WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Matricula> buscarPorAlunoETurma(int alunoId, int turmaId) throws SQLException {
		String sql = "SELECT * FROM matriculas WHERE aluno_id = ? AND turma_id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, alunoId);
			ps.setInt(2, turmaId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
			}
		}
	}

	/** Quantidade de matrículas ATIVAS na turma (para controle de vagas). */
	public int contarAtivasPorTurma(int turmaId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM matriculas WHERE turma_id = ? AND situacao = 'ATIVA'";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, turmaId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt(1) : 0;
			}
		}
	}

	private List<Matricula> consultar(String sql, Object parametro) throws SQLException {
		List<Matricula> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Matricula m) throws SQLException {
		ps.setInt(1, m.getAluno_id());
		ps.setInt(2, m.getTurma_id());
		ps.setDate(3, m.getData_matricula());
		ps.setString(4, m.getSituacao());
		ps.setBigDecimal(5, m.getMedia_final());
		ps.setBigDecimal(6, m.getFrequencia_percentual());
	}

	private Matricula montar(ResultSet rs) throws SQLException {
		Matricula m = new Matricula();
		m.setId(rs.getInt("id"));
		m.setAluno_id(rs.getInt("aluno_id"));
		m.setTurma_id(rs.getInt("turma_id"));
		m.setData_matricula(rs.getDate("data_matricula"));
		m.setSituacao(rs.getString("situacao"));
		m.setMedia_final(rs.getBigDecimal("media_final"));
		m.setFrequencia_percentual(rs.getBigDecimal("frequencia_percentual"));
		m.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		m.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return m;
	}
}
