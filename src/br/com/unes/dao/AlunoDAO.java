package br.com.unes.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.model.Aluno;

public class AlunoDAO {

	public void inserir(Aluno aluno) throws SQLException {
		String sql = "INSERT INTO alunos ("
				+ "ra, nome, nome_social, genero, data_nascimento, cpf, rg, "
                + "titulo_eleitor, nome_responsavel, nota_enem, possui_problema_saude, "
                + "descricao_problema_saude, possui_deficiencia, descricao_deficiencia, email, telefone, "
                + "telefone_emergencia, cep, logradouro, numero, complemento, bairro, cidade, uf, "
                + "curso_id, turno, data_ingresso) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try ( Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
		
			preencherParamtro(ps, aluno);
			ps.executeUpdate();

			}
	}
	
	public List<Aluno> listar() throws SQLException	{
		String sql = "SELECT * FROM alunos ORDER BY nome";
		List<Aluno> lista = new ArrayList<>();
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			
			while (rs.next()) {
				lista.add(montarAluno(rs));
			}
		}
		return lista;
	}
	
	public Aluno mostrarAluno(ResultSet rs) throws SQLException {
		Aluno a = new Aluno();
		
		a.setId(rs.getInt("id"));
	    a.setRa(rs.getString("ra"));
	    a.setNome(rs.getString("nome"));
	    a.setNome_social(rs.getString("nome_social"));
	    a.setGenero(rs.getString("genero"));
	    a.setData_nascimento(rs.getDate("data_nascimento"));
	    a.setCpf(rs.getString("cpf"));
	    a.setRg(rs.getString("rg"));
	    a.setTitulo_eleitor(rs.getString("titulo_eleitor"));
	    a.setNome_resposavel(rs.getString("nome_responsavel"));
	    a.setNota_enem(rs.getInt("nota_enem"));
	    a.setPossui_problema_saude(rs.getBoolean("possui_problema_saude"));
	    a.setDescricao_problema_saude(rs.getString("descricao_problema_saude"));
	    a.setPossui_deficiencia(rs.getBoolean("possui_deficiencia"));
	    a.setDescricao_deficiencia(rs.getString("descricao_deficiencia"));
	    a.setEmail(rs.getString("email"));
	    a.setTelefone(rs.getString("telefone"));
	    a.setTelefone_emergencia(rs.getString("telefone_emergencia"));
	    a.setCep(rs.getString("cep"));
	    a.setLogradouro(rs.getString("logradouro"));
	    a.setNumero(rs.getString("numero"));
	    a.setComplemento(rs.getString("complemento"));
	    a.setBairro(rs.getString("bairro"));
	    a.setCidade(rs.getString("cidade"));
	    a.setUf(rs.getString("uf"));
	    a.setCurso_id(rs.getInt("curso_id"));
	    a.setTurno(rs.getString("turno"));
	    a.setData_ingresso(rs.getTimestamp("data_ingresso").toLocalDateTime());
	    
		return a;
	}

	private static final Set<String> CAMPOS_BUSCA = Set.of("nome", "ra", "cpf", "turno");
	private static final Set<String> CAMPOS_ORDEM = Set.of("id", "ra", "nome", "data_ingresso", "atualizado_em");
	
	public List<Aluno> buscar(String campo, String valor, String ordem, boolean decrescente) throws SQLException {
		if (!CAMPOS_BUSCA.contains(campo) || !CAMPOS_ORDEM.contains(ordem)) 
			throw new IllegalArgumentException("Opção de busca invalida");

		String sql = "SELECT * FROM alunos WHERE " + campo + " LIKE ? ORDER BY " + ordem
				+ (decrescente ? " DESC" : " ASC");
		List<Aluno> lista = new ArrayList<>();
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)) {
			ps.setString(1, "%" + valor +  "%");
			try (ResultSet rs = ps.executeQuery()){
				while (rs.next()) lista.add(montarAluno(rs));
			}
		}
		return lista;
		
	}
	
	public Optional<Aluno> buscarPorId (int id) throws SQLException {
		String sql = "SELECT * FROM alunos WHERE id = ?";
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)){
			
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()){
				return rs.next() ? Optional.of(montarAluno(rs)) : Optional.empty();
			}
		}
		
	}
	
	public Optional<Aluno> buscarPorRa (String ra) throws SQLException {
		String sql = "SELECT * FROM alunos WHERE ra = ?";
		
		try(Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)){
			
			ps.setString(1, ra);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montarAluno(rs)) : Optional.empty();
			}
		}
	}
	
	public Optional<Aluno> buscarPorCpf (String cpf) throws SQLException {
		String sql = "SELECT * FROM alunos WHERE cpf = ?";
		
		try (Connection conexao = br.com.unes.util.Conexao.getConexao();
				PreparedStatement ps = conexao.prepareStatement(sql)){
			
			ps.setString(1, cpf);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? Optional.of(montarAluno(rs)) : Optional.empty();
				
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//============================================================
	// PARÂMETROS SQL
	//============================================================

	private Aluno montarAluno(ResultSet rs) throws SQLException	{
		Aluno a = new Aluno(); 
		a.setId(rs.getInt("id"));
		a.setRa(rs.getString("ra"));
		a.setNome(rs.getString("nome"));
	    a.setNome_social(rs.getString("nome_social"));
	    a.setGenero(rs.getString("genero"));
	    a.setData_nascimento(rs.getDate("data_nascimento"));
	    a.setCpf(rs.getString("cpf"));
	    a.setRg(rs.getString("rg"));
	    a.setTitulo_eleitor(rs.getString("titulo_eleitor"));
	    a.setNome_resposavel(rs.getString("nome_responsavel"));
	    a.setNota_enem(rs.getInt("nota_enem"));
	    a.setPossui_problema_saude(rs.getBoolean("possui_problema_saude"));
	    a.setDescricao_problema_saude(rs.getString("descricao_problema_saude"));
	    a.setPossui_deficiencia(rs.getBoolean("possui_deficiencia"));
	    a.setDescricao_deficiencia(rs.getString("descricao_deficiencia"));
	    a.setEmail(rs.getString("email"));
	    a.setTelefone(rs.getString("telefone"));
	    a.setTelefone_emergencia(rs.getString("telefone_emergencia"));
	    a.setCep(rs.getString("cep"));
	    a.setLogradouro(rs.getString("logradouro"));
	    a.setNumero(rs.getString("numero"));
	    a.setComplemento(rs.getString("complemento"));
	    a.setBairro(rs.getString("bairro"));
	    a.setCidade(rs.getString("cidade"));
	    a.setUf(rs.getString("uf"));
	    a.setCurso_id(rs.getInt("curso_id"));
	    a.setTurno(rs.getString("turno"));
	    a.setData_ingresso(rs.getTimestamp("data_ingresso").toLocalDateTime());
		
		return a;
	}
	
	private void preencherParamtro (PreparedStatement ps, Aluno aluno) throws SQLException{
		ps.setString(1, aluno.getRa());
		ps.setString(2, aluno.getNome());
		ps.setString(3, aluno.getNome_social());
		ps.setString(4, aluno.getGenero());
		ps.setDate(5, aluno.getData_nascimento());

		ps.setString(6, aluno.getCpf());
		ps.setString(7, aluno.getRg());
		ps.setString(8, aluno.getTitulo_eleitor());
		ps.setString(9, aluno.getNome_resposavel());
		ps.setInt(10, aluno.getNota_enem());

		ps.setBoolean(11, aluno.isPossui_problema_saude());
		ps.setString(12, aluno.getDescricao_problema_saude());

		ps.setBoolean(13, aluno.isPossui_deficiencia());
		ps.setString(14, aluno.getDescricao_deficiencia());

		ps.setString(15, aluno.getEmail());
		ps.setString(16, aluno.getTelefone());
		ps.setString(17, aluno.getTelefone_emergencia());

		ps.setString(18, aluno.getCep());
		ps.setString(19, aluno.getLogradouro());
		ps.setString(20, aluno.getNumero());
		ps.setString(21, aluno.getComplemento());
		ps.setString(22, aluno.getBairro());
		ps.setString(23, aluno.getCidade());
		ps.setString(24, aluno.getUf());

		ps.setInt(25, aluno.getCurso_id());
		ps.setString(26, aluno.getTurno());

		ps.setTimestamp(27, Timestamp.valueOf(aluno.getData_ingresso()));
		
	}
	
	
}









