package org.auditio.game;


import java.util.Random;

/**
 * Created by auditio on 15-03-01.
 */
public class Number{
    int num;

    /* Constructor to set num to a randomy generated value */
    public Number(){
        setNum();
    }
    /* Constructor to set num to given value */
    public Number(int n){
        setNum(n);
    }

    /*
     * Randomly generate a number and set it to member 'num'
     */
    public void setNum(){
        this.num = randomGen();
    }

    /*
     * Use input param to set the value of member 'num'
     */
    public void setNum(int n) {
        this.num = n;
    }

    public int getNum(){
        return this.num;
    }

    /*
     * Method to update the value of member 'num'
     */
    public int getNewNum(){
        setNum();

        return this.num;
    }

    /*
     * Generate a random number
     */
    public int randomGen(){
        Random rand = new Random();
        // generate nextInt in the range [0, n)
        int r = rand.nextInt(9) + 1;

        return r;
    }
}
