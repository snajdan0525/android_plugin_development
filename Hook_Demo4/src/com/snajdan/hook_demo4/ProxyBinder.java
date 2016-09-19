package com.snajdan.hook_demo4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.content.Context;
import android.os.IBinder;
import android.os.IInterface;

public class ProxyBinder implements InvocationHandler {

	private Class<?> m_iClipboardInterfaceClz;
	private Object m_iClipboardInterface;
	private Object m_base;
	private Context m_context;

	public ProxyBinder(Object base, Context context)
			throws ClassNotFoundException, NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {

		m_base = base;
		m_context = context;

		// 获得IClipboard的class
		m_iClipboardInterfaceClz = Class.forName("android.content.IClipboard",
				false, context.getClassLoader());

		// 获得服务的Stub 这是aidl内容 需读者熟悉aidl
		Class<?> clipboardServiceStub = Class.forName(
				"android.content.IClipboard$Stub", false,
				context.getClassLoader());

		// 获取stub的asInterface静态方法
		Method asInterface = clipboardServiceStub.getDeclaredMethod(
				"asInterface", IBinder.class);

		// 获得android.content.IClipboard实例 这是系统默认的实例
		m_iClipboardInterface = asInterface.invoke(null, base);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// 修改queryLocalInterface的行为
		// 我们动态的修改为我们自定义的IBinder对象
		if ("queryLocalInterface".equals(method.getName())) {
			return Proxy.newProxyInstance(m_context.getClassLoader(),
					new Class[] { IBinder.class, m_iClipboardInterfaceClz,
							IInterface.class }, new BinderHook(
							m_iClipboardInterface));
		}

		// 其他的操作都是调用跟原来一样的操作
		return method.invoke(m_base, args);
	}
}
