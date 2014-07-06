package com.android.szw.libbookreminder;

import java.io.Serializable;

/**
 * TODO<class BookInfo contains all the book info we care, for now, just the book name, borrow time and return time>
 * it implemens Serializable, because we need this when we attach BookInfo in an intent to start the BookInfoActivity
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:09:00
 * @version:  V1.0
 */
public class BookInfo implements Serializable{

	private static final long serialVersionUID = 6364852893615774286L;
	private String name;
	private String borrowTime;
	private String returnTime;
	public BookInfo(){}
	public BookInfo(String name, String borrowTime, String returnTime) {
		this.name = name;
		this.borrowTime = borrowTime;
		this.returnTime = returnTime;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName(){
		return name;
	}
	public String getBorrowTime() {
		return borrowTime;
	}

	public void setBorrowTime(String borrowTime) {
		this.borrowTime = borrowTime;
	}

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}
	
}
