package com.nichtemna.takeandsavephoto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
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

import com.nichtemna.takeandsavephoto.PhotoActivity.TransactionType;

public class CameraFragment extends Fragment implements OnClickListener {
	private static final int MEDIA_TYPE_IMAGE = 100;
	private Preview mPreview;
	private Camera mCamera;
	private FrameLayout mFrameLayout;
	private ImageButton imb_photo, imb_take_photo;

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
		mCamera = Camera.open();
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
	 * Callback for taken photo
	 */
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			saveTakenPhoto(data);
			goToPhotoFragment(TransactionType.TEMPRORAILY);
		}
	};

	/**
	 * Switch fragment to photo preview
	 */
	private void goToPhotoFragment(TransactionType type) {
		if (getActivity() instanceof FragmentToggler) {
			((FragmentToggler) getActivity()).toggleFragments(type);
		}
	}

	/**
	 * Saves taken image to Sdcard picrute derictory
	 * 
	 * @param data
	 *            - byte[] of taken picture
	 */
	private void saveTakenPhoto(byte[] data) {
		File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
		if (pictureFile == null) {
			Log.d("tag", "Error creating media file, check storage permissions: ");
			return;
		}
		try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d("tag", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("tag", "Error accessing file: " + e.getMessage());
		}
	}

	/**
	 * Create a File for saving an image or video
	 */
	private File getOutputMediaFile(int type) {

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

}
