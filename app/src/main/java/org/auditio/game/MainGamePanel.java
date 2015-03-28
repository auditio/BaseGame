package org.auditio.game;

/**
 * Created by auditio on 15-02-11.
 */
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

import java.util.LinkedList;
import java.util.ListIterator;


public class MainGamePanel extends SurfaceView implements
        SurfaceHolder.Callback{

    public final int MAX_STATEMENTS = 8;
    public final int OFFSET = 25;
    public final int SCOREPANEL = 280;

    private MainThread thread;
    private static final String TAG = MainThread.class.getSimpleName();

    /*
     * Create an array of statements of size MAX_STATEMENTS
     * this will be the maximum number of statements that one
     * can get wrong before level ends
     */

    private final LinkedList <Statement> statementList = new LinkedList<Statement>();
    private Glyphs glyphs;
    public final Score score;

    public MainGamePanel(Context context){
        super (context);

        getHolder().addCallback(this);
        this.glyphs = new Glyphs(BitmapFactory.decodeResource(getResources(), R.drawable.glyphs));

        // Initialize empty score board
        score = new Score(glyphs);

        for (int i = statementList.size(); i < MAX_STATEMENTS; i++) {

            Statement statement;
            statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0, glyphs, score);

            // Add to the list of statements
            synchronized (statementList) {
                statementList.add(statement);
            }
        }

        // create game loop thread
        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "MainGamePanel Surface Changed");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Keep adding new statements if there is room
        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;

        while (list.hasNext()){
            Statement s = list.next();


            s.setX(getWidth() / 2);
            s.setY(getHeight() + (++count * s.getBitmap().getHeight()) + OFFSET * count);

            if (count == 1){
                Log.d(TAG, "Statement height: " + s.getBitmap().getHeight() + " Width: " + s.getBitmap().getWidth() );
            }
        }

        Log.d(TAG, "Width = " + getWidth() + " Height = " + getHeight());

        thread.setRunning(true);
        thread.start();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "MainGamePanel surface destroyed");

        boolean retry = true;
        while (retry) {
            try {
                Log.d(TAG, "Trying to Join thread");
               thread.join();
                Log.d(TAG, "thread.SetRunning(false)");
                thread.setRunning(false);
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
                Log.d(TAG, "Caught Exception");
                thread.setRunning(false);
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

                        // check if in the lower part of the screen we exit
                        if (event.getY() > getHeight() - 70) {
                            thread.setRunning(false);
                            ((Activity) getContext()).finish();
                        }
                    }
                }
            }

        }


        return true;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        // fills the canvas with black
        canvas.drawColor(Color.BLACK);
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

                statement.setX(getWidth() / 2);
                statement.setY(getHeight() + (i * statement.getBitmap().getHeight()) + OFFSET * ++count);

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

}
