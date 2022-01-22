package lozn;

/**
 * Author:Lozn
 * Email:qssq521@gmail.com
 * 2022/1/21
 * 16:12
 */
public interface ScrollViewI {
    void smoothScrollBy(int dx, int dy);
    void smoothScrollTo(int x, int y);

    int getScrollX();

    void scrollTo(int x, int y);
    void scrollBy(int x, int y);

    int getWidth();
    int getHeight();

    int getScrollY();
}
