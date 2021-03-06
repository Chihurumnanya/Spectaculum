/*
 * Copyright 2014 Mario Guggenberger <mg@protyposis.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.protyposis.android.spectaculum.gles;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * @author Mario Guggenberger
 */
public class ExternalSurfaceTexture extends Texture implements SurfaceTexture.OnFrameAvailableListener {

    private static final long NANOTIME_SECOND = 1000000000;

    private SurfaceTexture mSurfaceTexture;
    private SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;
    private boolean mFrameAvailable;

    public ExternalSurfaceTexture() {
        super();

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTexture = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTexture);
        GLUtils.checkError("glBindTexture");

        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.checkError("glTexParameter");

        // This surface texture needs to be fed to the media player; through it,
        // the picture data will be written into the texture.
        mSurfaceTexture = new SurfaceTexture(mTexture);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }

    @Override
    public void delete() {
        mSurfaceTexture.release();
        GLES20.glDeleteTextures(1, new int[] { mTexture }, 0);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener l) {
        mOnFrameAvailableListener = l;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        /**
         * Timed rendering through {@link android.media.MediaCodec#releaseOutputBuffer(int, long)}
         * does not work with this texture. When a frame becomes available here, its buffer has
         * already been returned to the MediaCodec and released, which means it is already available
         * for the decoding of a following frame, which means that deferring the rendering here
         * (through a sleep or a delayed handler message) is too late, because it does not throttle
         * the decoding loop.
         * This unfortunately means that timed rendering cannot be used in a GL context, and a local
         * {@link Thread#sleep(long)} has to be used in the decoder loop instead.
         */
        notifyFrameAvailability();
    }

    private void notifyFrameAvailability() {
        mFrameAvailable = true;
        if(mOnFrameAvailableListener != null) {
            mOnFrameAvailableListener.onFrameAvailable(mSurfaceTexture);
        }
    }

    public boolean isTextureUpdateAvailable() {
        return mFrameAvailable;
    }

    public void updateTexture() {
        mFrameAvailable = false;
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mTransformMatrix);
    }
}
