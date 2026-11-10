package br.com.unes.model;

import java.time.LocalDateTime;

public class Auditoria {
	private long id;
	private Integer usuario_id;
	private String acao;
	private String entidade;
	private Long registro_id;
	private LocalDateTime data_hora;
	private String detalhes;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Integer getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(Integer usuario_id) {
		this.usuario_id = usuario_id;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getEntidade() {
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Long getRegistro_id() {
		return registro_id;
	}

	public void setRegistro_id(Long registro_id) {
		this.registro_id = registro_id;
	}

	public LocalDateTime getData_hora() {
		return data_hora;
	}

	public void setData_hora(LocalDateTime data_hora) {
		this.data_hora = data_hora;
	}

	public String getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(String detalhes) {
		this.detalhes = detalhes;
	}
}
