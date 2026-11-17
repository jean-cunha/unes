package br.com.unes.view;

import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.AlunoController;
import br.com.unes.model.Aluno;
import br.com.unes.util.Formato;

public class AlunoView extends CrudView<Aluno> {

	static final String[] UFS = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
			"PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" };
	static final String[] GENEROS = { "MASCULINO", "FEMININO", "OUTRO" };

	private final AlunoController controller = new AlunoController();
	private Map<Integer, String> cursos = Map.of();

	private final JTextField ra = txt(), nome = txt(), nomeSocial = txt(), nascimento = txt(), cpf = txt(),
			rg = txt(), titulo = txt(), responsavel = txt(), enem = txt(), email = txt(), telefone = txt(),
			telEmerg = txt(), cep = txt(), logradouro = txt(), numero = txt(), complemento = txt(),
			bairro = txt(), cidade = txt(), descSaude = txt(), descDef = txt();
	private final JComboBox<String> genero = comboTexto(true, GENEROS), uf = comboTexto(true, UFS),
			turno = comboTexto(true, "MATUTINO", "VESPERTINO", "NOTURNO", "INTEGRAL");
	private final JComboBox<Opcao> curso = comboOpcao();
	private final JCheckBox saude = new JCheckBox("Possui problema de saúde"),
			deficiencia = new JCheckBox("Possui deficiência");
	private final JTextField busca = new JTextField(16);
	private final JComboBox<String> campoBusca = comboTexto(false, "nome", "ra", "cpf", "turno");

	public AlunoView() {
		campo("RA*", ra);
		campo("Nome*", nome);
		campo("Nome social", nomeSocial);
		campo("Gênero", genero);
		campo("Nascimento* (dd/mm/aaaa)", nascimento);
		campo("CPF*", cpf);
		campo("RG", rg);
		campo("Título de eleitor", titulo);
		campo("Responsável", responsavel);
		campo("Nota ENEM", enem);
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
		campo("Curso*", curso);
		campo("Turno*", turno);
		campo("", saude);
		campo("Descrição saúde", descSaude);
		campo("", deficiencia);
		campo("Descrição deficiência", descDef);

		filtro.add(new javax.swing.JLabel("Buscar por"));
		filtro.add(campoBusca);
		filtro.add(busca);
		javax.swing.JButton ok = new javax.swing.JButton("Filtrar");
		ok.addActionListener(e -> aoFiltrar());
		filtro.add(ok);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "RA", "Nome", "CPF", "E-mail", "Curso", "Turno" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		List<Opcao> l = Opcoes.cursos();
		cursos = Opcoes.mapa(l);
		carregarCombo(curso, l, true);
	}

	@Override
	protected List<Aluno> carregar() throws Exception {
		String termo = busca.getText().trim();
		return termo.isEmpty() ? controller.listar() : controller.buscar((String) campoBusca.getSelectedItem(), termo);
	}

	@Override
	protected Object[] linha(Aluno a) {
		return new Object[] { a.getRa(), a.getNome(), a.getCpf(), a.getEmail(), cursos.get(a.getCurso_id()),
				a.getTurno() };
	}

	@Override
	protected void preencher(Aluno a) {
		if (a == null) {
			limpar(ra, nome, nomeSocial, nascimento, cpf, rg, titulo, responsavel, enem, email, telefone, telEmerg,
					cep, logradouro, numero, complemento, bairro, cidade, descSaude, descDef);
			selecionar(genero, null);
			selecionar(uf, null);
			selecionar(turno, null);
			selecionarId(curso, null);
			saude.setSelected(false);
			deficiencia.setSelected(false);
			return;
		}
		ra.setText(a.getRa());
		nome.setText(a.getNome());
		nomeSocial.setText(a.getNome_social());
		selecionar(genero, a.getGenero());
		nascimento.setText(Formato.data(a.getData_nascimento()));
		cpf.setText(a.getCpf());
		rg.setText(a.getRg());
		titulo.setText(a.getTitulo_eleitor());
		responsavel.setText(a.getNome_resposavel());
		enem.setText(String.valueOf(a.getNota_enem()));
		email.setText(a.getEmail());
		telefone.setText(a.getTelefone());
		telEmerg.setText(a.getTelefone_emergencia());
		cep.setText(a.getCep());
		logradouro.setText(a.getLogradouro());
		numero.setText(a.getNumero());
		complemento.setText(a.getComplemento());
		bairro.setText(a.getBairro());
		cidade.setText(a.getCidade());
		selecionar(uf, a.getUf());
		selecionarId(curso, a.getCurso_id());
		selecionar(turno, a.getTurno());
		saude.setSelected(a.isPossui_problema_saude());
		descSaude.setText(a.getDescricao_problema_saude());
		deficiencia.setSelected(a.isPossui_deficiencia());
		descDef.setText(a.getDescricao_deficiencia());
	}

	@Override
	protected Aluno ler() {
		Aluno a = atual != null ? atual : new Aluno();
		a.setRa(valor(ra));
		a.setNome(valor(nome));
		a.setNome_social(valor(nomeSocial));
		a.setGenero(selecionado(genero));
		a.setData_nascimento(Formato.paraData(nascimento.getText(), "Data de nascimento"));
		a.setCpf(valor(cpf));
		a.setRg(valor(rg));
		a.setTitulo_eleitor(valor(titulo));
		a.setNome_resposavel(valor(responsavel));
		a.setNota_enem(Formato.vazio(enem.getText()) ? 0 : Formato.paraInt(enem.getText(), "Nota ENEM"));
		a.setEmail(valor(email));
		a.setTelefone(valor(telefone));
		a.setTelefone_emergencia(valor(telEmerg));
		a.setCep(valor(cep));
		a.setLogradouro(valor(logradouro));
		a.setNumero(valor(numero));
		a.setComplemento(valor(complemento));
		a.setBairro(valor(bairro));
		a.setCidade(valor(cidade));
		a.setUf(selecionado(uf));
		a.setCurso_id(idObrigatorio(curso));
		a.setTurno(selecionado(turno));
		a.setPossui_problema_saude(saude.isSelected());
		a.setDescricao_problema_saude(valor(descSaude));
		a.setPossui_deficiencia(deficiencia.isSelected());
		a.setDescricao_deficiencia(valor(descDef));
		return a;
	}

	@Override protected void gravar(Aluno a) throws Exception { controller.salvar(a); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Aluno a) throws Exception { controller.excluir(a); }
}
