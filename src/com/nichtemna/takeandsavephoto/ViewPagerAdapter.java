package com.nichtemna.takeandsavephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Shishova Galina
 * nichtemna@gmail.com
 */
public class ViewPagerAdapter extends PagerAdapter {
    private final Context mContext;
    private final ArrayList<File> mFiles;
    private LayoutInflater mLayoutInflater;

    public ViewPagerAdapter(Context mContext, ArrayList<File> files) {
        this.mContext = mContext;
        this.mFiles = files;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) mLayoutInflater.inflate(R.layout.item_photo, container, false);
        File currentFile = mFiles.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
        view.setImageBitmap(bitmap);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
