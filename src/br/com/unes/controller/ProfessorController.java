package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;
import static br.com.unes.util.Formato.vazio;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.ProfessorDAO;
import br.com.unes.model.Professor;
import br.com.unes.util.Formato;

public class ProfessorController {

	public static final Set<String> TITULACOES = Set.of("GRADUADO", "ESPECIALISTA", "MESTRE", "DOUTOR");
	private final ProfessorDAO dao = new ProfessorDAO();

	public List<Professor> listar() throws SQLException {
		return dao.listar();
	}

	public List<Professor> listarAtivos() throws SQLException {
		return dao.listarAtivos();
	}

	public Optional<Professor> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Professor p) throws SQLException {
		exigirTexto(p.getNome(), "Nome");
		exigir(p.getData_nascimento() != null, "Data de nascimento é obrigatória.");
		exigir(Formato.cpfValido(p.getCpf()), "CPF inválido.");
		exigir(Formato.emailValido(p.getEmail()), "E-mail inválido.");
		exigir(TITULACOES.contains(p.getTitulacao()), "Selecione a titulação.");
		exigir("ATIVO".equals(p.getStatus_professor()) || "INATIVO".equals(p.getStatus_professor()),
				"Status inválido.");
		exigir(!p.isProblema_saude() || !vazio(p.getDescricao_problema_saude()), "Descreva o problema de saúde.");
		exigir(!p.isDeficiencia() || !vazio(p.getDescricao_deficiencia()), "Descreva a deficiência.");
		p.setCpf(Formato.apenasDigitos(p.getCpf()));

		Optional<Professor> outro = dao.buscarPorCpf(p.getCpf());
		exigir(outro.isEmpty() || outro.get().getId() == p.getId(), "Já existe professor com este CPF.");

		if (p.getId() == 0) {
			dao.inserir(p);
			Auditor.registrar("INSERIR", "professores", 0, p.getNome());
		} else {
			dao.atualizar(p);
			Auditor.registrar("ATUALIZAR", "professores", p.getId(), p.getNome());
		}
	}

	public void inativar(Professor p) throws SQLException {
		dao.inativar(p.getId());
		Auditor.registrar("INATIVAR", "professores", p.getId(), p.getNome());
	}
}
