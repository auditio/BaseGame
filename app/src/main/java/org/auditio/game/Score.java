package org.auditio.game;


import android.graphics.Bitmap;
import android.graphics.Canvas;
//import android.util.Log;

/**
 * Created by auditio on 15-03-05.
 *
 * This class contains details about the player's scores
 */
public class Score {
    //private static final String TAG = Score.class.getSimpleName();

    private int totalAnswered;
    private int totalCorrect;

    private int x;
    private int y;

    public Score(){
        this.totalAnswered = 0;
        this.totalCorrect = 0;
        this.x = 0;
        this.y = 45;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTotalAnswered(){
        return totalAnswered;
    }

    public int getTotalCorrect(){
        return totalCorrect;
    }

    /* Increment total count and correct count */
    public void right(){
        this.totalCorrect++;
        this.totalAnswered++;
    }

    /* only increment total count */
    public void wrong(){
        this.totalAnswered++;
    }
}
