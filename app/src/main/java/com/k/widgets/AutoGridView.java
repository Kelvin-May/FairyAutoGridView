package com.k.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import com.k.ui.AutoAdapter;
import com.k.ui.R;

/**
 *
 * 核心类,touch事件在这里监听,内部包含了两个widget(ExpandGridView与FairyView)
 *
 * FairyView 为选中抬起移动的View
 *
 * Created by Kelvin on 15/9/7.
 */
public class AutoGridView extends RelativeLayout {

    private static final String TAG = "AutoGridView";

    private ExpandGridView mGridView;

    private FairyView mFairyView;

    private LongClickRunnable mLongClickRunnable = new LongClickRunnable();

    private boolean isMove = false;

    private boolean isKeyDown = false;

    public AutoGridView(Context context) {
        super(context);
        init(context, null, -1);
    }

    public AutoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, -1);
    }

    public AutoGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mGridView = new ExpandGridView(context, attrs, defStyleAttr);
        // 这里要强制设置一个ID好恶心啊
        mGridView.setId(R.id.normal);
        this.addView(mGridView);

        mFairyView = new FairyView(context);
        mFairyView.setId(R.id.none);
        this.addView(mFairyView);
        mFairyView.setVisibility(View.GONE);
    }

    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    public ExpandGridView getGridView() {
        return mGridView;
    }

    private void showFairyAt(View v, final float x, final float y) {
        mFairyView.setVisibility(View.VISIBLE);
        mFairyView.copyViewAndMove(v, x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.d(TAG, "action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                isKeyDown = true;
                final int index = findViewPos(event);
                mLongClickRunnable.setEvent(index, event);
                if (index > -1) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                if (mFairyView.getVisibility() == View.VISIBLE) {
                    moveView(event);
                    mFairyView.moveView(event.getRawX(), event.getRawY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isMove = false;
                isKeyDown = false;
                if (mFairyView.getVisibility() == View.VISIBLE) {
                    mLongClickRunnable.cancelEvent();
                    mFairyView.clear();
                    AutoAdapter autoAdapter = mGridView.getAutoAdapter();
                    if (null != autoAdapter) {
                        autoAdapter.setHidePos(-1);
                    }
                } else {
                    // 模拟点击事件
//                    return super.onTouchEvent(mLongClickRunnable.event);
                }
                break;
            default:
                isMove = false;
                isKeyDown = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 开始自动调整位置的假动画
     * @param selectPos
     * @param movePos
     */
    private void doMoveAnim(final int selectPos, final int movePos) {
        if(selectPos != movePos) {
            int startAnim;
            int endAnim;
            // 选中的index > 移动到的index, 从start开始往前移, end就是移动的index
            boolean moveBack = selectPos >= movePos;
            if (moveBack) {
                startAnim = movePos + 1;
                endAnim = selectPos + 1;
            } else {
                startAnim = selectPos;
                endAnim = movePos;
            }
            Log.d(TAG, "moveBack = " + moveBack + " start = " + startAnim + "  end = " + endAnim);
            if (startAnim > -1 && endAnim > -1) {
                final int count = mGridView.getCount();
                if (endAnim <= count) {
                    for (int i = startAnim; i < endAnim; i++) {
                        final View view = mGridView.getChildAt(i);
                        if (null != view) {
                            int index;
                            if (moveBack) {
                                // 要做一个往前动一个的动画的话就要找到上一个
                                index = i - 1;
                            } else {
                                index = i + 1;
                            }
                            View copyView = getView(index);
//                                Log.d(TAG, "i = " + i + " name = " + getName(i) + " find copy view " + index + "  success ? " + (copyView != null));
                            if (null != copyView) {
                                final float x = copyView.getX() - view.getX();
                                final float y = copyView.getY() - view.getY();
//                                    Log.d(TAG, "i = " + i + " || " + getName(i) + " move from " + " x = " + view.getX() + " y = " + view.getY());
//                                    Log.d(TAG, "index = " + index + " || " + getName(index) + " move to " + " x = " + copyView.getX() + " y = " + copyView.getY());
//                                    Log.d(TAG, " x = " + x + " y = " + y);
                                TranslateAnimation t = new TranslateAnimation(x, 0f, y, 0f);
                                t.setDuration(400);
                                t.setInterpolator(interpolator);
                                view.clearAnimation();
                                view.startAnimation(t);
                            }
                        }
                    }
                }
            }
        }
    }

    private final Interpolator interpolator = new DecelerateInterpolator(2.0f);

    private void moveView(MotionEvent event) {
        final int index = findViewPos(event);
//        Log.d(TAG, "move to index = " + index);
        if (index > -1) {
            AutoAdapter autoAdapter = mGridView.getAutoAdapter();
            if (null != autoAdapter) {
                final int selectPos = autoAdapter.getHidePos();
                final boolean needAnim = autoAdapter.movePos(index);
                if (needAnim) {
                    // 需要做移动动画了
                    doMoveAnim(selectPos, index);
                }
            }
        }
    }

    private String getName(int index) {
        if (index > -1) {
            AutoAdapter autoAdapter = mGridView.getAutoAdapter();
            if (null != autoAdapter && index < autoAdapter.getCount()) {
                return autoAdapter.getItem(index);
            }
        }
        return null;
    }

    private View selectView(int index) {
        final int count = mGridView.getCount();
        if (index > -1 && index < count) {
            AutoAdapter autoAdapter = mGridView.getAutoAdapter();
            if (null != autoAdapter) {
                autoAdapter.setHidePos(index);
            }
            return mGridView.getChildAt(index);
        }
        return null;
    }

    private View getView(int index) {
        final int count = mGridView.getCount();
        if (index > -1 && index < count) {
            return mGridView.getChildAt(index);
        }
        return null;
    }

    private View getView(MotionEvent event) {
        final int index = findViewPos(event);
        return getView(index);
    }

    private int findViewPos(MotionEvent event) {
        int index = -1;
        final int count = mGridView.getCount();
        final float eventX = event.getX();
        final float eventY = event.getY();
        for (int i = 0; i < count; i++) {
            final View view = mGridView.getChildAt(i);
            if (null != view && view.getVisibility() == View.VISIBLE) {
                // 只要判断一个view的两个对点就行
                float viewX = view.getX();
                float viewY = view.getY();
                // 先判断是否在view的左上顶点的右下位置
                if (eventX > viewX && eventY > viewY) {
                    // 再判断是否在view的右下顶点的右上位置
                    viewX += view.getMeasuredWidth();
                    viewY += view.getMeasuredHeight();
                    if (eventX < viewX && eventY < viewY) {
                        if (view.getVisibility() == View.VISIBLE) {
                            index = i;
                        }
                        break;
                    }
                }
            }
        }
        return index;
    }

    class LongClickRunnable implements Runnable {

        /** 长按响应时间 */
        private final long longClickDelay = 0;

        private MotionEvent event;

        private int selectPos = -1;

        public void setEvent(int index, MotionEvent e) {
            this.selectPos = index;
            this.event = e;
            if (selectPos > -1) {
                postDelayed(this, longClickDelay);
            }
        }

        public void cancelEvent() {
            this.selectPos = -1;
            removeCallbacks(this);
        }

        @Override
        public void run() {
            if (null != event && selectPos > -1) {
                if (mFairyView.getVisibility() != View.VISIBLE && isKeyDown && !isMove) {
                    View view = selectView(selectPos);
                    if (null != view) {
                        showFairyAt(view, event.getRawX(), event.getRawY());
                    }
                }
            }
        }
    }

}
