package com.snajdan.hook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;
import android.widget.ScrollView;

public class HookApplication extends Application {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		Class<?> activityThreadClass = null;
		try {
			// 加载activity thread的class
			activityThreadClass = Class.forName("android.app.ActivityThread",
					false, getClassLoader());
			// 找到方法currentActivityThread
			Method method = activityThreadClass
					.getDeclaredMethod("currentActivityThread");
			// 由于这个方法是静态的，所以传入Null就行了
			Object currentActivityThread = method.invoke(null);

			// 把之前ActivityThread中的mInstrumentation替换成我们自己的
			Field field = activityThreadClass
					.getDeclaredField("mInstrumentation");
			field.setAccessible(true);
			Instrumentation instrumentation = (Instrumentation) field
					.get(currentActivityThread);
			InstrumentationProxy instrumentationProxy = new InstrumentationProxy(
					instrumentation);
			field.set(currentActivityThread, instrumentationProxy);
		} catch (ClassNotFoundException e) {
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
