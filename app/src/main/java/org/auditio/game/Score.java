package org.auditio.game;


/**
 * Created by auditio on 15-03-05.
 */
public class Score {
    public int totalAnswered;
    public int totalCorrect;

    public Score(){
        this.totalAnswered = 0;
        this.totalCorrect = 0;
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
