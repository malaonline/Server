package com.malalaoshi.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
        OnScaleGestureListener, OnTouchListener {
    private static String TAG = "ZoomImageView";

    private boolean initOnce;

    /**
     * 初始化时缩放的值
     */
    private float mInitScale;

    /**
     * 双击达到的值
     */
    private float mMidScale;

    /**
     * 最大放大值
     */
    private float mMaxScale;

    private Matrix mScaleMatrix;

    /**
     * 捕获用户多指触控时缩放比例
     */
    private ScaleGestureDetector mScaleGestureDetector;

    // ---------------------------自由移动
    /**
     * 记录上一个上一次多点触控的数量
     */
    private int mLastPointCount;

    private float mLastX;
    private float mLastY;

    private int mTouchSlop;// 系统的 判断用户手指是否移动的比较值
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    // --------------------双击放大与缩小
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;//是否正在双击缩放中,这时不予处理


    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        super.setScaleType(ScaleType.MATRIX);// 设置缩放类型

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);

        // Distance in dips a touch can wander before we think the user is
        // scrolling
        mTouchSlop = ViewConfiguration.get(context).getTouchSlop();
        //双击事件处理
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale)
                            return true;

                        float x = e.getX();
                        float y = e.getY();

                        if (getScale() < mMidScale) {
//							mScaleMatrix.postScale(mMidScale / getScale(),
//									mMidScale / getScale(), x, y);
//							setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mMidScale, x, y), 16);
                            isAutoScale = true;
                        } else {
//							mScaleMatrix.postScale(mInitScale / getScale(),
//									mInitScale / getScale(), x, y);
//							setImageMatrix(mScaleMatrix);
                            postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });
    }

    /**
     * 梯度放大
     *
     * @author Administrator
     */
    private class AutoScaleRunnable implements Runnable {
        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        // 缩放中心点
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.93f;

        private float tmpScale;

        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }

            if (getScale() > mTargetScale) {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            float currentScale = getScale();

            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);//再执行run
            } else {
                //设置为目标值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;//缩放结束,可以再次双击
            }

        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 当全局布局绘画完成后,获取ImageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {
        initImage();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initOnce=false;

    }

    private void initImage() {
        if (!initOnce) {
        // 得控件的宽和高
        int width = getWidth();
        int height = getHeight();

        // 得到图片 以及宽和高
        Drawable d = getDrawable();
        if (d == null) {
            return;
        }

        int dw = d.getIntrinsicWidth();
        int dh = d.getIntrinsicHeight();

        float scale = 1.0f;
        if (dw >= width && dh <= height) {
            scale = width * 1.0f / dw;
        }

        if (dh >= height && dw <= width) {
            scale = height * 1.0f / dh;
        }

        if ((dw >= width && dh >= height) || (dw <= width && dh <= height)) {
            scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        }

        // if(dw < width && dh < height){
        // scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
        // }

        // Log.i("--", "width = " + width + ", height = " + height +
        // ", dw = " + dw
        // + ", dh = " + dh + ",, scale = " + scale);

        mInitScale = scale;
        mMaxScale = 4 * mInitScale;
        mMidScale = 2 * mInitScale;

        // 将图片移动到控件的中心
        int dx = getWidth() / 2 - dw / 2;
        int dy = getHeight() / 2 - dh / 2;
        Log.i(TAG, "mInitScale:" + mInitScale + " mMaxScale:" + mMaxScale + " mMidScale:" + mMidScale + "dw:" + dw + "dh:" + dh + "width:" + width + "height:" + height + "dx:" + dx + "dy:" + dy);
        //重置后平移
        mScaleMatrix.setTranslate(dx, dy);
        mScaleMatrix.postScale(mInitScale, mInitScale, getWidth() / 2,
                getHeight() / 2);
        setImageMatrix(mScaleMatrix);
            initOnce = true;
        }
    }


    /**
     * 得到当前图片的缩放值
     *
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // Log.i("--","onScale");
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        // 缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }

            if (scale * scaleFactor > mMaxScale) {
                scale = mMaxScale / scale;
            }

            // 缩放
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    /**
     * 获得图片放大或缩小的宽和高，以及right、left、top、bottom
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable d = getDrawable();
        if (d != null) {
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }

        return rectF;
    }

    /**
     * 在缩放时，不断检测是否在最中间，是否露出边界
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }

            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }

            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        // 如果宽度或者高度小于控件的宽和高，则让其居中
        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
        /** 这里必须return true */
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        // 中心点的位置
        float x = 0;
        float y = 0;
        // 拿到多点触控的数量
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            x += event.getX();
            y += event.getY();
        }

        x /= pointCount;
        y /= pointCount;

        if (mLastPointCount != pointCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
            mLastPointCount = pointCount;
        }

        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    //不希望被父控件拦截
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    //不希望被父控件拦截
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }


                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    // 完成图片的移动
//				RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;

                        // 如果宽度小于控件的宽度, 不能横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        // 如果高度小于控件的高度，不能纵向移动
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }

                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointCount = 0;
                break;
        }

        return true;
    }

    /**
     * 当移动时，进行边界检查,否则可以自由移动
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }

        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }

        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }

        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    /**
     * 判断是否移动
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {

        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

}
