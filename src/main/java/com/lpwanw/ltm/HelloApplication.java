package com.lpwanw.ltm;

import com.lpwanw.ltm.client.Client;
import com.lpwanw.ltm.server.Command;
import com.lpwanw.ltm.server.Message;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    public static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        HelloApplication.stage = stage;
        stage.setTitle(Client.name);
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> {
            Message msg = new Message("", Command.EXIT);
            try {
                Client.ous.writeObject(msg);
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}