package activities;

import java.util.ArrayList;

import home.UserPage;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import layout.Icon;
import login.Utente;

public class UserActivities {
	
	private Utente user;
	private StackPane sp;
	private boolean subscribed;
	
	public UserActivities(Utente user) {
		this.user= user;
		fill();
	}
	
	private void fill() {
		sp= new StackPane();
		GestionePreferenze.readPreferences();
		ArrayList<Preferenza> prefUser= GestionePreferenze.getUserChoice(user);
		if(prefUser.isEmpty()) {
			Text ops= new Text("Looks like you're not subscribed into nothing, get started looking to all categories");
			ops.setWrappingWidth(150);
			sp.getChildren().add(ops);
			subscribed=false;
			StackPane.setAlignment(ops, Pos.CENTER);
		}
		else {
		VBox vb= new VBox(7);
		for(Preferenza pref: prefUser) {
			Text name= new Text(pref.getCoppia().getActivity().getName());
			Text opzSceltaG= new Text(pref.getScelta().getGiorno());
			Text opzSceltaO= new Text(pref.getScelta().getOrario().toString());
			Icon delete= new Icon("img/delete.png");
			delete.setWidth(20);
			delete.getIcon().setDisable(false);
			delete.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent me) {
					GestionePreferenze.deletePreference(pref);
					UserPage.update();
				}
			});
			HBox hbP= new HBox(name, opzSceltaG, opzSceltaO, delete.getIcon());
			hbP.setAlignment(Pos.CENTER_LEFT);
			hbP.setSpacing(10);
			vb.getChildren().add(hbP);
			}
		subscribed=true;
		sp.getChildren().add(vb);
		sp.setPickOnBounds(true);
		}
	}
	
	public Utente getUser() {
		return user;
	}

	public StackPane getStackPane() {
		return sp;
	}
	public boolean getSubscribed() {
		return subscribed;
	}
}
