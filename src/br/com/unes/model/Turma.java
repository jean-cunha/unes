package br.com.unes.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Turma {
	private int id;
	private String codigo;
	private int disciplina_id;
	private int professor_id;
	private String periodo_letivo;		//formato AAAA.S (ex.: 2026.2)
	private Date data_inicio;
	private Date data_fim;
	private String turno;
	private String sala;
	private int vagas;
	private BigDecimal media_aprovacao;		//padrão 6.00
	private BigDecimal frequencia_minima;		//padrão 75.00
	private String status;		//ABERTA, EM_ANDAMENTO ou ENCERRADA
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

	public int getDisciplina_id() {
		return disciplina_id;
	}

	public void setDisciplina_id(int disciplina_id) {
		this.disciplina_id = disciplina_id;
	}

	public int getProfessor_id() {
		return professor_id;
	}

	public void setProfessor_id(int professor_id) {
		this.professor_id = professor_id;
	}

	public String getPeriodo_letivo() {
		return periodo_letivo;
	}

	public void setPeriodo_letivo(String periodo_letivo) {
		this.periodo_letivo = periodo_letivo;
	}

	public Date getData_inicio() {
		return data_inicio;
	}

	public void setData_inicio(Date data_inicio) {
		this.data_inicio = data_inicio;
	}

	public Date getData_fim() {
		return data_fim;
	}

	public void setData_fim(Date data_fim) {
		this.data_fim = data_fim;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public int getVagas() {
		return vagas;
	}

	public void setVagas(int vagas) {
		this.vagas = vagas;
	}

	public BigDecimal getMedia_aprovacao() {
		return media_aprovacao;
	}

	public void setMedia_aprovacao(BigDecimal media_aprovacao) {
		this.media_aprovacao = media_aprovacao;
	}

	public BigDecimal getFrequencia_minima() {
		return frequencia_minima;
	}

	public void setFrequencia_minima(BigDecimal frequencia_minima) {
		this.frequencia_minima = frequencia_minima;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
