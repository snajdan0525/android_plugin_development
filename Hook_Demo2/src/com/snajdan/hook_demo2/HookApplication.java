package com.snajdan.hook_demo2;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;

public class HookApplication extends Application {
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		try {
			// 获得ActivityManagerNative
			Class<?> serviceManagerClz = Class.forName(
					"android.app.ActivityManagerNative", false,
					getClassLoader());
			// 获得ActivityManagerNative.getDefault静态方法
			Method getDefaultMethod = serviceManagerClz
					.getDeclaredMethod("getDefault");//IActivityManager

			// 获得原始的IActivityManager对象
			Object rawIActivityManagerInterface = getDefaultMethod.invoke(null);//IActivityManager
			
			// 我们自己的Hook的对象
			Object hookIActivityManagerInterface = Proxy.newProxyInstance(
					getClassLoader(), new Class[] { Class.forName(
							"android.app.IActivityManager", false,
							getClassLoader()) }, new AMSHook(
							rawIActivityManagerInterface));

			// 反射ActivityManagerNative的gDefault域
			Field gDefaultField = serviceManagerClz
					.getDeclaredField("gDefault");
			gDefaultField.setAccessible(true);
			Object gDefaultObject = gDefaultField.get(null);

			// 他的类型是Singleton
			Class<?> singletonClz = Class.forName("android.util.Singleton",
					false, getClassLoader());

			// 把他的mInstance域替换掉 成为我们自己的Hook对象
			Field mInstanceField = singletonClz.getDeclaredField("mInstance");
			mInstanceField.setAccessible(true);
			mInstanceField.set(gDefaultObject, hookIActivityManagerInterface);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
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
		}
	}
}
