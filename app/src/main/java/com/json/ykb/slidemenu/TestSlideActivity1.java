package com.json.ykb.slidemenu;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * com.loyo.oa.v2.activity
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class TestSlideActivity1 extends BaseActivity implements View.OnClickListener
{
    private ViewGroup img_title_left;
    private SlideMenu slideMenu;
    private ListView contentList;

    public void init(){
        setContentView(R.layout.activity_slide_main_layout);
        slideMenu=(SlideMenu)findViewById(R.id.main_container);
        slideMenu.setMode(SlideMenu.MODE_SCOLL_ALL_WITH_SCALE);
        slideMenu.setMenu(R.layout.layout_slide_menu);
        slideMenu.setContent(R.layout.layout_slide_content);

        contentList=(ListView)findViewById(R.id.contentList);
        contentListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentItems);
        contentList.setAdapter(contentListAdapter);

        img_title_left=(ViewGroup)findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        slideMenu.toggle();
    }
}
