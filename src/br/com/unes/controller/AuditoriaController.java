package br.com.unes.controller;

import java.sql.SQLException;
import java.util.List;

import br.com.unes.dao.AuditoriaDAO;
import br.com.unes.model.Auditoria;

public class AuditoriaController {

	private final AuditoriaDAO dao = new AuditoriaDAO();

	public List<Auditoria> listarRecentes(int limite) throws SQLException {
		return dao.listarRecentes(limite);
	}

	public List<Auditoria> listarPorUsuario(int usuarioId) throws SQLException {
		return dao.listarPorUsuario(usuarioId);
	}
}
