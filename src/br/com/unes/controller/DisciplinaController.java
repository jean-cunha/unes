package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.unes.dao.DisciplinaDAO;
import br.com.unes.dao.DisciplinaPrerequisitoDAO;
import br.com.unes.model.Disciplina;
import br.com.unes.model.DisciplinaPrerequisito;

public class DisciplinaController {

	private final DisciplinaDAO dao = new DisciplinaDAO();
	private final DisciplinaPrerequisitoDAO prereqDao = new DisciplinaPrerequisitoDAO();

	public List<Disciplina> listar() throws SQLException {
		return dao.listar();
	}

	public List<Disciplina> listarPorCurso(int cursoId) throws SQLException {
		return dao.listarPorCurso(cursoId);
	}

	public Optional<Disciplina> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Disciplina d) throws SQLException {
		exigirTexto(d.getCodigo(), "Código");
		exigirTexto(d.getNome(), "Nome");
		exigir(d.getCarga_horaria() > 0, "Carga horária deve ser maior que zero.");
		exigir(d.getSemestre_sugerido() > 0, "Semestre sugerido deve ser maior que zero.");
		exigir(d.getCurso_id() > 0, "Selecione o curso.");
		exigir("ATIVO".equals(d.getStatus_disciplina()) || "INATIVO".equals(d.getStatus_disciplina()),
				"Status inválido.");
		Optional<Disciplina> outra = dao.buscarPorCodigo(d.getCodigo());
		exigir(outra.isEmpty() || outra.get().getId() == d.getId(), "Já existe disciplina com este código.");

		if (d.getId() == 0) {
			dao.inserir(d);
			Auditor.registrar("INSERIR", "disciplinas", 0, d.getCodigo());
		} else {
			dao.atualizar(d);
			Auditor.registrar("ATUALIZAR", "disciplinas", d.getId(), d.getCodigo());
		}
	}

	public void inativar(Disciplina d) throws SQLException {
		dao.inativar(d.getId());
		Auditor.registrar("INATIVAR", "disciplinas", d.getId(), d.getCodigo());
	}

	//----- Pré-requisitos -----

	public List<Disciplina> listarPrerequisitos(int disciplinaId) throws SQLException {
		List<Disciplina> lista = new ArrayList<>();
		for (DisciplinaPrerequisito dp : prereqDao.listarPorDisciplina(disciplinaId))
			dao.buscarPorId(dp.getPrerequisito_id()).ifPresent(lista::add);
		return lista;
	}

	public void adicionarPrerequisito(int disciplinaId, int prerequisitoId) throws SQLException {
		exigir(disciplinaId != prerequisitoId, "Uma disciplina não pode ser pré-requisito dela mesma.");
		exigir(!prereqDao.existe(disciplinaId, prerequisitoId), "Pré-requisito já cadastrado.");
		exigir(!prereqDao.existe(prerequisitoId, disciplinaId),
				"Pré-requisito circular: a outra disciplina já depende desta.");
		DisciplinaPrerequisito dp = new DisciplinaPrerequisito();
		dp.setDisciplina_id(disciplinaId);
		dp.setPrerequisito_id(prerequisitoId);
		prereqDao.inserir(dp);
		Auditor.registrar("INSERIR", "disciplina_prerequisitos", disciplinaId, "prerequisito " + prerequisitoId);
	}

	public void removerPrerequisito(int disciplinaId, int prerequisitoId) throws SQLException {
		prereqDao.remover(disciplinaId, prerequisitoId);
		Auditor.registrar("EXCLUIR", "disciplina_prerequisitos", disciplinaId, "prerequisito " + prerequisitoId);
	}

	/** Lista os pré-requisitos ainda não cursados (aprovados) pelo aluno para a disciplina. */
	public List<Disciplina> prerequisitosPendentes(int disciplinaId, java.util.Set<Integer> aprovadas)
			throws SQLException {
		List<Disciplina> pendentes = new ArrayList<>();
		for (Disciplina d : listarPrerequisitos(disciplinaId))
			if (!aprovadas.contains(d.getId())) pendentes.add(d);
		return pendentes;
	}
}
