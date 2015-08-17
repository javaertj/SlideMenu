package com.json.ykb.slidemenu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * com.json.ykb.slidemenu
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public abstract class BaseActivity extends Activity
{
    /**
     * 作用于contentListView的适配器。
     */
    protected ArrayAdapter<String> contentListAdapter;

    /**
     * 用于填充contentListAdapter的数据源。
     */
    protected String[] contentItems = { "Content Item 1", "Content Item 2", "Content Item 3", "Content Item 4", "Content Item 5", "Content Item 6", "Content Item 7", "Content Item 8", "Content Item 9",
            "Content Item 10", "Content Item 11", "Content Item 12", "Content Item 13", "Content Item 14", "Content Item 15", "Content Item 16" };
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        init();
    }
    protected abstract void init();

}
