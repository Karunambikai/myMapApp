package com.example.gmapsapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ViewFlipper;

public class How_To_Use extends Activity {

	private ViewFlipper viewFlipper;
	private float lastX;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.how_to_use);
		viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
	}

	public boolean onTouchEvent(MotionEvent touchevent) {
		switch (touchevent.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();
			if (lastX < currentX) {
				if (viewFlipper.getDisplayedChild() == 0)
					break;
				viewFlipper.setInAnimation(this, R.anim.in_from_left);
				viewFlipper.setOutAnimation(this, R.anim.out_to_right);
				viewFlipper.showNext();
			}
			if (lastX > currentX) {
				if (viewFlipper.getDisplayedChild() == 1)
					break;
				viewFlipper.setInAnimation(this, R.anim.in_from_right);
				viewFlipper.setOutAnimation(this, R.anim.out_to_left);
				viewFlipper.showPrevious();
			}
			break;
		}
		}
		return false;
	}
}
