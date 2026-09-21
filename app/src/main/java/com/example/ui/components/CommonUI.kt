package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.ui.theme.Typography
import com.example.R

// Theme custom Colors
val CarbonDark = Color(0xFF1C1A1D)
val DarkSurface = Color(0xFF25232A)
val DarkSurfaceElevated = Color(0xFF49454F)
val LightBg = Color(0xFFFDF8F6)
val LightSurface = Color(0xFFFFFFFF)

val RacingOrange = Color(0xFFFF5722) // High Density Primary Racing Orange
val AccentAmber = Color(0xFFFFCC00) // High Density Secondary Amber
val SafeGreen = Color(0xFF2E7D32)
val CriticalRed = Color(0xFFD32F2F) // Vibrant Crimson Red

@Composable
fun CarTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (isDark) {
        darkColorScheme(
            primary = RacingOrange,
            secondary = AccentAmber,
            background = CarbonDark,
            surface = DarkSurface,
            onBackground = Color(0xFFE6E1E5),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = DarkSurfaceElevated,
            onSurfaceVariant = Color(0xFFCAC4D0),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondaryContainer = Color(0xFF332D41),
            onSecondaryContainer = Color(0xFFE8DEF8),
            error = CriticalRed,
            errorContainer = Color(0xFF8C1D18),
            onErrorContainer = Color(0xFFF9DEDC),
            outline = Color(0xFF938F99)
        )
    } else {
        lightColorScheme(
            primary = RacingOrange,
            secondary = AccentAmber,
            background = LightBg,
            surface = LightSurface,
            onBackground = Color(0xFF1D1B1E),
            onSurface = Color(0xFF1D1B1E),
            surfaceVariant = Color(0xFFF3EDF7),
            onSurfaceVariant = Color(0xFF49454F),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            error = CriticalRed,
            errorContainer = Color(0xFFF9DEDC),
            onErrorContainer = Color(0xFF410E0B),
            outline = Color(0xFFCAC4D0)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

object CarTranslate {
    private val translations = mapOf(
        "app_title" to mapOf("en" to "Car Check Pro", "fr" to "Car Check Pro", "ar" to "كار تشيك برو", "tn" to "كار تشيك برو (تونسي)"),
        "app_subtitle" to mapOf("en" to "Used Car Inspection Engine", "fr" to "Moteur de décision d'achat", "ar" to "مساعد فحص وتقييم السيارات المستعملة", "tn" to "فحص وتثبيت الكراهب المستعملة"),
        "past_inspections" to mapOf("en" to "My Inspections", "fr" to "Mes inspections", "ar" to "سجلات فحص السيارات", "tn" to "الفحوصات متاعي"),
        "no_inspections" to mapOf("en" to "No inspections recorded yet", "fr" to "Aucune inspection enregistrée", "ar" to "لا توجد عمليات فحص مسجلة بعد", "tn" to "ما فماش حتى فحص مسجل"),
        "start_new_inspection" to mapOf("en" to "New Inspection", "fr" to "Nouvelle inspection", "ar" to "بدء فحص سيارة جديدة", "tn" to "فحص جديد"),
        "settings" to mapOf("en" to "Admin config", "fr" to "Configuration", "ar" to "الإعدادات العامة", "tn" to "الإعدادات العامة"),
        "expert_phone" to mapOf("en" to "Consultant WhatsApp Phone", "fr" to "Téléphone de l'expert WhatsApp", "ar" to "رقم مستشار واتساب للفحص", "tn" to "رقم خبير الواتساب متاعنا"),
        "phone_placeholder" to mapOf("en" to "e.g. +33600000000", "fr" to "ex: +33600000000", "ar" to "مثال: +966500000000", "tn" to "مثال: 55348558"),
        "save" to mapOf("en" to "Save", "fr" to "Sauvegarder", "ar" to "حفظ التعديلات", "tn" to "حفظ التعديلات"),
        "cancel" to mapOf("en" to "Cancel", "fr" to "Annuler", "ar" to "إلغاء", "tn" to "إلغاء"),
        "brand" to mapOf("en" to "Brand", "fr" to "Marque", "ar" to "ماركة السيارة", "tn" to "الماركة"),
        "model" to mapOf("en" to "Model", "fr" to "Modèle", "ar" to "طراز وفئة السيارة", "tn" to "الموديل والنوع"),
        "year" to mapOf("en" to "Year of Manufacture", "fr" to "Année du véhicule", "ar" to "سنة الصنع", "tn" to "العام"),
        "fuel_type" to mapOf("en" to "Fuel Type", "fr" to "Carburant", "ar" to "نوع محرك الوقود", "tn" to "الوقود / مازوت ولا ليسانس"),
        "turbo" to mapOf("en" to "Turbocharged Engine?", "fr" to "Moteur Turbo ?", "ar" to "هل السيارة مزودة بشاحن تربو؟", "tn" to "فيها توربو؟"),
        "transmission" to mapOf("en" to "Transmission", "fr" to "Boîte de vitesses", "ar" to "ناقل الحركة (القير)", "tn" to "الفياتاس (البوات)"),
        "mileage" to mapOf("en" to "Mileage (km)", "fr" to "Kilométrage (km)", "ar" to "المسافة المقطوعة (الممشى)", "tn" to "الكيلومتراج (العداد)"),
        "next_step" to mapOf("en" to "Go to Inspection Hub", "fr" to "Aller au Centre d'inspection", "ar" to "الانتقال للوحة الفحص", "tn" to "تعدى للوح الفحص"),
        "yes" to mapOf("en" to "Yes", "fr" to "Oui", "ar" to "نعم / سليم", "tn" to "إي / سليم"),
        "no" to mapOf("en" to "No", "fr" to "Non", "ar" to "لا / به مشكلة", "tn" to "لا / فيه مشكلة"),
        "petrol" to mapOf("en" to "Petrol", "fr" to "Essence", "ar" to "بنزين", "tn" to "ليسانس"),
        "diesel" to mapOf("en" to "Diesel", "fr" to "Diesel", "ar" to "ديزل", "tn" to "مازوت"),
        "hybrid" to mapOf("en" to "Hybrid", "fr" to "Hybride", "ar" to "هجين", "tn" to "هجين (إيبريد)"),
        "ev" to mapOf("en" to "Electric (EV)", "fr" to "Électrique (EV)", "ar" to "كهربائي بالكامل", "tn" to "تريسينتي (الكتريك)"),
        "manual" to mapOf("en" to "Manual", "fr" to "Manuelle", "ar" to "قير عادي (يدوي)", "tn" to "مانويل (عادي)"),
        "automatic" to mapOf("en" to "Automatic", "fr" to "Automatique", "ar" to "قير أوتوماتيك", "tn" to "أوتوماتيك"),
        "back" to mapOf("en" to "Back", "fr" to "Retour", "ar" to "رجوع", "tn" to "رجوع"),
        "inspection_hub" to mapOf("en" to "Inspection Hub", "fr" to "Centre d'inspection", "ar" to "لوحة التحكم بالأقسام", "tn" to "لوحة الأقسام"),
        "generate_report" to mapOf("en" to "Generate Decision Report", "fr" to "Générer le rapport final", "ar" to "توليد التقرير والقرار", "tn" to "خرج تقرير الشريان"),
        "overall_score" to mapOf("en" to "Overall Score", "fr" to "Score global", "ar" to "التقييم الإجمالي للسيارة", "tn" to "السكور العام للكرهبة"),
        "verdict" to mapOf("en" to "Final Verdict", "fr" to "Verdict d'achat", "ar" to "قرار وتوصية الشراء", "tn" to "القرار النهائي متاعنا"),
        "buy" to mapOf("en" to "BUY", "fr" to "ACHETER", "ar" to "شراء", "tn" to "اشري وعينك مغمضة"),
        "negotiate" to mapOf("en" to "NEGOTIATE", "fr" to "NÉGOCIER", "ar" to "تفاوض على السعر", "tn" to "طيح في السوم"),
        "avoid" to mapOf("en" to "AVOID", "fr" to "ÉVITER", "ar" to "تجنب الشراء", "tn" to "أخطاك / ابعد عليها"),
        "risk_level" to mapOf("en" to "Risk Level", "fr" to "Niveau de risque", "ar" to "مستوى جدارة الاستثمار", "tn" to "درجة المخاطرة"),
        "repair_risk" to mapOf("en" to "Estimated Repair Risk", "fr" to "Frais de réparation estimés", "ar" to "الإنفاق التقريبي المتوقع للإصلاح", "tn" to "قداش بش تصرف تصليح"),
        "obd_scanner" to mapOf("en" to "DTC OBD Scanner", "fr" to "Lecteur codes défaut OBD2", "ar" to "محلل أكواد الأعطال OBD-II", "tn" to "سكانير الكرهبة (OBD)"),
        "add_code" to mapOf("en" to "Analyze Code", "fr" to "Analyser le code", "ar" to "تحليل الكود", "tn" to "حلل الكود"),
        "deal_breaker_alert" to mapOf("en" to "🚨 HIGH RISK DEAL BREAKER DETECTED", "fr" to "🚨 REFUS IMMÉDIAT DÉTECTÉ", "ar" to "🚨 تم اكتشاف عيب مانع للشراء", "tn" to "🚨 خطر خايب ياسرdetected"),
        "deal_breaker_descr" to mapOf(
            "en" to "Critical structural or mechanical defects detected. Highly recommended to AVOID buying.",
            "fr" to "Défauts critiques de châssis ou de moteur trouvés. Achat fortement déconseillé !",
            "ar" to "تم كشف عيوب خطيرة في الشاصي أو المحرك تمنع الشراء وتطلب تجنبها.",
            "tn" to "فما ديفو خايب ياسر في الشاصي ولا الموتور. أخطاك منها خير!"
        ),
        "consult_expert" to mapOf("en" to "Need a professional opinion before buying?", "fr" to "Besoin d'un avis d'expert avant d'acheter ?", "ar" to "تحتاج لرأي ميكانيكي خبير قبل الشراء؟", "tn" to "تحب تسقسي ميكانيكي قبل ما تشري؟"),
        "expert_pitch" to mapOf(
            "en" to "Request a live safety audit call or share this analysis with our technician on WhatsApp.",
            "fr" to "Appelez-nous gratuitement ou partagez ce rapport d'inspection avec notre technicien WhatsApp.",
            "ar" to "أرسل التقرير المولد بنقرة واحدة واحصل على استشارة ميكانيكية فورية مع خبيرنا الفني عبر واتساب.",
            "tn" to "ابعث تقرير الفحص هذا للخبير متاحنا على الواتساب وخذ رايو بالوقت."
        ),
        "call_whatsapp" to mapOf("en" to "WhatsApp Consultation", "fr" to "Consulter par WhatsApp", "ar" to "مستشار الفحص الفوري عبر واتساب", "tn" to "شاور الخبير في الواتساب بالوقت"),
        "share_report" to mapOf("en" to "Share Inspection Report", "fr" to "Partager le rapport d'inspection", "ar" to "مشاركة وتصدير ملف التقرير", "tn" to "بارطاجي تقرير الفحص"),
        "positive_points" to mapOf("en" to "Satisfactory Systems", "fr" to "Points d'inspection réussis", "ar" to "الأنظمة والأجزاء السليمة", "tn" to "الحاجات الباهية والنظيفة"),
        "negative_points" to mapOf("en" to "Issues & Warnings", "fr" to "Défauts & points d'alerte", "ar" to "العيوب والملاحظات الفنية المكتشفة", "tn" to "الديفوات والمشاكل اللي فما"),
        "delete" to mapOf("en" to "Delete Record", "fr" to "Supprimer la fiche", "ar" to "حذف السجل", "tn" to "فسخ الفحص هذا"),
        "not_applicable" to mapOf("en" to "Not applicable", "fr" to "Non applicable", "ar" to "غير منطبق", "tn" to "خارج الحسبة"),
        "progress" to mapOf("en" to "Inspection Progress", "fr" to "Progression du contrôle", "ar" to "مستوى تقدم الفحص", "tn" to "قداش كملنا فحص"),
        "view_report" to mapOf("en" to "Open Analysis", "fr" to "Ouvrir le rapport", "ar" to "استعراض التقرير", "tn" to "حل تقرير الفحص"),
        "enter_dtc" to mapOf("en" to "Enter OBD Fault (e.g. P0300)", "fr" to "Saisir code diagnostic (ex: P0300)", "ar" to "أدخل كود الأعطال المكتشف (مثال: P0300)", "tn" to "أكتب كود الدياغنوستيك (مثلا P0300)"),
        "severity" to mapOf("en" to "Severity", "fr" to "Sévérité du problème", "ar" to "مستوى خطورة الكود", "tn" to "قوة ديفو الكود"),
        "impact" to mapOf("en" to "Impact on purchase", "fr" to "Impact sur l'achat", "ar" to "التأثير على قرار شراء لسيارة", "tn" to "تأثيرها عل الشريان"),
        "normal" to mapOf("en" to "NORMAL", "fr" to "NORMAL", "ar" to "سليم / طبيعي", "tn" to "مريجل / نظيف"),
        "warning" to mapOf("en" to "WARNING", "fr" to "ANOMALIE", "ar" to "تحذير / خلل", "tn" to "فيه ديفو / خلل"),
        "unanswered" to mapOf("en" to "Not checked", "fr" to "Non contrôlé", "ar" to "لم يتم فحصه بعد", "tn" to "ما زال ما تثبتش"),
        "how_to_inspect" to mapOf("en" to "HOW TO CHECK", "fr" to "COMMENT CONTRÔLER", "ar" to "كيفية وطريقة الفحص الفعلي", "tn" to "كيفاش تثبت وتفحص بيدك"),
        "normal_res_title" to mapOf("en" to "NORMAL STATE", "fr" to "RÉSULTAT NORMAL", "ar" to "النتيجة السليمة والطبيعية", "tn" to "الحالة العادية والنظيفة"),
        "warn_res_title" to mapOf("en" to "WARNING SIGNS", "fr" to "DÉFAUT / SIGNAL D'ALERTE", "ar" to "دلالات التحذير والعيوب", "tn" to "العلامات اللي تفيقك بالديفوه"),
        "all_done" to mapOf("en" to "Complete", "fr" to "Terminé", "ar" to "مكتمل", "tn" to "كامل / مريجل"),
        "DOCUMENTS" to mapOf("en" to "Documents", "fr" to "Documents", "ar" to "الوثائق", "tn" to "الأوراق والوثائق"),
        "voice_guide_recorded" to mapOf("en" to "Your Voice Guide", "fr" to "Votre guide vocal", "ar" to "تسجيلك الصوتي المخصص", "tn" to "التسجيل الصوتي متاعك"),
        "voice_guide_placeholder" to mapOf("en" to "Ready to play instructions", "fr" to "Prêt à écouter les instructions", "ar" to "جاهز للاستماع الآن", "tn" to "حاضر باش تسمعو"),
        "record_voice_guide" to mapOf("en" to "Record Guide", "fr" to "Enregistrer un guide", "ar" to "سجّل دليلك الصوتي", "tn" to "سجّل صوتك هوني"),
        "stop_recording" to mapOf("en" to "Stop", "fr" to "Arrêter", "ar" to "إيقاف", "tn" to "قص التسجيل"),
        "recording_active" to mapOf("en" to "Recording...", "fr" to "Enregistrement...", "ar" to "جاري التسجيل...", "tn" to "قاعد يسجل...")
    )

    fun get(key: String, lang: String): String {
        return translations[key]?.get(lang) ?: key
    }
}

@Composable
fun MainGradientHeader(
    title: String,
    subtitle: String,
    isDarkMode: Boolean,
    action: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isDarkMode) {
                        listOf(RacingOrange.copy(alpha = 0.15f), Color.Transparent)
                    } else {
                        listOf(RacingOrange.copy(alpha = 0.08f), Color.Transparent)
                    }
                )
            )
            .padding(16.dp)
            .padding(top = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Professional App Logo integrated into the visual context
                Image(
                    painter = painterResource(id = R.drawable.img_app_logo_1782908571597),
                    contentDescription = "Car Check Pro Logo",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
            if (action != null) {
                Box(modifier = Modifier.padding(start = 8.dp)) {
                    action()
                }
            }
        }
    }
}

@Composable
fun ChecklistIllustration(
    itemId: String,
    isDark: Boolean,
    lang: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = remember(itemId) {
        val sanitizedId = itemId.lowercase()
            .replace("-", "_")
            .replace(" ", "_")
        context.resources.getIdentifier("img_$sanitizedId", "drawable", context.packageName)
    }

    if (imageResId != 0) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) Color(0xFF49454F) else Color(0xFFEADDFF)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Car check $itemId real photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Bottom end overlay badge specifying it's a real photo
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(0.65f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (lang == "ar") "صورة حقيقية للفحص" else if (lang == "tn") "تصويرة حقيقية مالفحص" else if (lang == "fr") "Photo réelle de contrôle" else "REAL INSPECTION PHOTO",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        val containerBg = if (isDark) {
            Color(0xFF2C2830)
        } else {
            Color(0xFFF5EEF8)
        }
        val primaryColor = RacingOrange
        val secondaryColor = if (isDark) Color(0xFFEADDFF) else Color(0xFF6750A4)
        val orangeAccent = Color(0xFFFF9800)
        
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp),
            colors = CardDefaults.cardColors(containerColor = containerBg),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                if (isDark) Color(0xFF49454F) else Color(0xFFEADDFF)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                val w = size.width
                val h = size.height
                val centerX = w / 2f
                val centerY = h / 2f
                
                when {
                    itemId.startsWith("doc") -> {
                        // Document sheet drawing of checklists
                        val paperW = w * 0.35f
                        val paperH = h * 0.85f
                        val paperX = centerX - paperW / 2f
                        val paperY = centerY - paperH / 2f
                        
                        drawRoundRect(
                            color = if (isDark) Color(0xFF38343C) else Color.White,
                            topLeft = Offset(paperX, paperY),
                            size = Size(paperW, paperH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                        drawRoundRect(
                            color = primaryColor.copy(0.25f),
                            topLeft = Offset(paperX, paperY),
                            size = Size(paperW, paperH),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        
                        // Header bar
                        drawRoundRect(
                            color = primaryColor.copy(0.8f),
                            topLeft = Offset(paperX + 6.dp.toPx(), paperY + 6.dp.toPx()),
                            size = Size(paperW - 12.dp.toPx(), 8.dp.toPx()),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )
                        
                        // Lines of text
                        var lineY = paperY + 18.dp.toPx()
                        while (lineY < paperY + paperH - 10.dp.toPx()) {
                            drawLine(
                                color = if (isDark) Color.White.copy(0.3f) else Color.Gray.copy(0.3f),
                                start = Offset(paperX + 8.dp.toPx(), lineY),
                                end = Offset(paperX + paperW - (if (lineY % 3 == 0f) 18.dp.toPx() else 8.dp.toPx()), lineY),
                                strokeWidth = 1.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            lineY += 6.dp.toPx()
                        }
                        
                        if (itemId == "doc_vin") {
                            // Magnifying glass hovering check
                            val magX = centerX + 18.dp.toPx()
                            val magY = centerY + 10.dp.toPx()
                            val magRadius = 8.dp.toPx()
                            drawCircle(
                                color = primaryColor,
                                radius = magRadius,
                                center = Offset(magX, magY),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawLine(
                                color = primaryColor,
                                start = Offset(magX + magRadius * 0.7f, magY + magRadius * 0.7f),
                                end = Offset(magX + magRadius * 1.6f, magY + magRadius * 1.6f),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        } else if (itemId == "doc_double_keys") {
                            // Two key fobs
                            val keyX = centerX + 18.dp.toPx()
                            val keyY = centerY - 10.dp.toPx()
                            drawRoundRect(
                                color = if (isDark) Color.Black else Color(0xFF3C3840),
                                topLeft = Offset(keyX, keyY),
                                size = Size(12.dp.toPx(), 22.dp.toPx()),
                                cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                            )
                            drawRoundRect(
                                color = if (isDark) Color(0xFF4A454F) else Color(0xFFC4C0C8),
                                topLeft = Offset(keyX + 3.dp.toPx(), keyY - 6.dp.toPx()),
                                size = Size(6.dp.toPx(), 6.dp.toPx())
                            )
                        } else {
                            // Stamp
                            drawCircle(
                                color = Color(0xFF388E3C).copy(0.85f),
                                radius = 5.dp.toPx(),
                                center = Offset(paperX + paperW - 6.dp.toPx(), paperY + paperH - 6.dp.toPx())
                            )
                        }
                    }
                    
                    itemId.startsWith("body") || itemId.startsWith("chassis") -> {
                        // Drawing side projection outline of car chassis
                        val carL = w * 0.65f
                        val carH = h * 0.35f
                        val carStartX = (w - carL) / 2f
                        val carStartY = centerY + 4.dp.toPx() - (carH / 2f)
                        
                        val carPath = Path().apply {
                            moveTo(carStartX, carStartY + carH * 0.7f)
                            lineTo(carStartX + carL * 0.04f, carStartY + carH * 0.45f)
                            lineTo(carStartX + carL * 0.12f, carStartY + carH * 0.45f)
                            lineTo(carStartX + carL * 0.28f, carStartY + carH * 0.42f)
                            lineTo(carStartX + carL * 0.42f, carStartY)
                            lineTo(carStartX + carL * 0.65f, carStartY)
                            lineTo(carStartX + carL * 0.78f, carStartY + carH * 0.45f)
                            lineTo(carStartX + carL * 0.94f, carStartY + carH * 0.45f)
                            lineTo(carStartX + carL * 0.98f, carStartY + carH * 0.55f)
                            lineTo(carStartX + carL * 0.98f, carStartY + carH * 0.75f)
                            lineTo(carStartX + carL * 0.85f, carStartY + carH * 0.75f)
                            
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    carStartX + carL * 0.65f, 
                                    carStartY + carH * 0.5f, 
                                    carStartX + carL * 0.81f, 
                                    carStartY + carH * 0.9f
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            
                            lineTo(carStartX + carL * 0.35f, carStartY + carH * 0.75f)
                            
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    carStartX + carL * 0.15f, 
                                    carStartY + carH * 0.5f, 
                                    carStartX + carL * 0.31f, 
                                    carStartY + carH * 0.9f
                                ),
                                startAngleDegrees = 0f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )
                            lineTo(carStartX, carStartY + carH * 0.75f)
                            close()
                        }
                        
                        drawPath(
                            path = carPath,
                            color = primaryColor.copy(alpha = 0.12f)
                        )
                        drawPath(
                            path = carPath,
                            color = primaryColor,
                            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                        
                        // Wheel hubs
                        drawCircle(
                            color = if (isDark) Color(0xFF4A454F) else Color(0xFF5A566E),
                            radius = 7.dp.toPx(),
                            center = Offset(carStartX + carL * 0.23f, carStartY + carH * 0.72f)
                        )
                        drawCircle(
                            color = if (isDark) Color(0xFF4A454F) else Color(0xFF5A566E),
                            radius = 7.dp.toPx(),
                            center = Offset(carStartX + carL * 0.73f, carStartY + carH * 0.72f)
                        )
                        
                        // Focus pointer highlight
                        val highlightColor = Color(0xFFD32F2F)
                        when (itemId) {
                            "body_alignment", "body_bolts" -> {
                                drawCircle(
                                    color = highlightColor,
                                    radius = 5.dp.toPx(),
                                    center = Offset(carStartX + carL * 0.22f, carStartY + carH * 0.44f),
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                                drawCircle(
                                    color = highlightColor,
                                    radius = 5.dp.toPx(),
                                    center = Offset(carStartX + carL * 0.82f, carStartY + carH * 0.46f),
                                    style = Stroke(width = 1.5.dp.toPx())
                                )
                            }
                            "body_paint" -> {
                                drawCircle(
                                    color = highlightColor,
                                    radius = 12.dp.toPx(),
                                    center = Offset(carStartX + carL * 0.5f, carStartY + carH * 0.3f),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                            "body_magnet" -> {
                                drawCircle(
                                    color = highlightColor,
                                    radius = 6.dp.toPx(),
                                    center = Offset(carStartX + carL * 0.48f, carStartY + carH * 0.5f),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                            "body_windows" -> {
                                drawLine(
                                    color = Color(0xFF0288D1),
                                    start = Offset(carStartX + carL * 0.35f, carStartY + 2.dp.toPx()),
                                    end = Offset(carStartX + carL * 0.7f, carStartY + 2.dp.toPx()),
                                    strokeWidth = 2.5.dp.toPx()
                                )
                            }
                            "body_spare_wheel", "body_trunk_floor" -> {
                                val trunkX = carStartX + carL * 0.86f
                                val trunkY = carStartY + carH * 0.56f
                                drawRect(
                                    color = highlightColor.copy(0.2f),
                                    topLeft = Offset(trunkX - 10.dp.toPx(), trunkY),
                                    size = Size(20.dp.toPx(), 10.dp.toPx())
                                )
                                drawRect(
                                    color = highlightColor,
                                    topLeft = Offset(trunkX - 10.dp.toPx(), trunkY),
                                    size = Size(20.dp.toPx(), 10.dp.toPx()),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                            "body_chassis_rails" -> {
                                val railY = carStartY + carH * 0.74f
                                drawLine(
                                    color = highlightColor,
                                    start = Offset(carStartX + 8.dp.toPx(), railY),
                                    end = Offset(carStartX + carL - 8.dp.toPx(), railY),
                                    strokeWidth = 3.dp.toPx()
                                )
                            }
                        }
                    }
                    
                    itemId.startsWith("int") -> {
                        // Drawing interior cockpit tools
                        when (itemId) {
                            "int_steering_wear" -> {
                                val sRadius = 24.dp.toPx()
                                drawCircle(
                                    color = if (isDark) Color(0xFF4A454F) else Color(0xFF5A566E),
                                    radius = sRadius,
                                    center = Offset(centerX, centerY),
                                    style = Stroke(width = 5.dp.toPx())
                                )
                                drawCircle(
                                    color = primaryColor,
                                    radius = 8.dp.toPx(),
                                    center = Offset(centerX, centerY)
                                )
                                drawLine(
                                    color = if (isDark) Color(0xFF4A454F) else Color(0xFF5A566E),
                                    start = Offset(centerX - sRadius + 2.dp.toPx(), centerY),
                                    end = Offset(centerX + sRadius - 2.dp.toPx(), centerY),
                                    strokeWidth = 3.5.dp.toPx()
                                )
                                drawLine(
                                    color = if (isDark) Color(0xFF4A454F) else Color(0xFF5A566E),
                                    start = Offset(centerX, centerY),
                                    end = Offset(centerX, centerY + sRadius - 2.dp.toPx()),
                                    strokeWidth = 3.5.dp.toPx()
                                )
                            }
                            "int_gear_knob_wear" -> {
                                drawLine(
                                    color = if (isDark) Color.White.copy(0.4f) else Color.Gray,
                                    start = Offset(centerX - 10.dp.toPx(), centerY + 20.dp.toPx()),
                                    end = Offset(centerX, centerY - 10.dp.toPx()),
                                    strokeWidth = 3.5.dp.toPx()
                                )
                                drawCircle(
                                    color = primaryColor,
                                    radius = 10.dp.toPx(),
                                    center = Offset(centerX, centerY - 10.dp.toPx())
                                )
                                // Draw layout on knob
                                drawCircle(color = Color.White.copy(0.4f), radius = 6.dp.toPx(), center = Offset(centerX, centerY - 10.dp.toPx()))
                            }
                            "int_pedal_wear" -> {
                                val pW = 10.dp.toPx()
                                val pH = 20.dp.toPx()
                                drawRoundRect(
                                    color = if (isDark) Color(0xFF322E36) else Color(0xFF49454E),
                                    topLeft = Offset(centerX - 12.dp.toPx(), centerY - pH / 2),
                                    size = Size(pW + 2.dp.toPx(), pH),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                                drawRoundRect(
                                    color = if (isDark) Color(0xFF322E36) else Color(0xFF49454E),
                                    topLeft = Offset(centerX + 8.dp.toPx(), centerY - pH / 2 + 2.dp.toPx()),
                                    size = Size(pW - 2.dp.toPx(), pH - 4.dp.toPx()),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                            }
                            else -> {
                                val bW = w * 0.4f
                                val bH = h * 0.45f
                                drawRoundRect(
                                    color = if (isDark) Color(0xFF38343C) else Color(0xFFEADDFF),
                                    topLeft = Offset(centerX - bW / 2, centerY - bH / 2),
                                    size = Size(bW, bH),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                                drawCircle(
                                    color = primaryColor,
                                    radius = 6.dp.toPx(),
                                    center = Offset(centerX, centerY)
                                )
                            }
                        }
                    }
                    
                    itemId.startsWith("engine") || itemId.startsWith("cooling") || itemId.startsWith("turbo") -> {
                        when {
                            itemId.contains("smoke") -> {
                                val pW = w * 0.22f
                                val px = centerX - 25.dp.toPx()
                                val py = centerY
                                drawLine(
                                    color = if (isDark) Color(0xFFC4C0C8) else Color(0xFF49454F),
                                    start = Offset(px, py),
                                    end = Offset(px + pW, py),
                                    strokeWidth = 5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                                drawCircle(
                                    color = primaryColor.copy(0.2f),
                                    radius = 8.dp.toPx(),
                                    center = Offset(px + pW + 6.dp.toPx(), py - 1.dp.toPx())
                                )
                                drawCircle(
                                    color = primaryColor.copy(0.12f),
                                    radius = 12.dp.toPx(),
                                    center = Offset(px + pW + 16.dp.toPx(), py - 3.dp.toPx())
                                )
                            }
                            itemId.contains("dipstick") -> {
                                drawCircle(
                                    color = Color(0xFFFFC107),
                                    radius = 6.dp.toPx(),
                                    center = Offset(centerX - 20.dp.toPx(), centerY - 6.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                                drawLine(
                                    color = if (isDark) Color.White.copy(0.6f) else Color.Gray,
                                    start = Offset(centerX - 14.dp.toPx(), centerY - 6.dp.toPx()),
                                    end = Offset(centerX + 25.dp.toPx(), centerY + 10.dp.toPx()),
                                    strokeWidth = 1.2.dp.toPx()
                                )
                                drawCircle(
                                    color = Color(0xFF8D6E63),
                                    radius = 3.dp.toPx(),
                                    center = Offset(centerX + 22.dp.toPx(), centerY + 9.dp.toPx())
                                )
                            }
                            itemId.contains("turbo") -> {
                                val turboPath = Path().apply {
                                    addArc(
                                        oval = androidx.compose.ui.geometry.Rect(
                                            centerX - 12.dp.toPx(), 
                                            centerY - 12.dp.toPx(), 
                                            centerX + 12.dp.toPx(), 
                                            centerY + 12.dp.toPx()
                                        ),
                                        startAngleDegrees = 0f,
                                        sweepAngleDegrees = 310f
                                    )
                                }
                                drawPath(
                                    path = turboPath,
                                    color = primaryColor,
                                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawLine(
                                    color = primaryColor,
                                    start = Offset(centerX + 2.dp.toPx(), centerY + 12.dp.toPx()),
                                    end = Offset(centerX + 20.dp.toPx(), centerY + 12.dp.toPx()),
                                    strokeWidth = 2.5.dp.toPx()
                                )
                                drawCircle(
                                    color = orangeAccent,
                                    radius = 5.dp.toPx(),
                                    center = Offset(centerX, centerY)
                                )
                            }
                            else -> {
                                val bW = 16.dp.toPx()
                                val bH = 30.dp.toPx()
                                drawRoundRect(
                                    color = if (isDark) Color(0xFF38343C) else Color(0xFFEADDFF),
                                    topLeft = Offset(centerX - bW / 2, centerY - bH / 2),
                                    size = Size(bW, bH),
                                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                                )
                                drawLine(
                                    color = primaryColor,
                                    start = Offset(centerX, centerY + 8.dp.toPx()),
                                    end = Offset(centerX + 8.dp.toPx(), centerY + 16.dp.toPx()),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                    }
                    
                    else -> {
                        // Standard generic dashboard meter representation
                        val bW = w * 0.5f
                        val bH = h * 0.4f
                        drawRoundRect(
                            color = primaryColor.copy(alpha = 0.08f),
                            topLeft = Offset(centerX - bW / 2, centerY - bH / 2),
                            size = Size(bW, bH),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                        drawLine(
                            color = primaryColor.copy(0.4f),
                            start = Offset(centerX - bW / 2 + 8.dp.toPx(), centerY),
                            end = Offset(centerX + bW / 2 - 8.dp.toPx(), centerY),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(0.45f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (lang == "ar") "مُخطّط توضيحي للفحص" else if (lang == "fr") "Schéma de contrôle" else "VISUAL SCHEMATIC GUIDE",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
}
