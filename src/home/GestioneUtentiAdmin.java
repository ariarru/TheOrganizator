package home;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import layout.Icon;
import layout.UserLicense;
import login.Admin;
import login.User;
import login.Utente;

public class GestioneUtentiAdmin {
	
	private File css = new File("layout.css");
	protected static ArrayList<Utente> allUtenti= new ArrayList<Utente>();
	private VBox vb;
	private static Path path;
	private static String pathStr;
	
	public GestioneUtentiAdmin() {
		generate();
	}
	//genera la schermata in cui vengono mostrate le "patenti" degli utenti
	private void generate() {
		GridPane grid= new GridPane();
		grid.setPadding(new Insets(5,5,10,10));
		grid.setHgap(10);
		grid.setVgap(15);
		int colonna=0;
		int riga=0;
		setAllUtenti();
		for(int i=0; i<allUtenti.size(); i++) {
			Utente dbUser= allUtenti.get(i); //dbUser= data base User
			if(dbUser instanceof Admin) { //gli admin non si vedono e modificano e vicenda
				i++;
				if(i==allUtenti.size()) {
					break;
				}
				dbUser= allUtenti.get(i);
			}
			grid.add(new UserLicense(allUtenti.get(i)).generateLicense(), colonna, riga);
			colonna++;
			if(colonna==3) {
				colonna=0;
				riga++;
			}
			
		}
		Icon add= new Icon("img/add.png");
		add.setWidth(60);
		add.changeColor("#b4dee0");
		
		add.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addUserWindow();
			}
		});
		StackPane spAdd= new StackPane(add.getIcon());
		spAdd.setPrefWidth(250);
		StackPane.setAlignment(add.getIcon(), Pos.CENTER);
		if(readForRequest()) {
			Icon notification= new Icon("img/notification.png");
			notification.setWidth(60);
			spAdd.getChildren().add(notification.getIcon());
			StackPane.setAlignment(notification.getIcon(), Pos.CENTER_RIGHT);
			notification.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					addUserFromRequest();
				}
			});
		}
		grid.add(spAdd, colonna, riga);
		grid.setAlignment(Pos.CENTER);
		vb= new VBox(grid);
	}
	
	public VBox getContent() {
		return vb;
	}
	//genera la finestra per aggiungere un utente
	private void addUserWindow() {
		Stage addUserWindow= new Stage();
		VBox layout= new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.setId("scenePane");
		layout.setSpacing(10);
		Scene addUserScene= new Scene(layout, 300,500);
		addUserScene.getStylesheets().add("file://" + css.getAbsolutePath());
		Label lU= new Label("Username: ");
		lU.setId("textPane");
		TextArea newUsername= new TextArea();
		newUsername.setMaxHeight(30);
		newUsername.setMaxWidth(170);
		HBox hb1= new HBox(lU, newUsername);
		HBox.setHgrow(lU, Priority.ALWAYS);
		hb1.setSpacing(8);
		Label lP= new Label("Password: ");
		lP.setId("textPane");
		TextArea newPW= new TextArea();
		newPW.setMaxHeight(30);
		newPW.setMaxWidth(170);
		HBox hb2=new HBox(lP, newPW);
		hb2.setSpacing(10);
		HBox.setHgrow(lP, Priority.ALWAYS);
		String newUserID=generateNewID();
		Label lID= new Label("ID: "+ newUserID);
		lID.setId("textPane");
		CheckBox admin= new CheckBox();
		Label lA= new Label("Admin: ");
		lA.setId("textPane");
		HBox hb3= new HBox(10);
		hb3.getChildren().addAll(lA, admin);
		Button cancel= new Button("Cancel");
		cancel.setId("logButton");
		cancel.setStyle("-fx-background-color: #8ecae4");
		Button done= new Button("Done");
		done.setId("logButton");
		done.setStyle("-fx-background-color: #8ecae4");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addUserWindow.close();
			}
		});
		done.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(admin.isSelected()) {
					addAdmin(newUsername.getText(), newPW.getText(), newUserID);
				}
				else {
					addUser(newUsername.getText(), newPW.getText(), newUserID);
				}
				addUserWindow.close();
			    AdminPage.updateUser();
			}	
		});
		HBox hb4= new HBox(cancel, done);
		hb3.setSpacing(20);
		hb3.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(hb1,hb2, lID, hb3, hb4);
		addUserWindow.setScene(addUserScene);
		addUserWindow.show();
		
	}
	//aggiunge un utente al json
	public void addUser(String name, String pw, String id) {
		try {
			path= Paths.get("Access.json");
			pathStr=""+path.toAbsolutePath();
			RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); //uso il RandomAccessFile per avere controllo sul cursore
			long posizioneCursore= rAF.length();
			while(rAF.length()>0) { 
				posizioneCursore--;
				rAF.seek(posizioneCursore);
				if(rAF.readByte() == ']') {
					rAF.seek(posizioneCursore);
					break;
				}
			}
			User nuovoUtente= new User(name, pw, id);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(nuovoUtente);
			rAF.writeBytes(",\n"+ json+ "]\n}");
		    rAF.close();
		} catch (FileNotFoundException e) {
			System.out.println(e+" in adding users");
		}
		catch (IOException e) {
			System.out.println(e+" in adding users");
		}
	}
	
	//restituisce l'elenco completo d utenti registrati nel JSON
	public static ArrayList<Utente> getAllUtenti(){ 
		setAllUtenti();
		return allUtenti;
	}
	
	//riempe o aggiorna l'ArrayList che gestisce gli utenti, letto direttamente dal json
	private static void setAllUtenti() {
		try {
			allUtenti.clear();
			path= Paths.get("Access.json");
			pathStr=""+path.toAbsolutePath();
			Object obj= JsonParser.parseReader(new FileReader(pathStr));
			JsonObject jsonObj= (JsonObject) obj;
			JsonArray accounts= (JsonArray) jsonObj.get("users");
			for(int i=0; i<accounts.size(); i++) {
				JsonObject dbUser= (JsonObject) accounts.get(i);
				if(dbUser.get("admin").getAsBoolean()) {
					Admin userA= new Admin(dbUser.get("username").getAsString(),dbUser.get("password").getAsString(),dbUser.get("ID").getAsString());
					allUtenti.add(userA);
				}
				else {
					User user= new User(dbUser.get("username").getAsString(),dbUser.get("password").getAsString(),dbUser.get("ID").getAsString());
					allUtenti.add(user);
				}
			}
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in getting all users list");
		}
	}
	
	//legge se ci sono state delle richieste di iscrizione
	private boolean readForRequest() {
		try {
			path= Paths.get("pendingRequest.json");
			pathStr=""+path.toAbsolutePath();
			Object obj= JsonParser.parseReader(new FileReader(pathStr));
			JsonObject jsonObj= (JsonObject) obj;
			JsonArray jsonArr= jsonObj.get("request").getAsJsonArray();
			if(jsonArr.size()==0) {
				return false;
			}
			else{
				return true;
			}
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in getting all users list");
			return false;
		}
	}
	
	//genera l'ID per un nuovo utente aggiungendo 1 all'ID dell'ultimo utente
	private String generateNewID() {
		String lastID=allUtenti.get(allUtenti.size()-1).getID();
		int i= Integer.parseInt(lastID.substring(3));
		lastID= ""+(i+1); //riutilizzo la vecchia stringa per non crearne altre
		String newID="#";
		for(int c=0; c<5; c++) { //5 è la lunghezza totale
			if(newID.length()+1+ lastID.length() <=5) {
				newID= newID+"0";
			}
		}
		newID= newID + lastID;
		return newID;
	}
	
	//visualizza la finestra per aggiungere un utente prendendo i dati dal json
	private void addUserFromRequest() {
		GridPane gridRequests= new GridPane();
		gridRequests.setAlignment(Pos.TOP_CENTER);
		gridRequests.setHgap(5);
		VBox layout= new VBox(gridRequests);
		layout.setAlignment(Pos.CENTER);
		layout.setSpacing(10);
		layout.setStyle("-fx-background-color: linear-gradient(to right bottom, #f09ca0 ,#cee9f1);");
		Scene addUserScene= new Scene(layout);
		addUserScene.getStylesheets().add("file://" + css.getAbsolutePath());
		Stage addUserWindowFR= new Stage();
		
		int colonna=0, riga=0;
		ArrayList<JsonObject> elencoRichieste= new ArrayList<JsonObject>();
		Text tU= new Text("Username");
		tU.setFont(new Font("Arial Black", 15));
		gridRequests.add(tU, colonna, riga);
		colonna++;
		Text tP= new Text("Password");
		tP.setFont(new Font("Arial Black", 15));
		gridRequests.add(tP, colonna, riga);
		colonna++;
		Text tA= new Text("Admin");
		tA.setFont(new Font("Arial Black", 15));
		gridRequests.add(tA, colonna, riga);
		colonna++;
		Text tAR= new Text("Accept or Refuse");
		tAR.setFont(new Font("Arial Black", 15));
		gridRequests.add(tAR, colonna, riga);
		colonna=0;
		riga++;
		try {
			path= Paths.get("pendingRequest.json");
			pathStr=""+path.toAbsolutePath();
			Object obj= JsonParser.parseReader(new FileReader(pathStr));
			JsonObject jsonObj= (JsonObject) obj;
			//ha controllato prima se fosse null
			JsonArray richieste= (JsonArray) jsonObj.get("request");
			for(int i=0; i<richieste.size(); i++) {
				jsonObj= richieste.get(i).getAsJsonObject();
				elencoRichieste.add(jsonObj);
				Text username= new Text(jsonObj.get("username").getAsString());
				username.setFont(new Font("Arial", 18));
				gridRequests.add(username, colonna, riga);
				colonna++;
				Text password= new Text(jsonObj.get("password").getAsString());
				password.setFont(new Font("Arial", 16));
				gridRequests.add(password, colonna, riga);
				colonna++;
				CheckBox admin= new CheckBox();
				gridRequests.add(admin, colonna, riga);
				colonna++;
				HBox hb2= new HBox(5);
				Icon add= new Icon("img/approve.png");
				add.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						if(admin.isSelected()) {
							addAdmin(username.getText(), password.getText(), generateNewID());
						}
						else {
							addUser(username.getText(), password.getText(), generateNewID());
						}
						gridRequests.getChildren().removeAll(username, password, admin, hb2);
					}
				});
				Icon deny= new Icon("img/delete.png");
				deny.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						for(int i=0; i<elencoRichieste.size(); i++) {
							if(elencoRichieste.get(i).get("username").getAsString().equals(username.getText())) {
								elencoRichieste.remove(i);
							}
						}
						gridRequests.getChildren().removeAll(username, password, admin, hb2);

					}
				});
				hb2.getChildren().addAll(add.getIcon(), deny.getIcon());
				gridRequests.add(hb2, colonna, riga);
				colonna=0;
				riga++;
			}
			
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in getting all users list");
		}
		Button close= new Button("Close");
		close.setId("logButton");
		close.setStyle("-fx-background-color: #ea7b80");
		close.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String elencoInJson= gson.toJson(elencoRichieste);
				try {
					path= Paths.get("pendingRequest.json");
					pathStr=""+path.toAbsolutePath();
					PrintWriter pw= new PrintWriter(pathStr);
					pw.print("{ \n \"request\": "+ elencoInJson + "\n}");
					pw.close();
				} catch(FileNotFoundException e) {
					System.out.println(e+ "in removing a pending requests");
				}
				addUserWindowFR.close();
				AdminPage.updateUser();
			}
		});
		Button cancel= new Button("Cancel");
		cancel.setId("logButton");
		cancel.setStyle("-fx-background-color: #ea7b80");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addUserWindowFR.close();
			}
		});
		HBox btns= new HBox(cancel, close);
		layout.getChildren().add(btns);
		addUserWindowFR.setScene(addUserScene);
		addUserWindowFR.show();
	}

	//aggiunge un admin al json
		public void addAdmin(String name, String pw, String id) {
			try {
				path= Paths.get("Access.json");
				pathStr=""+path.toAbsolutePath();
				RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); 
				long posizioneCursore= rAF.length();
				while(rAF.length()>0) { 
					posizioneCursore--;
					rAF.seek(posizioneCursore);
					if(rAF.readByte() == ']') {
						rAF.seek(posizioneCursore);
						break;
					}
				}
				Admin nuovoUtente= new Admin(name, pw, id);
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = gson.toJson(nuovoUtente);
				rAF.writeBytes(",\n"+ json+ "]\n}");
			    rAF.close();
			} catch (FileNotFoundException e) {
				System.out.println(e+" in adding admin");
			}
			catch (IOException e) {
				System.out.println(e+" in adding admin");
			}
		}
}
