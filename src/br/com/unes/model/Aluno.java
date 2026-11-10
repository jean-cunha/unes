package br.com.unes.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Aluno {
	//Identificação
	private int id;
	private String ra;		//Registro do Aluno
	private String nome;
	private String cpf;
	private String email;
	private String nome_social;		//não obrigatorio
	private String genero;
	private Date data_nascimento;
	private String rg;
	private String titulo_eleitor;
	private String nome_resposavel;
	private int nota_enem;

	//Saúde e deficiência
	private boolean possui_problema_saude;
	private String descricao_problema_saude;
	private boolean possui_deficiencia;
	private String descricao_deficiencia;

	//Contato
	private String telefone;
	private String telefone_emergencia;

	//Endereço
	private String cep;
	private String logradouro;		//rua, avenida, marginal etc
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String uf;

	//Dados acadêmicos
	private int curso_id;
	private String turno;
	private LocalDateTime data_ingresso;
	private LocalDateTime atualizado_em;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRa() {
		return ra;
	}

	public void setRa(String ra) {
		this.ra = ra;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getTitulo_eleitor() {
		return titulo_eleitor;
	}

	public void setTitulo_eleitor(String titulo_eleitor) {
		this.titulo_eleitor = titulo_eleitor;
	}

	public String getNome_resposavel() {
		return nome_resposavel;
	}

	public void setNome_resposavel(String nome_resposavel) {
		this.nome_resposavel = nome_resposavel;
	}

	public int getNota_enem() {
		return nota_enem;
	}

	public void setNota_enem(int nota_enem) {
		this.nota_enem = nota_enem;
	}

	public boolean isPossui_problema_saude() {
		return possui_problema_saude;
	}

	public void setPossui_problema_saude(boolean possui_problema_saude) {
		this.possui_problema_saude = possui_problema_saude;
	}

	public String getDescricao_problema_saude() {
		return descricao_problema_saude;
	}

	public void setDescricao_problema_saude(String descricao_problema_saude) {
		this.descricao_problema_saude = descricao_problema_saude;
	}

	public boolean isPossui_deficiencia() {
		return possui_deficiencia;
	}

	public void setPossui_deficiencia(boolean possui_deficiencia) {
		this.possui_deficiencia = possui_deficiencia;
	}

	public String getDescricao_deficiencia() {
		return descricao_deficiencia;
	}

	public void setDescricao_deficiencia(String descricao_deficiencia) {
		this.descricao_deficiencia = descricao_deficiencia;
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

	public int getCurso_id() {
		return curso_id;
	}

	public void setCurso_id(int curso_id) {
		this.curso_id = curso_id;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public LocalDateTime getData_ingresso() {
		return data_ingresso;
	}

	public void setData_ingresso(LocalDateTime data_ingresso) {
		this.data_ingresso = data_ingresso;
	}

	public LocalDateTime getAtualizado_em() {
		return atualizado_em;
	}

	public void setAtualizado_em(LocalDateTime atualizado_em) {
		this.atualizado_em = atualizado_em;
	}
}
