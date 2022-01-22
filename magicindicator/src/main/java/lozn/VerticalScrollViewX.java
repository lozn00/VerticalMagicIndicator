package lozn;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import java.lang.reflect.Method;

/**
 * Author:Lozn
 * Email:qssq521@gmail.com
 * 2022/1/21
 * 16:18
 */
public class VerticalScrollViewX extends NestedScrollView implements ScrollViewI{
    public VerticalScrollViewX(@NonNull Context context) {
        super(context);
    }

    public VerticalScrollViewX(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollViewX(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}

