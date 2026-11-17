package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.AvaliacaoDAO;
import br.com.unes.model.Avaliacao;

public class AvaliacaoController {

	public static final Set<String> TIPOS = Set.of("PROVA", "TRABALHO", "SEMINARIO", "OUTRO");
	private final AvaliacaoDAO dao = new AvaliacaoDAO();

	public List<Avaliacao> listar() throws SQLException {
		return dao.listar();
	}

	public List<Avaliacao> listarPorTurma(int turmaId) throws SQLException {
		return dao.listarPorTurma(turmaId);
	}

	public Optional<Avaliacao> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Avaliacao a) throws SQLException {
		exigir(a.getTurma_id() > 0, "Selecione a turma.");
		exigirTexto(a.getDescricao(), "Descrição");
		exigir(TIPOS.contains(a.getTipo()), "Tipo inválido.");
		exigir(a.getValor_maximo() != null && a.getValor_maximo().signum() > 0, "Valor máximo deve ser maior que zero.");
		exigir(a.getOrdem() > 0, "Ordem deve ser maior que zero.");

		if (a.getId() == 0) {
			dao.inserir(a);
			Auditor.registrar("INSERIR", "avaliacoes", 0, a.getDescricao());
		} else {
			dao.atualizar(a);
			Auditor.registrar("ATUALIZAR", "avaliacoes", a.getId(), a.getDescricao());
		}
	}

	public void excluir(Avaliacao a) throws SQLException {
		dao.excluir(a.getId());
		Auditor.registrar("EXCLUIR", "avaliacoes", a.getId(), a.getDescricao());
	}
}
