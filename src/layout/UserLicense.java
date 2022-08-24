package layout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import activities.GestionePreferenze;
import activities.Preferenza;
import home.AdminPage;
import home.GestioneUtentiAdmin;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import login.Admin;
import login.Utente;

public class UserLicense {
	
	private String username;
	private String password;
	private String ID;
	private File css = new File("layout.css");
	private static Path pathAccess= Paths.get("Access.json");
	private static String pathAccessStr=""+pathAccess.toAbsolutePath();
	
	public UserLicense(Utente utente) {
		this.username= utente.getUsername();
		this.password=utente.getPassword();
		this.ID= utente.getID();
	}
	//genera la "patente" che mostra le info degli utenti
	public StackPane generateLicense() {
		StackPane sp= new StackPane();
		Rectangle r1= new Rectangle(250, 125);
		r1.setFill(Color.web("#A8DADC"));
		r1.setId("userLicense");
		Rectangle r2= new Rectangle(250, 40);
		r2.setFill(Color.web("#357ca2"));
		r2.setId("userLicense");
		ImageView userImg= new ImageView(new Image("img/user.png"));
		userImg.setFitWidth(50);
		userImg.setPreserveRatio(true);
		VBox info= new VBox();
		Text usernamE= new Text("Username: "+ this.username);
		Text pw= new Text("Password: ");
		Label passworD= new Label(this.password);
		passworD.setEffect(new GaussianBlur());
		HBox hbP= new HBox(pw, passworD);
		hbP.setAlignment(Pos.CENTER);
		info.getChildren().addAll(usernamE, hbP);
		HBox infoEImg= new HBox(userImg, info);
		infoEImg.setTranslateY(15);
		Text id= new Text("ID: "+this.ID);
		id.setTranslateX(7);
		id.setTranslateY(-10);
		id.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 15));
		Icon edit= new Icon("img/edit.png");
		edit.getIcon().setStyle("-fx-border-radius: 10px;");
		edit.changeColor("#85b8c5");
		edit.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				generateEditPane();
			}
		});
		edit.getIcon().setTranslateX(-3);
		edit.getIcon().setTranslateY(-3);
		sp.getChildren().addAll(r1, r2, infoEImg, edit.getIcon(), id);
		StackPane.setAlignment(id, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(r1, Pos.TOP_CENTER);
		StackPane.setAlignment(r2, Pos.BOTTOM_CENTER);
		StackPane.setAlignment(infoEImg, Pos.CENTER);
		StackPane.setAlignment(edit.getIcon(), Pos.BOTTOM_RIGHT);
		
		return sp;
	}
	//genera la finestra per modificare le informazioni
	private void generateEditPane() {
		Stage window= new Stage();
		window.setTitle("Editing...");
		window.setResizable(true);
		Label lU= new Label("Username: ");
		lU.setId("textPane");
		TextArea changeUsername= new TextArea();
		changeUsername.setId("textAreaPane");
		changeUsername.appendText(this.username);
		changeUsername.setMaxSize(100, 10);
		HBox hb1= new HBox(lU, changeUsername);
		hb1.setSpacing(7);
		HBox.setHgrow(lU, Priority.ALWAYS);
		Label lP= new Label("Password: ");
		lP.setId("textPane");
		TextArea changePW= new TextArea();
		changePW.setId("textAreaPane");
		changePW.setMaxSize(100, 10);
		HBox hb2=new HBox(lP, changePW);
		hb2.setSpacing(10);
		HBox.setHgrow(lP, Priority.ALWAYS);
		Text makeAdmin= new Text("Admin");
		makeAdmin.setId("textAreaPane");
		CheckBox admin= new CheckBox();
		HBox hb3= new HBox(makeAdmin, admin);
		hb3.setSpacing(15);
		Button close= new Button("Close");
		Button save= new Button("Save");
		Button remove= new Button("Remove Account");
		remove.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				removeAccount(ID);
				window.close();
			}
		});
		HBox hb4= new HBox(save, remove, close);
		hb4.setSpacing(10);
		VBox vb= new VBox(hb1, hb2, hb3, hb4);
		vb.setSpacing(10);
		vb.setAlignment(Pos.CENTER_LEFT);
		vb.setId("scenePane");
		Scene windowScene= new Scene(vb, 270, 400);
		close.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				window.close();
			}
		});
		save.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(!username.equals(changeUsername.getText()) || !password.equals(changePW.getText()) ) {
					//controlla che esista un username
					if(changeUsername.getText()=="") {
						Alert ops= new Alert(AlertType.ERROR);
						ops.setContentText("You cannot leave an user without username");
						ops.showAndWait();
					}
					//cambia solo username
					 else if(changePW.getText()=="") {
						changeInfo(changeUsername.getText(),password,ID);
					}
					
					else {
						changeInfo(changeUsername.getText(),changePW.getText(),ID);
					}
				}
				if(admin.isSelected()) {
					changeRole(ID);
					}
				window.close();
			}
		});
		windowScene.getStylesheets().add("file://" + css.getAbsolutePath());
		window.setScene(windowScene);
		window.show();
		
	}
	
	//per rimuovere l'account riscrive il file json
	public void removeAccount(String id) {
		ArrayList<Utente> elencoUtenti= GestioneUtentiAdmin.getAllUtenti();
		ArrayList<Preferenza> elencoPreferenze= GestionePreferenze.getAllPreferences();
		for(int p=0; p<elencoPreferenze.size(); p++) {
			if(elencoPreferenze.get(p).getCoppia().getUtente().getID().equals(id)) {
				elencoPreferenze.remove(p);
			}
		}
		for(int i=0; i<elencoUtenti.size(); i++) {
			if(elencoUtenti.get(i).getID().equals(id)) {
				elencoUtenti.remove(i);
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String elencoInJson= gson.toJson(elencoUtenti);
		try {
			
			PrintWriter pw= new PrintWriter(pathAccessStr);
			pw.print("{ \n \"users\": "+ elencoInJson + "\n}");
			pw.close();
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in removing an account");
		}
		
		AdminPage.updateUser();
	}
	
	//cambia le informazioni dell'utente nell'ArrayList e sovrascrive il json
	private void changeInfo(String changedU, String changedP, String id) {
		ArrayList<Utente> elencoUtenti= GestioneUtentiAdmin.getAllUtenti();
		for(int i=0; i<elencoUtenti.size(); i++) {
			if(elencoUtenti.get(i).getID().equals(id)) {
				elencoUtenti.get(i).setUsername(changedU);
				elencoUtenti.get(i).setPassword(changedP);
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String elencoInJson= gson.toJson(elencoUtenti);
		try {
		PrintWriter pw= new PrintWriter(pathAccessStr);
		pw.print("{ \n \"users\": "+ elencoInJson + "\n}");
		pw.close();
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in editing info account");
		}
		AdminPage.updateUser();
	}
	
	//cambia il ruolo dell'utente nell'ArrayList e sovrascrive il json
	private void changeRole(String id) {
		ArrayList<Utente> elencoUtenti= GestioneUtentiAdmin.getAllUtenti();
		for(int i=0; i<elencoUtenti.size(); i++) {
			if(elencoUtenti.get(i).getID().equals(id)) {
				elencoUtenti.add(i, new Admin(elencoUtenti.get(i).getUsername(), elencoUtenti.get(i).getPassword(), elencoUtenti.get(i).getID()));
				elencoUtenti.remove(i+1);
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String elencoInJson= gson.toJson(elencoUtenti);
		try {
		PrintWriter pw= new PrintWriter(pathAccessStr);
		pw.print("{ \n \"users\": "+ elencoInJson + "\n}");
		pw.close();
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in editing info account");
		}
		AdminPage.updateUser();
	}
		
	
}






