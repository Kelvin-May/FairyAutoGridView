package com.k.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.k.utils.DisplayUtility;
import com.k.utils.ViewUtil;

/**
 * Created by Kelvin on 15/9/7.
 */
public class FairyView extends ViewGroup implements SurfaceHolder.Callback {

    private static final String TAG = "FairyView";

    private SurfaceHolder holder;

    private ViewRunnable mViewRunnable;

    private float copyValue = 1.5f;

    private final float DISPLAY_OFFSET_Y = DisplayUtility.dip2px(25);

    public FairyView(Context context) {
        super(context);
        init(context, null, -1);
    }

    public FairyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public FairyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(l, t, r, b);
            }
        }
    }

    /**
     * Init SurfaceView we need and init Thread draw Canvas
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // TODO Auto-generated constructor stub
        SurfaceView s = new SurfaceView(context, attrs, defStyleAttr);
        holder = s.getHolder();
        holder.addCallback(this);
        s.setZOrderOnTop(true);//设置画布  背景透明
        holder.setFormat(PixelFormat.TRANSLUCENT);
        mViewRunnable = new ViewRunnable(holder);
        addView(s);
        new Thread(mViewRunnable, TAG).start();
    }

    @Override
    public void destroyDrawingCache() {
        super.destroyDrawingCache();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (null != mViewRunnable) {
            synchronized (mViewRunnable) {
                mViewRunnable.notifyAll();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (null != mViewRunnable) {
        }
    }

    public void moveView(final float x, final float y) {
        if (getVisibility() == View.VISIBLE) {
            mViewRunnable.move(x, y);
        }
    }

    public void copyViewAndMove(View v, final float x, final float y) {
        mViewRunnable.copyView(v);
        moveView(x, y);
    }

    /**
     * 添加拷贝的放大百分比
     * @param value
     */
    public void setCopyValue(final float value) {
        this.copyValue = value;
    }

    public void clear() {
        copyViewAndMove(null, -1, -1);
        setVisibility(View.GONE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "action = " + event.getAction());
        //  make sure show right;
        clear();
        return super.onTouchEvent(event);
    }

    class ViewRunnable implements Runnable {

        private SurfaceHolder holder;

        private boolean isRun = true;

        private float curX = -1;

        private float curY = -1;

        private float nextX = -1;

        private float nextY = -1;

        private float offset = 1;

        private Bitmap bitmap;

        private int offsetX = 0;

        private int offsetY = 0;

        public ViewRunnable(SurfaceHolder holder) {
            this.holder = holder;
            this.isRun = true;
        }

        public void move(final float x, final float y) {
            if (canMove(x, curX) || canMove(y, curY)) {
                Log.d(TAG, "move");
                nextX = x;
                nextY = y;
                synchronized (this) {
                    this.notifyAll();
                }
            }
        }

        public void copyView(View v) {
            if (null != bitmap) {
                bitmap.recycle();
                bitmap = null;
            }
            if (null != v) {
                offsetX = (int) (v.getMeasuredWidth() * copyValue / 2);
                offsetY = (int) ((v.getMeasuredHeight() * copyValue / 2) + DISPLAY_OFFSET_Y);
                bitmap = ViewUtil.convertViewToBitmap(v, v.getMeasuredWidth(), v.getMeasuredHeight(), copyValue);
            }
        }

        private boolean canMove(final float v1, final float v2) {
            return Math.abs(v1 - v2) > offset;
        }

        @Override
        public void run() {
            while (isRun) {
                do {
                    Log.d(TAG, "run");
                    curX = nextX;
                    curY = nextY;
                    Canvas c = null;
                    try {
                        synchronized (holder) {
                            c = holder.lockCanvas();//锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
                            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//设置画布背景颜色
                            Paint p = new Paint(); //创建画笔
                            p.setColor(Color.WHITE);
                            if (null != bitmap) {
                                c.drawBitmap(bitmap, curX - offsetX, curY - offsetY, p);
                                c.drawText("x = " + curX + "   y = " + curY, 100, 310, p);
                            } else {
                                c.drawText("Can't find Drawing Cache ", 100, 310, p);
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    } finally {
                        if (c != null) {
                            holder.unlockCanvasAndPost(c);//结束锁定画图，并提交改变。
                        }
                    }
                } while (canMove(nextX, curX) || canMove(nextY, curY));
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
