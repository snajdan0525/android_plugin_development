package com.snalopainen.plugindevelopment_binder_hook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;

public class HookApplication extends Application {

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		try {
			// 获得ServiceManger的class
			Class<?> serviceManager = Class.forName(
					"android.os.ServiceManager", false, getClassLoader());
			// 找到getService静态方法
			Method getService = serviceManager.getDeclaredMethod("getService",
					String.class);
			// 获得原始的binder对象
			IBinder rawBinder = (IBinder) getService.invoke(null,
					CLIPBOARD_SERVICE);

			// 定制我们自己的IBinder对象
			ProxyBinder proxyBinder = new ProxyBinder(rawBinder, this);
			IBinder proxy = (IBinder) Proxy.newProxyInstance(getClassLoader(),
					new Class[] { IBinder.class }, proxyBinder);

			// 注入到ServiceManager的sCache中
			Field field = serviceManager.getDeclaredField("sCache");
			field.setAccessible(true);
			Map<String, IBinder> map = (Map<String, IBinder>) field.get(null);
			map.put(CLIPBOARD_SERVICE, proxy);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}