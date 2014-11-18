package com.cbb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDate {
	
	
	String myDate = null;
	Date today;
	public MyDate() {
		//Date today;
		
		SimpleDateFormat formatter;
		String pattern = "MMM. dd, yyyy";

		Locale locale = new Locale("en","US");
		formatter = new SimpleDateFormat(pattern, locale);
		today = new Date();
		myDate = formatter.format(today);
	}
	public  String getDate() {
		return myDate;
	}
	
	public Date getToday() {
		return today;
	}
	public static void main(String[] args){
		Date today;
		String output;
		SimpleDateFormat formatter;
		String pattern = "MMM. dd, yyyy";

		Locale locale = new Locale("en","US");
		formatter = new SimpleDateFormat(pattern, locale);
		today = new Date();
		output = formatter.format(today);
		System.out.println(pattern + " " + output);

	}
}
