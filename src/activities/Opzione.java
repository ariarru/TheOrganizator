package activities;

public class Opzione {
	//le opzioni vengono valutate per esprimere la preferenza
	
	private Range orario;	
	private String giorno;
	private String letter;
	
	public Opzione(String letter, String orario, String giorno) {
	this.orario= new Range(orario);
	this.giorno= giorno;
	this.letter= letter;
	}
	public String getGiorno() {
		return this.giorno;
	}
	public Range getOrario() {
		return this.orario;
	}
	public String getLetter() {
		return this.letter;
	}
	public void setOrario(Range orario) {
		this.orario = orario;
	}
	public void setGiorno(String giorno) {
		this.giorno = giorno;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	public String intoJson() {
		String json= "{\"ID\": \""+letter+"\",\n\t\"orario\": \""+orario.toString()+"\",\n\t\"giorno\": \""+giorno+"\"\n\t}";
		return json;
	}
}
