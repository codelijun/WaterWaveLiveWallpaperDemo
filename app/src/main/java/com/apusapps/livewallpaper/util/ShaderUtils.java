/*
 *
 * ShaderUtils.java
 *
 * Created by Wuwang on 2016/10/8
 */
package com.apusapps.livewallpaper.util;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import com.apusapps.livewallpaper.BuildConfig;

import java.io.InputStream;

/**
 * author: lijun
 * date:2018/06/25
 */
public class ShaderUtils {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "ShaderUtils";

    public static void checkGLError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                if (DEBUG) {
                    Log.e(TAG, "loadShader() Could not compile shader:" + shaderType);
                    Log.e(TAG, "loadShader() GLES20 Error:" + GLES20.glGetShaderInfoLog(shader));
                }
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int loadShader(Resources res, int shaderType, String resName) {
        return loadShader(shaderType, loadFromAssetsFile(resName, res));
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertex == 0) return 0;
        int fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragment == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertex);
            checkGLError("createProgram() Attach Vertex Shader");
            GLES20.glAttachShader(program, fragment);
            checkGLError("createProgram() Attach Fragment Shader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                if (DEBUG) {
                    Log.e(TAG, "createProgram() Could not link program:" + GLES20.glGetProgramInfoLog(program));
                }
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static int createProgram(Resources res, String vertexRes, String fragmentRes) {
        return createProgram(loadFromAssetsFile(vertexRes, res), loadFromAssetsFile(fragmentRes, res));
    }

    public static String loadFromAssetsFile(String fileName, Resources res) {
        StringBuilder result = new StringBuilder();
        try {
            InputStream is = res.getAssets().open(fileName);
            int ch;
            byte[] buffer = new byte[1024];
            while (-1 != (ch = is.read(buffer))) {
                result.append(new String(buffer, 0, ch));
            }
            is.close();
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, " loadFromAssetsFile() Exception: " + Log.getStackTraceString(e));
            }
            return null;
        }
        return result.toString().replaceAll("\\r\\n", "\n");
    }
}
