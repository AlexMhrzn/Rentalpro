package com.example.rental_property.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rental_property.R

@Composable
fun SavedScreen() {
    val ForestGreen = Color(0xFF1B5E20)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_favorite_24),
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = ForestGreen.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Favorites",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ForestGreen
        )
        Text(
            text = "Properties you save will appear here.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}