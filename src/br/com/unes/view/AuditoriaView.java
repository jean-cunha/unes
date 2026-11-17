package br.com.unes.view;

import java.util.List;

import br.com.unes.controller.AuditoriaController;
import br.com.unes.model.Auditoria;

/** Consulta (somente leitura) da trilha de auditoria. */
public class AuditoriaView extends CrudView<Auditoria> {

	private static final int LIMITE = 500;
	private final AuditoriaController controller = new AuditoriaController();

	public AuditoriaView() {
		montar();
	}

	@Override protected boolean somenteLeitura() { return true; }
	@Override protected String[] colunas() { return new String[] { "Data/hora", "Usuário", "Ação", "Entidade", "Registro", "Detalhes" }; }
	@Override protected List<Auditoria> carregar() throws Exception { return controller.listarRecentes(LIMITE); }

	@Override
	protected Object[] linha(Auditoria a) {
		return new Object[] { a.getData_hora(), a.getUsuario_id(), a.getAcao(), a.getEntidade(), a.getRegistro_id(),
				a.getDetalhes() };
	}

	@Override protected void preencher(Auditoria a) { }
	@Override protected Auditoria ler() { throw new UnsupportedOperationException(); }
	@Override protected void gravar(Auditoria a) { throw new UnsupportedOperationException(); }
}
