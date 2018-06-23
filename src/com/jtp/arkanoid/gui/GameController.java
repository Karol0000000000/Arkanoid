package com.jtp.arkanoid.gui;

import com.jtp.arkanoid.mechanics.Ball;
import com.jtp.arkanoid.Main;
import com.jtp.arkanoid.mechanics.Paddle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class GameController {

    private Main main;
    private Thread movingBall;
    private Ball moveBall;
    private Paddle paddle;
    private Object ballMonitor;
    private boolean ifGameIsOn;
    private boolean ifGameIsPaused;

    @FXML
    private Label labelPause;

    @FXML
    private Label labelStart;

    @FXML
    private ImageView imageViewPaddle;

    @FXML
    private ImageView imageViewBall;

    @FXML
    private Label labelCountDown;

    /**
     * metoda inicjuje odpwoiednie obiekty i zmienne, w tym watki
     */
    @FXML
    public void initialize(){
        ifGameIsPaused = false;
        ifGameIsOn = false;
        moveBall = new Ball(this.imageViewBall, this.imageViewPaddle,this, labelCountDown);
        movingBall = new Thread(moveBall);
        paddle = new Paddle(this, imageViewPaddle);
        imageViewBall.setVisible(false);
        labelCountDown.setVisible(false);
    }

    /**
     * metoda wywolywana w momencie klikniecia przez uzytkownika label'a 'start'
     * uruchamia watek pileczki
     */
    @FXML
    private void onActionStart(){
        ifGameIsOn = true;
        moveBall.setBallMonitor(ballMonitor);
        moveBall.setBricks(main.getBricks());
        moveBall.setCanWin(1);
        moveBall.setThreadFinish(false);
        moveBall.setPlayAgain(1);
        movingBall.start();
    }

    /**
     * metoda wywolywana w momencie, gdy uzytkownik kliknie 'koniec'
     */
    @FXML
    private void onActionFinish(){
        main.onFinish("Czy napewno zakończyć?","GAME");
    }

    /**
     * wywoluje po klikniecie w label 'pauza'
     * odpowiednio zarzadza watkiem pileczki stosowanie do sytuacji
     */
    @FXML
    private void onActionPause(){
        if(movingBall != null && movingBall.isAlive() && moveBall.getAvailablePauseThread()) {
            if (labelPause.getText().equals("Pauza")) {
                ifGameIsPaused=true;
                moveBall.setThreadStop(true);
                labelPause.setText("Kontynuuj");
            } else if (labelPause.getText().equals("Kontynuuj")) {
                ifGameIsPaused=false;
                moveBall.setThreadStop(false);
                synchronized (moveBall.getBallMonitor()) {
                    moveBall.getBallMonitor().notify();
                }
                labelPause.setText("Pauza");
            }
        }
    }

    public void onKey(KeyEvent e){
        paddle.movePaddle(e);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    public Thread getThread(){
        return movingBall;
    }

    public Label getLabelStart() {
        return labelStart;
    }

    public ImageView getImageViewBall() {
        return imageViewBall;
    }

    public Ball getMoveBall() {
        return moveBall;
    }

    public void setBallMonitor(Object ballMonitor) {
        this.ballMonitor = ballMonitor;
    }

    public void setIfGameIsOn(boolean ifGameIsOn) {
        this.ifGameIsOn = ifGameIsOn;
    }

    public boolean getIfGameIsOn() {
        return ifGameIsOn;
    }

    public boolean getIfGameIsPaused() {
        return ifGameIsPaused;
    }
}
