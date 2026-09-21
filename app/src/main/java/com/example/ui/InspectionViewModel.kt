package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InspectionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CarCheckDatabase.getDatabase(application)
    private val repository = CarInspectionRepository(db.inspectionDao())

    // Language setting: "fr", "en", "ar", "tn"
    var currentLanguage by mutableStateOf("fr")
        private set

    // Theme setting: true = dark mode, false = light mode
    var isDarkMode by mutableStateOf(false)
        private set

    // Configuration Expert Settings
    var configExpertPhone by mutableStateOf("00216 55 348 558")
        private set

    // Active inspection history stream
    val inspectionList: StateFlow<List<CarInspection>> = repository.allInspections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Active Inspection Draft State
    var draftBrand by mutableStateOf("")
    var draftModel by mutableStateOf("")
    var draftYear by mutableStateOf(2018)
    var draftFuel by mutableStateOf(FuelType.PETROL)
    var draftTurbo by mutableStateOf(false)
    var draftTransmission by mutableStateOf(TransmissionType.MANUAL)
    var draftMileage by mutableStateOf(80000)

    // Key-Value store: "item_id" -> "YES" or "WARNING" or "UNANSWERED"
    var draftResponses = mutableStateOf<Map<String, String>>(emptyMap())

    // Entered DTC Codes for OBD Scanner
    var draftDtcCodes by mutableStateOf<List<String>>(emptyList())

    // Active Inspection being viewed from history
    var selectedInspection by mutableStateOf<CarInspection?>(null)

    fun toggleLanguage() {
        currentLanguage = when (currentLanguage) {
            "fr" -> "en"
            "en" -> "ar"
            "ar" -> "tn"
            else -> "fr"
        }
    }

    fun setLanguage(lang: String) {
        if (lang in listOf("fr", "en", "ar", "tn")) {
            currentLanguage = lang
        }
    }

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    fun updateExpertPhone(phone: String) {
        if (phone.isNotBlank()) {
            configExpertPhone = phone
        }
    }

    fun initNewInspection() {
        draftBrand = ""
        draftModel = ""
        draftYear = 2018
        draftFuel = FuelType.PETROL
        draftTurbo = false
        draftTransmission = TransmissionType.MANUAL
        draftMileage = 80000
        val items = LocalInspectionData.CHECKLIST_ITEMS.associate { it.id to "UNANSWERED" }
        draftResponses.value = items
        draftDtcCodes = emptyList()
        selectedInspection = null
    }

    fun updateResponse(itemId: String, status: String) {
        val updated = draftResponses.value.toMutableMap()
        updated[itemId] = status
        draftResponses.value = updated
    }

    fun addDtcCode(code: String) {
        val upper = code.trim().uppercase()
        if (upper.isNotBlank() && !draftDtcCodes.contains(upper)) {
            draftDtcCodes = draftDtcCodes + upper
        }
    }

    fun removeDtcCode(code: String) {
        draftDtcCodes = draftDtcCodes.filter { it != code }
    }

    // Get checklist items filtered by active vehicle configurations
    fun getActiveChecklistItems(): List<ChecklistItem> {
        return LocalInspectionData.CHECKLIST_ITEMS.filter { item ->
            // Filter by turbo configuration
            if (item.conditionalTurbo != null && item.conditionalTurbo != draftTurbo) {
                return@filter false
            }
            // Filter by transmission configuration
            if (item.conditionalTransmission != null && !item.conditionalTransmission.contains(draftTransmission)) {
                return@filter false
            }
            // Filter by fuel configuration
            if (item.conditionalFuel != null && !item.conditionalFuel.contains(draftFuel)) {
                return@filter false
            }
            true
        }
    }

    // Calculate report statistics based on current active draft state
    fun calculateCurrentReport(): CalculatedReport {
        val activeItems = getActiveChecklistItems()
        val responses = draftResponses.value

        val categoryWeights = mapOf(
            ScoreCategory.DOCUMENTS to 10.0,
            ScoreCategory.BODYWORK to 20.0,
            ScoreCategory.INTERIOR to 10.0,
            ScoreCategory.ENGINE to 25.0,
            ScoreCategory.ELECTRICAL to 10.0,
            ScoreCategory.TRANSMISSION to 10.0,
            ScoreCategory.SUSPENSION_BRAKES to 10.0,
            ScoreCategory.ROAD_TEST to 5.0
        )

        val scores = mutableMapOf<ScoreCategory, Int>()
        var overallRaw = 0.0

        for (category in ScoreCategory.values()) {
            val catItems = activeItems.filter { it.scoreCategory == category }
            val weight = categoryWeights[category] ?: 10.0
            if (catItems.isEmpty()) {
                scores[category] = weight.toInt()
                overallRaw += weight
                continue
            }

            val n = catItems.size
            val w = catItems.count { responses[it.id] == "WARNING" }

            val catScore = kotlin.math.max(0.0, weight - (w * (weight / n)))
            scores[category] = catScore.toInt()
            overallRaw += catScore
        }

        val overallScore = kotlin.math.min(100, kotlin.math.max(0, overallRaw.toInt()))

        // Detect Deal Breakers
        val triggeredDealBreakers = activeItems.filter { it.isDealBreaker && responses[it.id] == "WARNING" }
        val hasDealBreaker = triggeredDealBreakers.isNotEmpty()

        // Verdict
        val verdict = when {
            hasDealBreaker -> "AVOID"
            overallScore >= 80 -> "BUY"
            overallScore >= 50 -> "NEGOTIATE"
            else -> "AVOID"
        }

        // Repair costs estimation risk
        val warningCount = activeItems.count { responses[it.id] == "WARNING" }
        val criticalWarnings = activeItems.count { responses[it.id] == "WARNING" && (it.scoreCategory == ScoreCategory.ENGINE || it.isDealBreaker) }

        val repairCostRisk = when {
            warningCount == 0 -> "LOW" // Pas de réparation
            criticalWarnings > 0 || warningCount >= 5 -> "CRITICAL" // 2000€+
            warningCount >= 3 -> "MODERATE" // 500€-2000€
            else -> "MINIMAL" // 100€-500€
        }

        val posList = activeItems.filter { responses[it.id] == "YES" }
        val negList = activeItems.filter { responses[it.id] == "WARNING" }

        return CalculatedReport(
            overallScore = overallScore,
            verdict = verdict,
            scores = scores,
            hasDealBreakers = hasDealBreaker,
            dealBreakersTriggered = triggeredDealBreakers,
            repairCostRisk = repairCostRisk,
            positivePoints = posList,
            negativePoints = negList,
            warningCount = warningCount
        )
    }

    // Load custom draft relative to past saved inspections
    fun loadInspection(inspection: CarInspection) {
        selectedInspection = inspection
        draftBrand = inspection.brand
        draftModel = inspection.model
        draftYear = inspection.year
        draftFuel = try { FuelType.valueOf(inspection.fuelType) } catch (e: Exception) { FuelType.PETROL }
        draftTurbo = inspection.isTurbo
        draftTransmission = try { TransmissionType.valueOf(inspection.transmission) } catch (e: Exception) { TransmissionType.MANUAL }
        draftMileage = inspection.mileage

        // Deserialize responses
        val respMap = mutableMapOf<String, String>()
        if (inspection.responsesJson.isNotBlank()) {
            inspection.responsesJson.split("||").forEach { row ->
                val cols = row.split("::")
                if (cols.size == 2) {
                    respMap[cols[0]] = cols[1]
                }
            }
        }
        draftResponses.value = respMap

        // Deserialize DTC codes
        draftDtcCodes = if (inspection.dtcCodesJson.isNotBlank() && inspection.dtcCodesJson != "[]") {
            inspection.dtcCodesJson.split(",").filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    fun saveCurrentInspection(onComplete: (Int) -> Unit) {
        val report = calculateCurrentReport()

        // Serialize responses
        val responsesStr = draftResponses.value.entries.joinToString("||") { "${it.key}::${it.value}" }
        // Serialize DTC
        val dtcStr = draftDtcCodes.joinToString(",")
        // Serialize scores
        val scoresStr = report.scores.entries.joinToString(",") { "${it.key.name}:${it.value}" }

        val entity = CarInspection(
            id = selectedInspection?.id ?: 0,
            brand = draftBrand,
            model = draftModel,
            year = draftYear,
            fuelType = draftFuel.name,
            isTurbo = draftTurbo,
            transmission = draftTransmission.name,
            mileage = draftMileage,
            overallScore = report.overallScore,
            verdict = report.verdict,
            scoresJson = scoresStr,
            responsesJson = responsesStr,
            dtcCodesJson = dtcStr,
            expertPhone = configExpertPhone
        )

        viewModelScope.launch {
            val id = repository.insert(entity)
            selectedInspection = entity.copy(id = id.toInt())
            onComplete(id.toInt())
        }
    }

    fun deleteInspection(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (selectedInspection?.id == id) {
                selectedInspection = null
            }
        }
    }
}

data class CalculatedReport(
    val overallScore: Int,
    val verdict: String, // BUY, NEGOTIATE, AVOID
    val scores: Map<ScoreCategory, Int>,
    val hasDealBreakers: Boolean,
    val dealBreakersTriggered: List<ChecklistItem>,
    val repairCostRisk: String, // LOW, MINIMAL, MODERATE, CRITICAL
    val positivePoints: List<ChecklistItem>,
    val negativePoints: List<ChecklistItem>,
    val warningCount: Int
)
