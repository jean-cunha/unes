package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Avaliacao;
import br.com.unes.util.Conexao;

public class AvaliacaoDAO {

	public void inserir(Avaliacao a) throws SQLException {
		String sql = "INSERT INTO avaliacoes (turma_id, descricao, tipo, valor_maximo, data_avaliacao, ordem) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, a);
			ps.executeUpdate();
		}
	}

	public void atualizar(Avaliacao a) throws SQLException {
		String sql = "UPDATE avaliacoes SET turma_id = ?, descricao = ?, tipo = ?, valor_maximo = ?, "
				+ "data_avaliacao = ?, ordem = ? WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, a);
			ps.setInt(7, a.getId());
			ps.executeUpdate();
		}
	}

	public void excluir(int id) throws SQLException {
		String sql = "DELETE FROM avaliacoes WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Avaliacao> listar() throws SQLException {
		return consultar("SELECT * FROM avaliacoes ORDER BY turma_id, ordem", null);
	}

	public List<Avaliacao> listarPorTurma(int turmaId) throws SQLException {
		return consultar("SELECT * FROM avaliacoes WHERE turma_id = ? ORDER BY ordem", turmaId);
	}

	public Optional<Avaliacao> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM avaliacoes WHERE id = ?", id).stream().findFirst();
	}

	private List<Avaliacao> consultar(String sql, Integer parametro) throws SQLException {
		List<Avaliacao> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setInt(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Avaliacao a) throws SQLException {
		ps.setInt(1, a.getTurma_id());
		ps.setString(2, a.getDescricao());
		ps.setString(3, a.getTipo());
		ps.setBigDecimal(4, a.getValor_maximo());
		ps.setDate(5, a.getData_avaliacao());
		ps.setInt(6, a.getOrdem());
	}

	private Avaliacao montar(ResultSet rs) throws SQLException {
		Avaliacao a = new Avaliacao();
		a.setId(rs.getInt("id"));
		a.setTurma_id(rs.getInt("turma_id"));
		a.setDescricao(rs.getString("descricao"));
		a.setTipo(rs.getString("tipo"));
		a.setValor_maximo(rs.getBigDecimal("valor_maximo"));
		a.setData_avaliacao(rs.getDate("data_avaliacao"));
		a.setOrdem(rs.getInt("ordem"));
		a.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		return a;
	}
}
