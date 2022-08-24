package login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Main.Indice;
import Main.Principale;
import home.AdminPage;
import home.UserPage;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import layout.BottomLogo;
import layout.Menu;
import layout.topBorder;


public  class Login {
	
	private String user= "";
	private String pw="";
	private File css = new File("layout.css");
	public static BorderPane loginPane;
	private Scene loginScene;
	private boolean state=false; //controlla se admin
	
	public Login(){
		loginPane= new BorderPane();
		loginScene= new Scene(loginPane, 1200, 750);
		loginPane.setTop(new topBorder("Login").getTop());
		loginPane.setCenter(generateLogin());
		loginPane.setRight(new BottomLogo().getBottomLogo());
		loginScene.getStylesheets().add("file://" + css.getAbsolutePath());
		Principale.map.put("Login", loginScene);
		
	}
	
	//aggiorna il topBorder
	public static void updateTop() {
		loginPane.setTop(new topBorder("Login").getTop());
	}
	//genera l'interfaccia
	public HBox generateLogin() {
		HBox hb= new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(20);
		TextField userField= new TextField();
		PasswordField pwField= new PasswordField();
		userField.setPromptText("Insert username");
		pwField.setPromptText("Insert password");
		userField.setMaxWidth(700);
		pwField.setMaxWidth(700);
		userField.setMaxHeight(50);
		pwField.setMaxHeight(50);
		userField.setId("login");
		pwField.setId("login");
		//il login avviene se viene premuto invio nel PasswordField	
		EventHandler<KeyEvent> enter= new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if(ke.getCode().equals(KeyCode.ENTER)) {
					user= userField.getText();
					pw= pwField.getText();
					userField.setPromptText("Insert username");
					pwField.setPromptText("Insert password");
					userField.setText("");
					pwField.setText("");
					access();
				}	
			}
		};

		pwField.setOnKeyReleased(enter);
		ImageView imgUser= new ImageView(new Image("img/img1_2.jpg"));
		imgUser.setFitHeight(400);
		imgUser.setPreserveRatio(true);
		imgUser.setId("logIcon");
		HBox hb1= new HBox();
		hb1.setSpacing(4);
		HBox hb2= new HBox();
		hb2.setSpacing(5);
		hb1.setAlignment(Pos.CENTER);
		hb2.setAlignment(Pos.CENTER);
		ImageView userImg= new ImageView(new Image ("img/user.png"));
		ImageView pwImg= new ImageView(new Image ("img/pw.png"));
		userImg.setFitHeight(45);
		userImg.setFitWidth(50);
		pwImg.setFitHeight(45);
		pwImg.setFitWidth(50);
		userImg.setId("logIcon");
		pwImg.setId("logIcon");
		hb1.getChildren().addAll(userImg, userField);
		hb2.getChildren().addAll(pwImg, pwField);
		Button logIn= new Button("Log In");
		logIn.setId("logButton");
		logIn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				user= userField.getText();
				pw= pwField.getText();
				userField.setPromptText("Insert username");
				pwField.setPromptText("Insert password");
				userField.setText("");
				pwField.setText("");
				access();
				}
		});
		Button subscribe= new Button("Subscribe");
		subscribe.setId("logButton");
		subscribe.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				richiestaIscrizione();
				}
		});
		HBox hb3= new HBox(subscribe, logIn);
		hb3.setAlignment(Pos.CENTER);
		hb3.setSpacing(10);
		VBox vbIn= new VBox(6);
		vbIn.setAlignment(Pos.CENTER);
		vbIn.getChildren().addAll(hb1, hb2, hb3);
		hb.getChildren().addAll(imgUser, vbIn);
		return hb;
	}
	
	public Scene getLoginScene() {
		return loginScene;
	}
	
	public BorderPane getLoginPane() {
		return loginPane;
	}
	//effettua il login
	private void access() {	
		try {
			Path pathAccess= Paths.get("Access.json");
			String pathAccessStr=""+pathAccess.toAbsolutePath();
			Object obj= JsonParser.parseReader(new FileReader(pathAccessStr));
			JsonObject jsonObj= (JsonObject) obj;
			JsonArray accounts= (JsonArray) jsonObj.get("users");
			boolean found=false;
			for(int i=0; i<accounts.size(); i++) {
				JsonObject dbUser= (JsonObject) accounts.get(i);  //data base user
				if(dbUser.get("username").getAsString().equals(user) && dbUser.get("password").getAsString().equals(pw)) {
					found=true;
					state=  dbUser.get("admin").getAsBoolean();
					if(state) {
						Utente accessUser= new Admin(dbUser.get("username").getAsString(), dbUser.get("password").getAsString(), dbUser.get("ID").getAsString());
						Principale.setLoggedStatus(true, accessUser);
						Principale.map.put("Admin", new AdminPage().getAdminScene());
						Principale.setNew(""+Indice.Admin);
						break;
					}
					else {
						Utente accessUser= new User(dbUser.get("username").getAsString(), dbUser.get("password").getAsString(), dbUser.get("ID").getAsString());
						Principale.setLoggedStatus(true, accessUser);
							Principale.map.put(""+Indice.User, new UserPage().getUserScene());
							Principale.setNew(""+Indice.User);
						break;
					}
				}
			}
			if(!found) {
				Alert popUp= new Alert(AlertType.ERROR);
				popUp.setHeaderText("This credentials are not valid:");
				popUp.setContentText(user + "\t" + pw +"\n sign in to create your profile");
				popUp.showAndWait(); 
			}
			else {
				for(int i=0; i<Indice.values().length; i++) {
					Menu.updateTop(Indice.values()[i]+"");
				}
				
			}
		}
		catch(FileNotFoundException e) {
			System.out.println(e + " in Login");
		} catch (IllegalArgumentException e) {
			System.out.println(e+ " in Login");
		}
	}
	
	//apre il modulo di iscrizione
	private void richiestaIscrizione() {
		Stage addUserWindow= new Stage();
		VBox layout= new VBox();
		layout.setAlignment(Pos.CENTER);
		layout.setId("scenePane");
		layout.setSpacing(10);
		Scene addUserScene= new Scene(layout, 300,350);
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
		Button cancel= new Button("Cancel");
		Button done= new Button("Done");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addUserWindow.close();
			}
		});
		done.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				Alert popUp2= new Alert(AlertType.ERROR);
				if(newUsername.getText().equals("") || newPW.getText().equals("")) {
					popUp2.setHeaderText("Invalid values");
					popUp2.setContentText("Please insert valid credentials");
					popUp2.show();
				}
				else {	
					addUser(newUsername.getText(), newPW.getText());
					popUp2= new Alert(AlertType.INFORMATION);
					popUp2.setHeaderText("Thanks for your subscription");
					popUp2.setContentText("Please wait for an admin to confirm your account. Validation may take maximum 24 hours");
					addUserWindow.close();
					popUp2.show(); 
				}
			}	
		});
		HBox hb3= new HBox(cancel, done);
		hb3.setSpacing(10);
		hb3.setAlignment(Pos.CENTER);
		layout.getChildren().addAll(hb1,hb2, hb3);
		addUserWindow.setScene(addUserScene);
		addUserWindow.show();
	}
	//aggiunge un utente al json del pendingRequest
		public void addUser(String name, String pw) {
			try {
				Path pathRequests= Paths.get("pendingRequest.json");
				String pathRequestsStr=""+pathRequests.toAbsolutePath();
				RandomAccessFile rAF= new RandomAccessFile(new File(pathRequestsStr), "rw"); 
				long posizioneCursore= rAF.length();
				long stop;
				while(rAF.length()>0) { 
					posizioneCursore--;
					rAF.seek(posizioneCursore);
					if(rAF.readByte() == ']') {
						stop=posizioneCursore;
						while(posizioneCursore>1) { //serve per controllare se ci siano altre richieste, in caso aggiunge la virgola
							posizioneCursore--;
							rAF.seek(posizioneCursore);
							if(rAF.readByte() == '}') {
								rAF.seek(posizioneCursore);
								rAF.writeBytes("},\n");
								stop++; //faccio in modo che il cursore sia dopo la virgola e \n
								break;
							}
						}
						rAF.seek(stop); 
						break;
					}
				}
				String json = "{\n"+ "    \"username\": \""+name+"\",\n"+ "    \"password\": \""+pw+"\"\n"+ "    }";
				rAF.writeBytes(json+ "]\n}");
			    rAF.close();
			} catch (FileNotFoundException e) {
				System.out.println(e+" in adding users");
			}
			catch (IOException e) {
				System.out.println(e+" in adding users");
			}
		}
	
}
