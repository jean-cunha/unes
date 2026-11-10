package br.com.unes.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Matricula {
	private int id;
	private int aluno_id;
	private int turma_id;
	private Date data_matricula;
	private String situacao;		//ATIVA, TRANCADA, CANCELADA, APROVADO ou REPROVADO
	private BigDecimal media_final;
	private BigDecimal frequencia_percentual;
	private LocalDateTime criado_em;
	private LocalDateTime atualizado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAluno_id() {
		return aluno_id;
	}

	public void setAluno_id(int aluno_id) {
		this.aluno_id = aluno_id;
	}

	public int getTurma_id() {
		return turma_id;
	}

	public void setTurma_id(int turma_id) {
		this.turma_id = turma_id;
	}

	public Date getData_matricula() {
		return data_matricula;
	}

	public void setData_matricula(Date data_matricula) {
		this.data_matricula = data_matricula;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public BigDecimal getMedia_final() {
		return media_final;
	}

	public void setMedia_final(BigDecimal media_final) {
		this.media_final = media_final;
	}

	public BigDecimal getFrequencia_percentual() {
		return frequencia_percentual;
	}

	public void setFrequencia_percentual(BigDecimal frequencia_percentual) {
		this.frequencia_percentual = frequencia_percentual;
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
