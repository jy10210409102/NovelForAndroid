package com.chenli.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.chenli.novel.NovelActivity;
import com.chenli.novelforandroid.R;
import com.chenli.photo.PhotoActivity;

public class MainActivity extends Activity {

	Button novelBut;
	Button photoBut;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		novelBut = (Button)findViewById(R.id.novelBut);
		photoBut = (Button)findViewById(R.id.photoBut);
		novelBut.setOnClickListener(butOnClickListener);
		photoBut.setOnClickListener(butOnClickListener);
	}
	
	OnClickListener butOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent it = new Intent();
			switch(v.getId()){
			case R.id.novelBut:
				it.setClass(MainActivity.this,NovelActivity.class);
				startActivity(it);
				break;
			case R.id.photoBut:
				it.setClass(MainActivity.this,PhotoActivity.class);
				startActivity(it);
				break;
			}
		}
	};
}
