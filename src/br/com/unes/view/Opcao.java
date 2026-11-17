package br.com.unes.view;

/** Item de combo com id (chave estrangeira) e rótulo exibido. */
public record Opcao(Integer id, String rotulo) {
	@Override
	public String toString() {
		return rotulo;
	}
}
