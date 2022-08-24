package layout;

import Main.Indice;

import Main.Principale;
import activities.AllCategoriesPage;
import home.AdminPage;
import home.Home;
import home.UserPage;
import infos.AboutUs;
import infos.FAQpage;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import login.Login;

public class Menu {
	
	private MenuButton menu;
	
	public Menu() {
		menu= new MenuButton();
		menu.setId("menuButton");
		ImageView img=new ImageView(new Image("img/menuIcon.png"));
		img.setFitHeight(25);
		img.setFitWidth(25);
		menu.setGraphic(img);
		MenuItem elementIndice;
		for(int i=0; i<Indice.values().length; i++) {
			try {
			if(Principale.isLogged() && Principale.getLoggedUser().isAdmin()) {
				if((Indice.values()[i]+"").equals("User")) //gli admin non vedono quello degli user 
					i++;
			}
			else if(Principale.isLogged()){
				if((Indice.values()[i]+"").equals("Admin")) 
					i++;
			}
			else {
				if((Indice.values()[i]+"").equals("Admin")) 
					break;
			}
			if(i==Indice.values().length)
				break;
			String value=Indice.values()[i]+"";
			elementIndice= new MenuItem(value);
			menu.getItems().add(elementIndice);
			elementIndice.setOnAction( e -> {
				//perchè le categorie e gli workspace vengono creati all'inizio
				if(value.equals("Categories")) {
					Principale.map.putIfAbsent(value, new AllCategoriesPage().getAllCategoriesScene());
				}
				else {
					Principale.map.put(value, changeIntoScene(value));
				}
				Principale.setNew(value);
			});
			} catch(NullPointerException e) {
				System.out.println(e+ "in menu item home");
			}
		}		
		menu.setOnMouseEntered(new EventHandler<MouseEvent>() {  
			public void handle(MouseEvent e){
				menu.setStyle("-fx-background-color: #F2929A;");
			}
		});
		menu.setOnMouseExited(new EventHandler<MouseEvent>() {  
			public void handle(MouseEvent e){
				menu.setStyle("-fx-background-color: transparent;");
			}
		});

	}
	
	public MenuButton getMenu() {
		return menu;
	}
	
	private Scene changeIntoScene(String sceneName) {
		switch(sceneName) {
		case "Home": return Principale.s1;
		case "Login": return new Login().getLoginScene();
		case "FAQ": return new FAQpage().getFaqScene();
		case "AboutUs": return new AboutUs().getAboutScene();
		case "Admin": return new AdminPage().getAdminScene();
		case "User": return new UserPage().getUserScene();
		default: return Principale.s1;
		}
	}
	
	public static void updateTop(String sceneName) {
		switch(sceneName) {
		case "Home": Home.updateTop();
		case "Login": Login.updateTop();
		case "Categories": AllCategoriesPage.updateTop();
		case "FAQ": FAQpage.updateTop();
		case "AboutUs": AboutUs.updateTop();
		case "Admin": AdminPage.updateTop();
		}
	}
	
	

}

/* Menu crea dei Menù a tendina, MenuItem è un oggetto dentro i Menù che però ha solo il setOnAction */
