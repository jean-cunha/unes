package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import br.com.unes.dao.DocumentoDAO;
import br.com.unes.model.Documento;

public class DocumentoController {

	public static final Set<String> TIPOS = Set.of("COMPROVANTE_RESIDENCIA", "COMPROVANTE_RENDA", "CDI",
			"ANTECEDENTES_CRIMINAIS", "HISTORICO_ESCOLAR", "DIPLOMA_GRADUACAO", "HISTORICO_ACADEMICO");
	private final DocumentoDAO dao = new DocumentoDAO();

	public List<Documento> listarPorAluno(int alunoId) throws SQLException {
		return dao.listarPorAluno(alunoId);
	}

	public List<Documento> listarPorProfessor(int professorId) throws SQLException {
		return dao.listarPorProfessor(professorId);
	}

	public void salvar(Documento d) throws SQLException {
		exigir(TIPOS.contains(d.getTipo()), "Tipo de documento inválido.");
		exigirTexto(d.getCaminho_arquivo(), "Arquivo");
		exigir((d.getAluno_id() == null) != (d.getProfessor_id() == null),
				"O documento deve pertencer a um aluno ou a um professor.");

		if (d.getId() == 0) {
			dao.inserir(d);
			Auditor.registrar("INSERIR", "documentos", 0, d.getTipo());
		} else {
			dao.atualizarCaminho(d.getId(), d.getCaminho_arquivo());
			Auditor.registrar("ATUALIZAR", "documentos", d.getId(), d.getTipo());
		}
	}

	public void excluir(Documento d) throws SQLException {
		dao.excluir(d.getId());
		Auditor.registrar("EXCLUIR", "documentos", d.getId(), d.getTipo());
	}
}
