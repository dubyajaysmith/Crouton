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

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CroutonView extends TextView {

	private static final int MESSAGE_SHOW = 0x0000;

	private static final int MESSAGE_HIDE = 0x0001;

	private Animation mInAnimation;

    private Animation mOutAnimation;

    private Handler mHandler;

    private Queue<Message> mMessageQueue;

	protected Message mCurrentShowMessage;

    public CroutonView(Context context) {
        super(context);
        initView(context);
    }

    public CroutonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CroutonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ViewParent viewParent = getParent();
		if (!(viewParent instanceof FrameLayout) && !(viewParent instanceof RelativeLayout)) {
			throw new RuntimeException("CroutonView's parent must be a FrameLayout or a RelativeLayout.");
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		int size = mMessageQueue.size();
		if (size == 0) {
			if (getVisibility() == View.VISIBLE) {
				return new SavedState(superState, true, mCurrentShowMessage);
			}
			else {
				return superState;
			}
		}
		else {
			Message[] array = new Message[size];
			mMessageQueue.toArray(array);
			if (getVisibility() == View.VISIBLE) {
				return new SavedState(superState, true, mCurrentShowMessage, array);
			}
			else {
				return new SavedState(superState, array);
			}
		}
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());

		if (savedState.wasVisible()) {
			if (savedState.hasMessages()) {
				Message[] messages = savedState.getMessages();
				for (int i = 0; i < messages.length; i++) {
					mMessageQueue.add(messages[i]);
				}
			}
			show(savedState.getText(), savedState.getStyle(), savedState.getDuration());
		}
		else if (savedState.hasMessages()) {
			Message[] messages = savedState.getMessages();
			for (int i = 0; i < messages.length; i++) {
				if (i == 0) {
					mHandler.sendMessage(messages[i]);
				}
				else {
					mMessageQueue.add(messages[i]);
				}
			}
		}
	}

    private void initView(Context context) {
    	if (isInEditMode()) return;

        mMessageQueue = new LinkedList<Message>();

		mHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
					case CroutonView.MESSAGE_SHOW:
						mCurrentShowMessage = Message.obtain(msg);
						setText((CharSequence) msg.obj);
						setBackgroundResource(msg.arg1);
						getParent().bringChildToFront(CroutonView.this);
						startAnimation(mInAnimation);
						mHandler.sendEmptyMessageDelayed(CroutonView.MESSAGE_HIDE, msg.arg2);
						break;
					default: // MESSAGE_HIDE
						if (mMessageQueue.isEmpty()) {
							startAnimation(mOutAnimation);
						}
						else {
                            Message message = mMessageQueue.poll();
							mCurrentShowMessage = Message.obtain(message);
							setText((CharSequence) message.obj);
                            setBackgroundResource(message.arg1);
                            mHandler.sendEmptyMessageDelayed(CroutonView.MESSAGE_HIDE, message.arg2);
						}
						break;
				}
				return true;
			}
		});

        mInAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        mInAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });

        mOutAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        mOutAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }
        });
    }

	void show(CharSequence text, int style, int duration) {
		if ((mHandler.hasMessages(MESSAGE_HIDE))) {
		    mMessageQueue.add(Message.obtain(mHandler, MESSAGE_SHOW, style, duration, text));
		}
		else {
			Message.obtain(mHandler, MESSAGE_SHOW, style, duration, text).sendToTarget();
		}
    }

	public static class SavedState extends BaseSavedState {

		private final boolean mVisible;
		private final Message[] mMessages;
		private final String mText;
		private final int mStyle;
		private final int mDuration;

		public SavedState(Parcelable superState, boolean visible, Message message) {
			this(superState, visible, message, null);
		}

		public SavedState(Parcelable superState, Message[] messages) {
			this(superState, false, null, messages);
		}

		public SavedState(Parcelable superState, boolean visible, Message message, Message[] messages) {
			super(superState);
			mVisible = visible;
			mText = (String) message.obj;
			mStyle = message.arg1;
			mDuration = message.arg2;
			mMessages = messages;
		}

		public SavedState(Parcel in) {
			super(in);
			mVisible = in.readInt() == 1;
			mText = in.readString();
			mStyle = in.readInt();
			mDuration = in.readInt();
			mMessages = in.createTypedArray(Message.CREATOR);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mVisible ? 1 : 0);
			dest.writeString(mText);
			dest.writeInt(mStyle);
			dest.writeInt(mDuration);
			dest.writeTypedArray(mMessages, flags); //TODO Can't marshal non-Parcelable objects across processes
		}

		public boolean wasVisible() {
			return mVisible;
		}

		public String getText() {
			return mText;
		}

		public int getStyle() {
			return mStyle;
		}

		public int getDuration() {
			return mDuration;
		}

		public boolean hasMessages() {
			return mMessages != null;
		}

		public Message[] getMessages() {
			return mMessages;
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

	}

}
