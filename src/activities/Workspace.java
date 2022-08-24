package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import layout.Block;
import layout.topBorder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class Workspace {
	
	private BorderPane pane;
	private Scene workScene;
	private ArrayList<Activity> activities= new ArrayList<Activity>();
	private String categoryName;
	private File css = new File("layout.css");
	private String IDactivity;
	
	//legato ad una specifica categoria
	public Workspace(String categoryName) { 
		this.categoryName= categoryName;
		pane= new BorderPane();
		pane.setTop(new topBorder(categoryName).getTop());
		pane.setCenter(center());
		workScene= new Scene(pane, 1200, 750);
		workScene.getStylesheets().add("file://" + css.getAbsolutePath());
		
	}
	
	//legge le attività dal json
	private void findInJson() {
		try {
			activities.clear();
			Path path= Paths.get(categoryName+".json");
			String pathStr=""+path.toAbsolutePath();
			JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
			JsonArray jsonArr= (JsonArray) jsonObj.get(categoryName); //array con attività della categoria
			for(JsonElement a: jsonArr) { //per ogni attività della categoria
				String idAct= a.getAsJsonObject().get("IDact").getAsString();
				JsonArray activityJ= (JsonArray) a.getAsJsonObject().getAsJsonArray("activity"); //contiene informazioni e opzioni
				for(int j=0; j<activityJ.size(); j++) {
					JsonObject dbActivity= (JsonObject) activityJ.get(j);
					Activity act = new Activity(idAct, dbActivity.get("name").getAsString(), dbActivity.get("where").getAsString());
					if(dbActivity.get("url").isJsonNull()) {
						act.setImageUrl("null");
					}
					else {
						act.setImageUrl(dbActivity.get("url").getAsString());
					}
					if(!(dbActivity.get("maxIscrizioni").isJsonNull())) {
						act.setMaxIscrizioni(dbActivity.get("maxIscrizioni").getAsInt());
					}
					if(!(dbActivity.get("descrizione").isJsonNull())) {
						act.setDescription(dbActivity.get("descrizione").getAsString());
					}
					if(!(dbActivity.get("materiali").isJsonNull())) {
						act.setMaterials(dbActivity.get("materiali").getAsString());
					}
					JsonArray optionsJ= (JsonArray) dbActivity.get("options");
					if(!(optionsJ.isJsonNull()))
						act.setOptions(optionsJ);
					activities.add(act);
				}
			}

		} catch(FileNotFoundException e) {
			System.out.println(e+ "in generating activities managment");
		}
	}
	
	private GridPane center() {
		GridPane grid= new GridPane();
		grid.setPadding(new Insets(10,10,10,10));
		grid.setHgap(15);
		grid.setVgap(15);
		int colonna=0;
		int riga=0;
		findInJson();
		for(int i=0; i<activities.size(); i++) {
			Activity dbActivity= activities.get(i); //dbActivity= data base Activity
			Block actBlock=new Block(dbActivity);
			actBlock.setNewWidth(200);
			grid.add(actBlock.getBlock(), colonna, riga);
			colonna++;
			if(colonna==3) {
				colonna=0;
				riga++;
			}
			
		}
		grid.setAlignment(Pos.CENTER);
		return grid;
	}
	public Scene getWorkspaceScene() {
		return workScene;
	}
	
	public String getIDact() {
		return IDactivity;
	}

}
