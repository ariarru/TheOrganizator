package home;

import java.io.File;

import Main.Indice;
import Main.Principale;
import activities.AllCategoriesPage;
import activities.UserActivities;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import layout.BottomLogo;
import layout.Icon;
import layout.Menu;
import layout.UserTable;
import layout.topBorder;
import login.Utente;

public class UserPage {
	private File css = new File("layout.css");
	public static BorderPane userPane;
	private Scene userScene;
	private static Utente user;
	
	
	public UserPage(){
		user= Principale.getLoggedUser();
		userPane= new BorderPane();
		userPane.setTop(new topBorder("@"+user.getUsername()).getTop());
		userPane.setLeft(showActivities());
		userPane.setRight(showInfo());
		userScene= new Scene(userPane, 1200, 750);
		userPane.getStylesheets().add("file://" + css.getAbsolutePath());
		Principale.map.put("UserPage", userScene);
	}
	
	//mostra le attività a cui è iscritto l'utente
	private static VBox showActivities() {
		VBox userActivities= new VBox(15);
		userActivities.setStyle("-fx-background-color:#D3EDEE");
		userActivities.setMaxWidth(500);
		Text yourActivities= new Text("Your Preferences:");
		yourActivities.setFont(new Font("Cathlyne", 40));
		UserActivities allUserActivities=	new UserActivities(user);
		userActivities.getChildren().addAll(yourActivities, allUserActivities.getStackPane());
		Button printTable= new Button("View your weekly table");
		printTable.setId("logButton");
		printTable.setStyle("-fx-background-color: #B6E0E2;");
		printTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				try {
					userPane.setCenter(new UserTable().getTable());
					
				} catch(IllegalArgumentException iae) {
					Alert sovrapposizione= new Alert(AlertType.WARNING);
					sovrapposizione.setHeaderText("Event overlaps");
					sovrapposizione.setContentText("Looks like some of your preferences happen in the same time. Check your preferences and remove the overlaps");
					sovrapposizione.show();
				}
				
			}
		});
		if(allUserActivities.getSubscribed()) {
			userActivities.getChildren().add(printTable);
		}
		userActivities.setPadding(new Insets(7, 5, 5, 5));
		userActivities.setPickOnBounds(true);
		Button allCategoriesBtn= new Button("to all Categories...");
		allCategoriesBtn.setId("logButton");
		allCategoriesBtn.setStyle("-fx-background-color: #B6E0E2;");
		allCategoriesBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				Principale.map.putIfAbsent(""+Indice.Categories, new AllCategoriesPage().getAllCategoriesScene());
				Principale.setNew(""+Indice.Categories);
			}
		});
		Line divisor= new Line(-150,0,150,0); //start x,start y, end x, end y
		divisor.setStroke(Color.web("#457B9D"));
		divisor.setStrokeWidth(3.5);		
		userActivities.setAlignment(Pos.TOP_CENTER);
		userActivities.getChildren().addAll(divisor, allCategoriesBtn);
				
		return userActivities;
	}
	
	//mostra le informazioni dell'utente
	private VBox showInfo() {
		VBox userInfo= new VBox();
		ImageView profilePhoto= new ImageView(new Image("img/big user.png"));
		profilePhoto.setFitWidth(100);
		profilePhoto.setPreserveRatio(true);
		Text welcome= new Text("Welcome @"+ user.getUsername()+"!");
		welcome.setFont(new Font("Cathlyne", 35));
		welcome.autosize();
		VBox contain= new VBox(profilePhoto, welcome);
		contain.setSpacing(15);
		contain.setAlignment(Pos.TOP_CENTER);
		Icon logOut= new Icon("img/dx-row.png");
		logOut.setWidth(55);
		logOut.changeColor("#8EB4CD");
		logOut.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				System.out.print("cliccato logout");
				Principale.setLoggedStatus(false, null);
				for(int i=0; i<Indice.values().length; i++) {
					Menu.updateTop(Indice.values()[i]+"");
				}
				Principale.setNew(""+Indice.Login);
			}
		});
		userInfo.getChildren().addAll(contain, logOut.getIcon());
		userInfo.setStyle("-fx-background-color:#9EC9E6");
		userInfo.setAlignment(Pos.CENTER);
		userInfo.setMinHeight(200);
		userInfo.setMaxWidth(350);
		userInfo.setSpacing(200);
		userInfo.setPadding(new Insets(20, 5, 5, 5));
		BottomLogo PatOnTS=new BottomLogo();
		userInfo.getChildren().add(PatOnTS.getBottomLogo());
		return userInfo;
	}
	
	public Scene getUserScene() {
		return userScene;
	}
	public static void update() {
		userPane.setLeft(showActivities());
	}
	
}
