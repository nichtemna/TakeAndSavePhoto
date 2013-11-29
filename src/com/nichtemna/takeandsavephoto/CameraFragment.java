package com.nichtemna.takeandsavephoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import com.nichtemna.takeandsavephoto.PhotoActivity.TransactionType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraFragment extends Fragment implements OnClickListener {
    private static final int MEDIA_TYPE_IMAGE = 100;

    private Preview mPreview;
    private Camera mCamera;
    private FrameLayout mFrameLayout;
    private ImageButton imb_photo, imb_take_photo;
    private int numberOfCameras;
    private int cameraCurrentlyLocked;
    private int defaultCameraId;
    private boolean isBackCamera = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        numberOfCameras = Camera.getNumberOfCameras();
        findIdOfDefaultCamera();
        super.onCreate(savedInstanceState);
    }

    private void findIdOfDefaultCamera() {
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                defaultCameraId = i;
                isBackCamera = true;
            }
        }
    }

    public static CameraFragment newInstance(TransactionType type) {
        CameraFragment frag = new CameraFragment();
        final Bundle args = new Bundle();
        args.putSerializable(Extras.TRANSACTION_TYPE, type);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        initViews(view);
        setListeners();
        return view;
    }

    private void initViews(View view) {
        mFrameLayout = (FrameLayout) view.findViewById(R.id.frag_cam_frame_surface);
        mPreview = new Preview(getActivity(), (SurfaceView) view.findViewById(R.id.frag_cam_surfaceView));
        mPreview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mFrameLayout.addView(mPreview);
        imb_photo = (ImageButton) view.findViewById(R.id.frag_cam_imb_photo);
        imb_take_photo = (ImageButton) view.findViewById(R.id.frag_cam_imb_take_photo);
    }

    private void setListeners() {
        imb_photo.setOnClickListener(this);
        imb_take_photo.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(defaultCameraId);
        mPreview.setmCameraID(defaultCameraId);
        mPreview.setCamera(mCamera);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_cam_imb_photo:
                goToPhotoFragment(TransactionType.PERMANENT);
                break;
            case R.id.frag_cam_imb_take_photo:
                mCamera.takePicture(null, null, jpegCallback);
                break;
            default:
                break;
        }
    }

    /**
     * Switch fragment to photo preview
     */
    private void goToPhotoFragment(TransactionType type) {
        if (getActivity() instanceof FragmentToggler) {
            ((FragmentToggler) getActivity()).toggleFragments(type);
        }
    }

    /**
     * Callback for taken photo
     */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            saveTakenPhoto(data);
        }
    };

    /**
     * Saves taken image to Sdcard picrute derictory
     *
     * @param data - byte[] of taken picture
     */
    private void saveTakenPhoto(byte[] data) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            bitmap = ImageUtils.rescaleBitmap(bitmap);

            if (isBackCamera) {
                bitmap = ImageUtils.rotate(bitmap, 90, false);
            } else {
                bitmap = ImageUtils.rotate(bitmap, -90, true);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
        File pictureFile = ImageUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("tag", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("tag", "Error accessing file: " + e.getMessage());
        } finally {
            Toast.makeText(getActivity(), "File saved " + pictureFile.getName(), Toast.LENGTH_LONG).show();
            goToPhotoFragment(TransactionType.TEMPRORAILY);
        }
    }


}
