package Telegram;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.telegram.telegrambots.meta.api.objects.Update;

public class AddCategory extends Command{

    private String category;


    public void command(Update update, Bot bot){
        consoleLog("AddCategory command with statusCommand = " + getStatusCommand());

        switch (getStatusCommand()) {
            case 0:
            
                consoleLog("step 0");
                bot.editMessage("write the name of the category:");
                setStatusCommand(1);
                break;
            case 1:
                consoleLog("step 1");
                category = bot.getText();
                category=category.trim();
                String iniziale= category.substring(0, 1);
                iniziale=iniziale.toUpperCase();
                String newCategoryName= iniziale+category.substring(1, category.length());
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
                    PrintWriter pWriter= new PrintWriter(pathStr);
                } catch (FileNotFoundException e) {
                    System.out.println(e+" in adding category");
                }
                catch (IOException e) {
                    System.out.println(e+" in adding category");
                }
                editMenu(1);
                break;

        }


    }
}