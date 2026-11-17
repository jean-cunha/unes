package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.model.Professor;
import br.com.unes.util.Conexao;

public class ProfessorDAO {

	private static final String COLUNAS = "nome, nome_social, genero, data_nascimento, cpf, titulo_eleitor, "
			+ "titulacao, curriculo_lattes, problema_saude, descricao_problema_saude, deficiencia, "
			+ "descricao_deficiencia, email, telefone, telefone_emergencia, cep, logradouro, numero, "
			+ "complemento, bairro, cidade, uf, status_professor";

	public void inserir(Professor p) throws SQLException {
		String sql = "INSERT INTO professores (" + COLUNAS + ") VALUES (" + "?, ".repeat(22) + "?)";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, p);
			ps.executeUpdate();
		}
	}

	public void atualizar(Professor p) throws SQLException {
		String sql = "UPDATE professores SET nome = ?, nome_social = ?, genero = ?, data_nascimento = ?, "
				+ "cpf = ?, titulo_eleitor = ?, titulacao = ?, curriculo_lattes = ?, problema_saude = ?, "
				+ "descricao_problema_saude = ?, deficiencia = ?, descricao_deficiencia = ?, email = ?, "
				+ "telefone = ?, telefone_emergencia = ?, cep = ?, logradouro = ?, numero = ?, complemento = ?, "
				+ "bairro = ?, cidade = ?, uf = ?, status_professor = ?, atualizado_em = CURRENT_TIMESTAMP "
				+ "WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			preencher(ps, p);
			ps.setInt(24, p.getId());
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
		String sql = "UPDATE professores SET status_professor = ?, atualizado_em = CURRENT_TIMESTAMP WHERE id = ?";

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}

	public List<Professor> listar() throws SQLException {
		return consultar("SELECT * FROM professores ORDER BY nome", null);
	}

	public List<Professor> listarAtivos() throws SQLException {
		return consultar("SELECT * FROM professores WHERE status_professor = 'ATIVO' ORDER BY nome", null);
	}

	public List<Professor> buscarPorNome(String nome) throws SQLException {
		return consultar("SELECT * FROM professores WHERE nome LIKE ? ORDER BY nome", "%" + nome + "%");
	}

	public Optional<Professor> buscarPorId(int id) throws SQLException {
		return consultar("SELECT * FROM professores WHERE id = ?", id).stream().findFirst();
	}

	public Optional<Professor> buscarPorCpf(String cpf) throws SQLException {
		return consultar("SELECT * FROM professores WHERE cpf = ?", cpf).stream().findFirst();
	}

	private List<Professor> consultar(String sql, Object parametro) throws SQLException {
		List<Professor> lista = new ArrayList<>();

		try (Connection conexao = Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			if (parametro != null) ps.setObject(1, parametro);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) lista.add(montar(rs));
			}
		}
		return lista;
	}

	private void preencher(PreparedStatement ps, Professor p) throws SQLException {
		ps.setString(1, p.getNome());
		ps.setString(2, p.getNome_social());
		ps.setString(3, p.getGenero());
		ps.setDate(4, p.getData_nascimento());
		ps.setString(5, p.getCpf());
		ps.setString(6, p.getTitulo_eleitor());
		ps.setString(7, p.getTitulacao());
		ps.setString(8, p.getCurriculo_lattes());
		ps.setBoolean(9, p.isProblema_saude());
		ps.setString(10, p.getDescricao_problema_saude());
		ps.setBoolean(11, p.isDeficiencia());
		ps.setString(12, p.getDescricao_deficiencia());
		ps.setString(13, p.getEmail());
		ps.setString(14, p.getTelefone());
		ps.setString(15, p.getTelefone_emergencia());
		ps.setString(16, p.getCep());
		ps.setString(17, p.getLogradouro());
		ps.setString(18, p.getNumero());
		ps.setString(19, p.getComplemento());
		ps.setString(20, p.getBairro());
		ps.setString(21, p.getCidade());
		ps.setString(22, p.getUf());
		ps.setString(23, p.getStatus_professor());
	}

	private Professor montar(ResultSet rs) throws SQLException {
		Professor p = new Professor();
		p.setId(rs.getInt("id"));
		p.setNome(rs.getString("nome"));
		p.setNome_social(rs.getString("nome_social"));
		p.setGenero(rs.getString("genero"));
		p.setData_nascimento(rs.getDate("data_nascimento"));
		p.setCpf(rs.getString("cpf"));
		p.setTitulo_eleitor(rs.getString("titulo_eleitor"));
		p.setTitulacao(rs.getString("titulacao"));
		p.setCurriculo_lattes(rs.getString("curriculo_lattes"));
		p.setProblema_saude(rs.getBoolean("problema_saude"));
		p.setDescricao_problema_saude(rs.getString("descricao_problema_saude"));
		p.setDeficiencia(rs.getBoolean("deficiencia"));
		p.setDescricao_deficiencia(rs.getString("descricao_deficiencia"));
		p.setEmail(rs.getString("email"));
		p.setTelefone(rs.getString("telefone"));
		p.setTelefone_emergencia(rs.getString("telefone_emergencia"));
		p.setCep(rs.getString("cep"));
		p.setLogradouro(rs.getString("logradouro"));
		p.setNumero(rs.getString("numero"));
		p.setComplemento(rs.getString("complemento"));
		p.setBairro(rs.getString("bairro"));
		p.setCidade(rs.getString("cidade"));
		p.setUf(rs.getString("uf"));
		p.setStatus_professor(rs.getString("status_professor"));
		p.setCriado_em(DaoUtil.lerData(rs, "criado_em"));
		p.setAtualizado_em(DaoUtil.lerData(rs, "atualizado_em"));
		return p;
	}
}
