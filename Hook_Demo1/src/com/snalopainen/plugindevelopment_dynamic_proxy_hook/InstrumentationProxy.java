package com.snalopainen.plugindevelopment_dynamic_proxy_hook;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class InstrumentationProxy extends Instrumentation {

	private Instrumentation m_base;

	public InstrumentationProxy(Instrumentation base) {
		m_base = base;
	}

	/**
	 * @param who
	 * @param contextThread
	 * @param token
	 * @param target
	 * @param intent
	 * @param requestCode
	 * @param options
	 * @return {@hide}
	 */
	public ActivityResult execStartActivity(Context who, IBinder contextThread,
			IBinder token, Activity target, Intent intent, int requestCode,
			Bundle options) {

		Log.d("snajdan_debug", "I am snajdan`naliza");

		try {
			Method execStartActivity = Instrumentation.class.getDeclaredMethod(
					"execStartActivity", Context.class, IBinder.class,
					IBinder.class, Activity.class, Intent.class, int.class,
					Bundle.class);
			execStartActivity.setAccessible(true);
			return (ActivityResult) execStartActivity.invoke(m_base, who,
					contextThread, token, target, intent, requestCode, options);
		} catch (Exception e) {
			// 该死的国内rom修改了 需要手动适配
			throw new RuntimeException("do not support!!! pls adapt it");
		}
	}
}
