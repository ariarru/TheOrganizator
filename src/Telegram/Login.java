package Telegram;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.telegram.telegrambots.meta.api.objects.Update;


public class Login extends Command{

    private String username;
    private String password;

    Login(){
        setName("login");

    }

    public void command(Update update, Bot bot){
        consoleLog("Login command with statusCommand = " + getStatusCommand());

        switch (getStatusCommand()) {
            //first step, ask for username
            case 0:
                consoleLog("step 0");
                bot.editMessage("write your username:");
                setStatusCommand(1);
                break;
            //second step, checks the username, if its right continues the command and if its wrong it will go back to the first step
            case 1:
                consoleLog("step 1");
                username = bot.getText();
                bot.editMessage("write your password:");
                setStatusCommand(2);
                break;
            case 2:
                password = bot.getText();
                try {
                    Path pathAccess= Paths.get("Access.json");
                    String pathAccessStr=""+pathAccess.toAbsolutePath();
                    Object obj= JsonParser.parseReader(new FileReader(pathAccessStr));
                    JsonObject jsonObj= (JsonObject) obj;
                    JsonArray accounts= (JsonArray) jsonObj.get("users");
                    boolean found=false;
                    for(int i=0; i<accounts.size(); i++) {
                        JsonObject dbUser= (JsonObject) accounts.get(i);  //data base user
                        if(dbUser.get("username").getAsString().equals(username) && dbUser.get("password").getAsString().equals(password)) {
                            found=true;
                            if(dbUser.get("admin").getAsBoolean()) {
                                bot.setLogon(2);
                            }
                            else {
                                bot.setLogon(1);
                            }
                            bot.setUserID(dbUser.get("ID").getAsString());
                            bot.editMenu(1);
                            break;
                        }
                    }
                    if(!found) {
                    setStatusCommand(0);
                    command(update, bot); 
                    }
                        
                    
                }
                catch(FileNotFoundException e) {
                    System.out.println(e + " in Login");
                } catch (IllegalArgumentException e) {
                    System.out.println(e+ " in Login");
                }
                break;

        }


    }
}
