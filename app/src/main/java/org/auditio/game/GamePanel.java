package org.auditio.game;

import org.auditio.game.util.SystemUiHider;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import java.util.LinkedList;
import java.util.ListIterator;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Typeface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GamePanel extends Activity implements
        SurfaceHolder.Callback{

    public final int OFFSET = 25;
    public int maxStatements = 5;
    public int scorePanel;


    private SurfaceView surface;
    private SurfaceHolder holder;
    private MainThread thread;
    private Bitmap background;
    private int gameOver_height;
    private long timeDiff;
    private int level = 1;
    private boolean update = true;
    private float curSpeed;

    //private static final String TAG = MainThread.class.getSimpleName();

    /*
     * Create an array of statements of size maxStatements
     * this will be the maximum number of statements that one
     * can get wrong before level ends
     */
    private final LinkedList<Statement> statementList = new LinkedList<Statement>();

    // Initialize empty score board
    public Score score;


    //private static final String TAG = GamePanel.class.getSimpleName();

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.surface);

        surface = (SurfaceView) findViewById(R.id.surfaceView);
        surface.getHolder().addCallback(this);

        //Log.d(TAG, "GamePanel: View Added");
        score = new Score();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //  mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /*
     * Called when the user clicks the Send button
     */
    public void startGame() {
        //Log.d(TAG, "***** STARTING GAME *****");
        holder  = surface.getHolder();

        background = BitmapFactory.decodeResource(getResources(), R.drawable.game_screen);

        for (int i = statementList.size(); i < maxStatements; i++) {

            Statement statement;
            statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0, score);


            // Add to the list of statements
            synchronized (statementList) {
                statementList.add(statement);
            }
        }

        // create game loop thread
        thread = new MainThread(holder, this);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Log.d(TAG, "GamePanel: Surface Created");
        startGame();

        // Keep adding new statements if there is room
        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;
        gameOver_height = surface.getHeight();
        scorePanel = surface.getHeight()/4;

        maxStatements = (surface.getHeight() - scorePanel) /
                (BitmapFactory.decodeResource(getResources(), R.drawable.statement).getHeight() + OFFSET);

        //Log.d(TAG, " ***** MAX: " + maxStatements);

        // Generate the statements to be displayed
        while (list.hasNext()) {
            Statement s = list.next();

            s.setX(surface.getWidth() / 2);
            s.setY(surface.getHeight() + (++count * s.getBitmap().getHeight()) + OFFSET * count);


            if (count == 1) {
                curSpeed = s.getSpeed().getYv();
            }
        }


        thread.setRunning(true);

        if (!thread.isAlive())
            thread.start();

    }

    /*
     * @return Returns a string containing a timestamp containing the duration of
     * time while player has played the game
     *
     */
    private String duration(){
        long minute,second;

        if (timeDiff < 60){
            minute = 0;
            second = timeDiff;
        }else {
            minute = timeDiff/60;
            timeDiff = timeDiff - minute * 60;
            second = timeDiff;
        }

        return String.format("%02d", minute) + ":" + String.format("%02d", second);
    }


    /*
     * Based on the level the user is at, increase the speed
     *
     */
    private void speedUp() {
        // Update speed every 30 seconds
        if (timeDiff == 30 * level) {
            this.curSpeed++;
            this.level++;

            //Log.d(TAG, "*** SPEEDUP! Duration: " + timeDiff + " Speed : " + this.curSpeed);
        }
    }


    /*
     * This method updates all the moving components of the game by determining
      * if the speed needs to change and/or if statements should be removed from the list
      * of statements
     */
    public void update() {
        // If game is over, no need to update
        if(!this.update)
            return;

        ListIterator<Statement> list;
        int count = 0;

        speedUp();

        synchronized (statementList) {
            // This variable will track the height of the last statement that is in the
            // iterator. At that point the additional statements will get added
            int lastY = 0;
            list = statementList.listIterator();

            // Iterate through the statement list and update the speed of statement
            // or clear out statements
            while (list.hasNext()) {
                // check collision with top wall if heading up
                Statement s = list.next();

                int stopAt = scorePanel + (count * s.getBitmap().getHeight() + OFFSET * count++);

                s.getSpeed().setYv(curSpeed);

                if (s.destroy()) {
                    // pop statement from the linkedlist
                    list.remove();

                } else if (s.getSpeed().getyDirection() == Speed.DIRECTION_UP
                        && s.getY() - s.getBitmap().getHeight() / 2 <= stopAt) {

                    s.stopMoving();

                } else {
                    // Check if something above was cleared up. If so, start moving again
                    s.getSpeed().setYv(curSpeed);
                }

                lastY = s.getY();
                // Update the moving statements
                s.update();
            }


            // If statements has been removed, regenerate
            for (int i = statementList.size(), c = 1; i < maxStatements; i++, c++) {

                Statement statement;
                statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0, score);

                statement.setX(surface.getWidth() / 2);
                statement.setY(lastY + statement.getBitmap().getHeight() * c + c* OFFSET);

                // Add to the list of statements
                statementList.add(statement);
            }
        }
    }


    /*
     * Draws the statements and the score
     */
    public void onDraw(Canvas canvas, long timeDiff) {
        // fills the canvas with background color
        canvas.drawBitmap(background, 0, 0, null);
        ListIterator<Statement> list;

        // Draw the score
        int maxWidth = surface.getWidth()/2;
        String s = "SCORE: " +  score.getTotalCorrect() + "/" + score.getTotalAnswered();

        Typeface tf = Typeface.create(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);

        int size = 0;
        Paint paint = new Paint();
        paint.setTypeface(tf);
        paint.setColor(Color.WHITE);

        // Determine max text size that fits in the width
        do {
            paint.setTextSize(++size);
        } while(paint.measureText(s) < maxWidth);

        // Draw the score
        canvas.drawText(s, maxWidth - 100, scorePanel/3, paint);

        // Once the game is over, draw the Game Over animation
        if (score.getTotalAnswered() - score.getTotalCorrect() == maxStatements){
            paint.setColor(Color.DKGRAY);
            String gameOver = "GAME OVER";
            this.update = false;

            canvas.drawText(gameOver, maxWidth - paint.measureText(gameOver)/2, gameOver_height, paint);

            if (gameOver_height > surface.getHeight() * 3 / 5) {
                gameOver_height -= 5;
            }

            // Print the time
            paint.setColor(Color.WHITE);
            paint.setTextSize(size * 3 / 5);
            canvas.drawText("Level:" + this.level + " " + duration(), maxWidth, scorePanel /2, paint );

            //Log.d(TAG, "Game Over - after: " + this.timeDiff);
        }else{
            // Print the time
            this.timeDiff = timeDiff;

            paint.setTextSize(size * 3 / 5);
            canvas.drawText("Level:" + this.level + " " + duration(), maxWidth, scorePanel / 2, paint);
        }

        //Iterate through statementList and draw
        synchronized (statementList) {
            list = statementList.listIterator();

            while (list.hasNext()) {
                list.next().draw(canvas);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());

        ListIterator<Statement> list;

        synchronized (statementList) {
            list = statementList.listIterator();

            while (list.hasNext()) {
                Statement s = list.next();

                if (!s.isTouched()) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Delegate event handling to the droid
                        s.handleActionDown((int) event.getX(), (int) event.getY());
                    }
                }
            }

        }

        return true;

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Log.d(TAG, "GamePanel surface destroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Log.d(TAG, "GamePanel Surface Changed");
    }


    @Override
    protected void onPause(){
        boolean retry = true;
        while (retry) {
            try {
                //Log.d(TAG, "thread.SetRunning(false)");
                thread.setRunning(false);
                //Log.d(TAG, "Trying to Join thread");
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                //Log.d(TAG, "Caught Exception");
            }
        }

        super.onPause();

        onDestroy();
    }


    @Override
    protected void onDestroy() {
        //Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop () {
        //Log.d(TAG, "Stopping...");
        super.onStop();
    }

}
