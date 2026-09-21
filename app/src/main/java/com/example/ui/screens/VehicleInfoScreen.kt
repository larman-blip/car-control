package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FuelType
import com.example.data.TransmissionType
import com.example.ui.InspectionViewModel
import com.example.ui.components.CarTranslate
import com.example.ui.components.MainGradientHeader
import com.example.ui.components.RacingOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleInfoScreen(
    viewModel: InspectionViewModel,
    onBack: () -> Unit,
    onNavigateToHub: () -> Unit
) {
    val lang = viewModel.currentLanguage
    val isDark = viewModel.isDarkMode
    val scrollState = rememberScrollState()

    var yearText by remember { mutableStateOf(viewModel.draftYear.toString()) }
    var mileageText by remember { mutableStateOf(viewModel.draftMileage.toString()) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = CarTranslate.get("start_new_inspection", lang), fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "1. " + CarTranslate.get("brand", lang) + " & " + CarTranslate.get("model", lang),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Brand
            OutlinedTextField(
                value = viewModel.draftBrand,
                onValueChange = {
                    viewModel.draftBrand = it
                    brandError = it.isBlank()
                },
                label = { Text(CarTranslate.get("brand", lang)) },
                isError = brandError,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("brand_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RacingOrange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (brandError) {
                Text(
                    text = "Brand cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Model
            OutlinedTextField(
                value = viewModel.draftModel,
                onValueChange = {
                    viewModel.draftModel = it
                    modelError = it.isBlank()
                },
                label = { Text(CarTranslate.get("model", lang)) },
                isError = modelError,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("model_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RacingOrange,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            if (modelError) {
                Text(
                    text = "Model cannot be empty",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Year and Mileage Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Year
                OutlinedTextField(
                    value = yearText,
                    onValueChange = {
                        yearText = it
                        val yr = it.toIntOrNull()
                        if (yr != null) {
                            viewModel.draftYear = yr
                        }
                    },
                    label = { Text(CarTranslate.get("year", lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("year_input")
                )

                // Mileage
                OutlinedTextField(
                    value = mileageText,
                    onValueChange = {
                        mileageText = it
                        val ml = it.toIntOrNull()
                        if (ml != null) {
                            viewModel.draftMileage = ml
                        }
                    },
                    label = { Text(CarTranslate.get("mileage", lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mileage_input")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Fuel Selector
            Text(
                text = "2. " + CarTranslate.get("fuel_type", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FuelType.values().forEach { fuel ->
                    val isSelected = viewModel.draftFuel == fuel
                    val label = when (fuel) {
                        FuelType.PETROL -> CarTranslate.get("petrol", lang)
                        FuelType.DIESEL -> CarTranslate.get("diesel", lang)
                        FuelType.HYBRID -> CarTranslate.get("hybrid", lang)
                        FuelType.EV -> CarTranslate.get("ev", lang)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.draftFuel = fuel }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Turbo Selector (Conditional on Petrol/Diesel/Hybrid)
            if (viewModel.draftFuel != FuelType.EV) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = CarTranslate.get("turbo", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Switch(
                        checked = viewModel.draftTurbo,
                        onCheckedChange = { viewModel.draftTurbo = it }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                // If EV, automatically turn off Turbo
                LaunchedEffect(Unit) {
                    viewModel.draftTurbo = false
                }
            }

            // Transmission Selector
            Text(
                text = "3. " + CarTranslate.get("transmission", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransmissionType.values().forEach { trans ->
                    val isSelected = viewModel.draftTransmission == trans
                    val label = when (trans) {
                        TransmissionType.MANUAL -> CarTranslate.get("manual", lang)
                        TransmissionType.AUTOMATIC -> CarTranslate.get("automatic", lang)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { viewModel.draftTransmission = trans }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (viewModel.draftBrand.isBlank()) {
                        brandError = true
                    }
                    if (viewModel.draftModel.isBlank()) {
                        modelError = true
                    }
                    if (viewModel.draftBrand.isNotBlank() && viewModel.draftModel.isNotBlank()) {
                        // Success! Proceed
                        onNavigateToHub()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_vehicle_info"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = CarTranslate.get("next_step", lang),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.NavigateNext,
                        contentDescription = "Next Arrow",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
