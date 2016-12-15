package cn.hjf.loadmorelistview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 滑动到最底部，加载更多数据的ListView
 * Created by huangjinfu on 2016/12/5.
 */

public class LoadMoreListView extends ListView {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 当前加载状态。
     * true-正在加载，{@link LoadMoreListView#loadingView} 处于显示状态
     * false-没有加载，{@link LoadMoreListView#loadingView} 处于隐藏状态
     */
    private boolean loading;
    /**
     * 底部的加载视图
     */
    private View loadingView;
    /**
     * 加载状态监听器
     */
    private OnLoadListener onLoadListener;

    /**
     * 加载控制器
     */
    private LoadController loadController;

    /**
     * 加载状态监听器
     */
    public interface OnLoadListener {
        /**
         * 加载更多，当前列表已经滑动到底部，应该加载更多数据了
         *
         * @param listView 当前操作的ListView
         */
        void onLoadMore(ListView listView);
    }

    /**
     * 控制ListView是否自动加载的对象
     */
    public interface LoadController {
        /**
         * 是否还有更多数据。
         *
         * @return true-还有更多数据，滑动到底部依然触发加载事件，false-说明没有更多数据可以加载，此时滑动到最底部，不会触发加载事件。
         */
        boolean haveMoreData();
    }

    public LoadMoreListView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        loadingView = getDefaultLoadingView();

        /**
         * 修复4.4以下版本会出现Adapter类型转换的错误。{@link ListView#addFooterView(View)}
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            addHeaderView(new View((context)));
        }

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!loading
                        && canScrollUp()
                        && reachBottom(firstVisibleItem, visibleItemCount, totalItemCount)) {
                    if (loadController != null && !loadController.haveMoreData()) {
                        return;
                    }
                    loading();
                }
            }
        });
    }

    /**
     * 生成默认的加载视图。{@link LoadMoreListView#loadingView}
     *
     * @return
     */
    private View getDefaultLoadingView() {
        TextView textView = new TextView(context);
        textView.setText("加载中...");
        textView.setPadding(90, 90, 90, 90);
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    /**
     * 是否达到ListView的底部
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @return
     */
    private boolean reachBottom(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        return firstVisibleItem + visibleItemCount == totalItemCount;
    }

    /**
     * 是否可以继续向上滑动
     *
     * @return
     */
    private boolean canScrollUp() {
        return canScrollVertically(1);
    }

    /**
     * 设置状态为加载状态，并且通知相应的事件到监听器 {@link LoadMoreListView#onLoadListener}。
     */
    private void loading() {
        loading = true;
        addFooterView(loadingView);

        if (onLoadListener != null) {
            onLoadListener.onLoadMore(LoadMoreListView.this);
        }
    }

    /**
     * 设置加载状态监听器
     *
     * @param onLoadListener
     */
    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    /**
     * 设置加载控制器
     *
     * @param loadController
     */
    public void setLoadController(LoadController loadController) {
        this.loadController = loadController;
    }

    /**
     * 设置加载视图
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }

    /**
     * 加载完成，在加载动作完成后，应该调用该方法，还原控件的加载状态。
     */
    public void loadComplete() {
        loading = false;
        removeFooterView(loadingView);
    }

}
