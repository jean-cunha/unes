package br.com.unes.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Hash de senha com PBKDF2. Formato armazenado: iteracoes:salt:hash (Base64). */
public final class SenhaUtil {

	private static final int ITERACOES = 120_000;

	private SenhaUtil() {
	}

	public static String gerarHash(String senha) {
		byte[] salt = new byte[16];
		new SecureRandom().nextBytes(salt);
		return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt) + ":"
				+ Base64.getEncoder().encodeToString(derivar(senha, salt, ITERACOES));
	}

	public static boolean conferir(String senha, String armazenado) {
		try {
			String[] p = armazenado.split(":");
			int it = Integer.parseInt(p[0]);
			byte[] salt = Base64.getDecoder().decode(p[1]);
			byte[] esperado = Base64.getDecoder().decode(p[2]);
			return MessageDigest.isEqual(esperado, derivar(senha, salt, it));
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static byte[] derivar(String senha, byte[] salt, int iteracoes) {
		try {
			PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, 256);
			return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Algoritmo de hash indisponível", e);
		}
	}
}
