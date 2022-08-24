package activities;

import login.Utente;

//classe che associa un utente ad un'attività
public class Coppia {
	
	private Utente utente;
	private Activity attivita;
	
	public Coppia(Utente u, Activity a) {
		utente=u;
		attivita=a;
	}
	
	public Utente getUtente() {
		return utente;
	}
	public Activity getActivity() {
		return attivita;
	}
	
	public String intoJson() {
		return "{\"coppia\":[ \n\t{\"userID\":\""+ utente.getID()+"\","
				+ " \n \"activityID\":\""+attivita.getId()+"\" }\n],";
	}
}
