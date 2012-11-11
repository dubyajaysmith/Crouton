Crouton
=======

Crouton is an Android library project designed to replace [Toast Notifications](https://developer.android.com/reference/android/widget/Toast.html).

It's a free implementation of this [Cyril Mottier](https://raw.github.com/cyrilmottier) blog post : [in-layout notifications](http://android.cyrilmottier.com/?p=773).

[![Crouton Info][1]][4][![Crouton Confirm][2]][5][![Crouton Alert][3]][6]

Usage
-----

1. Include a CroutonView widget in your layout.

        <com.neogb.crouton.CroutonView
	        android:id="@id/crouton"
	        style="@style/crouton"
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:layout_alignParentTop="true" />

	CroutonView's parent must be a FrameLayout or a RelativeLayout.

	You can also use layout_alignParentBottom instead of layout_alignParentTop, of course these two attributes works only with a RelativeLayout as parent.

2. In your `onCreate` method, find the CroutonView from your layout.

	mCroutonView = (CroutonView) findViewById(R.id.crouton);

3. Now you're ready to show some croutons.

	Create a Crouton for any CharSequence:

	    Crouton.makeText(CroutonView, CharSequence, int).show();
	    
	Create a Crouton with a String from your application's resources:

	    Crouton.makeText(CroutonView, int, int).show();

    The third parameter must be a Crouton style :

	* Crouton.STYLE_INFO
	* Crouton.STYLE_CONFIRM
	* Crouton.STYLE_ALERT


Developed by
------------

Guillaume BOUERAT

- [Google+](https://plus.google.com/u/0/112136052387869387989)
- [Twitter](https://twitter.com/GBouerat)
- [Google play](https://play.google.com/store/apps/developer?id=Guillaume+BOUERAT)

License
-------

	Copyright (C) 2012 Guillaume BOUERAT (https://github.com/GBouerat/Crouton)
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	     http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

[1]: https://github.com/GBouerat/Crouton/screenshots/crouton_info_small.png
[2]: https://github.com/GBouerat/Crouton/screenshots/crouton_confirm_small.png
[3]: https://github.com/GBouerat/Crouton/screenshots/crouton_alert_small.png
[4]: https://github.com/GBouerat/Crouton/screenshots/crouton_info.png
[5]: https://github.com/GBouerat/Crouton/screenshots/crouton_confirm.png
[6]: https://github.com/GBouerat/Crouton/screenshots/crouton_alert.png