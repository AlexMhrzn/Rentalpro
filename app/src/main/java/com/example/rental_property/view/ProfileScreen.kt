package com.example.rental_property.view

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rental_property.R
import com.example.rental_property.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ProfileScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""

    val ForestGreen = Color(0xFF1B5E20)
    val LightBg = Color(0xFFF8FAF8)

    // Profile State (Removed profileImageUrl)
    var isEditing by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Initial Data Fetch
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            userViewModel.getUserData(currentUserId) { user ->
                user?.let {
                    firstName = it.firstName
                    lastName = it.lastName
                    phone = it.phoneNumber
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // STATIC Profile Icon (Replaced clickable image upload)
        Surface(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            color = ForestGreen.copy(0.1f)
        ) {
            Icon(
                painterResource(R.drawable.baseline_person_24),
                contentDescription = "User Icon",
                modifier = Modifier.padding(30.dp),
                tint = ForestGreen
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = auth.currentUser?.email ?: "No Email", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields (Same as before)
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            enabled = isEditing,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save / Edit Toggle
        Button(
            onClick = {
                if (isEditing) {
                    userViewModel.updateUser(currentUserId, firstName, lastName, phone) { success, msg ->
                        Toast.makeText(context, msg ?: "Updated", Toast.LENGTH_SHORT).show()
                        if (success) isEditing = false
                    }
                } else {
                    isEditing = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color(0xFF4CAF50) else ForestGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isEditing) "Save Changes" else "Edit Profile", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Logout Button
        OutlinedButton(
            onClick = {
                auth.signOut()
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                (context as? Activity)?.finish()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red))
        ) {
            Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}