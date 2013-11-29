/*
 *  Copyright 2013 nichtemna
 *  nichtemna@gmaiil.com
 *  Skype: nichtemna
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nichtemna.takeandsavephoto;

import java.io.File;
import java.util.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nichtemna.takeandsavephoto.PhotoActivity.TransactionType;

public class PhotoFragment extends Fragment implements OnClickListener {
    public enum ImageSetMode {
        LAST, LEFT, RIGHT, POSITION
    }

    private final static int PERIOD = 500;
    private final static int TICK = 250;

    private ImageButton imb_remove, imb_camera;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private ArrayList<File> files = new ArrayList<File>();

    public static PhotoFragment newInstance(TransactionType type) {
        PhotoFragment frag = new PhotoFragment();
        final Bundle args = new Bundle();
        args.putSerializable(Extras.TRANSACTION_TYPE, type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        initViews(view);
        setListeners();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        if (getTransactionType().equals(TransactionType.TEMPRORAILY)) {
            cameraCountDownTimer.start();
        }

        getFileList();


        super.onActivityCreated(savedInstanceState);
    }

    public class CustomComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            return (int) (file2.lastModified() - file1.lastModified());
        }
    }

    private void initViews(View view) {
        imb_remove = (ImageButton) view.findViewById(R.id.frag_photo_imb_remove_photo);
        imb_camera = (ImageButton) view.findViewById(R.id.frag_photo_imb_camera);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    private void setListeners() {
        imb_remove.setOnClickListener(this);
        imb_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_photo_imb_remove_photo:
                removeCurrentImage();
                break;

            case R.id.frag_photo_imb_camera:
                goToCameraFragment();
                break;
        }
        cameraCountDownTimer.cancel();
    }

    /**
     * Switch fragment to photo preview
     */
    private void goToCameraFragment() {
        if (getActivity() instanceof FragmentToggler) {
            ((FragmentToggler) getActivity()).toggleFragments(TransactionType.PERMANENT);
        }
    }

    /**
     * Deletes current image form storage
     */
    private void removeCurrentImage() {
        int currentFilePosition = viewPager.getCurrentItem();
        files.get(currentFilePosition).delete();
        getFileList();
        //  adapter.notifyDataSetChanged();
    }

    private TransactionType getTransactionType() {
        return (TransactionType) getArguments().getSerializable(Extras.TRANSACTION_TYPE);
    }

    /**
     * If TransactionType is TEMPRORAILY, after timer finished we go back to {@link CameraFragment}
     */
    private CountDownTimer cameraCountDownTimer = new CountDownTimer(PERIOD, TICK) {

        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            goToCameraFragment();
        }
    };

    /**
     * Gets list of files in this folder
     */
    private void getFileList() {
        files.clear();
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
        Collections.addAll(files, root.listFiles());
        Collections.sort(files, new CustomComparator());
        adapter = new ViewPagerAdapter(getActivity(), files);
        viewPager.setAdapter(adapter);

        imb_remove.setVisibility(files.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }
}
