package br.com.unes.model;

public class DisciplinaPrerequisito {
	private int disciplina_id;
	private int prerequisito_id;		//disciplina que precisa ter sido cursada antes

	public int getDisciplina_id() {
		return disciplina_id;
	}

	public void setDisciplina_id(int disciplina_id) {
		this.disciplina_id = disciplina_id;
	}

	public int getPrerequisito_id() {
		return prerequisito_id;
	}

	public void setPrerequisito_id(int prerequisito_id) {
		this.prerequisito_id = prerequisito_id;
	}
}
