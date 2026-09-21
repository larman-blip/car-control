package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistItem
import com.example.data.LocalInspectionData
import com.example.data.SectionType
import com.example.ui.InspectionViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.components.CarTranslate
import com.example.ui.components.ChecklistIllustration
import com.example.ui.components.MainGradientHeader
import com.example.ui.components.RacingOrange
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.animation.core.*
import android.speech.tts.TextToSpeech
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionSectionScreen(
    viewModel: InspectionViewModel,
    sectionType: SectionType,
    onBack: () -> Unit
) {
    val lang = viewModel.currentLanguage
    val isDark = viewModel.isDarkMode
    val responses = viewModel.draftResponses.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val secTitle = when (sectionType) {
                        SectionType.DOCUMENTS -> CarTranslate.get("DOCUMENTS", lang) + " & History"
                        SectionType.OBD_SCANNER -> CarTranslate.get("obd_scanner", lang)
                        else -> "Inspection - ${sectionType.name.replace("_", " ")}"
                    }
                    Text(text = secTitle, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (sectionType == SectionType.OBD_SCANNER) {
                ObdScannerSectionView(viewModel = viewModel, lang = lang)
            } else {
                // Regular checklist items matching the current section
                val activeSecItems = viewModel.getActiveChecklistItems().filter { it.section == sectionType }

                if (activeSecItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = CarTranslate.get("not_applicable", lang),
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(activeSecItems, key = { it.id }) { item ->
                            ChecklistItemWidget(
                                item = item,
                                currentAnswer = responses[item.id] ?: "UNANSWERED",
                                lang = lang,
                                onAnswerSelected = { ans ->
                                    viewModel.updateResponse(item.id, ans)
                                }
                            )
                        }
                    }
                }
            }

            // Quick back button footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = CarTranslate.get("back", lang), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChecklistItemWidget(
    item: ChecklistItem,
    currentAnswer: String,
    lang: String,
    onAnswerSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checklist_item_${item.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.title.get(lang),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                if (item.isDealBreaker) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFD32F2F).copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CRITICAL",
                            color = Color(0xFFD32F2F),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Guidance Accordion Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .padding(12.dp)
            ) {
                // How to
                Text(
                    text = CarTranslate.get("how_to_inspect", lang) + ":",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = item.howToCheck.get(lang),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                // Normal outcome
                Text(
                    text = CarTranslate.get("normal_res_title", lang) + ":",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C)
                )
                Text(
                    text = item.normalResult.get(lang),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                // Warning outcome
                Text(
                    text = CarTranslate.get("warn_res_title", lang) + ":",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F)
                )
                Text(
                    text = item.warningResult.get(lang),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            VoiceGuideControl(itemId = item.id, lang = lang)

            Spacer(modifier = Modifier.height(12.dp))

            // Choice Chips row (YES / WARNING / UNANSWERED)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // YES (Passed / Normal)
                val isNormalSelected = currentAnswer == "YES"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isNormalSelected) Color(0xFF388E3C)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onAnswerSelected("YES") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = CarTranslate.get("yes", lang).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (isNormalSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                // WARNING (Failed / Problem)
                val isWarningSelected = currentAnswer == "WARNING"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isWarningSelected) Color(0xFFD32F2F)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onAnswerSelected("WARNING") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = CarTranslate.get("no", lang).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (isWarningSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                // UNANSWERED (Skip)
                val isUnansweredSelected = currentAnswer == "UNANSWERED"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isUnansweredSelected) MaterialTheme.colorScheme.outline
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onAnswerSelected("UNANSWERED") }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = CarTranslate.get("unanswered", lang).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (isUnansweredSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ObdScannerSectionView(
    viewModel: InspectionViewModel,
    lang: String
) {
    var searchCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = CarTranslate.get("enter_dtc", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchCode,
                        onValueChange = { searchCode = it },
                        placeholder = { Text("P0300, P0420...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dtc_code_input"),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            if (searchCode.isNotBlank()) {
                                viewModel.addDtcCode(searchCode)
                                searchCode = ""
                            }
                            focusManager.clearFocus()
                        })
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (searchCode.isNotBlank()) {
                                viewModel.addDtcCode(searchCode)
                                searchCode = ""
                            }
                            focusManager.clearFocus()
                        },
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(CarTranslate.get("add_code", lang))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active codes list
        Text(
            text = "Active Troubled Codes Logged:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (viewModel.draftDtcCodes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No diagnostic codes logged. Standard test looks healthy.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.draftDtcCodes) { code ->
                    val obdInfo = LocalInspectionData.OBD_DATABASE[code.uppercase().trim()]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dtc_card_$code"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RacingOrange.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.DeveloperBoard,
                                        contentDescription = "OBD Code",
                                        tint = RacingOrange
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = code.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = RacingOrange
                                    )
                                }
                                IconButton(onClick = { viewModel.removeDtcCode(code) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove DTC",
                                        tint = Color.Gray
                                    )
                                }
                            }

                            if (obdInfo != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = obdInfo.meaning.get(lang),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                val levelColor = when (obdInfo.severity) {
                                    "HIGH" -> Color(0xFFD32F2F)
                                    "MEDIUM" -> Color(0xFFFF9800)
                                    else -> Color(0xFF388E3C)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = CarTranslate.get("severity", lang) + ": ",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(levelColor.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = obdInfo.severity,
                                            color = levelColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = obdInfo.description.get(lang),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = CarTranslate.get("impact", lang) + ":",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RacingOrange
                                )
                                Text(
                                    text = obdInfo.impact.get(lang),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Custom Generic Code Description",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "This diagnostic trouble code indicates an active system failure recorded inside the engine control computer. Please check with an electronic OBD audit tool.",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private var ttsInstance: TextToSpeech? = null

fun speakTextWithTts(context: android.content.Context, text: String) {
    if (ttsInstance == null) {
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.let { tts ->
                    val locale = Locale("ar")
                    tts.setLanguage(locale)
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    } else {
        ttsInstance?.let { tts ->
            val locale = Locale("ar")
            tts.setLanguage(locale)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}

fun getAssetAudioDescriptor(context: android.content.Context, itemId: String): android.content.res.AssetFileDescriptor? {
    val items = LocalInspectionData.CHECKLIST_ITEMS
    val index = items.indexOfFirst { it.id == itemId } + 1
    
    val possiblePaths = mutableListOf<String>()
    possiblePaths.add("voice_guides/$itemId.mp3")
    possiblePaths.add("voice_guides/$itemId.m4a")
    possiblePaths.add("voice_guides/$itemId.wav")
    if (index > 0) {
        possiblePaths.add("voice_guides/$index.mp3")
        possiblePaths.add("voice_guides/$index.MP3")
        possiblePaths.add("voice_guides/$index.m4a")
        possiblePaths.add("voice_guides/$index.wav")
    }
    
    for (path in possiblePaths) {
        try {
            return context.assets.openFd(path)
        } catch (_: Exception) {}
    }
    return null
}

fun getExternalAudioFile(context: android.content.Context, itemId: String): File? {
    val items = LocalInspectionData.CHECKLIST_ITEMS
    val index = items.indexOfFirst { it.id == itemId } + 1
    val dir = context.getExternalFilesDir("voice_guides") ?: return null
    
    val possibleNames = mutableListOf<String>()
    possibleNames.add("$itemId.mp3")
    possibleNames.add("$itemId.m4a")
    possibleNames.add("$itemId.wav")
    if (index > 0) {
        possibleNames.add("$index.mp3")
        possibleNames.add("$index.MP3")
        possibleNames.add("$index.m4a")
        possibleNames.add("$index.wav")
    }
    
    for (name in possibleNames) {
        val file = File(dir, name)
        if (file.exists() && file.length() > 0) {
            return file
        }
    }
    return null
}

@Composable
fun VoiceGuideControl(itemId: String, lang: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val items = remember { LocalInspectionData.CHECKLIST_ITEMS }
    val itemIndex = remember(itemId) { items.indexOfFirst { it.id == itemId } + 1 }
    
    val audioFile = remember(itemId) { File(context.filesDir, "audio_guide_${itemId}.m4a") }
    var fileExists by remember(itemId) { mutableStateOf(audioFile.exists()) }

    val externalAudioFile = remember(itemId) { getExternalAudioFile(context, itemId) }
    val assetAudioDescriptor = remember(itemId) { getAssetAudioDescriptor(context, itemId) }

    val hasAsset = assetAudioDescriptor != null
    val hasExternal = externalAudioFile != null

    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isTtsPlaying by remember { mutableStateOf(false) }
    var recordDuration by remember { mutableStateOf(0) }

    var activeRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var activePlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordDuration = 0
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                recordDuration++
            }
        }
    }

    DisposableEffect(itemId) {
        onDispose {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activeRecorder != null) {
                    activeRecorder?.stop()
                }
                activeRecorder?.release()
            } catch (_: Exception) {}
            try {
                activePlayer?.stop()
                activePlayer?.release()
            } catch (_: Exception) {}
            try {
                if (isTtsPlaying) {
                    ttsInstance?.stop()
                }
            } catch (_: Exception) {}
            isRecording = false
            isPlaying = false
            isTtsPlaying = false
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                if (audioFile.exists()) {
                    audioFile.delete()
                }
                val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }
                r.setAudioSource(MediaRecorder.AudioSource.MIC)
                r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                r.setOutputFile(audioFile.absolutePath)
                r.prepare()
                r.start()
                activeRecorder = r
                isRecording = true
                Toast.makeText(context, if (lang == "ar" || lang == "tn") "بدأ التسجيل... تكلم الآن 🎙️" else "Enregistrement en cours...🎙️", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erreur initialisation micro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Permission micro refusée !", Toast.LENGTH_LONG).show()
        }
    }

    fun startRecordingFlow() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                if (audioFile.exists()) {
                    audioFile.delete()
                }
                val r = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    MediaRecorder(context)
                } else {
                    @Suppress("DEPRECATION")
                    MediaRecorder()
                }
                r.setAudioSource(MediaRecorder.AudioSource.MIC)
                r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                r.setOutputFile(audioFile.absolutePath)
                r.prepare()
                r.start()
                activeRecorder = r
                isRecording = true
                Toast.makeText(context, if (lang == "ar" || lang == "tn") "بدأ التسجيل... تكلم الآن 🎙️" else "Enregistrement en cours...🎙️", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erreur initialisation micro: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecordingFlow() {
        try {
            activeRecorder?.stop()
            activeRecorder?.release()
            activeRecorder = null
            isRecording = false
            fileExists = audioFile.exists()
            Toast.makeText(context, if (lang == "ar" || lang == "tn") "تم حفظ التسجيل فالميموار بنجاح! ✅" else "Enregistrement sauvegardé ! ✅", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            isRecording = false
            Toast.makeText(context, "قص التسجيل", Toast.LENGTH_SHORT).show()
        }
    }

    fun togglePlayFlow() {
        if (isPlaying || isTtsPlaying) {
            try {
                if (isTtsPlaying) {
                    ttsInstance?.stop()
                    isTtsPlaying = false
                }
                activePlayer?.stop()
                activePlayer?.release()
                activePlayer = null
                isPlaying = false
            } catch (_: Exception) {}
        } else {
            try {
                if (fileExists) {
                    val p = MediaPlayer()
                    p.setDataSource(audioFile.absolutePath)
                    p.prepare()
                    p.start()
                    activePlayer = p
                    isPlaying = true
                    p.setOnCompletionListener {
                        p.release()
                        activePlayer = null
                        isPlaying = false
                    }
                } else if (externalAudioFile != null) {
                    val p = MediaPlayer()
                    p.setDataSource(externalAudioFile.absolutePath)
                    p.prepare()
                    p.start()
                    activePlayer = p
                    isPlaying = true
                    p.setOnCompletionListener {
                        p.release()
                        activePlayer = null
                        isPlaying = false
                    }
                } else if (assetAudioDescriptor != null) {
                    val p = MediaPlayer()
                    p.setDataSource(
                        assetAudioDescriptor.fileDescriptor,
                        assetAudioDescriptor.startOffset,
                        assetAudioDescriptor.length
                    )
                    assetAudioDescriptor.close()
                    p.prepare()
                    p.start()
                    activePlayer = p
                    isPlaying = true
                    p.setOnCompletionListener {
                        p.release()
                        activePlayer = null
                        isPlaying = false
                    }
                } else {
                    // Fallback to TTS (Text-to-Speech)
                    val item = items.find { it.id == itemId }
                    val textToSpeak = item?.howToCheck?.get(lang) ?: ""
                    if (textToSpeak.isNotEmpty()) {
                        isTtsPlaying = true
                        speakTextWithTts(context, textToSpeak)
                        Toast.makeText(context, if (lang == "ar" || lang == "tn") "تشغيل المساعد الصوتي الذكي 🤖" else "Lecture par voix synthétisée 🤖", Toast.LENGTH_SHORT).show()
                        
                        // Automatically reset state after 8 seconds or manually if stopped
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(8000)
                            if (isTtsPlaying) {
                                isTtsPlaying = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Aucune explication disponible", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Erreur lecture audio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val audioTypeLabel = when {
        isRecording -> if (lang == "ar" || lang == "tn") "جاري التسجيل..." else "Enregistrement..."
        fileExists -> if (lang == "ar" || lang == "tn") "تسجيلك الشخصي 🎙️" else "Votre voix 🎙️"
        hasExternal -> if (lang == "ar" || lang == "tn") "صوت خارجي مضاف (Q$itemIndex) 📁" else "Audio externe (Q$itemIndex) 📁"
        hasAsset -> if (lang == "ar" || lang == "tn") "توجيه صوتي مسبق (Q$itemIndex) 🔊" else "Guide vocal (Q$itemIndex) 🔊"
        else -> if (lang == "ar" || lang == "tn") "المساعد الصوتي الذكي 🤖" else "Guide Vocal Intelligent 🤖"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecording) {
                Color(0xFFD32F2F).copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
            }
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.Mic else Icons.Filled.RecordVoiceOver,
                        contentDescription = "Audio Guide Icon",
                        tint = if (isRecording) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) {
                            CarTranslate.get("recording_active", lang) + " (${recordDuration}s)"
                        } else {
                            audioTypeLabel
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecording) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (isRecording) {
                    Button(
                        onClick = { stopRecordingFlow() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text(
                            text = CarTranslate.get("stop_recording", lang),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play button (always available due to TTS fallback!)
                        IconButton(
                            onClick = { togglePlayFlow() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying || isTtsPlaying) Icons.Filled.StopCircle else Icons.Filled.PlayCircle,
                                contentDescription = "Play voice guide",
                                tint = if (isPlaying || isTtsPlaying) Color(0xFF388E3C) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Record button
                        IconButton(
                            onClick = { startRecordingFlow() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Record voice guide",
                                tint = if (fileExists) Color.Gray else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Option to clear vocal
                        if (fileExists) {
                            IconButton(
                                onClick = {
                                    audioFile.delete()
                                    fileExists = false
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Clear voice guide",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Description of voice guidance
            if (!isRecording) {
                val descText = when {
                    fileExists -> if (lang == "ar" || lang == "tn") "جاهز للاستماع لتسجيلك الخاص" else "Prêt à écouter votre propre enregistrement"
                    hasExternal -> if (lang == "ar" || lang == "tn") "تم تحميل الملف الصوتي المخصص رقم $itemIndex بنجاح" else "Fichier audio personnalisé numéro $itemIndex chargé"
                    hasAsset -> if (lang == "ar" || lang == "tn") "اضغط تشغيل للاستماع للدليل الصوتي المرفق" else "Cliquez sur play pour écouter le guide audio inclus"
                    else -> if (lang == "ar" || lang == "tn") "انقر لتفعيل التوجيه الصوتي بالذكاء الاصطناعي" else "Cliquez pour écouter l'explication"
                }
                Text(
                    text = descText,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
