package com.example.rental_property.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rental_property.R
import com.example.rental_property.model.UserModel
import com.example.rental_property.repository.UserRepoImpl
import kotlinx.coroutines.launch

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { RegistrationBody() }
    }
}

@Composable
fun RegistrationBody() {
    var fname by remember { mutableStateOf("") }
    var lname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity
    val repo = remember { UserRepoImpl() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val ForestGreen = Color(0xFF2E7D32)
    val LeafGreen = Color(0xFF4CAF50)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()) // Prevents UI breaking when typing
                .padding(bottom = 24.dp)
        ) {
            // DESIGNER HEADER: Top Curve with Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(ForestGreen, LeafGreen)),
                        shape = RoundedCornerShape(bottomStart = 100.dp)
                    )
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        activity.finish()
                    },
                    modifier = Modifier.padding(top = 40.dp, start = 16.dp)
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
                        modifier = Modifier.size(60.dp).padding(bottom = 8.dp)
                    )
                    Text("Join Rental Pro", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            // FORM SECTION
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    "Create your profile",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ForestGreen
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Input Fields with Rounded Styling
                RegistrationField(value = fname, onValueChange = { fname = it }, label = "First Name", color = LeafGreen)
                RegistrationField(value = lname, onValueChange = { lname = it }, label = "Last Name", color = LeafGreen)
                RegistrationField(value = email, onValueChange = { email = it }, label = "Email", color = LeafGreen)
                RegistrationField(value = phone, onValueChange = { phone = it }, label = "Phone Number", color = LeafGreen)
                RegistrationField(value = pass, onValueChange = { pass = it }, label = "Password", isPassword = true, color = LeafGreen)

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = ForestGreen)
                } else {
                    Button(
                        onClick = {
                            if (fname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Fields cannot be empty") }
                                return@Button
                            }
                            isLoading = true
                            val user = UserModel("", email, fname, lname, phone, "")
                            repo.register(user, pass) { success, msg ->
                                isLoading = false
                                if (success) {
                                    Toast.makeText(context, "Account Created!", Toast.LENGTH_SHORT).show()
                                    context.startActivity(Intent(context, LoginActivity::class.java))
                                    activity.finish()
                                } else {
                                    coroutineScope.launch { snackbarHostState.showSnackbar(msg ?: "Error") }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                    ) {
                        Text("SIGN UP", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Navigation back to Login
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Already a member? ", color = Color.Gray)
                    Text(
                        "Login",
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, LoginActivity::class.java))
                            activity.finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    color: Color
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = color,
            focusedLabelColor = color
        )
    )
}