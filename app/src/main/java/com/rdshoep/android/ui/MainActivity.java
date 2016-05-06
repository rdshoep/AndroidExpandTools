package com.rdshoep.android.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rdshoep.android.R;
import com.rdshoep.android.pojo.SimpleItem;
import com.rdshoep.android.ui.activities.FragmentContainerActivity;
import com.rdshoep.android.ui.adapter.SimpleListAdapter;
import com.rdshoep.android.ui.fragments.FontableTextViewFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SimpleListAdapter.OnSimpleItemClickListener {

    @BindView(android.R.id.list)
    RecyclerView mList;

    List<SimpleItem> simpleItemList = new ArrayList<>();
    SimpleListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //初始化显示的功能
        simpleItemList.add(new SimpleItem("FontableTextView", "支持各种字体的设置，使界面显示更加自由!"
                , FontableTextViewFragment.class.getName()));

        mAdapter = new SimpleListAdapter();
        mAdapter.setItemList(simpleItemList);
        mAdapter.setClickListener(this);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(SimpleItem item) {
        String fragmentClass = (String) item.getExtra();
        if (fragmentClass != null) {
            FragmentContainerActivity.startActivityForResult(this, 1, item.getTitle(), fragmentClass, null);
        }
    }
}
