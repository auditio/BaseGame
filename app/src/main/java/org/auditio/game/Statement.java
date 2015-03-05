package org.auditio.game;

/**
 * Created by auditio on 15-02-28.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Statement {
    private Bitmap bitmap;
    private Bitmap right;
    private Bitmap wrong;

    // Store if the statement generated is wright or wrong

    /*
     * 0 = correct statement
     * 1 = wrong statement
     */
    private int correctAns;
    private Number[] nums;
    private Equation equation;


    private int x;
    private int y;
    private boolean touched;
    private Speed speed;

    public Statement(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = new Speed(20, 20);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = this.bitmap;
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

    public String generateStatement(){
        this.nums = new Number [2];
        for (int i = 0; i < nums.length; i++){
            nums[i] = new Number();
        }

        Equation e = new Equation(nums);
        e.getEquation();

        // Decide whether or not to return correct statement
        if (this.correctAns == 0)
            return e.getEquation() + " = " + e.solution();
        else{
          // Prepare wrong answer. Use the same decision picker to decide
          // whether or not to add or subtract
            int pick = prepWrongAnswer();

            if(pick == 0) { // add one
                int answer = e.solution() + 1;
                return e.getEquation() + " = " + answer;
            } else {
                int answer = e.solution() - 1;
                return e.getEquation() + " = " + answer;
            }
        }
    }

    /*
     * 0 = correct statement
     * 1 = wrong statement
     */
    private void decideRightWrong(){
        Random rand = new Random();
        this.correctAns = (new Number(rand.nextInt(9) % 2)).getNum() ;
    }

    /*
     * 0 = correct statement
     * 1 = wrong statement
    */
    private int prepWrongAnswer(){
        Random rand = new Random();
        return (new Number(rand.nextInt(9) % 2)).getNum();
    }


    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
    }

    public void handleActionDown(int eventX, int eventY) {
        if (eventX >= (x - bitmap.getWidth() / 2) && (eventX <= (x + bitmap.getWidth() / 2))) {
            if (eventY >= (y - bitmap.getHeight() / 2) && (y <= (y + bitmap.getHeight() / 2))) {
                // droid touched
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            setTouched(false);
        }

    }

    public void update() {
        if (!touched) {
            //x += (speed.getXv() * speed.getxDirection());
            y += (speed.getYv() * speed.getyDirection());
        }
    }

    public Speed getSpeed() {
        return speed;
    }
}
