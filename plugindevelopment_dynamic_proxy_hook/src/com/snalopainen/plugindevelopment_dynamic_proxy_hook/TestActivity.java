package com.snalopainen.plugindevelopment_dynamic_proxy_hook;

import com.snalopainen.plugindevelopment_dynamic_proxy_hook.R;

import android.app.Activity;
import android.os.Bundle;
public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);
	}
}
