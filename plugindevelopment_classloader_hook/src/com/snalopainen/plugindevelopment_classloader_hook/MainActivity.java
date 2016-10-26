package com.snalopainen.plugindevelopment_classloader_hook;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

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
	
	/**
	 * ������һ�ַ�ʽʵ�ֵ�
	 * @param loader
	 */
	private void inject(DexClassLoader loader){
		PathClassLoader pathLoader = (PathClassLoader) getClassLoader();
		
		try {
			Object dexElements = combineArray(
					getDexElements(getPathList(pathLoader)),
					getDexElements(getPathList(loader)));
			Object pathList = getPathList(pathLoader);
			setField(pathList, pathList.getClass(), "dexElements", dexElements);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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

	private static Object combineArray(Object arrayLhs, Object arrayRhs) {
		Class<?> localClass = arrayLhs.getClass().getComponentType();
		int i = Array.getLength(arrayLhs);
		int j = i + Array.getLength(arrayRhs);
		Object result = Array.newInstance(localClass, j);
		for (int k = 0; k < j; ++k) {
			if (k < i) {
				Array.set(result, k, Array.get(arrayLhs, k));
			} else {
				Array.set(result, k, Array.get(arrayRhs, k - i));
			}
		}
		return result;
	}


}
