package com.example.rental_property.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rental_property.R
import com.example.rental_property.repository.UserRepoImpl
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val repo = remember { UserRepoImpl() }

    val ForestGreen = Color(0xFF2E7D32)
    val LeafGreen = Color(0xFF4CAF50)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // ASYMMETRIC HEADER WITH LOGO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(ForestGreen, LeafGreen)),
                        shape = RoundedCornerShape(bottomEnd = 100.dp)
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.padding(start = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Rental Pro", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("Find Home. Easy.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Text("Welcome Back", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = ForestGreen, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularSocialButton(R.drawable.gmail)
                    Spacer(modifier = Modifier.width(20.dp))
                    CircularSocialButton(R.drawable.facebook)
                }

                Spacer(modifier = Modifier.height(25.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LeafGreen, focusedLabelColor = ForestGreen)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    trailingIcon = {
                        IconButton(onClick = { visibility = !visibility }) {
                            Icon(painter = painterResource(if (visibility) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24), contentDescription = null, tint = ForestGreen)
                        }
                    },
                    visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LeafGreen, focusedLabelColor = ForestGreen)
                )

                Text(
                    "Forgot Password?",
                    color = LeafGreen,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).clickable { context.startActivity(Intent(context, ForgetPasswordActivity::class.java)) },
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = LeafGreen)
                } else {
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Please fill all fields") }
                                return@Button
                            }
                            isLoading = true
                            repo.login(email, password) { success, message ->
                                isLoading = false
                                if (success) {
                                    context.startActivity(Intent(context, DashboardActivity::class.java))
                                    activity.finish()
                                } else {
                                    coroutineScope.launch { snackbarHostState.showSnackbar(message ?: "Error") }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("LOGIN", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Don't have an account? Sign Up",
                    modifier = Modifier.padding(bottom = 20.dp).clickable {
                        context.startActivity(Intent(context, RegistrationActivity::class.java))
                        activity.finish()
                    },
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CircularSocialButton(iconRes: Int) {
    Surface(
        modifier = Modifier.size(50.dp).clickable { },
        shape = CircleShape,
        color = Color(0xFFF5F5F5),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
        }
    }
}