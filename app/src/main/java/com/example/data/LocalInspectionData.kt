package com.example.data

enum class FuelType {
    PETROL, DIESEL, HYBRID, EV
}

enum class TransmissionType {
    MANUAL, AUTOMATIC
}

enum class SectionType {
    DOCUMENTS,
    BODYWORK,
    INTERIOR,
    DASHBOARD_LIGHTS,
    COLD_START,
    ENGINE_IDLING,
    TURBO,
    COOLING,
    ELECTRICAL,
    AIR_CONDITIONING,
    TRANSMISSION,
    DRIVELINE,
    SUSPENSION_STEERING,
    BRAKING,
    TIRES,
    ROAD_TEST,
    FLOOD_DAMAGE,
    FRAUD_DETECTION,
    OBD_SCANNER
}

enum class ScoreCategory {
    DOCUMENTS,
    BODYWORK,
    INTERIOR,
    ENGINE,
    ELECTRICAL,
    TRANSMISSION,
    SUSPENSION_BRAKES,
    ROAD_TEST
}

data class TranslationText(
    val en: String,
    val fr: String,
    val ar: String
) {
    fun get(lang: String): String = when (lang.lowercase()) {
        "fr" -> fr
        "ar" -> ar
        else -> en
    }
}

data class ChecklistItem(
    val id: String,
    val section: SectionType,
    val scoreCategory: ScoreCategory,
    val title: TranslationText,
    val howToCheck: TranslationText,
    val normalResult: TranslationText,
    val warningResult: TranslationText,
    val isDealBreaker: Boolean = false,
    val dealBreakerLabel: TranslationText? = null,
    val conditionalFuel: List<FuelType>? = null,
    val conditionalTurbo: Boolean? = null,
    val conditionalTransmission: List<TransmissionType>? = null
)

data class OdbCodeInfo(
    val code: String,
    val meaning: TranslationText,
    val severity: String, // "HIGH", "MEDIUM", "LOW"
    val description: TranslationText,
    val impact: TranslationText
)

object LocalInspectionData {

    val OBD_DATABASE = mapOf(
        "P0300" to OdbCodeInfo(
            "P0300",
            TranslationText("Random/Multiple Cylinder Misfire", "Ratés d'allumage aléatoires/multiples", "خلل عشوائي/متعدد في شرارة الاحتراق (ميسفاير)"),
            "HIGH",
            TranslationText("Engine control module detects misfires in multiple cylinders. Can damage catalytic converter.", "Le module moteur détecte des ratés dans plusieurs cylindres. Risque d'endommager le catalyseur.", "الكمبيوتر يكتشف عدم احتراق الوقود في عدة سلندرات. قد يتلف في دبة التلوث."),
            TranslationText("AVOID unless price accommodates a major ignition/fuel restoration. High risk.", "À ÉVITER sauf si le prix permet une réfection moteur. Risque élevé.", "تجنب الشراء إلا إذا كان السعر منخفضاً جداً ويغطي تكلفة الإصلاح.")
        ),
        "P0420" to OdbCodeInfo(
            "P0420",
            TranslationText("Catalyst System Efficiency Below Threshold", "Efficacité du système de catalyseur inférieure au seuil", "كفاءة دبة التلوث أقل من الحد المطلوب"),
            "MEDIUM",
            TranslationText("Catalytic converter is degraded or there is an exhaust leak.", "Le catalyseur est dégradé ou il y a une fuite d'échappement.", "دبة التلوث تالفة أو هناك تسريب في نظام العادم."),
            TranslationText("NEGOTIATE. Replacement of catalytic converter is expensive.", "À NÉGOCIER. Le remplacement du catalyseur est coûteux.", "تفاوض على السعر. استبدال دبة التلوث مكلف.")
        ),
        "P0171" to OdbCodeInfo(
            "P0171",
            TranslationText("System Too Lean (Bank 1)", "Mélange trop pauvre (Ligne 1)", "خليط الوقود فقير جداً (رقيق)"),
            "MEDIUM",
            TranslationText("Too much air or too little fuel detected. Common causes: vacuum leak, MAF sensor, fuel pump.", "Trop d'air ou manquent de carburant. Fuite d'air ou débitmètre défectueux.", "دخول هواء زائد أو وقود غير كافٍ. الأسباب الشائعة: تسريب هواء أو حساس الهواء."),
            TranslationText("NEGOTIATE. Repair cost ranges from light sensor swap to fuel pump replacement.", "À NÉGOCIER. Coût de réparation variable (capteur simple à pompe à essence).", "تفاوض على السعر. قد يكون حساساً بسيطاً أو طرمبة بنزين.")
        ),
        "P0299" to OdbCodeInfo(
            "P0299",
            TranslationText("Turbocharger Underboost", "Sous-alimentation du turbocompresseur", "انخفاض ضغط التربو"),
            "HIGH",
            TranslationText("Turbocharger output pressure is low. Potential wastegate failure, boost leak, or bad turbo carcass.", "La pression de suralimentation du turbo est faible. Soupape ou turbo défectueux.", "ضغط التربو ضعيف. قد يكون هناك تسريب هواء أو تلف في التوربينات للتربو."),
            TranslationText("AVOID or inspect turbo carefully. Can cost $1500+ to fix.", "DÉCISION IMPORTANTE: inspecter le turbo. Remplacement très onéreux (1500€+).", "تجنب أو افحص التربو بحذر. تكلفة الإصلاح تتجاوز 1500 دولار.")
        ),
        "P0700" to OdbCodeInfo(
            "P0700",
            TranslationText("Transmission Control System Malfunction", "Dysfonctionnement du système de contrôle de la transmission", "خلل في نظام التحكم بالقير (ناقل الحركة)"),
            "HIGH",
            TranslationText("General fault code indicating problem in transmission control computer. Often accompanied by solenoid/clutch faults.", "Code de défaut général de la boîte de vitesses. Souvent lié à des solénoïdes défaillants ou embrayage usé.", "خلل عام في نظام التحكم بالقير، وغالباً ما يكون مصاحباً للمشاكل الميكانيكية داخل القير."),
            TranslationText("AVOID. Automatic transmission overhaul or replacement is extremely costly.", "À ÉVITER. La réfection d'une boîte automatique est extrêmement coûteuse.", "تجنب في حال كان القير أوتوماتيكياً وينزلق، التكلفة باهظة جداً.")
        ),
        "P0340" to OdbCodeInfo(
            "P0340",
            TranslationText("Camshaft Position Sensor Circuit Malfunction", "Défaut de circuit du capteur de position d'arbre à cames", "خلل في دائرة حساس الكام شفت"),
            "MEDIUM",
            TranslationText("Error with the camshaft position sensor. Can cause rough idle, misfires or starting failure.", "Dysfonctionnement du capteur d'arbre à cames. Peut causer des démarrages difficiles.", "خلل في قراءة حساس وضعية عمود الكامات، يؤدي لصعوبة التشغيل والتفتفة."),
            TranslationText("NEGOTIATE. Typically a simple sensor replacement, but verify timing belt/chain is intact.", "À NÉGOCIER. Généralement un changement de capteur facile, vérifier l'état de la courroie.", "تفاوض على السعر. عادة حساس بسيط ولكن يجب التأكد من سلامة الجنزير/الصدر.")
        )
    )

    val CHECKLIST_ITEMS = listOf(
        // DOCUMENTS (Weight 10)
        ChecklistItem(
            "doc_vin", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("VIN corresponds with registration", "VIN correspond à la carte grise", "تطابق رقم الشاسيه (VIN) مع الاستمارة"),
            TranslationText("Check the VIN stamp on windshield/door jamb and compare with vehicle documents.", "Vérifier le numéro VIN frappé à froid et le comparer à la carte grise.", "قارن رقم الشاسيه المطبوع على الزجاج أو القائم مع المكتوب في الاستمارة."),
            TranslationText("VIN matches perfectly", "Le VIN correspond en tout point", "متطابق تماماً وبدون أي تعديل"),
            TranslationText("VIN mismatch or signs of grinding/modification", "Différence ou traces de meulage sur le VIN", "عدم تطابق رقم الشاسيه أو وجود آثار كشط وتعديل عليه"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("VIN Mismatch (Possible Stolen / Fraud Vehicle)", "Incohérence du VIN (Risque de fraude ou véhicule volé)", "عدم تطابق رقم الشاسيه (شبهة سرقة أو تزوير)")
        ),
        ChecklistItem(
            "doc_history", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("Service book availability", "Présence du carnet d'entretien", "وجود دفتر الصيانة"),
            TranslationText("Ask for the service book and verify stamps and dates.", "Demander le carnet d'entretien, vérifier les dates et tampons.", "اطلب دفتر الصيانة وتأكد من التواريخ والأختام للوكيل أو الورش."),
            TranslationText("Full booklet with regular logs", "Carnet complet et bien tamponné", "دفتر مكتمل بسجلات منتظمة"),
            TranslationText("Missing booklet or suspicious gaps", "Carnet absent ou gros trous d'entretien", "دفتر مفقود أو به فجوات صيانة غير مبررة")
        ),
        ChecklistItem(
            "doc_invoices", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("Maintenance invoices", "Factures d'entretien fournies", "فواتير الصيانة والقطع"),
            TranslationText("Inspect recent receipts for timing belt, clutch, or major repairs.", "Examiner les factures récentes pour la distribution, l'embrayage, etc.", "راجع الفواتير الأخيرة لقطع الغيار مثل الجنزير، الصوف، أو الفحمات."),
            TranslationText("Abundant, clear invoices tracing owners", "Factures détaillées traçant l'historique", "فواتير ميكانيكية غزيرة وواضحة تؤكد الصيانة"),
            TranslationText("No invoices available (lack of repair history)", "Aucune facture d'entretien disponible", "لا توجد أي فواتير تؤكد القيام بالصيانة")
        ),
        ChecklistItem(
            "doc_ct", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("Technical inspection report", "Contrôle technique disponible", "تقرير الفحص الدوري"),
            TranslationText("Read the legal technical test report from less than 6 months ago.", "Consulter le rapport officiel du CT datant de moins de 6 mois.", "اطلع على فحص السيارة الدوري الصادر خلال 6 أشهر ماضية."),
            TranslationText("Available with minor/no annotations", "Rapport disponible, vierge ou défauts mineurs", "متوفر وسليم بدون ملاحظات خطيرة"),
            TranslationText("Report missing, expired, or lists critical defects", "Rapport absent, périmé ou défauts critiques mentionnés", "مفقود، منتهي الصلاحية أو به عيوب تؤثر على السلامة")
        ),
        ChecklistItem(
            "doc_mileage_consistent", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("Mileage consistency", "Cohérence du kilométrage", "اتساق قراءة العداد ميكانيكياً"),
            TranslationText("Check service documents/inspection reports for chronological increase list.", "Consulter l'historique administratif ou les fiches pour valider la chronologie.", "تحقق من زيادة الكيلومترات بشكل متتابع في أوراق الصيانة والفحص الدوري."),
            TranslationText("Stamps showcase smooth mileage progression", "Progression du kilométrage logique et fluide", "العداد يرتفع بشكل واقعي وتدريجي"),
            TranslationText("Incoherent jumps, rollback signs, or missing logs", "Sauts incohérents, signes de manipulation ou baisse de km", "تراجع في القراءة أو فجوة كبيرة تثير الشبهة")
        ),
        ChecklistItem(
            "doc_double_keys", SectionType.DOCUMENTS, ScoreCategory.DOCUMENTS,
            TranslationText("Spare double keys", "Présence du double des clés", "وجود المفتاح البديل (السبير)"),
            TranslationText("Inspect if both electronic keys are available and work.", "S'assurer de la présence et du bon fonctionnement de deux clés.", "اطلب المفتاح الثاني وأكد عمل الأزرار فيه."),
            TranslationText("Two functional keys present", "Double des clés fonctionnel fourni", "يتوفر مفتاحان يعملان بكفاءة"),
            TranslationText("Only one single key (replacement costs $150+)", "Clé unique fournie (coût de reproduction élevé)", "مفتاح واحد فقط (تكلفة بديله مرتفعة)")
        ),

        // BODYWORK & ACCIDENTS (Weight 20)
        ChecklistItem(
            "body_alignment", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Body panel gaps alignment", "Alignement de la carrosserie", "وزنية الفراغات وتناسق الرفارف والكبوت"),
            TranslationText("Inspect gap spacing between plates on hood, doors, bumpers, trunk.", "Vérifier l'alignement et les interstices du capot, des portes, du coffre.", "افحص الفراغات الفاصلة بين الكبوت والرفارف، والأبواب والشنطة من الجهتين."),
            TranslationText("Even, symmetric panel gaps (factory condition)", "Interstices réguliers et parfaitement symétriques", "الفراغات متساوية ومتناسقة تماماً كحالة المصنع"),
            TranslationText("Asymmetric gaps, uneven panels (signs of hit/crash)", "Jours irréguliers, pièces décalées (signes d'accident re-vissé)", "فجوات غير متساوية أو ميلان تشير لتعرض لضربة من قبل")
        ),
        ChecklistItem(
            "body_magnet", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Magnet test for body filler", "Test de l'aimant (mastic masqué)", "اختبار المغناطيس (كشف معجون التلقيم)"),
            TranslationText("Glide a flexible magnet on fenders, door panels, and lower rocker panels.", "Faire glisser un petit aimant sur les ailes, portes et bas de caisse.", "مرر مغناطيساً خفيفاً على الرفارف والأبواب والزوائد السفلية."),
            TranslationText("Magnet stays secure on all metal sheets", "L'aimant tient fermement partout sur les tôles", "المغناطيس يمسك بثبات في كل أجزاء الصاج"),
            TranslationText("Magnet falls off in specific spots (heavy body filler)", "L'aimant tombe ou glisse (présence suspecte de gros mastic)", "المغناطيس يفلت في بعض الأماكن بسبب معجون السمكرة الكثيف")
        ),
        ChecklistItem(
            "body_paint", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Paint color and finish consistency", "Uniformité de la peinture", "تناسق لون ولمعة الدهان ورش التجميلي"),
            TranslationText("Inspect panels in direct sunlight. Search for orange peel texture or color shade differences.", "Observer les reflets au soleil, chercher des coulures ou différences de teinte.", "تفحص لمعان الدهان في الشمس، وابحث عن اختلاف درجة اللون أو رش زائد."),
            TranslationText("Uniform paint color and factory paint depth", "Teinte uniforme et reflet d'origine homogène", "اللون متطابق تماماً واللمعة واحدة في كافة الأجزاء"),
            TranslationText("Color mismatch, overspray on gaskets, or paint sagging", "Différence de ton notable, brouillard de peinture sur joints", "وجود اختلاف درجات، آثار بوية على الربلات أو أطراف النوافذ")
        ),
        ChecklistItem(
            "body_windows", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Window dating stamps consistency", "Dates d'origine des vitrages", "تناسق تواريخ تصنيع زجاج النوافذ"),
            TranslationText("Inspect the safety tiny seal text/numbers in corners of all windows and compare digits.", "Vérifier les codes cryptés gravés sur chaque vitre. Ils doivent coïncider.", "تحقق من الرمز والتواريخ المطبوعة أسفل كل زجاج وقارنها مع البقية."),
            TranslationText("All safety stamps indicate identical date of manufacture", "Toutes les vitres portent des marquages d'époque homogènes", "جميع النوافذ تحمل نفس تاريخ وجيل تصنيع السيارة"),
            TranslationText("One or multiple windows replaced (possible roll/crash)", "Vitrage remplacé (pare-brise ou vitres latérales non d'origine)", "أحد الزجاجات أو أكثر مستبدل (قد يدل على انقلاب أو كسر)")
        ),
        ChecklistItem(
            "body_seatbelts", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Seatbelt manufacturing dates", "Dates d'étiquettes des ceintures", "تواريخ تصنيع أحزمة الأمان"),
            TranslationText("Pull all belts to limits and check manufacturing date on labels.", "Tirer les ceintures au maximum et vérifier la date sur l'étiquette.", "اسحب الأحزمة حتى نهايتها واقرأ تاريخ الصنع المدون على اللصيقات."),
            TranslationText("Belt dates are aligned with car's production year", "Les dates des ceintures coïncident avec l'année du châssis", "تاريخ صنع الأحزمة يطابق تماماً شهر وسنة صنع السيارة"),
            TranslationText("Belts replaced, or safety tags cut off (possible airbag deploy)", "Ceintures changées ou étiquettes coupées (freins d'accident)", "استبدال الأحزمة أو قص لصقات الأمان (مؤشر تشغيل وتفجير إيرباق)")
        ),
        ChecklistItem(
            "body_spare_wheel", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Spare wheel well chassis floor", "Compartiment de la roue de secours", "صاج حوض السبير (العجلة الاحتياطية)"),
            TranslationText("Remove spare tire and check for crumpled metal or non-factory sealant.", "Enlever la roue de secours, examiner la tôle du fond et les joints.", "ارفع العجلة الاحتياطية وتفحص صاج الأرضية والمعجون واللحامات."),
            TranslationText("Flat sheet, clean seam sealant, factory welds", "Tôle parfaitement plate, joints d'origine réguliers", "الصاج مستوٍ تماماً ومعجون اللحام نظيف كالمصنع"),
            TranslationText("Crumpled metal, rust, raw welds, water accumulation", "Plis de tôle, mastic frotté, traces de soudure ou eau au fond", "انثنايات في الصاج، آثار تعديل لحام أو دخول مياه وصدأ"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Rear End Collision (Crumpled Chassis Floor)", "Choc arrière sévère (Plis de coffre / Soudure de fond)", "حادث خلفي مؤثر (انثنائيات في صاج الشنطة الخلفي)")
        ),
        ChecklistItem(
            "body_trunk_floor", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Trunk floor & side welding", "Planchers de coffre et soudures latérales", "أرضية وجوانب شنطة السيارة الخلفية"),
            TranslationText("Inspect inner side panel joints inside trunk underneath carpets.", "Inspecter le dessous du coffre et les raccords d'ailes arrières.", "ابحث عن آثار فك أو لحام داخلي في بطانة الشنطة وجوانبها."),
            TranslationText("Original uniform metal sheet and seam lines", "Soudures par points d'origine et masticage fluide", "لحام النقط الأصلي للمصنع سليم بدون تعديل"),
            TranslationText("Visible repair folds, hammering patterns or bad welds", "Déformations, plis, traces de marteau ou cordon de soudure", "وجود طعجات، آثار دوش ومطرقة السمكرة أو لحام أكسجين")
        ),
        ChecklistItem(
            "body_chassis_rails", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Chassis rail distortion check", "État des longerons (sans déformation)", "سلامة شاسيهات السيارة (الشاصي / الجسور طولي)"),
            TranslationText("Inspect main front and rear frame rails from underneath.", "Observer les longerons (avant / arrière) depuis le bas du moteur ou pont.", "افحص الكامرات والجسور الطولية للشاصي من الأمام والخلف."),
            TranslationText("Straight frame rails with no weld repairs", "Longerons lisses, sans déformation ni soudure", "الجسور سليمة ومستقيمة ولا يوجد آثار قص أو تعديل"),
            TranslationText("Distortion, welds, clamp marks or structural bends", "Placements de marbre visibles, fissures, traverses pliées", "اعوجاج، كسور، لحامات أو آثار فك سحب على البارد"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Core Structural Distortion (Bent Frame Rails)", "Déformation de structure majeure (Longerons pliés / Marbre)", "تأثر شاسيه السيارة الرئيسي (انحناء أو قص في الجسر/الشاصي)")
        ),
        ChecklistItem(
            "body_bolts", SectionType.BODYWORK, ScoreCategory.BODYWORK,
            TranslationText("Body panels bolts tools marks", "Vis des éléments de carrosserie (Traces de démontage)", "مسامير فك وتركيب أجزاء الهيكل الخارجية"),
            TranslationText("Check hood side mounting bolts, doors hinges, and fenders.", "Examiner la tête des boulons de fixation des ailes et du capot.", "تفحص مسامير تثبيت الرفارف، الكبوت والأبواب."),
            TranslationText("Intact original paint on bolt heads with no wear", "Peinture intègre sur les angles des têtes de vis", "دهان مسامير التثبيت سليم وخالٍ من آثار المفاتيح والخدوش"),
            TranslationText("Scratched paint, tooling marks, or missing washers", "Têtes de vis écaillées, métal usé par clés plates (re-montage)", "تأكل بوية المسمار، آثار فك وخدش بالسبانة دلالة على استبدال")
        ),

        // INTERIOR (Weight 10)
        ChecklistItem(
            "int_dash_condition", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Dashboard physical condition", "État du tableau de bord", "حالة طبلون السيارة العام"),
            TranslationText("Inspect dashboard for tears, cracks, fading or loose components.", "Vérifier l'absence de fissures, décoloration ou pièces déboîtées.", "تفحص الطبلون بحثاً عن شقوق، تقشر أو أجزاء غير مثبتة."),
            TranslationText("Pristine cockpit with solid trim fits", "Tableau de bord impeccable et ajustements parfaits", "طبلون سليم تماماً وبدون أي شروخ أو ارتخاء"),
            TranslationText("Cracks, sun fading, non-original drill holes", "Plastiques blanchis, fissures de soleil ou trous de vis", "تشققات بسبب الشمس، تقشر أو ثقوب إلكترونيات مضافة")
        ),
        ChecklistItem(
            "int_dash_disassembly", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Dashboard disassembly markers", "Traces de démontage du tableau de bord", "آثار فك وتجميع الطبلون"),
            TranslationText("Check for pry tool scratch marks near vents, clusters & screens.", "Inspecter le contour des aérateurs et de l'écran (marques de levier).", "تحقق من حواف حواضن المكيف الطبلون والعدادات لكشف آثار الفك."),
            TranslationText("Flawless plastic margins with zero scratch marks", "Jonctions de plastiques parfaites, aucun outil passé", "الحواف متطابقة والربط قوي بلا أي علامات كشط"),
            TranslationText("Broken clips, raw pry marks, or misaligned screens", "Clips cassés, plastique décollé ou tordu, vis manquantes", "أطراف بلاستيكية مشروخة، فراغات عوجاء أو مسامير مفقودة")
        ),
        ChecklistItem(
            "int_steering_wear", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Steering wheel wear", "Usure du volant", "مستوى تأكل واهتراء المقود"),
            TranslationText("Compare steering wheel leather/plastic gloss with displayed mileage.", "Vérifier si l'usure du cuir/plastique est cohérente avec les kilomètres.", "قارن اهتراء ملمس ولمعة المقود مع العداد التقديري للسيارة."),
            TranslationText("Texture normal for the odometer rating", "Usure harmonieuse et normale du grain du cuir", "ملمس المقود طبيعي ومناسب لمسافة الممشى المعلن"),
            TranslationText("Heavily polished or completely bald at low displayed km", "Volant pelé, ultra-lisse alors que le véhicule affiche peu de km", "المقود مستهلك واهترائه شديد مقارنة بممشى منخفض")
        ),
        ChecklistItem(
            "int_gear_knob_wear", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Gear shifter knob wear", "Usure du pommeau de vitesse", "اهتراء عصا القير (مقبض السرعات)"),
            TranslationText("Check the wear level on gear shifts and markings clarity.", "Examiner l'usure de la poignée des vitesses et des chiffres.", "تحقق من بهتان رسومات وملمس قبضة عصا القير."),
            TranslationText("Clear shifting pattern markings, firm handle", "Marquages lisibles, pommeau propre et non dégradé", "العصا متماسكة والأرقام مرسومة بوضوح وبلا اهتراء مفرط"),
            TranslationText("Worn markings, peeled off chrome or loose mechanism", "Chiffres effacés, cuir d'origine déchiré ou levier qui a du jeu libre", "الأرقام ممسوحة، تآكل شديد أو ارتجاج في المقبض")
        ),
        ChecklistItem(
            "int_pedal_wear", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Pedal rubber pads wear", "Usure des caoutchoucs de pédales", "تأكل ربلات دواسات الوقود والفرامل"),
            TranslationText("Inspect if brake and clutch rubbers are heavily worn out or brand new.", "Regarder si le relief des caoutchoucs de frein/embrayage est effacé.", "تفحص تآكل الحفر والنقوش في مطاط دواسات الفريد والدبرياج."),
            TranslationText("Moderate rubber details and textures", "Grip des pédales présent et usure normale", "الربلات موجودة ونقوشها طبيعية"),
            TranslationText("Metal showing through, or suspicious brand new replacements", "Caoutchouc percé à vif (métal apparent) ou pédales suspectes de neuf", "الحديد ظاهر للعيان من تحت الربلة، أو مستبدلة بربلة جديدة تثير الممشى")
        ),
        ChecklistItem(
            "int_seat_wear", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Driver's seat bolster wear", "Usure du siège conducteur (affaissement)", "هبوط واهتراء مرتبة السائق"),
            TranslationText("Sit down and feel the foam resilience and inspection of left side bolster.", "Vérifier le maintien dorsal et l'usure du flanc gauche du siège.", "اجلس على المقعد وتأكد من توازن اسفنج وعدم وجود تمزق بالجانب."),
            TranslationText("Firm support, intact seams and original fabric/leather", "Mousse toujours ferme, cuir non fripé ni percé", "المرتبة مريحة ومتماسكة والجلد غير ممزق أو متهرئ"),
            TranslationText("Sunken foam, torn leather, or completely broken spring frame", "Mousse affaissée (on sent la barre), assise décousue", "هبوط شديد في الاسفنج، تشققات وجلد ممزق أو انكسار الشاصي للمقعد")
        ),
        ChecklistItem(
            "int_water_infiltration", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Water leakage exploration", "Infiltration d'eau / Taches", "استكشاف تسريب ودخول المياه"),
            TranslationText("Check floor carpets underneath passenger mats and inside door card pockets.", "Tâter la moquette sous les surtapis à l'avant et à l'arrière.", "المس وافحص أسفل السجاد في الجهات الأربع وتحت المقاعد."),
            TranslationText("Completely dry floor everywhere", "Moquette parfaitement sèche et saine", "الأرضية جافة تماماً في كافة الزوايا والمقاعد"),
            TranslationText("Damp underlays, salt outlines, or wet padding", "Moquette trempée, traces de sel, aureoles d'inondation", "وجود رطوبة أو تجمعات مياه تشير لتسريب أو بقايا غسيل")
        ),
        ChecklistItem(
            "int_humidity_smell", SectionType.INTERIOR, ScoreCategory.INTERIOR,
            TranslationText("Smell inspection (mold or fuel)", "Mesure des odeurs (moisissure / carburant)", "فحص الروائح (رطوبة، عفن أو وقود)"),
            TranslationText("Close all windows and smell the cabin after it sat locked in the sun.", "Sentir l'habitacle fermé après exposition au chaud.", "أغلق النوافذ واستنشق هواء المقصورة بعد بقائها مغلقة في الشمس."),
            TranslationText("Neutral smell or faint freshener", "Pas d'odeur désagréable", "رائحة طبيعية أو معطر هواء عادي"),
            TranslationText("Stinky mold smell list, damp mustiness, or raw gas/oil smell", "Forte odeur de moisi, de chien mouillé ou vapeurs d'essence", "رائحة عفن رطوبة نفاذة، أو روائح بنزين وزيت محترق داخلية")
        ),
        ChecklistItem(
            "int_electrical_equip", SectionType.INTERIOR, ScoreCategory.ELECTRICAL,
            TranslationText("All electrical accessories check", "Fonctionnement des équipements électriques", "فحص عمل كافة الملحقات الكهربائية"),
            TranslationText("Test every window switch, mirrors motors, door lock, screens and wipers.", "Activer vitres, rétros, centralisation, radio, lumières et essuie-glaces.", "قم بتجربة جميع النوافذ الكهربائية، تحريك المرايا، السنترلاك، المساحات والأنوار."),
            TranslationText("Every accessory reacts immediately", "Tout fonctionne sans saccade du premier coup", "جميع الأزرار والملحقات تعمل بكفاءة تامة وفوراً"),
            TranslationText("Multiple broken buttons, dead windows, sluggish mirrors", "Vitres bloquées, boutons inactifs, essuie-glace poussif", "تعطل النوافذ، بطء حركة المرايا أو مشاكل في شاشة التحكم")
        ),

        // DASHBOARD WARNING SYSTEM (Weight 10 mapped - part of Electric)
        ChecklistItem(
            "dash_lights_koeo", SectionType.DASHBOARD_LIGHTS, ScoreCategory.ELECTRICAL,
            TranslationText("Warning lights sequence (Key ON - Engine OFF)", "Allumage des voyants (Contact sans démarrer)", "اختبار لمبات الطبلون (الافتتاحية قبل التشغيل KOEO)"),
            TranslationText("Turn ignition key ON but don't start the engine. Verify Check Engine, ABS, Airbag, ESP, Battery, Oil, and Glow Plug (Diesel) lights illuminate and then extinguish logically.", "Mettre le contact sans lancer le démarreur. Assurer la présence des témoins ABS, Airbag, Check Engine, Batterie, Huile.", "أدر المفتاح لوضعية التوصيل بدون تشغيل المحرك وتأكد من عمل كافة اللمبات ثم انطفائها بعد البدء."),
            TranslationText("All regulatory lights shine bright. No dead zones.", "Tous les voyants réglementaires s'allument", "جميع لمبات التحذير (ABS، الإيرباق، الماكينة) تعمل بوضوح"),
            TranslationText("Warning lights missing! Possible dashboard manipulation to hide faults.", "Voyant obligatoire déconnecté ! Cache de panne possible.", "لمبة تحذير أو أكثر لا تعمل! (وجود شبهة تلاعب لإخفاء الأعطال)"),
            isDealBreaker = false
        ),

        // COLD START (Weight 25 - part of Engine)
        ChecklistItem(
            "engine_start_cold", SectionType.COLD_START, ScoreCategory.ENGINE,
            TranslationText("Instant cold startup", "Démarrage immédiat à froid", "التشغيل والكرنك من أول دقة للمحرك"),
            TranslationText("Start the engine after hours of rest. Note cranking time speed.", "Démarrer le moteur à froid. Noter le temps de lancement.", "شغل المحرك بعد وقوف طويل واستمع لسرعة ومدة تشغيل السلف."),
            TranslationText("Fires up instantly (under 2 seconds) with steady starter speed", "Démarre instantanément en moins de 2 secondes", "تشتغل مباشرة وخلال ثانيتين مع قوة صوت التشغيل"),
            TranslationText("Slow cranking, multiple tries, heavy metallic starter strain", "Lancement pénible, plusieurs tentatives nécessaires", "صعوبة في الكرنك، محاولات متكررة أو ثقل وبطء ملحوظ")
        ),
        ChecklistItem(
            "engine_smoke_color", SectionType.COLD_START, ScoreCategory.ENGINE,
            TranslationText("Exhaust smoke colors assessment", "Couleur des fumées d'échappement", "لون دخان الشكمان بعد التشغيل"),
            TranslationText("Inspect tailpipe smoke closely during initial crank and first minutes of idle. Blue = oil burning, persistent white = coolant burning, excessive black = combustion issue.", "Observer la fumée au ralenti. Bleue = huile brûlée, blanche persistante = fuite eau, noire = mauvaise combustion.", "راقب لون ونوع الدخان الخارج من الشكمان: أزرق = احتراق زيت، أبيض مستمر = ماء، أسود = مشكلة احتراق."),
            TranslationText("Clear exhaust, standard water condensation mist", "Gaz transparent, vapeur d'eau légère qui s'estompe", "شفاف تماماً، أو بخار ماء عادي يتلاشى مع الحرارة"),
            TranslationText("Persistent colored smoke (BLUE / GREY / BLACK / WHITE)", "Fumée suspecte permanente (Bleue, Blanche, Noire)", "وجود دخان متصاعد مستمر (أزرق، أسود كثيف، أو أبيض كثيف)"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Persistent Exhaust Smoke (Engine / Turbo Burn)", "Fumée d'échappement suspecte (Moteur / Turbo usé)", "دخان شكمان مستمر (مؤشر احتراق زيت / ماء أو عطل تربو)")
        ),

        // ENGINE IDLING (Weight 25 - part of Engine)
        ChecklistItem(
            "engine_oil_cap_blowby", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Oil cap pressure (Blow-by)", "Sous-pression au bouchon d'huile", "فحص تبخر وضغط غطاء محرك الزيت (Blow-by)"),
            TranslationText("Loosen the oil cap on idle. Place it loose over the hole. Does it fly off?", "Dévisser le bouchon d'huile moteur au ralenti. Reste-t-il posé ?", "افتح غطاء الزيت والمحرك يعمل، ضعه بشكل خفيف فوق الفوهة."),
            TranslationText("Cap vibrates gently but stays on. Slight suction felt", "Le bouchon sautille légèrement sans s'envoler (aspiration)", "الغطاء يهتز بلطف مكانه ويظل ثابتاً (دلالة على ضغط طبيعي)"),
            TranslationText("Cap gets projected off, heavy gas blowing out", "Le bouchon est éjecté ou forte fumée grise s'échappe (moteur usé)", "المحرك يقذف الغطاء بقوة، مع تصاعد دخان رمادي كثيف (ضغط بستم تالف)"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Severe Blow-By (Worn Piston Rings / Engine)", "Forte surpression carter (Moteur rincé / segments hors service)", "فشل ضغط البساتم وتصاعد تهريب تبخر الزيت الزائد")
        ),
        ChecklistItem(
            "engine_coolant_mayo", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Coolant tank / 'Mayo' test", "Présence de mayonnaise (Liquide de refroidissement)", "فحص خلط زيت المحرك مع الماء (المطحنة / المايونيز)"),
            TranslationText("Inspect underneath the oil cap and inside the radiator expansion tank.", "Ouvrir le vase d'expansion et le bouchon de remplissage d'huile.", "افتح غطاء راديتر ماء وغطاء الزيت للتأكد من خلوه من الرغوة."),
            TranslationText("Pure oil under the cap, clean coolant in the tank", "Huile pure brune ou noire, liquide de refroidissement limpide", "الزيت نظيف ولونه طبيعي، والماء نقي وخالٍ من الرواسب"),
            TranslationText("Thick yellow/creamy emulsion ('mayonnaise') inside caps", "Pâte crémeuse marron/jaune (Chambre de culasse / joint HS)", "وجود رغوة صفراء كريمية كالمعجون (خلط ماء وزيت - كارتير الماكينة)"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Blown Head Gasket (Mayonnaise Detected)", "Joint de culasse HS (Mayonnaise et mélange d'eau dans l'huile)", "تلف كارتير الماكينة / خلط رأس المحرك (مايونيز وتلف المحرك)")
        ),
        ChecklistItem(
            "engine_bubbles_expansion", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Bubbles inside coolant expansion tank", "Bulles dans le vase d'expansion", "ارتجاج وفقاعات في وعاء ماء الرديتر"),
            TranslationText("Watch the open cooling tank as engine warms up. Are there continuous rising gas bubbles?", "Vérifier le liquide de refroidissement au ralenti. Y a-t-il des lamberts ?", "راقب وعاء الماء وهو يعمل، هل تتصاعد فقاعات هواء مستمرة؟"),
            TranslationText("Steady liquid level, no bubbles emerging", "Liquide stable sans aucun bouillonnement", "الماء راكد أو يتحرك بهدوء بدون اندفاع فقاعات هوائية"),
            TranslationText("Continuous heavy bubbles (combustion gases escaping to coolant)", "Bulles d'air continues (gaz de combustion poussés dans l'eau)", "فقاعات مستمرة تدل على تهريب هواء الكبس من غرف الاحتراق للراديتر"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Combustion Gas in Cooling System (Headed Gasket/Crack)", "Passage de compression dans l'eau (Joint de culasse / culasse fissurée)", "تسريب غازات الاحتراق لدورة التبريد (تلف رأس الماكينة)")
        ),
        ChecklistItem(
            "engine_fluid_leaks", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Severe mechanical fluid leaks", "Fuites de fluides majeures", "وجود تهريب وفوادان زيوت وسوائل"),
            TranslationText("Search engine block, transmission seals and ground beneath for puddles/drips.", "Inspecter le compartiment moteur et le sol pour repérer des gouttes actives.", "تحقق من أسفل المحرك والقير ومنطقة الراديتير لوجود بقع أو تهريب نشط."),
            TranslationText("Dry metal blocks, negligible oil sweating", "Moteur sec, pas de coulure grasse fraîche", "الماكينة والكرسي جافين تماماً، ولا توجد رطوبة زيتية"),
            TranslationText("Active dripping of engine oil, coolant, or transmission fluid", "Fuite importante ou coulure abondante de liquide de frein/boîte/direction", "تهريب زيت نشط بالتنقيط أو تهريب ماء واضح"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Active Severe Fluid Leak", "Fuite de fluide critique (Risque d'incendie ou casse mécanique)", "تهريب سوائل نشط وخطير (يهدد بتلف أجزاء المحرك والقير)")
        ),
        ChecklistItem(
            "engine_oil_dipstick", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Oil level and quality", "Niveau et qualité d'huile", "مستوى وجودة زيت المحرك"),
            TranslationText("Pull out dipstick, check oil level is between MIN-MAX, and see if there are metal particles.", "Tirer la jauge, vérifier le niveau et l'absence de paillettes métalliques.", "اسحب مقياس الزيت، تأكد من مستواه، وعدم وجود حبات برادة حديد لمعة."),
            TranslationText("Level within bounds, amber/black fluid, no metallic flakes", "Huile pure brune/noire, niveau parfait, aucune paillette", "الزيت ممتاز ومستواه طبيعي وخالٍ تماماً من برادة الحديد"),
            TranslationText("Dry dipstick, or shiny metallic flakes in oil (mechanical wear)", "Jauge sèche ou présence de limaille métallique brillante (Casse moteur)", "المحرك فارغ من الزيت، أو وجود برادة حديد لامعة (مؤشر تآكل البلوك)")
        ),
        ChecklistItem(
            "engine_belts_mounts", SectionType.ENGINE_IDLING, ScoreCategory.ENGINE,
            TranslationText("Belts & Engine mounts vibrations", "Courroies et supports moteur", "حالة السيور وكراسي الماكينة والارتجاج"),
            TranslationText("Check serpentine belt for cracks. Observe mount bushings for extreme shake.", "Inspecter les craquelures sur la courroie et les oscillations des silentblocs.", "افحص سير المحرك بحثاً عن تشققات، واهتزاز الماكينة المفرط."),
            TranslationText("Steady engine, quiet belt rotations", "Moteur stable, courroie souple non craquelée", "الماكينة مستقرة وهادئة عند التشغيل، والسرير مرن وسليم"),
            TranslationText("Cracked belt, or heavy idle vibration shaking cabin", "Courroie très craquelée ou support cassé qui fait trembler l'habitacle", "سير متشقق بشكل حرج، أو ارتجاج قوي للسيارة بسبب كسر كراسي الماكينة")
        ),

        // TURBO (Weight 25 Engine - CONDITIONAL: YES to turbo)
        ChecklistItem(
            "turbo_scream", SectionType.TURBO, ScoreCategory.ENGINE,
            TranslationText("Turbo high-pitched screeching", "Bruit de sifflement mécanique du Turbo", "صفير التربو أو خشونة صوته"),
            TranslationText("Rev the engine up under load. Listen for a loud 'siren' sound.", "Écouter le turbo à l'accélération (bruit de sirène de police).", "استمع لعمل التربو مع الدعس، هل صوته يشبه صفير الإسعاف الحاد؟"),
            TranslationText("Quiet low spooling whistle sound", "Léger souffle d'air normal à l'accélération", "صفير خفيف جداً وطبيعي لضغط الهواء"),
            TranslationText("Loud metallic scraping or police siren scream (turbo failure signs)", "Sifflement aigu semblable à une sirène (turbo en fin de vie)", "صوت صفير عالي كالصفارة الحادة أو احتكاك حديد"),
            conditionalTurbo = true
        ),
        ChecklistItem(
            "turbo_oil_pipe", SectionType.TURBO, ScoreCategory.ENGINE,
            TranslationText("Turbo plumbing oil accumulation", "Présence d'huile dans l'intercooler", "تراكم وتجمع الزيت داخل ليات التربو"),
            TranslationText("Unclamp the turbocharger intake plastic pipe and see if oil spills.", "Démonter la durite d'admission d'air froide, vérifier le gras.", "افتح قفل لي هواء التربو الخارجي وتأكد من خلوه من الزيت المتراكم."),
            TranslationText("Thin dry oil dust vapor (normal)", "Simple pellicule de vapeur huileuse sèche (normal)", "رطوبة زيت بسيطة أو جافة بالكامل (طبيعي)"),
            TranslationText("Puddle of wet oil dripping from intake hose (failed seals)", "Flot d'huile grasse coulante dans la durite (Paliers de turbo HS)", "وجود زيت سائل يتدفق من فتحات اللي (تلف صوف محور التربو)"),
            conditionalTurbo = true
        ),

        // COOLING (Weight 25 Engine)
        ChecklistItem(
            "cooling_temp_stability", SectionType.COOLING, ScoreCategory.ENGINE,
            TranslationText("Cooling system / overheating", "Maintien de la température / Surchauffe", "ثبات مؤشر حرارة محرك السيارة"),
            TranslationText("Let the engine reach operation temperature and watch temperature gauge carefully.", "Laisser chauffer, observer la position de l'aiguille de température.", "راقب مؤشر الحرارة في الطبلون بعد التشغيل والتحمية لـ 10 دقائق."),
            TranslationText("Temperature settles in middle zone and stays locked", "Température stable pile au milieu de la jauge", "الحرارة تثبت في النطاق الطبيعي والآمن للمصنع (عادة الربع أو النص)"),
            TranslationText("Gauge approaches RED line or continuous warning chime active", "Aiguille qui monte dans le rouge ou ventilateur qui tourne à fond en continu", "ارتفاع مؤشر الحرارة للمنطقة الحمراء أو غليان ماء الوعاء"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Engine Overheating Active", "Surchauffe moteur active (Moteur en danger immédiat de serrage)", "ارتفاع خطير وحرج في حرارة المحرك (سخونة مفرطة)")
        ),
        ChecklistItem(
            "cooling_components", SectionType.COOLING, ScoreCategory.ENGINE,
            TranslationText("Radiator, Hoses, and Fan", "Radiateur, durites et ventilateur", "سلامة الراديتر، الليات والمروحة التلقائية"),
            TranslationText("Verify hoses are flexible (not bloated/hard) and fan triggers correctly.", "Vérifier la souplesse des durites et le déclenchement du ventilateur.", "تأكد من مرونة ليات الماء وعمل المروحة فور وصول المحرك للحرارة."),
            TranslationText("Flexible hoses, dry radiator core, fan runs on command", "Durites souples, radiateur étanche, ventilateur régulé", "الليات مرنة والراديتر سليم، والمروحة تشتغل وتنطفئ آلياً"),
            TranslationText("Bloated hard hoses, active leak at radiator, or dead fan", "Durites rigides gonflées, fuite de liquide rouge ou ventilo inactif", "ليات يابسة ومنتفخة أو تهريب بوقرة الراديتر أو تعطل المروحة")
        ),

        // ELECTRICAL (Weight 10)
        ChecklistItem(
            "elec_battery_alt", SectionType.ELECTRICAL, ScoreCategory.ELECTRICAL,
            TranslationText("Battery & Alternator charge", "Batterie et alternateur", "حالة البطارية وشحن الدينامو (الألترنيتور)"),
            TranslationText("Ensure the engine starts dynamically and vehicle battery charge lamp turns off.", "S'assurer du démarrage vigoureux et de l'extinction du témoin batterie.", "تأكد من قوة دقة السلف وانطفاء لمبة البطارية الحمراء مباشرة بعد التشغيل."),
            TranslationText("Energetic crank, stable voltage", "Lancement rapide, tension stable", "تشغيل قوي ومستقر ومؤشر الشحن طبيعي"),
            TranslationText("Sluggish start, or battery light remains ON on idle (alternator dead)", "Démarreur poussif, témoin batterie allumé au ralenti (Alternateur en panne)", "سلف ضعيف، أو بقاء لمبة البطارية الحمراء مضيئة (ضعف دينامو الشحن)")
        ),
        ChecklistItem(
            "elec_cabin_heater_ac", SectionType.AIR_CONDITIONING, ScoreCategory.ELECTRICAL,
            TranslationText("Air Conditioning and Heating", "Climatisation froide et chauffage", "فحص مكيف الهواء البارد والحار والسرعات"),
            TranslationText("Turn AC to max cold. Wait 1 minute. Turn heater to max. Test blower speeds.", "Lancer la clim à fond, attendre l'air froid. Puis le chaud. Tester les vitesses.", "شغل التكييف على أقصى برودة لدقيقة، ثم الحار، وقم بتغيير سرعة المروحة."),
            TranslationText("Freezing air quickly, consistent heating, all blower steps functional", "Froid glacial rapide, chauffage brûlant, toutes les vitesses de soufflerie OK", "برودة جليدية سريعة، تفاعل الحار، وعمل كافة سرعات المروحة"),
            TranslationText("Warm air blowing on AC, dead fan speeds, or compressor thud noise", "Air tiède, ventilation bloquée, bruit sourd du compresseur enclenché", "المكيف يخرج هواء حار، أو سرعات المروحة تالفة أو صوت طقة كمبروسر قوية")
        ),

        // GEARBOX / TRANSMISSION (Weight 10)
        ChecklistItem(
            "trans_manual_clutch", SectionType.TRANSMISSION, ScoreCategory.TRANSMISSION,
            TranslationText("Manual clutch bite test", "Test de patinage de l'embrayage (Manuelle)", "اختبار فحمات الدبرياج والانزلاق (عادي)"),
            TranslationText("Engage 3rd gear, apply parking brake, and release clutch fast. Does engine stall?", "Serrer le frein à main, passer la 3e et lâcher l'embrayage. Le moteur cale-t-il ?", "شد فرامل اليد، ضع القير في الثالث، واترك الدبرياج بسرعة."),
            TranslationText("Engine stalls immediately (clutch provides high friction)", "Le moteur cale instantanément (embrayage en bon état)", "المحرك يطفأ في نفس اللحظة (الكلتش يمسك بقوة وبصحة جيدة)"),
            TranslationText("Engine revs up, delays stalling or slips (worn clutch)", "Le moteur reste en marche ou patine (disque d'embrayage à changer)", "المحرك يظل يعمل أو يتأخر في الانطفاء مع ريحة احتراق للكلتش"),
            conditionalTransmission = listOf(TransmissionType.MANUAL)
        ),
        ChecklistItem(
            "trans_auto_slip", SectionType.TRANSMISSION, ScoreCategory.TRANSMISSION,
            TranslationText("Automatic slip & gear shift delay", "Patinage et à-coups de boîte automatique", "انزلاق وتردد تعشيقات القير الأوتوماتيك"),
            TranslationText("Drive the vehicle, accelerate rapidly, check for abrupt thuds or rev flares.", "Tester tous les rapports en accélération, chercher des surrégimes à vide.", "قُد السيارة وتأكد من تبديل النمر، هل العداد يرتفع بدون استجابة للعزم؟"),
            TranslationText("Instant smooth transitions with solid drive link", "Changements fluides, aucun glissement de régime", "التبديلات ناعمة وسريعة ويوجد استجابة ثقيلة وسريعة للعزم"),
            TranslationText("Gear flares (revs rise but speed doesn't) or extreme slams", "Le régime s'emballe entre deux vitesses, chocs violents", "ارتفاع عدد دورات المحرك فجأة بين النمر دون انطلاق، نتشات قوية"),
            conditionalTransmission = listOf(TransmissionType.AUTOMATIC),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Automatic Transmission Slipping (Gearbox Failure)", "Glissement boîte automatique (Défaut interne ou disques brûlés)", "انزلاق وفشل القير الأوتوماتيكي (خلل ميكانيكي داخلي مكلف)")
        ),
        ChecklistItem(
            "trans_shift_fluidity", SectionType.TRANSMISSION, ScoreCategory.TRANSMISSION,
            TranslationText("Gear shift smooth engagement", "Passage fluide des vitesses", "سلاسة تبديل الحركة وسرعة انتقال القير"),
            TranslationText("Shift through all gears (including Reverse). Check for structural resistance or grinding.", "Passer tous les rapports et marche arrière. Chercher craquement de pignon.", "مرر القير بكل النمر والريوس وتحقق من عدم وجود صعوبة أو احتكاك نحاسي."),
            TranslationText("Light and clean lever throw, smooth reverse pop", "Levier souple, guidage précis, enclenchement feutré", "التعشيق سلس ومريح بدون أي مقاومة والصوت ناعم"),
            TranslationText("Hard resistance shifting, gear grinding noise, reverse pops out", "Craquement métallique lors du passage (synchro), vitesse qui saute", "صعوبة في التعشيق، سماع كركرة تروس أو ارتداد عصا القير")
        ),
        ChecklistItem(
            "trans_mount_vibration", SectionType.TRANSMISSION, ScoreCategory.TRANSMISSION,
            TranslationText("Transmission mounts & vibrations", "Supports boîte et vibrations", "كراسي القير والارتجاج"),
            TranslationText("Check for abnormal vibration or hard 'clunk' when shifting gears under load.", "Vérifier l'absence de claquements lors des passages de rapports ou vibrations.", "تحقق من سلامة كراسي القير عند التبديل، وعدم وجود نتشة أو ارتجاج قوي في المقصورة."),
            TranslationText("Smooth engine/gear shifts, intact silentbloc mounts", "Supports sains, aucun à-coup ni vibration anormale", "كراسي سليمة والتبديل ناعم دون أي اهتزاز غير طبيعي"),
            TranslationText("Strong vibration or heavy clunking, cracked rubber mounts", "Supports de boîte fatigués, claquements ou fortes vibrations", "ارتجاج قوي أو صوت طقة قوية مع تلف أو تشقق ربلات كراسي القير")
        ),

        // DRIVELINE (Weight 10 Transmission)
        ChecklistItem(
            "drive_cv_joints", SectionType.DRIVELINE, ScoreCategory.TRANSMISSION,
            TranslationText("CV Joints & Boots", "Cardans et soufflets", "العكوس ورؤوس الكردان والربلات واقية الشحم"),
            TranslationText("Turn steering wheel full lock inside a parking lot and accelerate. Listen for 'tac-tac' noise.", "Braquer à fond en roulant à basse vitesse. Chercher un claquement rotatif ('clac-clac').", "لف الدركسون بالكامل في ساحة واسعة وتحرك بالسيارة، هل تسمع طقطقة؟"),
            TranslationText("No clicking noises, rubber dust boots are sealed/healthy", "Silence complet en butée, soufflets huileux intacts", "هدوء كامل عند المنعطفات، وربلات العكوس مغلقة وتحفظ الشحم"),
            TranslationText("Rapid metal ticking ('tac-tac-tac') or torn leaking boots", "Claquement marqué en virage serré, graisse éparpillée sur la roue", "سماع صوت طقطقة سريعة (طق طق طق) أو ربلة العكس ممزقة ومهربة")
        ),

        // SUSPENSION & STEERING (Weight 10 Suspension/Brakes)
        ChecklistItem(
            "susp_shocks_bounce", SectionType.SUSPENSION_STEERING, ScoreCategory.SUSPENSION_BRAKES,
            TranslationText("Shock absorbers bounce test", "Amortisseurs (Test de rebond)", "اختبار مقاومة وارتداد المساعدين (المساعدات)"),
            TranslationText("Push body corner down hard and release. Does it stop bouncing immediately?", "Appuyer fort sur chaque aile du véhicule et relâcher. Noter l'arrêt.", "اضغط بقوة على زاوية السيارة لأسفل واتركها، هل تتوقف عن الارتجاج فوراً؟"),
            TranslationText("Stops immediately after 1 single cycle", "La voiture remonte et se stabilise directement sans pompage", "السيارة ترتفع وتقف في حركة واحدة سريعة ومثبتة"),
            TranslationText("Bounces multiple times (worn struts / fluid leak)", "La caisse oscille 2 ou 3 fois comme un bateau (amortisseurs usés/fuite)", "السيارة تهتز كالقارب لمرات متعددة (مساعدات تالفة أو تسريب هيدروليك)")
        ),
        ChecklistItem(
            "susp_joints_play", SectionType.SUSPENSION_STEERING, ScoreCategory.SUSPENSION_BRAKES,
            TranslationText("Ball joints and speed bump noises", "Rotules, biellettes et bruit de dos d'âne", "الجوزات، المقصات وأصوات المطبات"),
            TranslationText("Drive over a speed bump or rough terrain. Note clunks or free play in steering wheel.", "Passer un dos d'âne. Rechercher un bruit sourd métallique ou jeu libre.", "قُد السيارة فوق مطب صناعي أو أرض خشنة وتأكد من أصوات المقصات."),
            TranslationText("Silence and robust isolated feedback", "Aucun bruit d'accouplement bas, direction directe", "هدوء تام وتماسك رائع للمقود ونظام التعليق"),
            TranslationText("Loud thud/clunk on speed bumps, steering feels loose", "Claquement ('gong-gong') sur bosse, flou marqué dans l'axe volant", "طق طق قوية عند صعود المطبات أو ارتخاء ملحوظ بالدركسون")
        ),

        // BRAKES (Weight 10 Suspension/Brakes)
        ChecklistItem(
            "brakes_test", SectionType.BRAKING, ScoreCategory.SUSPENSION_BRAKES,
            TranslationText("Braking stability and vibration", "Freinage (Vibration / Trajectoire)", "ثبات الفرامل ومستوى رجة الهوب"),
            TranslationText("Apply brakes firmly on empty safe road. Check if car pulls to one side or brake pedal vibrates.", "Freiner fortement sur ligne droite sûre. Voir si la voiture tire d'un côté.", "اضغط الفرامل بقوة في مسار مستقيم، هل تميل السيارة أو يهتز المقود؟"),
            TranslationText("Car stops straight with flat constant pedal resistance", "La voiture s'arrête en ligne droite, pédale ferme", "السيارة تتوقف بمثالية وفي خط مستقيم والبدّال صلد"),
            TranslationText("Vibrations in steering wheel or pulling to one side (warped rotors/stuck caliper)", "Volant qui vibre fort (disques voilés) ou voiture qui chasse (étrier grippé)", "رجة قوية بالدركسون (هوبات معوجة) أو انحراف جانبي للسير (تهريب فحمات)")
        ),

        // TIRES (Weight 10 Suspension/Brakes)
        ChecklistItem(
            "tires_condition", SectionType.TIRES, ScoreCategory.SUSPENSION_BRAKES,
            TranslationText("Tires wear and DOT age", "État des pneus et âge DOT", "حالة الكفرات ونقشتها والتواريخ (DOT)"),
            TranslationText("Check tire wear uniformity, brand conformity on axle and examine DOT code (e.g. 2522 = 2022).", "Vérifier l'usure régulière, la marque identique par essieu, et lire le code DOT.", "تفحص تآكل نقوش الكفرات، وتجانس النوع واقرأ تاريخ الصنع (مثال: 2522 يعني الأسبوع 25 عام 2022)."),
            TranslationText("Symmetric wear, DOT age under 5 years, matching brands", "Usure symétrique, marque semblable, pneus de moins de 5 ans", "تآكل متناسق، عمر الكفر أقل من 5 سنوات والماركة واحدة بالكل"),
            TranslationText("Uneven wear (alignment bad), dry rot cracks, or DOT over 5 years old", "Usure en escalier, fissures de vieillissement, DOT supérieur à 5 ans", "تآكل داخلي ممسوح، تشققات على الأطراف، أو كفر قديم جداً منتهي الصلاحية")
        ),

        // ROAD TEST (Weight 5)
        ChecklistItem(
            "road_vibration", SectionType.ROAD_TEST, ScoreCategory.ROAD_TEST,
            TranslationText("Highway speed vibration & bearings", "Vibrations à haute vitesse et roulements", "هدوء الطريق للسرعات العالية والارتجاج (الرصافة)"),
            TranslationText("Drive at 90-130 km/h. Listen for a low hum (bearing) or shaking steering wheel.", "Rouler à 90-130 km/h. Chercher vibrations ou bourdonnement sourd (roulement).", "قُد بسرعة 90 إلى 130 كم/ساعة، هل تشعر برجة بالتوجيه أو تسمع ونة؟"),
            TranslationText("Smooth silent cruise, no drone noise", "Roulage silencieux et stable, aucun grondement", "هادئة تماماً ولا توجد رنة بالخلف أو اهتزاز بالمقود"),
            TranslationText("Steering wheel shakes or deep droning hum (worn bearing/unbalanced tires)", "Bourdonnement sourd qui augmente avec la vitesse, vibrations", "رعشة مستمرة بالدركسون أو صوت ونة عميق (تلف رمان كفر أو رصاص)")
        ),
        ChecklistItem(
            "road_tracking", SectionType.ROAD_TEST, ScoreCategory.ROAD_TEST,
            TranslationText("Steering tracking alignment", "Tenue de cap (Lâché de volant)", "اتساق واستقامة المقود عند تركه"),
            TranslationText("Momentarily release grip on steering wheel on a flat road. Does car drift?", "Lâcher le volant un court instant sur une route plane. La voiture dévie-t-elle ?", "افتح يديك قليلاً من المقود لوهلة في طريق مستوٍ تماماً، هل تنحرف السيارة؟"),
            TranslationText("Vehicle continues centered and straight", "La voiture reste parfaitement en ligne droite", "السيارة تتابع في خط مستقيم وبثبات ممتاز"),
            TranslationText("Vehicle pulls violently left or right", "Dérive immédiate à gauche ou à droite (géométrie défaillante)", "تنحرف السيارة مباشرة لليمين أو اليسار (خلل بميزان الكفرات)")
        ),

        // FLOOD DAMAGE (Weight 20 Body/Interior - mapped to Body/Interior in engine scoring)
        ChecklistItem(
            "flood_under_carpets", SectionType.FLOOD_DAMAGE, ScoreCategory.BODYWORK,
            TranslationText("Flood indicators inside carpets", "Moquettes humides / Traces de boue sous sièges", "دلائل وتأثير غرق السيارة بمياه السيول والفيضانات"),
            TranslationText("Pull carpets back near firewall, check fuse panel, and underneath seats for silt.", "Soulever la moquette au fond, chercher du sable fin ou de la boue.", "افحص خلف الدواسات بـ صاج الصدر والزوايا الضيقة أسفل المقاعد وعفن الكوابل."),
            TranslationText("Dry wiring channels, clean baseline paint with no mud", "Moquette propre, câbles d'usine impeccables", "توصيلات الأسلاك والصاج نظيفين وخاليين من الطمي والأتربة"),
            TranslationText("Fine dry mud deposits, silt line, or rusted harness connectors", "Sable fin incrusté, rouille blanche sur l'aluminium (Véhicule inondé)", "وجود رواسب طين مجفف، رواسب ناعمة، أو تآكل أبيض في الألومنيوم"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Flood Damaged Vehicle (Salvage Title Risk)", "Véhicule immergé/inondé (Gros risques d'oxydation électrique)", "السيارة تعرضت للغرق المائي (مخاطر كهربائية وعفن مستقبلي)")
        ),

        // FRAUD DETECTION (Weight 10 mapped to Fraud indicators in score system - calculated as subscale)
        ChecklistItem(
            "fraud_mileage_mismatch", SectionType.FRAUD_DETECTION, ScoreCategory.ROAD_TEST,
            TranslationText("Mileage / wear incoherence", "Incohérence kilométrage / usure générale", "شبهة تلاعب بالممشى ومسافة العداد"),
            TranslationText("See if a car with 50,000 km has completely worn pedals and driver bolster.", "Comparer l'état de fatigue général avec la valeur affichée du compteur.", "قارن اهتراء المقود والمقاعد بلمعان بوية الماكينة والعداد."),
            TranslationText("Wear matches general age and numbers perfectly", "Ajustement logique entre l'usure de l'habitacle et les kilomètres", "الاهتراء يتطابق تماماً ويعزز صحة قراءة العداد"),
            TranslationText("Extreme component destruction but low odometer (evident rollback)", "Cabine ruinée alors que le compteur affiche moins de 80 000 km", "الداخلية مهترئة جداً والعداد منخفض بطريقة غير طبيعية (تصفير العداد)"),
            isDealBreaker = true,
            dealBreakerLabel = TranslationText("Mileage Tampering / Odometric Fraud Found", "Fraude au compteur kilométrique (Odomètre manipulé)", "تلاعب واضح بالممشى وقراءة العداد (شبهة احتيال عداد)")
        )
    )
}
