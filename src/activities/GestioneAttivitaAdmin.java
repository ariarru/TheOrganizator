package activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Main.Principale;
import home.AdminPage;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import javafx.stage.Stage;
import layout.Icon;

public class GestioneAttivitaAdmin {
	
	private File css = new File("layout.css");
	private GridPane grid;
	private VBox vb;
	private static ArrayList<Activity> allActivities= new ArrayList<Activity>(); //contiene tutte le attività
	private static PrintWriter pWriter;
	private Stage addActivityWindow;
	private char letter;
	
	public GestioneAttivitaAdmin() {
		generate();
	}
	//genera la schermata in cui vengono mostrate le attività
	private void generate() {
		grid= new GridPane();
		vb= new VBox(grid);
		grid.setPadding(new Insets(10,10,10,10));
		grid.setHgap(10);
		grid.setVgap(15);
		int colonna=0, riga=0;
		Category c= Principale.getCategory();
		for(int i=0; i<c.getCategories().size(); i++) {
			String category= c.getCategories().get(i);
			grid.add(getTitle(category), colonna, riga);
			riga++;
			//aggiunge sotto le categorie
			readAllActivities();
			ArrayList<Activity> activities= getCategoryActivity(category);
			for(int a=0; a<activities.size(); a++) {
				grid.add(new ActivityInterface(activities.get(a)).getLayout(), colonna, riga);
				riga++;
			}
			Icon add2= new Icon("img/add.png");
			add2.setWidth(40);
			add2.changeColor("#CBE8B8");
			add2.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					addActivityWindow(category);
				}
			});
			grid.add(add2.getIcon(), colonna, riga);
			grid.setAlignment(Pos.TOP_CENTER);
			colonna++;
			riga=0;	
		}
		Icon add= new Icon("img/add.png");
		add.setWidth(40);
		add.changeColor("#CBE8B8");
		add.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addCategoryWindow();
			}
		});
		StackPane spAdd= new StackPane(add.getIcon());
		StackPane.setAlignment(add.getIcon(), Pos.CENTER);
		grid.add(spAdd, colonna, riga);grid.setAlignment(Pos.CENTER);
	}
	
	//crea blocco per scritta con nome categoria
	private StackPane getTitle(String title) {
		Text catTitle= new Text(title);
		catTitle.setFont(new Font("Cathlyne", 28));
		catTitle.setFill(Color.web("#000000"));
		StackPane sp= new StackPane(catTitle);
		sp.setPrefSize(200, 5);
		sp.setStyle("-fx-background-color: #afd397");
		return sp;
	}
	
	public ScrollPane getContent() {
		ScrollPane sp= new ScrollPane(vb);
		return sp;
	}
	//legge le attività dal file
	private static void readAllActivities() {
		try {
			Category c= Principale.getCategory();
			allActivities.clear();
			for(int i=0; i<c.getCategories().size() ; i++) {
				String category=c.getCategories().get(i);
				Path path= Paths.get(category+".json");
				String pathStr=""+path.toAbsolutePath();
				JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
				JsonArray jsonArr= (JsonArray) jsonObj.get(c.getCategories().get(i)); //array con attività della categoria
				for(JsonElement a: jsonArr) { //per ogni attività della categoria
					String idAct= a.getAsJsonObject().get("IDact").getAsString();
					JsonArray activityJ= (JsonArray) a.getAsJsonObject().getAsJsonArray("activity"); //contiene informazioni e opzioni
					for(int j=0; j<activityJ.size(); j++) {
						JsonObject dbActivity= (JsonObject) activityJ.get(j);
						Activity act = new Activity(idAct, dbActivity.get("name").getAsString(), dbActivity.get("where").getAsString());
						if(dbActivity.get("url").isJsonNull()) {
							act.setImageUrl("null");
						}
						else {
							act.setImageUrl(dbActivity.get("url").getAsString());
						}
						if(!(dbActivity.get("maxIscrizioni").isJsonNull())) {
							act.setMaxIscrizioni(dbActivity.get("maxIscrizioni").getAsInt());
						}
						if(!(dbActivity.get("descrizione").isJsonNull())) {
							act.setDescription(dbActivity.get("descrizione").getAsString());
						}
						if(!(dbActivity.get("materiali").isJsonNull())) {
							act.setMaterials(dbActivity.get("materiali").getAsString());
						}
						JsonArray optionsJ= (JsonArray) dbActivity.get("options");
						if(!(optionsJ.isJsonNull()))
							act.setOptions(optionsJ);
						allActivities.add(act);
					}
				}
			}
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in generating activities managment");
		}
	}
	//restituisce l'array con tutte le attività
	public static ArrayList<Activity> getAllActivities(){
		readAllActivities();
		return allActivities;
	}
	//genera la finestra per aggiungere una categoria
	private void addCategoryWindow() {
		Stage addCategoryWindow= new Stage();
		Text title= new Text("New Category");
		title.setId("textPane");
		VBox layout= new VBox(title);
		layout.setAlignment(Pos.CENTER);
		layout.setId("scenePane");
		layout.setSpacing(10);
		Scene addCategoryScene= new Scene(layout, 250,300);
		addCategoryScene.getStylesheets().add("file://" + css.getAbsolutePath());
		Label lT= new Label("Name: ");
		lT.setId("textPane");
		TextArea newName= new TextArea();
		newName.setMaxSize(110, 50);
		newName.setFont(new Font("Arial", 18));
		HBox hb1= new HBox(lT, newName);
		hb1.setSpacing(10);
		hb1.setAlignment(Pos.CENTER);
		Button cancel= new Button("Cancel");
		Button done= new Button("Done");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addCategoryWindow.close();
			}
		});
		done.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addCategory(newName.getText());
				addCategoryWindow.close();
			}	
		});
		HBox hb2= new HBox(cancel, done);
		hb2.setSpacing(13);
		Button addFromFile= new Button("Add from file...");
		addFromFile.setId("logButton");
		addFromFile.setStyle("-fx-background-color: #64a4b4;");
		addFromFile.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("File Json", "*.json"));
				Stage fileChooserWindow=new Stage();
				File selectedFile = fileChooser.showOpenDialog(fileChooserWindow);
				if (selectedFile != null && selectedFile.canRead()) {
					String name=selectedFile.getName();
					String newCategory="file:"+ selectedFile.getAbsolutePath();
					addCategoryFromFile(name, newCategory);
					addCategoryWindow.close();
				}
			}
		});
		layout.getChildren().addAll(hb1, hb2, addFromFile);
		addCategoryWindow.setScene(addCategoryScene);
		addCategoryWindow.show();
		
	}
	
	//aggiunge una categoria all'elenco nel file
	private void addCategory(String categoryName) {
		//prima sistemiamo la stringa
		categoryName=categoryName.trim();
		String iniziale= categoryName.substring(0, 1);
		iniziale=iniziale.toUpperCase();
		String newCategoryName= iniziale+categoryName.substring(1, categoryName.length());
		try {
			Path path= Paths.get("ElencoCategorie.txt");
			String pathStr=""+path.toAbsolutePath();
			RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); 
			long posizioneCursore= rAF.length();
			rAF.seek(posizioneCursore);
			rAF.writeBytes(", "+ newCategoryName);
			rAF.close();
			//crea direttamente il file
			path=Paths.get(newCategoryName+".json");
			pathStr=""+path.toAbsolutePath();
			pWriter= new PrintWriter(pathStr);
			Principale.updateCategories();
			addActivityWindow(newCategoryName);
		} catch (FileNotFoundException e) {
			System.out.println(e+" in adding category");
		}
		catch (IOException e) {
			System.out.println(e+" in adding category");
		}
	}
	//aggiunge una categoria all'elenco nel file
		private void addCategoryFromFile(String categoryName, String percorso) {
			//prima sistemiamo la stringa
			categoryName=categoryName.trim();
			int dot= categoryName.indexOf('.');
			if(dot!=-1)
				categoryName=categoryName.substring(0, dot);
			String iniziale= categoryName.substring(0, 1);
			iniziale=iniziale.toUpperCase();
			String newCategoryName= iniziale+categoryName.substring(1, categoryName.length());
			try {
				Path path= Paths.get("ElencoCategorie.txt");
				String pathStr=""+path.toAbsolutePath();
				RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); 
				long posizioneCursore= rAF.length();
				rAF.seek(posizioneCursore);
				rAF.writeBytes(", "+ newCategoryName);
				rAF.close();
				Principale.updateCategories();
				
			} catch (FileNotFoundException e) {
				System.out.println(e+" in adding category from file");
			}
			catch (IOException e) {
				System.out.println(e+" in adding category from file");
			}
		}
	
	//finestra per aggiungere attività
	private void addActivityWindow(String categoryName) {
		addActivityWindow= new Stage();
		Text title= new Text("New Category");
		title.setId("textPane");
		Font f= new Font("Arial", 16);
		Label lID= new Label("ID: ");
		lID.setId("textPane");
		Text id= new Text(generateNewId(categoryName));
		id.setFont(f);
		Label lT= new Label("Name: ");
		lT.setId("textPane");
		TextArea newName= new TextArea();
		newName.setMaxSize(120, 40);
		newName.setFont(f);
		HBox hb1= new HBox(lT, newName);
		hb1.setSpacing(10);
		Label lW= new Label("Where: ");
		lW.setId("textPane");
		TextArea where= new TextArea();
		where.setMaxSize(120, 40);
		where.setFont(f);
		HBox hb2= new HBox(lW, where);
		hb2.setSpacing(10);
		Label lMI= new Label("Max subscriptions' number: ");
		lMI.setId("textPane");
		Label insertInt= new Label("Insert only numbers");
		VBox vbMI= new VBox(lMI, insertInt);
		TextArea numMax= new TextArea();
		numMax.setMaxSize(120, 40);
		numMax.setFont(f);
		HBox hb4= new HBox(vbMI, numMax);
		hb4.setSpacing(10);
		Label lD= new Label("Description: ");
		lD.setId("textPane");
		TextArea description= new TextArea();
		description.setMaxSize(120, 50);
		description.setFont(new Font("Arial", 18));
		HBox hb5= new HBox(lD, description);
		hb5.setSpacing(10);
		Label lM= new Label("Materials: ");
		lM.setId("textPane");
		TextArea materials= new TextArea();
		materials.setMaxSize(120, 50);
		materials.setFont(f);
		HBox hb6= new HBox(lM, materials);
		hb6.setSpacing(10);
		Button cancel= new Button("Cancel");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				addActivityWindow.close();
			}
		});
		cancel.setId("logButton");
		cancel.setStyle("-fx-background-color: #5590B4;");
		Button done= new Button("Done");
		done.setId("logButton");
		done.setStyle("-fx-background-color: #5590B4;");
		done.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(newName.getText()!="") {
					Activity newAct= new Activity(id.getText(), newName.getText(), where.getText());
					newAct.setMaxIscrizioni(numMax.getText());
					if(!(description.getText()==null)) 
						newAct.setDescription(description.getText());
					if(!(materials.getText()==null)) 
						newAct.setMaterials(materials.getText());
					addOptions(newAct, categoryName);
				}
			}	
		});
		HBox hb7= new HBox(cancel, done);
		hb7.setSpacing(10);
		VBox layout= new VBox(title, hb1, hb2, hb4, hb5, hb6, hb7);
		layout.setAlignment(Pos.CENTER);
		layout.setId("scenePane");
		layout.setSpacing(10);
		Scene addActivityScene= new Scene(layout, 250,500);
		addActivityScene.getStylesheets().add("file://" + css.getAbsolutePath());
		addActivityWindow.setScene(addActivityScene);
		addActivityWindow.show();
	}
	
	//aggiunge un'attività al file
	private void addActivity(Activity a, String category) {
		try {
			Path pathA= Paths.get(category+".json");
			String pathAStr=""+pathA.toAbsolutePath();
			RandomAccessFile rAF= new RandomAccessFile(new File(pathAStr), "rw");
			long posizioneCursore= rAF.length();
			boolean wasEmpty=false;
			if(posizioneCursore==0) { //il file è vuoto
				rAF.seek(posizioneCursore);
				rAF.writeBytes("{\""+category+"\":[ \n ]}");
				posizioneCursore= rAF.length();
				wasEmpty=true;
			}
			while(rAF.length()>0) { 
				posizioneCursore--;
				rAF.seek(posizioneCursore);
				if(rAF.readByte() == ']') {
					rAF.seek(posizioneCursore);
					break;
				}
			}
			if(!wasEmpty)
				rAF.writeBytes(", ");
			rAF.writeBytes("\n"+ a.intoJson()+ "\n\t]\n}");
		    rAF.close();
		} catch (FileNotFoundException e) {
			System.out.println(e+" in adding activity");
		}
		catch (IOException e) { 
			System.out.println(e+" in adding activity");
		}
	}
	
	//genera l'id per la nuova attività
	private String generateNewId(String category) {
		String iniziale= category.substring(0,1);
		iniziale= iniziale.toUpperCase();
		String ID= iniziale+"001";
		boolean found=false;
		for(Activity a: allActivities) {
			if(a.getId().contains(iniziale)) {
				ID=a.getId();
				found=true;
			}
		}
		if(found) {
			int ultimaCifraId= Integer.parseInt(ID.substring(ID.length()-1));
			ultimaCifraId++;
			ID= ID.substring(0,ID.length()-1)+ultimaCifraId;
		}
		return ID;
	}
	
	//finestra per aggiungere opzioni e immagine
	private void addOptions(Activity a, String c) {
		VBox vb= new VBox(10);
		vb.setId("infoWindow");
		Label lU= new Label("Image: ");
		lU.setId("textPane");
		Button addImage= new Button("Add image from browser");
		addImage.setId("logButton");
		addImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
				Stage fileChooserWindow=new Stage();
				File selectedFile = fileChooser.showOpenDialog(fileChooserWindow);
				if (selectedFile != null) {
					String newUrlImg="file:"+ selectedFile.getAbsolutePath();
					a.setImageUrl(newUrlImg);
				}
			}
		});
		HBox hb1= new HBox(lU, addImage);
		hb1.setSpacing(10);
		TableView<Opzione> activityOptions= new TableView<Opzione>();
		activityOptions.setEditable(true);
		TableColumn<Opzione, String> letterColumn= new TableColumn<Opzione, String>("Letter");
		letterColumn.setCellValueFactory(new Callback<CellDataFeatures<Opzione, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Opzione, String> o) {
		         return new ReadOnlyObjectWrapper(o.getValue().getLetter());
		     }
		  });
		TableColumn<Opzione, String> dayColumn= new TableColumn<Opzione, String>("Day");
		dayColumn.setCellValueFactory(new Callback<CellDataFeatures<Opzione, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<Opzione, String> o) {
		         return new ReadOnlyObjectWrapper(o.getValue().getGiorno());
		     }
		  });
		dayColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		dayColumn.setOnEditCommit(new EventHandler<CellEditEvent<Opzione, String>>(){
			@Override
			public void handle(CellEditEvent<Opzione, String> event) {
				Opzione opt= event.getRowValue();
				opt.setGiorno(event.getNewValue());
			}
			
		});
		TableColumn<Opzione, Integer> timeFromColumn= new TableColumn<Opzione, Integer>("From");
		timeFromColumn.setCellValueFactory(new Callback<CellDataFeatures<Opzione, Integer>, ObservableValue<Integer>>() {
		     public ObservableValue<Integer> call(CellDataFeatures<Opzione, Integer> o) {
		         return new ReadOnlyObjectWrapper(o.getValue().getOrario().getLower());
		     }
		  });
		timeFromColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		timeFromColumn.setOnEditCommit(new EventHandler<CellEditEvent<Opzione, Integer>>(){
			@Override
			public void handle(CellEditEvent<Opzione, Integer> event) {
				Opzione opt= event.getRowValue();
				Range r= new Range(event.getNewValue());
				opt.setOrario(r);
			}
			
		});
		TableColumn<Opzione, Integer> timeToColumn= new TableColumn<Opzione, Integer>("To");
		timeToColumn.setCellValueFactory(new Callback<CellDataFeatures<Opzione, Integer>, ObservableValue<Integer>>() {
		     public ObservableValue<Integer> call(CellDataFeatures<Opzione, Integer> o) {
		         return new ReadOnlyObjectWrapper(o.getValue().getOrario().getUpper());
		     }
		  });
		timeToColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		timeToColumn.setOnEditCommit(new EventHandler<CellEditEvent<Opzione, Integer>>(){
			@Override
			public void handle(CellEditEvent<Opzione, Integer> event) {
				Opzione opt= event.getRowValue();
				Range r= new Range(event.getNewValue());
			}
			
		});
		activityOptions.getColumns().addAll(letterColumn, dayColumn, timeFromColumn, timeToColumn);

		Label lData= new Label("When: ");
		TextArea tGiorno= new TextArea();
		tGiorno.setPrefSize(100, 25);
		HBox hb3= new HBox(lData, tGiorno);
		Label lOrario= new Label("Time: ");
		TextArea tFrom= new TextArea();
		tFrom.setPrefSize(50, 25);
		Label insertInt= new Label("Insert numbers between 0 and 24");
		Label lbl= new Label("-");
		TextArea tTo= new TextArea();
		tTo.setPrefSize(50, 25);
		HBox hb6 = new HBox(tFrom,lbl, tTo);
		VBox vbTime= new VBox(insertInt, hb6);
		HBox hb4= new HBox(lOrario,vbTime);
		hb4.setSpacing(5);
		Label lLetter= new Label("Letter: ");
		letter='A';
		Text tLetter= new Text(""+letter);
		HBox hb5= new HBox(lLetter, tLetter);
		VBox vbOptions= new VBox(hb3, hb4, hb5);
		Button addOpt= new Button("Add option");
		addOpt.setId("logButton");
		addOpt.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(tGiorno.getText().equals("") ||tGiorno.getText().equals(null) ||tFrom.getText().equals("")  || tFrom.getText().equals(null) ||tTo.getText().equals("") || tTo.getText().equals(null)) {
					System.out.println("valori non validi");
				}		
				else {
				String lettera= tLetter.getText();
				String orario= tFrom.getText()+"-"+tTo.getText();
				String giorno= tGiorno.getText();
				activityOptions.getItems().add(new Opzione(lettera, orario, giorno));
				letter++;
				tLetter.setText(""+letter);
				tFrom.setText("");
				tTo.setText("");
				tGiorno.setText("");
				}
			}	
		});
		Button deleteOpz= new Button("Delete selected");
		deleteOpz.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				Opzione opz=activityOptions.getSelectionModel().getSelectedItem();
				activityOptions.getItems().remove(opz);
			}
		});
		deleteOpz.setId("pActBtn");
		vbOptions.getChildren().addAll(addOpt, deleteOpz);
		Button done= new Button("Done");
		done.setId("logButton");
		done.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				for(Opzione opz: activityOptions.getItems()) {
					a.addOption(opz);
				}
				addActivity(a, c);
				addActivityWindow.close();
			}	
		});
		vb.getChildren().addAll(hb1, activityOptions, vbOptions,done);
		vb.setSpacing(15);
		Scene sceneOptions= new Scene(vb, 350, 500);
		sceneOptions.getStylesheets().add("file://" + css.getAbsolutePath());
		addActivityWindow.setScene(sceneOptions);
	}
	
	//filtra le attività per categoria
	public static ArrayList<Activity> getCategoryActivity(String categoryName){
		ArrayList<Activity> categoryActivities= new ArrayList<Activity>();
		String categoryInitial= categoryName.substring(0, 1);
		for(int h=0; h<allActivities.size(); h++) {
			String iniziale=allActivities.get(h).getId().substring(0, 1);
			if(categoryInitial.equals(iniziale)) {
				categoryActivities.add(allActivities.get(h));
			}
		}
		return categoryActivities;
	}
	
	
	//elimina un attività dalla categoria
	public static void deleteActivity(Activity a) {
		String inizialeCat= a.getId().substring(0, 1);
		Category c= new Category("ElencoCategorie.txt");
		String category="";
		//trova nome categoria
		for(int i=0; i<c.getCategories().size(); i++) {
			String inizialeCatElenco= c.getCategories().get(i).substring(0,1);
			if(inizialeCatElenco.equals(inizialeCat)) {
				category=c.getCategories().get(i);
			}
		}
		//elimina l'attività dall'elenco complessivo
		for(int j=0; j<allActivities.size(); j++) {
			if(allActivities.get(j).getId().equals(a.getId())){
				allActivities.remove(j);
			}
		}
		//prende le attività filtrate
		ArrayList<Activity> activities= getCategoryActivity(category);
		try {
			Path path= Paths.get(category+".json");
			String pathStr=""+path.toAbsolutePath();
			pWriter = new PrintWriter(new File(pathStr));
			pWriter.write("{\""+category+"\":[ \n");
			//trascrive solo quelle della categoria
			for(int h=0; h<activities.size(); h++) {
				pWriter.write(""+ activities.get(h).intoJson());
				if(h<activities.size()-1)
					pWriter.write(",\n");
				else
					pWriter.write("\n");

			}
			pWriter.write("]}");
			pWriter.close();
		} catch (FileNotFoundException e) {
			System.out.println(e+" in deleting activity");
		}
		catch (IOException e) { 
			System.out.println(e+" in deleting activity");
		}
		AdminPage.updateActivities();
	}
	
	//aggiorna l'attività salvata e riscrive il file
	public static void updateActivity(Activity a) {
		String inizialeCat= a.getId().substring(0, 1);
		Category c= new Category("ElencoCategorie.txt");
		String category="";
		//trova nome categoria
		for(int i=0; i<c.getCategories().size(); i++) {
			String inizialeCatElenco= c.getCategories().get(i).substring(0,1);
			if(inizialeCatElenco.equals(inizialeCat)) {
				category=c.getCategories().get(i);
			}
		}
		//elimina l'attività dall'elenco complessivo
		for(int j=0; j<allActivities.size(); j++) {
			if(allActivities.get(j).getId().equals(a.getId())){
				allActivities.set(j, a);
			}
		}
		//prende le attività filtrate
		ArrayList<Activity> activities= getCategoryActivity(category);
		try {
			Path path= Paths.get(category+".json");
			String pathStr=""+path.toAbsolutePath();
			pWriter = new PrintWriter(new File(pathStr));
			pWriter.write("{\""+category+"\":[ \n");
			//trascrive solo quelle della categoria
			for(int h=0; h<activities.size(); h++) {
				pWriter.write(""+ activities.get(h).intoJson());
				if(h<activities.size()-1)
					pWriter.write(",\n");
				else
					pWriter.write("\n");

			}
			pWriter.write("]}");
			pWriter.close();
		} catch (FileNotFoundException e) {
			System.out.println(e+" in updating activity");
		}
		catch (IOException e) { 
			System.out.println(e+" in updating activity");
		}
		AdminPage.updateActivities();
	}
}
	
