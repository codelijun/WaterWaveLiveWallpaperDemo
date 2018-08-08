package com.apusapps.livewallpaper.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.apusapps.livewallpaper.BuildConfig;

/**
 * author: lijun
 * date:2018/06/25
 */
public class LiveWallpaperPreview extends GLSurfaceView {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "LiveWallpaperPreview";
    private CoreRenderer mRenderer;

    public LiveWallpaperPreview(Context context) {
        this(context, null);
    }

    public LiveWallpaperPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (DEBUG) {
            Log.d(TAG, " LiveWallpaperPreview() ");
        }
        init(context);
    }

    private void init(Context context) {
        if (DEBUG) {
            Log.v(TAG, "init()");
        }
        setEGLContextClientVersion(2);
        mRenderer = new CoreRenderer(context);
        setRenderer(mRenderer);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (DEBUG) {
            Log.d(TAG, " onWindowVisibilityChanged() " + "visibility = [" + (visibility == View.VISIBLE) + "]");
        }
        mRenderer.onVisibilityChanged(visibility == View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRenderer.onTouchEvent(0, 0);
        return false;
    }

    public void onScreenSwitchChanged(boolean screenOn) {
        if (DEBUG) {
            Log.d(TAG, " onScreenSwitchChanged() " + "screenOn = [" + screenOn + "]");
        }
        mRenderer.onVisibilityChanged(screenOn);
    }

    public void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, " onDestroy() ");
        }
        mRenderer.onDestroy();
    }
}
