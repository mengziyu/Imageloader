package com.ziyu.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String[] imageUrls = new String[]{
            "http://pic3.16pic.com/00/16/16/16pic_1616414_b.jpg",
            "http://img3.duitang.com/uploads/item/201410/14/20141014124605_EPZQc.thumb.700_0.jpeg",
            "http://img5q.duitang.com/uploads/item/201212/01/20121201215258_dzFNV.thumb.700_0.jpeg",
            "http://img5q.duitang.com/uploads/blog/201411/10/20141110182717_fLMyv.thumb.700_0.jpeg",
            "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=f0ab8abb91eef01f4d4110c1d5ceb513/1b4c510fd9f9d72a247d23c2d12a2834359bbbd7.jpg",
            "http://pic3.16pic.com/00/16/16/16pic_1616414_b.jpg",
            "http://pic3.16pic.com/00/16/16/16pic_1616414_b.jpg",
            "http://img4q.duitang.com/uploads/item/201301/23/20130123120145_hKdjM.thumb.700_0.jpeg",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new ImageAdapter(imageUrls,this));
    }

}
