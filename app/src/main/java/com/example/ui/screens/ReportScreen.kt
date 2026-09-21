package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChecklistItem
import com.example.data.ScoreCategory
import com.example.ui.CalculatedReport
import com.example.ui.InspectionViewModel
import com.example.ui.components.CarTranslate
import com.example.ui.components.MainGradientHeader
import com.example.ui.components.RacingOrange
import com.example.ui.components.SafeGreen
import com.example.ui.components.CriticalRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val lang = viewModel.currentLanguage
    val isDark = viewModel.isDarkMode
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val report = remember { viewModel.calculateCurrentReport() }

    val verdictColor = when (report.verdict) {
        "BUY" -> Color(0xFF388E3C)
        "NEGOTIATE" -> Color(0xFFFF9800)
        else -> Color(0xFFD32F2F)
    }

    val verdictLabel = when (report.verdict) {
        "BUY" -> CarTranslate.get("buy", lang)
        "NEGOTIATE" -> CarTranslate.get("negotiate", lang)
        else -> CarTranslate.get("avoid", lang)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = CarTranslate.get("verdict", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onDone) {
                        Icon(imageVector = Icons.Filled.Check, contentDescription = "Done")
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Vehicle Identity Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = "Car",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${viewModel.draftBrand} ${viewModel.draftModel}".uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${viewModel.draftYear} • ${viewModel.draftFuel} • ${viewModel.draftTransmission} • ${viewModel.draftMileage} km",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score and Recommendation Card (styled according to High Density Design HTML)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("score_and_recommendation_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = CarTranslate.get("overall_score", lang),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${report.overallScore}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 44.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 44.sp
                                )
                                Text(
                                    text = "/100",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${CarTranslate.get("verdict", lang)}: $verdictLabel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Circular Progress Indicator representing overall score percent
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = report.overallScore.toFloat() / 100f,
                                strokeWidth = 8.dp,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "${report.overallScore}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary stats row (Risk & Repair limits)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = CarTranslate.get("risk_level", lang).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                            val riskStr = when (report.verdict) {
                                "BUY" -> "LOW"
                                "NEGOTIATE" -> "MEDIUM"
                                else -> "HIGH"
                            }
                            Text(
                                text = riskStr,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = CarTranslate.get("repair_risk", lang).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                            val repairStr = when (report.repairCostRisk) {
                                "LOW" -> "0€"
                                "MINIMAL" -> "100€-500€"
                                "MODERATE" -> "500€-2000€"
                                else -> "2000€+"
                            }
                            Text(
                                text = repairStr,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Deal Breakers Danger container (if present)
            if (report.hasDealBreakers) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Warning Icon",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = CarTranslate.get("deal_breaker_alert", lang),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = CarTranslate.get("deal_breaker_descr", lang),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // List triggered deal breakers
                        report.dealBreakersTriggered.forEach { dbItem ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "DB",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dbItem.dealBreakerLabel?.get(lang) ?: dbItem.title.get(lang),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Export / Share Button
            Button(
                onClick = {
                    shareTextReport(context, viewModel, report, lang)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("export_text_report_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CarTranslate.get("share_report", lang),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (lang == "ar") "تفاصيل تصنيف النقاط والمحاور:" else if (lang == "tn") "هوني تلقى تفاصيل سكور كل فئة:" else if (lang == "fr") "Détails d'évaluation par catégorie :" else "Scoring Breakdown Details:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dynamic Grid of categories (3 columns as in High Density Design HTML)
            val categories = ScoreCategory.values()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                val chunkSize = 3
                val rows = categories.toList().chunked(chunkSize)
                rows.forEach { rowCategories ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { sc ->
                            val score = report.scores[sc] ?: 0
                            val weight = when (sc) {
                                ScoreCategory.DOCUMENTS -> 10
                                ScoreCategory.BODYWORK -> 20
                                ScoreCategory.INTERIOR -> 10
                                ScoreCategory.ENGINE -> 25
                                ScoreCategory.ELECTRICAL -> 10
                                ScoreCategory.TRANSMISSION -> 10
                                ScoreCategory.SUSPENSION_BRAKES -> 10
                                ScoreCategory.ROAD_TEST -> 5
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                CategoryGridItem(
                                    category = sc,
                                    score = score,
                                    maxWeight = weight,
                                    lang = lang,
                                    isDark = isDark
                                )
                            }
                        }
                        // If the last row is not full, add empty space weights to fill out the 3 columns beautifully
                        if (rowCategories.size < chunkSize) {
                            repeat(chunkSize - rowCategories.size) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expandable Warning/Errors Accordion
            ExpandablePointsWidget(
                title = CarTranslate.get("negative_points", lang),
                points = report.negativePoints,
                lang = lang,
                isWarn = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable Positive Accordion
            ExpandablePointsWidget(
                title = CarTranslate.get("positive_points", lang),
                points = report.positivePoints,
                lang = lang,
                isWarn = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Expert help card configuration
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = CarTranslate.get("consult_expert", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = CarTranslate.get("expert_pitch", lang),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            openWhatsAppConsultation(context, viewModel.configExpertPhone, viewModel, report, lang)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("whatsapp_consult_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Call, contentDescription = "Call", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = CarTranslate.get("call_whatsapp", lang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryGridItem(
    category: ScoreCategory,
    score: Int,
    maxWeight: Int,
    lang: String,
    isDark: Boolean
) {
    val title = when (category) {
        ScoreCategory.DOCUMENTS -> CarTranslate.get("DOCUMENTS", lang)
        ScoreCategory.BODYWORK -> "Tôlerie & Alignement"
        ScoreCategory.INTERIOR -> "Finition Habitacle"
        ScoreCategory.ENGINE -> "Bloc Moteur & Turbo"
        ScoreCategory.ELECTRICAL -> "Circuit Électrique"
        ScoreCategory.TRANSMISSION -> "Boîte de vitesses"
        ScoreCategory.SUSPENSION_BRAKES -> "Suspension & Freins"
        ScoreCategory.ROAD_TEST -> "Essai Routier"
    }

    val translated = when (lang) {
        "fr" -> when (category) {
            ScoreCategory.DOCUMENTS -> "Sûreté Doc."
            ScoreCategory.BODYWORK -> "Carrosserie"
            ScoreCategory.INTERIOR -> "Habitacle"
            ScoreCategory.ENGINE -> "Moteur"
            ScoreCategory.ELECTRICAL -> "Électricité"
            ScoreCategory.TRANSMISSION -> "Boîte vit."
            ScoreCategory.SUSPENSION_BRAKES -> "Châssis"
            ScoreCategory.ROAD_TEST -> "Essai route"
        }
        "ar" -> when (category) {
            ScoreCategory.DOCUMENTS -> "السجلات"
            ScoreCategory.BODYWORK -> "الهيكل"
            ScoreCategory.INTERIOR -> "الداخلية"
            ScoreCategory.ENGINE -> "المحرك"
            ScoreCategory.ELECTRICAL -> "الكهرباء"
            ScoreCategory.TRANSMISSION -> "القير"
            ScoreCategory.SUSPENSION_BRAKES -> "العفشة"
            ScoreCategory.ROAD_TEST -> "تجربة طريق"
        }
        "tn" -> when (category) {
            ScoreCategory.DOCUMENTS -> "الأوراق"
            ScoreCategory.BODYWORK -> "الكاروسري"
            ScoreCategory.INTERIOR -> "الداخلية"
            ScoreCategory.ENGINE -> "الموتور"
            ScoreCategory.ELECTRICAL -> "الكهرباء"
            ScoreCategory.TRANSMISSION -> "البوات"
            ScoreCategory.SUSPENSION_BRAKES -> "السسبنسيون"
            ScoreCategory.ROAD_TEST -> "تجربة الكياس"
        }
        else -> when (category) {
            ScoreCategory.DOCUMENTS -> "Documents"
            ScoreCategory.BODYWORK -> "Bodywork"
            ScoreCategory.INTERIOR -> "Interior"
            ScoreCategory.ENGINE -> "Engine"
            ScoreCategory.ELECTRICAL -> "Electrical"
            ScoreCategory.TRANSMISSION -> "Gearbox"
            ScoreCategory.SUSPENSION_BRAKES -> "Suspension"
            ScoreCategory.ROAD_TEST -> "Road Test"
        }
    }

    val icon = when (category) {
        ScoreCategory.DOCUMENTS -> Icons.Filled.Description
        ScoreCategory.BODYWORK -> Icons.Filled.MinorCrash
        ScoreCategory.INTERIOR -> Icons.Filled.Chair
        ScoreCategory.ENGINE -> Icons.Filled.Settings
        ScoreCategory.ELECTRICAL -> Icons.Filled.Bolt
        ScoreCategory.TRANSMISSION -> Icons.Filled.SwapVert
        ScoreCategory.SUSPENSION_BRAKES -> Icons.Filled.Build
        ScoreCategory.ROAD_TEST -> Icons.Filled.Speed
    }

    val fraction = score.toFloat() / maxWeight.toFloat()
    val progressColor = when {
        fraction >= 0.8f -> SafeGreen
        fraction >= 0.5f -> RacingOrange
        else -> CriticalRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$score/$maxWeight",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }
            Text(
                text = translated,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            LinearProgressIndicator(
                progress = fraction,
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun CategoryRowItem(
    category: ScoreCategory,
    score: Int,
    maxWeight: Int,
    lang: String
) {
    val title = when (category) {
        ScoreCategory.DOCUMENTS -> CarTranslate.get("DOCUMENTS", lang)
        ScoreCategory.BODYWORK -> "Tôlerie & Alignement"
        ScoreCategory.INTERIOR -> "Finition Habitacle"
        ScoreCategory.ENGINE -> "Bloc Moteur & Turbo"
        ScoreCategory.ELECTRICAL -> "Circuit Électrique"
        ScoreCategory.TRANSMISSION -> "Boîte de vitesses"
        ScoreCategory.SUSPENSION_BRAKES -> "Suspension & Freins"
        ScoreCategory.ROAD_TEST -> "Essai Routier"
    }

    val translated = when (lang) {
        "fr" -> when (category) {
            ScoreCategory.DOCUMENTS -> "Documents & Historique"
            ScoreCategory.BODYWORK -> "Carrosserie & Tôlerie"
            ScoreCategory.INTERIOR -> "Intérieur & Habitacle"
            ScoreCategory.ENGINE -> "Moteur & Échappements"
            ScoreCategory.ELECTRICAL -> "Électricité & Éclairage"
            ScoreCategory.TRANSMISSION -> "Boîte & Transmission"
            ScoreCategory.SUSPENSION_BRAKES -> "Amortisseurs & Freinage"
            ScoreCategory.ROAD_TEST -> "Essai sur route"
        }
        "ar" -> when (category) {
            ScoreCategory.DOCUMENTS -> "أوراق وسجلات صيانة السيارة"
            ScoreCategory.BODYWORK -> "صاج الشاسيه والرفارف الخارجية"
            ScoreCategory.INTERIOR -> "المقاعد والكهرباء والتحكم الداخلي"
            ScoreCategory.ENGINE -> "المحرك والكرنك والتبخر والشكمان"
            ScoreCategory.ELECTRICAL -> "البطارية والدينامو والتكييف"
            ScoreCategory.TRANSMISSION -> "ناقل الحركة والكلتش والقير"
            ScoreCategory.SUSPENSION_BRAKES -> "المساعدين والمقود وجهاز الفرامل"
            ScoreCategory.ROAD_TEST -> "تجربة واستقرار المركبة على الطريق"
        }
        "tn" -> when (category) {
            ScoreCategory.DOCUMENTS -> "أوراق وسجلات صيانة الكرهبة"
            ScoreCategory.BODYWORK -> "صاج والكاروسري والدهينة البرانية"
            ScoreCategory.INTERIOR -> "الصالون والفرش والتحكم الداخلي"
            ScoreCategory.ENGINE -> "الموتور والتوربو والشان والشكمان"
            ScoreCategory.ELECTRICAL -> "الضو والباتري والدينامو والكليم"
            ScoreCategory.TRANSMISSION -> "البوات والكلتش ونقل الحركة"
            ScoreCategory.SUSPENSION_BRAKES -> "الامورتيسورات والمقود ومجموعة الفرامل"
            ScoreCategory.ROAD_TEST -> "تجربة واستقرار الكرهبة في الكياس"
        }
        else -> title
    }

    val fraction = score.toFloat() / maxWeight.toFloat()
    val progressColor = when {
        fraction >= 0.8f -> SafeGreen
        fraction >= 0.5f -> RacingOrange
        else -> CriticalRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = translated,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$score / $maxWeight",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = fraction,
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
fun ExpandablePointsWidget(
    title: String,
    points: List<ChecklistItem>,
    lang: String,
    isWarn: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isWarn) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (isWarn) CriticalRed else SafeGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$title (${points.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (points.isEmpty()) {
                        Text(
                            text = "No system items recorded.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
                        )
                    } else {
                        points.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .padding(start = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (isWarn) Icons.Filled.ErrorOutline else Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = if (isWarn) CriticalRed else SafeGreen,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.title.get(lang),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isWarn) item.warningResult.get(lang) else item.normalResult.get(lang),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Share text report via intent
fun shareTextReport(
    context: Context,
    viewModel: InspectionViewModel,
    report: CalculatedReport,
    lang: String
) {
    val sb = StringBuilder()
    sb.append("🚗 CAR CHECK PRO REPORT 🚗\n")
    sb.append("===============================\n")
    sb.append("VEHICLE: ${viewModel.draftBrand.uppercase()} ${viewModel.draftModel.uppercase()}\n")
    sb.append("YEAR: ${viewModel.draftYear}\n")
    sb.append("FUEL: ${viewModel.draftFuel}\n")
    sb.append("TRANSMISSION: ${viewModel.draftTransmission}\n")
    sb.append("MILEAGE: ${viewModel.draftMileage} km\n")
    sb.append("===============================\n")
    sb.append("OVERALL SCORE: ${report.overallScore}/100\n")
    sb.append("FINAL RECOMMENDED VERDICT: ${report.verdict}\n")
    sb.append("ESTIMATED REPAIR RISK: ${report.repairCostRisk}\n")
    if (report.hasDealBreakers) {
        sb.append("🚨 WARNING: CRITICAL DEAL BREAKER DETECTED!\n")
        report.dealBreakersTriggered.forEach {
            sb.append(" - ${it.dealBreakerLabel?.get(lang) ?: it.title.get(lang)}\n")
        }
    }
    sb.append("===============================\n")
    sb.append("SCORING DETAILS:\n")
    report.scores.forEach { (cat, valScore) ->
        sb.append(" - $cat: $valScore\n")
    }

    if (viewModel.draftDtcCodes.isNotEmpty()) {
        sb.append("\nOBD TROUBLE CODES LOGGED: ${viewModel.draftDtcCodes.joinToString(", ")}\n")
    }

    sb.append("\nReport generated by Car Check Pro.")

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Car Check Pro Audit - ${viewModel.draftBrand} ${viewModel.draftModel}")
        putExtra(Intent.EXTRA_TEXT, sb.toString())
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share inspection report"))
}

// Open WhatsApp
fun openWhatsAppConsultation(
    context: Context,
    phone: String,
    viewModel: InspectionViewModel,
    report: CalculatedReport,
    lang: String
) {
    val message = "Bonjour, je viens de réaliser une inspection sur une ${viewModel.draftBrand} ${viewModel.draftModel} (${viewModel.draftYear}, ${viewModel.draftMileage}km) avec Car Check Pro.\nScore d'évaluation : ${report.overallScore}/100.\nVerdict recommandé : ${report.verdict}.\nSévérité des réparations : ${report.repairCostRisk}.\nPouvez-vous me donner votre avis d'expert ?"
    val urlStr = "https://api.whatsapp.com/send?phone=${phone.replace(" ", "").replace("+", "")}&text=${Uri.encode(message)}"

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlStr))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to standard dialer
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (err: Exception) {
            Toast.makeText(context, "Could not open dialer or WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }
}
