package br.com.unes.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Conversões e validações usadas pelos controllers e views. */
public final class Formato {

	private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu")
			.withResolverStyle(java.time.format.ResolverStyle.STRICT);

	private Formato() {
	}

	public static boolean vazio(String s) {
		return s == null || s.isBlank();
	}

	/** Texto "dd/MM/aaaa" para java.sql.Date; vazio vira null. */
	public static Date paraData(String texto, String campo) {
		if (vazio(texto)) return null;
		try {
			return Date.valueOf(LocalDate.parse(texto.trim(), DATA));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(campo + " inválida (use dd/mm/aaaa).");
		}
	}

	public static String data(Date d) {
		return d == null ? "" : d.toLocalDate().format(DATA);
	}

	public static int paraInt(String texto, String campo) {
		try {
			return Integer.parseInt(texto.trim());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(campo + " deve ser um número inteiro.");
		}
	}

	public static BigDecimal paraDecimal(String texto, String campo) {
		try {
			return new BigDecimal(texto.trim().replace(',', '.'));
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(campo + " deve ser um número decimal.");
		}
	}

	public static String decimal(BigDecimal v) {
		return v == null ? "" : v.toPlainString();
	}

	public static String apenasDigitos(String s) {
		return s == null ? null : s.replaceAll("\\D", "");
	}

	public static boolean cpfValido(String cpf) {
		String c = apenasDigitos(cpf);
		if (c == null || c.length() != 11 || c.chars().distinct().count() == 1) return false;
		for (int t = 9; t < 11; t++) {
			int soma = 0;
			for (int i = 0; i < t; i++) soma += (c.charAt(i) - '0') * (t + 1 - i);
			int dv = (soma * 10) % 11 % 10;
			if (dv != c.charAt(t) - '0') return false;
		}
		return true;
	}

	public static boolean emailValido(String email) {
		return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	}

	public static void exigir(boolean condicao, String mensagem) {
		if (!condicao) throw new IllegalArgumentException(mensagem);
	}

	public static void exigirTexto(String valor, String campo) {
		exigir(!vazio(valor), campo + " é obrigatório.");
	}
}
