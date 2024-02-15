package com.aliger.usbstreaming

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.TextureView
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.jiangdg.ausbc.CameraClient
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.utils.ToastUtils
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun USBCameraScreen() {
    val cameraPermissionState: PermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val permissions = listOf(
        Manifest.permission.CAMERA,
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO,

        )
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    if(multiplePermissionsState.allPermissionsGranted){
        BuildScreen(rememberCameraClient(context = LocalContext.current))
    }else{
        NoPermissionScreen(multiplePermissionsState::launchMultiplePermissionRequest)
    }
}

@Composable
fun rememberCameraClient(context: Context): CameraClient = remember {
    CameraClient.newBuilder(context).apply {
        setEnableGLES(true)
        setCameraStrategy(CameraUvcStrategy(context))
        setRawImage(false)
        setCameraRequest(
            CameraRequest.Builder()
                .setFrontCamera(false)
                .setPreviewWidth(1280)
                .setPreviewHeight(720)
                .create()
        )
        openDebug(true)

    }.build()
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BuildScreen(
    cameraClient : CameraClient,
    usbCameraViewModel: USBCameraViewModel = koinViewModel()
) {
    cameraClient.addPreviewDataCallBack(object : IPreviewDataCallBack {
        init {
            Log.d("camera_streaming","INSIDE PREVIEW CONSTUCTOR!")
        }
        override fun onPreviewData(
            data: ByteArray?,
            width: Int,
            height: Int,
            format: IPreviewDataCallBack.DataFormat
        ) {
            //Log.d("camera_streaming","PREVIEW DATA CALLBACK")
        }

    })

    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.height(100.dp))
            Box(Modifier.height(500.dp)) {
                AndroidView(
                    factory = { context1 ->
                        TextureView(context1).apply {
                            this.surfaceTextureListener = (object : TextureView.SurfaceTextureListener{
                                val _tag = "camera_streaming"
                                override fun onSurfaceTextureAvailable(
                                    surface: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) {
                                    usbCameraViewModel.startVideoStreaming(cameraClient, this@apply)
                                }

                                override fun onSurfaceTextureSizeChanged(
                                    surface: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) {
                                    Log.d(_tag, "onSurfaceTextureSIZECHANGED")
                                    cameraClient.setRenderSize(1280, 720)
                                }

                                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                    Log.d(_tag, "onSurfaceTextureDESTROYED")
                                    cameraClient.closeCamera()
                                    return true
                                }

                                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                                    //Log.d(_tag, "onSurfaceTextureUPDATED")
                                }

                            })
                        }
                    }
                )
            }
            val currentContext = LocalContext.current
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    captureImage(cameraClient, currentContext)
                },
                shape = CircleShape,
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 2.dp,
                        color = Color.Green,
                        shape = CircleShape
                    ),
                contentPadding = PaddingValues(1.dp),
                elevation = ButtonDefaults.buttonElevation(20.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.camera_icon),
                    contentDescription = "Camera Icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

    }

}

fun captureImage(cameraClient: CameraClient, context: Context){

    cameraClient.captureImage(object : ICaptureCallBack {
        override fun onBegin() {
            Toast.makeText(context, "onBegin", Toast.LENGTH_SHORT).show()
            Log.i("CameraClient", "onBegin")

        }

        override fun onError(error: String?) {
            Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show()
            Log.i("CameraClient", "onError")
        }

        override fun onComplete(path: String?) {
            Toast.makeText(context, "onComplete", Toast.LENGTH_SHORT).show()
            ToastUtils.show("OnComplete")
            Log.i("CameraClient", "onComplete")
        }
    })
}