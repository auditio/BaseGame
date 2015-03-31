package org.auditio.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by auditio on 15-03-17.
 */
public class Glyphs {

        private static final String TAG = Glyphs.class.getSimpleName();
        private Bitmap bitmap;	// bitmap containing the character map/sheet

        // Map to associate a bitmap to each character
        private Map<Character, Bitmap> glyphs = new HashMap<Character, Bitmap>(62);

        private int width;	// width in pixels of one character
        private int height;	// height in pixels of one character

        // the characters in the English alphabet
        private char[] charactersL = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z' };
        private char[] charactersU = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z' };
        private char[] numbers = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '(', '!', '@', '#', '$', '%', '&', '.', ',', '?', ':', ';', ')', '+', '-', '=','/', ' ' };

        public Glyphs(Bitmap bitmap) {
            super();
            this.bitmap = bitmap;
            this.width = 23*3;
            this.height = 30*3;
            // Cutting up the glyphs
            // Starting with the first row - lower cases
            for (int i = 0; i < 26; i++) {
                glyphs.put(charactersL[i], Bitmap.createBitmap(bitmap,
                    (i * width), 0, width, height));
            }
            Log.d(TAG, "Lowercases initialised");

            // Continuing with the second row - upper cases
            // Note that the row starts at 15px - hardcoded
            for (int i = 0; i < 26; i++) {
                glyphs.put(charactersU[i], Bitmap.createBitmap(bitmap,
                    (i * width), 45, width, height));
            }
            // row 3 for numbers
            Log.d(TAG, "Uppercases initialised");
            for (int i = 0; i < numbers.length; i++) {
                glyphs.put(numbers[i], Bitmap.createBitmap(bitmap,
                    (i * width), (90 * 3), width, height));
            }
            Log.d(TAG, "Numbers initialised");

        }

        /**
         * Draws the string onto the canvas at <code>x</code> and <code>y</code>
         * @param canvas
         */
        public void drawString(Canvas canvas, String text, int x, int y) {
            if (canvas == null) {
                Log.d(TAG, "Canvas is null");
            }

            // Set new x to center the statement
            x -= text.length() / 2 * width;

            for (int i = 0; i < text.length(); i++) {
                Character ch = text.charAt(i);
                //Log.d(TAG, "Trying to draw: " + ch);
                if (glyphs.get(ch) != null) {
                    //Log.d (TAG, "Drawing" + glyphs.get(ch).getWidth() + " " + glyphs.get(ch).getHeight());
                    canvas.drawBitmap(glyphs.get(ch), x + (i * width), y, null);
                }
            }
        }
}
