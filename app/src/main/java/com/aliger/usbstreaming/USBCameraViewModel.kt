package com.aliger.usbstreaming

import android.view.SurfaceView
import android.view.TextureView
import androidx.lifecycle.ViewModel
import com.pedro.encoder.input.sources.video.VideoSource
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class USBCameraViewModel(private val streamingController: StreamingController) : ViewModel(){

    fun startVideoStreaming(
        textureView: TextureView
    ) {
        streamingController.setUpServer()
        streamingController.startStream()
        startPreview(textureView)
    }

    fun startVideoStreaming(surfaceView: SurfaceView) {
        streamingController.setUpServer()
        streamingController.startStream()
        startPreview(surfaceView)
    }

//    fun onVideoFrame(frame: ByteArray, size: Int, isIdr: Boolean, timestamp: Long){
//        streamingController.onVideoFrame(frame,size,isIdr,timestamp)
//    }

    fun stopVideoStreaming(){
        streamingController.stopStream()
        streamingController.stopPreview()
        streamingController.release()
    }

    fun changeVideoSource(newSource: VideoSource){
        streamingController.changeVideoSource(newSource)
    }

    fun startPreview(textureView: TextureView){
        streamingController.startPreview(textureView)
    }

    fun startPreview(surfaceView: SurfaceView){
        streamingController.startPreview(surfaceView)
    }




}