package com.lpwanw.ltm.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> queue = new ArrayList<>();
    public static List<String> nameList = new ArrayList<>();
    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(8888)){
            Socket clientSocket;
            while(true){
                System.out.println("Server waiting for client");
                clientSocket = serverSocket.accept();
                System.out.println("New client request received : " + clientSocket);
                ObjectOutputStream ous = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                ClientHandler newClient = new ClientHandler(clientSocket,ois,ous);
                Thread thread = new Thread(newClient);
                thread.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
