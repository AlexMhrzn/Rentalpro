package com.example.rental_property.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rental_property.R
import com.example.rental_property.repository.UserRepoImpl
import com.example.rental_property.viewmodel.UserViewModel
import com.example.rental_property.viewmodel.ViewModelFactory

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = UserRepoImpl()
            val viewModel: UserViewModel = viewModel(factory = ViewModelFactory(repository))
            ForgetPasswordScreen(viewModel)
        }
    }
}

@Composable
fun ForgetPasswordScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val activity = context as Activity
    var email by remember { mutableStateOf("") }

    val ForestGreen = Color(0xFF2E7D32)
    val LeafGreen = Color(0xFF4CAF50)

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // THEMED HEADER: Symmetrical Curve for a "Balanced" look
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(ForestGreen, LeafGreen)),
                        shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                    ),
                contentAlignment = Alignment.TopStart
            ) {
                IconButton(
                    onClick = { activity.finish() },
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(70.dp).padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Password Recovery",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Don't worry, it happens!",
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Enter your email address below and we'll send you a link to reset your password.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("example@mail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeafGreen,
                        focusedLabelColor = ForestGreen,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isNotEmpty()) {
                            viewModel.forgetPassword(email) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    activity.finish()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text(
                        "SEND RESET LINK",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Back to Login",
                    modifier = Modifier.clickable { activity.finish() },
                    color = LeafGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}