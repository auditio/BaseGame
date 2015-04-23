package org.auditio.game;

/**
 * Created by auditio on 15-02-28.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
//import android.util.Log;

import java.util.Random;

public class Statement {
    //private static final String TAG = Statement.class.getSimpleName();

    // The score reference object that gets updated by action on statements
    private Score score;

    // The image to render behind the statement
    private Bitmap bitmap;

    /*
     * 0 = Correct equation
     * 1 = Wrong equation
     * 2 = Statement has not been generated yet
     */
    private int correctAns = 2;
    /*
     * 0 = Player picked tick
     * 1 = Player picked cross
     * 2 = No Answer yet
     */
    private int chosenAns = 2;

    // Array of Number object to be used for equation generation
    private Number[] nums;

    private int x;
    private int y;

    private Speed speed;
    public String statement;
    private boolean touched = false;
    private boolean destroy = false;

    /*
     *
     */
    public Statement(Bitmap bitmap, int x, int y, Score score) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.speed = new Speed(0, 2);

        decideRightWrong();
        this.statement = generateStatement();

        //Log.d(TAG, "EQUATION: " + this.statement);
        //Log.d(TAG, bitmap.getWidth() + " " + bitmap.getHeight());
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

    /*
     * If destroy is set to true, it will be removed from the game screen
     */
    public boolean destroy () { return destroy; }

    /*
     * Updates the speed and diretion of the statement object to make it stop moving
     */
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
     *
     *   @return Returns a string containing a mathematical statement
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
     * Randomly generate 0 or 1 to determine whether or not the equation
     * prepared should be correct or incorrect and store the result
     * in member 'correctAns'
     *
     * 0 = correct equation
     * 1 = incorrect equation
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

    /*
     * Update the statement's speed and direction of movement
     */
    public void update() {
        y += (speed.getYv() * speed.getyDirection());
    }


    /* Calculate square areas at the end of the statement blob which will contain tick or cross mark
     *
     *      Tick coordinate: x = bitmap.x + bitmap.y, y = bitmap.y
     *
     *      Cross coordinate: x = bitmap.witdh - bitmap.y, y = bitmap.y
     *
     * If the event params passed in falls within the button area, action accordingly
     *
     * @param eventX Contains the x coordinate of the touched area
     * @param eventY Contains the y coordinate of the touched area
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

                //Log.d(TAG, "* PICKED TICK * " + this.statement);
            }else
                setTouched (false);

        } else if ((eventX >= x + bitmap.getWidth()/2 - bitmap.getHeight()) && (eventX <= (x + bitmap.getWidth()/2))){
            if((eventY >= (y - bitmap.getHeight()/2)) && (eventY <= y + bitmap.getHeight()/2)){
                // Cross mark was clicked
                chosenAns = 1;
                setTouched (true);

                //Log.d(TAG, " * PICKED CROSS * " + this.statement);
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

    /*
     * Mark the statement for deletion
     */
    private void destroyStatement(){
        // Make the statement disappear
        destroy = true;
    }

    /*
     * Determine and update player's score and mark statements for destruction if the
     * chosen answer is correct
     */
    private void clearRight(){
        // Increment total count
        if(touched) {
            if (chosenAns == correctAns) {
                //Increment score points
                //Log.d(TAG, "RIGHT!!! chosen:" + chosenAns + " correct:" + correctAns);
                destroyStatement();
                score.right();
            } else {
                //Log.d(TAG, "WRONG! chosen:" + chosenAns + " correct:" + correctAns);
                score.wrong();
            }

        }
    }


    /**
     * Method draws the bitmap container of the statement and the
     * appropriately sized string equation in it at the given coordinates
     *
     * @param canvas Takes in the canvas to draw on
     */
    public void draw(Canvas canvas) {
        int x = this.x - (this.bitmap.getWidth() / 2);
        int y = this.y - (this.bitmap.getHeight() / 2);

        int width = canvas.getWidth()/3;

        canvas.drawBitmap(bitmap, x, y, null);
        //Log.d(TAG, "** STATEMENT: " + statement);
        if (!touched) {
            Typeface tf = Typeface.create(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);

            int size = width/7;
            Paint paint = new Paint();
            paint.setTypeface(tf);
            paint.setColor(Color.GRAY);

            do {
                paint.setTextSize(++size);
            } while(paint.measureText(this.statement) < width);

            canvas.drawText(this.statement, width, this.y + this.bitmap.getHeight()/3, paint);

        }
    }

}
