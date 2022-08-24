package home;


import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import layout.Block;
import layout.BottomLogo;
import layout.topBorder;
import login.Login;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;

import Main.Indice;
import Main.Principale;
import activities.Category;
import activities.Workspace;
import Telegram.*;

public class Home {

	public VBox homePage= new VBox();
	public ScrollPane homeScroll= new ScrollPane();
	public static BorderPane root= new BorderPane();
	private boolean bot;
	private BotSession session = null;
	
	
	public Home() {	
		generateHome();
		homeScroll.setContent(homePage);
		homeScroll.setStyle("-fx-background-color: #f8fdf7;");
		homeScroll.setFitToWidth(true);
		root.setTop(new topBorder(""+Indice.Home).getTop());
		root.setCenter(homeScroll);
	}
	//aggiorna il topBorder
		public static void updateTop() {
			root.setTop(new topBorder(""+Indice.Home).getTop());
		}
	
	//genera l'interfaccia della home
	private void generateHome() {
		ImageView homeWallpaper= new ImageView(new Image("img/img01.jpg"));
		homeWallpaper.setPreserveRatio(true);
		homeWallpaper.setFitWidth(975);
		StackPane stkpTitle= new StackPane();
		Text subTitle= new Text("Choose your perfect holidays!");
		subTitle.setId("subTitle");
		Ellipse ellipse = new Ellipse(); 
		ellipse.setCenterX(125.0f);
		ellipse.setCenterY(125.0f);
		ellipse.setRadiusX(170.0f);
		ellipse.setRadiusY(35.0f);
		ellipse.setEffect(new DropShadow(2.5, 0.0, -3.0, Color.web("#D3DBDA")));
		ellipse.setFill(Color.web("#F4F4F4"));
		stkpTitle.getChildren().addAll(ellipse,subTitle);
		stkpTitle.setTranslateY(130);
		StackPane stkp= new StackPane(homeWallpaper, stkpTitle);
		StackPane.setAlignment(homeWallpaper, Pos.TOP_CENTER);
		stkp.setPrefHeight(320);

		VBox experiences= new VBox();
		experiences.setAlignment(Pos.TOP_CENTER);
		
		//dispone le tre categorie principali
		Category c= Principale.getCategory();
		GridPane expImages= new GridPane();
		expImages.setAlignment(Pos.CENTER);
		expImages.setHgap(20);
		
		Block block1= new Block("img/Fitness.jpg", "Fitness");
		block1.getBlock().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				String nome= c.getCategories().get(0);
				Principale.map.putIfAbsent(nome, new Workspace(nome).getWorkspaceScene());
				Principale.setNew(nome);
			}
		});
		
		Block block2= new Block("img/Nature.jpg", "Nature");
		block2.getBlock().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				String nome=c.getCategories().get(1);
				Principale.map.putIfAbsent(nome, new Workspace(nome).getWorkspaceScene());
				Principale.setNew(nome);
			}
		});
		Block block3= new Block("img/Cinema.jpg", "Cinema");
		block3.getBlock().setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				String nome=c.getCategories().get(2);
				Principale.map.putIfAbsent(nome, new Workspace(nome).getWorkspaceScene());
				Principale.setNew(nome);
			}
		});
		expImages.add(block1.getBlock(), 0, 0);
		expImages.add(block2.getBlock(), 1, 0);
		expImages.add(block3.getBlock(), 2, 0);
		
		Text expText=new Text("select your preferences between our beautiful categories");
		expText.setFont(new Font("Gill Sans", 25));
		HBox hb= new HBox(expText);
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		
		Line divisor= new Line(-400,0,400,0); //start x,start y, end x, end y
		divisor.setStroke(Color.web("#bb2a32"));
		divisor.setStrokeWidth(3.5);
		Button logInBtn= new Button("LogIn");
		logInBtn.setId("logButton");
		logInBtn.setStyle("-fx-background-color: #B6E0E2;");
		logInBtn.setFont(new Font("American Typewriter", 15));
		logInBtn.setPrefWidth(80);
		logInBtn.setPrefHeight(30);
		logInBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
					Principale.map.put(""+Indice.Login, new Login().getLoginScene());
					Principale.setNew(""+Indice.Login);
			}
		});
		Button telegramBot= new Button();
		if(bot)
			telegramBot.setText("End Telegram Bot");
		else
			telegramBot.setText("Start Telegram Bot");
		telegramBot.setId("logButton");
		telegramBot.setStyle("-fx-background-color: #B6E0E2;");
		telegramBot.setFont(new Font("American Typewriter", 15));
		telegramBot.setPrefWidth(160);
		telegramBot.setPrefHeight(30);
		telegramBot.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(bot) {
					//fai spegnere il bot
					telegramBot.setText("Start Telegram Bot");
					bot=false;
					Alert telegramDown = new Alert(AlertType.INFORMATION);
					telegramDown.setHeaderText("Shutting the bot down");
					telegramDown.setContentText("We're turning the bot off when you will press \"ok\", don't worry if it the app gets stuck, it's normal, this might take one minute, please be patience");
					telegramDown.showAndWait();
					System.out.println("Closing Bot... 1 minute left");
					session.stop();
					
				}
				else {
					//fai partire il bot
					telegramBot.setText("End Telegram Bot");
					bot=true;
					ApiContextInitializer.init();
					TelegramBotsApi api = new TelegramBotsApi();
					try {
						session = api.registerBot(new Bot());
						System.out.println("PatonsBot is Online");
					} catch (Exception e) {
						//TODO: handle exception
						e.printStackTrace();
					}
					
				}
			}
		});
		HBox btns= new HBox(logInBtn, telegramBot);
		btns.setSpacing(10);
		btns.setAlignment(Pos.CENTER);
		//inizio sezione autori
		HBox authors= new HBox();
		Line divisorV= new Line (0, -60, 0, 60);
		divisorV.setStroke(Color.web("#bb2a32"));
		divisorV.setStrokeWidth(3.5);
		
		HBox aAri= new HBox(15);
		ImageView pAri= new ImageView(new Image ("img/ariArruzzoli.jpg"));
		pAri.setSmooth(true);
		pAri.setId("imgAuthor");
		pAri.setFitWidth(80);
		pAri.setPreserveRatio(true);
		Font fText= new Font("Arial", 14);
		Text nAri= new Text("Arianna Arruzzoli");
		nAri.setFont(new Font("Arial Black", 16));
		Text dAri= new Text("Designer and creator of The Organizator");
		dAri.setWrappingWidth(150);
		dAri.setFont(fText);
		VBox textA= new VBox(nAri,dAri);
		aAri.getChildren().addAll(pAri, textA);
		
		HBox aAle= new HBox(15);
		ImageView pAle= new ImageView(new Image ("img/aleMondini.jpg"));
		pAle.setSmooth(true);
		pAle.setId("imgAuthor");
		pAle.setFitWidth(80);
		pAle.setPreserveRatio(true);
		Text nAle= new Text("Alessandro Mondini");
		nAle.setFont(new Font("Arial Black", 16));
		Text dAle= new Text("Technician and Telegram supervisor");
		dAle.setWrappingWidth(150);
		dAle.setFont(fText);
		VBox textB= new VBox(nAle,dAle);
		aAle.getChildren().addAll(pAle, textB);
		authors.getChildren().addAll(aAri, divisorV, aAle);
		authors.setAlignment(Pos.CENTER);
		authors.setSpacing(3);
		experiences.getChildren().addAll(stkp, expImages, hb, btns, divisor);
		experiences.setSpacing(10);
		homePage.getChildren().addAll(experiences, authors, new BottomLogo().getBottomLogo());
		homePage.setAlignment(Pos.TOP_CENTER);
		homePage.setStyle("-fx-background-color: transparent;");
	}
	
	public BorderPane getHome() {
		return root;
	}
	

}
