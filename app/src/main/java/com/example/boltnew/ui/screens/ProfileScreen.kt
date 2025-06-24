package com.example.boltnew.ui.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.boltnew.data.model.Profile
import com.example.boltnew.data.model.Address
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import com.example.boltnew.utils.CameraUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    // Camera launcher
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && capturedImageUri != null) {
            viewModel.updateAvatar(context, capturedImageUri!!)
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            capturedImageUri = CameraUtils.createImageUri(context)
            cameraLauncher.launch(capturedImageUri!!)
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Profile") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                if (!uiState.isEditing && uiState.profile != null) {
                    IconButton(onClick = { viewModel.setEditing(true) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        // Content
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
            
            uiState.profile != null -> {
                if (uiState.isEditing) {
                    EditProfileContent(
                        profile = uiState.profile!!,
                        onSave = { username, email, dateOfBirth, addresses ->
                            viewModel.updateProfile(username, email, dateOfBirth, addresses)
                        },
                        onCancel = { viewModel.setEditing(false) },
                        onAvatarClick = {
                            if (cameraPermissionState.status.isGranted) {
                                capturedImageUri = CameraUtils.createImageUri(context)
                                cameraLauncher.launch(capturedImageUri!!)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onAddAddress = { address -> viewModel.addAddress(address) },
                        onUpdateAddress = { address -> viewModel.updateAddress(address) },
                        onDeleteAddress = { address -> viewModel.deleteAddress(address) }
                    )
                } else {
                    ProfileContent(
                        profile = uiState.profile!!,
                        onAvatarClick = {
                            if (cameraPermissionState.status.isGranted) {
                                capturedImageUri = CameraUtils.createImageUri(context)
                                cameraLauncher.launch(capturedImageUri!!)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProfileContent(
    profile: Profile,
    onAvatarClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Avatar and basic info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarSection(
                    avatarUrl = profile.avatar?.url,
                    onClick = onAvatarClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (profile.role != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = profile.role.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
        
        item {
            // Basic Information Card
            ProfileInfoCard(profile = profile)
        }
        
        if (profile.addresses.isNotEmpty()) {
            item {
                Text(
                    text = "Addresses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(profile.addresses) { address ->
                AddressCard(address = address)
            }
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            item {
                Text(
                    text = "My Adverts",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(profile.userAdverts) { advert ->
                UserAdvertCard(advert = advert)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditProfileContent(
    profile: Profile,
    onSave: (String, String, LocalDate, List<Address>) -> Unit,
    onCancel: () -> Unit,
    onAvatarClick: () -> Unit,
    onAddAddress: (Address) -> Unit,
    onUpdateAddress: (Address) -> Unit,
    onDeleteAddress: (Address) -> Unit
) {
    var username by remember { mutableStateOf(profile.username) }
    var email by remember { mutableStateOf(profile.email) }
    var dateOfBirth by remember { mutableStateOf(profile.dateOfBirth) }
    
    val dateDialogState = rememberMaterialDialogState()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Avatar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AvatarSection(
                    avatarUrl = profile.avatar?.url,
                    onClick = onAvatarClick,
                    isEditing = true
                )
            }
        }
        
        item {
            // Edit Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Edit Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date of Birth
                    OutlinedTextField(
                        value = dateOfBirth.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        onValueChange = { },
                        label = { Text("Date of Birth") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dateDialogState.show() },
                        enabled = false,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date"
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        
                        Button(
                            onClick = {
                                onSave(username, email, dateOfBirth, profile.addresses)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok")
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = dateOfBirth,
            title = "Select Date of Birth"
        ) { date ->
            dateOfBirth = date
        }
    }
}

@Composable
private fun AvatarSection(
    avatarUrl: String?,
    onClick: () -> Unit,
    isEditing: Boolean = false
) {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            modifier = Modifier
                .size(120.dp)
                .clickable { onClick() },
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = if (avatarUrl.startsWith("http")) avatarUrl else File(avatarUrl),
                    contentDescription = "Profile Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        
        // Camera Icon
        Surface(
            modifier = Modifier
                .size(36.dp)
                .clickable { onClick() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Take Photo",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
    
    if (isEditing) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap to change photo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProfileInfoCard(profile: Profile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Profile Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            ProfileInfoItem(
                icon = Icons.Default.Person,
                label = "Username",
                value = profile.username
            )
            
            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = "Email",
                value = profile.email
            )
            
            ProfileInfoItem(
                icon = Icons.Default.DateRange,
                label = "Date of Birth",
                value = profile.dateOfBirth.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
            )
            
            if (profile.fullAddress.isNotBlank()) {
                ProfileInfoItem(
                    icon = Icons.Default.Home,
                    label = "Primary Address",
                    value = profile.fullAddress
                )
            }
        }
    }
}

@Composable
private fun AddressCard(address: Address) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = address.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = buildString {
                    append(address.firstLineAddress)
                    if (address.secondLineAddress.isNotBlank()) {
                        append("\n${address.secondLineAddress}")
                    }
                    append("\n${address.city} ${address.postCode}")
                    append("\n${address.country}")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (address.phoneNumber != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = address.phoneNumber,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun UserAdvertCard(advert: com.example.boltnew.data.model.UserAdvert) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = advert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = advert.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}