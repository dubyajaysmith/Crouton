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
package com.neogb.crouton;

public class Crouton {

    private static final int LENGTH_SHORT = 1300;

    private static final int LENGTH_LONG = 3000;

    public static final int STYLE_ALERT = R.color.crouton_alert;

    public static final int STYLE_CONFIRM = R.color.crouton_confirm;

    public static final int STYLE_INFO = R.color.crouton_info;

    private final CroutonView mCroutonView;

	private final CharSequence mText;

	private final int mStyle;

    public static Crouton makeText(CroutonView croutonView, CharSequence text, int style) {
        return new Crouton(croutonView, text, style);
    }

    public static Crouton makeText(CroutonView croutonView, int resId, int style) {
        return new Crouton(croutonView, croutonView.getContext().getString(resId), style);
    }

    private Crouton(CroutonView croutonView, CharSequence text, int style) {
		if (croutonView == null) {
			throw new IllegalArgumentException("CroutonView can not be null.");
		}
		else if (style != STYLE_ALERT && style != STYLE_CONFIRM && style != STYLE_INFO) {
			throw new IllegalArgumentException("You have to use a Crouton's style.");
		}
        mCroutonView = croutonView;
		mText = text;
		mStyle = style;
    }

    public void show() {
		mCroutonView.show(mText, mStyle, (mStyle == STYLE_ALERT ? LENGTH_LONG : LENGTH_SHORT));
    }

}
