package br.com.unes.model;

import java.time.LocalDateTime;

public class Curso {
	private int id;
	private String codigo;
	private String nome;
	private int carga_horaria;
	private String modalidade;		//PRESENCIAL, EAD ou HIBRIDO
	private int quantidade_semestres;
	private String status_curso;		//ATIVO ou INATIVO (nunca apagar: inativar - RN09)
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

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public int getQuantidade_semestres() {
		return quantidade_semestres;
	}

	public void setQuantidade_semestres(int quantidade_semestres) {
		this.quantidade_semestres = quantidade_semestres;
	}

	public String getStatus_curso() {
		return status_curso;
	}

	public void setStatus_curso(String status_curso) {
		this.status_curso = status_curso;
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
