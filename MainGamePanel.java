package org.auditio.game;

/**
 * Created by auditio on 15-02-11.
 */

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.Log;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;


public class MainGamePanel extends SurfaceView implements
        SurfaceHolder.Callback{

    private final int MAX_STATEMENTS = 10;
    private final int OFFSET = 5;

    private MainThread thread;
    private static final String TAG = MainThread.class.getSimpleName();

    /*
     * Create an array of statements of size MAX_STATEMENTS
     * this will be the maximum number of statements that one
     * can get wrong before level ends
     */

    private LinkedList <Statement> statementList = new LinkedList<Statement>();
    public Score score;

    public MainGamePanel(Context context){
        super (context);

        getHolder().addCallback(this);


        for (int i = 0; i < MAX_STATEMENTS; i++) {

            Statement statement;
            statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.statement), 0, 0);

            // Add to the list of statements
            statementList.add(statement);
        }

        // Initialize empty score board
        score = new Score();

        // create game loop thread
        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Keep adding new statements if there is room
        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;

        while (list.hasNext()){
            Statement s = list.next();


            s.setX(getWidth() / 2);
            s.setY(getHeight() + (++count * s.getBitmap().getHeight()) + OFFSET);

            if (count == 1){
                Log.d(TAG, "Statement height: " + s.getBitmap().getHeight() + " Width: " + s.getBitmap().getWidth() );
            }
            //int height = getHeight() + (++count * s.getBitmap().getHeight()) + OFFSET;
            //Log.d(TAG, "Height = " + height);
        }

        Log.d(TAG, "Width = " + getWidth() + " Height = " + getHeight());

        thread.setRunning(true);
        thread.start();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {

                thread.join();
                thread.setRunning(false);
                retry = false;

            } catch (InterruptedException e) {
                // try again shutting down the thread

            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());

        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;

        while (list.hasNext()){
            Statement s = list.next();

            Log.d(TAG, ++count +  "." + s.statement);

            if(!s.isTouched()){
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    // Delegate event handling to the droid
                    s.handleActionDown((int)event.getX(), (int)event.getY());

                    // check if in the lower part of the screen we exit
                    if (event.getY() > getHeight() - 10){
                        thread.setRunning(false);
                        ((Activity)getContext()).finish();
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

        //Iterate through statementList and draw
        ListIterator<Statement> list = statementList.listIterator();

        while (list.hasNext()){
            list.next().draw(canvas);
        }


    }


    public void update() {

        // Keep adding new statements if there is room
        ListIterator<Statement> list = statementList.listIterator();

        int count = 0;

        while (list.hasNext()){
            // check collision with top wall if heading up
            Statement s = list.next();

            if(s.destroy()){
                // pop statement from the linkedlist
                list.remove();

            } else if (s.getSpeed().getyDirection() == Speed.DIRECTION_UP
                    && s.getY() - s.getBitmap().getHeight() / 2 <= 250 + (++count * s.getBitmap().getHeight() + OFFSET)) {

                s.stopMoving();
                //statement.getSpeed().toggleYDirection();

            }

            // Update the moving statements
            s.update();
        }
    }

}
