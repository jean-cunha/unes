package br.com.unes.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Tela padrão de cadastro: tabela no topo, formulário no meio e botões embaixo.
 * As subclasses declaram os campos (campo/largo) no construtor e terminam com montar().
 */
public abstract class CrudView<T> extends JPanel {

	private final DefaultTableModel modelo = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable tabela = new JTable(modelo);
	private final List<T> itens = new ArrayList<>();
	private final JPanel form = new JPanel(new GridBagLayout());
	protected final JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
	protected T atual;
	protected boolean ajustando;
	private int coluna;
	private int linhaForm;
	private JButton btnRemover;

	//---------------- contrato das subclasses ----------------

	protected abstract String[] colunas();

	protected abstract List<T> carregar() throws Exception;

	protected abstract Object[] linha(T t);

	/** Preenche o formulário; t == null limpa para novo cadastro. */
	protected abstract void preencher(T t);

	/** Lê o formulário (reaproveita {@code atual} na edição). Lança IllegalArgumentException se inválido. */
	protected abstract T ler();

	protected abstract void gravar(T t) throws Exception;

	protected void recarregarOpcoes() throws Exception {
	}

	/** Texto do botão de remoção/inativação; null = sem botão. */
	protected String rotuloRemover() {
		return null;
	}

	protected void remover(T t) throws Exception {
		throw new UnsupportedOperationException();
	}

	protected boolean somenteLeitura() {
		return false;
	}

	//---------------- construção ----------------

	protected final void campo(String rotulo, JComponent c) {
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(3, 6, 3, 6);
		g.anchor = GridBagConstraints.WEST;
		g.gridy = linhaForm;
		g.gridx = coluna * 2;
		form.add(new JLabel(rotulo), g);
		g.gridx = coluna * 2 + 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		form.add(c, g);
		if (++coluna == 2) {
			coluna = 0;
			linhaForm++;
		}
	}

	/** Campo que ocupa a linha inteira. */
	protected final void largo(String rotulo, JComponent c) {
		if (coluna != 0) {
			coluna = 0;
			linhaForm++;
		}
		GridBagConstraints g = new GridBagConstraints();
		g.insets = new Insets(3, 6, 3, 6);
		g.anchor = GridBagConstraints.WEST;
		g.gridy = linhaForm;
		g.gridx = 0;
		if (rotulo != null) form.add(new JLabel(rotulo), g);
		g.gridx = 1;
		g.gridwidth = 3;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		form.add(c, g);
		linhaForm++;
	}

	protected final void montar() {
		setLayout(new BorderLayout(6, 6));
		modelo.setColumnIdentifiers(colunas());
		tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabela.setAutoCreateRowSorter(false);
		tabela.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) return;
			int i = tabela.getSelectedRow();
			if (i >= 0 && i < itens.size()) {
				atual = itens.get(i);
				preencher(atual);
			}
		});

		JPanel topo = new JPanel(new BorderLayout());
		if (filtro.getComponentCount() > 0) topo.add(filtro, BorderLayout.NORTH);
		JScrollPane sp = new JScrollPane(tabela);
		sp.setPreferredSize(new Dimension(800, 220));
		topo.add(sp, BorderLayout.CENTER);

		JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton atualizar = new JButton("Atualizar");
		atualizar.addActionListener(e -> atualizar());
		botoes.add(atualizar);
		if (!somenteLeitura()) {
			JButton novo = new JButton("Novo");
			novo.addActionListener(e -> novo());
			JButton salvar = new JButton("Salvar");
			salvar.addActionListener(e -> salvar());
			botoes.add(novo);
			botoes.add(salvar);
			if (rotuloRemover() != null) {
				btnRemover = new JButton(rotuloRemover());
				btnRemover.addActionListener(e -> remover());
				botoes.add(btnRemover);
			}
		}

		if (somenteLeitura()) {
			add(topo, BorderLayout.CENTER);
		} else {
			// âncora para empurrar os campos para o topo do painel
			GridBagConstraints g = new GridBagConstraints();
			g.gridy = linhaForm + 1;
			g.weighty = 1;
			form.add(new JPanel(), g);
			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topo, new JScrollPane(form));
			split.setResizeWeight(0.4);
			add(split, BorderLayout.CENTER);
		}
		add(botoes, BorderLayout.SOUTH);
	}

	//---------------- ações ----------------

	/** Recarrega opções dos combos e a tabela. */
	public final void atualizar() {
		try {
			ajustando = true;
			try {
				recarregarOpcoes();
			} finally {
				ajustando = false;
			}
			recarregarItens();
			novo();
		} catch (Exception e) {
			erro(e);
		}
	}

	/** Chamado pelos combos de filtro quando o usuário muda a seleção. */
	protected final void aoFiltrar() {
		if (ajustando) return;
		try {
			recarregarItens();
			novo();
		} catch (Exception e) {
			erro(e);
		}
	}

	private void recarregarItens() throws Exception {
		List<T> lista = carregar();
		itens.clear();
		itens.addAll(lista);
		modelo.setRowCount(0);
		for (T t : itens) modelo.addRow(linha(t));
	}

	protected final void novo() {
		tabela.clearSelection();
		atual = null;
		preencher(null);
	}

	private void salvar() {
		try {
			T t = ler();
			gravar(t);
			recarregarItens();
			novo();
			JOptionPane.showMessageDialog(this, "Registro salvo com sucesso.");
		} catch (Exception e) {
			erro(e);
			try {
				recarregarItens();	// descarta alterações em memória do item que falhou
			} catch (Exception ignorada) {
			}
		}
	}

	private void remover() {
		if (atual == null) {
			JOptionPane.showMessageDialog(this, "Selecione um registro na tabela.");
			return;
		}
		int r = JOptionPane.showConfirmDialog(this, "Confirma: " + rotuloRemover() + "?", "Confirmação",
				JOptionPane.YES_NO_OPTION);
		if (r != JOptionPane.YES_OPTION) return;
		try {
			remover(atual);
			recarregarItens();
			novo();
		} catch (Exception e) {
			erro(e);
		}
	}

	protected final void erro(Exception e) {
		if (e instanceof IllegalArgumentException) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
		} else {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	//---------------- utilitários de campos ----------------

	protected static JTextField txt() {
		return new JTextField(18);
	}

	/** Texto aparado; vazio vira null. */
	protected static String valor(JTextField f) {
		String s = f.getText().trim();
		return s.isEmpty() ? null : s;
	}

	protected static void limpar(JTextField... campos) {
		for (JTextField f : campos) f.setText("");
	}

	protected static JComboBox<String> comboTexto(boolean comVazio, String... itens) {
		JComboBox<String> c = new JComboBox<>();
		if (comVazio) c.addItem("");
		for (String s : itens) c.addItem(s);
		return c;
	}

	protected static String selecionado(JComboBox<String> c) {
		String s = (String) c.getSelectedItem();
		return s == null || s.isEmpty() ? null : s;
	}

	protected static void selecionar(JComboBox<String> c, String valor) {
		c.setSelectedItem(valor == null ? "" : valor);
		if (c.getSelectedItem() == null && c.getItemCount() > 0) c.setSelectedIndex(0);
	}

	protected static JComboBox<Opcao> comboOpcao() {
		return new JComboBox<>();
	}

	/** Repovoa o combo preservando a seleção atual (por id). */
	protected static void carregarCombo(JComboBox<Opcao> combo, List<Opcao> opcoes, boolean comVazio) {
		Integer antes = idDe(combo);
		combo.removeAllItems();
		if (comVazio) combo.addItem(new Opcao(null, ""));
		for (Opcao o : opcoes) combo.addItem(o);
		selecionarId(combo, antes);
	}

	protected static Integer idDe(JComboBox<Opcao> combo) {
		Opcao o = (Opcao) combo.getSelectedItem();
		return o == null ? null : o.id();
	}

	protected static int idObrigatorio(JComboBox<Opcao> combo) {
		Integer id = idDe(combo);
		return id == null ? 0 : id;
	}

	protected static void selecionarId(JComboBox<Opcao> combo, Integer id) {
		for (int i = 0; i < combo.getItemCount(); i++) {
			Integer atual = combo.getItemAt(i).id();
			if (java.util.Objects.equals(atual, id)) {
				combo.setSelectedIndex(i);
				return;
			}
		}
		if (combo.getItemCount() > 0) combo.setSelectedIndex(0);
	}

	protected static void marcar(JCheckBox c, boolean v) {
		c.setSelected(v);
	}
}
