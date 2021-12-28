package com.lpwanw.ltm.Controller;

import com.lpwanw.ltm.client.Client;
import com.lpwanw.ltm.server.Command;
import com.lpwanw.ltm.server.Message;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public TextArea ChatPane;
    public TextField ChatText;
    public static String matchName;
    public Label StatusText;
    public Button SendButton;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    public Button FindButton;
    public Button LeaveButton;
    public Button ExitButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Login");
        dialog.setHeaderText("Enter your nickname");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/com/lpwanw/ltm/Login.fxml"));
        Pane pane;
        try {
            pane = fxml.load();
            dialog.getDialogPane().setContent(pane);
            LoginController con = fxml.getController();

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return con.nameTextField.getText();
                }
                System.exit(0);
                return null;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client.clientInit();
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Message msg = new Message(result.get(), Command.INIT);
                Client.ous.writeObject(msg);
                msg = (Message) Client.ois.readObject();
                while (!msg.getCommand().equals(Command.INIT_SUCCESS)) {
                    dialog.setHeaderText("Nickname has been used, try again with another nickname");
                    result = dialog.showAndWait();
                    if (result.isPresent()) {
                        msg = new Message(result.get(), Command.INIT);
                        Client.ous.writeObject(msg);
                        msg = (Message) Client.ois.readObject();
                    } else {
                        System.exit(0);
                    }
                }
                Client.name = result.get();
                Thread readMessage = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Message receivedMessage = (Message) Client.ois.readObject();
                            System.out.println(receivedMessage.getCommand() + ": " + receivedMessage.getData());
                            switch (receivedMessage.getCommand()) {
                                case RECEIVE -> Platform.runLater(() -> {
                                    String text = "[" + HelloController.matchName + "] (" + HelloController.simpleDateFormat.format(new Date()) + "): " + receivedMessage.getData();
                                    ChatPane.setText(ChatPane.getText() + "\n" + text);
                                    ChatPane.setScrollTop(Double.MAX_VALUE);
                                });
                                case FOUND_MATCH -> {
                                    HelloController.matchName = receivedMessage.getData();
                                    Platform.runLater(() -> {
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Found Match");
                                        alert.setHeaderText("Do you want to chat with...");
                                        alert.setContentText(HelloController.matchName);
                                        Message msg1 = new Message("", Command.ACCEPT);
                                        Optional<ButtonType> option = alert.showAndWait();
                                        if (option.isPresent()) {
                                            if (option.get() == ButtonType.OK) {
                                                msg1.setCommand(Command.ACCEPT);
                                                StatusText.setText("In match with: " + HelloController.matchName);
                                                SendButton.setDisable(false);
                                                FindButton.setDisable(true);
                                                LeaveButton.setDisable(false);
                                                ChatPane.setText("");
                                                ChatText.setText("");

                                            } else if (option.get() == ButtonType.CANCEL) {
                                                msg1.setCommand(Command.DENY);
                                            } else {
                                                msg1.setCommand(Command.DENY);
                                            }
                                        }else{
                                            msg1.setCommand(Command.DENY);
                                        }
                                        try {
                                            Client.ous.writeObject(msg1);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                                case QUEUE -> Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("QUEUE");
                                    alert.setHeaderText("You are in queue...");
                                    alert.show();
                                    StatusText.setText("In queue...");
                                    SendButton.setDisable(true);
                                    FindButton.setDisable(false);
                                    LeaveButton.setDisable(true);
                                    ChatPane.setText("");
                                    ChatText.setText("");
                                });
                                case LEAVE_MATCH -> Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("QUEUE");
                                    alert.setHeaderText(HelloController.matchName + " left...");
                                    alert.show();
                                    StatusText.setText("In queue...");
                                    SendButton.setDisable(true);
                                    FindButton.setDisable(false);
                                    LeaveButton.setDisable(true);
                                    ChatPane.setText("");
                                    ChatText.setText("");
                                });
                                case INCOME_MATCH -> Platform.runLater(() -> {
                                    HelloController.matchName = receivedMessage.getData();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Match incoming");
                                    alert.setHeaderText(HelloController.matchName + " want to chat with you");
                                    alert.show();
                                    StatusText.setText("In match with: " + HelloController.matchName);
                                    SendButton.setDisable(false);
                                    FindButton.setDisable(true);
                                    LeaveButton.setDisable(false);
                                    ChatPane.setText("");
                                    ChatText.setText("");
                                });
                            }
                        } catch (IOException e) {
                            Thread.currentThread().interrupt();
                            return;
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                });
                readMessage.start();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void onKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            sendMessage();
        }
    }

    public void onSendMessage() {
        sendMessage();
    }

    public void sendMessage() {
        try {
            Message msg = new Message(ChatText.getText(), Command.SEND);
            Client.ous.writeObject(msg);
            String text = "Me (" + HelloController.simpleDateFormat.format(new Date()) + "): " + msg.getData();
            ChatPane.setText(ChatPane.getText() + "\n" + text);
            ChatText.setText("");
            ChatPane.setScrollTop(Double.MAX_VALUE);
        } catch (IOException e) {
            StatusText.setText("In queue...");
            SendButton.setDisable(true);
            ChatPane.setText("");
            ChatText.setText("");
            e.printStackTrace();
        }
    }

    public void onFindMath() {
        Message msg = new Message("", Command.FIND_MATCH);
        try {
            Client.ous.writeObject(msg);
        } catch (IOException e) {
            System.out.println(false);
        }
    }

    public void onLeaveChat() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave chat");
        alert.setHeaderText("Leaving");
        alert.setContentText("Are you sure????");
        Message msg1 = new Message("", Command.LEAVE_MATCH);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                try {
                    Client.ous.writeObject(msg1);
                    StatusText.setText("In queue...");
                    SendButton.setDisable(true);
                    FindButton.setDisable(false);
                    LeaveButton.setDisable(true);
                    ChatPane.setText("");
                    ChatText.setText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void onExitButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Leaving");
        alert.setContentText("Are you sure????");
        Message msg1 = new Message("", Command.EXIT);
        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                try {
                    Client.ous.writeObject(msg1);
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}