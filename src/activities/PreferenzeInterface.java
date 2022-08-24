package activities;

import java.util.ArrayList;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import login.Utente;

public class PreferenzeInterface {
	private VBox layout;
	private StackPane sp= new StackPane();
	private boolean thereArePref=false;
	private GridPane gridPreferences= new GridPane();
	
	//costruttore per come le vede l'admin
	public PreferenzeInterface(ArrayList<Preferenza> preferences) {
		if(!(preferences.isEmpty()))
			thereArePref=true;
		organize(preferences);
	}
	
	//costruttore per mostrare all'utente
	public PreferenzeInterface(Utente u) {
		
	}
	
	//organizza le preferenze per attività
	private void organize(ArrayList<Preferenza> preferences) {
		ArrayList<String> check= new ArrayList<String>();
		int riga=0, colonna=0;
		gridPreferences.setAlignment(Pos.CENTER);
		gridPreferences.setVgap(2);
		gridPreferences.setHgap(3);
		for(Preferenza p: preferences) {
			Activity a= p.getCoppia().getActivity();
			layout= new VBox(5);
			if(check.contains(a.getName())) {
				
			}	
			else {
			createTitle(a.getName());
			//isola attività
			ArrayList<Preferenza> prefFiltrate= onlyPrefOneAct(preferences, a);
			generateGraphic(prefFiltrate);
			gridPreferences.add(layout, colonna, riga);
			colonna++;
			if(colonna==3) {
				colonna=0;
				riga++;
			}
			check.add(a.getName());
			}
		}
	}
	
	private ArrayList<Preferenza> onlyPrefOneAct(ArrayList<Preferenza> preferences, Activity a){
		ArrayList<Preferenza> scremate= new ArrayList<Preferenza>();
		for(Preferenza preferenza: preferences) {
			if(preferenza.getCoppia().getActivity().getId().equals(a.getId()))
				scremate.add(preferenza);
		}
		return scremate;
	}
	
	
	//crea titolo con nome attività
	private void createTitle(String title) {
		Text aTitle= new Text(title);
		aTitle.setFont(new Font("Cathlyne", 28));
		aTitle.setFill(Color.web("#000000"));
		StackPane sp= new StackPane(aTitle);
		sp.setPrefSize(200, 65);
		sp.setStyle("-fx-background-color: #C7E6BC");
		layout.getChildren().add(sp);
	}
	
	//crea elenco dove sono mostrati gli iscritti e le loro preferenze
	private void generateGraphic(ArrayList<Preferenza> list) { 
		TableView<Preferenza> listaUtenti= new TableView<Preferenza>();
		TableColumn<Preferenza, String> IDColumn= new TableColumn<Preferenza, String>("ID");
		IDColumn.setCellValueFactory(new Callback<CellDataFeatures<Preferenza, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Preferenza, String> p) {
		         String id=p.getValue().getCoppia().getUtente().getID();
		         return new ReadOnlyObjectWrapper(id);
		     }
		  });
		TableColumn<Preferenza, String>	usernameColumn= new TableColumn<Preferenza, String>("Username");
		usernameColumn.setCellValueFactory(new Callback<CellDataFeatures<Preferenza, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Preferenza, String> p) {
		         String username=p.getValue().getCoppia().getUtente().getUsername();
		         return new ReadOnlyObjectWrapper(username);
		     }
		  });
		TableColumn<Preferenza, String> prefColumn= new TableColumn<Preferenza, String>("Preferences");
		prefColumn.setCellValueFactory(new Callback<CellDataFeatures<Preferenza, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Preferenza, String> p) {
		         String opz=p.getValue().getFullId();
		         return new ReadOnlyObjectWrapper(opz);
		     }
		  });
		listaUtenti.getColumns().addAll(IDColumn, usernameColumn, prefColumn);
		listaUtenti.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		for(Preferenza p: list) {
			listaUtenti.getItems().add(p);
		}
		listaUtenti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		Button deletePref= new Button("Delete selected");
		deletePref.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				Preferenza pref=listaUtenti.getSelectionModel().getSelectedItem();
				GestionePreferenze.deletePreference(pref);
			}
		});
		deletePref.setId("pActBtn");
		layout.getChildren().addAll(listaUtenti, deletePref);
	}
	
	public StackPane getInterface() {
		if(thereArePref) {
			ScrollPane scrollP= new ScrollPane(gridPreferences);
			scrollP.setStyle("-fx-background-color: transparent;");
			scrollP.setFitToHeight(true);
			scrollP.setFitToWidth(true);
			sp.getChildren().add(scrollP);
			StackPane.setAlignment(gridPreferences, Pos.CENTER);
		}
		else {
			Text noPreferences= new Text("Looks like no users have expressed any preferences yet");
			noPreferences.setFont(new Font("Copperplate", 20));
			noPreferences.setWrappingWidth(300);
			sp.getChildren().add(noPreferences);
			StackPane.setAlignment(noPreferences, Pos.CENTER);
			
		}
		return sp;
	}
	
	
	
}
