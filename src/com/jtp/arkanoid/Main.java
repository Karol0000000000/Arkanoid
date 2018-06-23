package com.jtp.arkanoid;

import com.jtp.arkanoid.gui.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Karol Jasłowski H6X1S1
 */

public class Main extends Application{

    private Pane pane;
    private GameController gamecontroller;
    private Stage primary;
    private List<ImageView> bricks;
    private double layoutX;
    private double layoutY;
    private int indexOfBricks;
    private Object ballMonitor;
    private WindowEvent eventOnClose;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primary = primaryStage;
        onStart();
    }

    /**
     * metoda inicjuje obiekty i zmienne
     * ładuje plik fxml
     * dodaje do sceny obsługe poruszania paletki
     * obsługuje efekt nacisniecia x (kończenie okna)
     * @throws IOException wyjatek ze sceny
     */

    public void onStart() throws java.io.IOException {
        ballMonitor = new Object();
        bricks = new ArrayList<ImageView>();
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/gameFXML.fxml"));
        pane = loader.load();
        addBricks();
        Scene scene = new Scene(pane,1000,600);
        primary.setScene(scene);
        gamecontroller = loader.getController();
        gamecontroller.setMain(this);
        gamecontroller.setIfGameIsOn(false);
        gamecontroller.setBallMonitor(ballMonitor);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                gamecontroller.onKey(event);
            }
        });
        primary.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                eventOnClose = event;
                onFinish("Czy na pewno wyjść?","MAIN");
            }
        });
        primary.show();
    }

    /**
     * metoda dodaje cegiełki do kontenera
     */
    private void addBricks() {
        indexOfBricks = -1;
        for(int i=0; i<5; i++){
            for(int j=0; j<9; j++){
                indexOfBricks++;
                layoutY = 15 + 45*i;
                layoutX = 150 + 75*j;

                bricks.add(new ImageView(new Image(this.getClass().getResourceAsStream("/prostokat.png"))));
                bricks.get(indexOfBricks).setLayoutX(layoutX);
                bricks.get(indexOfBricks).setLayoutY(layoutY);
                bricks.get(indexOfBricks).setFitHeight(40);
                bricks.get(indexOfBricks).setFitWidth(70);

                pane.getChildren().add(bricks.get(indexOfBricks));
            }
        }
    }


    /**
     * @param infoOnFinish informacja wyswietlana uzytkownikowi przy konczeniu gry (Alert)
     * @param sourceOnFinish ktora klasa wywoluje metode (MAIN lub GAME)
     * wyswietla informacje w chwili konca gry lub jej przerwania
     * zarzadza odpowiednio watkiem pileczki stosownie do sytuacji
     * konczy program jesli kontekst sytuacji na to wskazuje
     */
    public void onFinish(String infoOnFinish, String sourceOnFinish){

        if(gamecontroller.getThread().isAlive()) {
            gamecontroller.getMoveBall().setThreadStop(true);
        }

        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        alert1.setTitle("Koniec gry");
        alert1.setHeaderText("Arkanoid");
        alert1.setContentText(infoOnFinish);

        ButtonType buttonYes1 = new ButtonType("Tak");
        ButtonType buttonNo1 = new ButtonType("Nie");
        alert1.getButtonTypes().setAll(buttonYes1,buttonNo1);

        Optional<ButtonType> buttonType1 = alert1.showAndWait();
        if(buttonType1.get() == buttonYes1){
                if(gamecontroller.getThread().isAlive()){
                    gamecontroller.getMoveBall().setThreadStop(false);
                    synchronized(ballMonitor) {
                        ballMonitor.notify();
                    }
                    gamecontroller.getMoveBall().setThreadFinish(true);
                }
                if(gamecontroller.getIfGameIsOn()) {
                    if(!sourceOnFinish.equals("MAIN")) {
                        oneMore();
                    }
                }else{
                    Platform.exit();
                }

        }else if(buttonType1.get() == buttonNo1){
            if(gamecontroller.getThread().isAlive()) {
                if(!gamecontroller.getIfGameIsPaused()) {
                    gamecontroller.getMoveBall().setThreadStop(false);
                    synchronized (ballMonitor) {
                        ballMonitor.notify();
                    }
                }
            }
            if(sourceOnFinish.equals("MAIN")){
                eventOnClose.consume();
            }
        }


    }

    /**
     * metoda pyta uzytkownika czy chce zagrac jeszcze raz
     * przy twierdzacej odpowiedzi umozliwia mu taka sytuacje
     */
    private void oneMore(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Koniec gry");
        alert.setHeaderText("Arkanoid");
        alert.setContentText("Czy chcesz zagrać jeszcze raz?");

        ButtonType buttonYes = new ButtonType("Tak");
        ButtonType buttonNo = new ButtonType("Nie");
        alert.getButtonTypes().setAll(buttonYes,buttonNo);

        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.get() == buttonYes){
            try {
                onStart();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(buttonType.get() == buttonNo){
            Platform.exit();
        }

    }

    public Pane getPane() {
        return pane;
    }

    public List<ImageView> getBricks() {
        return bricks;
    }

}
