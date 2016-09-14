package com.snajdan.hook;

import com.example.hook1.R;

import android.app.Activity;
import android.os.Bundle;
public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
	}
}
