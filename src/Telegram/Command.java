package Telegram;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Command extends Bot {
    
    private String name;
    private int statusCommand =  0 ;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getStatusCommand() {
        return statusCommand;
    }
    public void setStatusCommand(int status) {
        this.statusCommand = status;
    }

    public void command(Update update, Bot bot){}
    public void command(Update update, Bot bot,String option){}


    
}
