package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.FrequenciaDAO;
import br.com.unes.dao.MatriculaDAO;
import br.com.unes.dao.NotaDAO;
import br.com.unes.dao.TurmaDAO;
import br.com.unes.model.Matricula;
import br.com.unes.model.Turma;

public class MatriculaController {

	public static final Set<String> SITUACOES = Set.of("ATIVA", "TRANCADA", "CANCELADA", "APROVADO", "REPROVADO");
	private final MatriculaDAO dao = new MatriculaDAO();
	private final TurmaDAO turmaDao = new TurmaDAO();
	private final NotaDAO notaDao = new NotaDAO();
	private final FrequenciaDAO frequenciaDao = new FrequenciaDAO();

	public List<Matricula> listar() throws SQLException {
		return dao.listar();
	}

	public List<Matricula> listarPorAluno(int alunoId) throws SQLException {
		return dao.listarPorAluno(alunoId);
	}

	public List<Matricula> listarPorTurma(int turmaId) throws SQLException {
		return dao.listarPorTurma(turmaId);
	}

	public Optional<Matricula> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Matricula m) throws SQLException {
		exigir(m.getAluno_id() > 0, "Selecione o aluno.");
		exigir(m.getTurma_id() > 0, "Selecione a turma.");
		exigir(m.getData_matricula() != null, "Data da matrícula é obrigatória.");
		exigir(SITUACOES.contains(m.getSituacao()), "Situação inválida.");
		Turma turma = turmaDao.buscarPorId(m.getTurma_id())
				.orElseThrow(() -> new IllegalArgumentException("Turma não encontrada."));

		Optional<Matricula> existente = dao.buscarPorAlunoETurma(m.getAluno_id(), m.getTurma_id());
		exigir(existente.isEmpty() || existente.get().getId() == m.getId(), "Aluno já matriculado nesta turma.");

		if (m.getId() == 0) {
			exigir(!"ENCERRADA".equals(turma.getStatus()), "Turma encerrada: não aceita novas matrículas.");
			exigir(dao.contarAtivasPorTurma(turma.getId()) < turma.getVagas(), "Turma sem vagas disponíveis.");
			dao.inserir(m);
			Auditor.registrar("INSERIR", "matriculas", 0, "aluno " + m.getAluno_id() + " turma " + m.getTurma_id());
		} else {
			dao.atualizar(m);
			Auditor.registrar("ATUALIZAR", "matriculas", m.getId(), m.getSituacao());
		}
	}

	/** Matrícula não é apagada: é cancelada. */
	public void cancelar(Matricula m) throws SQLException {
		dao.atualizarSituacao(m.getId(), "CANCELADA");
		Auditor.registrar("CANCELAR", "matriculas", m.getId(), null);
	}

	/**
	 * Recalcula a frequência da matrícula (presenças / total de aulas) e, se houver
	 * notas, a média final como soma das notas lançadas limitada ao máximo de 10.
	 */
	public void recalcularFrequencia(int matriculaId) throws SQLException {
		int presencas = frequenciaDao.contarPresencas(matriculaId);
		int total = presencas + frequenciaDao.contarFaltas(matriculaId);
		BigDecimal pct = total == 0 ? BigDecimal.ZERO
				: BigDecimal.valueOf(presencas).multiply(new BigDecimal("100"))
						.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
		Optional<Matricula> m = dao.buscarPorId(matriculaId);
		if (m.isPresent()) dao.atualizarResultado(matriculaId, m.get().getMedia_final(), pct);
	}

	public void recalcularMedia(int matriculaId) throws SQLException {
		BigDecimal soma = notaDao.somarPorMatricula(matriculaId).min(BigDecimal.TEN).setScale(2, RoundingMode.HALF_UP);
		Optional<Matricula> m = dao.buscarPorId(matriculaId);
		if (m.isPresent()) dao.atualizarResultado(matriculaId, soma, m.get().getFrequencia_percentual());
	}
}
