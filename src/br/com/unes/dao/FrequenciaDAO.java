package br.com.unes.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Frequencia;
import br.com.unes.util.Conexao;

public class FrequenciaDAO {

	public void inserir(Frequencia f) throws SQLException {
		String sql = "INSERT INTO frequencias (matricula_id, data_aula, presente, registrado_por_usuario_id) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, f.getMatricula_id());
			ps.setDate(2, f.getData_aula());
			ps.setBoolean(3, f.isPresente());
			DaoUtil.gravarInteger(ps, 4, f.getRegistrado_por_usuario_id());
			ps.executeUpdate();
		}
	}

	public void atualizar(Frequencia f) throws SQLException {
		String sql = "UPDATE frequencias SET data_aula = ?, presente = ?, registrado_por_usuario_id = ? "
				+ "WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setDate(1, f.getData_aula());
			ps.setBoolean(2, f.isPresente());
			DaoUtil.gravarInteger(ps, 3, f.getRegistrado_por_usuario_id());
			ps.setInt(4, f.getId());
			ps.executeUpdate();
		}
	}

	public void excluir(int id) throws SQLException {
		String sql = "DELETE FROM frequencias WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Frequencia> listarPorMatricula(int matriculaId) throws SQLException {
		String sql = "SELECT * FROM frequencias WHERE matricula_id = ? ORDER BY data_aula";
		List<Frequencia> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, matriculaId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	public Optional<Frequencia> buscarPorId(int id) throws SQLException {
		String sql = "SELECT * FROM frequencias WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
			}
		}
	}

	public Optional<Frequencia> buscarPorMatriculaEData(int matriculaId, Date dataAula) throws SQLException {
		String sql = "SELECT * FROM frequencias WHERE matricula_id = ? AND data_aula = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, matriculaId);
			ps.setDate(2, dataAula);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
			}
		}
	}

	public int contarPresencas(int matriculaId) throws SQLException {
		return contar(matriculaId, true);
	}

	public int contarFaltas(int matriculaId) throws SQLException {
		return contar(matriculaId, false);
	}

	private int contar(int matriculaId, boolean presente) throws SQLException {
		String sql = "SELECT COUNT(*) FROM frequencias WHERE matricula_id = ? AND presente = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, matriculaId);
			ps.setBoolean(2, presente);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getInt(1) : 0;
			}
		}
	}

	private Frequencia montar(ResultSet rs) throws SQLException {
		Frequencia f = new Frequencia();
		f.setId(rs.getInt("id"));
		f.setMatricula_id(rs.getInt("matricula_id"));
		f.setData_aula(rs.getDate("data_aula"));
		f.setPresente(rs.getBoolean("presente"));
		f.setRegistrado_por_usuario_id(DaoUtil.lerInteger(rs, "registrado_por_usuario_id"));
		f.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		return f;
	}
}
