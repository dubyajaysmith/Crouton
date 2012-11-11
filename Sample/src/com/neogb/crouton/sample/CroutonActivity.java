/*
 * Copyright (C) 2012 Guillaume BOUERAT (https://github.com/GBouerat/Crouton)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.neogb.crouton.sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;

import com.neogb.crouton.Crouton;
import com.neogb.crouton.CroutonView;

public class CroutonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crouton_activity);
        
        enableStrictMode();

        final CroutonView croutonView = (CroutonView) findViewById(R.id.crouton);

        findViewById(R.id.info).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Crouton.makeText(croutonView, "Crouton type : Info", Crouton.STYLE_INFO).show();
            }
        });
        findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Crouton.makeText(croutonView, "Crouton type : Confirm", Crouton.STYLE_CONFIRM)
                        .show();
            }
        });
        findViewById(R.id.alert).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Crouton.makeText(croutonView, "Crouton type : Alert", Crouton.STYLE_ALERT).show();
            }
        });
    }

    @TargetApi(9)
    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().build());
        }
    }

}
