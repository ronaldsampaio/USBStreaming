package com.aliger.usbstreaming.usb

import android.content.Context
import android.graphics.SurfaceTexture
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.pedro.library.util.sources.video.VideoSource

/**
 * The context provided to this class must be provided by a view because that context contain LifecycleOwner.
 * Usb library need a context that contain LifecycleOwner to work.
 */
class CameraUsbSource(private val context: Context): VideoSource() {

  private var cameraClient: CameraClient? = null

  override fun create(width: Int, height: Int, fps: Int): Boolean {
    this.width = width
    this.height = height
    this.fps = fps
    created = true
    return true
  }

  override fun start(surfaceTexture: SurfaceTexture) {
    this.surfaceTexture = surfaceTexture
    cameraClient = CameraClient.newBuilder(context).apply {
      setCameraStrategy(CameraUvcStrategy(context))
      setCameraRequest(
        CameraRequest.Builder()
          .setFrontCamera(false)
          .setPreviewWidth(width)
          .setPreviewHeight(height)
          .create()
      )
      openDebug(true)

    }.build()
    cameraClient?.openCamera(this.surfaceTexture!!, width, height)
  }

  override fun stop() {
    cameraClient?.closeCamera()
    cameraClient = null
    this.surfaceTexture = null
  }

  override fun release() {
    this.surfaceTexture = null
    cameraClient = null
  }

  override fun isRunning(): Boolean {
    return cameraClient?.isCameraOpened() ?: false
  }

  fun captureImage(callback: (String) -> Unit) {
    cameraClient?.captureImage(object : ICaptureCallBack {
      override fun onBegin() {
        callback("begin")
      }

      override fun onError(error: String?) {
        callback("error: $error")
      }

      override fun onComplete(path: String?) {
        callback("complete: $path")
      }
    })
  }
}