package com.apusapps.livewallpaper.object;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * author: lijun
 * date:2018/06/25
 */
public class Background extends BaseTarget {

    public Background(int textureId, float bitmapAspectRatio) {
        this.mTextureId = textureId;
        this.mBgBitmapAspectRatio = this.mBitmapAspectRatio = bitmapAspectRatio;
        initVertexData();
    }

    @Override
    public void onSurfaceChanged(float screenAspectRatio) {
        super.onSurfaceChanged(screenAspectRatio);
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 1f,
                0f, 0f, 0f, 0, 1, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    @Override
    public void drawSelf() {
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexcoordHandle, 2, GLES20.GL_SHORT, false, 0, mTexcoordBuffer);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mTexUniformHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexRectangle.length / 3);
    }
}
