package com.json.ykb.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.json.ykb.slidemenu.slidebase.SlideBase;
import com.json.ykb.slidemenu.util.Util;

/**
 * com.json.ykb.slidemenu.customview
 * 描述 :滑动菜单
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class SlideLayout extends  RelativeLayout implements SlideBase
{
    private String TAG = getClass().getSimpleName();

    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     */
    public SlideLayout(Context context)
    {
        this(context, null);
    }

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     * @param attrs
     */
    public SlideLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    /**
     * 重写SlidingLayout的构造函数
     *
     * @param context
     * @param attrs
     * @param defaultStyle
     */
    public SlideLayout(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs,defaultStyle);
        setLayoutParams(new LayoutParams(-1,-1));
        initContainer();
    }

    @Override
    public void initContainer(){
        ImageView imageView=new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(-1, -2));
        imageView.setImageResource(R.drawable.slide_menu_bg);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);

        View menu=createContainer(MENU_CONTAINER_ID);
        View content=createContainer(CONTENT_CONTAINER_ID);
        addView(menu);
        addView(content);
    }

    @Override
    public View createContainer(int id){
        LinearLayout container=id==CONTENT_CONTAINER_ID?new SlideContent(getContext()):new LinearLayout(getContext());
        container.setId(id);
        container.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params=new LayoutParams(-1,-1);

        if (MENU_CONTAINER_ID==id)
            params=new LayoutParams(Util.diptoPx(getContext(),250),-1);
            //important ：if didn't set this rule,rightMargin will be useless
        else
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        container.setLayoutParams(params);

        return container;
    }

    @Override
    public void setMenu(int layoutId){
        LinearLayout left=(LinearLayout)findViewById(MENU_CONTAINER_ID);
        if(left.getChildCount()>0)
            throw new IllegalArgumentException("菜单视图已存在");
        View menu= LayoutInflater.from(getContext()).inflate(layoutId,null,false);
        setMenu(menu);
    }

    @Override
    public void setMenu(View menuView){
        addChildView(menuView,MENU_CONTAINER_ID);
    }

    @Override
    public void setContent(int layoutId){
        LinearLayout right=(LinearLayout)findViewById(CONTENT_CONTAINER_ID);
        if(right.getChildCount()>0)
            throw new IllegalArgumentException("主视图已存在");
        View content=LayoutInflater.from(getContext()).inflate(layoutId,null,false);
        setContent(content);
    }

    @Override
    public void setContent(View contentView){
        addChildView(contentView, CONTENT_CONTAINER_ID);
    }

    @Override
    public void addChildView(View view,int id)
    {
        LinearLayout container=(LinearLayout)findViewById(id);
        if(null==container)
            throw new IllegalArgumentException("视图容器为空");

        if(null==view.getLayoutParams())
            view.setLayoutParams(new ViewGroup.LayoutParams(-1,-1));

        container.addView(view);
    }

    @Override
    public void onLayoutInit()
    {
        // 获取右侧布局对象
        SlideContent rightLayout = (SlideContent)findViewById(CONTENT_CONTAINER_ID);
        MarginLayoutParams rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
        rightLayoutParams.width = getMeasuredWidth();
        rightLayout.setLayoutParams(rightLayoutParams);
        // 获取左侧布局对象
        LinearLayout leftLayout = (LinearLayout)findViewById(MENU_CONTAINER_ID);
        MarginLayoutParams leftLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
        int rightEdge = -leftLayoutParams.width;

        rightLayout.init(rightEdge,leftLayoutParams);
        loadOnce = true;
        Log.e(TAG, "initData,rightEdge : " + rightEdge + " leftWidth : " + leftLayoutParams.width + " rightWidth : " + rightLayout.getWidth());
    }

    /**
     * 设置滑动时和菜单展开时不能操作的视图
     * @param bindView
     */
    public void setScrollEvent(View bindView){
        ((SlideContent)findViewById(CONTENT_CONTAINER_ID)).setScrollEvent(bindView);
    }

    /**
     * 在onLayout中重新设定左侧布局和右侧布局的参数。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce)
        {
            onLayoutInit();
        }
    }

    /**
     * 切换菜单状态
     */
    public void toggle(){
        ((SlideContent)findViewById(CONTENT_CONTAINER_ID)).toggle();
    }
}
