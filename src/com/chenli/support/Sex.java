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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.chenli.dao.NovelInfo;

/**
 * ClassName:Sex <br/>
 * date: 2014年5月25日下午11:58:38 <br/>
 * 
 * @author ChenLi
 */
public class Sex implements Support {

	public final String CODE = "UTF-8";
	public static String BASE_URL = "http://www.339zz.com/";// http://www.161zz.com/htm/novellist2/1.htm
	public static String NOVEL_LIST_URL = BASE_URL + "/htm/novellist";
	public final int TYPE_MIN = 1;
	public final int TYPE_MAX = 8;

	int defualtType = 1;

//	public static void main(String[] args) throws Exception {
//		Sex sex = new Sex();
//		sex.main();
//	}

	public void main()  {

		String url = NOVEL_LIST_URL + defualtType;
		log("合成的url = " + url);
		
		// 获得每页的内容
		int sumNum = getSeletecPageCount(url);
		log("页面总数：" + sumNum);
		
		//遍历得到每页的信息
	    for (int n = 1; n < sumNum; n++) {
	    	 log(url + "/" + n + ".htm");
	    	 ArrayList<NovelInfo> novelInfos = selectNovelInfo(url + "/" + n + ".htm");
	    	 //下载内容
	    	 for(int i = 0; i < novelInfos.size(); i++){
	    		 saveNove(novelInfos.get(i));
	    		 return;
	    	 }
	    	
            return;
        }
	}

	@Override
	public int getSeletecPageCount(String url) {
		int num = 0;
		try {
			// 连接主页，获取html，开始进行解析  如何设置超时时间
			Document doc = Jsoup.connect(url).get();
			//获得页面开始标示
			Elements nodes = doc.getElementsByClass("pagination");
			String temp = nodes.get(0).select("a[href]").last().attr("href");
			log(temp.replaceFirst(".htm", ""));
			num = Integer.parseInt(temp.replaceFirst(".htm", ""));
		} catch (IOException e) {  
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return num;
	}

	
	
	@Override
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
					log("name:" + novelInfo.name + ",url:" + novelInfo.url + ",time:" + novelInfo.time);
					
					list.add(novelInfo);
				}
				
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

	@Override
	public String getNoveUrl(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveNove(NovelInfo novelInfo) {
		try {
			
			Document doc = Jsoup.connect(novelInfo.url).get();
			Elements elements =	doc.getElementsByClass("content");
			log(elements.get(0).html().replaceAll("<br />", "").replaceAll("\n\n", "\n"));
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String saveNove2(String url) {
		try {
			
			Document doc = Jsoup.connect(url).get();
			Elements elements =	doc.getElementsByClass("content");
			//log(elements.get(0).html().replaceAll("<br />", "").replaceAll("\n\n", "\n"));
			String text = elements.get(0).html().replaceAll("<br />", "").replaceAll("\n\n", "\n");
			return text;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	/**
	 * 打印
	 */
	public void log(String text) {
		Log.e("chenli", text);
	}
	
	
	public void setUrl(Context context, String url){
		BASE_URL = url;
		NOVEL_LIST_URL = BASE_URL + "/htm/novellist";
		//写入文件
		SharedPreferences sharedPreferences = context.getSharedPreferences("url", Context.MODE_PRIVATE); //私有数据
		Editor editor = sharedPreferences.edit();//获取编辑器
		editor.putString("url", url);
		editor.commit();//提交修改
	}
	
	public String getUrl(Context context){
		String url = "";
		//读取文件
		SharedPreferences share= context.getSharedPreferences("url", Context.MODE_PRIVATE);
		url=share.getString("str", BASE_URL);
		return url;
	}
}
