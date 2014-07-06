package com.android.szw.libbookreminder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * TODO<use Regular Expression to get BookInfo from html>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:17:30
 * @version:  V1.0
 */
public class RegrexTool {
    /*
     * decodeUnicode
     * input: String dataStr(NCR format,like &#x12ea;&#xe212;&#xadbe;)
     * output: String (Unicode String)
     */
	public static String decodeUnicode(String dataStr) {
		dataStr = dataStr.replace("&#x", "");
		dataStr = dataStr.replace(";", "");
		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < dataStr.length()-3; i+=4) {
			buffer.append(new Character((char)Integer.parseInt(dataStr.substring(i, i+4), 16)));
		}
		return buffer.toString();
	}
	public static ArrayList<BookInfo> getBookInfo(BufferedReader bufReader) throws IOException {
		ArrayList<BookInfo> bookInfoList = new ArrayList<BookInfo>();
		BookInfo bookInfo = null;
		String lineText = null;
		Pattern namePattern = Pattern.compile("<td class=\"whitetext\" width=\"35%\"><a class=\"blue\" href=\"[^\"]+\">([^<]+)</a>",Pattern.UNICODE_CASE);
		Pattern borrowTimePattern = Pattern.compile("<td class=\"whitetext\" width=\"13%\">([^<]+)</td>");
		Pattern returnTimePattern = Pattern.compile("<td class=\"whitetext\" width=\"13%\"><font color=>([-\\d]+)\\s*</font></td>");
		while ((lineText = bufReader.readLine()) != null) {

			//match book name
			Matcher nameMatcher = namePattern.matcher(lineText);
			if(nameMatcher.find()) {
				bookInfo = new BookInfo();
				// the book name is in NCR format, we need decode it to unicode
				String NCR = decodeUnicode(nameMatcher.group(1));
				bookInfo.setName(NCR);
				
				//match borrow time
				
				lineText = bufReader.readLine();
				Matcher borrowTimeMatcher = borrowTimePattern.matcher(lineText);
				if(borrowTimeMatcher.find()) {
					bookInfo.setBorrowTime(borrowTimeMatcher.group(1));
				}
				
				//match return time
				lineText = bufReader.readLine();
				Matcher returnTimeMatcher = returnTimePattern.matcher(lineText);
				if(returnTimeMatcher.find()) {
					bookInfo.setReturnTime(returnTimeMatcher.group(1));
					
				}
				
				bookInfoList.add(bookInfo);
			}


		}
		
		return bookInfoList;
	}
}
