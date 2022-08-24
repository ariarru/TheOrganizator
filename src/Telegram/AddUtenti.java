package Telegram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.telegram.telegrambots.meta.api.objects.Update;

import login.Admin;
import login.User;

public class AddUtenti extends Command{

    private String username;
    private String password;
    private String type;
    private static Path path;
	private static String pathStr;

    public void command(Update update, Bot bot, String option){
        
        username = option.split("-")[0];
        password = option.split("-")[1];
        setStatusCommand(2);
        command(update, bot);
    }
    public void command(Update update, Bot bot){
        consoleLog("AddUtente command with statusCommand = " + getStatusCommand());

        switch (getStatusCommand()) {
            case 0:
            
                consoleLog("step 0");
                bot.editMessage("write the username:");
                setStatusCommand(1);
                break;
            case 1:
                consoleLog("step 1");
                username = bot.getText();
                bot.editMessage("write the password:");
                setStatusCommand(2);
                break;
            case 2:
                consoleLog("step 2");
                password = bot.getText();
                bot.editMessage("write \"yes\" if the new user is an admin, or \"no\" if he isn't :");
                setStatusCommand(3);
                break;
            case 3:
                consoleLog("step 3");
                type = bot.getText();
                if(!type.equalsIgnoreCase("yes")){
                    if(!type.equalsIgnoreCase("no")){
                        consoleLog("risposta sbagliata >:(");
                        setStatusCommand(2);
                        command(update, bot);
                    }
                }
                try {
                    Path path= Paths.get("Access.json");
                    pathStr=""+path.toAbsolutePath();
                    RandomAccessFile rAF= new RandomAccessFile(new File(pathStr), "rw"); //uso il RandomAccessFile per avere controllo sul cursore
                    long posizioneCursore= rAF.length();
                    while(rAF.length()>0) { 
                        posizioneCursore--;
                        rAF.seek(posizioneCursore);
                        if(rAF.readByte() == ']') {
                            rAF.seek(posizioneCursore);
                            break;
                        }
                    }
                    if(type.equalsIgnoreCase("yes")){
                        Admin nuovoUtente= new Admin(username, password, generateNewID());
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(nuovoUtente);
                        rAF.writeBytes(",\n"+ json+ "]\n}");
                        rAF.close();
                    } else if(type.equalsIgnoreCase("no")){
                        User nuovoUtente= new User(username, password, generateNewID());
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(nuovoUtente);
                        rAF.writeBytes(",\n"+ json+ "]\n}");
                        rAF.close();
                    }
                    
                } catch (FileNotFoundException e) {
                    System.out.println(e+" in adding users");
                }
                catch (IOException e) {
                    System.out.println(e+" in adding users");
                }
                bot.editMenu(1);
                break;

        }


    }

    private String generateNewID() {
        try {
			path= Paths.get("Access.json");
			pathStr=""+path.toAbsolutePath();
			Object obj= JsonParser.parseReader(new FileReader(pathStr));
			JsonObject jsonObj= (JsonObject) obj;
			JsonArray accounts= (JsonArray) jsonObj.get("users");
            JsonObject dbUser= (JsonObject) accounts.get(accounts.size()-1);
            String lastID=dbUser.get("ID").getAsString();
            int i= Integer.parseInt(lastID.substring(3));
            lastID= ""+(i+1); //riutilizzo la vecchia stringa per non crearne altre
            String newID="#";
            for(int c=0; c<5; c++) { //5 è la lunghezza totale
                if(newID.length()+1+ lastID.length() <=5) {
                    newID= newID+"0";
                }
            }
            newID= newID + lastID;
            return newID;
		} catch(FileNotFoundException e) {
			System.out.println(e+ "in getting all users list");
		}
        return null;
		
	}
}
