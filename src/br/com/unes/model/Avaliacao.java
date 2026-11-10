package br.com.unes.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

public class Avaliacao {
	private int id;
	private int turma_id;
	private String descricao;
	private String tipo;		//PROVA, TRABALHO, SEMINARIO ou OUTRO
	private BigDecimal valor_maximo;
	private Date data_avaliacao;
	private int ordem;
	private LocalDateTime criado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTurma_id() {
		return turma_id;
	}

	public void setTurma_id(int turma_id) {
		this.turma_id = turma_id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getValor_maximo() {
		return valor_maximo;
	}

	public void setValor_maximo(BigDecimal valor_maximo) {
		this.valor_maximo = valor_maximo;
	}

	public Date getData_avaliacao() {
		return data_avaliacao;
	}

	public void setData_avaliacao(Date data_avaliacao) {
		this.data_avaliacao = data_avaliacao;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public LocalDateTime getCriado_em() {
		return criado_em;
	}

	public void setCriado_em(LocalDateTime criado_em) {
		this.criado_em = criado_em;
	}
}
