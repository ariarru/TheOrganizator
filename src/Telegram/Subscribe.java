package Telegram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Subscribe extends Command{

    private String username;
    private String password;

    Subscribe(){
        setName("subscribe");
    }

    public void command(Update update, Bot bot){
        consoleLog("Subscribe command with statusCommand = " + getStatusCommand());

        switch (getStatusCommand()) {
            //first step, ask for username
            case 0:
            
                consoleLog("step 0");
                bot.editMessage("write the username:");
                setStatusCommand(1);
                break;
            //second step, checks the username, if its right continues the command and if its wrong it will go back to the first step
            case 1:
                consoleLog("step 1");
                username = bot.getText();
                bot.editMessage("write the password:");
                setStatusCommand(2);
                break;
            case 2:
                password = bot.getText();
                try {
                    Path pathRequests= Paths.get("pendingRequest.json");
                    String pathRequestsStr=""+pathRequests.toAbsolutePath();
                    RandomAccessFile rAF= new RandomAccessFile(new File(pathRequestsStr), "rw"); 
                    long posizioneCursore= rAF.length();
                    long stop;
                    while(rAF.length()>0) { 
                        posizioneCursore--;
                        rAF.seek(posizioneCursore);
                        if(rAF.readByte() == ']') {
                            stop=posizioneCursore;
                            while(posizioneCursore>1) { //serve per controllare se ci siano altre richieste, in caso aggiunge la virgola
                                posizioneCursore--;
                                rAF.seek(posizioneCursore);
                                if(rAF.readByte() == '}') {
                                    rAF.seek(posizioneCursore);
                                    rAF.writeBytes("},\n");
                                    stop++; //faccio in modo che il cursore sia dopo la virgola e \n
                                    break;
                                }
                            }
                            rAF.seek(stop); 
                            break;
                        }
                    }
                    String json = "{\n"+ "    \"username\": \""+username+"\",\n"+ "    \"password\": \""+password+"\" \n"+ "    }";
                    rAF.writeBytes(json+ "]\n}");
                    rAF.close();
                    bot.editMenu(1);
                    setStatusCommand(0);
                } catch (FileNotFoundException e) {
                    System.out.println(e+" in adding users");
                }
                catch (IOException e) {
                    System.out.println(e+" in adding users");
                }
                
                break;

        }


    }
}