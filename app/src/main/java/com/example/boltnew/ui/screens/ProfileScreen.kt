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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.model.auth.profile.UserAdvert
import com.example.boltnew.presentation.viewmodel.ProfileViewModel
import com.example.boltnew.ui.components.AddressFormModal
import com.example.boltnew.ui.components.AdvertFormModal
import com.example.boltnew.ui.components.ProfileEditModal
import com.example.boltnew.utils.CameraUtils
import com.example.boltnew.utils.DisplayResult
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current
    
    // Pull-to-refresh state
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refreshProfile()
        }
    )
    
    // Update refresh state based on profile loading
    LaunchedEffect(profileState) {
        if (profileState !is com.example.boltnew.utils.RequestState.Loading) {
            isRefreshing = false
        }
    }
    
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
    
    // Show operation message
    uiState.operationMessage?.let { message ->
        LaunchedEffect(message) {
            // Auto-clear message after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearOperationMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Refresh Button
                    IconButton(
                        onClick = { 
                            isRefreshing = true
                            viewModel.refreshProfile() 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Logout Button
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = {
            uiState.operationMessage?.let { message ->
                SnackbarHost(
                    hostState = remember { SnackbarHostState() }.apply {
                        LaunchedEffect(message) {
                            showSnackbar(message)
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
        ) {
            // Content
            profileState.DisplayResult(
                modifier = Modifier.fillMaxSize(),
                onLoading = {
                    if (!isRefreshing) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Loading your profile...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                onError = { errorMessage ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Unable to load profile",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(onClick = onBackClick) {
                                    Text("Go Back")
                                }
                                Button(onClick = { viewModel.retryLoadProfile() }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }
                },
                onSuccess = { profile ->
                    ProfileContent(
                        profile = profile,
                        onAvatarClick = {
                            if (cameraPermissionState.status.isGranted) {
                                capturedImageUri = CameraUtils.createImageUri(context)
                                cameraLauncher.launch(capturedImageUri!!)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        onEditProfile = { viewModel.showProfileEditModal() },
                        onAddAddress = { viewModel.showAddAddressModal() },
                        onEditAddress = { address -> viewModel.showEditAddressModal(address) },
                        onDeleteAddress = { address -> viewModel.deleteAddress(address) },
                        onAddAdvert = { viewModel.showAddAdvertModal() },
                        onEditAdvert = { advert -> viewModel.showEditAdvertModal(advert) },
                        onDeleteAdvert = { advert -> viewModel.deleteAdvert(advert) },
                        isUpdatingAvatar = uiState.isUpdatingAvatar,
                        isAddressLoading = uiState.isAddressLoading,
                        isAdvertLoading = uiState.isAdvertLoading
                    )
                }
            )
            
            // Pull refresh indicator
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
    
    // Profile Edit Modal
    ProfileEditModal(
        isVisible = uiState.showProfileModal,
        profile = (profileState as? com.example.boltnew.utils.RequestState.Success)?.data,
        isLoading = uiState.isProfileLoading,
        onDismiss = { viewModel.hideProfileEditModal() },
        onSave = { dateOfBirth ->
            viewModel.updateProfileDob(dateOfBirth)
        }
    )
    
    // Address Form Modal
    AddressFormModal(
        isVisible = uiState.showAddressModal,
        address = uiState.editingAddress,
        isLoading = uiState.isAddressLoading,
        onDismiss = { viewModel.hideAddressModal() },
        onSave = { address ->
            if (uiState.editingAddress != null) {
                viewModel.updateAddress(address)
            } else {
                viewModel.createAddress(address)
            }
        }
    )
    
    // Advert Form Modal
    AdvertFormModal(
        isVisible = uiState.showAdvertModal,
        advert = uiState.editingAdvert,
        categories = categories,
        isLoading = uiState.isAdvertLoading,
        onDismiss = { viewModel.hideAdvertModal() },
        onSave = { title, description, slug, categoryId, coverImageUri ->
            if (uiState.editingAdvert != null) {
                viewModel.updateAdvert(
                    context = context,
                    advert = uiState.editingAdvert!!,
                    title = title,
                    description = description,
                    slug = slug,
                    categoryId = categoryId,
                    coverImageUri = coverImageUri
                )
            } else {
                viewModel.createAdvert(
                    context = context,
                    title = title,
                    description = description,
                    slug = slug,
                    categoryId = categoryId,
                    coverImageUri = coverImageUri
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProfileContent(
    profile: Profile,
    onAvatarClick: () -> Unit,
    onEditProfile: () -> Unit,
    onAddAddress: () -> Unit,
    onEditAddress: (Address) -> Unit,
    onDeleteAddress: (Address) -> Unit,
    onAddAdvert: () -> Unit,
    onEditAdvert: (UserAdvert) -> Unit,
    onDeleteAdvert: (UserAdvert) -> Unit,
    isUpdatingAvatar: Boolean = false,
    isAddressLoading: Boolean = false,
    isAdvertLoading: Boolean = false
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar and basic info section
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AvatarSection(
                    avatarUrl = profile.avatar?.url,
                    onClick = onAvatarClick,
                    isUpdating = isUpdatingAvatar
                )

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

                // Account status
                Surface(
                    color = if (profile.isConfirmed) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (profile.isConfirmed) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (profile.isConfirmed) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (profile.isConfirmed) "Verified Account" else "Pending Verification",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (profile.isConfirmed) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                }
            }
        }
        
        // Basic Information Card
        item {
            ProfileInfoCard(
                profile = profile,
                onEditProfile = onEditProfile
            )
        }
        
        // Addresses Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Addresses (${profile.addresses.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Add Address Button
                IconButton(
                    onClick = onAddAddress,
                    enabled = !isAddressLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Address",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        if (profile.addresses.isNotEmpty()) {
            items(profile.addresses) { address ->
                AddressCard(
                    address = address,
                    onEdit = { onEditAddress(address) },
                    onDelete = { onDeleteAddress(address) },
                    isLoading = isAddressLoading
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Addresses Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add your first address to get started with deliveries and billing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddAddress,
                            enabled = !isAddressLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Address")
                        }
                    }
                }
            }
        }
        
        // User Adverts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Adverts (${profile.userAdverts.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Add Advert Button
                IconButton(
                    onClick = onAddAdvert,
                    enabled = !isAdvertLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Advert",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        if (profile.userAdverts.isNotEmpty()) {
            items(profile.userAdverts) { advert ->
                UserAdvertCard(
                    advert = advert,
                    onEdit = { onEditAdvert(advert) },
                    onDelete = { onDeleteAdvert(advert) },
                    isLoading = isAdvertLoading
                )
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Adverts Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create your first advert to start sharing your services or products!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddAdvert,
                            enabled = !isAdvertLoading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Advert")
                        }
                    }
                }
            }
        }
        
        // Add some bottom padding for better scrolling experience
        item {
            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

@Composable
private fun AvatarSection(
    avatarUrl: String?,
    onClick: () -> Unit,
    isUpdating: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
                Box(modifier = Modifier.fillMaxSize()) {
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
                    
                    // Loading overlay
                    if (isUpdating) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        }
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
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isUpdating) "Updating..." else "Tap to change photo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ProfileInfoCard(
    profile: Profile,
    onEditProfile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Profile Information",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onEditProfile) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
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
                value = profile.dateOfBirthFormatted?.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) ?: "Not set"
            )
            
            ProfileInfoItem(
                icon = Icons.Default.AccountCircle,
                label = "Account Status",
                value = if (profile.isConfirmed) "Verified" else "Pending Verification"
            )
            
            ProfileInfoItem(
                icon = Icons.Default.Security,
                label = "Provider",
                value = profile.provider.replaceFirstChar { it.uppercase() }
            )
            
            if (profile.addresses.isNotEmpty()) {
                ProfileInfoItem(
                    icon = Icons.Default.Home,
                    label = "Primary Address",
                    value = "${profile.addresses.first().city}, ${profile.addresses.first().country}"
                )
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = address.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Row {
                    IconButton(
                        onClick = onEdit,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Address",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Address",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
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
private fun UserAdvertCard(
    advert: UserAdvert,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isLoading: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = "Advert",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = advert.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Row {
                    IconButton(
                        onClick = onEdit,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Advert",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        enabled = !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Advert",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = advert.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            
            if (advert.slug != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Slug: ${advert.slug}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
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