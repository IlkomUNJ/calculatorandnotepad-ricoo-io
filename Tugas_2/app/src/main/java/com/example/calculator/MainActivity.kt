package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculator.ui.theme.CalculatorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                AppNavigator()
            }

        }
    }
}

object AppRoutes {
    const val MENU_SCREEN = "menu"
    const val CALCULATOR_SCREEN = "calculator"
    const val TEXTEDITOR_SCREEN = "text_editor"

    const val NOTEPAD_MENU = "texteditor_menu"
    const val NOTE_EDITOR = "texteditor_editor"
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            fontSize = 28.sp,
            text = "Welcome to Main Menu"
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {

            navController.navigate(AppRoutes.CALCULATOR_SCREEN)

        }) {
            Text("Calculator")
        }
        Button(onClick = {

            navController.navigate(AppRoutes.TEXTEDITOR_SCREEN)

        }) {
            Text("Text Editor")
        }
    }
}

@Composable
fun AppNavigator() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.MENU_SCREEN
    ) {
        composable(route = AppRoutes.MENU_SCREEN) {
            MenuScreen(navController = navController)
        }

        composable(route = AppRoutes.CALCULATOR_SCREEN) {
            CalculatorScreen.SetupLayout(navController = navController)
        }

        composable(route = AppRoutes.TEXTEDITOR_SCREEN) {
            TextEditorScreen.SetupLayout(navController = navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculatorTheme {
        AppNavigator()
    }
}