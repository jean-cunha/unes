package br.com.unes.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

/** Utilitários compartilhados pelos DAOs (colunas anuláveis e timestamps). */
final class DaoUtil {

	private DaoUtil() {
	}

	static LocalDateTime lerData(ResultSet rs, String coluna) throws SQLException {
		Timestamp t = rs.getTimestamp(coluna);
		return t == null ? null : t.toLocalDateTime();
	}

	static Integer lerInteger(ResultSet rs, String coluna) throws SQLException {
		int v = rs.getInt(coluna);
		return rs.wasNull() ? null : v;
	}

	static Long lerLong(ResultSet rs, String coluna) throws SQLException {
		long v = rs.getLong(coluna);
		return rs.wasNull() ? null : v;
	}

	static void gravarInteger(PreparedStatement ps, int indice, Integer valor) throws SQLException {
		if (valor == null) ps.setNull(indice, Types.INTEGER);
		else ps.setInt(indice, valor);
	}

	static void gravarLong(PreparedStatement ps, int indice, Long valor) throws SQLException {
		if (valor == null) ps.setNull(indice, Types.BIGINT);
		else ps.setLong(indice, valor);
	}
}
