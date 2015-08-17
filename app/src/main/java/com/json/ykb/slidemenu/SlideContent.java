package com.json.ykb.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.json.ykb.slidemenu.slidebase.SlideBase;

/**
 * com.json.ykb.slidemenu.customview
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/11.
 */
public class SlideContent extends LinearLayout
{
    private String TAG = getClass().getSimpleName();

    /**
     * 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
     */
    public static final int SNAP_VELOCITY = 200;

    /**
     * 屏幕像素密度
     */
    private float density;

    /**
     * 滑动速度
     */
    private int slideSpeed=10;
    /**
     * 记录当前的滑动状态
     */
    private int slideState;

    /**
     * 右侧布局最多可以滑动到的左边缘。
     */
    private int leftEdge = 0;

    /**
     * 右侧布局最多可以滑动到的右边缘。
     */
    private int rightEdge = 0;

    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int touchSlop;

    /**
     * 记录手指按下时的横坐标。
     */
    private float xDown;

    /**
     * 记录手指按下时的纵坐标。
     */
    private float yDown;

    /**
     * 记录手指移动时的横坐标。
     */
    private float xMove;

    /**
     * 记录手指移动时的纵坐标。
     */
    private float yMove;

    /**
     * 记录手机抬起时的横坐标。
     */
    private float xUp;

    /**
     * 左侧布局当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
     */
    private boolean isLeftLayoutVisible;

    /**
     * 是否正在滑动。
     */
    private boolean isSliding;

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;
    /**
     * 左侧布局的参数，通过此参数来重新确定左侧布局的宽度，以及更改leftMargin的值。
     */
    private MarginLayoutParams leftLayoutParams;

    /**
     * 右侧布局的参数，通过此参数来重新确定右侧布局的宽度。
     */
    private MarginLayoutParams rightLayoutParams;

    /**
     * 用于计算手指滑动的速度。
     */
    private VelocityTracker mVelocityTracker;
    /**
     * 滑动控制器
     */
    private SlideRunner slideRunner=new SlideRunner();
    /**
     * 滑动时和菜单展开时不能操作的视图
     */
    private View mBindView;

    public SlideContent(Context context){
        this(context, null);
    }
    public SlideContent(Context context,AttributeSet attributeSet){
        this(context, attributeSet, 0);
    }
    public SlideContent(Context context,AttributeSet attributeSet,int defaultStyle){
        super(context,attributeSet,defaultStyle);
        density=getResources().getDisplayMetrics().density;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 初始化
     * @param rightEdge_
     */
    public void init(int rightEdge_,MarginLayoutParams leftLayoutParams_){
        if(!loadOnce) {
            loadOnce=!loadOnce;
            leftLayoutParams=leftLayoutParams_;
            rightLayoutParams = (MarginLayoutParams)getLayoutParams();
            rightEdge = rightEdge_;
        }
    }

    /**
     * 设置滑动时和菜单展开时不能操作的视图
     * @param bindView
     */
    public void setScrollEvent(View bindView){
        if(null!=bindView)
            mBindView=bindView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        createVelocityTracker(event);
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时，记录按下时的横坐标
                xDown = event.getRawX();
                yDown = event.getRawY();
                slideState = SlideBase.DO_NOTHING;
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动时，对比按下时的横坐标，计算出移动的距离，来调整右侧布局的leftMargin值，从而显示和隐藏左侧布局
                xMove = event.getRawX();
                yMove = event.getRawY();
                int moveDistanceX = (int) (xMove - xDown);
                int moveDistanceY = (int) (yMove - yDown);
                checkSlideState(moveDistanceX, moveDistanceY);
                switch(slideState)
                {
                    case SlideBase.SHOW_MENU:
                        rightLayoutParams.rightMargin = -moveDistanceX;
                        onSlide();
                        break;
                    case SlideBase.HIDE_MENU:
                        rightLayoutParams.rightMargin = rightEdge - moveDistanceX;
                        onSlide();
                        break;
                    default:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                xUp = event.getRawX();
                int upDistanceX = (int) (xUp - xDown);
                Log.e(TAG, " upDistanceX : " + upDistanceX + " isSliding : " + isSliding + " slideState : " + slideState);
                if (isSliding)
                {
                    // 手指抬起时，进行判断当前手势的意图
                    switch(slideState)
                    {
                        case SlideBase.SHOW_MENU:
                            if (shouldScrollToLeftLayout())
                            {
                                scrollToLeftLayout();
                            } else
                            {
                                scrollToRightLayout();
                            }
                            break;
                        case SlideBase.HIDE_MENU:
                            if (shouldScrollToRightLayout())
                            {
                                scrollToRightLayout();
                            }  else
                            {
                                scrollToLeftLayout();
                            }
                            break;
                        default:
                            break;
                    }
                }
                recycleVelocityTracker();
                break;
        }
        if (isEnabled())
        {
            if (isSliding)
            {
                unFocusBindView();
                return true;
            }
//            if (isLeftLayoutVisible)
//            {
//                return true;
//            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return onTouchEvent(ev);
    }

    /**
     * 执行滑动过程中的逻辑操作，如边界检查，改变偏移值，可见性检查等。
     */
    private void onSlide()
    {
        checkSlideBorder();
        setLayoutParams(rightLayoutParams);
    }

    /**
     * 根据手指移动的距离，判断当前用户的滑动意图，然后给slideState赋值成相应的滑动状态值。
     *
     * @param moveDistanceX
     *            横向移动的距离
     * @param moveDistanceY
     *            纵向移动的距离
     */
    private void checkSlideState(int moveDistanceX, int moveDistanceY)
    {
        if (isLeftLayoutVisible)
        {
            if (!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX < 0)
            {
                isSliding = true;
                slideState = SlideBase.HIDE_MENU;
            }
        } else if (!isSliding && Math.abs(moveDistanceX) >= touchSlop && moveDistanceX > 0 && Math.abs(moveDistanceY) <= touchSlop)
        {
            isSliding = true;
            slideState = SlideBase.SHOW_MENU;
        }
    }

    /**
     * 在滑动过程中检查左侧菜单的边界值，防止绑定布局滑出屏幕。
     */
    private void checkSlideBorder()
    {
        if (rightLayoutParams.rightMargin > leftEdge)
        {
            rightLayoutParams.rightMargin = leftEdge;
        } else if (rightLayoutParams.rightMargin < rightEdge)
        {
            rightLayoutParams.rightMargin = rightEdge;
        }
    }

    /**
     * 判断是否应该滚动将左侧布局展示出来。如果手指移动距离大于屏幕的1/6，或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将左侧布局展示出来。
     *
     * @return 如果应该滚动将左侧布局展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToLeftLayout()
    {
        return xUp - xDown > leftLayoutParams.width / 6 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 判断是否应该滚动将右侧布局展示出来。如果手指移动距离加上leftLayoutPadding大于屏幕的1/6， 或者手指移动速度大于SNAP_VELOCITY， 就认为应该滚动将右侧布局展示出来。
     *
     * @return 如果应该滚动将右侧布局展示出来返回true，否则返回false。
     */
    private boolean shouldScrollToRightLayout()
    {
        return xDown - xUp > leftLayoutParams.width / 6 || getScrollVelocity() > SNAP_VELOCITY;
    }

    /**
     * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
     *
     * @param event
     *            右侧布局监听控件的滑动事件
     */
    private void createVelocityTracker(MotionEvent event)
    {
        if (mVelocityTracker == null)
        {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 获取手指在右侧布局的监听View上的滑动速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity()
    {
        if(null==mVelocityTracker)
            return 0;
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker()
    {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 将屏幕滚动到左侧布局界面
     */
    public void scrollToLeftLayout()
    {
        slideRunner.stop();
        slideRunner.start(-getSpeed());
    }

    /**
     * 将屏幕滚动到右侧布局界面
     */
    public void scrollToRightLayout()
    {
        slideRunner.stop();
        slideRunner.start(getSpeed());
    }

    private float getSpeed(){
        float speed=getScrollVelocity()==0?slideSpeed:getScrollVelocity()/1000f;
        if(speed<slideSpeed)
            speed=slideSpeed;

        return speed;
    }


    /**
     * 左侧布局是否完全显示出来，或完全隐藏，滑动过程中此值无效。
     *
     * @return 左侧布局完全显示返回true，完全隐藏返回false。
     */
    public boolean isLeftLayoutVisible()
    {
        return isLeftLayoutVisible;
    }

    /**
     * 使用可以获得焦点的控件在滑动的时候失去焦点。
     */
    private void unFocusBindView()
    {
        if(null!=mBindView) {
            mBindView.setPressed(false);
            mBindView.setFocusable(false);
            mBindView.setFocusableInTouchMode(false);
        }
    }

    private class SlideRunner implements Runnable{
        private float mSpeed;
        private boolean run;
        private int rightMargin;

        private SlideRunner(){
        }

        public void stop() {
            this.run = false;
        }

        public void start(float mSpeed) {
            this.mSpeed = mSpeed;
            rightMargin = rightLayoutParams.rightMargin;
            Log.e(TAG,"rightMargin : "+rightMargin+" speed : "+mSpeed);
            run=true;
            post(this);
        }
        @Override
        public void run()
        {
            if(!run)
                return;
            float speeds=mSpeed;
            boolean toRight=speeds<0?true:false;
            // 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。

            if(toRight)
                speeds-=10;
            else
                speeds+=10;
            //保持不同分辨率手机滑动同步
            rightMargin += speeds*density;

            if (rightMargin < rightEdge||rightMargin > leftEdge)
            {
                if(rightMargin < rightEdge)
                    rightMargin =rightEdge;
                else
                    rightMargin =leftEdge;

                changeMenuAndContentState(mSpeed);
                onSlide(rightMargin,mSpeed, false);
                return;
            }
            onSlide(rightMargin,mSpeed, true);
            postDelayed(this,1);
        }
    }

    /**
     * 改变菜单、内容视图的当前状态
     * @param speed
     */
    private void changeMenuAndContentState(float speed){
        if (speed > 0)
        {
            isLeftLayoutVisible = false;
        } else
        {
            isLeftLayoutVisible = true;
        }
        isSliding = false;
    }

    /**
     * 滑动
     * @param rightMargin
     * @param speed
     * @param sliding
     */
    private void onSlide(int rightMargin,float speed,boolean sliding){
        rightLayoutParams.rightMargin = rightMargin;
        setLayoutParams(rightLayoutParams);

        Log.e(TAG, "onProgressUpdate,rightMargin : " + rightMargin + " x : " + getLeft() + " y : " + getTop());
        if(sliding)
            unFocusBindView();
    }

    /**
     * 切换菜单状态
     */
    public void toggle(){
        if (isLeftLayoutVisible())
        {
            scrollToRightLayout();
        } else
        {
            scrollToLeftLayout();
        }
    }
}
