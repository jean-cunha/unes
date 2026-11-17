package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import br.com.unes.dao.FrequenciaDAO;
import br.com.unes.model.Frequencia;
import br.com.unes.util.Sessao;

public class FrequenciaController {

	private final FrequenciaDAO dao = new FrequenciaDAO();
	private final MatriculaController matriculaController = new MatriculaController();

	public List<Frequencia> listarPorMatricula(int matriculaId) throws SQLException {
		return dao.listarPorMatricula(matriculaId);
	}

	public Optional<Frequencia> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Frequencia f) throws SQLException {
		exigir(f.getMatricula_id() > 0, "Selecione a matrícula.");
		exigir(f.getData_aula() != null, "Data da aula é obrigatória.");
		Optional<Frequencia> existente = dao.buscarPorMatriculaEData(f.getMatricula_id(), f.getData_aula());
		exigir(existente.isEmpty() || existente.get().getId() == f.getId(),
				"Já existe registro de frequência para esta matrícula nesta data.");

		f.setRegistrado_por_usuario_id(Sessao.getUsuarioId());
		if (f.getId() == 0) {
			dao.inserir(f);
			Auditor.registrar("INSERIR", "frequencias", 0, "matricula " + f.getMatricula_id());
		} else {
			dao.atualizar(f);
			Auditor.registrar("ATUALIZAR", "frequencias", f.getId(), "matricula " + f.getMatricula_id());
		}
		matriculaController.recalcularFrequencia(f.getMatricula_id());
	}

	public void excluir(Frequencia f) throws SQLException {
		dao.excluir(f.getId());
		Auditor.registrar("EXCLUIR", "frequencias", f.getId(), "matricula " + f.getMatricula_id());
		matriculaController.recalcularFrequencia(f.getMatricula_id());
	}
}
