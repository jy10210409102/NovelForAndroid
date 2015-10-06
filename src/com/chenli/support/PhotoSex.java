/**
 * 
 */
package com.chenli.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.chenli.dao.NovelInfo;

/**
 * ClassName:Main <br/>       
 * date: 2014年11月23日下午8:46:04 <br/>     
 * @author ChenLi            
 */
public class PhotoSex {
	private final String tag = "PhotoSex";
	private final String CODE = "UTF-8";
	public static String BASE_URL = "http://www.339zz.com";// "http://www.161zz.com";// http://www.161zz.com/htm/novellist2/1.htm
	public static String NOVEL_LIST_URL = BASE_URL + "/htm/piclist";
	private final int TYPE_MIN = 1;
	private final int TYPE_MAX = 8;
	int defualtType = 1;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PhotoSex main = new PhotoSex();
		main.main();
	}
	
	private void main(){
		String url = NOVEL_LIST_URL + defualtType;
		int sumNum = getSeletecPageCount(url);
		Log.e(tag, "页面总数：" + sumNum);
		//遍历得到每页的信息
	    for (int n = 1; n < sumNum; n++) {
	    	Log.e(tag, url + "/" + n + ".htm");
	    	 ArrayList<NovelInfo> novelInfos = selectNovelInfo(url + "/" + n + ".htm");
	    	 //下载内容
	    	 for(int i = 0; i < novelInfos.size(); i++){
	    		 saveNove(novelInfos.get(i));
	    		 //return;
	    	 }
	    	
            return;
        }
	}
	
	/**
	 * 获得搜索页数
	 */
	public int getSeletecPageCount(String url) {
		int num = 0;
		try {
			// 连接主页，获取html，开始进行解析  如何设置超时时间
			Document doc = Jsoup.connect(url).get();
			//获得页面开始标示3
			
			Elements nodes = doc.getElementsByClass("pagination");
			String temp = nodes.get(0).select("a[href]").last().attr("href");
			Log.e(tag, temp.replaceFirst(".htm", ""));
			num = Integer.parseInt(temp.replaceFirst(".htm", ""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return num;
	}
	
	public ArrayList<NovelInfo> selectNovelInfo(String str) {
		ArrayList<NovelInfo> list = new ArrayList<NovelInfo>();
		try {
			Document doc = Jsoup.connect(str).get();
			/*Element nodes =*/ List<Element> elements = doc.getElementById("text_box").parent().children();//parentNode().childNodes();
			for(int i = 0; i < elements.size(); i++){
				//如果是<li>节点
				Element element = elements.get(i);
				if(element.nodeName().equals("li")){
					//log(elements.get(i).toString());
					//开始解析
					NovelInfo novelInfo = new NovelInfo();
					String[] tempStr = element.child(0).text().split("【");
					if(tempStr.length == 1)
					{
						novelInfo.name = element.child(0).text();
						novelInfo.time = "未知";
					}else{
						novelInfo.name = "【" + tempStr[1];
						novelInfo.time = tempStr[0];
					}
					novelInfo.url = BASE_URL + element.childNode(0).attr("href");
					Log.e("main","name:" + novelInfo.name + ",url:" + novelInfo.url + ",time:" + novelInfo.time);
					list.add(novelInfo);
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public boolean saveNove(NovelInfo novelInfo) {
		try {
			Document doc = Jsoup.connect(novelInfo.url).get();
			Elements elements =	doc.getElementsByClass("pics").get(0).getElementsByTag("img");
			/*return*/ 
			Log.e(tag, "0::" + elements.size());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < elements.size(); i++){
				list.add(elements.get(i).getAllElements().attr("src").toString());
				Log.e(tag, list.get(i));
			}
			novelInfo.photoList = list;
			//Log.e("chenli", elements.get(0).getElementsByTag("img").get(5).getAllElements().attr("src").toString());
//			Log.e("main", elements.get(0).html().replaceAll("<br />", "").replaceAll("\n\n", "\n"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<String> saveNove2(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements elements =	doc.getElementsByClass("pics").get(0).getElementsByTag("img");
			/*return*/ 
			Log.e(tag, "0::" + elements.size());
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < elements.size(); i++){
				list.add(elements.get(i).getAllElements().attr("src").toString());
				Log.e(tag, list.get(i));
			}
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void setUrl(Context context, String url){
		BASE_URL = url;
		NOVEL_LIST_URL = BASE_URL + "/htm/piclist";
		//写入文件
		SharedPreferences sharedPreferences = context.getSharedPreferences("photourl", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("url", url);
		editor.commit();//提交修改
	}
	
	public String getUrl(Context context){
		String url = "";
		//读取文件
		SharedPreferences share= context.getSharedPreferences("photourl", Context.MODE_PRIVATE);
		url=share.getString("str", BASE_URL);
		return url;
	}


}
