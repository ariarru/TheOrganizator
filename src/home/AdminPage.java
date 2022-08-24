package home;

import java.io.File;
import Main.Principale;
import activities.GestioneAttivitaAdmin;
import activities.GestionePreferenzeAdmin;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import layout.BottomLogo;
import layout.Icon;
import layout.topBorder;

public class AdminPage {
	
	private File css = new File("layout.css");
	public static BorderPane adminPane;
	private Scene adminScene;
	
	public AdminPage(){
		adminPane= new BorderPane();
		adminScene= new Scene(adminPane, 1200, 750);
		adminPane.setTop(new topBorder("Admin").getTop());
		adminPane.setLeft(generateOptions());
		
		adminScene.getStylesheets().add("file://" + css.getAbsolutePath());
		
		Principale.map.put("Admin", adminScene);
		
	}
	//aggiorna il topBorder
	public static void updateTop() {
		adminPane= new BorderPane();
		adminPane.setTop(new topBorder("Admin").getTop());
	}
		
	//genera le possibilità di azione che può compiere l'admin
	public VBox generateOptions() {
		VBox vb= new VBox(); //colore border #693539
		vb.setAlignment(Pos.TOP_CENTER);
		vb.setSpacing(10);
		vb.setPadding(new Insets(20,20, 3,3));
		Text titleUsers= new Text("Users Management");
		titleUsers.setId("subTitle");
		Icon editUtenti= new Icon("img/edit utenti.png");
		editUtenti.setWidth(70);
		editUtenti.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				adminPane.setCenter(new GestioneUtentiAdmin().getContent());
			}
		});
		Text editText= new Text("Add or Remove users / Edit users' information");
		//quando viene cliccata l'icon compare nel center del borderpane la possibilità di modificare le cose
		VBox vb1= new VBox();
		vb1.getChildren().addAll(editUtenti.getIcon(), editText);
		vb1.setAlignment(Pos.CENTER);
		
		Text titleActivities= new Text("Activities Management");
		titleActivities.setId("subTitle");
		Icon editActivities= new Icon("img/editActivities.png");
		editActivities.setWidth(70);
		editActivities.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				adminPane.setCenter(new GestioneAttivitaAdmin().getContent());
			}
		});
		Text editActivitiesText= new Text("Add, Remove or Edit activities");
		VBox vb2= new VBox();
		vb2.getChildren().addAll(editActivities.getIcon(), editActivitiesText);
		vb2.setAlignment(Pos.CENTER);
		HBox hb1= new HBox(vb2);
		hb1.setSpacing(10);
		Icon editPreferences= new Icon("img/preferenze.png");
		editPreferences.setWidth(70);
		editPreferences.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				adminPane.setCenter(new GestionePreferenzeAdmin().getContent()); 
			}
		});
		Text editPreferencesText= new Text("View or Edit preferences");
		VBox vb3= new VBox();
		vb3.getChildren().addAll(editPreferences.getIcon(), editPreferencesText);
		vb3.setAlignment(Pos.CENTER);
		
		hb1.getChildren().addAll(vb3);
		vb.getChildren().addAll(titleUsers, vb1, titleActivities, hb1, new BottomLogo().getBottomLogo());
		vb.setMaxWidth(450);
		vb.setMinWidth(310);
		vb.setStyle("-fx-background-color:#d16369");
		return vb;
	}
	
	public Scene getAdminScene() {
		return adminScene;
	}
	
	public BorderPane getAdminPane() {
		return adminPane;
	}
	
	//aggiorna la visuale degli utenti dopo eventuali azioni di modifica o rimozione
	public static void updateUser() {
		adminPane.setCenter(new GestioneUtentiAdmin().getContent());
	}
	//aggiorna la visuale delle attività dopo modifiche o rimozioni
	public static void updateActivities() {
		adminPane.setCenter(new GestioneAttivitaAdmin().getContent());
	}
	//aggiorna la visuale delle preferenze dopo modifiche o rimozioni
	public static void updatePreferences() {
		adminPane.setCenter(new GestionePreferenzeAdmin().getContent()); 

	}
	
}
