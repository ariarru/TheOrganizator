package layout;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class Icon {
	
	private Button icona= new Button();
	private ImageView imgIcon;
	private String onColor="#F2929A;";
	
	//bottone icona
	public Icon(String url) {
	imgIcon= new ImageView(url);
	setWidth(25);
	icona.setId("Icon");
	icona.setOnMouseEntered(mouseOnIcon());
	icona.setOnMouseExited(new EventHandler<MouseEvent>() {  
		public void handle(MouseEvent arg0){
			icona.setStyle("-fx-background-color: transparent;");
		}
	});
	}
	
	public Button getIcon() {
		return icona;
	}
	//imposta nuova dimensione per l'icon
	public void setWidth(double d) {
		imgIcon.setFitWidth(d);
		imgIcon.setPreserveRatio(true);
		icona.setGraphic(imgIcon);
	}
	//cambia colore dell'alone
	public void changeColor(String newColor) {
		onColor= newColor;
	}
	//cambia l'immagine
	public void setNewGraphic(String url) {
		imgIcon= new ImageView(url);
		setWidth(25);
	}
	
	//crea l'alone intorno all'immagine
	public EventHandler<MouseEvent> mouseOnIcon() {
		EventHandler<MouseEvent> changeOnColor= new EventHandler<MouseEvent>() {  
			public void handle(MouseEvent arg0){
				icona.setStyle("-fx-background-color: "+onColor+";");
			}
		};
		return changeOnColor;
		
	}
	
}
