package com.cxl;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cxl.duanpianjoke.R;
import com.waps.AppConnect;

public class MainActivity extends Activity {
	ListView menuList;
	ArrayAdapter<KeyValue> menuAdapter;
	public static final ArrayList<KeyValue> MENU_List = new ArrayList<KeyValue>();
	static {
		for(int i=1;i<=40;i++){
		MENU_List.add(new KeyValue(String.valueOf(i), "第"+i+"页"));
		}
		
	}

	@Override
	protected void onDestroy() {
		AppConnect.getInstance(this).finalize();
		super.onDestroy();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		AppConnect.getInstance(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		menuList = (ListView) findViewById(R.id.menuList);
		menuAdapter = new ArrayAdapter<KeyValue>(this, R.layout.simple_list_layout, R.id.txtListItem, MENU_List);
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				KeyValue menu = (KeyValue) arg0.getItemAtPosition(pos);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("menu", menu.getKey());
				bundle.putBoolean("startByMenu", true);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
		Bundle fromBundle = getIntent().getExtras();
		boolean showMenu = fromBundle==null?false:fromBundle.getBoolean("showMenu");
		
		if ((PreferenceUtil.getScrollY(MainActivity.this) != 0
				|| PreferenceUtil.getTxtIndex(MainActivity.this) != DetailActivity.Start_Page_Index)&&!showMenu) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, DetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("startByMenu", false);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}
	}
}