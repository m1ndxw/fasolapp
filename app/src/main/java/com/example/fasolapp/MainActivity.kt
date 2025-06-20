package com.example.fasolapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fasolapp.data.Employee
import com.example.fasolapp.ui.screens.*
import com.example.fasolapp.ui.theme.FasolAppTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import androidx.navigation.NavType
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            // Handle permission denial
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        setContent {
            FasolAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { LoginScreen(navController) }
                        composable(
                            "dashboard/{employee}",
                            arguments = listOf(navArgument("employee") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val employeeJson = backStackEntry.arguments?.getString("employee")
                            val employee = Json.decodeFromString<Employee>(employeeJson!!)
                            DashboardScreen(navController, employee, application)
                        }
                        composable(
                            "tasks/{employee}",
                            arguments = listOf(navArgument("employee") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val employeeJson = backStackEntry.arguments?.getString("employee")
                            val employee = Json.decodeFromString<Employee>(employeeJson!!)
                            TasksScreen(navController, employee, application)
                        }
                        composable(
                            "stats/{employee}",
                            arguments = listOf(navArgument("employee") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val employeeJson = backStackEntry.arguments?.getString("employee")
                            val employee = Json.decodeFromString<Employee>(employeeJson!!)
                            StatsScreen(navController, employee, application)
                        }
                        composable("admin") { AdminScreen(navController, application) }
                        composable(
                            "profile/{employee}",
                            arguments = listOf(navArgument("employee") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val employeeJson = backStackEntry.arguments?.getString("employee")
                            val employee = Json.decodeFromString<Employee>(employeeJson!!)
                            ProfileScreen(navController, employee, application)
                        }
                    }
                }
            }
        }
    }
}