package com.aliger.usbstreaming

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aliger.usbstreaming.ui.theme.USBStreamingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            USBStreamingTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    InitNavigator()
                }
            }
        }
    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitNavigator(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.Home.name ){
        composable(route = AppScreens.USBCamera.name){
            USBCameraScreen()
        }
        composable(route = AppScreens.Home.name){
            HomeScreen { navController.navigate(AppScreens.USBCamera.name) }
        }

    }


}

@Composable
fun HomeScreen(onClickNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(onClick = onClickNext) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Button Start Stream")
            Text(text = "Start Stream")
        }
    }
}

enum class AppScreens{
    Home,
    USBCamera,
}