package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.unes.model.Auditoria;
import br.com.unes.util.Conexao;

/** Trilha de auditoria: somente inclusão e consulta (registros nunca são alterados nem apagados). */
public class AuditoriaDAO {

	public void inserir(Auditoria a) throws SQLException {
		String sql = "INSERT INTO auditoria (usuario_id, acao, entidade, registro_id, detalhes) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			DaoUtil.gravarInteger(ps, 1, a.getUsuario_id());
			ps.setString(2, a.getAcao());
			ps.setString(3, a.getEntidade());
			DaoUtil.gravarLong(ps, 4, a.getRegistro_id());
			ps.setString(5, a.getDetalhes());
			ps.executeUpdate();
		}
	}

	public List<Auditoria> listarRecentes(int limite) throws SQLException {
		String sql = "SELECT * FROM auditoria ORDER BY data_hora DESC, id DESC LIMIT ?";
		return consultar(sql, limite);
	}

	public List<Auditoria> listarPorUsuario(int usuarioId) throws SQLException {
		String sql = "SELECT * FROM auditoria WHERE usuario_id = ? ORDER BY data_hora DESC, id DESC";
		return consultar(sql, usuarioId);
	}

	public List<Auditoria> listarPorRegistro(String entidade, long registroId) throws SQLException {
		String sql = "SELECT * FROM auditoria WHERE entidade = ? AND registro_id = ? "
				+ "ORDER BY data_hora DESC, id DESC";
		List<Auditoria> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, entidade);
			ps.setLong(2, registroId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private List<Auditoria> consultar(String sql, int parametro) throws SQLException {
		List<Auditoria> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private Auditoria montar(ResultSet rs) throws SQLException {
		Auditoria a = new Auditoria();
		a.setId(rs.getLong("id"));
		a.setUsuario_id(DaoUtil.lerInteger(rs, "usuario_id"));
		a.setAcao(rs.getString("acao"));
		a.setEntidade(rs.getString("entidade"));
		a.setRegistro_id(DaoUtil.lerLong(rs, "registro_id"));
		a.setData_hora(DaoUtil.lerData(rs, "data_hora"));
		a.setDetalhes(rs.getString("detalhes"));
		return a;
	}
}
