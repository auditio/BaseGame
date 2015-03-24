package org.auditio.game;


import java.util.Random;

/**
 * Created by auditio on 15-03-01.
 */
public class Number{
    int num;

    public Number(){
        setNum();
    }
    public Number(int n){
        setNum(n);
    }

    public void setNum(){
        this.num = randomGen();
    }

    public void setNum(int n) {
        this.num = n;
    }

    public int getNum(){
        return this.num;
    }

    public int getNewNum(){
        setNum();

        return this.num;
    }

    public int randomGen(){
        Random rand = new Random();
        // generate nextInt in the range [0, n)
        int r = rand.nextInt(9) + 1;

        return r;
    }
}
