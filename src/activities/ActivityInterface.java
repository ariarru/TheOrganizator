package activities;

import java.io.*;

import Main.Principale;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import layout.Icon;

public class ActivityInterface {
	
	private Activity a;
	private VBox layout= new VBox();
	private File css = new File("layout.css");
	private Stage activityWindow;
	private char letter;
	
	public ActivityInterface(Activity a) {
		this.a=a;
		activityDesign();
	}
	
	//crea StackPane con informazioni
	private void activityDesign() { 
		layout.getChildren().clear();
		layout.setId("infoWindow");
		layout.setSpacing(10);
		Label lName= new Label("Activity: ");
		HBox hb1= new HBox(lName);
		Text tName= new Text(a.getName());
		hb1.getChildren().add(tName);
		HBox header= new HBox(hb1);
		if(Principale.isLogged() && Principale.getLoggedUser().isAdmin()) {
			Icon delete= new Icon("img/delete2.png");
			delete.setWidth(25);
			delete.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me){
					GestioneAttivitaAdmin.deleteActivity(a);
				}
			});
			Icon edit= new Icon("img/edit.png");
			edit.setWidth(25);
			edit.getIcon().setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me){
					edit();
				}
			});
			HBox hbBtns= new HBox(edit.getIcon(), delete.getIcon());
			hbBtns.setSpacing(10);
			header.getChildren().add(hbBtns);
		}
		header.setSpacing(55);
		Label lLuogo= new Label("Where: ");
		Text tLuogo= new Text(a.getWhere());
		HBox hb2= new HBox(lLuogo, tLuogo);
		Label lDescrizione= new Label("Description: ");
		String tD="";
		if(a.getDescription()==null) {
			tD= "Mistery event, find every specific the day of the event!";
		}
		else {
			tD=a.getDescription();
		}
		Text tDescrizione= new Text(tD);
		tDescrizione.setWrappingWidth(175);
		HBox hb7= new HBox(lDescrizione, tDescrizione);
		Label lMaterials= new Label("Materials: ");
		String tM="";
		if(a.getMaterials()==null) {
			tM= "You'll just need your enthusiasm ;)";
		}
		else {
			tM=a.getMaterials();
		}
		Text tMaterials= new Text(tM);
		tMaterials.setWrappingWidth(175);
		HBox hb8= new HBox(lMaterials, tMaterials);
		Text maxNum= new Text("This activity may have a maximum of "+ a.getMaxIscrizioni()+" people.");
		maxNum.setWrappingWidth(175);
		GridPane gridOptions= new GridPane();
		int riga=0, colonna=0;
		for(Opzione o: a.getOptions()) {
			Label lData= new Label("When: ");
			Text tGiorno= new Text(o.getGiorno());
			HBox hb3= new HBox(lData, tGiorno);
			Label lOrario= new Label("Time: ");
			Text tOrario= new Text(o.getOrario().toString());
			HBox hb4= new HBox(lOrario, tOrario);
			Label lId= new Label("ID: ");
			Text tID= new Text(o.getLetter());
			HBox hb5= new HBox(lId, tID);
			VBox vb= new VBox(hb3, hb4, hb5);
			vb.setSpacing(2);
			if(Principale.isLogged() && !(Principale.getLoggedUser().isAdmin())) {
				Button subscribe= new Button("Subscribe");
				subscribe.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent me) {
						GestionePreferenze.subscribe(a,o);
						activityWindow.close();
					}	
				});
				subscribe.setId("pActBtn");
				HBox hb6= new HBox(subscribe);
				hb6.setSpacing(10);
				vb.getChildren().addAll(hb6);
			}
			else {
				Line divisor= new Line(-125,0,130,0); //start x,start y, end x, end y
				divisor.setStroke(Color.web("#96ba7e"));
				divisor.setStrokeWidth(3.5);
				vb.getChildren().add(divisor);
			}
			
			gridOptions.add(vb, riga, colonna);
			colonna++;
			if(colonna==3) {
				colonna=0;
				riga++;
			}
		}
		layout.setAlignment(Pos.TOP_CENTER);
		layout.getChildren().addAll(header, hb2, hb7, hb8, maxNum, gridOptions);
	}
	
	//mostra le informazioni in una finestra popup
	public void showInfo(Activity a) {
		StackPane.setAlignment(layout, Pos.TOP_CENTER);
		Button cancel= new Button("Cancel");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				activityWindow.close();
			}
		});
		cancel.setId("pActBtn");
		layout.getChildren().add(cancel);
		ScrollPane scroll= new ScrollPane(layout);
		scroll.setFitToWidth(true);
		scroll.setStyle("-fx-background-color: transparent;");
		Scene infoScene=new Scene(scroll);
		infoScene.getStylesheets().add("file://" + css.getAbsolutePath());
		activityWindow= new Stage();
		activityWindow.setScene(infoScene);
		activityWindow.show();

	}
	
	//modifica le informaizoni dell'attività
	private void edit() {
		layout.getChildren().clear();
		layout.setId("infoWindow");
		layout.setSpacing(10);
		Label lName= new Label("Activity: ");
		HBox hb1= new HBox(lName);
		TextArea tName= new TextArea();
		tName.setText(a.getName());
		tName.setMaxSize(200, 25);
		hb1.getChildren().add(tName);
		Label lLuogo= new Label("Where: ");
		TextArea tLuogo= new TextArea(a.getWhere());
		tLuogo.setPromptText(a.getName());
		tLuogo.setMaxSize(200, 25);
		HBox hb2= new HBox(lLuogo, tLuogo);
		Label lDescrizione= new Label("Description: ");
		String tD="";
		if(a.getDescription()==null) {
			tD= "Mistery event, find every specific the day of the event!";
		}
		else {
			tD=a.getDescription();
		}
		TextArea tDescrizione= new TextArea();
		tDescrizione.setText(tD);
		tDescrizione.setMaxSize(200, 25);
		HBox hb7= new HBox(lDescrizione, tDescrizione);
		Label lMaterials= new Label("Materials: ");
		String tM="";
		if(a.getMaterials()==null) {
			tM= "You'll just need your enthusiasm ;)";
		}
		else {
			tM=a.getMaterials();
		}
		TextArea tMaterials= new TextArea();
		tMaterials.setText(tM);
		tMaterials.setMaxSize(200, 25);
		HBox hb8= new HBox(lMaterials, tMaterials);
		Text maxNumText= new Text("This activity may have a maximum of ");
		TextArea maxNum= new TextArea();
		maxNum.setText(""+a.getMaxIscrizioni());
		maxNum.setMaxSize(70, 25);
		HBox hb9= new HBox(maxNumText, maxNum);
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
		activityOptions.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		activityOptions.setMaxWidth(400);
		activityOptions.setMaxHeight(300);
		for(Opzione o: a.getOptions()) {
			activityOptions.getItems().add(o);
			letter= o.getLetter().charAt(0);
		}
		letter++;
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
		Text tLetter= new Text(""+letter);
		HBox hb5= new HBox(lLetter, tLetter);
		VBox vbOptions= new VBox(hb3, hb4, hb5);
		Button addOpt= new Button("Add option");
		addOpt.setId("logButton");
		addOpt.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(tGiorno.getText()=="" ||tGiorno.getText()==null ||tFrom.getText()== "" || tFrom.getText()== null ||tTo.getText()== ""|| tTo.getText()== null) {
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
				a.removeOptions(opz);
			}
		});
		deleteOpz.setId("pActBtn");
		HBox hbOptions= new HBox(addOpt, deleteOpz);
		hbOptions.setSpacing(10);
		vbOptions.getChildren().addAll(hbOptions);
		Button ok= new Button("Ok");
		ok.setId("logButton");
		ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if(!(tName.getText().equals(a.getName())) && (!(tName.getText().equals("")) || !(tName.getText().equals(null))))
					a.setName(tName.getText());
				if(!(tLuogo.getText().equals(a.getWhere())) && (!(tLuogo.getText().equals("")) || !(tLuogo.getText().equals(null))))
					a.setWhere(tLuogo.getText());
				if(tDescrizione.getText().equals(a.getDescription()))
					a.setDescription(tDescrizione.getText());
				if(tMaterials.getText()!= a.getMaterials())
					a.setMaterials(tMaterials.getText());
				if(!(maxNum.getText().equals(""+a.getMaxIscrizioni())) && (!(maxNum.getText().equals("")) || !(maxNum.getText().equals(null)))) {
					//controlla che sia effettivamente un numero
					boolean numero=false;
					for(int c=0; c<maxNum.getText().length(); c++) {
						if(maxNum.getText().charAt(c)>47 && maxNum.getText().charAt(c)<58)
							numero=true;
						else
							numero=false;
					}
					if(numero)
						a.setMaxIscrizioni(Integer.parseInt(maxNum.getText()));
				}
				GestioneAttivitaAdmin.updateActivity(a);
			}
		});
		Button cancel= new Button("Cancel");
		cancel.setId("logButton");
		cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				activityDesign();
			}
		});
		HBox hb10= new HBox(ok, cancel);
		layout.setAlignment(Pos.TOP_CENTER);
		layout.getChildren().addAll(hb1, hb2, hb7, hb8, hb9,activityOptions, vbOptions, hb10);
		
	}
	
	public VBox getLayout() {
		return layout;
	}
	
}
