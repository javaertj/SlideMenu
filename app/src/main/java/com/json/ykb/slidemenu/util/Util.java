package com.json.ykb.slidemenu.util;

import android.content.Context;

/**
 * com.json.ykb.slidemenu.util
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/12.
 */
public class Util
{
    /**
     * dp转换为px
     * @param dipValue
     * @param context
     * @return
     */
    public static int diptoPx(Context context,float dipValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * density + 0.5f);
    }
}
