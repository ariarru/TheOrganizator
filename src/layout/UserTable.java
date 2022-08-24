package layout;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import activities.GestionePreferenze;
import activities.Preferenza;
import activities.Range;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class UserTable {
	
	private VBox page;
	private HBox tabella;
	private String[] days= { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	private String toFile;
	
	public UserTable() {
		tabella= new HBox();
		page= new VBox(tabella);
		toFile="";
		createTable();
	}
	//crea la tabella
	private void createTable() {
		for(String giorno: days) {
			toFile= toFile+giorno+"----------------------------------\n";
			VBox colonna= new VBox(3);
			colonna.getChildren().add(getHeader(giorno));
			ArrayList<Preferenza> daily= getGiornoActivity(giorno);
			for(Preferenza pr: daily) {
				colonna.getChildren().add(getBody(pr));
			}
			tabella.getChildren().add(colonna);
		}
		Button export= new Button("Save...");
		export.setId("pActBtn");
		export.setStyle("-fx-background-color: #D2F2F4;");
		export.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT files (*.txt)", "*.txt"));
				Stage fileChooserWindow=new Stage();
				File selectedFile = fileChooser.showSaveDialog(fileChooserWindow);
				if (selectedFile != null) {
					 try {
				            PrintWriter writer=new PrintWriter(selectedFile);
				            writer.println(toFile);
				            writer.close();
				        } catch (IOException ex) {
				            System.out.println(ex + "in saving user's program");
				        }
				}
			}
		});
		page.getChildren().add(export);
		page.setSpacing(20);
		page.setAlignment(Pos.CENTER);
	}
	
	//grafica con testo giorno settimana
	private StackPane getHeader(String title) {
		StackPane spTitle= new StackPane();
		spTitle.setAlignment(Pos.CENTER);
		Text head= new Text(title);
		head.setFont(new Font("Cathlyne", 24));
		head.setFill(Color.web("#000000"));
		spTitle.getChildren().add(head);
		spTitle.setPrefSize(150, 5);
		spTitle.setStyle("-fx-background-color: #ffcab4;");
		return spTitle;
	}
	
	private StackPane getBody(Preferenza p) {
		//contiene nome, materiali e range
		StackPane spBody= new StackPane();
		spBody.setAlignment(Pos.CENTER);
		Text nome= new Text(p.getCoppia().getActivity().getName());
		nome.setFont(new Font("Arial Black", 14));
		nome.setFill(Color.web("#000000"));
		Text materiali= new Text("Materials:"+p.getCoppia().getActivity().getMaterials());
		materiali.setFont(new Font("Arial", 14));
		materiali.setWrappingWidth(100);
		materiali.setFill(Color.web("#000000"));
		Text orario= new Text("Time:"+p.getScelta().getOrario().toString());
		orario.setFont(new Font("Arial Black", 14));
		orario.setFill(Color.web("#000000"));
		VBox body= new VBox(nome, materiali, orario);
		body.setPadding(new Insets(2,2,2,2));
		body.setSpacing(5);
		body.setAlignment(Pos.CENTER);
		toFile= toFile+nome.getText()+"\t"+materiali.getText()+"\t"+orario.getText()+"\n";
		spBody.getChildren().add(body);
		spBody.setPrefSize(150, 5);
		spBody.setStyle("-fx-background-color: #ffffff");
		return spBody;
	}
	
	//prende le attività di quel giorno
	private ArrayList<Preferenza> getGiornoActivity(String giorno){
		ArrayList<Preferenza> allPreferences= GestionePreferenze.getUserChoice();
		ArrayList<Preferenza> dailyPreferences= new ArrayList<Preferenza>();
		//cerca nelle preferenze solo quelle del giorno
		for(Preferenza p: allPreferences) {
			if(p.getScelta().getGiorno().equals(giorno))
				dailyPreferences.add(p);
		}
		sort(dailyPreferences);
		return dailyPreferences;
	}
	
	//ordina in base all'orario
	private void sort(ArrayList<Preferenza> input) {
		ArrayList<Preferenza> output= new ArrayList<Preferenza>();
		for(int x=0; x<input.size(); x++) {
			Preferenza prefMin=input.get(x);
			Range min= prefMin.getScelta().getOrario();
			for(int i=1; i<input.size(); i++) {
				Range r= input.get(i).getScelta().getOrario();
				if((min.getLower()< r.getLower() && r.getLower()<min.getUpper())|| (min.getLower()< r.getUpper() && r.getUpper()<min.getUpper())) {
					//se vero gli orari si sovrappongono
					throw new IllegalArgumentException();
				}
				else if(min.getLower()>r.getLower()) {
					if(min.getUpper()>r.getUpper()) {
						//il nuovo range è più piccolo
						prefMin=input.get(i);
					}
				}
			}
			output.add(prefMin);
		}
		input=output;
	}
	
	public VBox getTable() {
		return page;
	}
}
