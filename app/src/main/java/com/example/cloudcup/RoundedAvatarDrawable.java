package com.example.cloudcup;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


// a drawable that draws an oval

public class RoundedAvatarDrawable extends Drawable {
    private final Bitmap mBitmap;  // represent image that consist of pixels with a specified width,height,and color format.
    private final Paint mPaint; // Paint class holds the style and color information about how to draw geometries,text and bitmaps
    private final RectF mRectF; // Holds four float coordinates for a rectangle(left,top,right,bottom)
    private final int mBitmapWidth;
    private final int mBitmapHeight;

    public RoundedAvatarDrawable(Bitmap bitmap){
        mBitmap = bitmap;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // true -> smooths out the edges of image.
        mPaint.setDither(true); // true-> slow compared to false but it tries to distribute the error inherent in this process and reduce the visual artifacts.

        // shader used to draw a bitmap as a texture. The bitmap can be repeated or mirrored by setting the tiling mode.
        final BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        mPaint.setShader(shader);

        // NOTE -> we assume bitmap is properly scaled to current density.
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawOval(mRectF,mPaint);
    }

    @Override
    public void setAlpha(int i) { // assign color alpha value leaving its r,g,b values unchanged.
        if(mPaint.getAlpha() != i){
            mPaint.setAlpha(i);
            invalidateSelf();
        }
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter); // used to modify the color of each pixel drawn with that paint.
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT; // support translucency.
    }

    // other than implement methods.

    @Override
    protected void onBoundsChange(Rect bounds){
        super.onBoundsChange(bounds);
        mRectF.set(bounds); // copy the coordinates from bounds into this rectangle
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }


    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);  // Filtering affects the sampling of bitmaps when they are transformed.
        invalidateSelf();
    }

    @Override
    public void setDither(boolean dither) {
        mPaint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
