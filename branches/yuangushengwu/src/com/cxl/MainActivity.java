package com.cxl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cxl.yuangushengwu.R;
import com.waps.AppConnect;

public class MainActivity extends Activity {
	private ListView searchListView;
	private EditText txtSearch;
	MatchListAdapter matchListAdapter = new MatchListAdapter();
	private TextWatcher watcher = new MyTextWatcher();
	Bitmap bitmap = null;
	public static List<String> searchList = ListManager.AllList;
	public static List<String> favoriteList = new ArrayList<String>();
	public static final String Separator = "、";

	@Override
	protected void onDestroy() {
		AppConnect.getInstance(this).finalize();
		if (bitmap != null) {
			bitmap.recycle();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		searchList = ListManager.getSearchList(txtSearch.getText().toString());
		matchListAdapter.notifyDataSetChanged();
		super.onResume();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 连接服务器. 应用启动时调用(为了统计准确性，此句必须填写).
		AppConnect.getInstance(this);
		setContentView(R.layout.main);

		searchListView = (ListView) findViewById(R.id.searchList);
		searchListView.setAdapter(matchListAdapter);
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				String selectItem = (String) arg0.getItemAtPosition(pos);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, DetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("selectItem", selectItem);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtSearch.clearFocus();
		txtSearch.addTextChangedListener(this.watcher);
		txtSearch.clearFocus();

	}

	class MyTextWatcher implements TextWatcher {
		public void afterTextChanged(Editable paramEditable) {
		}

		public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
		}

		public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {
			searchList = ListManager.getSearchList(paramCharSequence.toString());
			matchListAdapter.notifyDataSetChanged();
		}
	}

	class MatchListAdapter extends BaseAdapter {
		private MatchListAdapter() {
		}

		public int getCount() {
			return searchList.size();
		}

		public Object getItem(int paramInt) {
			return searchList.get(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		InputStream assetFile = null;

		public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
			if (paramView == null)
				paramView = ((LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.search_list_layout, null);
			TextView localTextView1 = (TextView) paramView.findViewById(R.id.txtSearchEnName);
			final String itemName = getItem(paramInt).toString();
			localTextView1.setText(itemName);

			ImageView itemIcon = (ImageView) paramView.findViewById(R.id.itemIcon);
			AssetManager assets = getAssets();

			try {
				assetFile = assets.open("image/" + itemName.substring(0, itemName.lastIndexOf(Separator)) + ".jpg");
				bitmap = BitMapUtil.adujstSizeByRate(assetFile);
				itemIcon.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (assetFile != null) {
					try {
						assetFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			return paramView;
		}
	}

}