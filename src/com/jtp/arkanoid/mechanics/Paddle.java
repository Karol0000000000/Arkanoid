package com.jtp.arkanoid.mechanics;

import com.jtp.arkanoid.gui.GameController;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Paddle {

    private GameController gamecontroller;
    private ImageView paddle;

    public Paddle(GameController gameController, ImageView paddle) {
        this.gamecontroller = gameController;
        this.paddle = paddle;
    }

    /**
     * metoda umozliwiajaca poruszanie paletki po ekranie
     * @param e klawisz wcisniety przez uzytkownika
     */
    public void movePaddle(KeyEvent e) {
        if(e.getCode() == KeyCode.LEFT){
                double x = paddle.getLayoutX() - 25;
                if (x < 0) {
                    x = 0;
                }
                paddle.setLayoutX(x);

        }else if(e.getCode() == KeyCode.RIGHT){
            double y = paddle.getLayoutX()+25;
            if((y+ paddle.getFitWidth()) > gamecontroller.getMain().getPane().getLayoutX() + gamecontroller.getMain().getPane().getWidth()){
                y = gamecontroller.getMain().getPane().getLayoutX()+ gamecontroller.getMain().getPane().getWidth()- paddle.getFitWidth();
            }

            paddle.setLayoutX(y);
        }
    }
}
