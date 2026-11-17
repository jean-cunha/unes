package br.com.unes.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Nota;
import br.com.unes.util.Conexao;

public class NotaDAO {

	public void inserir(Nota n) throws SQLException {
		String sql = "INSERT INTO notas (matricula_id, avaliacao_id, valor, lancada_por_usuario_id) "
				+ "VALUES (?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, n.getMatricula_id());
			ps.setInt(2, n.getAvaliacao_id());
			ps.setBigDecimal(3, n.getValor());
			DaoUtil.gravarInteger(ps, 4, n.getLancada_por_usuario_id());
			ps.executeUpdate();
		}
	}

	public void atualizar(Nota n) throws SQLException {
		String sql = "UPDATE notas SET valor = ?, lancada_por_usuario_id = ?, data_lancamento = CURRENT_TIMESTAMP "
				+ "WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setBigDecimal(1, n.getValor());
			DaoUtil.gravarInteger(ps, 2, n.getLancada_por_usuario_id());
			ps.setInt(3, n.getId());
			ps.executeUpdate();
		}
	}

	public void excluir(int id) throws SQLException {
		String sql = "DELETE FROM notas WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Nota> listarPorMatricula(int matriculaId) throws SQLException {
		return consultar("SELECT * FROM notas WHERE matricula_id = ? ORDER BY avaliacao_id", matriculaId);
	}

	public List<Nota> listarPorAvaliacao(int avaliacaoId) throws SQLException {
		return consultar("SELECT * FROM notas WHERE avaliacao_id = ? ORDER BY matricula_id", avaliacaoId);
	}

	public Optional<Nota> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM notas WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Nota> buscarPorMatriculaEAvaliacao(int matriculaId, int avaliacaoId) throws SQLException {
		String sql = "SELECT * FROM notas WHERE matricula_id = ? AND avaliacao_id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, matriculaId);
			ps.setInt(2, avaliacaoId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montar(rs)) : Optional.empty();
			}
		}
	}

	/** Soma das notas lançadas na matrícula; retorna zero se não houver notas. */
	public BigDecimal somarPorMatricula(int matriculaId) throws SQLException {
		String sql = "SELECT COALESCE(SUM(valor), 0) FROM notas WHERE matricula_id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, matriculaId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
			}
		}
	}

	private List<Nota> consultar(String sql, int parametro) throws SQLException {
		List<Nota> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private Nota montar(ResultSet rs) throws SQLException {
		Nota n = new Nota();
		n.setId(rs.getInt("id"));
		n.setMatricula_id(rs.getInt("matricula_id"));
		n.setAvaliacao_id(rs.getInt("avaliacao_id"));
		n.setValor(rs.getBigDecimal("valor"));
		n.setData_lancamento(DaoUtil.lerData(rs, "data_lancamento"));
		n.setLancada_por_usuario_id(DaoUtil.lerInteger(rs, "lancada_por_usuario_id"));
		return n;
	}
}
