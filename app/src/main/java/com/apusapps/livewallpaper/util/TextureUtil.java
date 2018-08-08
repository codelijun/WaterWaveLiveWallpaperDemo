package com.apusapps.livewallpaper.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.apusapps.livewallpaper.BuildConfig;

import java.io.IOException;
import java.io.InputStream;

/**
 * author: lijun
 * date:2018/06/25
 */
public class TextureUtil {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "TextureUtil";

    /**
     * 绑定纹理
     *
     * @param context
     * @param textureId         纹理的id
     * @param bitmapAspectRatio 纹理的高宽比例
     */
    public static void loadTexture(Context context, int[] textureId, float[] bitmapAspectRatio) {
        InputStream is = null;
        int[] texNames = new int[ImageUtil.bitmapAssetsRes.length];
        GLES20.glGenTextures(texNames.length, texNames, 0);
        TextureUtil.checkGlError("glGenTextures");
        for (int i = 0; i < ImageUtil.bitmapAssetsRes.length; i++) {
            if (DEBUG) {
                Log.d(TAG, " loadTexture() " + ImageUtil.bitmapAssetsRes[i]);
            }
            try {
                is = context.getAssets().open(ImageUtil.bitmapAssetsRes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is == null) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            bitmapAspectRatio[i] = (float) height / (float) width;
            textureId[i] = TextureUtil.loadTextures(texNames[i], bitmap);
            try {
                is.close();
            } catch (IOException e) {
                if (DEBUG) {
                    Log.e(TAG, " loadTexture() " + Log.getStackTraceString(e));
                }
            }
            System.gc();
        }
    }

    private static int loadTextures(int texNames, Bitmap bitmap) {
        //http://www.arvrschool.com/read.php?tid=130
        if (texNames != 0) {
            //激活纹理单元，GL_TEXTURE0代表纹理单元0，GL_TEXTURE1代表纹理单元1，以此类推。OpenGL使用纹理单元来表示被绘制的纹理
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //第一个参数代表这是一个2D纹理，第二个参数就是OpenGL要绑定的纹理对象ID，也就是让OpenGL后面的纹理调用都使用此纹理对象
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texNames);
            //设置纹理过滤参数，GL_TEXTURE_MIN_FILTER代表纹理缩写的情况，GL_LINEAR_MIPMAP_LINEAR代表缩小时使用三线性过滤的方式
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            //GL_TEXTURE_MAG_FILTER代表纹理放大，GL_LINEAR代表双线性过滤
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);
            //加载实际纹理图像数据到OpenGL ES的纹理对象中，这个函数是Android封装好的，可以直接加载bitmap格式
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            TextureUtil.checkGlError("texImage2D");
            bitmap.recycle();
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        if (texNames == 0) {
            if (DEBUG) {
                Log.e("TextureUtil", "Error loading texture (empty texture handle)");
            }
            throw new RuntimeException(
                    "Error loading texture (empty texture handle).");
        }
        return texNames;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}
