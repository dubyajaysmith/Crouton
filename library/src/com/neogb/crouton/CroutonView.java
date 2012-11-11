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
import android.os.Handler.Callback;
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

public class CroutonView extends TextView implements Callback {

	private static final int MESSAGE_SHOW = 0x0000;

	private static final int MESSAGE_HIDE = 0x0001;

	private Animation mInAnimation;

    private Animation mOutAnimation;

    private Handler mHandler;

	private Queue<MessageHolder> mMessageHolderQueue;

	private MessageHolder mCurrentMessageHolder;

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
	protected void onDetachedFromWindow() {
		mHandler.removeMessages(MESSAGE_HIDE, null);
		super.onDetachedFromWindow();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		mHandler.removeMessages(MESSAGE_HIDE, null);
		setVisibility(GONE);
		int size = mMessageHolderQueue.size();
		if (size == 0) {
			if (getVisibility() == View.VISIBLE) {
				return new SavedState(superState, true, mCurrentMessageHolder);
			}
			else {
				return superState;
			}
		}
		else {
			MessageHolder[] array = new MessageHolder[size];
			mMessageHolderQueue.toArray(array);
			if (getVisibility() == View.VISIBLE) {
				return new SavedState(superState, true, mCurrentMessageHolder, array);
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
			if (savedState.hasMessageHolders()) {
				MessageHolder[] messageHolders = savedState.getMessageHolders();
				for (int i = 0; i < messageHolders.length; i++) {
					mMessageHolderQueue.add(messageHolders[i]);
				}
			}
			mHandler.sendMessage(Message.obtain(mHandler, MESSAGE_SHOW, savedState.getMessageHolder()));
		}
		else if (savedState.hasMessageHolders()) {
			MessageHolder[] messageHolders = savedState.getMessageHolders();
			Message message = Message.obtain(mHandler, MESSAGE_SHOW, messageHolders[0]);
			for (int i = 1; i < messageHolders.length; i++) {
				mMessageHolderQueue.add(messageHolders[i]);
			}
			mHandler.sendMessage(message);
		}
	}

    private void initView(Context context) {
    	if (isInEditMode()) return;

		mMessageHolderQueue = new LinkedList<MessageHolder>();

		mHandler = new Handler(this);

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

	@Override
	public boolean handleMessage(Message msg) {
		MessageHolder holder;
		switch (msg.what) {
			case CroutonView.MESSAGE_SHOW:
				mCurrentMessageHolder = new MessageHolder((MessageHolder) msg.obj);
				holder = (MessageHolder) msg.obj;
				setText(holder.text);
				setBackgroundResource(holder.style);
				getParent().bringChildToFront(CroutonView.this);
				startAnimation(mInAnimation);
				mHandler.sendEmptyMessageDelayed(CroutonView.MESSAGE_HIDE, holder.duration);
				break;
			default: // MESSAGE_HIDE
				if (mMessageHolderQueue.isEmpty()) {
					startAnimation(mOutAnimation);
				}
				else {
					holder = (MessageHolder) mMessageHolderQueue.poll();
					mCurrentMessageHolder = new MessageHolder(holder);
					setText(holder.text);
					setBackgroundResource(holder.style);
					mHandler.sendEmptyMessageDelayed(CroutonView.MESSAGE_HIDE, holder.duration);
				}
				break;
		}
		return true;
	}

	void show(CharSequence text, int style, int duration) {
		MessageHolder holder = new MessageHolder(text, style, duration);
		if ((mHandler.hasMessages(MESSAGE_HIDE))) {
			mMessageHolderQueue.add(holder);
		}
		else {
			Message.obtain(mHandler, MESSAGE_SHOW, holder).sendToTarget();
		}
    }

	static class MessageHolder implements Parcelable {

		CharSequence text;
		int style;
		int duration;

		public MessageHolder(CharSequence text, int style, int duration) {
			this.text = text;
			this.style = style;
			this.duration = duration;
		}

		public MessageHolder(Parcel source) {
			text = source.readString();
			style = source.readInt();
			duration = source.readInt();
		}

		public MessageHolder(MessageHolder holder) {
			this.text = holder.text;
			this.style = holder.style;
			this.duration = holder.duration;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(text.toString());
			dest.writeInt(style);
			dest.writeInt(duration);
		}

		public static final Creator<MessageHolder> CREATOR = new Creator<MessageHolder>() {

			public MessageHolder createFromParcel(Parcel source) {
				return new MessageHolder(source);
			}

			public MessageHolder[] newArray(int size) {
				return new MessageHolder[size];
			}
		};

		public int describeContents() {
			return 0;
		}

	}

	public static class SavedState extends BaseSavedState {

		private final boolean mVisible;
		private final MessageHolder mMessageHolder;
		private final MessageHolder[] mMessageHolders;

		public SavedState(Parcelable superState, boolean visible, MessageHolder messageHolder) {
			this(superState, visible, messageHolder, null);
		}

		public SavedState(Parcelable superState, MessageHolder[] messageHolders) {
			this(superState, false, null, messageHolders);
		}

		public SavedState(Parcelable superState, boolean visible, MessageHolder messageHolder, MessageHolder[] messageHolders) {
			super(superState);
			mVisible = visible;
			mMessageHolder = messageHolder;
			mMessageHolders = messageHolders;
		}

		public SavedState(Parcel in) {
			super(in);
			mVisible = in.readInt() == 1;
			mMessageHolder = in.readParcelable(getClass().getClassLoader());
			mMessageHolders = in.createTypedArray(MessageHolder.CREATOR);
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(mVisible ? 1 : 0);
			dest.writeParcelable(mMessageHolder, flags);
			dest.writeTypedArray(mMessageHolders, flags);
		}

		public boolean wasVisible() {
			return mVisible;
		}

		public MessageHolder getMessageHolder() {
			return mMessageHolder;
		}

		public boolean hasMessageHolders() {
			return mMessageHolders != null;
		}

		public MessageHolder[] getMessageHolders() {
			return mMessageHolders;
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};

	}

}
