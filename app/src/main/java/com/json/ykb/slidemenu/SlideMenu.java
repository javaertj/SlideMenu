package com.json.ykb.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.json.ykb.slidemenu.slidebase.SlideBase;
import com.json.ykb.slidemenu.util.Util;
import com.nineoldandroids.view.ViewHelper;

/**
 * com.json.ykb.slidemenu.customview
 * 描述 :多种效果的滑动视图
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class SlideMenu extends HorizontalScrollView implements SlideBase
{
    private String TAG = getClass().getSimpleName();
    /**
     * 滑动偏移量
     */
    private static final int BASE_SLIDE_BLOCK = 6;

    /**
     * 屏幕像素密度
     */
    private float density;

    /**
     * 菜单效果
     */
    private int mode;

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;

    /**
     * 左侧布局对象。
     */
    private View leftLayout;

    /**
     * 右侧布局对象。
     */
    private View rightLayout;
    /**
     * 菜单宽度
     */
    private int menuWidth;
    /**
     * 滑动开始时的x坐标
     */
    private float downX;
    /**
     * 菜单状态
     */
    private boolean isMenuOpen;

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     */
    public SlideMenu(Context context)
    {
        this(context, null);
    }

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     * @param attrs
     */
    public SlideMenu(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     * @param attrs
     * @param defaultStyle
     */
    public SlideMenu(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs, defaultStyle);
        density = getResources().getDisplayMetrics().density;
        setLayoutParams(new RelativeLayout.LayoutParams(-2, -1));
        initContainer();
    }

    @Override
    public void initContainer()
    {
        LinearLayout container = (LinearLayout) createContainer(MAIN_CONTAINER_ID);
        addView(container);
        View menu = createContainer(MENU_CONTAINER_ID);
        View content = createContainer(CONTENT_CONTAINER_ID);
        container.addView(menu);
        container.addView(content);
    }

    @Override
    public View createContainer(int id)
    {
        LinearLayout container = new LinearLayout(getContext());
        container.setId(id);
        container.setOrientation(LinearLayout.VERTICAL);
        if (id == MAIN_CONTAINER_ID) {
            container.setOrientation(LinearLayout.HORIZONTAL);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);

        if (MENU_CONTAINER_ID == id) {
            params = new RelativeLayout.LayoutParams(Util.diptoPx(getContext(),250), -1);
        }

        container.setLayoutParams(params);

        return container;
    }

    @Override
    public void setMenu(int layoutId)
    {
        LinearLayout left = (LinearLayout) findViewById(MENU_CONTAINER_ID);
        if (left.getChildCount() > 0) {
            throw new IllegalArgumentException("菜单视图已存在");
        }
        View menu = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
        setMenu(menu);
    }

    @Override
    public void setMenu(View menuView)
    {
        addChildView(menuView, MENU_CONTAINER_ID);
    }

    @Override
    public void setContent(int layoutId)
    {
        LinearLayout right = (LinearLayout) findViewById(CONTENT_CONTAINER_ID);
        if (right.getChildCount() > 0) {
            throw new IllegalArgumentException("主视图已存在");
        }
        View content = LayoutInflater.from(getContext()).inflate(layoutId, null, false);
        setContent(content);
    }

    @Override
    public void setContent(View contentView)
    {
        addChildView(contentView, CONTENT_CONTAINER_ID);
    }

    @Override
    public void addChildView(View view, int id)
    {
        LinearLayout container = (LinearLayout) findViewById(id);
        if (null == container) {
            throw new IllegalArgumentException("视图容器为空");
        }

        if (null == view.getLayoutParams()) {
            view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        }

        container.addView(view);
    }


    @Override
    public void onLayoutInit()
    {
        // 获取左侧布局对象
        leftLayout = findViewById(MENU_CONTAINER_ID);
        menuWidth = leftLayout.getLayoutParams().width;
        // 获取右侧布局对象
        rightLayout = findViewById(CONTENT_CONTAINER_ID);
        MarginLayoutParams rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
        rightLayoutParams.width = getResources().getDisplayMetrics().widthPixels;
        rightLayout.setLayoutParams(rightLayoutParams);
        scrollTo(menuWidth, 0);
        loadOnce = true;
    }

    /**
     * 设置菜单动画模式
     *
     * @param mode
     */
    public void setMode(int mode)
    {
        this.mode = mode;
    }

    /**
     * 在onLayout中重新设定左侧布局和右侧布局的参数。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            onLayoutInit();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale=l * 1.0f / menuWidth;
        switch (mode) {
            case MODE_CONTENT_SCROLL_ONLY:
                ViewHelper.setTranslationX(leftLayout, menuWidth * scale);
                break;
            case MODE_SCOLL_ALL_WITH_SCALE:
                float leftScale = 1 - 0.3f * scale;
                float rightScale = 0.8f + scale * 0.2f;

                ViewHelper.setScaleX(leftLayout, leftScale);
                ViewHelper.setScaleY(leftLayout, leftScale);
                ViewHelper.setAlpha(leftLayout, 0.6f + 0.4f * (1 - scale));
                ViewHelper.setTranslationX(leftLayout, menuWidth * scale * 0.6f);

                ViewHelper.setPivotX(rightLayout, 0);
                ViewHelper.setPivotY(rightLayout, rightLayout.getHeight() / 2);
                ViewHelper.setScaleX(rightLayout, rightScale);
                ViewHelper.setScaleY(rightLayout, rightScale);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP) {

            int scrollX = (int) (ev.getRawX() - downX);
            int slideWidth = menuWidth / BASE_SLIDE_BLOCK;
            Log.e(TAG, "scrollx : " + scrollX + " downX : " + downX + " upX " + ev.getRawX() + " width/6 : " + slideWidth + " menuwidth : " + menuWidth);
            if (scrollX == 0 || scrollX == menuWidth) {
                return false;
            }
            int slideX = Math.abs(scrollX);
            //右滑
            if (scrollX > 0) {
                if (slideX >= slideWidth) {
                    smoothScrollTo(0, 0);
                    isMenuOpen = true;
                } else {
                    smoothScrollTo(menuWidth, 0);
                    isMenuOpen = false;
                }
            }
            //左滑
            else if (scrollX < 0) {
                if (slideX >= slideWidth) {
                    smoothScrollTo(menuWidth, 0);
                    isMenuOpen = false;
                } else {
                    smoothScrollTo(0, 0);
                    isMenuOpen = true;
                }
            }
            //未滑动
            else {
                if (!isMenuOpen) {
                    smoothScrollTo(menuWidth, 0);
                } else{
                    smoothScrollTo(0, 0);
                }
            }
            downX=0;
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if(downX==0)
                downX = ev.getRawX();
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu()
    {
        if (isMenuOpen) {
            return;
        }

        smoothScrollTo(0, 0);
        isMenuOpen = true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu()
    {
        if (!isMenuOpen) {
            return;
        }

        smoothScrollTo(menuWidth, 0);
        isMenuOpen = false;
    }

    /**
     * 切换菜单状态
     */
    public void toggle()
    {
        if (isMenuOpen) {
            closeMenu();
        } else {
            openMenu();
        }
    }
}
