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


public class MainGamePanel extends SurfaceView implements
        SurfaceHolder.Callback{

    private MainThread thread;
    private static final String TAG = MainThread.class.getSimpleName();
    private Statement statement;


    public MainGamePanel(Context context){
        super (context);

        getHolder().addCallback(this);

        statement = new Statement(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 0, 0);

        //Log.d(TAG, "Initial Height: " + getHeight());

        // create game loop thread
        thread = new MainThread(getHolder(), this);

        setFocusable(true);
    }

    @Override

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Need to set initial coordinate again since the view isn't created when
        // MainGamePanel was instantiated so getHeight() would
        statement.setX(50);
        statement.setY(getHeight());
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

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            // Delegate event handling to the droid
            statement.handleActionDown((int)event.getX(), (int)event.getY());

            // check if in the lower part of the screen we exit
            if (event.getY() > getHeight() - 50){
                thread.setRunning(false);
                ((Activity)getContext()).finish();
            } else {
                Log.d (TAG, "Coords: x=" + event.getX() + ", y=" + event.getY());
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE){
            // the gestures
            if(statement.isTouched()){
                // the droid was picked up and being dragged
                statement.setX((int)event.getX());
                statement.setY((int)event.getY());
            }
        }

        //return super.onTouchEvent(event);

        return true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // fills the canvas with black
        canvas.drawColor(Color.BLACK);
        statement.draw(canvas);
    }

    public void update() {
        // check collision with right wall if heading right
        /*if (droid.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
                && droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
            droid.getSpeed().toggleXDirection();
        }

        // check collision with left wall if heading left
        /*if (droid.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
                && droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
            droid.getSpeed().toggleXDirection();
        }*/

        // check collision with bottom wall if heading down
        if (statement.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
                && statement.getY() + statement.getBitmap().getHeight() / 2 >= getHeight()) {

            statement.getSpeed().toggleYDirection();

        }

        // check collision with top wall if heading up
        if (statement.getSpeed().getyDirection() == Speed.DIRECTION_UP
                && statement.getY() - statement.getBitmap().getHeight() / 2 <= 0) {
            statement.getSpeed().toggleYDirection();

        }

        // Update the lone droid
        statement.update();

    }

}
