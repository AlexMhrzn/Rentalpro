package com.example.rental_property.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.rental_property.model.RentModel
import com.example.rental_property.viewmodel.RentViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.painterResource

@Composable
fun HomeScreen(viewModel: RentViewModel, isAdmin: Boolean, searchQuery: String) {
    val rents by viewModel.rents.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 1. Fetch data when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchRents()
    }

    // 2. SEARCH LOGIC: Filter the list based on title or location
    val filteredRents = remember(searchQuery, rents) {
        rents.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
        }
    }

    // 3. UI Layout
    if (filteredRents.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No properties found", color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredRents) { rent ->
                PropertyCard(rent, isAdmin, userId, viewModel)
            }
        }
    }
}

@Composable
fun PropertyCard(rent: RentModel, isAdmin: Boolean, userId: String, viewModel: RentViewModel) {
    val context = LocalContext.current
    val forestGreen = Color(0xFF1B5E20)
    val leafGreen = Color(0xFF4CAF50)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Property Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (rent.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(rent.imageUrl),
                        contentDescription = "Property Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Image Available", color = Color.Gray)
                    }
                }

                // Status Badge Overlay
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = when(rent.status) {
                        "pending" -> Color(0xFFFFC107)
                        "sold" -> Color.Red
                        else -> leafGreen
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = rent.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = rent.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = forestGreen
                )

                Text(text = rent.location, color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = rent.price,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = forestGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ADMIN VS USER BUTTONS
                if (isAdmin) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (rent.status == "pending") {
                            Button(
                                onClick = { viewModel.repo.updateRentStatus(rent.rentId, "sold", rent.buyerId) { _, _ -> } },
                                colors = ButtonDefaults.buttonColors(containerColor = forestGreen)
                            ) { Text("Verify Sale") }

                            OutlinedButton(
                                onClick = { viewModel.repo.updateRentStatus(rent.rentId, "available", "") { _, _ -> } }
                            ) { Text("Cancel") }
                        }

                        IconButton(onClick = { viewModel.repo.deleteRent(rent.rentId) { _, _ -> } }) {
                            Icon(painterResource(com.example.rental_property.R.drawable.baseline_notifications_24), "Delete", tint = Color.Red)
                        }
                    }
                } else {
                    // USER VIEW
                    if (rent.status == "available") {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                viewModel.repo.updateRentStatus(rent.rentId, "pending", userId) { success, _ ->
                                    if(success) Toast.makeText(context, "Buy Request Sent!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = forestGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Buy / Rent Property", fontWeight = FontWeight.Bold)
                        }
                    } else if (rent.status == "pending" && rent.buyerId == userId) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Awaiting Admin Approval", color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
