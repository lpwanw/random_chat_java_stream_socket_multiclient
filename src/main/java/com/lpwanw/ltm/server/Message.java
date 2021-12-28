package com.lpwanw.ltm.server;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 6529685098267757690L;
    private String data;
    private Command command;
    public Message( String msg, Command send) {
        this.command = send;
        this.data = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
