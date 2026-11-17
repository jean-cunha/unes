package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.CursoDAO;
import br.com.unes.model.Curso;

public class CursoController {

	public static final Set<String> MODALIDADES = Set.of("PRESENCIAL", "EAD", "HIBRIDO");
	private final CursoDAO dao = new CursoDAO();

	public List<Curso> listar() throws SQLException {
		return dao.listar();
	}

	public List<Curso> listarAtivos() throws SQLException {
		return dao.listarAtivos();
	}

	public Optional<Curso> buscarPorId(int id) throws SQLException {
		return dao.buscarPorId(id);
	}

	public void salvar(Curso c) throws SQLException {
		exigirTexto(c.getCodigo(), "Código");
		exigirTexto(c.getNome(), "Nome");
		exigir(c.getCarga_horaria() > 0, "Carga horária deve ser maior que zero.");
		exigir(c.getQuantidade_semestres() > 0, "Quantidade de semestres deve ser maior que zero.");
		exigir(MODALIDADES.contains(c.getModalidade()), "Modalidade inválida.");
		exigir("ATIVO".equals(c.getStatus_curso()) || "INATIVO".equals(c.getStatus_curso()), "Status inválido.");
		Optional<Curso> outro = dao.buscarPorCodigo(c.getCodigo());
		exigir(outro.isEmpty() || outro.get().getId() == c.getId(), "Já existe curso com este código.");

		if (c.getId() == 0) {
			LocalDateTime agora = LocalDateTime.now();
			c.setCriado_em(agora);
			c.setAtualizado_em(agora);
			dao.inserir(c);
			Auditor.registrar("INSERIR", "cursos", 0, c.getCodigo());
		} else {
			dao.atualizar(c);
			Auditor.registrar("ATUALIZAR", "cursos", c.getId(), c.getCodigo());
		}
	}

	/** Curso nunca é apagado (RN09). */
	public void inativar(Curso c) throws SQLException {
		dao.inativar(c.getId());
		Auditor.registrar("INATIVAR", "cursos", c.getId(), c.getCodigo());
	}
}
