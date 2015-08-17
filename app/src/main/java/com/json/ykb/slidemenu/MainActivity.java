package com.json.ykb.slidemenu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * com.json.ykb.slidemenu
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class MainActivity extends BaseActivity
{
    private ListView lv_main;
    private ArrayList<ClickItem> items;
    private MAdapter adapter;

    void  initMainItem() {
        if(null==items) {
            items = new ArrayList<>();
            items.add(0, new ClickItem(R.drawable.home_sign, "测试SlideMenu", TestSlideActivity1.class));
            items.add(0, new ClickItem(R.drawable.home_track, "测试SlideLayout", TestSlideActivity.class));
        }
    }

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        lv_main=(ListView)findViewById(R.id.lv_main);
        initMainItem();
        adapter=new MAdapter();
        lv_main.setAdapter(adapter);
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent=new Intent(MainActivity.this,items.get(i).cls);
                startActivity(intent);
            }
        });
    }

    class ViewHolder {
        ImageView img_item;
        TextView tv_item;
    }

    private class MAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main, null);

                holder.img_item = (ImageView) convertView.findViewById(R.id.img_item);
                holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ClickItem item = (ClickItem) getItem(position);

            holder.img_item.setImageDrawable(getResources().getDrawable(item.imageViewRes));
            holder.tv_item.setText(item.title);

            return convertView;
        }
    };

    class ClickItem {
        int imageViewRes;
        String title;
        Class<?> cls;

        public ClickItem(int _imageViewRes, String _title, Class<?> _cls) {
            imageViewRes = _imageViewRes;
            title = _title;
            cls = _cls;
        }
    }
}
