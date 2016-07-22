package com.lwy.myselect.test;

import java.util.ArrayList;
import java.util.List;

import com.lwy.myselect.mapper.GetClass;

//test unit code
public class TestCode {

	public static void main(String[] args) {
		List<String> entityPackageList = new ArrayList<>();
		entityPackageList.add("com/lwy/myselect/entity");
		List<String> entityPathList = new ArrayList<>();
		for(String str:entityPackageList){ //get all class name
			GetClass gc = new GetClass();
			entityPathList.addAll(gc.getClass(System.getProperty("user.dir") + "/src/" + str));
		}
		for(String s:entityPathList)
			System.out.println(s);
	}

}
