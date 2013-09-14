package com.nichtemna.takeandsavephoto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

public class PhotoActivity extends FragmentActivity implements FragmentToggler {
	/**
	 * Flag to change fragments for few seconds or permanently
	 */
	public enum TransactionType {
		PERMANENT, TEMPRORAILY
	}

	private boolean isCamVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_photo);
		toggleFragments(TransactionType.PERMANENT);
	}

	@Override
	public void toggleFragments(TransactionType type) {
		if (isCamVisible) {
			isCamVisible = false;
			getSupportFragmentManager().beginTransaction().replace(R.id.ac_photo_frame_main, PhotoFragment.newInstance(type), Extras.FRAGMENT_CAMERA)
					.commit();
		} else {
			isCamVisible = true;
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.ac_photo_frame_main, CameraFragment.newInstance(type), Extras.FRAGMENT_CAMERA).commit();
		}
		getSupportFragmentManager().executePendingTransactions();
	}
}
