package br.com.unes.view;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.unes.controller.AlunoController;
import br.com.unes.controller.CursoController;
import br.com.unes.controller.DisciplinaController;
import br.com.unes.controller.MatriculaController;
import br.com.unes.controller.ProfessorController;
import br.com.unes.controller.TurmaController;
import br.com.unes.model.Aluno;
import br.com.unes.model.Curso;
import br.com.unes.model.Disciplina;
import br.com.unes.model.Matricula;
import br.com.unes.model.Professor;
import br.com.unes.model.Turma;

/** Monta as listas de opções (combos) a partir dos controllers. */
final class Opcoes {

	private Opcoes() {
	}

	static List<Opcao> cursos() throws SQLException {
		List<Opcao> l = new ArrayList<>();
		for (Curso c : new CursoController().listar())
			l.add(new Opcao(c.getId(), c.getCodigo() + " - " + c.getNome() + inativo(c.getStatus_curso())));
		return l;
	}

	static List<Opcao> professores() throws SQLException {
		List<Opcao> l = new ArrayList<>();
		for (Professor p : new ProfessorController().listar())
			l.add(new Opcao(p.getId(), p.getNome() + inativo(p.getStatus_professor())));
		return l;
	}

	static List<Opcao> disciplinas() throws SQLException {
		List<Opcao> l = new ArrayList<>();
		for (Disciplina d : new DisciplinaController().listar())
			l.add(new Opcao(d.getId(), d.getCodigo() + " - " + d.getNome() + inativo(d.getStatus_disciplina())));
		return l;
	}

	static List<Opcao> alunos() throws SQLException {
		List<Opcao> l = new ArrayList<>();
		for (Aluno a : new AlunoController().listar()) l.add(new Opcao(a.getId(), a.getRa() + " - " + a.getNome()));
		return l;
	}

	static List<Opcao> turmas() throws SQLException {
		List<Opcao> l = new ArrayList<>();
		for (Turma t : new TurmaController().listar())
			l.add(new Opcao(t.getId(), t.getCodigo() + " (" + t.getPeriodo_letivo() + ")"));
		return l;
	}

	static List<Opcao> matriculas() throws SQLException {
		Map<Integer, String> alunos = mapa(alunos());
		Map<Integer, String> turmas = mapa(turmas());
		List<Opcao> l = new ArrayList<>();
		for (Matricula m : new MatriculaController().listar())
			l.add(new Opcao(m.getId(), alunos.get(m.getAluno_id()) + " | " + turmas.get(m.getTurma_id())));
		return l;
	}

	static Map<Integer, String> mapa(List<Opcao> opcoes) {
		Map<Integer, String> m = new HashMap<>();
		for (Opcao o : opcoes) m.put(o.id(), o.rotulo());
		return m;
	}

	private static String inativo(String status) {
		return "INATIVO".equals(status) ? " (inativo)" : "";
	}
}
