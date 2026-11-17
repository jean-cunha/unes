package br.com.unes.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import br.com.unes.controller.AvaliacaoController;
import br.com.unes.controller.MatriculaController;
import br.com.unes.controller.NotaController;
import br.com.unes.model.Avaliacao;
import br.com.unes.model.Matricula;
import br.com.unes.model.Nota;
import br.com.unes.util.Formato;

/** Lançamento de notas de uma matrícula (selecionada no filtro). */
public class NotaView extends CrudView<Nota> {

	private final NotaController controller = new NotaController();
	private final AvaliacaoController avaliacaoController = new AvaliacaoController();
	private final MatriculaController matriculaController = new MatriculaController();
	private Map<Integer, String> avaliacoes = Map.of();
	private final JComboBox<Opcao> matricula = comboOpcao(), avaliacao = comboOpcao();
	private final JTextField valor = txt();

	public NotaView() {
		filtro.add(new JLabel("Matrícula:"));
		filtro.add(matricula);
		matricula.addActionListener(e -> {
			if (ajustando) return;
			carregarAvaliacoes();
			aoFiltrar();
		});
		campo("Avaliação*", avaliacao);
		campo("Nota*", valor);
		montar();
	}

	@Override protected String[] colunas() { return new String[] { "Avaliação", "Nota", "Lançada em" }; }

	@Override
	protected void recarregarOpcoes() throws Exception {
		carregarCombo(matricula, Opcoes.matriculas(), true);
		carregarAvaliacoes();
	}

	private void carregarAvaliacoes() {
		boolean antes = ajustando;
		ajustando = true;
		try {
			List<Opcao> l = new java.util.ArrayList<>();
			Integer mid = idDe(matricula);
			if (mid != null) {
				Matricula m = matriculaController.buscarPorId(mid).orElse(null);
				if (m != null)
					for (Avaliacao a : avaliacaoController.listarPorTurma(m.getTurma_id()))
						l.add(new Opcao(a.getId(), a.getOrdem() + " - " + a.getDescricao() + " (máx. "
								+ Formato.decimal(a.getValor_maximo()) + ")"));
			}
			avaliacoes = Opcoes.mapa(l);
			carregarCombo(avaliacao, l, true);
		} catch (Exception e) {
			erro(e);
		} finally {
			ajustando = antes;
		}
	}

	@Override
	protected List<Nota> carregar() throws Exception {
		Integer mid = idDe(matricula);
		return mid == null ? Collections.emptyList() : controller.listarPorMatricula(mid);
	}

	@Override
	protected Object[] linha(Nota n) {
		return new Object[] { avaliacoes.get(n.getAvaliacao_id()), Formato.decimal(n.getValor()),
				n.getData_lancamento() };
	}

	@Override
	protected void preencher(Nota n) {
		if (n == null) {
			selecionarId(avaliacao, null);
			valor.setText("");
			return;
		}
		selecionarId(avaliacao, n.getAvaliacao_id());
		valor.setText(Formato.decimal(n.getValor()));
	}

	@Override
	protected Nota ler() {
		Formato.exigir(idDe(matricula) != null, "Selecione a matrícula no filtro.");
		Nota n = atual != null ? atual : new Nota();
		n.setMatricula_id(idObrigatorio(matricula));
		n.setAvaliacao_id(idObrigatorio(avaliacao));
		n.setValor(Formato.paraDecimal(valor.getText(), "Nota"));
		return n;
	}

	@Override protected void gravar(Nota n) throws Exception { controller.salvar(n); }
	@Override protected String rotuloRemover() { return "Excluir"; }
	@Override protected void remover(Nota n) throws Exception { controller.excluir(n); }
}
