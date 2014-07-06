package com.android.szw.libbookreminder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * TODO<the Activity to show the information of borrowed books>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:27:28
 * @version:  V1.0
 */
public class BookInfoActivity extends Activity {
	private ListView listView = null;
	private TextView borrowedBookCountsTextView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookinfo_activity_layout);
		listView = (ListView)findViewById(R.id.listView);
		borrowedBookCountsTextView = (TextView)findViewById(R.id.textViewBookCount);
		ArrayList<BookInfo> bookinfoList = (ArrayList<BookInfo>)getIntent().getSerializableExtra("booklist");
		borrowedBookCountsTextView.setText(String.valueOf(bookinfoList.size()));
		
		List<Map<String, Object>> bookList = covert(bookinfoList);
		String[] from = new String[]{"name", "borrowtime", "returntime"};
		int[] to = new int[]{R.id.textViewName, R.id.textViewBorrowTime, R.id.textViewReturnTime};
		MySimpleAdapter adapter = new MySimpleAdapter(this, bookList, R.layout.book_item_layout, from, to);
		listView.setAdapter(adapter);
	}
	List<Map<String, Object>> covert(ArrayList<BookInfo> booklist){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < booklist.size(); i++) {
			Map<String,Object> item = new HashMap<String,Object>();
			item.put("name", booklist.get(i).getName());
			item.put("borrowtime", booklist.get(i).getBorrowTime());
			item.put("returntime", booklist.get(i).getReturnTime());
			list.add(item);
		}
		return list;
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_cancellation:
				AccountManager.getInstance(this).clearAccount();
				this.finish();
				System.exit(0);
			return true;
		case R.id.action_exit:
			this.finish();
			System.exit(0);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	/**
	 * TODO<used to set different books with different colors>
	 *  3 kinds of colors for 3 status of books (has passed time, will pass time, a long time to pass)
	 * @author  SunZongwen
	 * @data:  2014-7-6 ÏÂÎç10:29:16
	 * @version:  V1.0
	 */
	private class MySimpleAdapter extends SimpleAdapter{
		private List<Map<String, Object>> booklist = null;
		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			booklist = (List<Map<String, Object>>) data;
			// TODO Auto-generated constructor stub
		}
		

		public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            int day = MyDate.dayBetween(booklist.get(position).get("returntime").toString(),MyDate.getDate());
            if(day < 0){
            	TextView textView = (TextView)view.findViewById(R.id.textViewName);
            	textView.setBackgroundResource(R.color.red);
            	
            }
            else if(day < AccountManager.getInstance(BookInfoActivity.this).getPreRemindDay()){
            	TextView textView = (TextView)view.findViewById(R.id.textViewName);
            	textView.setBackgroundResource(R.color.yellow);
            }
            return view;
		}
		
		
	}

}
