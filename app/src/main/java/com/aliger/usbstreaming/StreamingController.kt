package com.aliger.usbstreaming

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import android.view.TextureView
import com.jiangdg.ausbc.CameraClient
import com.pedro.common.ConnectChecker
import com.pedro.encoder.utils.gl.AspectRatioMode
import com.pedro.library.util.sources.audio.MicrophoneSource
import com.pedro.rtspserver.ClientListener
import com.pedro.rtspserver.RtspServerStream
import com.pedro.rtspserver.ServerClient
import org.koin.core.annotation.Factory
import java.nio.ByteBuffer

@Factory
class StreamingController(private val context: Context) : ClientListener, ConnectChecker {
    private lateinit var rtspServerStream : RtspServerStream
    private var portNum = 18554
    private var prepared = false


    fun setUpServer(cameraClient: CameraClient) {
        rtspServerStream = RtspServerStream(context,portNum,this,USBCameraSource(context,cameraClient),
            MicrophoneSource()
        )
        rtspServerStream.getGlInterface().setAspectRatioMode(AspectRatioMode.Fill)

    }


    fun startStream(){
        if(rtspServerStream.prepareVideo(1280, 720, 4000000, rotation = 90) && rtspServerStream.prepareAudio(48000,false,128000)){
            rtspServerStream.startStream()
        }
        else{
            Log.d("camera_str","ERROR PREPARING VIDEO OR AUDIO")
        }

        Log.d("Streaming", "STARTED SERVER!")
        //else Log.e("Streaming", "Error preparing audio or video")

    }

    fun changeVideoSource(usbCameraSource: USBCameraSource) {
        //rtspStream.changeVideoSource(usbCameraSource)
        rtspServerStream.changeVideoSource(usbCameraSource)
    }

//    fun onVideoFrame(frame: ByteArray, size: Int, isIdr: Boolean, timestamp: Long) {
//        val h264Buffer = ByteBuffer.wrap(frame)
//        val info = MediaCodec.BufferInfo()
//        info.size = size
//        info.offset = 0
//        var presentTimeUs = System.nanoTime() / 1000
//        info.presentationTimeUs = System.nanoTime() / 1000 - presentTimeUs
//        info.presentationTimeUs = timestamp
//        if(isIdr){
//            info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
//            if(!rtspServer.isRunning()){
//                Log.d("camera_streaming","SERVER NOT RUNNING! SETTING VIDEO INFO")
//                //PODE DAR MERDA POIS PODE RETORNAR NULL POR ALGUM MOTIVO
//                val (newSps, newPps) = decodeSpsPpsFromBuffer(h264Buffer, size)!!
//                rtspServer.setVideoInfo(newSps,newPps,null)
//            }
//        }
//        if(rtspServer.isRunning()){
//            Log.d("Streaming","SERVER RUNNING!")
//            rtspServer.sendVideo(h264Buffer,info)
//        }
//
//        if (isIdr) {
//            info.flags = MediaCodec.BUFFER_FLAG_KEY_FRAME
//            if (!rtspClient.isStreaming) {
//                presentTimeUs = System.nanoTime() / 1000
//                val (newSps, newPps) = decodeSpsPpsFromBuffer(h264Buffer, size)
//                rtspClient.setVideoInfo(newSps, newPps, null)
//                rtspClient.connect("<rtsp://<rtsp-server-IP>:8554/mystream")
//            }
//        }
//        if (rtspClient.isStreaming) {
//            rtspClient.sendVideo(h264Buffer, info)
//        }
//    }

    private fun decodeSpsPpsFromBuffer(
        outputBuffer: ByteBuffer,
        length: Int
    ): Pair<ByteBuffer, ByteBuffer>? {
        val csd = ByteArray(length)
        outputBuffer[csd, 0, length]
        var i = 0
        var spsIndex = -1
        var ppsIndex = -1
        while (i < length - 4) {
            if (csd[i].toInt() == 0 && csd[i + 1].toInt() == 0 && csd[i + 2].toInt() == 0 && csd[i + 3].toInt() == 1) {
                if (spsIndex == -1) {
                    spsIndex = i
                } else {
                    ppsIndex = i
                    break
                }
            }
            i++
        }
        if (spsIndex != -1 && ppsIndex != -1) {
            val sps = ByteArray(ppsIndex)
            System.arraycopy(csd, spsIndex, sps, 0, ppsIndex)
            val pps = ByteArray(length - ppsIndex)
            System.arraycopy(csd, ppsIndex, pps, 0, length - ppsIndex)
            return Pair(ByteBuffer.wrap(sps), ByteBuffer.wrap(pps))
        }
        return null
    }


    fun startPreview(textureView: TextureView){
        rtspServerStream.startPreview(textureView)
    }

    fun startPreview(surfaceView: SurfaceView) {
        rtspServerStream.startPreview(surfaceView)
    }

    fun stopStream(){
        //rtspServerCamera1.stopStream()
    }

    //FROM ClientListener()
    override fun onClientConnected(client: ServerClient) {
        Log.d("Streaming","Client Connected: ${client.name}")

    }

    override fun onClientDisconnected(client: ServerClient) {
        Log.d("Streaming","Client Disconnected: ${client.name}")
    }

    //FROM ConnectChecker()
    override fun onAuthError() {
        Log.e("Streaming","Auth ERROR!")
    }

    override fun onAuthSuccess() {
        Log.d("Streaming","Auth Success")
    }

    override fun onConnectionFailed(reason: String) {
        Log.e("Streaming","Connection Failed: $reason")
    }

    override fun onConnectionStarted(url: String) {
        Log.d("Streaming","Connection Started with: $url")
    }

    override fun onConnectionSuccess() {
        Log.d("Streaming","Connection Successful!")
    }

    override fun onDisconnect() {
        Log.d("Streaming","onDisconnect!")
    }

    override fun onNewBitrate(bitrate: Long) {
    }




}