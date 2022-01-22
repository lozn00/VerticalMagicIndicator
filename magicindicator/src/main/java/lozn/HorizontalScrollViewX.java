package lozn;

import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import java.lang.reflect.Method;

/**
 * Author:Lozn
 * Email:qssq521@gmail.com
 * 2022/1/21
 * 16:12
 */
public class HorizontalScrollViewX extends HorizontalScrollView implements ScrollViewI {// implements ScrollViewI {

    public HorizontalScrollViewX(Context context) {
        super(context);
    }

    public HorizontalScrollViewX(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollViewX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalScrollViewX(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}