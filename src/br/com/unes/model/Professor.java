package br.com.unes.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Professor {
	// ---- Identificação ----
	private int id;
	private String nome;
	private String nome_social;		//não obrigatorio
	private String genero;
	private Date data_nascimento;
	private String cpf;
	private String titulo_eleitor;

	// ---- Formação ----
	private String titulacao;		//GRADUADO, ESPECIALISTA, MESTRE ou DOUTOR
	private String curriculo_lattes;

	// ---- Saúde e deficiência ----
	private boolean problema_saude;
	private String descricao_problema_saude;
	private boolean deficiencia;
	private String descricao_deficiencia;

	// ---- Contato ----
	private String email;
	private String telefone;
	private String telefone_emergencia;

	// ---- Endereço ----
	private String cep;
	private String logradouro;		//rua, avenida, marginal etc
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String uf;

	// ---- Controle ----
	private String status_professor;		//ATIVO ou INATIVO
	private LocalDateTime criado_em;
	private LocalDateTime atualizado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome_social() {
		return nome_social;
	}

	public void setNome_social(String nome_social) {
		this.nome_social = nome_social;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public Date getData_nascimento() {
		return data_nascimento;
	}

	public void setData_nascimento(Date data_nascimento) {
		this.data_nascimento = data_nascimento;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getTitulo_eleitor() {
		return titulo_eleitor;
	}

	public void setTitulo_eleitor(String titulo_eleitor) {
		this.titulo_eleitor = titulo_eleitor;
	}

	public String getTitulacao() {
		return titulacao;
	}

	public void setTitulacao(String titulacao) {
		this.titulacao = titulacao;
	}

	public String getCurriculo_lattes() {
		return curriculo_lattes;
	}

	public void setCurriculo_lattes(String curriculo_lattes) {
		this.curriculo_lattes = curriculo_lattes;
	}

	public boolean isProblema_saude() {
		return problema_saude;
	}

	public void setProblema_saude(boolean problema_saude) {
		this.problema_saude = problema_saude;
	}

	public String getDescricao_problema_saude() {
		return descricao_problema_saude;
	}

	public void setDescricao_problema_saude(String descricao_problema_saude) {
		this.descricao_problema_saude = descricao_problema_saude;
	}

	public boolean isDeficiencia() {
		return deficiencia;
	}

	public void setDeficiencia(boolean deficiencia) {
		this.deficiencia = deficiencia;
	}

	public String getDescricao_deficiencia() {
		return descricao_deficiencia;
	}

	public void setDescricao_deficiencia(String descricao_deficiencia) {
		this.descricao_deficiencia = descricao_deficiencia;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getTelefone_emergencia() {
		return telefone_emergencia;
	}

	public void setTelefone_emergencia(String telefone_emergencia) {
		this.telefone_emergencia = telefone_emergencia;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getStatus_professor() {
		return status_professor;
	}

	public void setStatus_professor(String status_professor) {
		this.status_professor = status_professor;
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
