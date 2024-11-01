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
        BuildScreen()
    }else{
        NoPermissionScreen(multiplePermissionsState::launchMultiplePermissionRequest)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BuildScreen(
    usbCameraViewModel: USBCameraViewModel = koinViewModel()
) {
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
                        TextureView(context1).also {
                            it.surfaceTextureListener = (object : TextureView.SurfaceTextureListener{
                                val _tag = "camera_streaming"
                                override fun onSurfaceTextureAvailable(
                                    surface: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) {
                                    usbCameraViewModel.startVideoStreaming(it)
                                }

                                override fun onSurfaceTextureSizeChanged(
                                    surface: SurfaceTexture,
                                    width: Int,
                                    height: Int
                                ) {
                                    Log.d(_tag, "onSurfaceTextureSIZECHANGED")
                                }

                                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                    Log.d(_tag, "onSurfaceTextureDESTROYED")
                                    usbCameraViewModel.stopVideoStreaming()
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