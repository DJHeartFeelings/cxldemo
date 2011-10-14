/* 
 * Copyright (C) 2009 Roman Masek
 * 
 * This file is part of OpenSudoku.
 * 
 * OpenSudoku is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenSudoku is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenSudoku.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package cz.romario.opensudoku.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.waps.AdView;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

import com.cxl.opensudoku.R;
import cz.romario.opensudoku.db.FolderColumns;
import cz.romario.opensudoku.db.SudokuDatabase;
import cz.romario.opensudoku.game.FolderInfo;
import cz.romario.opensudoku.gui.FolderDetailLoader.FolderDetailCallback;
import cz.romario.opensudoku.utils.AndroidUtils;

/**
 * List of puzzle's folder. This activity also serves as root activity of application.
 * 
 * @author romario
 *
 */
public class FolderListActivity extends ListActivity implements UpdatePointsNotifier{
    
	public static final int MENU_ITEM_ADD = Menu.FIRST;
    public static final int MENU_ITEM_RENAME = Menu.FIRST + 1;
    public static final int MENU_ITEM_DELETE = Menu.FIRST + 2;
    public static final int MENU_ITEM_ABOUT = Menu.FIRST + 3;
    public static final int MENU_ITEM_EXPORT = Menu.FIRST + 4;
    public static final int MENU_ITEM_EXPORT_ALL = Menu.FIRST + 5;
    public static final int MENU_ITEM_IMPORT = Menu.FIRST + 6;
	
	private static final int DIALOG_ABOUT = 0;
    private static final int DIALOG_ADD_FOLDER = 1;
    private static final int DIALOG_RENAME_FOLDER = 2;
    private static final int DIALOG_DELETE_FOLDER = 3;
	
    private static final String TAG = "FolderListActivity";
    
    private Cursor mCursor;
    private SudokuDatabase mDatabase;
    private FolderListViewBinder mFolderListBinder;
    
    // input parameters for dialogs
    private TextView mAddFolderNameInput;
    private TextView mRenameFolderNameInput;
    private long mRenameFolderID; 
    private long mDeleteFolderID;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	

		
		setContentView(R.layout.folder_list);
//		View getMorePuzzles = (View)findViewById(R.id.get_more_puzzles);
		
		// 连接服务器. 应用启动时调用(为了统计准确性，此句必须填写).
		AppConnect.getInstance(this);
		pointsTextView = (TextView) findViewById(R.id.PointsTextView);
		AppConnect.getInstance(FolderListActivity.this).getPoints(
				FolderListActivity.this);
		
//		LinearLayout container =(LinearLayout)findViewById(R.id.AdLinearLayout);
//		new AdView(this,container).DisplayAd(20);//每20秒轮换一次广告；最少为20

//				Button awardBurron = (Button) findViewById(R.id.awardButton);
//				awardBurron.setOnClickListener(new Button.OnClickListener() {
//					public void onClick(View arg0) {
//						// 奖励虚拟货币
//						AppConnect.getInstance(FolderListActivity.this).awardPoints(10, FolderListActivity.this);
//					}
//				});
		
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		// Inform the list we provide context menus for items
        getListView().setOnCreateContextMenuListener(this);
		
//		getMorePuzzles.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/opensudoku-android/wiki/Puzzles"));
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});
		Button offers = (Button) findViewById(R.id.OffersButton);
		offers.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				// 显示推荐安装程序（Offer）.
				AppConnect.getInstance(FolderListActivity.this).showOffers(FolderListActivity.this);
			}
		});
		
		Button owns = (Button) findViewById(R.id.OwnsButton);
		owns.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				// 显示自家应用列表.
				AppConnect.getInstance(FolderListActivity.this).showMore(FolderListActivity.this);
			}
		});
		
		mDatabase = new SudokuDatabase(getApplicationContext());
		mCursor = mDatabase.getFolderList();
		startManagingCursor(mCursor);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.folder_list_item,
				mCursor, new String[] { FolderColumns.NAME, FolderColumns._ID},
				new int[] { R.id.name, R.id.detail});
		mFolderListBinder = new FolderListViewBinder(this);
		adapter.setViewBinder(mFolderListBinder);
		
        setListAdapter(adapter);
        
        // show changelog on first run
//        Changelog changelog = new Changelog(this);
//        changelog.showOnFirstRun();
	}
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	updateList();
    }
    
    @Override
    protected void onDestroy() {
    	AppConnect.getInstance(this).finalize();
    	super.onDestroy();
    	mDatabase.close();
    	mFolderListBinder.destroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putLong("mRenameFolderID", mRenameFolderID);
    	outState.putLong("mDeleteFolderID", mDeleteFolderID);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle state) {
    	super.onRestoreInstanceState(state);
    	
    	mRenameFolderID = state.getLong("mRenameFolderID");
    	mDeleteFolderID = state.getLong("mDeleteFolderID");
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
        // This is our one standard application action -- inserting a
        // new note into the list.
		menu.add(0, MENU_ITEM_ADD, 0, R.string.add_folder)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
		menu.add(0, MENU_ITEM_IMPORT, 0, R.string.import_file)
        .setShortcut('8', 'i')
        .setIcon(android.R.drawable.ic_menu_upload);
        menu.add(0, MENU_ITEM_EXPORT_ALL, 1, R.string.export_all_folders)
        .setShortcut('7', 'e')
        .setIcon(android.R.drawable.ic_menu_share);
        menu.add(0, MENU_ITEM_ABOUT, 2, R.string.about)
        .setShortcut('1', 'h')
        .setIcon(android.R.drawable.ic_menu_info_details);
        

        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, FolderListActivity.class), null, intent, 0, null);

        return true;
		
	}
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(FolderColumns.NAME)));

        menu.add(0, MENU_ITEM_EXPORT, 0, R.string.export_folder);
        menu.add(0, MENU_ITEM_RENAME, 1, R.string.rename_folder);
        menu.add(0, MENU_ITEM_DELETE, 2, R.string.delete_folder);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
    	LayoutInflater factory = LayoutInflater.from(this);
    	
    	switch (id) {
    	case DIALOG_ABOUT:
            final View aboutView = factory.inflate(R.layout.about, null);
            TextView versionLabel = (TextView)aboutView.findViewById(R.id.version_label);
            String versionName = AndroidUtils.getAppVersionName(getApplicationContext());
            versionLabel.setText(getString(R.string.version, versionName));
            return new AlertDialog.Builder(this)
                .setIcon(R.drawable.opensudoku)
                .setTitle(R.string.app_name)
                .setView(aboutView)
                .setPositiveButton("OK", null)
                .create();
    	case DIALOG_ADD_FOLDER:
    		View addFolderView = factory.inflate(R.layout.folder_name, null);
            mAddFolderNameInput = (TextView)addFolderView.findViewById(R.id.name);
            return new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_add)
                .setTitle(R.string.add_folder)
                .setView(addFolderView)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	mDatabase.insertFolder(mAddFolderNameInput.getText().toString().trim(), System.currentTimeMillis());
                    	updateList();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    	case DIALOG_RENAME_FOLDER:
            final View renameFolderView = factory.inflate(R.layout.folder_name, null);
            mRenameFolderNameInput = (TextView)renameFolderView.findViewById(R.id.name);

            return new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_menu_edit)
            .setTitle(R.string.rename_folder_title)
            .setView(renameFolderView)
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	mDatabase.updateFolder(mRenameFolderID, mRenameFolderNameInput.getText().toString().trim());
                	updateList();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .create();
    	case DIALOG_DELETE_FOLDER:
        	return new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_delete)
            .setTitle(R.string.delete_folder_title)
            .setMessage(R.string.delete_folder_confirm)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// TODO: this could take a while, I should show progress dialog
                	mDatabase.deleteFolder(mDeleteFolderID);
                	updateList();
                }
            })
            .setNegativeButton(android.R.string.no, null)
            .create();
    		
    		
    	}
    	
    	return null;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	super.onPrepareDialog(id, dialog);
    	
    	switch (id) {
    	case DIALOG_ADD_FOLDER:
    		break;
    	case DIALOG_RENAME_FOLDER:
    	{
    		FolderInfo folder = mDatabase.getFolderInfo(mRenameFolderID);
    		String folderName = folder != null ? folder.name : "";
    		dialog.setTitle(getString(R.string.rename_folder_title, folderName));
    		mRenameFolderNameInput.setText(folderName);
    		break;
    	}
    	case DIALOG_DELETE_FOLDER:
    	{
    		FolderInfo folder = mDatabase.getFolderInfo(mDeleteFolderID);
    		String folderName = folder != null ? folder.name : "";
    		dialog.setTitle(getString(R.string.delete_folder_title, folderName));
    		break;
    	}
    	}
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        

        switch (item.getItemId()) {
        case MENU_ITEM_EXPORT:
        	Intent intent = new Intent();
        	intent.setClass(this, SudokuExportActivity.class);
        	intent.putExtra(SudokuExportActivity.EXTRA_FOLDER_ID, info.id);
        	startActivity(intent);
        	return true;
        case MENU_ITEM_RENAME:
        	mRenameFolderID = info.id;
        	showDialog(DIALOG_RENAME_FOLDER);
        	return true;
        case MENU_ITEM_DELETE:
        	mDeleteFolderID = info.id;
        	showDialog(DIALOG_DELETE_FOLDER);
        	return true;
        }
        return false;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
        case MENU_ITEM_ADD:
        	showDialog(DIALOG_ADD_FOLDER);
            return true;
        case MENU_ITEM_IMPORT:
        	intent = new Intent();
        	intent.setClass(this, FileListActivity.class);
        	intent.putExtra(FileListActivity.EXTRA_FOLDER_NAME, "/sdcard");
        	startActivity(intent);
        	return true;
        case MENU_ITEM_EXPORT_ALL:
        	intent = new Intent();
        	intent.setClass(this, SudokuExportActivity.class);
        	intent.putExtra(SudokuExportActivity.EXTRA_FOLDER_ID, SudokuExportActivity.ALL_FOLDERS);
        	startActivity(intent);
        	return true;
        case MENU_ITEM_ABOUT:
        	showDialog(DIALOG_ABOUT);
        	return true;
        }
        return super.onOptionsItemSelected(item);
	}
	/**
	 * item的onclick事件
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (id == 1 ) {//easy的不做限制
				Intent i = new Intent(this, SudokuListActivity.class);
				i.putExtra(SudokuListActivity.EXTRA_FOLDER_ID, id);
				startActivity(i);
		} else if (id == 2 && !Has_Medium_Require_Point) {
			// 先获取当前积分
			AppConnect.getInstance(FolderListActivity.this).getPoints(
					FolderListActivity.this);
			if (currentPointTotal < Medium_Require_Point) {
				showMyDialog(Medium_Require_Point);
			} else {
				Intent i = new Intent(this, SudokuListActivity.class);
				i.putExtra(SudokuListActivity.EXTRA_FOLDER_ID, id);
				startActivity(i);
			}
		} else if (id == 3 && !Has_Hard_Require_Point) {
			// 先获取当前积分
			AppConnect.getInstance(FolderListActivity.this).getPoints(
					FolderListActivity.this);
			if (currentPointTotal < Hard_Require_Point) {
				showMyDialog(Hard_Require_Point);
			} else {
				Intent i = new Intent(this, SudokuListActivity.class);
				i.putExtra(SudokuListActivity.EXTRA_FOLDER_ID, id);
				startActivity(i);
			}
		} else {
			Intent i = new Intent(this, SudokuListActivity.class);
			i.putExtra(SudokuListActivity.EXTRA_FOLDER_ID, id);
			startActivity(i);
		}
	}

	private void updateList() {
		mCursor.requery();
	}
	
	private static class FolderListViewBinder implements ViewBinder {
		private Context mContext;
		private FolderDetailLoader mDetailLoader;
		
		
		public FolderListViewBinder(Context context) {
			mContext = context;
			mDetailLoader = new FolderDetailLoader(context);
		}
		
		@Override
		public boolean setViewValue(View view, Cursor c, int columnIndex) {

			switch (view.getId()) {
			case R.id.name:
				((TextView)view).setText(c.getString(columnIndex));
				break;
			case R.id.detail:
				final long folderID = c.getLong(columnIndex);
				final TextView detailView = (TextView)view;
				detailView.setText(mContext.getString(R.string.loading));
				mDetailLoader.loadDetailAsync(folderID, new FolderDetailCallback() {
					@Override
					public void onLoaded(FolderInfo folderInfo) {
						if (folderInfo != null)
							detailView.setText(folderInfo.getDetail(mContext));
					}
				});
			}
			
			return true;
		}
		
		public void destroy() {
			mDetailLoader.destroy();
		}
	}
	TextView pointsTextView;
	String displayText;
	boolean update_text = false;
	int currentPointTotal = 0;//当前积分
	public static final int Easy_Require_Point = 180;
	public static final int Medium_Require_Point = 150;
	public static final int Hard_Require_Point = 200;
	private static boolean Has_Easy_Require_Point = false;//是否达到积分
	private static boolean Has_Medium_Require_Point = false;//是否达到积分
	private static boolean Has_Hard_Require_Point = false;//是否达到积分

	final Handler mHandler = new Handler();
	
	// 创建一个线程
	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			if (pointsTextView != null) {
				if (update_text) {
					pointsTextView.setText(displayText);
					update_text = false;
				}
			}
		}
	};
	
	@Override
	protected void onResume() {
		AppConnect.getInstance(FolderListActivity.this).getPoints(FolderListActivity.this);
		super.onResume();
	}
	/**
	 * AppConnect.getPoints()方法的实现，必须实现
	 *
	 * @param currencyName
	 *            虚拟货币名称.
	 * @param pointTotal
	 *            虚拟货币余额.
	 */
	public void getUpdatePoints(String currencyName, int pointTotal) {

		currentPointTotal = pointTotal;
		if (currentPointTotal >= Easy_Require_Point) {
			Has_Easy_Require_Point = true;
		}
		if (currentPointTotal >= Medium_Require_Point) {
			Has_Medium_Require_Point = true;
		}
		if (currentPointTotal >= Hard_Require_Point) {
			Has_Hard_Require_Point = true;
		}
		update_text = true;
		displayText = currencyName + ": " + pointTotal;
		mHandler.post(mUpdateResults);
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 *
	 * @param error
	 *            请求失败的错误信息
	 */

	public void getUpdatePointsFailed(String error) {
		currentPointTotal = 0;
		update_text = true;
		displayText = error;
		mHandler.post(mUpdateResults);
	}
	private void showMyDialog(int requirePoint) {
		String setting = requirePoint==Medium_Require_Point?"普通":"困难";
		new AlertDialog.Builder(FolderListActivity.this)
				.setIcon(R.drawable.happy2)
				.setTitle("当前积分：" + currentPointTotal)
				.setMessage(
						"【温馨提示:】只要积分满足" + requirePoint + "，【"+setting+"】关卡就可以永久使用！！ 您当前的积分不足" + requirePoint
								+ "，无法使用【"+setting+"】关卡。\n【免费获得积分方法:】请点击确认键进入推荐下载列表 , 下载并安装软件获得相应积分。")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// 显示推荐安装程序（Offer）.
						AppConnect.getInstance(FolderListActivity.this).showOffers(FolderListActivity.this);
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						//						finish();
					}
				}).show();
	}

	
}
