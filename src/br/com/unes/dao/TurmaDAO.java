package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Turma;
import br.com.unes.util.Conexao;

public class TurmaDAO {

	public void inserir(Turma t) throws SQLException {
		String sql = "INSERT INTO turmas (codigo, disciplina_id, professor_id, periodo_letivo, data_inicio, "
				+ "data_fim, turno, sala, vagas, media_aprovacao, frequencia_minima, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, t);
			ps.executeUpdate();
		}
	}

	public void atualizar(Turma t) throws SQLException {
		String sql = "UPDATE turmas SET codigo = ?, disciplina_id = ?, professor_id = ?, periodo_letivo = ?, "
				+ "data_inicio = ?, data_fim = ?, turno = ?, sala = ?, vagas = ?, media_aprovacao = ?, "
				+ "frequencia_minima = ?, status = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, t);
			ps.setInt(13, t.getId());
			ps.executeUpdate();
		}
	}

	/** Status: ABERTA, EM_ANDAMENTO ou ENCERRADA. */
	public void atualizarStatus(int id, String status) throws SQLException {
		String sql = "UPDATE turmas SET status = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public void excluir(int id) throws SQLException {
		String sql = "DELETE FROM turmas WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		}
	}

	public List<Turma> listar() throws SQLException {
		return consultar("SELECT * FROM turmas ORDER BY periodo_letivo DESC, codigo", null);
	}

	public List<Turma> listarPorDisciplina(int disciplinaId) throws SQLException {
		return consultar("SELECT * FROM turmas WHERE disciplina_id = ? ORDER BY periodo_letivo DESC, codigo",
				disciplinaId);
	}

	public List<Turma> listarPorProfessor(int professorId) throws SQLException {
		return consultar("SELECT * FROM turmas WHERE professor_id = ? ORDER BY periodo_letivo DESC, codigo",
				professorId);
	}

	public List<Turma> listarPorPeriodo(String periodoLetivo) throws SQLException {
		return consultar("SELECT * FROM turmas WHERE periodo_letivo = ? ORDER BY codigo", periodoLetivo);
	}

	public Optional<Turma> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM turmas WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Turma> buscarPorCodigo(String codigo) throws SQLException {
		return consultar("SELECT * FROM turmas WHERE codigo = ?", codigo).stream().findFirst();
	}

	private List<Turma> consultar(String sql, Object parametro) throws SQLException {
		List<Turma> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Turma t) throws SQLException {
		ps.setString(1, t.getCodigo());
		ps.setInt(2, t.getDisciplina_id());
		ps.setInt(3, t.getProfessor_id());
		ps.setString(4, t.getPeriodo_letivo());
		ps.setDate(5, t.getData_inicio());
		ps.setDate(6, t.getData_fim());
		ps.setString(7, t.getTurno());
		ps.setString(8, t.getSala());
		ps.setInt(9, t.getVagas());
		ps.setBigDecimal(10, t.getMedia_aprovacao());
		ps.setBigDecimal(11, t.getFrequencia_minima());
		ps.setString(12, t.getStatus());
	}

	private Turma montar(ResultSet rs) throws SQLException {
		Turma t = new Turma();
		t.setId(rs.getInt("id"));
		t.setCodigo(rs.getString("codigo"));
		t.setDisciplina_id(rs.getInt("disciplina_id"));
		t.setProfessor_id(rs.getInt("professor_id"));
		t.setPeriodo_letivo(rs.getString("periodo_letivo"));
		t.setData_inicio(rs.getDate("data_inicio"));
		t.setData_fim(rs.getDate("data_fim"));
		t.setTurno(rs.getString("turno"));
		t.setSala(rs.getString("sala"));
		t.setVagas(rs.getInt("vagas"));
		t.setMedia_aprovacao(rs.getBigDecimal("media_aprovacao"));
		t.setFrequencia_minima(rs.getBigDecimal("frequencia_minima"));
		t.setStatus(rs.getString("status"));
		t.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		t.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return t;
	}
}
