package layout;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class BottomLogo {
	
	private StackPane sp= new StackPane();
	
	//serve per il logo
	//consigliato metterlo sempre nel Rigth del BorderPane
	public BottomLogo() {
		ImageView logo= new ImageView("img/logo.png");
		logo.setFitHeight(20);
		logo.setPreserveRatio(true);
		sp.getChildren().add(logo);
		StackPane.setAlignment(logo, Pos.BOTTOM_RIGHT);
	}
	
	
	public StackPane getBottomLogo() {
		return sp;
	}
	
	
}
