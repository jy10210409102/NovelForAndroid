package com.chenli.photo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenli.dao.NovelInfo;
import com.chenli.novelforandroid.R;
import com.chenli.support.PhotoSex;

public class PhotoActivity extends Activity {

	ListView list;
	PhotoSex sex;
	TextView text;
	RelativeLayout rela;
	Button but;
	EditText edit;
	EditText urlText;
	ArrayAdapter<String> arrayAdapter;
	LinearLayout photoShow;
	final String splitStr = " ### ";
	boolean isRun = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_activity);
		sex = new PhotoSex();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		list = (ListView) super.findViewById(R.id.novelList);
		text = (TextView) super.findViewById(R.id.text);
		rela = (RelativeLayout)super.findViewById(R.id.rela);
		but = (Button)super.findViewById(R.id.but);
		edit = (EditText)super.findViewById(R.id.edit);
		urlText = (EditText)super.findViewById(R.id.url);
		photoShow = (LinearLayout)findViewById(R.id.photoShow);
		urlText.setText(sex.getUrl(PhotoActivity.this));
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, getData());
		list.setAdapter(arrayAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				isRun = false;
				String url = ((TextView) view).getText().toString().split(splitStr)[1];
				Log.e("chenli", "点击为" + url);
				showText(url);
			}

		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				isRun = false;
				String url = ((TextView) view).getText().toString().split(splitStr)[1];
				Log.e("chenli", "点击为" + url);
				openUrl(url);
				return true;
			}
		});
		
		but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				arrayAdapter.clear();
				final int type = Integer.parseInt(edit.getText().toString());
				sex.setUrl(PhotoActivity.this, urlText.getText().toString()); 
				new Thread() {
					public void run() {
						if (isRun) {
							isRun = false; // 关闭线程
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						isRun = true;
						getUrlData(type);	// 获取数据
					}
				}.start();
				
			}
		});
	}

	private void showText(final String url) {
		this.photoShow.setVisibility(View.VISIBLE);
		rela.setVisibility(View.GONE);
		new Thread(){
			public void run(){
				ArrayList<String> photoList = sex.saveNove2(url);
				if(photoShow.getChildCount() != 0){
					photoShow.removeAllViewsInLayout();
				}
				Message msg = Message.obtain();
				msg.what = 2;
				msg.obj = photoList;
				hander.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.photoShow.setVisibility(View.GONE);
			rela.setVisibility(View.VISIBLE);
			//finish();
			return true;
		}
		return false;
	}

	private void getUrlData(int type) {
		String url = PhotoSex.NOVEL_LIST_URL + type;
		Log.e("chenli", "url = " + url);
		// 获得每页的内容
		int sumNum = sex.getSeletecPageCount(url);

		// 遍历得到每页的信息
		for (int n = 1; n < sumNum; n++) {
			Log.e("chenli", "页面n = " + n + " 页面总数：" +  sumNum +"   url =" + url + "/" + n + ".htm");
			ArrayList<NovelInfo> novelInfos = sex.selectNovelInfo(url + "/" + n
					+ ".htm");
			for (int i = 0; i < novelInfos.size(); i++) {
				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = novelInfos.get(i);
				hander.sendMessage(msg);
				if(!isRun){
					return ;
				}
			}
		}

	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>();
		return data;
	}

	@SuppressLint("HandlerLeak")
	Handler hander = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				NovelInfo novelInfo = (NovelInfo) msg.obj;
				if (novelInfo != null) {
					arrayAdapter.add(novelInfo.name + splitStr + novelInfo.url);
				}
				break;
			case 2:
				ArrayList<String> photoList = (ArrayList<String>) msg.obj;
				for(int i = 0; i < photoList.size() ; i++){
					Button but = new Button(PhotoActivity.this);
					but.setText(photoList.get(i));
					but.setTag(photoList.get(i));
					but.setOnClickListener(butOnClickListener);
					photoShow.addView(but);
				}
				break;
			}
		}
	};
	
	OnClickListener butOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
	        openUrl((String)v.getTag());
		}
	};
	
	private void openUrl(String url){
		 Intent intent = new Intent();        
	        intent.setAction("android.intent.action.VIEW");    
	        Uri content_url = Uri.parse(url);   
	        intent.setData(content_url);  
	        startActivity(intent);
	}
	
}
