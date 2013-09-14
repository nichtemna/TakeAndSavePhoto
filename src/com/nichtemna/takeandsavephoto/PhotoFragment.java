package com.nichtemna.takeandsavephoto;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nichtemna.takeandsavephoto.PhotoActivity.TransactionType;

public class PhotoFragment extends Fragment implements OnClickListener {
	private final static int PERIOD = 3000;

	private ImageButton imb_remove, imb_camera;
	private ImageView iv_photo;
	private File currentFile;

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
		setImagePreview();
		if (getTransactionType().equals(TransactionType.TEMPRORAILY)) {
			cameraCountDownTimer.start();
		}
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews(View view) {
		imb_remove = (ImageButton) view.findViewById(R.id.frag_photo_imb_remove_photo);
		imb_camera = (ImageButton) view.findViewById(R.id.frag_photo_imb_camera);
		iv_photo = (ImageView) view.findViewById(R.id.frag_photo_iv);
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
		default:
			break;
		}
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
		if (currentFile != null) {
			currentFile.delete();
		}
	}

	private TransactionType getTransactionType() {
		return (TransactionType) getArguments().getSerializable(Extras.TRANSACTION_TYPE);
	}

	private void setImagePreview() {
		File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
		File[] files = root.listFiles();
		if (files.length > 0) {
			currentFile = files[files.length - 1];
			Bitmap bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
			iv_photo.setImageBitmap(bitmap);
		} else {
			imb_remove.setEnabled(false);
		}
	}

	private CountDownTimer cameraCountDownTimer = new CountDownTimer(PERIOD, 0) {

		public void onTick(long millisUntilFinished) {
		}

		public void onFinish() {
			goToCameraFragment();
		}
	}.start();

}