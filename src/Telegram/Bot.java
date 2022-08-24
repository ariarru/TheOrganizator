 package Telegram;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import activities.Activity;
import activities.Category;
import activities.Coppia;
import login.User;
import login.Utente;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;    



public class Bot extends TelegramLongPollingBot {

    private int status = -1; //0 = wait for command input, 1 = wait for input for one command, -1 = bot has a new chat/has restarted again so it will send the loginMenù
    private String lastCommand;
    private String text;
    private int logon = 0; // 0 = not logged; 1 = logged as user; 2 = logged as admin
    //private Handler handler = new Handler();
    private long chatId;
    private long messageId = -1;
    private long lastMessageId;
    private String lastText;
    private Command command;
    private String option;
    private String categoria;
    private String userID;

    public void setUserID(String userID){
        this.userID = userID;
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public int getLogon() {
        return logon;
    }

    public void setLogon(int logon) {
        this.logon = logon;
    }

    @Override
    public String getBotUsername() {
        
        return "PathonsBot";
    }

    @Override public void onUpdateReceived(Update update) {
        consoleLog("Update received!");
        //controlla subito se ha un callback query
        if(update.hasCallbackQuery()){
            deleteLastMessage();
            consoleLog("Update has callback");
            chatId = update.getCallbackQuery().getMessage().getChatId();
            messageId = update.getCallbackQuery().getMessage().getMessageId();
            consoleLog("with this data: chatid" + chatId+", messageId"+messageId);
            startCommand(update);
        }

        
        text = update.getMessage().getText();
        chatId = update.getMessage().getChatId();
        lastMessageId = update.getMessage().getMessageId();
        lastText = text;
        deleteLastMessage();
        consoleLog("text inside the update:" + text);

        if((lastText.equals("/home") || lastText.equals("/start"))){
            
            if(messageId == -1){
                consoleLog("Update has \"/help\" or \"/start\" and the chat doesn't have the first message, sending first menù" );
                messageId = lastMessageId;
                status = 0;
                sendMenu(1);
            } else {
                consoleLog("Update has \"/help\" or \"/start\" and the chat does have the first message, editing first menù" );
                status = 0;
                editMenu(1);
            }
            


        //checks if the status is in input mode for the command 

        } else if(status == 1){

            //calls the commandHandler method to continue the command or start a new one 
            consoleLog("Continuing the command");
            this.continueCommand(update);
        }
        
    }


    public void startCommand(Update update){
        consoleLog("Inside startCommand()");
        status = 1;
        lastCommand=update.getCallbackQuery().getData();
        option=null;
        //in case that che payload as an "option", like "categoria_fitness"
        if(lastCommand.indexOf("_")>0){
            option = lastCommand.split("_")[1];
            lastCommand = lastCommand.split("_")[0];
        }
        switch (lastCommand) {
            case "login":

                if(logon == 0){
                    consoleLog("Starting login command");
                    command = new Login();
                    command.command(update, this);
                }
                consoleLog("already logged!");
                break;
            
            case "sub":
                consoleLog("Starting sub command");
                command = new Subscribe();
                command.command(update, this);
                break;    
            
            case "home":
                
                consoleLog("Starting home command");
                status = 0;
                editMenu(1);
                break;
            
            case "user":
                consoleLog("Starting user command");
                status = 0;
                editMenu(7);
                break;
            case "categorie":
                
                consoleLog("Starting categorie command");
                status = 0;
                editMenu(2);
                break;

            case "categoria":

                consoleLog("Starting categoria command");
                status = 0;
                editMenu(3);
                break;

            case "attivita":
                consoleLog("Starting attivita command");
                status = 0;
                editMenu(4);
                break;
            
            case "addpreferenza":
                String activityID = option.split("-")[0];
                String lettera = option.split("-")[1];
                try {
                    Path path= Paths.get("Record.json");
                    String pathStr=""+path.toAbsolutePath();
                    RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); 
                    long posizioneCursore= rAF.length();
                    long stop;
                    while(rAF.length()>0) { 
                        consoleLog("Imrighthere");
                        posizioneCursore--;
                        rAF.seek(posizioneCursore);
                        if(rAF.readByte() == ']') {
                            stop=posizioneCursore;
                            while(posizioneCursore>1) { //serve per controllare se ci siano altre preferenze, in caso aggiunge la virgola
                                posizioneCursore--;
                                rAF.seek(posizioneCursore);
                                if(rAF.readByte() == '}') {
                                    rAF.seek(posizioneCursore);
                                    rAF.writeBytes("},\n");
                                    break;
                                }
                            }
                            rAF.seek(stop); //faccio in modo che il cursore sia dopo la virgola e \n
                            break;
                        }
                    }
                    
                    Coppia c= new Coppia(new User("nome","pass",userID), new Activity(activityID, "nome", "luogo"));
                    String json = c.intoJson()+ "\n\t \"opzione\":\""+ lettera+"\" } \n ] }"; //aggiungere lettera opzione
                    rAF.writeBytes(json);
                    rAF.close();

                }catch(IOException e) {
                    System.out.println(e+" writing preferences on json");
                }
                status = 0;
                editMenu(1);
                break;
            
            case "faq":

                consoleLog("starting faq command");
                status = 0;
                editMenu(6);
                break;
            
            case "addcategoria":
                consoleLog("starting addcategoria command");
                command = new AddCategory();
                command.command(update, this);
                break;
            
            case "addutente":
                consoleLog("starting addutente command");
                command = new AddUtenti();
                if(!option.equals(null)){
                    command.command(update, this, option);
                } else {
                    command.command(update, this);
                }
                
                break;
            
            case "addutenterichieste":
                consoleLog("starting addutenterichieste command");
                status = 0;
                editMenu(16);
                
                break;
            case "lista":
                consoleLog("starting listautenti command");
                status = 0;
                editMenu(15);
                break;

            case "editutente":
                consoleLog("starting editutente command");
			    try {
                    Path path= Paths.get("Access.json");
			        String pathStr=""+path.toAbsolutePath();
                    Object obj = JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray utenti= (JsonArray) jsonObj.get("users");
                    
                    for(int i=0; i<utenti.size(); i++) {
                        jsonObj= utenti.get(i).getAsJsonObject();
                        consoleLog(jsonObj.get("ID").getAsString());
                        if(jsonObj.get("ID").getAsString().equals(option)){
                            utenti.remove(i);
                            
                        }
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String elencoInJson= gson.toJson(utenti);
                    try {
                        path= Paths.get("Access.json");
                        pathStr=""+path.toAbsolutePath();
                        PrintWriter pw= new PrintWriter(pathStr);
                        pw.print("{ \n \"users\": "+ elencoInJson + "\n}");
                        pw.close();
                    } catch(FileNotFoundException e) {
                        System.out.println(e+ "in removing a pending requests");
                    }
                    command = new AddUtenti();
                    command.command(update, this);
                    
                } catch (JsonIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                

                break;
            case "editattivita":
                consoleLog("starting editattivita command");
                String category = option.split("-")[0];
                String ID = option.split("-")[1];
			    try {
                    Path path= Paths.get(category+".json");
                    String pathStr=""+path.toAbsolutePath();
                    Object obj = JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray attivita= (JsonArray) jsonObj.get(category);
                    
                    for(int i=0; i<attivita.size(); i++) {
                        jsonObj= attivita.get(i).getAsJsonObject();
                        if(jsonObj.get("IDact").getAsString().equals(ID)){
                            attivita.remove(i);
                            
                        }
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String elencoInJson= gson.toJson(attivita);
                    try {
                        
                        path= Paths.get(category+".json");
                        pathStr=""+path.toAbsolutePath();
                        PrintWriter pw= new PrintWriter(pathStr);
                        pw.print("{ \n \""+category+"\": "+ elencoInJson + "\n}");
                        pw.close();
                    } catch(FileNotFoundException e) {
                        System.out.println(e+ "in removing an activity");
                    }
                    command = new AddAttivita();
                    command.command(update, this, category);
                    
                } catch (JsonIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                

                break;

            case "removepreferenze":
                consoleLog("starting removepreferenze command");
                status = 0;
                editMenu(19);
                break;

            case "removepreferenza":
                consoleLog("starting removepreferenza command");
                String elencoInJson="";
		        String daEliminareId=option;
                try {
                    Path path= Paths.get("Record.json");
                    String pathStr=""+path.toAbsolutePath();
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray preferencesJ= (JsonArray) jsonObj.getAsJsonArray("preferences");
                    for(int p=0; p<preferencesJ.size(); p++) {
                        JsonObject dbPreference= (JsonObject) preferencesJ.get(p); //prende l'espressione della preferenza
                        JsonArray dbCoupleAsArr= (JsonArray) dbPreference.get("coppia").getAsJsonArray(); //prende il valore della coppia
                        JsonObject dbCouple= dbCoupleAsArr.get(0).getAsJsonObject();
                        if(daEliminareId.equals(dbCouple.get("activityID").getAsString()+dbPreference.get("opzione").getAsString())){
                            preferencesJ.remove(p);
                        }
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                     elencoInJson= gson.toJson(preferencesJ);
                    PrintWriter pw= new PrintWriter(pathStr);
                    pw.print("{ \n \"preferences\": "+ elencoInJson + "\n}");
                    pw.close();
                } catch(FileNotFoundException e) {
                    System.out.println(e+ "in removing a preference");
                }
                if(logon == 2){
                    editMenu(19);
                } else {
                    editMenu(7);
                }
                

                break;

            case "addattivita":
                consoleLog("starting addattivita command");
                if(option == null){
                    status = 0;
                    editMenu(18);
                    break;
                }
                command = new AddAttivita();
                command.command(update, this,option);
                
                break;
            
            case "removeattivita":
            consoleLog("starting removeattivita command");
            category = option.split("-")[0];
            ID = option.split("-")[1];
            
                try {
                    Path path= Paths.get(category+".json");
                    String pathStr=""+path.toAbsolutePath();
                    Object obj = JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray attivita= (JsonArray) jsonObj.get(category);
                    
                    for(int i=0; i<attivita.size(); i++) {
                        jsonObj= attivita.get(i).getAsJsonObject();
                        if(jsonObj.get("IDact").getAsString().equals(ID)){
                            attivita.remove(i);
                            
                        }
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                     elencoInJson= gson.toJson(attivita);
                    try {
                        
                        path= Paths.get(category+".json");
                        pathStr=""+path.toAbsolutePath();
                        PrintWriter pw= new PrintWriter(pathStr);
                        pw.print("{ \n \""+category+"\": "+ elencoInJson + "\n}");
                        pw.close();
                    } catch(FileNotFoundException e) {
                        System.out.println(e+ "in removing an activity");
                    }
                } catch (JsonIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                status = 0;
                editMenu(17);
                break;
            case "removeutente":
                consoleLog("starting removeutente command");
                try {
                    Path path= Paths.get("Access.json");
			        String pathStr=""+path.toAbsolutePath();
                    Object obj = JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray utenti= (JsonArray) jsonObj.get("users");
                    
                    for(int i=0; i<utenti.size(); i++) {
                        jsonObj= utenti.get(i).getAsJsonObject();
                        if(jsonObj.get("ID").getAsString().equals(option)){
                            utenti.remove(i);
                            
                        }
                    }
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    elencoInJson= gson.toJson(utenti);
                    try {
                        path= Paths.get("Access.json");
                        pathStr=""+path.toAbsolutePath();
                        PrintWriter pw= new PrintWriter(pathStr);
                        pw.print("{ \n \"users\": "+ elencoInJson + "\n}");
                        pw.close();
                    } catch(FileNotFoundException e) {
                        System.out.println(e+ "in removing a pending requests");
                    }
                } catch (JsonIOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                status = 0;
                editMenu(15);
                break;
            
            case "listaattivita":
                consoleLog("starting listaattivita command");
                status = 0;
                editMenu(17);
                break;

            case "about":
                consoleLog("Starting about command");
                editMessage("PATonTSBot, the telegram version of the app that helps you organise and book any events! created by Alessandro Mondini and Arianna Arruzzoli");
                setStatus(0);
                break;

            case "logout":
                consoleLog("starting logout command");
                logon = 0;
                setStatus(0);
                editMenu(1);
                break;
            
            case "admin":
                consoleLog("starting admin command");
                setStatus(0);
                editMenu(10);
                break;

            case "utenti":
                consoleLog("starting utenti command");
                setStatus(0);
                editMenu(11);
                break;
            
            case "gestionecategorie":
                consoleLog("starting gestionecategorie command");
                setStatus(0);
                editMenu(12);
                break;

            case "gestioneattivita":
                consoleLog("starting gestioneattivita command");
                setStatus(0);
                editMenu(13);
                break;

            case "gestionepreferenze":
                consoleLog("starting gestionepreferenze command");
                setStatus(0);
                editMenu(14);
                break;
            default:
                consoleLog("no command called");
                editMessage("no comand");
                setStatus(0);
                break;
        }

    }

    public void continueCommand(Update update){
        consoleLog("continuing last command");
        command.command(update, this);
    }

    public void consoleLog(String log){
        System.out.println(log);
    }

    //functions for sending/editing/deleting messagges

    public void deleteLastMessage(){
        consoleLog("Deleting the last message with this data: chatid" + chatId+", messageId"+lastMessageId);
        DeleteMessage delete = new DeleteMessage();
        delete.setChatId(chatId);
        delete.setMessageId(toIntExact(lastMessageId));
        try {
            execute(delete);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessage(String text){
        consoleLog("editing message with this data: chatid" + chatId+", messageId"+messageId);
        EditMessageText edt = new EditMessageText();
            edt.setChatId(chatId);
            edt.setMessageId(toIntExact(messageId));
            edt.setText(text);
        try {
            execute(edt);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessage(String text, InlineKeyboardMarkup markup){
        consoleLog("editing message with this data: chatid" + chatId+", messageId"+messageId);
        EditMessageText edt = new EditMessageText();
            edt.setChatId(chatId);
            edt.setMessageId(toIntExact(messageId));
            edt.setText(text);
            edt.setReplyMarkup(markup);
        try {
            execute(edt);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        SendMessage sendMessage = initializeMessage();
        consoleLog("sending message");
        sendMessage.setText(message);
        try {
            execute(sendMessage); 
        } catch (TelegramApiException e) {
            System.err.println(e.getMessage());
        }
    }

    //funzione per l'inizializzazione del oggetto messaggio

    public SendMessage initializeMessage(){
        consoleLog("initializing message with this data: chatid" + chatId+", messageId"+messageId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        return message;
    }

    public void sendMessage(SendMessage message){
        consoleLog("sending message");
        try {
            execute(message); 
        } catch (TelegramApiException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendMenu(int num_menu){
        SendMessage menu = initializeMessage();
        consoleLog("Sending menù with this data: chatid" + chatId+", messageId"+messageId);
        switch (num_menu) {
            case 1:
                if(getLogon() == 0){
                    consoleLog("Sending login menù");
                    menu.setText("Welcome to PATonTS bot! log in to start organise all the events!");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Login").setCallbackData("login"));
                    rowInline.add(new InlineKeyboardButton().setText("Subscribe").setCallbackData("sub"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    menu.setReplyMarkup(markupInline);
                    
                } else if(logon == 1){
                    consoleLog("Sending user home menù");
                    menu.setText("Home:");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Categorie").setCallbackData("categorie"));
                    rowInline.add(new InlineKeyboardButton().setText("Faq").setCallbackData("faq"));
                    rowInline.add(new InlineKeyboardButton().setText("About us").setCallbackData("about"));
                    rowInline.add(new InlineKeyboardButton().setText("User").setCallbackData("user"));
                    rowInline.add(new InlineKeyboardButton().setText("Log out").setCallbackData("logout"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    menu.setReplyMarkup(markupInline);
                } else if(logon == 2){
                    consoleLog("Sending admin home menù");
                    menu.setText("Home:");
                    InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                    List<InlineKeyboardButton> rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Categorie").setCallbackData("categorie"));
                    rowInline.add(new InlineKeyboardButton().setText("Faq").setCallbackData("faq"));
                    rowInline.add(new InlineKeyboardButton().setText("About us").setCallbackData("about"));
                    rowInline.add(new InlineKeyboardButton().setText("Admin").setCallbackData("admin"));
                    rowInline.add(new InlineKeyboardButton().setText("Log out").setCallbackData("logout"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);
                    menu.setReplyMarkup(markupInline);
                }
                
                break;
        
            default:
                break;
        }
        sendMessage(menu);
        
    }

    public void editMenu(int num_menu){
        consoleLog("editing menù with this data: chatid" + chatId+", messageId"+messageId);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline;
                List<InlineKeyboardButton> rowInline;
        switch (num_menu) {
            case 1:
                if(logon == 0){
                    consoleLog("editing login menù");
                    
                    rowsInline = new ArrayList<>(); //righe
                    rowInline = new ArrayList<>(); //colonne
                    rowInline.add(new InlineKeyboardButton().setText("Login").setCallbackData("login"));
                    rowInline.add(new InlineKeyboardButton().setText("Subscribe").setCallbackData("sub"));
                    // Set the keyboard to the markup
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);

                    
                } else if(logon == 1){
                    consoleLog("editing user home menù");

                    rowsInline = new ArrayList<>();
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Categories").setCallbackData("categorie"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Faq").setCallbackData("faq"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("About us").setCallbackData("about"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("User").setCallbackData("user"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Log out").setCallbackData("logout"));
                    rowsInline.add(rowInline);
                    // Set the keyboard to the markup
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);

                } else if(logon == 2){
                    consoleLog("editing admin home menù");
                    rowsInline = new ArrayList<>();
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Categories").setCallbackData("categorie"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Faq").setCallbackData("faq"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("About us").setCallbackData("about"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Admin").setCallbackData("admin"));
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText("Log out").setCallbackData("logout"));
                    rowsInline.add(rowInline);
                    // Add it to the message
                    markupInline.setKeyboard(rowsInline);

                }
                editMessage("Home:", markupInline);
                break;
                
            case 2:
                consoleLog("editing categorie menù");

                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                Category elenco= new Category("ElencoCategorie.txt");
                for(String category: elenco.getCategories()) {
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(category).setCallbackData("categoria_"+category));
                    rowsInline.add(rowInline);
                }

                
                // Set the keyboard to the markup
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                editMessage("categories menù:", markupInline);
                
                break;

            case 3:
                consoleLog("editing categoria menù");
                categoria = option;
                consoleLog(categoria);
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();

                try {
                    Path path= Paths.get(categoria+".json");
                    String pathStr=""+path.toAbsolutePath();
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray jsonArr= (JsonArray) jsonObj.get(option); //array con attività della categoria
                    for(JsonElement a: jsonArr) { //per ogni attività della categoria
                        JsonArray activityJ= (JsonArray) a.getAsJsonObject().getAsJsonArray("activity"); //contiene informazioni e opzioni
                        for(int j=0; j<activityJ.size(); j++) {
                            JsonObject dbActivity= (JsonObject) activityJ.get(j);
                            rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(dbActivity.get("name").getAsString()).setCallbackData("attivita_"+dbActivity.get("name").getAsString()));
                            rowsInline.add(rowInline);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                    

                //for(int i = 0; i < 10; i++){}
                
                // Set the keyboard to the markup
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                editMessage(option+" activity menù:", markupInline);
                
                break;
            case 4:
                String info = "";
                consoleLog("editing attività "+ option +" menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();

                try {
                    Path path= Paths.get(categoria+".json");
                    String pathStr=""+path.toAbsolutePath();
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray jsonArr= (JsonArray) jsonObj.get(categoria); //array con attività della categoria
                    for(JsonElement a: jsonArr) { //per ogni attività della categoria
                        String IDact = a.getAsJsonObject().get("IDact").getAsString();
                        JsonArray activityJ= (JsonArray) a.getAsJsonObject().getAsJsonArray("activity"); //contiene informazioni e opzioni
                        for(int j=0; j<activityJ.size(); j++) {
                            jsonObj = (JsonObject) activityJ.get(j);
                            if(option.equals(jsonObj.get("name").getAsString())){
                                info = info + option + "\n";
                                if(!(jsonObj.get("where").isJsonNull())) {
                                    info = info + " - where: " + jsonObj.get("where").getAsString()+"\n";
                                }
                                if(!(jsonObj.get("maxIscrizioni").isJsonNull())) {
                                    info = info + " - max number of subscriptions: " + jsonObj.get("maxIscrizioni").getAsInt()+"\n";
                                }
                                if(!(jsonObj.get("descrizione").isJsonNull())) {
                                    info = info + " - description: " +jsonObj.get("descrizione").getAsString()+"\n";
                                }
                                if(!(jsonObj.get("materiali").isJsonNull())) {
                                    info = info + " - materials: " +jsonObj.get("materiali").getAsString()+"\n";
                                }
                                JsonArray optionsJ= (JsonArray) jsonObj.get("options");
                                if(!(optionsJ.isJsonNull()))
                                    for(int i=0; i<optionsJ.size(); i++) {
                                        JsonObject opJ= optionsJ.get(i).getAsJsonObject();
                                        consoleLog(IDact);
                                        rowInline = new ArrayList<>();
                                        rowInline.add(new InlineKeyboardButton().setText(opJ.get("hour").getAsString()+" "+opJ.get("day").getAsString()).setCallbackData("addpreferenza_"+IDact+"-"+opJ.get("ID").getAsString()));
                                        rowsInline.add(rowInline);
                                    }
                            }
                        }
                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                markupInline.setKeyboard(rowsInline);
                editMessage(info, markupInline);
                break;

            case 6:
                String faqs = "";
                Path path= Paths.get("FAQ.json");
	            String pathStr=""+path.toAbsolutePath();
                try {
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray jsonArr= (JsonArray) jsonObj.get("FAQs"); 
                    for(JsonElement a: jsonArr) { //per ogni oggettto 
                        //prendi domanda
                        String domanda= a.getAsJsonObject().get("question").getAsString();
                        faqs = faqs + " - " + domanda + "\n";
                        //prendi risposta
                        String risposta= a.getAsJsonObject().get("answer").getAsString();
                        faqs = faqs + " - " + risposta + "\n";
                    }
                } catch(FileNotFoundException e) {
                    System.out.println(e+ "in generating FAQs");
        
                }

                editMessage(faqs);
                break;

            case 7:
                consoleLog("editing preferenze menù");

                rowsInline = new ArrayList<>();
                try {
                    path= Paths.get("Record.json");
                    pathStr=""+path.toAbsolutePath();
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray preferencesJ= (JsonArray) jsonObj.getAsJsonArray("preferences");
                    for(int p=0; p<preferencesJ.size(); p++) {
                        JsonObject dbPreference= (JsonObject) preferencesJ.get(p); //prende l'espressione della preferenza
                        JsonArray dbCoupleAsArr= (JsonArray) dbPreference.get("coppia").getAsJsonArray(); //prende il valore della coppia
                        JsonObject dbCouple= dbCoupleAsArr.get(0).getAsJsonObject();
                        consoleLog(userID+dbCouple.get("userID").getAsString());
                        if(userID.equals(dbCouple.get("userID").getAsString())){
                            rowInline = new ArrayList<>();
                            rowInline.add(new InlineKeyboardButton().setText(dbCouple.get("userID").getAsString()+", "+dbCouple.get("activityID").getAsString()+dbPreference.get("opzione").getAsString()).setCallbackData("removepreferenza_"+dbCouple.get("activityID").getAsString()+dbPreference.get("opzione").getAsString()));
                            rowsInline.add(rowInline);
                        }
                       
                    }
                } catch(FileNotFoundException e) {
                    System.out.print(e+ "in reading preferences from admin page");
                }
                    
                markupInline.setKeyboard(rowsInline);

                editMessage("List of all the preferences:", markupInline);

                break;

            case 10:
                consoleLog("editing admin menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Handle users").setCallbackData("utenti"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Handle categories").setCallbackData("gestionecategorie"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Handle preferences").setCallbackData("gestionepreferenze"));
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("Admin menù:", markupInline);
                break;
            case 11:
                consoleLog("editing gestione utenti menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("add users").setCallbackData("addutente"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("add users from the list of requests").setCallbackData("addutenterichieste"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Remove users").setCallbackData("listautenti"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Edit users").setCallbackData("listautenti"));
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("Handle user menù:", markupInline);
                break;
            case 12:
                consoleLog("editing gestione categorie menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Add category").setCallbackData("addcategoria"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Handle activities").setCallbackData("gestioneattivita"));
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("handle categories menù:", markupInline);
                break;
            case 13:
                consoleLog("editing gestione attività menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("add activity").setCallbackData("addattivita"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("remove activity").setCallbackData("listaattivita"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Edit activity").setCallbackData("listaattivita"));
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("Handle activities menù:", markupInline);
                break;
            case 14:
                consoleLog("editing gestione preferenze menù");
                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("Remove preference").setCallbackData("removepreferenze"));
                rowsInline.add(rowInline);
                // Add it to the message
                markupInline.setKeyboard(rowsInline);
                editMessage("Handle preferences menù:", markupInline);
                break;
            case 15:
                consoleLog("editing lista utenti menù");
                ArrayList<Utente> allUtenti= new ArrayList<Utente>();
                rowsInline = new ArrayList<>();
                try {
                    allUtenti.clear();
                    path= Paths.get("Access.json");
                    pathStr=""+path.toAbsolutePath();
                    Object obj= JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray accounts= (JsonArray) jsonObj.get("users");
                    for(int i=0; i<accounts.size(); i++) {
                        JsonObject dbUser= (JsonObject) accounts.get(i);
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton().setText("edit user: " + dbUser.get("ID").getAsString() ).setCallbackData("editutente_"+dbUser.get("ID").getAsString()));
                        rowInline.add(new InlineKeyboardButton().setText("remove user: " + dbUser.get("ID").getAsString() ).setCallbackData("removeutente_"+dbUser.get("ID").getAsString()));
                        rowsInline.add(rowInline);
                    }
                } catch(FileNotFoundException e) {
                    System.out.println(e+ "in getting all users list");
                }
                
                
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("User list:", markupInline);
                break;

            case 16:
                consoleLog("editing lista richieste menù");
                rowsInline = new ArrayList<>();
                try {
                    path= Paths.get("pendingRequest.json");
                    pathStr=""+path.toAbsolutePath();
                    Object obj= JsonParser.parseReader(new FileReader(pathStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    //ha controllato prima se fosse null
                    JsonArray richieste= (JsonArray) jsonObj.get("request");
                    for(int i=0; i<richieste.size(); i++) {
                        JsonObject dbUser= (JsonObject) richieste.get(i);
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton().setText("add: " + dbUser.get("username").getAsString() ).setCallbackData("addutente_"+dbUser.get("password").getAsString()+"-"+dbUser.get("password").getAsString() ));
                        rowsInline.add(rowInline);
                    }
                } catch(FileNotFoundException e) {
                    System.out.println(e+ "in getting all users list");
                }
                
                
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("user form list:", markupInline);
                break;
            case 17:
                consoleLog("editing lista attivita menù");
                elenco= new Category("ElencoCategorie.txt");
                rowsInline = new ArrayList<>();
                try {
                    for(String category: elenco.getCategories()) {
                        path= Paths.get(category+".json");
                        pathStr=""+path.toAbsolutePath();
                        consoleLog(category);
                        JsonObject jsonObj = JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                        JsonArray jsonArr= (JsonArray) jsonObj.get(category); //array con attività della categoria
                        for(JsonElement a: jsonArr) { //per ogni attività della categoria
                            String idAct= a.getAsJsonObject().get("IDact").getAsString();
                            JsonArray activityJ= (JsonArray) a.getAsJsonObject().getAsJsonArray("activity"); //contiene informazioni e opzioni
                            for(int j=0; j<activityJ.size(); j++) {
                                JsonObject dbActivity= (JsonObject) activityJ.get(j);
                                rowInline = new ArrayList<>();
                                rowInline.add(new InlineKeyboardButton().setText("edit "+dbActivity.get("name").getAsString()).setCallbackData("editattivita_"+category+"-"+idAct));
                                rowInline.add(new InlineKeyboardButton().setText("remove "+dbActivity.get("name").getAsString()).setCallbackData("removeattivita_"+category+"-"+idAct));
                                rowsInline.add(rowInline);
                            }
                        }
                    }
                } catch(FileNotFoundException e) {
                    System.out.println(e+ "in generating activities managment");
                }
                
                
                // Add it to the message
                markupInline.setKeyboard(rowsInline);

                editMessage("Lista of activities:", markupInline);
                break;
            case 18:
                consoleLog("editing add activity menù");

                rowsInline = new ArrayList<>();
                rowInline = new ArrayList<>();
                elenco= new Category("ElencoCategorie.txt");
                for(String category: elenco.getCategories()) {
                    rowInline = new ArrayList<>();
                    rowInline.add(new InlineKeyboardButton().setText(category).setCallbackData("addattivita_"+category));
                    rowsInline.add(rowInline);
                }
                markupInline.setKeyboard(rowsInline);

                editMessage("List of categories:", markupInline);
                break;
            case 19:
                consoleLog("editing remove preferenze menù");

                rowsInline = new ArrayList<>();
                try {
                    path= Paths.get("Record.json");
                    pathStr=""+path.toAbsolutePath();
                    JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
                    JsonArray preferencesJ= (JsonArray) jsonObj.getAsJsonArray("preferences");
                    for(int p=0; p<preferencesJ.size(); p++) {
                        JsonObject dbPreference= (JsonObject) preferencesJ.get(p); //prende l'espressione della preferenza
                        JsonArray dbCoupleAsArr= (JsonArray) dbPreference.get("coppia").getAsJsonArray(); //prende il valore della coppia
                        JsonObject dbCouple= dbCoupleAsArr.get(0).getAsJsonObject();
                        rowInline = new ArrayList<>();
                        rowInline.add(new InlineKeyboardButton().setText(dbCouple.get("userID").getAsString()+", "+dbCouple.get("activityID").getAsString()+dbPreference.get("opzione").getAsString()).setCallbackData("removepreferenza_"+dbCouple.get("activityID").getAsString()+dbPreference.get("opzione").getAsString()));
                        rowsInline.add(rowInline);
                    }
                } catch(FileNotFoundException e) {
                    System.out.print(e+ "in reading preferences from admin page");
                }
                    
                markupInline.setKeyboard(rowsInline);

                editMessage("List of all the preferences:", markupInline);
                break;
            default:
                break;
        }

        

    }

    @Override
    public String getBotToken() {
        
        return "1919191860:AAHqyaGHfdfzKDlwWTghTq9Hm7LLyNN0TzY";
    }
    
}
