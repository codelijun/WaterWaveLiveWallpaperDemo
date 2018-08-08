package com.apusapps.livewallpaper.core;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import com.apusapps.livewallpaper.BuildConfig;
import com.apusapps.livewallpaper.object.Background;
import com.apusapps.livewallpaper.object.BaseTarget;
import com.apusapps.livewallpaper.util.ImageUtil;
import com.apusapps.livewallpaper.util.ShaderUtils;
import com.apusapps.livewallpaper.util.TextureUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: lijun
 * date:2018/06/25
 */
public class CoreRenderer implements GLSurfaceView.Renderer {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "CoreRenderer";
    private static final float NS_PER_SECOND = TimeUnit.SECONDS.toNanos(2);

    private Context mContext;

    private int mProgram;
    private int mMatrixHandle;
    private int mPositionHandle;
    private int mTexcoordHandle;
    private int mTexUniformHandle;
    private boolean mSurfaceViewVisible;

    private List<BaseTarget> mDrawTargetList = new ArrayList<>();

    public CoreRenderer(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if (DEBUG) {
            Log.d(TAG, " onSurfaceCreated() ");
        }
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,
                GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        int[] textureId = new int[ImageUtil.bitmapAssetsRes.length];
        float[] bitmapAspectRatio = new float[ImageUtil.bitmapAssetsRes.length];
        TextureUtil.loadTexture(mContext, textureId, bitmapAspectRatio);
        Background background = new Background(textureId[0], bitmapAspectRatio[0]);
        mDrawTargetList.add(background);
        initGL();

        mStartTime = 0;
    }

    public void onVisibilityChanged(boolean visible) {
        if (DEBUG) {
            Log.d(TAG, " onVisibilityChanged() " + "visible = [" + visible + "]  mDrawTargetList size == " + mDrawTargetList.size());
        }
        this.mSurfaceViewVisible = visible;
        for (BaseTarget baseTarget : mDrawTargetList) {
            baseTarget.onVisibilityChanged(visible);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (DEBUG) {
            Log.d(TAG, " onSurfaceChanged() ");
        }
        if (height == 0) {
            height = 1;
        }

        this.width = width;
        this.height = height;

        GLES20.glViewport(0, 0, width, height);
        float screenAspectRatio = (float) width / (float) height;
        for (BaseTarget baseTarget : mDrawTargetList) {
            baseTarget.onSurfaceChanged(screenAspectRatio);
        }
    }

    private int resolutionHandle, rippleOffsetHandle, rippleCenterUvXHandle, rippleCenterUvYHandle;
    private int timeHandle;
    private int width, height;

    private void initGL() {
        mProgram = ShaderUtils.createProgram(mContext.getResources(), "shader/commonShader.vert", "shader/commonShader.frag");

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mTexcoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexcoord");
        mTexUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");

        timeHandle = GLES20.glGetUniformLocation(mProgram, "time");
        resolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        rippleOffsetHandle = GLES20.glGetUniformLocation(mProgram, "rippleOffset");
        rippleCenterUvXHandle = GLES20.glGetUniformLocation(mProgram, "rippleCenterUvX");
        rippleCenterUvYHandle = GLES20.glGetUniformLocation(mProgram, "rippleCenterUvY");


        GLES20.glUseProgram(mProgram);

        for (BaseTarget baseTarget : mDrawTargetList) {
            baseTarget.setMatrixHandle(mMatrixHandle);
            baseTarget.setPositionHandle(mPositionHandle);
            baseTarget.setTexcoordHandle(mTexcoordHandle);
            baseTarget.setTexUniformHandle(mTexUniformHandle);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mSurfaceViewVisible) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glEnableVertexAttribArray(mTexcoordHandle);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        long now = System.nanoTime();
        float delta = now / NS_PER_SECOND;

        if (DEBUG) {
            Log.d(TAG, " onDrawFrame() delta== " + delta);
        }

        float offset = 0.03f;

        GLES20.glUniform1f(rippleOffsetHandle, offset);
        GLES20.glUniform1f(rippleCenterUvXHandle, 0f);
        GLES20.glUniform1f(rippleCenterUvYHandle, 0f);

        GLES20.glUniform2f(resolutionHandle, width, height);


        GLES20.glUniform1f(timeHandle, delta);

        for (BaseTarget baseTarget : mDrawTargetList) {
            baseTarget.drawSelf();
        }

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexcoordHandle);
    }

    public void onTouchEvent(float positionX, float positionY){
        if (DEBUG) {
            Log.d(TAG, " onTouchEvent() " + "positionX = [" + positionX + "], positionY = [" + positionY + "]");
        }
        mStartTime = System.currentTimeMillis();
    }

    private long mStartTime;

    public void onDestroy() {
        if (DEBUG) {
            Log.d(TAG, " onDestroy() ");
        }
        GLES20.glDisable(GLES20.GL_BLEND);
        for (BaseTarget baseTarget : mDrawTargetList) {
            baseTarget.destroy();
        }
    }
}
