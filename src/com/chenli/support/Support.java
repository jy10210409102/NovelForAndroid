/**
 * 
 */
package com.chenli.support;

import java.util.ArrayList;

import com.chenli.dao.NovelInfo;

/**
 * ClassName:ReadNovel <br/>       
 * date: 2014年5月26日上午12:00:29 <br/>     
 * @author ChenLi            
 */
public interface Support {
	
	/**
	 * 获得搜索页数
	 */
	int getSeletecPageCount(String url);
	
	/**
	 * 得到小说名称 章节和 来源
	 */
	ArrayList<NovelInfo> selectNovelInfo(String str);
	
	/**
	 * 获得小说目录url
	 */
	String getNoveUrl(String str);
	
	/**
	 * 存下所有章节地址
	 */
	boolean saveNove(NovelInfo novelInfo);
	
}
