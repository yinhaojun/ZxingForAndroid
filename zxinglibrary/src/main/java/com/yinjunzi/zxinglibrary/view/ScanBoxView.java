package com.yinjunzi.zxinglibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yinjunzi.zxinglibrary.R;
import com.yinjunzi.zxinglibrary.embedded.barcodescanner.CameraPreview;
import com.yinjunzi.zxinglibrary.util.QRCodeUtil;

public class ScanBoxView extends View {
    private int mMoveStepDistance;
    private int mAnimDelayTime;

    private Rect mFramingRect;
    private float mScanLineTop;
    private float mScanLineLeft;
    private Paint mPaint;
    private TextPaint mTipPaint;

    private int mMaskColor;
    private int mCornerColor;
    private int mCornerLength;
    private int mCornerSize;
    private int mScanLineSize;
    private int mScanLineColor;
    private int mScanLineMargin;
    private boolean mIsShowDefaultScanLineDrawable;
    private Drawable mCustomScanLineDrawable;
    private Bitmap mScanLineBitmap;
    private int mBorderSize;
    private int mBorderColor;
    private int mAnimTime;
    private int mRectWidth;
    private String mQRCodeTipText;
    private String mTipText;
    private int mTipTextSize;
    private int mTipTextColor;
    private boolean mIsTipTextBelowRect;
    private int mTipTextMargin;
    private boolean mIsShowTipTextAsSingleLine;
    private int mTipBackgroundColor;
    private boolean mIsShowTipBackground;
    private boolean mIsScanLineReverse;

    private Bitmap mOriginQRCodeScanLineBitmap;
    private Bitmap mOriginBarCodeScanLineBitmap;

    protected CameraPreview cameraPreview;
    //    // Cache the framingRect and previewFramingRect, so that we can still draw it after the preview
//    // stopped.
//    protected Rect framingRect;
//    protected Rect previewFramingRect;
    private float mHalfCornerSize;
    private StaticLayout mTipTextSl;
    private int mTipBackgroundRadius;

    public ScanBoxView(Context context) {
        super(context);
        init(context);
    }

    public ScanBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initCustomAttrs(context, attrs);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mMaskColor = Color.parseColor("#33FFFFFF");
        mCornerColor = Color.WHITE;
        mCornerLength = QRCodeUtil.dp2px(context, 20);
        mCornerSize = QRCodeUtil.dp2px(context, 3);
        mScanLineSize = QRCodeUtil.dp2px(context, 1);
        mScanLineColor = Color.WHITE;
        mScanLineMargin = 0;
        mIsShowDefaultScanLineDrawable = false;
        mCustomScanLineDrawable = null;
        mScanLineBitmap = null;
        mBorderSize = QRCodeUtil.dp2px(context, 1);
        mBorderColor = Color.WHITE;
        mAnimTime = 1000;
        mRectWidth = QRCodeUtil.dp2px(context, 200);
        mMoveStepDistance = QRCodeUtil.dp2px(context, 2);
        mTipText = null;
        mTipTextSize = QRCodeUtil.sp2px(context, 14);
        mTipTextColor = Color.WHITE;
        mIsTipTextBelowRect = false;
        mTipTextMargin = QRCodeUtil.dp2px(context, 20);
        mIsShowTipTextAsSingleLine = false;
        mTipBackgroundColor = Color.parseColor("#22000000");
        mIsShowTipBackground = false;
        mIsScanLineReverse = false;

        mTipPaint = new TextPaint();
        mTipPaint.setAntiAlias(true);

        mTipBackgroundRadius = QRCodeUtil.dp2px(context, 4);
    }


    public void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanBoxView);
        final int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();

        afterInitCustomAttrs();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.ScanBoxView_cornerSize) {
            mCornerSize = typedArray.getDimensionPixelSize(attr, mCornerSize);
        } else if (attr == R.styleable.ScanBoxView_cornerLength) {
            mCornerLength = typedArray.getDimensionPixelSize(attr, mCornerLength);
        } else if (attr == R.styleable.ScanBoxView_scanLineSize) {
            mScanLineSize = typedArray.getDimensionPixelSize(attr, mScanLineSize);
        } else if (attr == R.styleable.ScanBoxView_maskColor) {
            mMaskColor = typedArray.getColor(attr, mMaskColor);
        } else if (attr == R.styleable.ScanBoxView_cornerColor) {
            mCornerColor = typedArray.getColor(attr, mCornerColor);
        } else if (attr == R.styleable.ScanBoxView_scanLineColor) {
            mScanLineColor = typedArray.getColor(attr, mScanLineColor);
        } else if (attr == R.styleable.ScanBoxView_scanLineMargin) {
            mScanLineMargin = typedArray.getDimensionPixelSize(attr, mScanLineMargin);
        } else if (attr == R.styleable.ScanBoxView_isShowDefaultScanLineDrawable) {
            mIsShowDefaultScanLineDrawable = typedArray.getBoolean(attr, mIsShowDefaultScanLineDrawable);
        } else if (attr == R.styleable.ScanBoxView_customScanLineDrawable) {
            mCustomScanLineDrawable = typedArray.getDrawable(attr);
        } else if (attr == R.styleable.ScanBoxView_borderSize) {
            mBorderSize = typedArray.getDimensionPixelSize(attr, mBorderSize);
        } else if (attr == R.styleable.ScanBoxView_borderColor) {
            mBorderColor = typedArray.getColor(attr, mBorderColor);
        } else if (attr == R.styleable.ScanBoxView_animTime) {
            mAnimTime = typedArray.getInteger(attr, mAnimTime);
        } else if (attr == R.styleable.ScanBoxView_qrCodeTipText) {
            mQRCodeTipText = typedArray.getString(attr);
        } else if (attr == R.styleable.ScanBoxView_tipTextSize) {
            mTipTextSize = typedArray.getDimensionPixelSize(attr, mTipTextSize);
        } else if (attr == R.styleable.ScanBoxView_tipTextColor) {
            mTipTextColor = typedArray.getColor(attr, mTipTextColor);
        } else if (attr == R.styleable.ScanBoxView_isTipTextBelowRect) {
            mIsTipTextBelowRect = typedArray.getBoolean(attr, mIsTipTextBelowRect);
        } else if (attr == R.styleable.ScanBoxView_tipTextMargin) {
            mTipTextMargin = typedArray.getDimensionPixelSize(attr, mTipTextMargin);
        } else if (attr == R.styleable.ScanBoxView_isShowTipTextAsSingleLine) {
            mIsShowTipTextAsSingleLine = typedArray.getBoolean(attr, mIsShowTipTextAsSingleLine);
        } else if (attr == R.styleable.ScanBoxView_isShowTipBackground) {
            mIsShowTipBackground = typedArray.getBoolean(attr, mIsShowTipBackground);
        } else if (attr == R.styleable.ScanBoxView_tipBackgroundColor) {
            mTipBackgroundColor = typedArray.getColor(attr, mTipBackgroundColor);
        } else if (attr == R.styleable.ScanBoxView_isScanLineReverse) {
            mIsScanLineReverse = typedArray.getBoolean(attr, mIsScanLineReverse);
        }
    }

    private void afterInitCustomAttrs() {

        if (mCustomScanLineDrawable != null) {
            mOriginQRCodeScanLineBitmap = ((BitmapDrawable) mCustomScanLineDrawable).getBitmap();
        }
        if (mOriginQRCodeScanLineBitmap == null) {
            mOriginQRCodeScanLineBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.qrcode_default_scan_line);
            mOriginQRCodeScanLineBitmap = QRCodeUtil.makeTintBitmap(mOriginQRCodeScanLineBitmap, mScanLineColor);
        }
        mOriginBarCodeScanLineBitmap = QRCodeUtil.adjustPhotoRotation(mOriginQRCodeScanLineBitmap, 90);

        mHalfCornerSize = 1.0f * mCornerSize / 2;

        mTipPaint.setTextSize(mTipTextSize);
        mTipPaint.setColor(mTipTextColor);

        setIsBarcode();
    }

    public void setCameraPreview(CameraPreview view) {
        this.cameraPreview = view;
        view.addStateListener(new CameraPreview.StateListener() {
            @Override
            public void previewSized() {
                refreshSizes();
                invalidate();
            }

            @Override
            public void previewStarted() {

            }

            @Override
            public void previewStopped() {

            }

            @Override
            public void cameraError(Exception error) {

            }

            @Override
            public void cameraClosed() {

            }
        });
    }

    protected void refreshSizes() {
        if (cameraPreview == null) {
            return;
        }
        Rect framingRect = cameraPreview.getFramingRect();
        if (framingRect != null) {
            this.mFramingRect = framingRect;
            mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mFramingRect == null) {
            return;
        }

        // 画遮罩层
        drawMask(canvas);

        // 画边框线
        drawBorderLine(canvas);

        // 画四个直角的线
        drawCornerLine(canvas);

        // 画扫描线
        drawScanLine(canvas);
        // 画提示文本
        drawTipText(canvas);

        // 移动扫描线的位置
        moveScanLine();

    }

    /**
     * 画遮罩层
     *
     * @param canvas
     */
    private void drawMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mMaskColor != Color.TRANSPARENT) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mMaskColor);
            canvas.drawRect(0, 0, width, mFramingRect.top, mPaint);
            canvas.drawRect(0, mFramingRect.top, mFramingRect.left, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(mFramingRect.right + 1, mFramingRect.top, width, mFramingRect.bottom + 1, mPaint);
            canvas.drawRect(0, mFramingRect.bottom + 1, width, height, mPaint);
        }
    }

    /**
     * 画边框线
     *
     * @param canvas
     */
    private void drawBorderLine(Canvas canvas) {
        if (mBorderSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mBorderColor);
            mPaint.setStrokeWidth(mBorderSize);
            canvas.drawRect(mFramingRect, mPaint);
        }
    }

    /**
     * 画四个直角的线
     *
     * @param canvas
     */
    private void drawCornerLine(Canvas canvas) {
        if (mHalfCornerSize > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(mCornerColor);
            mPaint.setStrokeWidth(mCornerSize);
            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.top, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.top - mHalfCornerSize, mFramingRect.left, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.top, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.top, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.top - mHalfCornerSize, mFramingRect.right, mFramingRect.top - mHalfCornerSize + mCornerLength, mPaint);

            canvas.drawLine(mFramingRect.left - mHalfCornerSize, mFramingRect.bottom, mFramingRect.left - mHalfCornerSize + mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.left, mFramingRect.bottom + mHalfCornerSize, mFramingRect.left, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
            canvas.drawLine(mFramingRect.right + mHalfCornerSize, mFramingRect.bottom, mFramingRect.right + mHalfCornerSize - mCornerLength, mFramingRect.bottom, mPaint);
            canvas.drawLine(mFramingRect.right, mFramingRect.bottom + mHalfCornerSize, mFramingRect.right, mFramingRect.bottom + mHalfCornerSize - mCornerLength, mPaint);
        }
    }

    /**
     * 画扫描线
     *
     * @param canvas
     */
    private void drawScanLine(Canvas canvas) {
        if (mScanLineBitmap != null) {
            RectF lineRect = new RectF(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineBitmap.getHeight());
            canvas.drawBitmap(mScanLineBitmap, null, lineRect, mPaint);
        } else {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mScanLineColor);
            canvas.drawRect(mFramingRect.left + mHalfCornerSize + mScanLineMargin, mScanLineTop, mFramingRect.right - mHalfCornerSize - mScanLineMargin, mScanLineTop + mScanLineSize, mPaint);
        }
    }

    /**
     * 画提示文本
     *
     * @param canvas
     */
    private void drawTipText(Canvas canvas) {
        if (TextUtils.isEmpty(mTipText) || mTipTextSl == null) {
            return;
        }

        if (mIsTipTextBelowRect) {
            if (mIsShowTipBackground) {
                mPaint.setColor(mTipBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);
                if (mIsShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    mTipPaint.getTextBounds(mTipText, 0, mTipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - mTipBackgroundRadius;
                    canvas.drawRoundRect(new RectF(left, mFramingRect.bottom + mTipTextMargin - mTipBackgroundRadius, left + tipRect.width() + 2 * mTipBackgroundRadius, mFramingRect.bottom + mTipTextMargin + mTipTextSl.getHeight() + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                } else {
                    canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.bottom + mTipTextMargin - mTipBackgroundRadius, mFramingRect.right, mFramingRect.bottom + mTipTextMargin + mTipTextSl.getHeight() + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                }
            }

            canvas.save();
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0, mFramingRect.bottom + mTipTextMargin);
            } else {
                canvas.translate(mFramingRect.left + mTipBackgroundRadius, mFramingRect.bottom + mTipTextMargin);
            }
            mTipTextSl.draw(canvas);
            canvas.restore();
        } else {
            if (mIsShowTipBackground) {
                mPaint.setColor(mTipBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);

                if (mIsShowTipTextAsSingleLine) {
                    Rect tipRect = new Rect();
                    mTipPaint.getTextBounds(mTipText, 0, mTipText.length(), tipRect);
                    float left = (canvas.getWidth() - tipRect.width()) / 2 - mTipBackgroundRadius;
                    canvas.drawRoundRect(new RectF(left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight() - mTipBackgroundRadius, left + tipRect.width() + 2 * mTipBackgroundRadius, mFramingRect.top - mTipTextMargin + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                } else {
                    canvas.drawRoundRect(new RectF(mFramingRect.left, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight() - mTipBackgroundRadius, mFramingRect.right, mFramingRect.top - mTipTextMargin + mTipBackgroundRadius), mTipBackgroundRadius, mTipBackgroundRadius, mPaint);
                }
            }

            canvas.save();
            if (mIsShowTipTextAsSingleLine) {
                canvas.translate(0, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight());
            } else {
                canvas.translate(mFramingRect.left + mTipBackgroundRadius, mFramingRect.top - mTipTextMargin - mTipTextSl.getHeight());
            }
            mTipTextSl.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 移动扫描线的位置
     */
    private void moveScanLine() {

        // 处理非网格扫描图片的情况
        mScanLineTop += mMoveStepDistance;
        int scanLineSize = mScanLineSize;
        if (mScanLineBitmap != null) {
            scanLineSize = mScanLineBitmap.getHeight();
        }

        if (mIsScanLineReverse) {
            if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize || mScanLineTop < mFramingRect.top + mHalfCornerSize) {
                mMoveStepDistance = -mMoveStepDistance;
            }
        } else {
            if (mScanLineTop + scanLineSize > mFramingRect.bottom - mHalfCornerSize) {
                mScanLineTop = mFramingRect.top + mHalfCornerSize + 0.5f;
            }
        }
        postInvalidateDelayed(mAnimDelayTime, mFramingRect.left, mFramingRect.top, mFramingRect.right, mFramingRect.bottom);
    }

    public void setIsBarcode() {
        if (mCustomScanLineDrawable != null || mIsShowDefaultScanLineDrawable) {
            mScanLineBitmap = mOriginQRCodeScanLineBitmap;
        }

        mTipText = mQRCodeTipText;
        mAnimDelayTime = (int) ((1.0f * mAnimTime * mMoveStepDistance) / mRectWidth);

        if (!TextUtils.isEmpty(mTipText)) {
            if (mIsShowTipTextAsSingleLine) {
                mTipTextSl = new StaticLayout(mTipText, mTipPaint, QRCodeUtil.getScreenResolution(getContext()).x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
            } else {
                mTipTextSl = new StaticLayout(mTipText, mTipPaint, mRectWidth - 2 * mTipBackgroundRadius, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, true);
            }
        }

//        calFramingRect();

        postInvalidate();
    }


    public int getMaskColor() {
        return mMaskColor;
    }

    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
    }

    public int getCornerColor() {
        return mCornerColor;
    }

    public void setCornerColor(int cornerColor) {
        mCornerColor = cornerColor;
    }

    public int getCornerLength() {
        return mCornerLength;
    }

    public void setCornerLength(int cornerLength) {
        mCornerLength = cornerLength;
    }

    public int getCornerSize() {
        return mCornerSize;
    }

    public void setCornerSize(int cornerSize) {
        mCornerSize = cornerSize;
    }

    public int getScanLineSize() {
        return mScanLineSize;
    }

    public void setScanLineSize(int scanLineSize) {
        mScanLineSize = scanLineSize;
    }

    public int getScanLineColor() {
        return mScanLineColor;
    }

    public void setScanLineColor(int scanLineColor) {
        mScanLineColor = scanLineColor;
    }

    public int getScanLineMargin() {
        return mScanLineMargin;
    }

    public void setScanLineMargin(int scanLineMargin) {
        mScanLineMargin = scanLineMargin;
    }

    public boolean isShowDefaultScanLineDrawable() {
        return mIsShowDefaultScanLineDrawable;
    }

    public void setShowDefaultScanLineDrawable(boolean showDefaultScanLineDrawable) {
        mIsShowDefaultScanLineDrawable = showDefaultScanLineDrawable;
    }

    public Drawable getCustomScanLineDrawable() {
        return mCustomScanLineDrawable;
    }

    public void setCustomScanLineDrawable(Drawable customScanLineDrawable) {
        mCustomScanLineDrawable = customScanLineDrawable;
    }

    public Bitmap getScanLineBitmap() {
        return mScanLineBitmap;
    }

    public void setScanLineBitmap(Bitmap scanLineBitmap) {
        mScanLineBitmap = scanLineBitmap;
    }

    public int getBorderSize() {
        return mBorderSize;
    }

    public void setBorderSize(int borderSize) {
        mBorderSize = borderSize;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        mBorderColor = borderColor;
    }

    public int getAnimTime() {
        return mAnimTime;
    }

    public void setAnimTime(int animTime) {
        mAnimTime = animTime;
    }

    public String getQRCodeTipText() {
        return mQRCodeTipText;
    }

    public void setQRCodeTipText(String qrCodeTipText) {
        mQRCodeTipText = qrCodeTipText;
    }

    public String getTipText() {
        return mTipText;
    }

    public void setTipText(String tipText) {
        mTipText = tipText;
    }

    public int getTipTextColor() {
        return mTipTextColor;
    }

    public void setTipTextColor(int tipTextColor) {
        mTipTextColor = tipTextColor;
    }

    public int getTipTextSize() {
        return mTipTextSize;
    }

    public void setTipTextSize(int tipTextSize) {
        mTipTextSize = tipTextSize;
    }

    public boolean isTipTextBelowRect() {
        return mIsTipTextBelowRect;
    }

    public void setTipTextBelowRect(boolean tipTextBelowRect) {
        mIsTipTextBelowRect = tipTextBelowRect;
    }

    public int getTipTextMargin() {
        return mTipTextMargin;
    }

    public void setTipTextMargin(int tipTextMargin) {
        mTipTextMargin = tipTextMargin;
    }

    public boolean isShowTipTextAsSingleLine() {
        return mIsShowTipTextAsSingleLine;
    }

    public void setShowTipTextAsSingleLine(boolean showTipTextAsSingleLine) {
        mIsShowTipTextAsSingleLine = showTipTextAsSingleLine;
    }

    public boolean isShowTipBackground() {
        return mIsShowTipBackground;
    }

    public void setShowTipBackground(boolean showTipBackground) {
        mIsShowTipBackground = showTipBackground;
    }

    public int getTipBackgroundColor() {
        return mTipBackgroundColor;
    }

    public void setTipBackgroundColor(int tipBackgroundColor) {
        mTipBackgroundColor = tipBackgroundColor;
    }

    public boolean isScanLineReverse() {
        return mIsScanLineReverse;
    }

    public void setScanLineReverse(boolean scanLineReverse) {
        mIsScanLineReverse = scanLineReverse;
    }

    public float getHalfCornerSize() {
        return mHalfCornerSize;
    }

    public void setHalfCornerSize(float halfCornerSize) {
        mHalfCornerSize = halfCornerSize;
    }

    public StaticLayout getTipTextSl() {
        return mTipTextSl;
    }

    public void setTipTextSl(StaticLayout tipTextSl) {
        mTipTextSl = tipTextSl;
    }

    public int getTipBackgroundRadius() {
        return mTipBackgroundRadius;
    }

    public void setTipBackgroundRadius(int tipBackgroundRadius) {
        mTipBackgroundRadius = tipBackgroundRadius;
    }
}