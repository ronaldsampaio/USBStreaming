package com.aliger.usbstreaming

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.jiangdg.ausbc.CameraClient
import com.pedro.library.util.sources.video.VideoSource

class USBCameraSource(
    private val context: Context,
    private val cameraClient: CameraClient,
): VideoSource(), LifecycleOwner {

    private var mWidth = 1280
    private var mHeight = 720
    init {
        Log.d("camera_str", "USBCameraSource INIT!")
    }

    private val lifecycleRegistry = LifecycleRegistry(this)

    override fun create(width: Int, height: Int, fps: Int): Boolean {
        mWidth = width
        mHeight = height
        return try {
            Log.d("camera_str", "defining Render size to $width Width and $height height")
            cameraClient.setRenderSize(width,height)
            created = true
            true
        } catch (e : Exception){
            Log.d("camera_str","EXCEPTION ON CREATE USBCAMERASOURCE -> $e")
            false
        }

    }

    override fun start(surfaceTexture: SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        cameraClient.openCamera(surfaceTexture,mWidth,mHeight)
    }

    override fun stop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        cameraClient.let {
            it.closeCamera()
            surfaceTexture = null
        }
    }

    override fun release() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun isRunning(): Boolean {
        return cameraClient.isCameraOpened()!!
    }


    override val lifecycle: Lifecycle
        get() = lifecycleRegistry


}