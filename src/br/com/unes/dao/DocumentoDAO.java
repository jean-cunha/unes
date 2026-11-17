package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Documento;
import br.com.unes.util.Conexao;

public class DocumentoDAO {

	/** O documento pertence a um aluno OU a um professor (o outro id deve ser nulo). */
	public void inserir(Documento d) throws SQLException {
		if ((d.getAluno_id() == null) == (d.getProfessor_id() == null))
			throw new IllegalArgumentException("Documento deve pertencer a um aluno ou a um professor");

		String sql = "INSERT INTO documentos (tipo, caminho_arquivo, aluno_id, professor_id) VALUES (?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, d.getTipo());
			ps.setString(2, d.getCaminho_arquivo());
			DaoUtil.gravarInteger(ps, 3, d.getAluno_id());
			DaoUtil.gravarInteger(ps, 4, d.getProfessor_id());
			ps.executeUpdate();
		}
	}

	public void atualizarCaminho(int id, String caminhoArquivo) throws SQLException {
		String sql = "UPDATE documentos SET caminho_arquivo = ?, data_envio = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, caminhoArquivo);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public void excluir(int id) throws SQLException {
		String sql = "DELETE FROM documentos WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Documento> listarPorAluno(int alunoId) throws SQLException {
		return consultar("SELECT * FROM documentos WHERE aluno_id = ? ORDER BY data_envio DESC", alunoId);
	}

	public List<Documento> listarPorProfessor(int professorId) throws SQLException {
		return consultar("SELECT * FROM documentos WHERE professor_id = ? ORDER BY data_envio DESC", professorId);
	}

	public Optional<Documento> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM documentos WHERE id = ?", id).stream().findFirst();
	}

	private List<Documento> consultar(String sql, int parametro) throws SQLException {
		List<Documento> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private Documento montar(ResultSet rs) throws SQLException {
		Documento d = new Documento();
		d.setId(rs.getInt("id"));
		d.setTipo(rs.getString("tipo"));
		d.setCaminho_arquivo(rs.getString("caminho_arquivo"));
		d.setData_envio(DaoUtil.lerData(rs, "data_envio"));
		d.setAluno_id(DaoUtil.lerInteger(rs, "aluno_id"));
		d.setProfessor_id(DaoUtil.lerInteger(rs, "professor_id"));
		return d;
	}
}
