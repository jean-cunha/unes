package br.com.unes.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Nota {
	private int id;
	private int matricula_id;
	private int avaliacao_id;
	private BigDecimal valor;
	private LocalDateTime data_lancamento;
	private Integer lancada_por_usuario_id;

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

	public int getAvaliacao_id() {
		return avaliacao_id;
	}

	public void setAvaliacao_id(int avaliacao_id) {
		this.avaliacao_id = avaliacao_id;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDateTime getData_lancamento() {
		return data_lancamento;
	}

	public void setData_lancamento(LocalDateTime data_lancamento) {
		this.data_lancamento = data_lancamento;
	}

	public Integer getLancada_por_usuario_id() {
		return lancada_por_usuario_id;
	}

	public void setLancada_por_usuario_id(Integer lancada_por_usuario_id) {
		this.lancada_por_usuario_id = lancada_por_usuario_id;
	}
}
