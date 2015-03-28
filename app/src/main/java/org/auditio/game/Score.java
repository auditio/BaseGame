package org.auditio.game;


import android.graphics.Canvas;

/**
 * Created by auditio on 15-03-05.
 */
public class Score {
    private int totalAnswered;
    private int totalCorrect;
    private Glyphs glyphs;

    public Score(Glyphs glyphs){
        this.totalAnswered = 0;
        this.totalCorrect = 0;
        this.glyphs = glyphs;
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
        glyphs.drawString(canvas, getTotalCorrect() + "-" + getTotalAnswered(), 250, 10);
    }
}
