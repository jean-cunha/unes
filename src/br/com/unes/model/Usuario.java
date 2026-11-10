package br.com.unes.model;

import java.time.LocalDateTime;

public class Usuario {
	private int id;
	private String login;
	private String senha_hash;
	private String perfil;		//SECRETARIA, PEDAGOGIA, PROFESSOR ou ALUNO
	private boolean ativo;
	private Integer aluno_id;		//preenchido apenas no perfil ALUNO
	private Integer professor_id;		//preenchido apenas no perfil PROFESSOR
	private LocalDateTime ultimo_acesso;
	private LocalDateTime criado_em;
	private LocalDateTime atualizado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha_hash() {
		return senha_hash;
	}

	public void setSenha_hash(String senha_hash) {
		this.senha_hash = senha_hash;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getAluno_id() {
		return aluno_id;
	}

	public void setAluno_id(Integer aluno_id) {
		this.aluno_id = aluno_id;
	}

	public Integer getProfessor_id() {
		return professor_id;
	}

	public void setProfessor_id(Integer professor_id) {
		this.professor_id = professor_id;
	}

	public LocalDateTime getUltimo_acesso() {
		return ultimo_acesso;
	}

	public void setUltimo_acesso(LocalDateTime ultimo_acesso) {
		this.ultimo_acesso = ultimo_acesso;
	}

	public LocalDateTime getCriado_em() {
		return criado_em;
	}

	public void setCriado_em(LocalDateTime criado_em) {
		this.criado_em = criado_em;
	}

	public LocalDateTime getAtualizado_em() {
		return atualizado_em;
	}

	public void setAtualizado_em(LocalDateTime atualizado_em) {
		this.atualizado_em = atualizado_em;
	}
}
