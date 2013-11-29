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

import android.net.Uri;
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
                    .commitAllowingStateLoss();
        } else {
            isCamVisible = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ac_photo_frame_main, CameraFragment.newInstance(type), Extras.FRAGMENT_CAMERA).commitAllowingStateLoss();
        }
        getSupportFragmentManager().executePendingTransactions();
    }
}
