package org.auditio.game;


import android.graphics.Bitmap;
import android.graphics.Canvas;
//import android.util.Log;

/**
 * Created by auditio on 15-03-05.
 */
public class Score {
    //private static final String TAG = Score.class.getSimpleName();

    private int totalAnswered;
    private int totalCorrect;
    private Glyphs glyphs;
    private Bitmap bitmap;
    private int x;
    private int y;

    public Score(){
        this.totalAnswered = 0;
        this.totalCorrect = 0;
        this.x = 0;
        this.y = 45;
    }

    public Score(Glyphs glyphs, Bitmap bitmap){
        this.totalAnswered = 0;
        this.totalCorrect = 0;
        this.glyphs = glyphs;
        this.bitmap = bitmap;
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


    public void draw(Canvas canvas){
        int x = this.x - (this.bitmap.getWidth() / 2);

        canvas.drawBitmap(bitmap, x , y, null);

        String printScore = getTotalCorrect() + "/" + getTotalAnswered();

        x = this.x - 50 + (printScore.length() * 23 * 3 / 2 );
        glyphs.drawString(canvas, printScore, x, 100);
    }
}
