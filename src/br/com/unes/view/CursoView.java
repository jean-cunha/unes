package br.com.unes.view;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import br.com.unes.controller.CursoController;
import br.com.unes.model.Curso;
import br.com.unes.util.Formato;

public class CursoView extends CrudView<Curso> {

	private final CursoController controller = new CursoController();
	private final JTextField codigo = txt(), nome = txt(), carga = txt(), semestres = txt();
	private final JComboBox<String> modalidade = comboTexto(false, "PRESENCIAL", "EAD", "HIBRIDO");
	private final JComboBox<String> status = comboTexto(false, "ATIVO", "INATIVO");

	public CursoView() {
		campo("Código*", codigo);
		campo("Nome*", nome);
		campo("Carga horária (h)*", carga);
		campo("Qtd. semestres*", semestres);
		campo("Modalidade*", modalidade);
		campo("Status*", status);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Código", "Nome", "Carga", "Modalidade", "Semestres", "Status" }; }
	@Override protected List<Curso> carregar() throws Exception { return controller.listar(); }

	@Override
	protected Object[] linha(Curso c) {
		return new Object[] { c.getCodigo(), c.getNome(), c.getCarga_horaria(), c.getModalidade(),
				c.getQuantidade_semestres(), c.getStatus_curso() };
	}

	@Override
	protected void preencher(Curso c) {
		if (c == null) {
			limpar(codigo, nome, carga, semestres);
			selecionar(modalidade, "PRESENCIAL");
			selecionar(status, "ATIVO");
			return;
		}
		codigo.setText(c.getCodigo());
		nome.setText(c.getNome());
		carga.setText(String.valueOf(c.getCarga_horaria()));
		semestres.setText(String.valueOf(c.getQuantidade_semestres()));
		selecionar(modalidade, c.getModalidade());
		selecionar(status, c.getStatus_curso());
	}

	@Override
	protected Curso ler() {
		Curso c = atual != null ? atual : new Curso();
		c.setCodigo(valor(codigo));
		c.setNome(valor(nome));
		c.setCarga_horaria(Formato.paraInt(carga.getText(), "Carga horária"));
		c.setQuantidade_semestres(Formato.paraInt(semestres.getText(), "Quantidade de semestres"));
		c.setModalidade(selecionado(modalidade));
		c.setStatus_curso(selecionado(status));
		return c;
	}

	@Override protected void gravar(Curso c) throws Exception { controller.salvar(c); }
	@Override protected String rotuloRemover() { return "Inativar"; }
	@Override protected void remover(Curso c) throws Exception { controller.inativar(c); }
}
