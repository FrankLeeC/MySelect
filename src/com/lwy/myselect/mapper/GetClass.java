package com.lwy.myselect.mapper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static jdk.nashorn.internal.objects.NativeArray.lastIndexOf;

//通过包名获取包下所有的类名 
public class GetClass {

	private List<String> list = new ArrayList<>();
	public List<String> getClass(String packageName){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(packageName);
		String path = null;
		if(url != null){
			path = url.getPath();
			path = decode(path);
		}
		loop(path);
		for(int i=0;i<list.size();i++){
			String p = list.get(i);
			int index = p.lastIndexOf(packageName);
			String name = p.substring(index).replaceAll("/",".");
			list.set(i,name);
		}
		return list;
	}

	/**
	 * 特殊符号 十六进制值
	 * + 转义符为 %2B                      % 转义符为 %25
	 * 空格 转义符为 + 或 %20                # 转义符为 %23
	 * / 转义符为 %2F                          & 转义符为 %26
	 * ? 转义符为 %3F                     = 转义符为 %3D
	 * @param str
     */
	private String decode(String str){
		str = str.replaceAll("%2B","+").replaceAll("%20"," ").replaceAll("%2F","/")
				.replaceAll("%3F","?").replaceAll("%23","#").replaceAll("%26","&")
				.replaceAll("%3D","=").replaceAll("%25","%");
		return str;
	}

	private void loop(String path){
		File file = new File(path);
		File[] files = file.listFiles();
		for(File f:files){
			if(f.isDirectory()){
				loop(path+File.separator+f.getName());
			}
			else{
				if(f.getPath().endsWith(".class")){
					String p = path + "." + f.getName().substring(0,f.getName().length()-6);
					list.add(p);
				}
			}
		}
	}
}
