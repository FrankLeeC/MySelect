package com.lwy.myselect.mapper;

import java.io.File;
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

	private void loop(String path){
		File file = new File(path);
		File[] files = file.listFiles();
		for(File f:files){
			if(f.isDirectory()){
				loop(path+File.separator+f.getName());
			}
			else{
				if(f.getPath().endsWith(".class")){
					String p = path + f.getName().substring(0,f.getName().length()-6);
					list.add(p);
				}
			}
		}
	}
}
