package com.chenli.novel;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenli.dao.NovelInfo;
import com.chenli.novelforandroid.R;
import com.chenli.support.Sex;

public class NovelActivity extends Activity {

	ListView list;
	Sex sex;
	TextView text;
	RelativeLayout rela;
	Button but;
	EditText edit;
	EditText urlText;
	ArrayAdapter<String> arrayAdapter;
	final String splitStr = " ### ";
	boolean isRun = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.novel_activity);
		sex = new Sex();
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
		urlText.setText(sex.getUrl(NovelActivity.this));
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
		
		
		but.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				arrayAdapter.clear();
				final int type = Integer.parseInt(edit.getText().toString());
				sex.setUrl(NovelActivity.this, urlText.getText().toString()); 
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
		this.text.setVisibility(View.VISIBLE);
		rela.setVisibility(View.GONE);
		new Thread(){
			public void run(){
				String text = sex.saveNove2(url);
				Message msg = Message.obtain();
				msg.what = 2;
				msg.obj = text;
				hander.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.text.setVisibility(View.GONE);
			rela.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}

	private void getUrlData(int type) {
		String url = Sex.NOVEL_LIST_URL + type;
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
				String t = (String) msg.obj;
				text.setText(t);
				break;
			}
		}
	};

}
