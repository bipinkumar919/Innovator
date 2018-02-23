package com.example.akshay.noblind;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.android.gms.vision.face.Face;

/**
 * Created by bipin on 28/10/17.
 */

class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    public int status = 1;


    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    String s;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;

    String gets(){
        return s;
    }

    FaceGraphic(GraphicOverlay overlay, int _status) {
        super(overlay);
        status = _status;

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void setId(int id) {
        mFaceId = id;
    }



    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }


    @Override
    public void draw(Canvas canvas) {

        Face face = mFace;
        if (face == null) {
            return;
        }

        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        //  canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        // canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        //  canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        //  canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        //  canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        // if (face.getIsLeftEyeOpenProbability()<0.4 && face.getIsRightEyeOpenProbability()<0.4){
        //    canvas.drawText("You are sleeping",x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);

        if(status == 1){
            if (face.getIsSmilingProbability()<0.2){
                canvas.drawText("Name - Akshay Kumar",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*4, mIdPaint);
                canvas.drawText("Branch - CSE",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*5, mIdPaint);
                canvas.drawText("Year - Second",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*6, mIdPaint);
                canvas.drawText("Interests - Plays Football, ",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*7, mIdPaint);
                canvas.drawText("makes apps, hangs out with friends",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*8, mIdPaint);
                s = "He is SAD";
            }
            else if (face.getIsSmilingProbability()<0.4 && face.getIsSmilingProbability()>0.2){
                canvas.drawText("Name - Akshay Kumar",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*4, mIdPaint);
                canvas.drawText("Branch - CSE",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*5, mIdPaint);
                canvas.drawText("Year - Second",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*6, mIdPaint);
                canvas.drawText("Interests - Plays Football, ",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*7, mIdPaint);
                canvas.drawText("makes apps, hangs out with friends",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*8, mIdPaint);
                s = "He is NEUTRAL";
            }
            else{
                canvas.drawText("Name - Akshay Kumar",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*4, mIdPaint);
                canvas.drawText("Branch - CSE",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*5, mIdPaint);
                canvas.drawText("Year - Second",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*6, mIdPaint);
                canvas.drawText("Interests - Plays Football, ",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*7, mIdPaint);
                canvas.drawText("makes apps, hangs out with friends",x + ID_X_OFFSET*2, y + ID_Y_OFFSET*8, mIdPaint);
                s = "He is HAPPY";
            }
        }

        else{
            canvas.drawText("Sorry I cannot recognize him.",x + ID_X_OFFSET*1, y + ID_Y_OFFSET*4, mIdPaint);
        }

        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
