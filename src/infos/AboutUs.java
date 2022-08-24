package infos;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import layout.BottomLogo;
import layout.topBorder;

public class AboutUs {
	private File css = new File("layout.css");
	public static BorderPane aboutPane;
	private Scene aboutScene;
	private StackPane spAU;
	
	public AboutUs(){
		aboutPane= new BorderPane();
		aboutPane.setTop(new topBorder("About Us").getTop());
		aboutPane.setCenter(generateGraphic());
		aboutScene= new Scene(aboutPane, 1200, 750);
		aboutScene.getStylesheets().add("file://" + css.getAbsolutePath());
	}
	//aggiorna il topBorder
	public static void updateTop() {
		aboutPane= new BorderPane();
		aboutPane.setTop(new topBorder("About Us").getTop());
	}
	
	private StackPane generateGraphic() {
		Font f= new Font(17);
		HBox hbInfoOrg= new HBox(10);
		ImageView logoOrg= new ImageView(new Image("img/logoOrg.png"));
		logoOrg.setFitHeight(90);
		Text descriptionOrg= new Text("The Organizator was born as an event management application. It gives the possibility to manage events and activities, to record user registrations and then define a well-stocked program of activities.\nGiven its flexibility, it is adaptable to any type of context that requires an organization.");
		descriptionOrg.setWrappingWidth(700);
		descriptionOrg.setFont(f.font("Verdana", FontWeight.NORMAL, FontPosture.ITALIC, 17));
		descriptionOrg.setFill(Color.BLACK);
		hbInfoOrg.getChildren().addAll(logoOrg, descriptionOrg);
		hbInfoOrg.setAlignment(Pos.CENTER);
		HBox hbInfoPat= new HBox(12);
		Text descriptionPat= new Text("The organizator is an application from the PATonTS team.");
		descriptionPat.setFont(f.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
		descriptionPat.setFill(Color.BLACK);
		hbInfoPat.getChildren().addAll(descriptionPat, new BottomLogo().getBottomLogo());
		hbInfoPat.setAlignment(Pos.CENTER);
		Line divL= new Line(-350,0, 350, 0);
		divL.setStroke(Color.web("#db4753"));
		divL.setStrokeWidth(2.5);
		Text div= new Text("The Team");
		div.setFill(Color.web("#d93f4c"));
		div.setFont(f.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 19));
		HBox hbInfoAri= new HBox(10);
		ImageView pAri= new ImageView(new Image ("img/ariArruzzoli.jpg"));
		pAri.setSmooth(true);
		pAri.setFitHeight(120);
		pAri.setPreserveRatio(true);
		Font fText= new Font("Arial", 14);
		Text nAri= new Text("Arianna Arruzzoli");
		nAri.setFont(new Font("Arial Black", 16));
		Text dAri= new Text("Designer and creator of The Organizator \nStudent at University of Bologna, Information Science for Managment");
		dAri.setWrappingWidth(350);
		dAri.setFont(fText);
		Text cAri= new Text("email: arianna.arruzzoli@studio.unibo.it");
		cAri.setFont(new Font("Arial", 12));
		VBox textA= new VBox(nAri,dAri,cAri);
		hbInfoAri.getChildren().addAll(pAri, textA);
		HBox hbInfoAle= new HBox(10);
		ImageView pAle= new ImageView(new Image ("img/aleMondini.jpg"));
		pAle.setSmooth(true);
		pAle.setId("imgAuthor");
		pAle.setFitHeight(120);
		pAle.setPreserveRatio(true);
		Text nAle= new Text("Alessandro Mondini");
		nAle.setFont(new Font("Arial Black", 16));
		Text dAle= new Text("Technician and Telegram supervisor \nStudent at University of Bologna, Information Science for Managment");
		dAle.setWrappingWidth(350);
		dAle.setFont(fText);
		Text cAle= new Text("email: alessandro.mondini3@studio.unibo.it");
		cAle.setFont(new Font("Arial", 12));
		VBox textB= new VBox(nAle,dAle,cAle);
		hbInfoAle.getChildren().addAll(pAle, textB);
		HBox hbAuthors= new HBox(hbInfoAri, hbInfoAle);
		hbAuthors.setSpacing(10);
		hbAuthors.setAlignment(Pos.CENTER);
		VBox page= new VBox(12);
		page.getChildren().addAll(hbInfoOrg, hbInfoPat, divL, div, hbAuthors);
		page.setAlignment(Pos.CENTER);
		spAU= new StackPane(page);
		spAU.setPrefSize(1200, 750);
		return spAU;
	}
	
	public Scene getAboutScene() {
		return aboutScene;
	}
}
