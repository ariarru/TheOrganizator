package Telegram;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.telegram.telegrambots.meta.api.objects.Update;

import activities.Activity;
public class AddAttivita extends Command {

private String category;
private String where;
private int max;
private String desc;
private String materiali;
private String nome;

    public void command(Update update, Bot bot, String option){
            
        category = option;
        command(update, bot);
    }
    public void command(Update update, Bot bot){
        consoleLog("AddAttivita command with statusCommand = " + getStatusCommand());

        switch (getStatusCommand()) {
            case 0:
            
                consoleLog("step 0");
                bot.editMessage("write the name of the activity:");
                setStatusCommand(1);
                break;
            case 1:
                nome = bot.getText();
                consoleLog("step 1");
                bot.editMessage("write the place:");
                setStatusCommand(2);
                break;
            case 2:
                where = bot.getText();
                consoleLog("step 2");
                bot.editMessage("write the max number of subscriptions:");
                setStatusCommand(3);
                break;
            case 3:
                max = Integer.parseInt(bot.getText());
                consoleLog("step 3");
                bot.editMessage("write the description:");
                setStatusCommand(4);
                break;
            case 4:
                desc = bot.getText();
                consoleLog("step 4");
                bot.editMessage("write the materials:");
                setStatusCommand(5);
                break;
            case 5:
                materiali = bot.getText();
                consoleLog("step 5");
                String iniziale= category.substring(0, 1);
                iniziale=iniziale.toUpperCase();
                Activity a = new Activity(generateNewId(category), nome, where);
                a.setDescription(desc);
                a.setMaterials(materiali);
                
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
                bot.editMenu(1);
                break;

        }

    }

    private String generateNewId(String category) {
		String iniziale= category.substring(0,1);
		iniziale= iniziale.toUpperCase();
		String ID= iniziale+"001";
		boolean found=false;
        Path path= Paths.get(category+".json");
        String pathStr=""+path.toAbsolutePath();
        try{
            JsonObject jsonObj= JsonParser.parseReader(new FileReader(pathStr)).getAsJsonObject();
            JsonArray jsonArr= (JsonArray) jsonObj.get(category); //array con attività della categoria
            for(JsonElement a: jsonArr) {
                JsonObject db= (JsonObject) a;
                if(db.get("IDact").getAsString().contains(iniziale)) {
                    ID=db.get("IDact").getAsString();
                    found=true;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
		if(found) {
			int ultimaCifraId= Integer.parseInt(ID.substring(ID.length()-1));
			ultimaCifraId++;
			ID= ID.substring(0,ID.length()-1)+ultimaCifraId;
		}
		return ID;
	}
}
