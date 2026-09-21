package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CarInspection
import com.example.ui.InspectionViewModel
import com.example.ui.components.CarTranslate
import com.example.ui.components.MainGradientHeader
import com.example.ui.components.RacingOrange
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import android.content.Intent
import android.content.Context
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: InspectionViewModel,
    onStartNewInspection: () -> Unit,
    onViewInspection: (CarInspection) -> Unit
) {
    val lang = viewModel.currentLanguage
    val isDark = viewModel.isDarkMode
    val inspections by viewModel.inspectionList.collectAsState()
    val context = LocalContext.current

    var showPhoneDialog by remember { mutableStateOf(false) }
    var tempPhone by remember { mutableStateOf(viewModel.configExpertPhone) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.initNewInspection()
                    onStartNewInspection()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier
                    .testTag("start_new_inspection_button")
                    .padding(bottom = 16.dp, end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Icon")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CarTranslate.get("start_new_inspection", lang),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Title Header with controls
            MainGradientHeader(
                title = CarTranslate.get("app_title", lang),
                subtitle = CarTranslate.get("app_subtitle", lang),
                isDarkMode = isDark,
                action = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Language button
                        IconButton(onClick = { viewModel.toggleLanguage() }) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "Toggle Language",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Dark mode toggle
                        IconButton(onClick = { viewModel.toggleDarkMode() }) {
                            Icon(
                                imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = "Toggle Dark Mode",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Settings button
                        IconButton(onClick = {
                            tempPhone = viewModel.configExpertPhone
                            showPhoneDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )

            // Vibrant Contact Expert / Car Diagnostic Card directly taking them to WhatsApp
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        openWhatsAppDirectly(context, viewModel.configExpertPhone, lang)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF25D366).copy(alpha = 0.12f) // Soft vibrant green
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Color(0xFF25D366) // WhatsApp Green
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF25D366))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "WhatsApp Expert",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (lang == "ar") "دياغنوستيك الكرهبة - اتصل بالخبير" else if (lang == "tn") "دياغنوستيك الكرهبة - كلم الخبير بالوقت" else if (lang == "fr") "Diagnostic Auto - Contacter l'Expert" else "Car Diagnostic - Contact Expert",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (lang == "ar") "اضغط للاتصال مباشرة عبر واتساب" else if (lang == "tn") "انزل هوني باش يتعدى للواتساب ديريكت" else if (lang == "fr") "Cliquez pour contacter directement sur WhatsApp" else "Click to contact directly on WhatsApp",
                                fontSize = 12.sp,
                                color = if (isDark) Color.White.copy(0.7f) else Color(0xFF2E7D32)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Arrow",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = CarTranslate.get("past_inspections", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            if (inspections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = "No car",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = CarTranslate.get("no_inspections", lang),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(inspections, key = { it.id }) { item ->
                        InspectionCard(
                            item = item,
                            lang = lang,
                            onClick = {
                                viewModel.loadInspection(item)
                                onViewInspection(item)
                            },
                            onDelete = {
                                viewModel.deleteInspection(item.id)
                            },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }

    if (showPhoneDialog) {
        AlertDialog(
            onDismissRequest = { showPhoneDialog = false },
            title = {
                Text(
                    text = CarTranslate.get("settings", lang),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = CarTranslate.get("expert_phone", lang),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { tempPhone = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        placeholder = { Text(CarTranslate.get("phone_placeholder", lang)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )


                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateExpertPhone(tempPhone)
                        showPhoneDialog = false
                    }
                ) {
                    Text(CarTranslate.get("save", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPhoneDialog = false }) {
                    Text(CarTranslate.get("cancel", lang))
                }
            }
        )
    }
}

@Composable
fun InspectionCard(
    item: CarInspection,
    lang: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(item.timestamp) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(item.timestamp))
    }

    val themeColor = when (item.verdict) {
        "BUY" -> Color(0xFF388E3C)
        "NEGOTIATE" -> Color(0xFFFF9800)
        else -> Color(0xFFD32F2F)
    }

    val labelVerdict = when (item.verdict) {
        "BUY" -> CarTranslate.get("buy", lang)
        "NEGOTIATE" -> CarTranslate.get("negotiate", lang)
        else -> CarTranslate.get("avoid", lang)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("inspection_card_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.brand} ${item.model}".uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${item.year}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.mileage} km",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                // Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${item.overallScore}/100",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = themeColor
                        )
                        Text(
                            text = labelVerdict,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = themeColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Trash icon with safe spacing
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun importVoiceGuideUri(context: android.content.Context, uri: Uri): String? {
    val resolver = context.contentResolver
    var fileName: String? = null
    try {
        val cursor = resolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }
            }
        } finally {
            cursor?.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    if (fileName == null) {
        val path = uri.path ?: return null
        val cut = path.lastIndexOf('/')
        fileName = if (cut != -1) path.substring(cut + 1) else path
    }

    val lowerName = fileName.lowercase()
    if (!lowerName.endsWith(".mp3") && !lowerName.endsWith(".m4a") && !lowerName.endsWith(".wav") && !lowerName.endsWith(".ogg")) {
        return null
    }

    val dir = context.getExternalFilesDir("voice_guides") ?: return null
    if (!dir.exists()) {
        dir.mkdirs()
    }

    // Smart number extraction to support "39.mp3", "Q39.mp3", "Record 39.mp3"
    val digitRegex = Regex("\\d+")
    val match = digitRegex.find(fileName.substringBeforeLast("."))
    val targetName = if (match != null) {
        val num = match.value
        val ext = fileName.substringAfterLast(".").lowercase()
        "$num.$ext"
    } else {
        fileName
    }

    val targetFile = File(dir, targetName)
    try {
        resolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        return targetName
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}

// Open WhatsApp directly for expert car diagnostic assistance
fun openWhatsAppDirectly(context: Context, phone: String, lang: String) {
    val message = if (lang == "ar") {
        "مرحباً، أود الحصول على دياغنوستيك واستشارة فنية بخصوص سيارة."
    } else if (lang == "tn") {
        "عصلامة، نحب نعمل دياغنوستيك وتثبيت لكرهبة ونشاور خبير."
    } else {
        "Bonjour, je souhaite obtenir un diagnostic et avis d'expert pour un véhicule."
    }
    val urlStr = "https://api.whatsapp.com/send?phone=${phone.replace(" ", "").replace("+", "")}&text=${Uri.encode(message)}"
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (err: Exception) {
            Toast.makeText(context, "Could not open dialer or WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
