package br.com.unes.controller;

import br.com.unes.dao.AuditoriaDAO;
import br.com.unes.model.Auditoria;
import br.com.unes.util.Sessao;

/** Registra ações na trilha de auditoria; falhas de auditoria nunca interrompem a operação. */
final class Auditor {

	private static final AuditoriaDAO DAO = new AuditoriaDAO();

	private Auditor() {
	}

	static void registrar(String acao, String entidade, long registroId, String detalhes) {
		try {
			Auditoria a = new Auditoria();
			a.setUsuario_id(Sessao.getUsuarioId());
			a.setAcao(acao);
			a.setEntidade(entidade);
			a.setRegistro_id(registroId > 0 ? registroId : null);
			a.setDetalhes(detalhes);
			DAO.inserir(a);
		} catch (Exception e) {
			System.err.println("Falha ao registrar auditoria: " + e.getMessage());
		}
	}
}
