package br.com.unes.controller;

import static br.com.unes.util.Formato.exigir;
import static br.com.unes.util.Formato.exigirTexto;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import br.com.unes.dao.UsuarioDAO;
import br.com.unes.model.Usuario;
import br.com.unes.util.SenhaUtil;
import br.com.unes.util.Sessao;

public class UsuarioController {

	public static final Set<String> PERFIS = Set.of("SECRETARIA", "PEDAGOGIA", "PROFESSOR", "ALUNO");
	private final UsuarioDAO dao = new UsuarioDAO();

	public List<Usuario> listar() throws SQLException {
		return dao.listar();
	}

	public boolean existeAlgumUsuario() throws SQLException {
		return !dao.listar().isEmpty();
	}

	/** Autentica e abre a sessão; lança IllegalArgumentException com mensagem genérica em caso de falha. */
	public Usuario autenticar(String login, String senha) throws SQLException {
		Optional<Usuario> u = dao.buscarPorLogin(login == null ? "" : login.trim());
		if (u.isEmpty() || !u.get().isAtivo() || !SenhaUtil.conferir(senha == null ? "" : senha, u.get().getSenha_hash()))
			throw new IllegalArgumentException("Login ou senha inválidos.");
		dao.registrarAcesso(u.get().getId());
		Sessao.setUsuario(u.get());
		Auditor.registrar("LOGIN", "usuarios", u.get().getId(), null);
		return u.get();
	}

	public void sair() {
		if (Sessao.getUsuario() != null) Auditor.registrar("LOGOUT", "usuarios", Sessao.getUsuario().getId(), null);
		Sessao.setUsuario(null);
	}

	/**
	 * Salva o usuário. Em usuário novo a senha é obrigatória; em edição, senha em
	 * branco mantém a atual.
	 */
	public void salvar(Usuario u, String senhaNova) throws SQLException {
		exigirTexto(u.getLogin(), "Login");
		exigir(PERFIS.contains(u.getPerfil()), "Perfil inválido.");
		exigir(!"ALUNO".equals(u.getPerfil()) || u.getAluno_id() != null, "Perfil ALUNO exige vínculo com um aluno.");
		exigir(!"PROFESSOR".equals(u.getPerfil()) || u.getProfessor_id() != null,
				"Perfil PROFESSOR exige vínculo com um professor.");
		Optional<Usuario> outro = dao.buscarPorLogin(u.getLogin());
		exigir(outro.isEmpty() || outro.get().getId() == u.getId(), "Já existe usuário com este login.");

		boolean trocarSenha = senhaNova != null && !senhaNova.isEmpty();
		if (u.getId() == 0 || trocarSenha) {
			exigir(trocarSenha && senhaNova.length() >= 6, "Senha deve ter ao menos 6 caracteres.");
			u.setSenha_hash(SenhaUtil.gerarHash(senhaNova));
		}
		if (u.getId() == 0) {
			dao.inserir(u);
			Auditor.registrar("INSERIR", "usuarios", 0, u.getLogin());
		} else {
			dao.atualizar(u);
			Auditor.registrar("ATUALIZAR", "usuarios", u.getId(), u.getLogin());
		}
	}

	public void definirAtivo(Usuario u, boolean ativo) throws SQLException {
		exigir(Sessao.getUsuarioId() == null || Sessao.getUsuarioId() != u.getId() || ativo,
				"Você não pode desativar o próprio usuário.");
		dao.definirAtivo(u.getId(), ativo);
		Auditor.registrar(ativo ? "ATIVAR" : "DESATIVAR", "usuarios", u.getId(), u.getLogin());
	}
}
