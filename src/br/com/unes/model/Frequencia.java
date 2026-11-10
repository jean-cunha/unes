package br.com.unes.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Frequencia {
	private int id;
	private int matricula_id;
	private Date data_aula;
	private boolean presente;
	private Integer registrado_por_usuario_id;
	private LocalDateTime criado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatricula_id() {
		return matricula_id;
	}

	public void setMatricula_id(int matricula_id) {
		this.matricula_id = matricula_id;
	}

	public Date getData_aula() {
		return data_aula;
	}

	public void setData_aula(Date data_aula) {
		this.data_aula = data_aula;
	}

	public boolean isPresente() {
		return presente;
	}

	public void setPresente(boolean presente) {
		this.presente = presente;
	}

	public Integer getRegistrado_por_usuario_id() {
		return registrado_por_usuario_id;
	}

	public void setRegistrado_por_usuario_id(Integer registrado_por_usuario_id) {
		this.registrado_por_usuario_id = registrado_por_usuario_id;
	}

	public LocalDateTime getCriado_em() {
		return criado_em;
	}

	public void setCriado_em(LocalDateTime criado_em) {
		this.criado_em = criado_em;
	}
}
