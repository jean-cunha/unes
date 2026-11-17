package br.com.unes.view;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.ProfessorController;
import br.com.unes.model.Professor;
import br.com.unes.util.Formato;

public class ProfessorView extends CrudView<Professor> {

	private final ProfessorController controller = new ProfessorController();
	private final JTextField nome = txt(), nomeSocial = txt(), nascimento = txt(), cpf = txt(), titulo = txt(),
			lattes = txt(), email = txt(), telefone = txt(), telEmerg = txt(), cep = txt(), logradouro = txt(),
			numero = txt(), complemento = txt(), bairro = txt(), cidade = txt(), descSaude = txt(), descDef = txt();
	private final JComboBox<String> genero = comboTexto(true, AlunoView.GENEROS),
			uf = comboTexto(true, AlunoView.UFS),
			titulacao = comboTexto(true, "GRADUADO", "ESPECIALISTA", "MESTRE", "DOUTOR"),
			status = comboTexto(false, "ATIVO", "INATIVO");
	private final JCheckBox saude = new JCheckBox("Possui problema de saúde"),
			deficiencia = new JCheckBox("Possui deficiência");

	public ProfessorView() {
		campo("Nome*", nome);
		campo("Nome social", nomeSocial);
		campo("Gênero", genero);
		campo("Nascimento* (dd/mm/aaaa)", nascimento);
		campo("CPF*", cpf);
		campo("Título de eleitor", titulo);
		campo("Titulação*", titulacao);
		campo("Currículo Lattes", lattes);
		campo("E-mail*", email);
		campo("Telefone", telefone);
		campo("Tel. emergência", telEmerg);
		campo("CEP", cep);
		campo("Logradouro", logradouro);
		campo("Número", numero);
		campo("Complemento", complemento);
		campo("Bairro", bairro);
		campo("Cidade", cidade);
		campo("UF", uf);
		campo("", saude);
		campo("Descrição saúde", descSaude);
		campo("", deficiencia);
		campo("Descrição deficiência", descDef);
		campo("Status*", status);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Nome", "CPF", "Titulação", "E-mail", "Status" }; }
	@Override protected List<Professor> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Professor p) {
		return new Object[] { p.getNome(), p.getCpf(), p.getTitulacao(), p.getEmail(), p.getStatus_professor() };
	}

	@Override
	protected void preencher(Professor p) {
		if (p == null) {
			limpar(nome, nomeSocial, nascimento, cpf, titulo, lattes, email, telefone, telEmerg, cep, logradouro,
					numero, complemento, bairro, cidade, descSaude, descDef);
			selecionar(genero, null);
			selecionar(uf, null);
			selecionar(titulacao, null);
			selecionar(status, "ATIVO");
			saude.setSelected(false);
			deficiencia.setSelected(false);
			return;
		}
		nome.setText(p.getNome());
		nomeSocial.setText(p.getNome_social());
		selecionar(genero, p.getGenero());
		nascimento.setText(Formato.data(p.getData_nascimento()));
		cpf.setText(p.getCpf());
		titulo.setText(p.getTitulo_eleitor());
		selecionar(titulacao, p.getTitulacao());
		lattes.setText(p.getCurriculo_lattes());
		email.setText(p.getEmail());
		telefone.setText(p.getTelefone());
		telEmerg.setText(p.getTelefone_emergencia());
		cep.setText(p.getCep());
		logradouro.setText(p.getLogradouro());
		numero.setText(p.getNumero());
		complemento.setText(p.getComplemento());
		bairro.setText(p.getBairro());
		cidade.setText(p.getCidade());
		selecionar(uf, p.getUf());
		saude.setSelected(p.isProblema_saude());
		descSaude.setText(p.getDescricao_problema_saude());
		deficiencia.setSelected(p.isDeficiencia());
		descDef.setText(p.getDescricao_deficiencia());
		selecionar(status, p.getStatus_professor());
	}

	@Override
	protected Professor ler() {
		Professor p = atual != null ? atual : new Professor();
		p.setNome(valor(nome));
		p.setNome_social(valor(nomeSocial));
		p.setGenero(selecionado(genero));
		p.setData_nascimento(Formato.paraData(nascimento.getText(), "Data de nascimento"));
		p.setCpf(valor(cpf));
		p.setTitulo_eleitor(valor(titulo));
		p.setTitulacao(selecionado(titulacao));
		p.setCurriculo_lattes(valor(lattes));
		p.setEmail(valor(email));
		p.setTelefone(valor(telefone));
		p.setTelefone_emergencia(valor(telEmerg));
		p.setCep(valor(cep));
		p.setLogradouro(valor(logradouro));
		p.setNumero(valor(numero));
		p.setComplemento(valor(complemento));
		p.setBairro(valor(bairro));
		p.setCidade(valor(cidade));
		p.setUf(selecionado(uf));
		p.setProblema_saude(saude.isSelected());
		p.setDescricao_problema_saude(valor(descSaude));
		p.setDeficiencia(deficiencia.isSelected());
		p.setDescricao_deficiencia(valor(descDef));
		p.setStatus_professor(selecionado(status));
		return p;
	}

	@Override protected void gravar(Professor p) throws Exception { controller.salvar(p); }
	@Override protected String rotuloRemover() { return "Inativar"; }
	@Override protected void remover(Professor p) throws Exception { controller.inativar(p); }
}
