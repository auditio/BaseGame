package org.auditio.game;

/**
 * Created by auditio on 15-02-28.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
//import android.util.Log;

import java.util.Random;

public class Statement {
    //private static final String TAG = Statement.class.getSimpleName();

    private Bitmap bitmap;
    // Store if the statement generated is wright or wrong

    /*
     * 0 = correct statement
     * 1 = wrong statement
     */
    private int correctAns = 2;
    /*
     * 0 = correct statement
     * 1 = wrong statement
     */
    private int chosenAns = 2;
    private Number[] nums;

    private int x;
    private int y;
    private boolean touched = false;
    private Speed speed;
    public String statement;
    private boolean destroy = false;

    private Glyphs glyphs;
    private Score score;

    public Statement(Bitmap bitmap, int x, int y, Glyphs glyphs, Score score) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = new Speed(5, 5);

        decideRightWrong();
        this.statement = generateStatement();
        //Log.d(TAG, "EQUATION: " + this.statement);
        //Log.d(TAG, bitmap.getWidth() + " " + bitmap.getHeight());

        // Load the glyph resources
        this.glyphs = glyphs;
        this.score = score;

    }

    public Bitmap getBitmap() {
        return bitmap;
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

    public boolean destroy () { return destroy; }

    public void stopMoving(){
        this.speed.setYv(0);
    }

    public Speed getSpeed() {
        return speed;
    }

    public boolean isTouched() {
        return touched;
    }

    /**
      * Generate the equation to be displayed.
     *  Set CorrectAns = 0 means equation is true
     *      CorrectAns = 1 means equation is false
     */
    private String generateStatement(){
        this.nums = new Number [2];
        for (int i = 0; i < nums.length; i++){
            nums[i] = new Number();
        }

        Equation e = new Equation(nums);
        e.getEquation();

        int solution = e.getSolution();

        // Decide whether or not to return correct statement
        if (this.correctAns == 0)
            return e.getEquation() + "=" + solution;
        else{
          // Prepare wrong answer. Use the same decision picker to decide
          // whether or not to add or subtract
            int pick = prepWrongAnswer();

            if(pick == 0) { // add one
                int answer = solution + 1 + prepWrongAnswer();
                return e.getEquation() + "=" + answer;
            } else {
                int answer = solution - 1 - prepWrongAnswer();
                return e.getEquation() + "=" + answer;
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

    public void setTouched(boolean touched) {
        this.touched = touched;
        //Log.d(TAG, "Touched statement x#" + x + " y#" + y);
    }

    public void draw(Canvas canvas) {
        int x = this.x - (this.bitmap.getWidth() / 2);
        int y = this.y - (this.bitmap.getHeight() / 2);

        canvas.drawBitmap(bitmap, x, y, null);
        //Log.d(TAG, "** STATEMENT: " + statement);
        if (!touched)
            glyphs.drawString(canvas, this.statement, this.x, y + 20 );
    }


    /* Calculate square areas at the end of the statement blob which will contain tick or cross mark
     *
     * Tick coordinate: x = bitmap.x + bitmap.y, y = bitmap.y
     *
     * Cross coordinate: x = bitmap.witdh - bitmap.y, y = bitmap.y
     *
     *
     * */
    public void handleActionDown(int eventX, int eventY) {
        //Log.d(TAG, statement);
        // Check if tick was clicked
        if((eventX >= (x - bitmap.getWidth()/2) && (eventX <= x - bitmap.getWidth()/2 + bitmap.getHeight()))){
            if((eventY >= (y - bitmap.getHeight()/2)) && (eventY <= y + bitmap.getHeight()/2)){
                // Tick mark was clicked
                chosenAns = 0;
                setTouched (true);

                //Log.d(TAG, "* PICKED TICK *");
            }else
                setTouched (false);

        } else if ((eventX >= x + bitmap.getWidth()/2 - bitmap.getHeight()) && (eventX <= (x + bitmap.getWidth()/2))){
            if((eventY >= (y - bitmap.getHeight()/2)) && (eventY <= y + bitmap.getHeight()/2)){
                // Cross mark was clicked
                chosenAns = 1;
                setTouched (true);

                //Log.d(TAG, " * PICKED CROSS *");
            } else
                setTouched (false);

        } else
            setTouched(false);

        /*
        int rightXlower = x - bitmap.getWidth()/2;
        int rightXupper = x - bitmap.getWidth()/2 + bitmap.getHeight();
        int yLower = y - bitmap.getHeight()/2;
        int yUpper = y + bitmap.getHeight()/2;

        int wrongXlower = x + bitmap.getWidth()/2 - bitmap.getHeight();
        int wrongXupper = x + bitmap.getWidth()/2;

        Log.d(TAG, "---------------------------------------------------");
        Log.d(TAG, "Right X: " + rightXlower + " X:" + rightXupper + " Wrong X: " + wrongXlower + " X:" + wrongXupper);
        Log.d(TAG, "Y lower:" + wrongXlower + " upper:" + wrongXupper);
        Log.d(TAG, "---------------------------------------------------");
        */

        // At this point we are ready to compare if the user chose the right/wrong answer
        clearRight();

    }

    public void update() {
        y += (speed.getYv() * speed.getyDirection());
    }

    private void clearRight(){
        // Increment total count
        if(touched) {
            if (chosenAns == correctAns) {
                //Increment score points
                //Log.d(TAG, "RIGHT!!!");
                destroyStatement();
                score.right();
            } else {
                //Log.d(TAG, "WRONG! ChosenAns:" + chosenAns);
                score.wrong();
            }

        }
    }

    private void destroyStatement(){
        // Make the statement disappear
        destroy = true;
    }

}
