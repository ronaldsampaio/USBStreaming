package com.aliger.usbstreaming

import android.view.SurfaceView
import android.view.TextureView
import androidx.lifecycle.ViewModel
import com.jiangdg.ausbc.CameraClient
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class USBCameraViewModel(private val streamingController: StreamingController) : ViewModel(){

    fun startVideoStreaming(
        cameraClient: CameraClient,
        textureView: TextureView
    ) {
        streamingController.setUpServer(cameraClient)
        streamingController.startStream()
        startPreview(textureView)
    }

    fun startVideoStreaming(cameraClient: CameraClient, surfaceView: SurfaceView) {
        streamingController.setUpServer(cameraClient)
        streamingController.startStream()
        startPreview(surfaceView)
    }

//    fun onVideoFrame(frame: ByteArray, size: Int, isIdr: Boolean, timestamp: Long){
//        streamingController.onVideoFrame(frame,size,isIdr,timestamp)
//    }

    fun stopVideoStreaming(){
        streamingController.stopStream()
    }

    fun changeVideoSource(usbCameraSource: USBCameraSource){
        streamingController.changeVideoSource(usbCameraSource)
    }

    fun startPreview(textureView: TextureView){
        streamingController.startPreview(textureView)
    }

    fun startPreview(surfaceView: SurfaceView){
        streamingController.startPreview(surfaceView)
    }




}