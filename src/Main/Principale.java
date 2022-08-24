package Main;

import java.io.File;
import java.util.HashMap;

import activities.Category;
import home.Home;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import login.Utente;



public class Principale extends Application {

	public static Stage primary;
	public static BorderPane root= new BorderPane();
	public static Scene s1= new Scene(root, 1200, 900);
	public static HashMap<String, Scene> map= new HashMap<String, Scene>();
	private static boolean logged=false;
	private static Utente loggedUser;
	private static Category categories;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Homepage");
		primary = primaryStage;
		categories= new Category("ElencoCategorie.txt");
		root= new Home().getHome();
		s1.setRoot(root);
		
		File css = new File("layout.css");
		s1.getStylesheets().add("file://" + css.getAbsolutePath());
		
		map.put(""+Indice.Home, s1);
		
		primaryStage.setScene(s1);
		primary.show();
	}
	
	//aggiorna la Scene
	public static void setNew(String key) {
		if(map.containsKey(key) && (map.get(key) != null) ) {
			primary.setScene(map.get(key));
			primary.setTitle(key);
		}
	}
	
	//per controllare che il login sia già stato effetuato e da chi
	public static void setLoggedStatus(boolean state, Utente logUser) {
		loggedUser= logUser;
		logged=state;
	}
	
	public static boolean isLogged() {
		return logged;
	}
	
	public static Utente getLoggedUser() {
		if(isLogged()) {
			return loggedUser;
		}
		else 
			return null;
	}
	
	//aggiorna le categorie
	public static void updateCategories() {
		categories=new Category("ElencoCategorie.txt");
	}
	public static Category getCategory() {
		return categories;
	}

}
