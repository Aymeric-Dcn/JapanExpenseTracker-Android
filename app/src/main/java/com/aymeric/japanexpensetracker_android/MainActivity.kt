package com.aymeric.japanexpensetracker_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.aymeric.japanexpensetracker_android.presentation.navigation.AppNavigation
import com.aymeric.japanexpensetracker_android.presentation.viewmodel.ExpenseViewModel
import com.aymeric.japanexpensetracker_android.ui.theme.JapanExpenseTrackerAndroidTheme
import androidx.compose.foundation.layout.padding

class MainActivity : ComponentActivity() {

    private val expenseViewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JapanExpenseTrackerAndroidTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        viewModel = expenseViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}