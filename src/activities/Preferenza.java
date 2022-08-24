package activities;

import Main.Principale;

public class Preferenza {
	
	private Coppia coppia;
	private Opzione scelta;
	
	public Preferenza(Coppia c, Opzione opz) {
		coppia=c;
		scelta=opz;
	}
	
	public void setNewCoppia(Coppia newC) {
		if(Principale.getLoggedUser().isAdmin()) { //controlla se la modifica sia fatta dall'admin
			coppia= newC;
		}
	}
	public void setNewScelta(Opzione newOpz) {
			scelta= newOpz;
	}
	
	public Coppia getCoppia() {
		return coppia;
	}
	
	public Opzione getScelta() {
		return scelta;
	}
	//restituisce l'id completo della preferenza
	public String getFullId() {
		String id=coppia.getActivity().getId();
		id=id+scelta.getLetter();
		return id;
	}
	public String getIDPreferenza() {
		String idPref=coppia.getActivity().getId(); //prendeId attività
		idPref= idPref+scelta.getLetter(); //unisce lettera preferenza
		return idPref;
	}
	
	public String intoJson() {
		return "\t\"opzione\":\""+scelta.getLetter()+"\"";
	}
}
