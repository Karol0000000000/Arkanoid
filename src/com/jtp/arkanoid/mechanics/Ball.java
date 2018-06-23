package com.jtp.arkanoid.mechanics;

import com.jtp.arkanoid.gui.GameController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Ball implements Runnable {

    private static int i;
    private ImageView ball;
    private ImageView paddle;
    private GameController gameController;
    private Label labelCountDown;
    private double x;
    private double y;
    private List<ImageView> bricks;
    private int canWin;
    private int playAgain;
    private boolean threadFinish;
    private Object ballMonitor;
    private boolean threadStop;
    private boolean availablePauseThread;

    public Ball(ImageView ball, ImageView paddle, GameController gameController, Label labelCountDown) {
        availablePauseThread = false;
        threadStop = false;
        threadFinish = true;
        playAgain = 0;
        canWin = 0;
        bricks = new ArrayList<>();
        this.gameController = gameController;
        this.ball = ball;
        this.paddle = paddle;
        this.labelCountDown = labelCountDown;
        x = 0.2;
        y = 0.2;
    }

    /**
     * obsluguje nastepujace zdarzenia:
     * poruszanie pileczki,
     * odbijanie pileczki od scian,
     * odbijanie pileczki od paletki,
     * sytuacje, gdy pileczka spadnie pod paletke
     */
    @Override
    public void run() {

        countDown();

        gameController.getImageViewBall().setVisible(true);
        gameController.getLabelStart().setVisible(false);

        while (!threadFinish) {
            availablePauseThread = true;

            synchronized(ballMonitor){
                if(threadStop){
                    try {
                        ballMonitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(threadFinish){
                        return;
                    }
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    //poruszanie pileczki

                    ball.setLayoutX(ball.getLayoutX() - x);
                    ball.setLayoutY(ball.getLayoutY() - y);


                    //odbijanie pileczki od scian

                    if (ball.getLayoutX() <= gameController.getMain().getPane().getLayoutX() + 5) {
                        x = -x;
                    }
                    if (ball.getLayoutY() <= gameController.getMain().getPane().getLayoutY() + 5) {
                        y = -y;
                    }

                    if (ball.getLayoutX() + ball.getFitWidth() >= gameController.getMain().getPane().getLayoutY() +
                            gameController.getMain().getPane().getWidth() - 5) {
                        x = -x;
                    }


                    //odbijanie pileczki od paletki

                    if (ball.getLayoutX() + (ball.getFitWidth() / 3) >= paddle.getLayoutX() &&
                            ball.getLayoutX() + (ball.getFitWidth() / 2) <= paddle.getLayoutX() + (paddle.getFitWidth() / 5)) {

                        if (ball.getLayoutY() + ball.getFitHeight() >= paddle.getLayoutY()
                                && ball.getLayoutY() + ball.getFitHeight() <= paddle.getLayoutY()+paddle.getFitHeight()/3) {

                            x = 0.2;

                            if (y > 0.2) y = 0.2;
                            else if (y < -0.2) y = -0.2;
                            y = -y;
                        }
                    }

                    else if (ball.getLayoutX() + (ball.getFitWidth() / 2) >= paddle.getLayoutX() + (paddle.getFitWidth() / 5) &&
                            ball.getLayoutX() + (ball.getFitWidth() / 2) <= paddle.getLayoutX() + (2 * (paddle.getFitWidth() / 5))) {

                        if (ball.getLayoutY() + ball.getFitHeight() >= paddle.getLayoutY()
                                && ball.getLayoutY() + ball.getFitHeight() <= paddle.getLayoutY()+paddle.getFitHeight()/3) {

                            x=0.1;

                            if(y == 0.2 || y ==0.28) y = -0.25;
                            else if(y == -0.2 || y == -0.28) y = 0.25;
                            else y = -y;
                        }
                    }

                    else if (ball.getLayoutX() + (ball.getFitWidth() / 2) >= paddle.getLayoutX() + (2 * (paddle.getFitWidth() / 5)) &&
                            ball.getLayoutX() + (ball.getFitWidth() / 2) <= paddle.getLayoutX() + 3* (paddle.getFitWidth() / 5)) {

                        if (ball.getLayoutY() + ball.getFitHeight() >= paddle.getLayoutY()
                                && ball.getLayoutY() + ball.getFitHeight() <= paddle.getLayoutY()+paddle.getFitHeight()/3) {
                            x = 0;

                            if(y == 0.2 || y == 0.25) y = -0.28;
                            else if(y == -0.2 || y == -0.25) y =0.28;
                            else y = -y;
                        }
                    }

                    else if (ball.getLayoutX() + (ball.getFitWidth() / 2) >= paddle.getLayoutX() + 3* (paddle.getFitWidth() / 5) &&
                            ball.getLayoutX() + (ball.getFitWidth() / 2) <= paddle.getLayoutX() + (4 * (paddle.getFitWidth() / 5))) {

                        if (ball.getLayoutY() + ball.getFitHeight() >= paddle.getLayoutY()
                                && ball.getLayoutY() + ball.getFitHeight() <= paddle.getLayoutY()+paddle.getFitHeight()/3) {

                            x=-0.1;

                            if(y == 0.2 || y ==0.28) y = -0.25;
                            else if(y == -0.2 || y == -0.28) y = 0.25;
                            else y = -y;
                        }
                    }

                    else if (ball.getLayoutX() + (ball.getFitWidth() / 2) >= paddle.getLayoutX() + 4* (paddle.getFitWidth() / 5) &&
                            ball.getLayoutX() + 2*(ball.getFitWidth() / 3) <= paddle.getLayoutX() + paddle.getFitWidth()) {

                        if (ball.getLayoutY() + ball.getFitHeight() >= paddle.getLayoutY()
                                && ball.getLayoutY() + ball.getFitHeight() <= paddle.getLayoutY()+paddle.getFitHeight()/3) {

                            x=-0.2;

                            if (y > 0.2) y = 0.2;
                            else if (y < -0.2) y = -0.2;
                            y = -y;

                        }
                    }

                    //sytuacja, gdy pileczka spadnie pod paletke

                    else if(ball.getLayoutY()+ball.getFitHeight() > paddle.getLayoutY()+paddle.getFitHeight()/3 && playAgain == 1){
                        playAgain = 0;
                        threadFinish = true;
                        ball.setVisible(false);
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Koniec gry");
                        alert.setHeaderText("Arkanoid");
                        alert.setContentText("Koniec gry, piłka spadła pod paletkę\n" +
                                "Czy chcesz zagrać jeszcze raz?");

                        ButtonType yes = new ButtonType("Tak");
                        ButtonType no = new ButtonType("Nie");
                        alert.getButtonTypes().setAll(yes, no);
                        Optional<ButtonType> buttonType = alert.showAndWait();

                        if(buttonType.get() == yes){
                            try {
                                gameController.getMain().onStart();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if(buttonType.get() == no){
                            Platform.exit();
                        }
                    }

                    manageBricks();
                }
            });
        }

    }

    /**
     * odlicznie 3..2..1.. przed rozpoczeciem gry
     */
    private void countDown() {
        labelCountDown.setVisible(true);
        for (i = 3; i > 0; i--) {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    labelCountDown.setText(String.valueOf(i));
                }
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        labelCountDown.setVisible(false);
    }

    /**
     * zderzenie pileczki z cegielkami
     */
    private void manageBricks() {
        if(bricks.isEmpty() && canWin == 1){
            canWin = 0;
            threadFinish = true;
            ball.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Koniec gry");
            alert.setHeaderText("Arkanoid");
            alert.setContentText("Wygrana ! Gratulacje !\n" +
                    "Czy chcesz zagrać jeszcze raz?");

            ButtonType yes = new ButtonType("Tak");
            ButtonType no = new ButtonType("Nie");
            alert.getButtonTypes().setAll(yes, no);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if(buttonType.get() == yes){
                try {
                    gameController.getMain().onStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(buttonType.get() == no){
                Platform.exit();
            }
        }
        for (ImageView brick : bricks) {

            if(ball.getLayoutX() + 7*(ball.getFitWidth()/8) >= brick.getLayoutX()
                    && ball.getLayoutX() + ball.getFitWidth()/8 <= brick.getLayoutX()+brick.getFitWidth()) {

                if((ball.getLayoutY() <= brick.getLayoutY()+brick.getFitHeight()
                        && ball.getLayoutY() >= brick.getLayoutY()+35)
                        || (ball.getLayoutY()+ball.getFitHeight() >= brick.getLayoutY()
                        && ball.getLayoutY()+ ball.getFitHeight()<=brick.getLayoutY()+5)){
                    y = -y;

                    gameController.getMain().getPane().getChildren().remove(brick);
                    bricks.remove(brick);
                    return;
                }


            }else if (ball.getLayoutY() + ball.getFitHeight() >= brick.getLayoutY()
                    && ball.getLayoutY() <= brick.getLayoutY() + brick.getFitHeight()) {

                if ((ball.getLayoutX() + ball.getFitWidth() >= brick.getLayoutX()
                        && ball.getLayoutX() + ball.getFitWidth() <= brick.getLayoutX()+5)
                        || (ball.getLayoutX() <= brick.getLayoutX() + brick.getFitWidth()
                        &&  ball.getLayoutX() >= brick.getLayoutX() + 65)) {
                    if(x == 0)x=0.2;
                    x = -x;

                    gameController.getMain().getPane().getChildren().remove(brick);
                    bricks.remove(brick);
                    return;
                }
            }
        }
    }

    public void setCanWin(int canWin) {
        this.canWin = canWin;
    }

    public void setPlayAgain(int playAgain) {
        this.playAgain = playAgain;
    }

    public void setBricks(List<ImageView> bricks) {
        this.bricks.addAll(bricks);
    }

    public void setThreadFinish(boolean threadFinish) {
        this.threadFinish = threadFinish;
    }

    public void setThreadStop(boolean threadStop) {
        this.threadStop = threadStop;
    }

    public void setBallMonitor(Object ballMonitor) {
        this.ballMonitor = ballMonitor;
    }

    public Object getBallMonitor() {
        return ballMonitor;
    }

    public boolean getAvailablePauseThread() {
        return availablePauseThread;
    }
}
