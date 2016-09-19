package com.snajdan.hook_demo4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.content.ClipData;

public class BinderHook implements InvocationHandler {
	private Object m_base;

	public BinderHook(Object iClipboardInterface) {
		m_base = iClipboardInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// 当调用getPrimaryClip的时候 我们始终返回我们自己定义的一段字符串
		if ("getPrimaryClip".equals(method.getName())) {
			return ClipData.newPlainText(null, "I am crazy");
		}

		// 使得客户端一直默认粘贴板中有东西
		if ("getPrimaryClip".equals(method.getName())) {
			return true;
		}

		// 其它的操作都还是一样调用远程的服务接口
		return method.invoke(m_base, args);
	}
}