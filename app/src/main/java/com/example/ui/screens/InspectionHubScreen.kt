package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SectionType
import com.example.ui.InspectionViewModel
import com.example.ui.components.CarTranslate
import com.example.ui.components.MainGradientHeader
import com.example.ui.components.RacingOrange
import com.example.ui.components.SafeGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionHubScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onNavigateToSection: (SectionType) -> Unit,
    onNavigateToReport: () -> Unit
) {
    val lang = viewModel.currentLanguage
    val isDark = viewModel.isDarkMode
    val activeItems = viewModel.getActiveChecklistItems()
    val responses = viewModel.draftResponses.value

    // Calculate progression metrics
    val totalCount = activeItems.size
    val checkedCount = activeItems.count { responses[it.id] != "UNANSWERED" }
    val progressPercent = if (totalCount > 0) (checkedCount.toFloat() / totalCount.toFloat()) else 0f

    // Define Sections lists
    val sections = SectionType.values().toList()

    val sectionIcons = mapOf(
        SectionType.DOCUMENTS to Icons.Filled.Folder,
        SectionType.BODYWORK to Icons.Filled.DirectionsCar,
        SectionType.INTERIOR to Icons.Filled.AirlineSeatReclineExtra,
        SectionType.DASHBOARD_LIGHTS to Icons.Filled.Warning,
        SectionType.COLD_START to Icons.Filled.AcUnit,
        SectionType.ENGINE_IDLING to Icons.Filled.Settings,
        SectionType.TURBO to Icons.Filled.Speed,
        SectionType.COOLING to Icons.Filled.Thermostat,
        SectionType.ELECTRICAL to Icons.Filled.BatteryChargingFull,
        SectionType.AIR_CONDITIONING to Icons.Filled.WindPower,
        SectionType.TRANSMISSION to Icons.Filled.Merge,
        SectionType.DRIVELINE to Icons.Filled.AltRoute,
        SectionType.SUSPENSION_STEERING to Icons.Filled.TireRepair,
        SectionType.BRAKING to Icons.Filled.Stop,
        SectionType.TIRES to Icons.Filled.Info,
        SectionType.ROAD_TEST to Icons.Filled.CompassCalibration,
        SectionType.FLOOD_DAMAGE to Icons.Filled.Water,
        SectionType.FRAUD_DETECTION to Icons.Filled.Search,
        SectionType.OBD_SCANNER to Icons.Filled.DeveloperBoard
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${viewModel.draftBrand} ${viewModel.draftModel}".uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
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
            // Summary Info Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${viewModel.draftFuel} • ${viewModel.draftTransmission} • ${viewModel.draftMileage} km",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${viewModel.draftYear}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Progress Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = CarTranslate.get("progress", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$checkedCount / $totalCount",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progressPercent,
                        color = RacingOrange,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            // Grid of Sections
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 96.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(sections) { secType ->
                    // Exclude Turbo if turbo is NO
                    if (secType == SectionType.TURBO && !viewModel.draftTurbo) {
                        return@items
                    }

                    val titleText = when (secType) {
                        SectionType.DOCUMENTS -> CarTranslate.get("DOCUMENTS", lang) + " & History"
                        SectionType.BODYWORK -> "Tôlerie & Alignement"
                        SectionType.INTERIOR -> "Habitacle"
                        SectionType.DASHBOARD_LIGHTS -> "Témoins d'alerte KOEO"
                        SectionType.COLD_START -> "Démarrage à Froid"
                        SectionType.ENGINE_IDLING -> "Qualité Moteur Ralenti"
                        SectionType.TURBO -> "Sifflement / Fuite Turbo"
                        SectionType.COOLING -> "Témpérature & Durites"
                        SectionType.ELECTRICAL -> "Batterie & Démarreur"
                        SectionType.AIR_CONDITIONING -> "Climatisation Froide"
                        SectionType.TRANSMISSION -> "Boîte de vitesses"
                        SectionType.DRIVELINE -> "Cardans & Jeux"
                        SectionType.SUSPENSION_STEERING -> "Amortisseurs & Trains"
                        SectionType.BRAKING -> "Stablité Freinage & ABS"
                        SectionType.TIRES -> "Pneus & Dates DOT"
                        SectionType.ROAD_TEST -> "Essai Routier (90-130)"
                        SectionType.FLOOD_DAMAGE -> "Véhicule d'Inondé"
                        SectionType.FRAUD_DETECTION -> "Compteur trafiqué"
                        SectionType.OBD_SCANNER -> "Lecteur Diagnostic OBD"
                    }

                    // Local overrides for Arabic/French
                    val translatedTitle = when (lang) {
                        "fr" -> when (secType) {
                            SectionType.DOCUMENTS -> "1. Documents & Historique"
                            SectionType.BODYWORK -> "2. Tôlerie & Choc"
                            SectionType.INTERIOR -> "3. Habitacle & Finition"
                            SectionType.DASHBOARD_LIGHTS -> "4. Voyants Tableau (KOEO)"
                            SectionType.COLD_START -> "5. Démarrage à froid"
                            SectionType.ENGINE_IDLING -> "6. Moteur au ralenti"
                            SectionType.TURBO -> "7. Suralimentation Turbo"
                            SectionType.COOLING -> "8. Système de refroidissement"
                            SectionType.ELECTRICAL -> "9. Batterie / Démarrage"
                            SectionType.AIR_CONDITIONING -> "10. Climatisation"
                            SectionType.TRANSMISSION -> "11. Boîte de vitesses"
                            SectionType.DRIVELINE -> "12. Transmission / Cardans"
                            SectionType.SUSPENSION_STEERING -> "13. Suspension & Direction"
                            SectionType.BRAKING -> "14. Système de freinage"
                            SectionType.TIRES -> "15. Pneumatiques & DOT"
                            SectionType.ROAD_TEST -> "16. Essai routier dynamique"
                            SectionType.FLOOD_DAMAGE -> "17. Détection Inondation"
                            SectionType.FRAUD_DETECTION -> "18. Analyse de fraudes"
                            SectionType.OBD_SCANNER -> "19. Scanner Codes OBD"
                        }
                        "ar" -> when (secType) {
                            SectionType.DOCUMENTS -> "١. فحص الوثائق والتطابق"
                            SectionType.BODYWORK -> "٢. هيكل صاج السيارة والطلاء"
                            SectionType.INTERIOR -> "٣. الفرش والتحكم الداخلي"
                            SectionType.DASHBOARD_LIGHTS -> "٤. لمبات التحذير بالطبلون"
                            SectionType.COLD_START -> "٥. تشغيل المحرك البارد"
                            SectionType.ENGINE_IDLING -> "٦. الماكينة عند الوقوف"
                            SectionType.TURBO -> "٧. شاحن التربو وعزم الهواء"
                            SectionType.COOLING -> "٨. دورة تبريد الماكينة والرديتر"
                            SectionType.ELECTRICAL -> "٩. الكهرباء والمارش للسيارة"
                            SectionType.AIR_CONDITIONING -> "١٠. التكييف والتهوية"
                            SectionType.TRANSMISSION -> "١١. القير والفابريكة"
                            SectionType.DRIVELINE -> "١٢. الكرادين والعكوس"
                            SectionType.SUSPENSION_STEERING -> "١٣. المساعدين والمقصات"
                            SectionType.BRAKING -> "١٤. كفاءة الفرامل والـ ABS"
                            SectionType.TIRES -> "١٥. الكفرات والتواريخ والتآكل"
                            SectionType.ROAD_TEST -> "١٦. تجربة سيارة على سرعة"
                            SectionType.FLOOD_DAMAGE -> "١٧. غرق ومياه السيول"
                            SectionType.FRAUD_DETECTION -> "١٨. التلاعب بالممشى والرش"
                            SectionType.OBD_SCANNER -> "١٩. فحص كابل كمبيوتر OBD"
                        }
                        "tn" -> when (secType) {
                            SectionType.DOCUMENTS -> "1. الأوراق وجو الكرهبة ومطابقتها"
                            SectionType.BODYWORK -> "2. الكاروسري والدهينة والضربات"
                            SectionType.INTERIOR -> "3. الصالون وبلاستيك Habitacle"
                            SectionType.DASHBOARD_LIGHTS -> "4. فوات الطابلو ولمبات التحذير"
                            SectionType.COLD_START -> "5. تخديم الكرهبة وهي باردة"
                            SectionType.ENGINE_IDLING -> "6. حس الموتور وهو واقف"
                            SectionType.TURBO -> "7. ريجيم التوربو وهوا الموتور"
                            SectionType.COOLING -> "8. سيستام التبريد والرادياتور"
                            SectionType.ELECTRICAL -> "9. الضو والباتري والمارش"
                            SectionType.AIR_CONDITIONING -> "10. الكليماتيزور والتهوية"
                            SectionType.TRANSMISSION -> "11. بوات فيتاس ومشاكلها"
                            SectionType.DRIVELINE -> "12. الكردونات والترانزميسيون"
                            SectionType.SUSPENSION_STEERING -> "13. الامورتيسورات والمقصات"
                            SectionType.BRAKING -> "14. الفرامل وقوة الـ ABS"
                            SectionType.TIRES -> "15. العجالي وتاريخ الصنع والتآكل"
                            SectionType.ROAD_TEST -> "16. تجربة الكرهبة في الكياس"
                            SectionType.FLOOD_DAMAGE -> "17. معاينة الغرق ودخول الماء"
                            SectionType.FRAUD_DETECTION -> "18. ترافيك وبش تلاعب بالعداد"
                            SectionType.OBD_SCANNER -> "19. سكانير OBD وفيش دياغنوستيك"
                        }
                        else -> titleText // default english
                    }

                    // Count of item answers
                    val sectItems = activeItems.filter { it.section == secType }
                    val itemTotal = sectItems.size
                    val itemAnswered = sectItems.count { responses[it.id] != "UNANSWERED" }
                    val hasWarning = sectItems.any { responses[it.id] == "WARNING" }

                    SectionGridCard(
                        title = translatedTitle,
                        icon = sectionIcons[secType] ?: Icons.Filled.CheckBox,
                        answeredCount = itemAnswered,
                        totalCount = itemTotal,
                        hasWarning = hasWarning,
                        isChecklist = (secType != SectionType.OBD_SCANNER),
                        onClick = { onNavigateToSection(secType) }
                    )
                }
            }

            // Save & Report Button fixed at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // Generate report (save to DB first then view report)
                        viewModel.saveCurrentInspection { id ->
                            onNavigateToReport()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_generate_report"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Assessment, contentDescription = "Assessment")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CarTranslate.get("generate_report", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionGridCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    answeredCount: Int,
    totalCount: Int,
    hasWarning: Boolean,
    isChecklist: Boolean,
    onClick: () -> Unit
) {
    val progressColor = if (answeredCount == totalCount && totalCount > 0) MaterialTheme.colorScheme.primary else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("section_card_$title"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (hasWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                if (hasWarning) {
                    Icon(
                        imageVector = Icons.Filled.ReportGmailerrorred,
                        contentDescription = "Warning Present",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                } else if (isChecklist && answeredCount == totalCount && totalCount > 0) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Completed",
                        tint = SafeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 16.sp,
                maxLines = 2,
                modifier = Modifier.height(34.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            if (isChecklist) {
                Text(
                    text = "$answeredCount / $totalCount",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (answeredCount == totalCount) MaterialTheme.colorScheme.primary else Color.Gray
                )
            } else {
                Text(
                    text = "DTC Codes Checker",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}
