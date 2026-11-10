package br.com.unes.model;

import java.time.LocalDateTime;

public class Documento {
	private int id;
	private String tipo;		//COMPROVANTE_RESIDENCIA, COMPROVANTE_RENDA, CDI, ANTECEDENTES_CRIMINAIS, HISTORICO_ESCOLAR, DIPLOMA_GRADUACAO ou HISTORICO_ACADEMICO
	private String caminho_arquivo;
	private LocalDateTime data_envio;
	private Integer aluno_id;		//o documento pertence a um aluno OU a um professor
	private Integer professor_id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCaminho_arquivo() {
		return caminho_arquivo;
	}

	public void setCaminho_arquivo(String caminho_arquivo) {
		this.caminho_arquivo = caminho_arquivo;
	}

	public LocalDateTime getData_envio() {
		return data_envio;
	}

	public void setData_envio(LocalDateTime data_envio) {
		this.data_envio = data_envio;
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
}
