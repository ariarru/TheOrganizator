package activities;


import java.io.File;

import Main.Principale;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import layout.Block;
import layout.topBorder;

public class AllCategoriesPage {
	private File css = new File("layout.css");
	public static BorderPane allCategoryPane;
	private Scene allCategoriesScene;
	
	//presenta l'insieme delle categorie
	public AllCategoriesPage(){
		allCategoryPane= new BorderPane();
		allCategoryPane.setTop(new topBorder("Categories").getTop());
		allCategoryPane.setCenter(generatePane());
		allCategoriesScene= new Scene(allCategoryPane, 1200, 750);
		allCategoriesScene.getStylesheets().add("file://" + css.getAbsolutePath());
		Principale.map.put("AllCategories", allCategoriesScene);
	}
	
	private ScrollPane generatePane() {
		VBox vb= new VBox();
		Category elenco= new Category("ElencoCategorie.txt");
		elenco.generateWorkspaces();
		int counter=1;
		for(String category: elenco.getCategories()) {
			String path="img/"+category+".jpg";
			Block b;
			try {
				b= new Block(path);
				b.setNewWidth(225);
			}
			catch(IllegalArgumentException e) {
				b= new Block("img/category.png");
				b.setNewWidth(50);
			}
			
			Text name= new Text(category);
			name.setFont(new Font("Cathlyne", 55));
			HBox hb= new HBox();
			hb.setAlignment(Pos.CENTER);
			hb.setSpacing(50);
			if(counter==1) {
				hb.getChildren().addAll(b.getBlock(), name);
				counter--;
			}
			else {
				hb.getChildren().addAll(name, b.getBlock());
				counter++;
			}
			hb.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent e) {
					Principale.setNew(category);
				}
			});
			Line divisor= new Line(-400,0,400,0); //start x,start y, end x, end y
			divisor.setStroke(Color.web("#bb2a32"));
			divisor.setStrokeWidth(3.5);
			vb.getChildren().addAll(hb, divisor);
			vb.setAlignment(Pos.CENTER);
			vb.setSpacing(10);
			
		}
		ScrollPane scroll= new ScrollPane(vb);
		scroll.setFitToWidth(true);
		scroll.setFitToHeight(true);
		scroll.setStyle("-fx-background-color: #f8fdf7;");
		return scroll;
	}
	
	public Scene getAllCategoriesScene() {
		return allCategoriesScene;
	}
	public BorderPane getAllCategoriesPane() {
		return allCategoryPane;
	}
	//aggiorna il topBorder
	public static void updateTop() {
		allCategoryPane= new BorderPane();
		allCategoryPane.setTop(new topBorder("About Us").getTop());
		Category elenco= new Category("ElencoCategorie.txt");
		elenco.generateWorkspaces();
	}
}
