package br.com.unes.model;

import java.time.LocalDateTime;

public class Disciplina {
	private int id;
	private String codigo;
	private String nome;
	private int carga_horaria;
	private int semestre_sugerido;
	private int curso_id;
	private String status_disciplina;		//ATIVO ou INATIVO
	private LocalDateTime criado_em;
	private LocalDateTime atualizado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getCarga_horaria() {
		return carga_horaria;
	}

	public void setCarga_horaria(int carga_horaria) {
		this.carga_horaria = carga_horaria;
	}

	public int getSemestre_sugerido() {
		return semestre_sugerido;
	}

	public void setSemestre_sugerido(int semestre_sugerido) {
		this.semestre_sugerido = semestre_sugerido;
	}

	public int getCurso_id() {
		return curso_id;
	}

	public void setCurso_id(int curso_id) {
		this.curso_id = curso_id;
	}

	public String getStatus_disciplina() {
		return status_disciplina;
	}

	public void setStatus_disciplina(String status_disciplina) {
		this.status_disciplina = status_disciplina;
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
