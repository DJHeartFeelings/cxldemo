/*
 *========================================================================
 * CoinFlip.java
 * Oct 5, 2011 8:32:16 PM | variable
 * Copyright (c) 2011 Richard Banasiak
 *========================================================================
 * This file is part of CoinFlip.
 *
 *    CoinFlip is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    CoinFlip is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with CoinFlip.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.banasiak.coinflip;

import java.util.EnumMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CoinFlip extends Activity
{
    // debugging tag
    private static final String TAG = "CoinFlip";

    // add-on package name
    private static final String EXTPKG = "com.banasiak.coinflipext";

    // version of the settings schema used by this codebase
    private static final int SCHEMA_VERSION = 3;

    // enumerator of all possible transition states
    private enum ResultState
    {
        HEADS_HEADS,
        HEADS_TAILS,
        TAILS_HEADS,
        TAILS_TAILS,
        UNKNOWN
    }

    EnumMap<ResultState, AnimationDrawable> coinAnimationsMap;
    EnumMap<ResultState, Drawable> coinImagesMap;

    private final Coin theCoin = new Coin();
    private ShakeListener shaker;
    private Boolean currentResult = true;
    private Boolean previousResult = true;
    private ImageView coinImage;
    private CustomAnimationDrawable coinAnimationCustom;
    private TextView resultText;
    private TextView instructionsText;
    private SoundPool soundPool;
    private int soundCoin;
    private int soundOneUp;
    private int flipCounter = 0;

    private final Util util = new Util(this);

    /**
     * Called when the user presses the menu button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Called when the user selects an item from the options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        Intent intent;

        switch (item.getItemId())
        {
            case R.id.about_menu:
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
            case R.id.selftest_menu:
                intent = new Intent(this, SelfTest.class);
                startActivity(intent);
                return true;
            case R.id.settings_menu:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                return true;
            case R.id.exit:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume()");

        int force = Settings.getShakePref(this);

        resetCoin();
        resetInstructions(force);

        // determine coin type to draw
        String coinPrefix = Settings.getCoinPref(this);

        if (coinPrefix.equals("default"))
        {
            Log.d (TAG, "Default coin selected");
            loadInternalResources();
        }
        else
        {
            Log.d (TAG, "Add-on coin selected");
            loadExternalResources(coinPrefix);
        }

        if (force == 0)
        {
            shaker.pause();
        }
        else
        {
            shaker.resume(force);
        }

        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        shaker.pause();
        super.onPause();

        // persist state
        Settings.setFlipCount(this, flipCounter);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        // reset settings if external package has been removed
        if (!util.isExtPkgInstalled(EXTPKG))
        {
            Settings.resetCoinPref(this);
        }

        // reset settings if they are from an earlier version.
        // if any setting keys have changed and we don't reset, the app
        // will force close and nasty e-mails soon follow
        if( Settings.getSchemaVersion(this) != SCHEMA_VERSION )
        {
            Settings.resetAllPrefs(this);
            Settings.setSchemaVersion(this, SCHEMA_VERSION);
        }

        // restore state
        flipCounter = Settings.getFlipCount(this);

        setContentView(R.layout.main);

        // initialize the coin image and result text views
        initViews();

        // initialize the sounds
        initSounds();

        // initialize the coin maps
        coinAnimationsMap = new EnumMap<CoinFlip.ResultState, AnimationDrawable>(ResultState.class);
        coinImagesMap = new EnumMap<CoinFlip.ResultState, Drawable>(ResultState.class);

        // initialize the shake listener
        shaker = new ShakeListener(this);
        shaker.setOnShakeListener(new ShakeListener.OnShakeListener()
        {
            public void onShake()
            {
                flipCoin();
            }
        });

        // initialize the onclick listener
        coinImage.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                flipCoin();
            }
        });
    }

    private void flipCoin()
    {
        Log.d(TAG, "flipCoin()");

        flipCounter++;
        Log.d(TAG, "flipCounter=" + flipCounter);

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // we're in the process of flipping the coin
        ResultState resultState = ResultState.UNKNOWN;

        // vibrate if enabled
        if (Settings.getVibratePref(this))
        {
            vibrator.vibrate(100);
        }

        // flip the coin and update the state with the result
        resultState = updateState(theCoin.flip());

        // update the screen with the result of the flip
        renderResult(resultState);

    }

    private void resetCoin()
    {
        Log.d(TAG, "resetCoin()");

        // hide the animation and draw the reset image
        displayCoinAnimation(false);
        displayCoinImage(true);
        coinImage.setImageDrawable(getResources().getDrawable(R.drawable.unknown));
        resultText.setText("");
    }

    private void resetInstructions(int force)
    {
        Log.d(TAG, "resetInstructions()");

        if (force == 0)
        {
            instructionsText.setText(R.string.instructions_tap_tv);
        }
        else
        {
            instructionsText.setText(R.string.instructions_tap_shake_tv);
        }
    }

    private ResultState updateState(boolean flipResult)
    {
        // Analyze the current coin state and the new coin state and determine
        // the proper transition between the two.
        // true = HEADS | false = TAILS

        Log.d(TAG, "updateState()");

        ResultState resultState = ResultState.UNKNOWN;
        currentResult = flipResult;

        // this is easier to read than the old code
        if (previousResult == true && currentResult == true)
        {
            resultState = ResultState.HEADS_HEADS;
        }
        if (previousResult == true && currentResult == false)
        {
            resultState = ResultState.HEADS_TAILS;
        }
        if (previousResult == false && currentResult == true)
        {
            resultState = ResultState.TAILS_HEADS;
        }
        if (previousResult == false && currentResult == false)
        {
            resultState = ResultState.TAILS_TAILS;
        }

        // update the previousResult for the next flip
        previousResult = currentResult;

        return resultState;
    }

    private BitmapDrawable resizeBitmapDrawable(BitmapDrawable image, int width, int height)
    {
        // load the transparent background and convert to a bitmap
        BitmapDrawable background = (BitmapDrawable) getResources().getDrawable(R.drawable.background);
        Bitmap background_bm = background.getBitmap();

        // convert the passed in image to a bitmap and resize according to parameters
        Bitmap image_bm = Bitmap.createScaledBitmap(image.getBitmap(), width, height, true);

        // create a new canvas to combine the two images on
        Bitmap comboImage_bm = Bitmap.createBitmap(background_bm.getWidth(), background_bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(comboImage_bm);

        // add the background as well as the new image to the horizontal center of the image
        comboImage.drawBitmap(background_bm, 0f, 0f, null);
        comboImage.drawBitmap(image_bm, (background_bm.getWidth()-image_bm.getWidth())/2, 0f, null);

        // convert the new combo image bitmap to a BitmapDrawable
        BitmapDrawable comboImage_bmd = new BitmapDrawable(comboImage_bm);

        return comboImage_bmd;
    }

    private AnimationDrawable generateAnimatedDrawable(final Drawable imageA, final Drawable imageB, final Drawable edge, ResultState resultState)
    {
        AnimationDrawable animation = new AnimationDrawable();
        int widthA = ((BitmapDrawable)imageA).getBitmap().getWidth();
        int heightA = ((BitmapDrawable)imageA).getBitmap().getHeight();
        int widthB = ((BitmapDrawable)imageB).getBitmap().getWidth();
        int heightB = ((BitmapDrawable)imageB).getBitmap().getHeight();

        // create the individual animation frames for the heads side
        BitmapDrawable imageA_8 = (BitmapDrawable) imageA;
        BitmapDrawable imageA_6 = resizeBitmapDrawable(imageA_8, (int)(widthA*0.75), heightA);
        BitmapDrawable imageA_4 = resizeBitmapDrawable(imageA_8, (int)(widthA*0.50), heightA);
        BitmapDrawable imageA_2 = resizeBitmapDrawable(imageA_8, (int)(widthA*0.25), heightA);

        // create the individual animation frames for the tails side
        BitmapDrawable imageB_8 = (BitmapDrawable) imageB;
        BitmapDrawable imageB_6 = resizeBitmapDrawable(imageB_8, (int)(widthB*0.75), heightB);
        BitmapDrawable imageB_4 = resizeBitmapDrawable(imageB_8, (int)(widthB*0.50), heightB);
        BitmapDrawable imageB_2 = resizeBitmapDrawable(imageB_8, (int)(widthB*0.25), heightB);

        // create the appropriate animation depending on the result state
        switch (resultState)
        {
            case HEADS_HEADS:
                // Begin Flip 1
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                // Begin Flip 2
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                // Begin Flip 3
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                break;
            case HEADS_TAILS:
                // Begin Flip 1
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                // Begin Flip 2
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                // Begin Flip 3 (half flip)
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                break;
            case TAILS_HEADS:
                // Begin Flip 1
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                // Begin Flip 2
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                // Begin Flip 3 (half flip)
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                break;
            case TAILS_TAILS:
                // Begin Flip 1
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                // Begin Flip 2
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                // Begin Flip 3
                animation.addFrame(imageB_8, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_8, 20);
                animation.addFrame(imageA_6, 20);
                animation.addFrame(imageA_4, 20);
                animation.addFrame(imageA_2, 20);
                animation.addFrame(edge, 20);
                animation.addFrame(imageB_2, 20);
                animation.addFrame(imageB_4, 20);
                animation.addFrame(imageB_6, 20);
                animation.addFrame(imageB_8, 20);
                break;
            default:
                Log.w(TAG, "Invalid state. Resetting coin.");
                resetCoin();
                break;
        }

        animation.setOneShot(true);

        return animation;
    }

    // load resources internal to the CoinFlip package
    private void loadInternalResources()
    {
        Log.d(TAG, "loadInternalResources()");

        AnimationDrawable coinAnimation;
        ResultState resultState;

        // load the images
        Drawable heads = getResources().getDrawable(R.drawable.heads);
        Drawable tails = getResources().getDrawable(R.drawable.tails);
        Drawable edge = getResources().getDrawable(R.drawable.edge);

        // only do all the CPU-intensive animation rendering if animations are enabled
        if (Settings.getAnimationPref(this))
        {
            // render the animation for each result state and store it in the animations map
            resultState = ResultState.HEADS_HEADS;
            coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
            coinAnimationsMap.put(resultState, coinAnimation);

            resultState = ResultState.HEADS_TAILS;
            coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
            coinAnimationsMap.put(resultState, coinAnimation);

            resultState = ResultState.TAILS_HEADS;
            coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
            coinAnimationsMap.put(resultState, coinAnimation);

            resultState = ResultState.TAILS_TAILS;
            coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
            coinAnimationsMap.put(resultState, coinAnimation);
        }

        // add the appropriate image for each result state to the images map
        // WTF?  There's some kind of rendering bug if you use the "heads" or "tails" variables here...
        coinImagesMap.put(ResultState.HEADS_HEADS, getResources().getDrawable(R.drawable.heads));
        coinImagesMap.put(ResultState.HEADS_TAILS, getResources().getDrawable(R.drawable.tails));
        coinImagesMap.put(ResultState.TAILS_HEADS, getResources().getDrawable(R.drawable.heads));
        coinImagesMap.put(ResultState.TAILS_TAILS, getResources().getDrawable(R.drawable.tails));
    }

    // load resources from the external CoinFlipExt package
    private void loadExternalResources(final String coinPrefix)
    {
        Log.d(TAG, "loadExternalResources()");

        AnimationDrawable coinAnimation;
        ResultState resultState;

        try
        {
            Resources extPkgResources = getPackageManager().getResourcesForApplication(EXTPKG);

            // load the image IDs from the add-in package
            int headsId = extPkgResources.getIdentifier(coinPrefix + "_heads", "drawable", EXTPKG);
            int tailsId = extPkgResources.getIdentifier(coinPrefix + "_tails", "drawable", EXTPKG);
            int edgeId = extPkgResources.getIdentifier(coinPrefix + "_edge", "drawable", EXTPKG);

            // load the images from the add-in package via their ID
            Drawable heads = extPkgResources.getDrawable(headsId);
            Drawable tails = extPkgResources.getDrawable(tailsId);
            Drawable edge = extPkgResources.getDrawable(edgeId);

            // only do all the CPU-intensive animation rendering if animations are enabled
            if (Settings.getAnimationPref(this))
            {
                // render the animation for each result state and store it in the animations map
                resultState = ResultState.HEADS_HEADS;
                coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
                coinAnimationsMap.put(resultState, coinAnimation);

                resultState = ResultState.HEADS_TAILS;
                coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
                coinAnimationsMap.put(resultState, coinAnimation);

                resultState = ResultState.TAILS_HEADS;
                coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
                coinAnimationsMap.put(resultState, coinAnimation);

                resultState = ResultState.TAILS_TAILS;
                coinAnimation = generateAnimatedDrawable(heads, tails, edge, resultState);
                coinAnimationsMap.put(resultState, coinAnimation);
            }

            // add the appropriate image for each result state to the images map
            // WTF?  There's (still) some kind of rendering bug if you use the "heads" or "tails" variables here...
            coinImagesMap.put(ResultState.HEADS_HEADS, extPkgResources.getDrawable(headsId));
            coinImagesMap.put(ResultState.HEADS_TAILS, extPkgResources.getDrawable(tailsId));
            coinImagesMap.put(ResultState.TAILS_HEADS, extPkgResources.getDrawable(headsId));
            coinImagesMap.put(ResultState.TAILS_TAILS, extPkgResources.getDrawable(tailsId));

        }
        catch (NameNotFoundException e)
        {
            Log.e(TAG, "NameNotFoundException");
            e.printStackTrace();
        }

    }

    private void renderResult(final ResultState resultState)
    {
        Log.d(TAG, "renderResult()");

        AnimationDrawable coinAnimation;

        // load the appropriate coin animation based on the state
        coinAnimation = coinAnimationsMap.get(resultState);
        coinAnimationCustom = new CustomAnimationDrawable(coinAnimation)
        {
            @Override
            void onAnimationFinish()
            {
                playCoinSound();
                updateResultText(resultState);
            }
        };

        coinImage.setImageDrawable(coinImagesMap.get(resultState));

        // hide the static image and clear the text
        displayCoinImage(false);
        displayCoinAnimation(false);
        resultText.setText("");

        // display the result
        if (Settings.getAnimationPref(this))
        {
            // hide the static image and render the animation
            displayCoinImage(false);
            displayCoinAnimation(true);
            coinImage.setBackgroundDrawable(coinAnimationCustom);
            coinAnimationCustom.start();
            // handled by animation callback
            // playCoinSound();
            // updateResultText(resultState, resultText);
        }
        else
        {
            // hide the animation and display the static image
            displayCoinImage(true);
            displayCoinAnimation(false);
            playCoinSound();
            updateResultText(resultState);
        }
    }

    private void initSounds()
    {
        // MediaPlayer was causing ANR issues on some devices.
        // SoundPool should be more efficient.

        Log.d(TAG, "initSounds()");
        soundPool  = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
        soundCoin = soundPool.load(this, R.raw.coin, 1);
        soundOneUp = soundPool.load(this, R.raw.oneup, 1);

    }

    private void playSound(int sound)
    {
        Log.d(TAG, "playSound()");
        if (Settings.getSoundPref(this))
        {
            AudioManager mgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;

            soundPool.play(sound, volume, volume, 1, 0, 1f);
        }
    }

    private void playCoinSound()
    {
        Log.d(TAG, "playCoinSound()");

        synchronized (this) {
            if (flipCounter < 100)
            {
                playSound(soundCoin);
            }
            else
            {
                //Happy Easter!  (For Ryan)
                //Toast.makeText(this, "1-UP", Toast.LENGTH_SHORT).show();
                playSound(soundOneUp);
                flipCounter = 0;
            }
        }
    }

    private void updateResultText(ResultState resultState)
    {
        Log.d(TAG, "updateResultText()");

        if (Settings.getTextPref(this))
        {
            switch (resultState)
            {
                case HEADS_HEADS:
                case TAILS_HEADS:
                    resultText.setText(R.string.heads);
                    resultText.setTextColor(Color.parseColor("green"));
                    break;
                case HEADS_TAILS:
                case TAILS_TAILS:
                    resultText.setText(R.string.tails);
                    resultText.setTextColor(Color.parseColor("red"));
                    break;
                default:
                    resultText.setText(R.string.unknown);
                    resultText.setTextColor(Color.parseColor("yellow"));
                    break;
            }
        }
        else
        {
            resultText.setText("");
        }
    }
    private void displayCoinAnimation(boolean flag)
    {
        Log.d(TAG, "displayCoinAnimation()");

        // safety first!
        if (coinAnimationCustom != null )
        {
            if (flag)
            {
                coinAnimationCustom.setAlpha(255);
            }
            else
            {
                coinAnimationCustom.setAlpha(0);
            }
        }
    }
    private void displayCoinImage(boolean flag)
    {
        Log.d(TAG, "displayCoinImage()");

        // safety first!
        if (coinImage != null)
        {
            if (flag)
            {
                // get rid of the animation background
                coinImage.setBackgroundDrawable(null);
                coinImage.setAlpha(255);
            }
            else
            {
                coinImage.setAlpha(0);
            }
        }
    }
    private void initViews()
    {
        Log.d(TAG, "initCoinImageView()");
        coinImage = (ImageView) findViewById(R.id.coin_image_view);
        resultText = (TextView) findViewById(R.id.result_text_view);
        instructionsText = (TextView) findViewById(R.id.instructions_text_view);
    }

}