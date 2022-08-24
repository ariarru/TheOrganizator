package infos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import layout.topBorder;


public class FAQpage {
	
	
	private ArrayList<String> faqs= new ArrayList<String>(); 
	private static BorderPane bPane;
	private Scene faqScene;
	private File css = new File("layout.css");
	private static Path path= Paths.get("FAQ.json");
	private static String pathStr=""+path.toAbsolutePath();
	
	public FAQpage() {
		bPane= new BorderPane();
		bPane.setTop(new topBorder("FAQs").getTop());
		generatePage();
		faqScene= new Scene(bPane, 1200, 750);
		faqScene.getStylesheets().add("file://" + css.getAbsolutePath());
		
	}
	//aggiorna il topBorder
	public static void updateTop() {
		bPane= new BorderPane();
		bPane.setTop(new topBorder("FAQs").getTop());
	}
	//legge dal json
	private void readFromJson() {
		try {
			JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
			JsonArray jsonArr= (JsonArray) jsonObj.get("FAQs"); 
			for(JsonElement a: jsonArr) { //per ogni oggettto 
				//prendi domanda
				String domanda= a.getAsJsonObject().get("question").getAsString();
				faqs.add(domanda);
				//prendi risposta
				String risposta= a.getAsJsonObject().get("answer").getAsString();
				faqs.add(risposta);
			}
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in generating FAQs");

		}
	}
	
	private void generatePage() {
		StackPane header = new StackPane();
		ImageView imgF= new ImageView(new Image("img/faqs.png"));
		imgF.setPreserveRatio(true);
		imgF.setFitWidth(975);
		Text faqTitle= new Text("Frequently Asked Questions");
		faqTitle.setFont(new Font("Copperplate", 40));
		faqTitle.setFill(Color.web("#080db1"));
		Rectangle rH= new Rectangle(650, 70); //double width, double heigth
		rH.setFill(Color.web("#eaeff3"));
		rH.setEffect(new DropShadow(8, 2, 0, Color.web("#3d5a80")));
		StackPane title = new StackPane();
		title.getChildren().addAll(rH, faqTitle);
		header.getChildren().addAll(imgF, title);
		header.setAlignment(imgF, Pos.TOP_CENTER);
		header.setPrefHeight(60);
		VBox vb= new VBox(5);
		vb.getChildren().add(header);
		vb.setAlignment(Pos.TOP_CENTER);
		readFromJson();
		for(int d=0; d< faqs.size(); d++) {
			Text fTitle= new Text("•"+faqs.get(d));
			fTitle.setId("titleF");
			d++;
			Text fText= new Text(faqs.get(d));
			fText.setId("textF");
			fText.setWrappingWidth(700);
			vb.getChildren().addAll(fTitle, fText);
		}
		ScrollPane scroll= new ScrollPane(vb);
		scroll.setFitToWidth(true);
		scroll.setStyle("-fx-background-color: #f8fdf7;");
		bPane.setCenter(scroll);
		
	}

	public Scene getFaqScene() {
		return faqScene;
	}


}
