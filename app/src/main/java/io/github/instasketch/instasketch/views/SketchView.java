package io.github.instasketch.instasketch.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.instasketch.instasketch.R;

public class SketchView extends View {

    private Path drawPath;
    private Paint canvasPaint, drawPaint;
    private int paintColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float currentBrushSize, lastBrushSize;
    private Context mContext;

    private boolean eraseMode = false;


    public SketchView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public SketchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SketchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private void init(){
        currentBrushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = currentBrushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(currentBrushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.BUTT);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //create canvas of certain device size.
        super.onSizeChanged(w, h, oldw, oldh);
        //create Bitmap of certain w,h
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //apply bitmap to graphic to start drawing.
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        //redraw
        invalidate();
        return true;
    }

    //    The methods below implement the functionality of the sketch palette
    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        currentBrushSize = pixelAmount;
        // canvasPaint.setStrokeWidth(newSize);
        drawPaint.setStrokeWidth(newSize);

    }

    public void setPaintColor(int color) {
        drawPaint.setColor(color);
    }

    public float getPaintColor() { return paintColor; }


    public String dumpToFile() {
        File outputDir = mContext.getCacheDir(); // context being the Activity pointer
        FileOutputStream fo = null;
        try {
            File outputFile = File.createTempFile("sketch", ".png", outputDir);
            fo = new FileOutputStream(outputFile);
            canvasBitmap.compress(Bitmap.CompressFormat.PNG, 80, fo);
            fo.close();
            return outputFile.getAbsolutePath();
        }
        catch (Exception e){

        }
        return null;
    }

    public void setErase(boolean eraseMode){
        this.eraseMode = eraseMode;
        if(eraseMode){
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            drawPaint.setXfermode(null);
        }
    }

    public boolean isEraseMode(){
        return eraseMode;
    }
    public void setLastBrushSize(float newSize){
        lastBrushSize = newSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }
}
