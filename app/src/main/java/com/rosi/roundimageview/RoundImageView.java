package com.rosi.roundimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

@SuppressWarnings("unused")
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {
  private static final float DEFAULT_BORDER_WIDTH = 10;
  private static final float DEFAULT_SHADOW_RADIUS = 5;
  private static final int DEFAULT_SHADOW_COLOR = 0xff000000;
  private static final int DEFAULT_BORDER_COLOR = 0xffffffff;
  private float strokeWidth = DEFAULT_BORDER_WIDTH;
  private int borderColor = DEFAULT_BORDER_COLOR;
  private float shadowRadius = DEFAULT_SHADOW_RADIUS;
  private int shadowColor = DEFAULT_SHADOW_COLOR;
  private RectF imageRect;
  private Paint maskPaint;
  private Paint userImagePaint;
  private Paint borderPaint;
  private Bitmap theImage;

  public RoundImageView(Context context) {
    this(context, null);
  }

  public RoundImageView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  public int getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(int borderColor) {
    this.borderColor = borderColor;
    borderPaint.setColor(borderColor);
    invalidate();
  }

  public int getShadowColor() {
    return shadowColor;
  }

  public void setShadowColor(int shadowColor) {
    this.shadowColor = shadowColor;
    borderPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
    invalidate();
  }

  public float getShadowRadius() {
    return shadowRadius;
  }

  public void setShadowRadius(float shadowRadius) {
    this.shadowRadius = shadowRadius;
    borderPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
    updateImageSize();
    invalidate();
  }

  public float getStrokeWidth() {
    return strokeWidth;
  }

  public void setStrokeWidth(float strokeWidth) {
    this.strokeWidth = strokeWidth;
    borderPaint.setStrokeWidth(strokeWidth);
    updateImageSize();
    invalidate();
  }

  @Override public void setImageDrawable(@Nullable Drawable drawable) {
    if (drawable != null) {
      theImage = drawableToBitmap(drawable);

    }
    super.setImageDrawable(drawable);

  }

  @Override
  public void onDraw(Canvas canvas) {

    Drawable drawable = getDrawable();
    if (drawable == null) {
      return;
    }

    //draw border
    float radius = Math.min(imageRect.width() / 2, imageRect.height() / 2) + (strokeWidth / 2);
    canvas.drawCircle(imageRect.centerX(), imageRect.centerY(), radius, borderPaint);

    //draw user image
    Bitmap roundImage = createRoundImage(theImage, imageRect.width());
    canvas.drawBitmap(roundImage, newRectFromBitmap(roundImage), imageRect, null);
    roundImage.recycle();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    updateImageSize();
  }

  private static Rect newRectFromBitmap(Bitmap bitmap) {
    return new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
  }

  /**
   * Create new image from the specified src image, with a round mask.
   *
   * @param src  the original image.
   * @param size the width and height of the new image.
   */
  private Bitmap createRoundImage(Bitmap src, float size) {
    //create empty bitmap
    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
    int tSize = (int) Math.max(1, size);
    Bitmap bmp = Bitmap.createBitmap(tSize, tSize, conf);
    Canvas canvas = new Canvas(bmp);

    //draw mask
    canvas.drawCircle(size / 2, size / 2, size / 2, maskPaint);
    canvas.drawBitmap(src, newRectFromBitmap(src),
        new RectF(
            0,
            0,
            size,
            size),
        userImagePaint);

    return bmp;
  }

  private Bitmap drawableToBitmap(Drawable drawable) {
    if (drawable == null) {
      return null;
    } else if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    }

    int width = drawable.getIntrinsicWidth();
    int height = drawable.getIntrinsicHeight();

    if (!(width > 0 && height > 0)) {
      return null;
    }

    Bitmap bitmap = null;
    try {
      bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
    } catch (OutOfMemoryError e) {
      e.printStackTrace();
    }
    return bitmap;
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    // Load the styled attributes and set their properties
    TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
    try {
      strokeWidth = attributes.getDimension(R.styleable.RoundImageView_border_width, DEFAULT_BORDER_WIDTH);
      shadowRadius = attributes.getDimension(R.styleable.RoundImageView_shadow_radius, DEFAULT_SHADOW_RADIUS);
      shadowColor = attributes.getColor(R.styleable.RoundImageView_shadow_color, DEFAULT_SHADOW_COLOR);
      borderColor = attributes.getColor(R.styleable.RoundImageView_border_color, DEFAULT_BORDER_COLOR);
      if (shadowRadius > 0) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
      }
    } finally {
      attributes.recycle();
    }

    maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    maskPaint.setColor(0xffffffff);
    maskPaint.setStyle(Paint.Style.FILL_AND_STROKE);

    userImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    userImagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

    borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(strokeWidth);
    borderPaint.setColor(borderColor);
    borderPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
  }

  private void updateImageSize() {
    int size = Math.min(getWidth(), getHeight());
    if (imageRect == null) {
      imageRect = new RectF();
    }
    imageRect.set(
        shadowRadius + strokeWidth,
        shadowRadius + strokeWidth,
        size - shadowRadius - strokeWidth,
        size - shadowRadius - strokeWidth);
  }
}
