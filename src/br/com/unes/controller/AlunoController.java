package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;
import static br.com.unes.util.Formato.vazio;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.AlunoDAO;
import br.com.unes.model.Aluno;
import br.com.unes.util.Formato;

public class AlunoController {

	public static final Set<String> TURNOS = Set.of("MATUTINO", "VESPERTINO", "NOTURNO", "INTEGRAL");
	private final AlunoDAO dao = new AlunoDAO();

	public List<Aluno> listar() throws SQLException {
		return dao.listar();
	}

	public List<Aluno> buscar(String campo, String valor) throws SQLException {
		return dao.buscar(campo, valor, "nome", false);
	}

	public Optional<Aluno> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Aluno a) throws SQLException {
		exigirTexto(a.getRa(), "RA");
		exigirTexto(a.getNome(), "Nome");
		exigir(a.getData_nascimento() != null, "Data de nascimento é obrigatória.");
		exigir(Formato.cpfValido(a.getCpf()), "CPF inválido.");
		exigir(Formato.emailValido(a.getEmail()), "E-mail inválido.");
		exigir(a.getCurso_id() > 0, "Selecione o curso.");
		exigir(a.getTurno() != null, "Selecione o turno.");
		exigir(a.getNota_enem() >= 0 && a.getNota_enem() <= 1000, "Nota do ENEM deve estar entre 0 e 1000.");
		exigir(!a.isPossui_problema_saude() || !vazio(a.getDescricao_problema_saude()),
				"Descreva o problema de saúde.");
		exigir(!a.isPossui_deficiencia() || !vazio(a.getDescricao_deficiencia()), "Descreva a deficiência.");
		a.setCpf(Formato.apenasDigitos(a.getCpf()));

		Optional<Aluno> porRa = dao.buscarPorRa(a.getRa());
		exigir(porRa.isEmpty() || porRa.get().getId() == a.getId(), "Já existe aluno com este RA.");
		Optional<Aluno> porCpf = dao.buscarPorCpf(a.getCpf());
		exigir(porCpf.isEmpty() || porCpf.get().getId() == a.getId(), "Já existe aluno com este CPF.");

		if (a.getId() == 0) {
			if (a.getData_ingresso() == null) a.setData_ingresso(LocalDateTime.now());
			dao.inserir(a);
			Auditor.registrar("INSERIR", "alunos", 0, a.getRa());
		} else {
			dao.atualizar(a);
			Auditor.registrar("ATUALIZAR", "alunos", a.getId(), a.getRa());
		}
	}

	public void excluir(Aluno a) throws SQLException {
		dao.excluir(a.getId());
		Auditor.registrar("EXCLUIR", "alunos", a.getId(), a.getRa());
	}
}
