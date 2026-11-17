package br.com.unes.util;

import br.com.unes.model.Usuario;

/** Usuário autenticado na execução atual. */
public final class Sessao {

	private static Usuario usuario;

	private Sessao() {
	}

	public static Usuario getUsuario() {
		return usuario;
	}

	public static void setUsuario(Usuario u) {
		usuario = u;
	}

	public static Integer getUsuarioId() {
		return usuario == null ? null : usuario.getId();
	}

	public static boolean temPerfil(String... perfis) {
		if (usuario == null) return false;
		for (String p : perfis) if (p.equals(usuario.getPerfil())) return true;
		return false;
	}
}
