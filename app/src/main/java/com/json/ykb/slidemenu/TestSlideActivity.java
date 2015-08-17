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
public class TestSlideActivity extends BaseActivity implements View.OnClickListener
{
    private ViewGroup img_title_left;
    private SlideLayout slideLayout;
    private ListView contentList;

    @Override
    public void init(){
        slideLayout=new SlideLayout(this);
        slideLayout.setBackgroundColor(getResources().getColor(R.color.black));
        slideLayout.setMenu(R.layout.layout_slide_menu);
        slideLayout.setContent(R.layout.layout_slide_content);

        setContentView(slideLayout);

        contentList=(ListView)findViewById(R.id.contentList);
        contentListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentItems);
        contentList.setAdapter(contentListAdapter);

        img_title_left=(ViewGroup)findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        slideLayout.toggle();
    }
}
