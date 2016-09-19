package com.snajdan.hook_demo4;

import com.example.hook_demo4.R;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button button;
	private ClipboardManager m_clipboardManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button);
		m_clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ClipData.Item item = m_clipboardManager.getPrimaryClip()
						.getItemAt(0);
				Toast.makeText(MainActivity.this, item.getText(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
