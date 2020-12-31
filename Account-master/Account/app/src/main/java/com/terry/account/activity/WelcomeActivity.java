package com.terry.account.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.terry.account.R;


public class WelcomeActivity extends Activity
{
	private LinearLayout mLyWelcome;
	private ImageView mLogo;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);

		mLyWelcome = (LinearLayout) findViewById(R.id.ly_welcome);
		mLogo = (ImageView) findViewById(R.id.logo_welcome);

		//logo的动画
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
		mLogo.startAnimation(anim);

		AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(2200);
		mLyWelcome.setAnimation(animation);

		animation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {


			}

			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(WelcomeActivity.this,
						LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
}

