package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.TurmaDAO;
import br.com.unes.model.Turma;

public class TurmaController {

	public static final Set<String> STATUS = Set.of("ABERTA", "EM_ANDAMENTO", "ENCERRADA");
	private final TurmaDAO dao = new TurmaDAO();

	public List<Turma> listar() throws SQLException {
		return dao.listar();
	}

	public List<Turma> listarPorProfessor(int professorId) throws SQLException {
		return dao.listarPorProfessor(professorId);
	}

	public Optional<Turma> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Turma t) throws SQLException {
		exigirTexto(t.getCodigo(), "Código");
		exigir(t.getDisciplina_id() > 0, "Selecione a disciplina.");
		exigir(t.getProfessor_id() > 0, "Selecione o professor.");
		exigir(t.getPeriodo_letivo() != null && t.getPeriodo_letivo().matches("\\d{4}\\.[12]"),
				"Período letivo deve estar no formato AAAA.S (ex.: 2026.2).");
		exigir(t.getData_inicio() != null && t.getData_fim() != null, "Informe data de início e de fim.");
		exigir(!t.getData_fim().before(t.getData_inicio()), "Data de fim anterior à data de início.");
		exigir(t.getVagas() > 0, "Vagas deve ser maior que zero.");
		exigir(t.getMedia_aprovacao() != null && t.getMedia_aprovacao().signum() >= 0
				&& t.getMedia_aprovacao().compareTo(BigDecimal.TEN) <= 0, "Média de aprovação deve estar entre 0 e 10.");
		exigir(t.getFrequencia_minima() != null && t.getFrequencia_minima().signum() >= 0
				&& t.getFrequencia_minima().compareTo(new BigDecimal("100")) <= 0,
				"Frequência mínima deve estar entre 0 e 100.");
		exigir(STATUS.contains(t.getStatus()), "Status inválido.");
		Optional<Turma> outra = dao.buscarPorCodigo(t.getCodigo());
		exigir(outra.isEmpty() || outra.get().getId() == t.getId(), "Já existe turma com este código.");

		if (t.getId() == 0) {
			dao.inserir(t);
			Auditor.registrar("INSERIR", "turmas", 0, t.getCodigo());
		} else {
			dao.atualizar(t);
			Auditor.registrar("ATUALIZAR", "turmas", t.getId(), t.getCodigo());
		}
	}

	public void excluir(Turma t) throws SQLException {
		dao.excluir(t.getId());
		Auditor.registrar("EXCLUIR", "turmas", t.getId(), t.getCodigo());
	}
}
