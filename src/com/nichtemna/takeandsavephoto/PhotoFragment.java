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
	public enum ImageSetMode {
		LAST, LEFT, RIGHT, POSITION
	}

	private final static int PERIOD = 2000;
	private final static int TICK = 1000;

	private ImageButton imb_remove, imb_camera, imb_left, imb_right;
	private ImageView iv_photo;
	private int currentFilePosition = 0;
	private File[] files;

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
		getFileList();
		managePreview(ImageSetMode.LAST);
		if (getTransactionType().equals(TransactionType.TEMPRORAILY)) {
			cameraCountDownTimer.start();
		}
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Gets list of files in this folder
	 */
	private void getFileList() {
		File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
		files = root.listFiles();
	}

	private void initViews(View view) {
		imb_remove = (ImageButton) view.findViewById(R.id.frag_photo_imb_remove_photo);
		imb_camera = (ImageButton) view.findViewById(R.id.frag_photo_imb_camera);
		imb_left = (ImageButton) view.findViewById(R.id.frag_photo_imb_left);
		imb_right = (ImageButton) view.findViewById(R.id.frag_photo_imb_right);
		iv_photo = (ImageView) view.findViewById(R.id.frag_photo_iv);
	}

	private void setListeners() {
		imb_remove.setOnClickListener(this);
		imb_camera.setOnClickListener(this);
		imb_left.setOnClickListener(this);
		imb_right.setOnClickListener(this);
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

		case R.id.frag_photo_imb_left:
			managePreview(ImageSetMode.LEFT);
			break;

		case R.id.frag_photo_imb_right:
			managePreview(ImageSetMode.RIGHT);
			break;

		default:
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
		if (files.length > 0 && files.length >= currentFilePosition - 1) {
			files[currentFilePosition].delete();
			getFileList();
			if (files.length > 0) {
				if (files.length > currentFilePosition) {
					// show image with same position
					managePreview(ImageSetMode.POSITION);
				} else {
					managePreview(ImageSetMode.LAST);
				}
			} else {
				managePreview(ImageSetMode.LAST);
			}
		}
	}

	private TransactionType getTransactionType() {
		return (TransactionType) getArguments().getSerializable(Extras.TRANSACTION_TYPE);
	}

	/**
	 * Set last taken image to imageView
	 */
	private void managePreview(ImageSetMode mode) {
		if (files.length > 0) {
			switch (mode) {
			case LAST:
				currentFilePosition = 0;
				break;

			case LEFT:
				currentFilePosition = currentFilePosition + 1;
				break;

			case RIGHT:
				currentFilePosition = currentFilePosition - 1;
				break;

			case POSITION:
				// currentFilePosition = currentFilePosition; //mean that current position stay the same
				break;
			default:
				break;
			}
			setImage();
		} else {
			iv_photo.setImageResource(android.R.color.transparent);
		}
		setButtonsVisibility();
	}

	/**
	 * Sets image to imageView
	 */
	private void setImage() {
		File currentFile = files[currentFilePosition];
		Bitmap bitmap = BitmapFactory.decodeFile(currentFile.getAbsolutePath());
		iv_photo.setImageBitmap(bitmap);
	}

	/**
	 * Sets buttons visibility to allow user only correct movements
	 */
	private void setButtonsVisibility() {
		// Button Remove
		if (files.length > 0) {
			imb_remove.setVisibility(View.VISIBLE);
		} else {
			imb_remove.setVisibility(View.INVISIBLE);
		}

		// Button Left
		if (files.length > 1 && files.length > currentFilePosition + 1) {
			imb_left.setVisibility(View.VISIBLE);
		} else {
			imb_left.setVisibility(View.INVISIBLE);
		}

		// Button Right
		if (files.length > 1 && currentFilePosition > 0) {
			imb_right.setVisibility(View.VISIBLE);
		} else {
			imb_right.setVisibility(View.INVISIBLE);
		}
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
}
