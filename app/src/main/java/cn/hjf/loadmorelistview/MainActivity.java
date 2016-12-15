package cn.hjf.loadmorelistview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private List<String> data = new ArrayList<>();
    private LoadMoreListView loadMoreListView;
    private DataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadMoreListView = (LoadMoreListView) findViewById(R.id.listview);
//        loadMoreListView.addFooterView(getNormalView());
//        loadMoreListView.addHeaderView(getNormalView());
        loadMoreListView.setLoadingView(getLoadingView());
        loadMoreListView.setOnLoadListener(new LoadMoreListView.OnLoadListener() {
            @Override
            public void onLoadMore(ListView listView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData();
                        Toast.makeText(MainActivity.this, "加载完成", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        loadMoreListView.loadComplete();
                    }
                }, 3000);
            }
        });
        loadMoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "click : " + data.get(position - loadMoreListView.getHeaderViewsCount()), Toast.LENGTH_SHORT).show();
            }
        });
        loadMoreListView.setLoadController(new LoadMoreListView.LoadController() {
            @Override
            public boolean haveMoreData() {
                return data.size() < 120;
//                return false;
            }
        });


        addData();

        adapter = new DataAdapter(this, data);
        loadMoreListView.setAdapter(adapter);


    }

    private void addData() {
        int start = data.size();
        int end = data.size() + 40;
        for (int i = start; i < end; i++) {
            data.add("data : " + (i + 1));
        }
    }

    private View getNormalView() {
        TextView textView = new TextView(this);
        textView.setText("Footer And Header");
        textView.setPadding(45, 45, 45, 45);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        textView.setBackgroundResource(R.color.colorPrimary);
        return textView;
    }

    private View getLoadingView() {
        TextView textView = new TextView(this);
        textView.setText("加载中...");
        textView.setPadding(90, 90, 90, 90);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.color.colorAccent);
        textView.setTextColor(getResources().getColor(R.color.colorWhite));
        return textView;
    }
}
