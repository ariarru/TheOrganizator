package activities;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;



public class Activity {
	
	private String IDact; 
	private String name;
	private String where; //indica il luogo
	private String url;
	private int maxIscrizioni;
	private String descrizione=null;
	private String materiali=null;
	private ArrayList<Opzione> proposte;
	
	public Activity(String id, String name, String luogo) {
		this.IDact= id;
		this.name=name;
		this.where= luogo;
		proposte= new ArrayList<Opzione>();
	}
	
	
	public String getId() {
		return this.IDact;
	}
	public String getName() {
		return this.name;
	}
	public String getWhere() {
		return this.where;
	}
	public int getMaxIscrizioni() {
		return this.maxIscrizioni;
	}
	public String getDescription() {
		return this.descrizione;
	}
	public String getMaterials() {
		return this.materiali;
	}
	public void setName(String name) {
		this.name=name;
	}
	public void setWhere(String newWhere) {
		this.where= newWhere;
	}
	public String getUrl() {
		return this.url;
	}
	public void setImageUrl(String newUrl) {
		if(newUrl.equals("null"))
			this.url="img/emptyActivity.png";
		else
			this.url= newUrl;
	}
	public void setDescription(String description) {
		this.descrizione= description;
	}
	public void setMaterials(String materials) {
		this.materiali=materials;
	}
	public void setMaxIscrizioni(String x) {
		char[] str= x.toCharArray();
		boolean check=false;
		//controlla che sia davvero un numero
		for(char c: str) {
			if(c<58 && c>48)
				check=true;
			else
				check=false;
		}
		if(check)
		maxIscrizioni=Integer.parseInt(x);
		else
			maxIscrizioni=1;
	
	}
	public void setMaxIscrizioni(int x) {
		maxIscrizioni=x;
	}

	
	//genera un array con le opzioni di preferenza
	public void setOptions(JsonArray jsonArr) {
		for(int i=0; i<jsonArr.size(); i++) {
			JsonObject opJ= jsonArr.get(i).getAsJsonObject();
			Opzione option= new Opzione(opJ.get("ID").getAsString(),opJ.get("orario").getAsString(),opJ.get("giorno").getAsString());
			proposte.add(option);
		}
	}
	//aggiunge un'opzione all'array
	public void addOption(Opzione o) {
		proposte.add(o);

	}
	//restitiusce tutte le opzioni
	public ArrayList<Opzione> getOptions(){
		return proposte;
	}
	//rimuove un'opzione
	public void removeOptions(Opzione o) {
		if(proposte.contains(o)) {
			proposte.remove(o);
		}
		else {
			System.out.println("check removeoptions");
		}
	}
	
	//data la lettera restituisce l'opzione associata
	public Opzione getOption(String idLetter) {
		for(int i=0; i<proposte.size(); i++) {
			if(proposte.get(i).getLetter().equals(idLetter)) {
				return proposte.get(i);
			}
		}
		return null;
	}
	
	public String intoJson() {
		String json= "{\n\"IDact\":\""+IDact+"\", \n\"activity\":[\n{\"name\":\""+name+"\", \n\t \"where\":\""+where+"\",\n\t\"url\":";
		if(url==null) 
			json=json+"null";
		else
			json=json+"\""+url+"\"";
		json=json+", \n\t \"maxIscrizioni\":"+ maxIscrizioni+", \n\t\"descrizione\":";
		if(descrizione==null) 
			json=json+"null";
		else
			json=json+"\""+descrizione+"\"";
		json=json+", \n\t\"materiali\":";
		if(materiali==null) 
			json=json+"null";
		else
			json=json+"\""+materiali+"\"";
		json=json+", \n\t\"options\": ["; 
		//inserire intoJson delle opzioni
		for(int p=0; p<proposte.size(); p++) {
			json=json+ proposte.get(p).intoJson();
			if(p!=proposte.size()-1)
				json=json+",\n";
		}
		
		json=json+"]\n\t} \n ]}";
		return json;
				
	}
	
}
