package com.lpwanw.ltm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final ObjectInputStream serverInput;
    private final ObjectOutputStream serverOutput;
    boolean isLogged;
    private String username;
    private ClientHandler match;

    public ClientHandler(Socket socket, ObjectInputStream serverInput, ObjectOutputStream serverOutput) {
        this.socket = socket;
        this.serverInput = serverInput;
        this.serverOutput = serverOutput;
        this.isLogged = true;
    }

    @Override
    public void run() {
        Message received;
        try (socket; serverInput; serverOutput) {
            while (!Thread.currentThread().isInterrupted()) {
                received = (Message) serverInput.readObject();
                System.out.println(received.getCommand());
                switch (received.getCommand()) {
                    case INIT -> {
                        System.out.println("Name: " + received.getData());
                        this.username = received.getData();
                        if (Server.nameList.contains(this.username)) {
                            Message message = new Message("In Queue", Command.INIT_FAIL);
                            this.getServerOutput().writeObject(message);
                            System.out.println(message.getCommand());
                        } else {
                            this.username = received.getData();
                            Server.nameList.add(this.username);
                            Message message = new Message("In Queue", Command.INIT_SUCCESS);
                            System.out.println(message.getCommand());
                            this.getServerOutput().writeObject(message);
                            Collections.shuffle(Server.queue);
                            for (int i = 0; i < Server.queue.size(); i++) {
                                ClientHandler client = Server.queue.get(i);
                                message = new Message(client.getUsername(), Command.FOUND_MATCH);
                                this.getServerOutput().writeObject(message);
                                message = (Message) this.getServerInput().readObject();
                                if (message.getCommand().equals(Command.ACCEPT)) {
                                    client.setMatch(this);
                                    this.setMatch(client);
                                    message.setCommand(Command.INCOME_MATCH);
                                    message.setData(this.getUsername());
                                    client.getServerOutput().writeObject(message);
                                    Server.queue.remove(client);
                                    System.out.println("Match: " + this.username+ " vs " + match.username);
                                    break;
                                }
                            }
                            if(message.getCommand().equals(Command.INCOME_MATCH)){
                                break;
                            }
                            message = new Message("", Command.QUEUE);
                            Server.queue.add(this);
                            this.getServerOutput().writeObject(message);
                        }
                    }
                    case SEND -> {
                        if (match != null) {
                            received.setCommand(Command.RECEIVE);
                            System.out.println(this.username + " SEND to " + match.username);
                            match.getServerOutput().writeObject(received);
                        }
                    }
                    case FIND_MATCH -> {
                        Message message = new Message("",Command.QUEUE);
                        Collections.shuffle(Server.queue);
                        for (int i = 0; i < Server.queue.size(); i++) {
                            ClientHandler client = Server.queue.get(i);
                            if(client.username.equals(this.username)){
                                continue;
                            }
                            message = new Message(client.getUsername(), Command.FOUND_MATCH);
                            this.getServerOutput().writeObject(message);
                            message = (Message) this.getServerInput().readObject();
                            if (message.getCommand().equals(Command.ACCEPT)) {
                                client.setMatch(this);
                                this.setMatch(client);
                                message.setCommand(Command.INCOME_MATCH);
                                message.setData(this.getUsername());
                                client.getServerOutput().writeObject(message);
                                Server.queue.remove(client);
                                Server.queue.remove(this);
                                System.out.println("Match: " + this.username+ " vs " + match.username);
                                break;
                            }
                        }
                        if(message.getCommand().equals(Command.INCOME_MATCH)){
                            break;
                        }
                        message = new Message("", Command.QUEUE);
                        this.getServerOutput().writeObject(message);
                    }
                    case LEAVE_MATCH -> {
                        Message message = new Message("",Command.LEAVE_MATCH);
                        this.match.getServerOutput().writeObject(message);
                        this.match.setMatch(null);
                        Server.queue.add(this.match);
                        this.setMatch(null);
                        Server.queue.add(this);
                    }
                    case EXIT -> {
                        if (this.getMatch() == null) {
                            Server.queue.remove(this);
                            Server.nameList.remove(this.username);
                            System.out.println(this.username + " logout");
                            Thread.currentThread().interrupt();
                            break;
                        }
                        match.setMatch(null);
                        Message message = new Message("In Queue", Command.QUEUE);
                        match.getServerOutput().writeObject(message);
                        Server.queue.add(match);
                        Server.nameList.remove(this.username);
                        this.isLogged = false;
                        System.out.println(this.username + " logout");
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Xin lỗi");
            Server.queue.remove(this);
            Server.nameList.remove(this.username);
            System.out.println(this.username + " logout");
        }
    }

    public ObjectInputStream getServerInput() {
        return serverInput;
    }

    public ObjectOutputStream getServerOutput() {
        return serverOutput;
    }

    public String getUsername() {
        return username;
    }

    public ClientHandler getMatch() {
        return match;
    }

    public void setMatch(ClientHandler match) {
        this.match = match;
    }
}
