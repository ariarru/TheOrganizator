package layout;

import activities.Activity;
import activities.ActivityInterface;
import javafx.event.EventHandler;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Block{
	
	private ImageView img= new ImageView();
	private StackPane sp= new StackPane(img);
	private Text title= new Text();
	
	
	//costruttore per immagini di presentazione categorie
	public Block(String url, String text) {
		this.title.setText(text);
		this.sp.getChildren().add(title);
		this.title.setVisible(false);
		this.img.setImage(new Image(url));
		img.setPreserveRatio(true);
		img.setFitWidth(300);
		img.setSmooth(true);
		DropShadow ds= new DropShadow(8, 2,0, Color.web("#1D3557"));
		img.setEffect(ds);
		
		title.setFont(new Font("Arial", 22));
		title.setFill(Color.WHITE);
		
		img.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				img.setEffect(new BoxBlur(2, 2, 2));
				title.setVisible(true);
				
			}
		});
		img.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				title.setVisible(false);
				img.setEffect(new BoxBlur(0,0,0));
				img.setEffect(ds);
			}
		});
			
	}
	
	//costruttore per semplici immagini
	public Block(String url) {
		this.img.setImage(new Image(url));
		img.setPreserveRatio(true);
		img.setFitWidth(300);
		img.setSmooth(true);
		DropShadow ds= new DropShadow(8, 2,0, Color.web("#1D3557"));
		img.setEffect(ds); //forse
	}
	
	//costruttore per la disposizione di effettive attività
	public Block(Activity a) {
		this.img.setImage(new Image(a.getUrl()));
		img.setPreserveRatio(true);
		img.setFitWidth(300);
		img.setSmooth(true);
		DropShadow ds= new DropShadow(8, 2,0, Color.web("#1D3557"));
		img.setEffect(ds); 
		img.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				ActivityInterface ai=new ActivityInterface(a);
				ai.showInfo(a);
			}
		});
	}
	
	public void setNewWidth(double d) {
		img.setFitWidth(d);
	}
	
	public StackPane getBlock() {
		return sp;
	}
	public ImageView getImg() {
		return img;
	}
	
	public void setNewImg(String newUrl) {
		this.img.setImage(new Image(newUrl));
	}
	
	public void newTitle(String text) {
		this.title.setText(text);
	}
	
	
}
