package com.apusapps.livewallpaper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.apusapps.livewallpaper.core.LiveWallpaperPreview;

/**
 * author: lijun
 * date:2018/06/25
 */
public class MainActivity extends Activity {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "MainActivity";
    private LiveWallpaperPreview mLiveWallpaperPreview;
    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                if (DEBUG) {
                    Log.d(TAG, " onReceive() 亮屏");
                }
                mLiveWallpaperPreview.onScreenSwitchChanged(true);
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                if (DEBUG) {
                    Log.d(TAG, " onReceive() 灭屏");
                }
                mLiveWallpaperPreview.onScreenSwitchChanged(false);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initState();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, filter);

        setContentView(R.layout.activity_main);
        mLiveWallpaperPreview = findViewById(R.id.live_wallpaper_preview);
    }

    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveWallpaperPreview.onDestroy();
        unregisterReceiver(mScreenReceiver);
    }

}
