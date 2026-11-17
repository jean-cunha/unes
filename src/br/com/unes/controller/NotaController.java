package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.unes.dao.AvaliacaoDAO;
import br.com.unes.dao.MatriculaDAO;
import br.com.unes.dao.NotaDAO;
import br.com.unes.model.Avaliacao;
import br.com.unes.model.Matricula;
import br.com.unes.model.Nota;
import br.com.unes.util.Sessao;

public class NotaController {

	private final NotaDAO dao = new NotaDAO();
	private final AvaliacaoDAO avaliacaoDao = new AvaliacaoDAO();
	private final MatriculaDAO matriculaDao = new MatriculaDAO();
	private final MatriculaController matriculaController = new MatriculaController();

	public List<Nota> listarPorMatricula(int matriculaId) throws SQLException {
		return dao.listarPorMatricula(matriculaId);
	}

	public List<Nota> listarPorAvaliacao(int avaliacaoId) throws SQLException {
		return dao.listarPorAvaliacao(avaliacaoId);
	}

	public Optional<Nota> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Nota n) throws SQLException {
		exigir(n.getMatricula_id() > 0, "Selecione a matrícula.");
		exigir(n.getAvaliacao_id() > 0, "Selecione a avaliação.");
		exigir(n.getValor() != null && n.getValor().signum() >= 0, "Valor da nota inválido.");

		Avaliacao av = avaliacaoDao.buscarPorId(n.getAvaliacao_id())
				.orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada."));
		Matricula mat = matriculaDao.buscarPorId(n.getMatricula_id())
				.orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
		exigir(av.getTurma_id() == mat.getTurma_id(), "A avaliação não pertence à turma da matrícula.");
		exigir(n.getValor().compareTo(av.getValor_maximo()) <= 0,
				"Nota maior que o valor máximo da avaliação (" + av.getValor_maximo() + ").");
		exigir("ATIVA".equals(mat.getSituacao()), "Só é possível lançar nota em matrícula ATIVA.");

		n.setLancada_por_usuario_id(Sessao.getUsuarioId());
		Optional<Nota> existente = dao.buscarPorMatriculaEAvaliacao(n.getMatricula_id(), n.getAvaliacao_id());
		if (n.getId() == 0 && existente.isPresent()) {
			n.setId(existente.get().getId());	// reaproveita o lançamento existente
		}
		if (n.getId() == 0) {
			dao.inserir(n);
			Auditor.registrar("INSERIR", "notas", 0, "matricula " + n.getMatricula_id());
		} else {
			dao.atualizar(n);
			Auditor.registrar("ATUALIZAR", "notas", n.getId(), "matricula " + n.getMatricula_id());
		}
		matriculaController.recalcularMedia(n.getMatricula_id());
	}

	public void excluir(Nota n) throws SQLException {
		dao.excluir(n.getId());
		Auditor.registrar("EXCLUIR", "notas", n.getId(), "matricula " + n.getMatricula_id());
		matriculaController.recalcularMedia(n.getMatricula_id());
	}
}
