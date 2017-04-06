package com.walfud.cachedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.walfud.cache.BitmapCache;
import com.walfud.cache.Cache;

public class MainActivity extends Activity {

    public static final String TAG = "CacheDemo";

    private RecyclerView mRv;
    private Cache<Bitmap> mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRv.setAdapter(new RecyclerView.Adapter<ViewHolder>() {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_rv, parent, false));
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                Bitmap bitmap = mCache.get(String.valueOf(position));
                if (bitmap != null) {
                    Log.e(TAG, "onBindViewHolder: " + "hit: " + position);
                } else {
                    Log.e(TAG, "onBindViewHolder: " + "miss: " + position);
                    try {
                        bitmap = BitmapFactory.decodeStream(getResources().getAssets().open(String.format("%d.jpg", position)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mCache.set(String.valueOf(position), bitmap);
                }
                holder.iv.setImageBitmap(bitmap);
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });

        //
        mCache = new BitmapCache(this);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView keyTv;

        public ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            keyTv = (TextView) itemView.findViewById(R.id.tv_key);
        }
    }
}
