package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatHouseController implements Initializable{

    class Runnable2 implements Runnable{

        private TextArea textArea;

        @Override
        public void run() {
            try {
                while(true)
                {
                    synchronized(textArea)
                    {
                        String dom = Main.user.dom;
                        textArea.clear();
                        List<String> wiadomosci = new ArrayList<String>();
                        wiadomosci = Main.client.getMessage(dom);

                        for(int i=0; i<wiadomosci.size(); i++)
                        {
                            String wiadomosc=wiadomosci.get(i) + "\n";

                            textArea.appendText(wiadomosc);
                        }
                    }
                    Thread.sleep(2000);
                }
            }
            catch (Exception e) {
            }
        }
        public void setUiElement(TextArea textarea)
        {
            this.textArea = textarea;
        }
    }


    Thread refreshMessages=null;

    @FXML
    private Label friendEmailLabel;

    @FXML
    private Pane mainAppPane;

    @FXML
    private TextArea messageArea = new TextArea();

    @FXML
    private TextField messageField;

    @FXML
    private Button messageButton;

    @FXML
    private Button backButton;

    @FXML
    private Label welcomeLabel;

    @FXML
    void backButtonClick(ActionEvent event) throws IOException {

        Main.user.znajomy = "";
        if(refreshMessages.isAlive())
            refreshMessages.stop();
        ScreenController control = new ScreenController(this.backButton);
        control.switchScene("MainAppPanel.fxml");
    }

    @FXML
    void messageButtonClick(ActionEvent event) throws IOException {

        if(messageField.getText() == "")
        {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Wiadomosc nie moze byc pusta!");
            alert.showAndWait();
        }
        else
        {
            synchronized (messageArea) {
                String login = Main.user.email;
                String dom = Main.user.dom;
                String tresc=messageField.getText();
                messageField.clear();
                Main.client.sendMessageHouse(login, dom, tresc);
                messageArea.appendText(login + ": " + tresc);
            }
        }
    }

    private void loadBackground()
    {
        String dom = Main.user.dom;

        switch(dom)
        {
            case "Gryffindor": changeBackground("../images/gryffBackground.png");
                break;
            case "Slytherin": changeBackground("../images/slythBackground.png");
                break;
            case "Hufflepuff": changeBackground("../images/huffBackground.png");
                break;
            case "Ravenclaw": changeBackground("../images/ravenBackground.png");
                break;
            default: changeBackground("../images/gryffBackground.png");
                break;
        }
    }

    private void changeBackground(String background)
    {
        String backgroundUrl = RegisterController.class.getResource(background).toExternalForm();
        mainAppPane.setStyle("-fx-background-image: url('" + backgroundUrl + "')");
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        loadBackground();
        welcomeLabel.setText("Witaj " + Main.user.imie + " " + Main.user.nazwisko + " (" + Main.user.email + ")");


        friendEmailLabel.setText("Pokój wspólny " + Main.user.dom);

        var watekCzas2 = new Runnable2();
        watekCzas2.setUiElement(messageArea);

        refreshMessages = new Thread(watekCzas2);
        refreshMessages.start();

        System.out.println("ChatController initialize");
    }
}