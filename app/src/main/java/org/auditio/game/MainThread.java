package org.auditio.game;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;


/**
 * Created by auditio on 15-02-25.
 */
public class MainThread extends Thread {
    private final static int        MAX_FPS = 50;
    private final static int        MAX_FRAME_SKIPS = 5;
    private final static int        FRAME_PERIOD = 1000 / MAX_FPS;

    // flags to hold game state
    private boolean                 running;
    private SurfaceHolder           surfaceHolder;
    private GamePanel           gamePanel;

    //private static final String     TAG = MainThread.class.getSimpleName();


    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Canvas canvas;

        long beginTime;
        long timeDiff;
        int sleepTime;
        int frameSkipped;


        long tickCount = 0L;
        //Log.d(TAG, "****** Starting Game Loop *****");

        while (running) {
            canvas = null;

            // lock canvas for exclusive pixel editing
            try {
                canvas = this.surfaceHolder.lockCanvas();

                beginTime = System.currentTimeMillis();
                frameSkipped = 0;

                // update game state and render canvas on the panel
                this.gamePanel.update();
                this.gamePanel.onDraw(canvas);

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

            tickCount++;
        }

        //Log.d(TAG, "Game Loop Executed " + tickCount + " times");
    }
}