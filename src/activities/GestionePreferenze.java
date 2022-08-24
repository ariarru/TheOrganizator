package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Main.Principale;
import home.AdminPage;
import home.GestioneUtentiAdmin;
import home.UserPage;
import javafx.scene.control.Alert;
import login.Utente;

public class GestionePreferenze {

	private static ArrayList<Preferenza> preferences= new ArrayList<Preferenza>();
	private static Path path;
	private static String pathStr;
	public GestionePreferenze() {
		readPreferences();
	}
	
	//legge il json
	public static void readPreferences() {
		try {
			preferences.clear();
			path= Paths.get("Record.json");
			pathStr=""+path.toAbsolutePath();
			JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
			JsonArray preferencesJ= (JsonArray) jsonObj.getAsJsonArray("preferences");
			for(int p=0; p<preferencesJ.size(); p++) {
				JsonObject dbPreference= (JsonObject) preferencesJ.get(p); //prende l'espressione della preferenza
				JsonArray dbCoupleAsArr= (JsonArray) dbPreference.get("coppia").getAsJsonArray(); //prende il valore della coppia
				JsonObject dbCouple= dbCoupleAsArr.get(0).getAsJsonObject();
				Activity dbAct=getActivityDaID(dbCouple.get("activityID").getAsString());
				Coppia c= new Coppia(getUtenteDaID(dbCouple.get("userID").getAsString()), dbAct);
				String dbOpt= dbPreference.get("opzione").getAsString();
				Opzione opt= dbAct.getOption(dbOpt);
				Preferenza dbPref= new Preferenza(c, opt);
				preferences.add(dbPref);
			}
		} catch(FileNotFoundException e) {
			System.out.print(e+ "in reading preferences from admin page");
		}
	}
	
	//legge le preferenze associate all'utente loggato
	public static ArrayList<Preferenza> getUserChoice(){
		ArrayList<Preferenza> preferenzeEspresse= new ArrayList<Preferenza>();
		for(Preferenza pr: preferences) {
			if(pr.getCoppia().getUtente().getID().equals(Principale.getLoggedUser().getID())) {
				preferenzeEspresse.add(pr);
			}
		}
		return preferenzeEspresse;
	}
	
	//legge le preferenze espresse da uno specifico utente
	public static ArrayList<Preferenza> getUserChoice(Utente user){
		ArrayList<Preferenza> preferenzeEspresse= new ArrayList<Preferenza>();
		for(Preferenza pr: preferences) {
			if(pr.getCoppia().getUtente().getID().equals(user.getID())) {
				preferenzeEspresse.add(pr);
			}
		}
		return preferenzeEspresse;
	}
	//associa id a utente
	private static Utente getUtenteDaID(String id) {
		ArrayList<Utente> list=GestioneUtentiAdmin.getAllUtenti();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getID().equals(id)) {
				return list.get(i);
			}
		}
		return null;
		
	}
	//associa id ad attività
	private static Activity getActivityDaID(String id) {
		ArrayList<Activity> list= GestioneAttivitaAdmin.getAllActivities();
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getId().equals(id)) {
				return list.get(i);
			}
		}
		return null;
	}
	
	//scrive sul json la coppia utente-attività e la preferenza
	public static void subscribe(Activity a, Opzione o) {
		if(Principale.isLogged()) {
			readPreferences();
			try {
				RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); 
				long posizioneCursore= rAF.length();
				long stop;
				while(rAF.length()>0) { 
					posizioneCursore--;
					rAF.seek(posizioneCursore);
					if(rAF.readByte() == ']') {
						stop=posizioneCursore;
						while(posizioneCursore>1) { //serve per controllare se ci siano altre preferenze, in caso aggiunge la virgola
							posizioneCursore--;
							rAF.seek(posizioneCursore);
							if(rAF.readByte() == '}') {
								rAF.seek(posizioneCursore);
								rAF.writeBytes("},\n");
								break;
							}
						}
						rAF.seek(stop); //faccio in modo che il cursore sia dopo la virgola e \n
						break;
					}
				}
				
				Coppia c= new Coppia(Principale.getLoggedUser(), a);
				String json = c.intoJson()+ "\n\t \"opzione\":\""+ o.getLetter()+"\" } \n ] }"; //aggiungere lettera opzione
				rAF.writeBytes(json);
				rAF.close();

				}catch(IOException e) {
					System.out.println(e+" writing preferences on json");
				}
		}
		else {
			Alert requestLogInAlert= new Alert(Alert.AlertType.INFORMATION);
			requestLogInAlert.setTitle("Ooops!..");
			requestLogInAlert.setHeaderText("Seems like you've already suscribed to this activity, please modify it from your UserPage");
			requestLogInAlert.show();
		}
		
	}
	
	//vede gli utenti associati all'attività
	public static ArrayList<Utente> getPeople(Activity a){
		ArrayList<Utente> iscritti= new ArrayList<Utente>();
		for(Preferenza p: preferences) {
			Activity tempAct=p.getCoppia().getActivity();
			if(tempAct.getId().equals(a.getId())) {
				Utente u= p.getCoppia().getUtente();
				iscritti.add(u);
			}
		}
		return iscritti;

	}
	
	//elimina la preferenza e poi riscrive il file
	public static void deletePreference(Preferenza pref) {
		String elencoInJson="";
		String daEliminareId=pref.getFullId();
		for(int i=0; i<preferences.size(); i++) {
			String attualeID=preferences.get(i).getFullId();
			if(daEliminareId.equals(attualeID)) {
				preferences.remove(i);
			}
			else {
				if(!(elencoInJson.equals(""))) 
					elencoInJson= elencoInJson+", \n";
				elencoInJson= elencoInJson+preferences.get(i).getCoppia().intoJson()+preferences.get(i).intoJson()+"}\n";
			}
		}
		
		try {
		PrintWriter pw= new PrintWriter(pathStr);
		pw.print("{ \n \"preferences\": ["+ elencoInJson + "\n]}");
		pw.close();
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in removing a preference");
		}
		if(Principale.getLoggedUser().isAdmin()) {
			AdminPage.updatePreferences();
		}
		else {
			UserPage.update();
		}
		
	}
	
	public static ArrayList<Preferenza> getAllPreferences(){
		return preferences;
	}
}
