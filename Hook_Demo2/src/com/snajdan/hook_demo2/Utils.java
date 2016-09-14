package com.snajdan.hook_demo2;

import android.app.Activity;
import android.content.Intent;

public class Utils {
	public static void invokePluginActivity(Activity activity, Class<?> who) {
		Intent intent = new Intent(activity, who);
		intent.putExtra(Constants.EXTRA_INVOKE_PLUGIN, true);
		activity.startActivity(intent);
	}
}
