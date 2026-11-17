package br.com.unes.view;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import br.com.unes.controller.DocumentoController;
import br.com.unes.model.Documento;
import br.com.unes.util.Formato;

/** Documentos de um aluno ou de um professor (selecionados no filtro). */
public class DocumentoView extends CrudView<Documento> {

	private final DocumentoController controller = new DocumentoController();
	private final JComboBox<String> dono = comboTexto(false, "Aluno", "Professor");
	private final JComboBox<Opcao> pessoa = comboOpcao();
	private final JComboBox<String> tipo = comboTexto(true, DocumentoController.TIPOS.stream().sorted().toArray(String[]::new));
	private final JTextField caminho = new JTextField(30);

	public DocumentoView() {
		filtro.add(new JLabel("Dono:"));
		filtro.add(dono);
		filtro.add(pessoa);
		dono.addActionListener(e -> {
			if (ajustando) return;
			carregarPessoas();
			aoFiltrar();
		});
		pessoa.addActionListener(e -> aoFiltrar());

		campo("Tipo*", tipo);
		JPanel p = new JPanel(new BorderLayout(4, 0));
		p.add(caminho, BorderLayout.CENTER);
		JButton escolher = new JButton("Escolher...");
		escolher.addActionListener(e -> {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				caminho.setText(fc.getSelectedFile().getAbsolutePath());
		});
		p.add(escolher, BorderLayout.EAST);
		largo("Arquivo*", p);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Tipo", "Arquivo", "Enviado em" }; }

	@Override
	protected void recarregarOpcoes() {
		carregarPessoas();
	}

	private void carregarPessoas() {
		boolean antes = ajustando;
		ajustando = true;
		try {
			boolean aluno = "Aluno".equals(dono.getSelectedItem());
			carregarCombo(pessoa, aluno ? Opcoes.alunos() : Opcoes.professores(), true);
		} catch (Exception e) {
			erro(e);
		} finally {
			ajustando = antes;
		}
	}

	@Override
	protected List<Documento> carregar() throws Exception {
		Integer id = idDe(pessoa);
		if (id == null) return Collections.emptyList();
		return "Aluno".equals(dono.getSelectedItem()) ? controller.listarPorAluno(id) : controller.listarPorProfessor(id);
	}

	@Override
	protected Object[] linha(Documento d) {
		return new Object[] { d.getTipo(), d.getCaminho_arquivo(), d.getData_envio() };
	}

	@Override
	protected void preencher(Documento d) {
		if (d == null) {
			selecionar(tipo, null);
			caminho.setText("");
			return;
		}
		selecionar(tipo, d.getTipo());
		caminho.setText(d.getCaminho_arquivo());
	}

	@Override
	protected Documento ler() {
		Integer id = idDe(pessoa);
		Formato.exigir(id != null, "Selecione o aluno/professor no filtro.");
		Documento d = atual != null ? atual : new Documento();
		d.setTipo(selecionado(tipo));
		d.setCaminho_arquivo(valor(caminho));
		boolean aluno = "Aluno".equals(dono.getSelectedItem());
		d.setAluno_id(aluno ? id : null);
		d.setProfessor_id(aluno ? null : id);
		return d;
	}

	@Override protected void gravar(Documento d) throws Exception { controller.salvar(d); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Documento d) throws Exception { controller.excluir(d); }
}
