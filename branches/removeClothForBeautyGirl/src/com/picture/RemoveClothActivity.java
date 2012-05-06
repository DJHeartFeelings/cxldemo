package com.picture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.cxl.removeClothForBeautyGirl.R;
import com.waps.AppConnect;
import com.waps.UpdatePointsNotifier;

public class RemoveClothActivity extends Activity implements UpdatePointsNotifier {

	private int SCREEN_W;

	private int SCREEN_H;

	private int imagePosition;

	private MediaPlayer mp3Player;

	//先不发声音吧。呵呵
	private static boolean isPlayMusic = false;

	Handler handler = new Handler();

	private void initRequrePointPreference() {
		hasEnoughReadPointPreferenceValue = PreferenceUtil.getHasEnoughReadPoint(RemoveClothActivity.this);
	}

	private void showGetPointDialog(String type) {
		new AlertDialog.Builder(RemoveClothActivity.this).setIcon(R.drawable.happy2)
				.setTitle("当前积分：" + currentPointTotal).setMessage("只要积分满足" + requireReadPoint + "，就可以" + type)
				.setPositiveButton("免费获得积分", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						// 显示推荐安装程序（Offer）.
						AppConnect.getInstance(RemoveClothActivity.this).showOffers(RemoveClothActivity.this);
					}
				}).show();
	}

	public static boolean hasEnoughReadPointPreferenceValue = false;
	public static final int requireReadPoint = 50;

	public static int currentPointTotal = 0;

	protected void onResume() {
		if (!hasEnoughReadPointPreferenceValue) {
			AppConnect.getInstance(this).getPoints(this);
		}
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
		if (pointTotal >= requireReadPoint) {
			hasEnoughReadPointPreferenceValue = true;
			PreferenceUtil.setHasEnoughReadPoint(RemoveClothActivity.this, true);
		}
	}

	/**
	 * AppConnect.getPoints() 方法的实现，必须实现
	 * 
	 * @param error
	 *            请求失败的错误信息
	 */

	public void getUpdatePointsFailed(String error) {
		hasEnoughReadPointPreferenceValue = false;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		Intent intent = getIntent();
		imagePosition = intent.getIntExtra("imagePosition", 0);

		initRequrePointPreference();
		if (!hasEnoughReadPointPreferenceValue && imagePosition > 6) {
			handler.post(new Runnable() {
				public void run() {
					showGetPointDialog("解锁本图片");

				}
			});
		} else {
			initMP3Player();
			setContentView(new MyView(this));

		}
	}

	private void initMP3Player() {
		mp3Player = MediaPlayer.create(RemoveClothActivity.this, R.raw.higirl);
		mp3Player.setLooping(false);
	}

	private void playMusic() {
		mp3Player.start();
	}

	class MyView extends View {
		private Bitmap mBitmap;
		private Canvas mCanvas;
		private Paint mPaint;
		private Path mPath;
		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		public MyView(Context context) {
			super(context);
			setFocusable(true);
			setScreenWH();
			setBackGround();

			// 1.if icon1 is a image,you can open MENU_ITEM_COMMENT bellow
			// Bitmap bm = createBitmapFromSRC();
			// if you want to set icon1 image's alpha,you can open
			// MENU_ITEM_COMMENT bellow
			// bm = setBitmapAlpha(bm, 100);
			// if you want to scale icon1 image,you can open MENU_ITEM_COMMENT
			// bellow
			// bm = scaleBitmapFillScreen(bm);

			// 2.if icon1 is color
			// Bitmap bm = createBitmapFromARGB(0x8800ff00, SCREEN_W, SCREEN_H);
			int drawableId = 0;
			try {
				drawableId = R.drawable.class.getDeclaredField("pre" + imagePosition).getInt(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			Bitmap bm = scaleBitmapFillScreen(BitmapFactory.decodeResource(getResources(), drawableId));
			seticon1Bitmap(bm);

		}

		private void setScreenWH() {
			// get screen info
			DisplayMetrics dm = new DisplayMetrics();
			dm = this.getResources().getDisplayMetrics();
			// get screen width
			int screenWidth = dm.widthPixels;
			// get screen height
			int screenHeight = dm.heightPixels;

			SCREEN_W = screenWidth;
			SCREEN_H = screenHeight;
		}

		private Bitmap createBitmapFromSRC() {
			return BitmapFactory.decodeResource(getResources(), R.drawable.icon1);
		}

		/**
		 * 
		 * @param colorARGB
		 *            should like 0x8800ff00
		 * @param width
		 * @param height
		 * @return
		 */
		private Bitmap createBitmapFromARGB(int colorARGB, int width, int height) {
			int[] argb = new int[width * height];

			for (int i = 0; i < argb.length; i++) {

				argb[i] = colorARGB;

			}
			return Bitmap.createBitmap(argb, width, height, Config.ARGB_8888);
		}

		/**
		 * 
		 * @param bm
		 * @param alpha
		 *            ,and alpha should be like ox00000000-oxff000000
		 * @note set bitmap's alpha
		 * @return
		 */
		/*
		 * private Bitmap setBitmapAlpha(Bitmap bm, int alpha) { int[] argb =
		 * new int[bm.getWidth() * bm.getHeight()]; bm.getPixels(argb, 0,
		 * bm.getWidth(), 0, 0, bm.getWidth(), bm .getHeight());
		 * 
		 * 
		 * for (int i = 0; i < argb.length; i++) {
		 * 
		 * argb[i] = ((alpha) | (argb[i] & 0x00FFFFFF)); } return
		 * Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(),
		 * Config.ARGB_8888); }
		 */

		/**
		 * 
		 * @param bm
		 * @param alpha
		 *            ,alpha should be between 0 and 255
		 * @note set bitmap's alpha
		 * @return
		 */
		private Bitmap setBitmapAlpha(Bitmap bm, int alpha) {
			int[] argb = new int[bm.getWidth() * bm.getHeight()];
			bm.getPixels(argb, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

			for (int i = 0; i < argb.length; i++) {

				argb[i] = ((alpha << 24) | (argb[i] & 0x00FFFFFF));
			}
			return Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
		}

		/**
		 * 
		 * @param bm
		 * @note if bitmap is smaller than screen, you can scale it fill the
		 *       screen.
		 * @return
		 */
		private Bitmap scaleBitmapFillScreen(Bitmap bm) {
			return Bitmap.createScaledBitmap(bm, SCREEN_W, SCREEN_H, true);
		}

		private void setBackGround() {
			int drawableId = 0;
			try {
				drawableId = R.drawable.class.getDeclaredField("after" + imagePosition).getInt(this);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			setBackgroundResource(drawableId);
		}

		/**
		 * 
		 * @param bm
		 * @note set icon1 bitmap , which overlay on background.
		 */
		private void seticon1Bitmap(Bitmap bm) {
			// setting paint
			mPaint = new Paint();
			mPaint.setAlpha(0);
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
			mPaint.setAntiAlias(true);

			mPaint.setDither(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(20);

			// set path
			mPath = new Path();
			;

			// converting bitmap into mutable bitmap
			mBitmap = Bitmap.createBitmap(SCREEN_W, SCREEN_H, Config.ARGB_8888);
			mCanvas = new Canvas();
			mCanvas.setBitmap(mBitmap);
			// drawXY will result on that Bitmap
			// be sure parameter is bm, not mBitmap
			mCanvas.drawBitmap(bm, 0, 0, null);
		}

		protected void onDraw(Canvas canvas) {
			canvas.drawBitmap(mBitmap, 0, 0, null);
			mCanvas.drawPath(mPath, mPaint);
			super.onDraw(canvas);
		}

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			mPath.reset();
		}

		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				if (isPlayMusic) {
					playMusic();
				}
				break;
			}
			return true;
		}
	}
}