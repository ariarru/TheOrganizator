package layout;

import Main.Principale;
import Main.Indice;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import login.Login;

public class topBorder {
	
	private HBox hbTitle;
	private final Text mainTitle= new Text("The Organizator");
	private Label namePage;
	
	public topBorder(String labelNamePage) {
		hbTitle=new HBox();
		namePage= new Label();
		namePage.setText(labelNamePage);
		namePage.setId("subTitle");
		mainTitle.setId("title");
		generateTopBorder();
	}
	
	private void generateTopBorder() {
		HBox hb1= new HBox(10);
		hb1.getChildren().addAll(new Menu().getMenu(), namePage);
		hb1.setAlignment(Pos.CENTER);
		hb1.setMaxWidth(250);
		HBox title= new HBox();
		title.getChildren().add(mainTitle);
		title.setMaxWidth(1500);
		HBox.setHgrow(title, Priority.ALWAYS);
		title.setAlignment(Pos.CENTER);
		
		HBox hb2=new HBox();
		if(Principale.isLogged()) {
			Icon logOutIcon= new Icon("img/logIn.png");
			logOutIcon.setWidth(25);
			logOutIcon.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					Principale.setLoggedStatus(false, null);
					for(int i=0; i<Indice.values().length; i++) {
						if((Indice.values()[i]+"").equals("Login"))
							i++;
						Menu.updateTop(Indice.values()[i]+"");
					}
					Principale.map.put(""+Indice.Login, new Login().getLoginScene());
					Principale.setNew(""+Indice.Login);
				}
			});
			hb2.getChildren().add(logOutIcon.getIcon());
		}
		else {
			Icon logInIcon= new Icon("img/logIn.png");
			logInIcon.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					Principale.map.put(""+Indice.Login, new Login().getLoginScene());
					Principale.setNew(""+Indice.Login);
				}

			});
			hb2.getChildren().add(logInIcon.getIcon());
		}
		hb2.setAlignment(Pos.CENTER_RIGHT);
		hb2.setMaxWidth(150);
		HBox.setHgrow(hb2, Priority.ALWAYS);
		
		hbTitle.getChildren().addAll(hb1, title, hb2); 
		hbTitle.setAlignment(Pos.CENTER);
		hbTitle.setId("hbTitle");
		
		
	}
	public HBox getTop() {
		return hbTitle;
	}
	
}
