package org.auditio.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * Created by auditio on 15-02-25.
 *
 * This class contains the main game thread that takes care of rendering and
 * updating the game screen.
 *
 * The game loop keeps track of average frame rate to determine whether or not
 * to skip frames to ensure consistency in display
 */
public class MainThread extends Thread {
    private final static int        MAX_FPS = 50;
    private final static int        MAX_FRAME_SKIPS = 5;
    private final static int        FRAME_PERIOD = 1000 / MAX_FPS;

    // flags to hold game state
    private boolean                 running;
    private SurfaceHolder           surfaceHolder;
    private GamePanel               gamePanel;
    private long                    timerStart;

    //private static final String     TAG = MainThread.class.getSimpleName();


    /* Constructing the main thread and initializing the timer when the
     * game was started.
     *
     * @param surfaceHolder The surface of the game to update
     * @param gamePanel The game panel to update and render
     *
     */
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
        this.timerStart = System.currentTimeMillis();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public void run() {
        Canvas canvas;

        long beginTime;
        long timeDiff = 0;
        int sleepTime;
        int frameSkipped;

        //Log.d(TAG, "****** Starting Game Loop *****")

        /* This is the game loop that renders and updates the game screen */
        while (running) {
            canvas = null;

            // lock canvas for exclusive pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas();

                beginTime = System.currentTimeMillis();
                frameSkipped = 0;

                // update game state and render canvas on the panel
                this.gamePanel.update();
                this.gamePanel.onDraw(canvas, (beginTime - this.timerStart) /1000);

                timeDiff = System.currentTimeMillis() - beginTime;
                sleepTime = (int) (FRAME_PERIOD - timeDiff);

                // If tasks completed prior to period end, put thread to sleep
                if(sleepTime > 0){
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {

                    }
                }

                // If tasks need longer to complete, need to skip frames
                while (sleepTime < 0 && frameSkipped < MAX_FRAME_SKIPS){
                    // Update state without rendering
                    this.gamePanel.update();

                    sleepTime += FRAME_PERIOD;
                    frameSkipped++;
                }
            } finally {
                // Unlock canvas unless its blank
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        //Log.d(TAG, "Game Loop Executed " + tickCount + " times");
    }
}