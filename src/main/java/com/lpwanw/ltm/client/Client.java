package com.lpwanw.ltm.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public final static int ServerPort = 8888;
    public static ObjectOutputStream ous;
    public static ObjectInputStream ois;
    public static InetAddress ip;
    public static Socket s;
    public static String name;
    public static void clientInit(){
        try {
            ip = InetAddress.getByName("localhost");
            s = new Socket(ip,Client.ServerPort);
            ous = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
