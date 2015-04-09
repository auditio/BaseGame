package org.auditio.game;

import org.auditio.game.util.SystemUiHider;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.LinkedList;
import java.util.ListIterator;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GamePanel extends Activity implements
        SurfaceHolder.Callback{

    public int MAX_STATEMENTS = 8;
    public final int OFFSET = 25;
    public final int SCOREPANEL = 280;

    SurfaceView surface;
    SurfaceHolder holder;
    private MainThread thread;
    //private static final String TAG = MainThread.class.getSimpleName();

    /*
     * Create an array of statements of size MAX_STATEMENTS
     * this will be the maximum number of statements that one
     * can get wrong before level ends
     */

    private final LinkedList<Statement> statementList = new LinkedList<Statement>();

    private Glyphs glyphs;

    // Initialize empty score board
    public Score score;


    private static final String TAG = GamePanel.class.getSimpleName();

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
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.surface);

        surface = (SurfaceView) findViewById(R.id.surfaceView);
        surface.getHolder().addCallback(this);

        Log.d(TAG, "GamePanel: View Added");

        glyphs = new Glyphs(BitmapFactory.decodeResource(getResources(), R.drawable.glyphs));
        score = new Score(glyphs, BitmapFactory.decodeResource(getResources(), R.drawable.score_panel));

        startGame();
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

    /** Called when the user clicks the Send button */
    public void startGame() {
        Log.d(TAG, "***** STARTING GAME *****");
        holder  = surface.getHolder();

        for (int i = statementList.size(); i < MAX_STATEMENTS; i++) {

            Statement statement;
            statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0, glyphs, score);

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
        Log.d(TAG, "GamePanel: Surface Created");
        // Keep adding new statements if there is room
        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;

        while (list.hasNext()) {
            Statement s = list.next();

            s.setX(surface.getWidth() / 2);
            s.setY(surface.getHeight() + (++count * s.getBitmap().getHeight()) + OFFSET * count);

            if (count == 1) {
                // Set max number of statements based on the device size
                //MAX_STATEMENTS = getWidth() / s.getBitmap().getHeight();
                //Log.d(TAG, "Statement height: " + s.getBitmap().getHeight() + " Width: " + s.getBitmap().getWidth());
                //Log.d(TAG, "***** " + MAX_STATEMENTS + " *****");

                score.setX(surface.getWidth() / 2);
            }
        }

        thread.setRunning(true);
        thread.start();
    }



    public void onDraw(Canvas canvas) {
        // fills the canvas with background color
        canvas.drawColor(Color.parseColor("#bdada0"));
        ListIterator<Statement> list;

        // Draw the score
        synchronized (score){
            score.draw(canvas);
        }


        //Iterate through statementList and draw
        synchronized (statementList) {
            list = statementList.listIterator();

            while (list.hasNext()) {
                list.next().draw(canvas);
            }
        }
    }


    public void update() {
        ListIterator<Statement> list;
        int count = 0;

        synchronized (statementList) {
            // If statements has been removed, regenerate
            for (int i = statementList.size(); i < MAX_STATEMENTS; i++) {

                Statement statement;
                statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0, glyphs, score);

                statement.setX(surface.getWidth() / 2);
                statement.setY(surface.getHeight() + ((i - 1) * statement.getBitmap().getHeight()) + OFFSET * ++count);

                // Add to the list of statements
                statementList.add(statement);
            }


            list = statementList.listIterator();



            while (list.hasNext()) {
                // check collision with top wall if heading up
                Statement s = list.next();

                int stopAt = SCOREPANEL + (++count * s.getBitmap().getHeight() + OFFSET * count);

                if (s.destroy()) {
                    // pop statement from the linkedlist
                    list.remove();

                } else if (s.getSpeed().getyDirection() == Speed.DIRECTION_UP
                        && s.getY() - s.getBitmap().getHeight() / 2 <= stopAt) {


                    s.stopMoving();
                    //statement.getSpeed().toggleYDirection();

                } else {
                    // Check if something above was cleared up. If so, start moving again
                    s.getSpeed().setYv(5);
                }

                // Update the moving statements
                s.update();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());

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
        Log.d(TAG, "GamePanel surface destroyed");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "GamePanel Surface Changed");
    }


    @Override
    protected void onPause(){
        boolean retry = true;
        while (retry) {
            try {
                Log.d(TAG, "thread.SetRunning(false)");
                thread.setRunning(false);
                Log.d(TAG, "Trying to Join thread");
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.d(TAG, "Caught Exception");
            }
        }

        super.onPause();
    }
    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying...");
        super.onDestroy();
    }

    @Override
    protected void onStop () {
        Log.d(TAG, "Stopping...");
        super.onStop();
    }

}
