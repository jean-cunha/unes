package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Usuario;
import br.com.unes.util.Conexao;

public class UsuarioDAO {

	public void inserir(Usuario u) throws SQLException {
		String sql = "INSERT INTO usuarios (login, senha_hash, perfil, ativo, aluno_id, professor_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, u);
			ps.executeUpdate();
		}
	}

	public void atualizar(Usuario u) throws SQLException {
		String sql = "UPDATE usuarios SET login = ?, senha_hash = ?, perfil = ?, ativo = ?, aluno_id = ?, "
				+ "professor_id = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, u);
			ps.setInt(7, u.getId());
			ps.executeUpdate();
		}
	}

	public void atualizarSenha(int id, String senhaHash) throws SQLException {
		String sql = "UPDATE usuarios SET senha_hash = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, senhaHash);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public void definirAtivo(int id, boolean ativo) throws SQLException {
		String sql = "UPDATE usuarios SET ativo = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setBoolean(1, ativo);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public void registrarAcesso(int id) throws SQLException {
		String sql = "UPDATE usuarios SET ultimo_acesso = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Usuario> listar() throws SQLException {
		return consultar("SELECT * FROM usuarios ORDER BY login", null);
	}

	public List<Usuario> listarPorPerfil(String perfil) throws SQLException {
		return consultar("SELECT * FROM usuarios WHERE perfil = ? ORDER BY login", perfil);
	}

	public Optional<Usuario> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM usuarios WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Usuario> buscarPorLogin(String login) throws SQLException {
		return consultar("SELECT * FROM usuarios WHERE login = ?", login).stream().findFirst();
	}

	private List<Usuario> consultar(String sql, Object parametro) throws SQLException {
		List<Usuario> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Usuario u) throws SQLException {
		ps.setString(1, u.getLogin());
		ps.setString(2, u.getSenha_hash());
		ps.setString(3, u.getPerfil());
		ps.setBoolean(4, u.isAtivo());
		DaoUtil.gravarInteger(ps, 5, u.getAluno_id());
		DaoUtil.gravarInteger(ps, 6, u.getProfessor_id());
	}

	private Usuario montar(ResultSet rs) throws SQLException {
		Usuario u = new Usuario();
		u.setId(rs.getInt("id"));
		u.setLogin(rs.getString("login"));
		u.setSenha_hash(rs.getString("senha_hash"));
		u.setPerfil(rs.getString("perfil"));
		u.setAtivo(rs.getBoolean("ativo"));
		u.setAluno_id(DaoUtil.lerInteger(rs, "aluno_id"));
		u.setProfessor_id(DaoUtil.lerInteger(rs, "professor_id"));
		u.setUltimo_acesso(DaoUtil.lerData(rs, "ultimo_acesso"));
		u.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		u.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return u;
	}
}
