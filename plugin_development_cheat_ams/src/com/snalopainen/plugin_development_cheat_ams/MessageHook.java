package com.snalopainen.plugin_development_cheat_ams;

import java.lang.reflect.Field;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class MessageHook implements Handler.Callback {

	private Handler.Callback m_base;
	private static final int LAUNCH_ACTIVITY = 100;
	private Field m_intentField;

	public MessageHook(Object base, ClassLoader classLoader)
			throws ClassNotFoundException, NoSuchFieldException {
		m_base = (Handler.Callback) base;
		// 获取ActivityClientRecord的class
		Class<?> activityClientRecordClz = Class.forName(
				"android.app.ActivityThread$ActivityClientRecord", false,
				classLoader);
		// 获得它的intent
		m_intentField = activityClientRecordClz.getDeclaredField("intent");
		m_intentField.setAccessible(true);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// 检测到时启动一个activit
		if (msg.what == LAUNCH_ACTIVITY) {
			try {
				// msg.obj是android.app.ActivityThread$ActivityClientRecord对象，请参考前面的源码解析
				Intent intent = (Intent) m_intentField.get(msg.obj);
				ComponentName componentName = intent.getComponent();
				// 检测到是启动StubActivity
				if (componentName != null
						&& componentName.getClassName().equals(
								SubActivity.class.getCanonicalName())) {
					// 获得之前启动插件的intent
					Intent raw = intent
							.getParcelableExtra(Constants.EXTRA_RAW_INTENT);
					// 替换成插件的component
					intent.setComponent(raw.getComponent());
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		// 之后的操作还是和原来一样
		return m_base != null && m_base.handleMessage(msg);
	}
}
