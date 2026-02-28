package com.example.rental_property.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.rental_property.R
import com.example.rental_property.model.RentModel
import com.example.rental_property.repository.RentRepoImpl
import com.example.rental_property.repository.UserRepoImpl
import com.example.rental_property.viewmodel.RentViewModel
import com.example.rental_property.viewmodel.UserViewModel
import com.example.rental_property.viewmodel.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DashboardBody() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val context = LocalContext.current

    // Theme Colors
    val forestGreen = Color(0xFF1B5E20)
    val leafGreen = Color(0xFF4CAF50)
    val lightBg = Color(0xFFF8FAF8)

    // Setup ViewModels
    val userRepo = remember { UserRepoImpl() }
    val rentRepo = remember { RentRepoImpl() }
    val rentViewModel: RentViewModel = viewModel(factory = ViewModelFactory(rentRepo))
    val userViewModel: UserViewModel = viewModel(factory = ViewModelFactory(userRepo))

    // State Management
    var searchQuery by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Fetch Admin Status
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            userViewModel.getUserData(currentUserId) { user ->
                isAdmin = user?.role == "admin"
            }
        }
    }

    if (showAddDialog) {
        AddPropertyDialog(viewModel = rentViewModel, onDismiss = { showAddDialog = false })
    }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = leafGreen,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("List Property") }
                )
            }
        },
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = forestGreen,
                        titleContentColor = Color.White
                    ),
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("RENTAL PRO", fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 20.sp)
                            Text("Premium Living", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        }
                    },
                    actions = {
                        IconButton(onClick = { Toast.makeText(context, "No notifications", Toast.LENGTH_SHORT).show() }) {
                            Icon(painterResource(R.drawable.baseline_notifications_24), "Notifications", tint = Color.White)
                        }
                    }
                )

                // Decorative Green Line
                Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(leafGreen.copy(alpha = 0.4f)))

                // Search & Header (Only visible on Home Tab)
                if (selectedIndex == 0) {
                    Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Discover", fontSize = 14.sp, color = Color.Gray)
                                Text("Perfect Homes", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = forestGreen)
                            }
                            Box(modifier = Modifier.size(45.dp).background(lightBg, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(painterResource(R.drawable.baseline_person_24), null, tint = forestGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Working Search Field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by location or title...") },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = forestGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = leafGreen,
                                cursorColor = forestGreen
                            )
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val navItems = listOf(
                    Triple("Home", R.drawable.baseline_home_24, 0),
                    Triple("Saved", R.drawable.baseline_favorite_24, 1),
                    Triple("Profile", R.drawable.baseline_person_24, 2)
                )
                navItems.forEach { item ->
                    val isSelected = selectedIndex == item.third
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedIndex = item.third },
                        label = { Text(item.first) },
                        icon = { Icon(painterResource(item.second), null) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = leafGreen.copy(alpha = 0.1f),
                            selectedIconColor = forestGreen,
                            selectedTextColor = forestGreen
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(lightBg)) {
            when (selectedIndex) {
                0 -> HomeScreen(rentViewModel, isAdmin, searchQuery)
                1 -> SavedScreen()
                2 -> ProfileScreen(userViewModel) // Pass the userViewModel here!
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyDialog(viewModel: RentViewModel, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                enabled = !isUploading,
                onClick = {
                    if (title.isNotEmpty() && price.isNotEmpty()) {
                        isUploading = true
                        if (imageUri != null) {
                            // Upload with image
                            viewModel.repo.uploadImage(imageUri!!) { success, imageUrl ->
                                if (success && imageUrl != null) {
                                    val newRent = RentModel(
                                        title = title,
                                        price = "$$price/mo",
                                        location = location,
                                        imageUrl = imageUrl,
                                        status = "available"
                                    )
                                    viewModel.repo.addRent(newRent) { addSuccess, _ ->
                                        isUploading = false
                                        if (addSuccess) onDismiss()
                                    }
                                } else {
                                    isUploading = false
                                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // Upload without image
                            val newRent = RentModel(
                                title = title,
                                price = "$$price/mo",
                                location = location,
                                imageUrl = "", // Empty image URL
                                status = "available"
                            )
                            viewModel.repo.addRent(newRent) { addSuccess, _ ->
                                isUploading = false
                                if (addSuccess) onDismiss()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Fill title and price at least", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                if (isUploading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Post Listing")
                }
            }
        },
        title = { Text("New Property Listing", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Image Picker Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(0.3f))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, "Add Image", tint = Color.Gray)
                            Text("Select Property Photo (Optional)", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (USD)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            }
        }
    )
}