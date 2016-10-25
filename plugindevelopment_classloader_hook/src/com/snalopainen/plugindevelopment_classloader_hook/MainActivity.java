package com.snalopainen.plugindevelopment_classloader_hook;

import java.lang.reflect.Field;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	private static Object getPathList(Object baseDexClassLoader)
			throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
		return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
	}
	
	private static Object getField(Object obj, Class<?> cl, String field)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field localField = cl.getDeclaredField(field);
		localField.setAccessible(true);
		return localField.get(obj);
	}
	
	private static Object getDexElements(Object paramObject)
			throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		return getField(paramObject, paramObject.getClass(), "dexElements");
	}
	
	//setField(pathList, pathList.getClass(), "dexElements", dexElements);
	private static void setField(Object obj, Class<?> cl, String field,
			Object value) throws NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {

		Field localField = cl.getDeclaredField(field);
		localField.setAccessible(true);
		localField.set(obj, value);
	}
}
