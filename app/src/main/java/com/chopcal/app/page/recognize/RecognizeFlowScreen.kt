package com.chopcal.app.page.recognize

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.core.content.ContextCompat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.CaptureMealTarget
import com.chopcal.app.domain.model.CookingMethod
import com.chopcal.app.domain.model.HealthTip
import com.chopcal.app.domain.model.Ingredient
import com.chopcal.app.domain.model.InputMethod
import com.chopcal.app.domain.model.MealType
import com.chopcal.app.domain.model.Portion
import com.chopcal.app.domain.model.RecipeLine
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.delay

/**
 * 识别流程：INPUT（选餐次 + 拍照/相册）为底部弹窗，点拍照/相册后进入全屏识别页面。
 */
@Composable
fun RecognizeFlowScreen(
    dateEpochDay: Long,
    target: CaptureMealTarget,
    imageUri: String?,
    viewModel: RecognizeViewModel,
    onDismiss: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(dateEpochDay, target, imageUri) {
        viewModel.sendIntent(RecognizeIntent.Open(dateEpochDay, target, imageUri))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RecognizeEffect.Finished -> onDismiss()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(chopCalColors.screen),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = chopCalColors.inkMuted,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { viewModel.sendIntent(RecognizeIntent.Dismiss) },
            )
        }
        when (state.stage) {
            RecognizeStage.INPUT -> Unit
            RecognizeStage.RECOGNIZING -> RecognizingStage(state)
            RecognizeStage.CANDIDATES -> CandidatesStage(state, viewModel::sendIntent)
            RecognizeStage.SEARCH -> SearchDishStage(state, viewModel::sendIntent)
            RecognizeStage.ADJUST -> AdjustStage(state, viewModel::sendIntent)
            RecognizeStage.RESULT -> ResultStage(state, viewModel::sendIntent)
        }
    }
}

// ── 识别中（含健康小提示滚动） ───────────────────────────────

@Composable
private fun RecognizingStage(state: RecognizeUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = chopCalColors.forestGreen)
        Text(
            text = stringResource(R.string.recognizing_text),
            fontSize = 13.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(top = 16.dp),
        )
        HealthTipCarousel(tips = state.healthTips, modifier = Modifier.padding(top = 24.dp))
    }
}

@Composable
private fun HealthTipCarousel(tips: List<HealthTip>, modifier: Modifier = Modifier) {
    if (tips.isEmpty()) return
    var index by remember { mutableStateOf(Random.nextInt(tips.size)) }
    LaunchedEffect(tips) {
        while (true) {
            delay(5000L)
            index = (index + 1) % tips.size
        }
    }
    val tip = tips[index]
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(chopCalColors.card, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = tip.text,
            fontSize = 11.sp,
            color = chopCalColors.ink,
        )
        Text(
            text = stringResource(R.string.health_tip_hint),
            fontSize = 9.sp,
            color = chopCalColors.inkFaint,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

// ── 候选阶段 ──────────────────────────────────────────────────

@Composable
private fun CandidatesStage(state: RecognizeUiState, onIntent: (RecognizeIntent) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.candidates_title),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )
        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            state.candidates.forEachIndexed { index, candidate ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(chopCalColors.card, RoundedCornerShape(14.dp))
                        .clickable { onIntent(RecognizeIntent.SelectCandidate(index)) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = candidate.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = chopCalColors.ink,
                        )
                        Text(
                            text = candidate.matchedDish?.let {
                                val kcal = state.candidateKcals.getOrNull(index) ?: 0
                                "约 $kcal kcal · ${it.method}"
                            }.orEmpty(),
                            fontSize = 10.sp,
                            color = chopCalColors.inkMuted,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                    Text(
                        text = "${candidate.confidence}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = chopCalColors.forestGreen,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onIntent(RecognizeIntent.NotThisDish) }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.not_this_dish),
                fontSize = 12.sp,
                color = chopCalColors.inkMuted,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onIntent(RecognizeIntent.RetryRecognize) }
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.retake),
                fontSize = 12.sp,
                color = chopCalColors.inkMuted,
            )
        }
    }
}

// ── 核对食材与分量 ────────────────────────────────────────────

@Composable
private fun AdjustStage(state: RecognizeUiState, onIntent: (RecognizeIntent) -> Unit) {
    val draft = state.dishDraft
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var showIngredientPicker by remember { mutableStateOf(false) }
    var showMethodSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = chopCalColors.inkMuted,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onIntent(RecognizeIntent.BackToCandidates) },
            )
            Text(
                text = draft?.dishName.orEmpty(),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        if (draft != null) {
            // 食材明细
            SectionLabel(stringResource(R.string.result_ingredients))
            draft.lines.forEachIndexed { index, line ->
                IngredientRow(
                    line = line,
                    onEdit = { editingIndex = index },
                    onRemove = { onIntent(RecognizeIntent.RemoveLine(index)) },
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(chopCalColors.card, RoundedCornerShape(12.dp))
                    .clickable { showIngredientPicker = true }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.add_ingredient),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = chopCalColors.forestGreen,
                )
            }

            // 分量
            SectionLabel(stringResource(R.string.label_portion))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Portion.entries.forEach { portion ->
                    ChoiceChip(
                        label = stringResource(portion.labelRes),
                        selected = draft.portion == portion,
                        onClick = { onIntent(RecognizeIntent.SelectPortion(portion)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // 做法
            SectionLabel(stringResource(R.string.label_method))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                ChoiceChip(
                    label = draft.method.label,
                    selected = true,
                    onClick = { showMethodSheet = true },
                    modifier = Modifier.weight(1f),
                )
                ChoiceChip(
                    label = stringResource(R.string.method_more),
                    selected = false,
                    onClick = { showMethodSheet = true },
                )
            }

            // 份数
            SectionLabel(stringResource(R.string.label_servings))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepperButton("−") { onIntent(RecognizeIntent.UpdateServings(draft.servings - 1)) }
                Text(
                    text = "${draft.servings}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = chopCalColors.ink,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
                StepperButton("+") { onIntent(RecognizeIntent.UpdateServings(draft.servings + 1)) }
            }

            com.chopcal.app.core.design.component.PrimaryButton(
                text = stringResource(R.string.capture_compute),
                onClick = { onIntent(RecognizeIntent.ComputeResult) },
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
            )
        }
    }

    editingIndex?.let { index ->
        val line = draft?.lines?.getOrNull(index)
        if (line != null) {
            EditIngredientDialog(
                line = line,
                onConfirm = { amountG ->
                    onIntent(RecognizeIntent.EditLineAmount(index, amountG))
                    editingIndex = null
                },
                onDismiss = { editingIndex = null },
            )
        }
    }

    if (showIngredientPicker) {
        IngredientPickerDialog(
            ingredients = state.ingredientResults,
            onQueryChange = { onIntent(RecognizeIntent.SearchIngredient(it)) },
            onAdd = { ingredient, amountG ->
                onIntent(RecognizeIntent.AddIngredient(ingredient, amountG))
                showIngredientPicker = false
            },
            onDismiss = { showIngredientPicker = false },
        )
    }

    if (showMethodSheet) {
        CookingMethodSheet(
            current = draft?.method ?: CookingMethod.HOME_STYLE,
            onSelect = {
                onIntent(RecognizeIntent.SelectCookingMethod(it))
                showMethodSheet = false
            },
            onDismiss = { showMethodSheet = false },
        )
    }
}

@Composable
private fun IngredientRow(
    line: RecipeLine,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = line.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
            )
            Text(
                text = "${line.kcalPer100g.roundToInt()} kcal / 100g",
                fontSize = 10.sp,
                color = chopCalColors.inkFaint,
            )
        }
        Text(
            text = "${line.amountG}g",
            fontSize = 12.sp,
            color = chopCalColors.ink,
        )
        TextButton(onClick = onEdit) {
            Text(stringResource(R.string.ingredient_edit), fontSize = 11.sp, color = chopCalColors.forestGreen)
        }
        TextButton(onClick = onRemove) {
            Text(stringResource(R.string.ingredient_delete), fontSize = 11.sp, color = chopCalColors.warning)
        }
    }
}

@Composable
private fun EditIngredientDialog(
    line: RecipeLine,
    onConfirm: (Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var amount by remember { mutableStateOf(line.amountG.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = chopCalColors.card,
        title = { Text(stringResource(R.string.ingredient_edit), color = chopCalColors.ink) },
        text = {
            Column {
                Text(line.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = chopCalColors.ink)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.ingredient_weight)) },
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    text = "${line.kcalPer100g.roundToInt()} kcal / 100g",
                    fontSize = 10.sp,
                    color = chopCalColors.inkFaint,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(amount.toDoubleOrNull() ?: line.amountG) }) {
                Text(stringResource(R.string.common_confirm), color = chopCalColors.forestGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel), color = chopCalColors.inkMuted)
            }
        },
    )
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = chopCalColors.inkMuted,
        modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
    )
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = if (selected) chopCalColors.surfaceMint else chopCalColors.card,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted,
        )
    }
}

@Composable
private fun StepperButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(chopCalColors.surfaceMuted, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, fontSize = 18.sp, color = chopCalColors.ink)
    }
}

// ── 结果阶段 ──────────────────────────────────────────────────

@Composable
private fun ResultStage(state: RecognizeUiState, onIntent: (RecognizeIntent) -> Unit) {
    val total = state.totalNutrients
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.result_title_home),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 10.dp)) {
            Text(
                text = "${total.kcalMin}–${total.kcalMax}",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.forestGreen,
            )
            Text(
                text = " kcal",
                fontSize = 11.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ResultMacro(stringResource(R.string.home_protein), "${total.proteinG}g")
            ResultMacro(stringResource(R.string.home_carbs), "${total.carbsG}g")
            ResultMacro(stringResource(R.string.home_fat), "${total.fatG}g")
        }

        // 营养对照
        state.comparison?.let { c ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .background(chopCalColors.surfaceMint, RoundedCornerShape(12.dp))
                    .padding(14.dp),
            ) {
                Text(
                    text = stringResource(R.string.result_target_percent, c.percent),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = chopCalColors.forestGreen,
                )
                Text(
                    text = stringResource(R.string.result_remaining, c.remainingKcal),
                    fontSize = 10.sp,
                    color = chopCalColors.inkMuted,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        // 多菜列表
        if (state.drafts.isNotEmpty()) {
            Text(
                text = stringResource(R.string.result_ingredients),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
            )
            state.drafts.forEach { draft ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = draft.dishName,
                        fontSize = 12.sp,
                        color = chopCalColors.ink,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "${draft.nutrients().kcalTypical} kcal",
                        fontSize = 11.sp,
                        color = chopCalColors.inkMuted,
                    )
                }
            }
        }

        // 继续添加一道菜
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(chopCalColors.card, RoundedCornerShape(12.dp))
                .clickable { onIntent(RecognizeIntent.ContinueAdd) }
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.result_add_dish),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.forestGreen,
            )
        }

        val queryOnly = state.isQueryOnly
        com.chopcal.app.core.design.component.PrimaryButton(
            text = stringResource(if (queryOnly) R.string.result_got_it else R.string.result_save),
            onClick = {
                if (queryOnly) onIntent(RecognizeIntent.Dismiss) else onIntent(RecognizeIntent.SaveResult)
            },
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        )
    }

    state.saveConflict?.let { conflict ->
        val mealLabel = (state.target as? CaptureMealTarget.Meal)
            ?.mealType?.let { stringResource(it.labelRes) }.orEmpty()
        SaveConflictDialog(
            mealLabel = mealLabel,
            conflict = conflict,
            onAdd = { onIntent(RecognizeIntent.SaveAdd) },
            onReplace = { onIntent(RecognizeIntent.SaveReplace) },
            onCancel = { onIntent(RecognizeIntent.CancelSave) },
        )
    }
}

@Composable
private fun SaveConflictDialog(
    mealLabel: String,
    conflict: SaveConflict,
    onAdd: () -> Unit,
    onReplace: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = chopCalColors.card,
        title = { Text(stringResource(R.string.save_conflict_title, mealLabel), color = chopCalColors.ink) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.save_conflict_desc, conflict.existingKcal, conflict.existingCount),
                    fontSize = 11.sp,
                    color = chopCalColors.inkMuted,
                )
                Text(
                    text = stringResource(R.string.save_conflict_question),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = chopCalColors.ink,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                )
                com.chopcal.app.core.design.component.PrimaryButton(
                    text = stringResource(R.string.save_add, mealLabel),
                    onClick = onAdd,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                OutlinedButton(
                    onClick = onReplace,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                ) {
                    Text(stringResource(R.string.save_replace, mealLabel), color = chopCalColors.inkMuted)
                }
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.common_cancel), color = chopCalColors.inkMuted)
                }
            }
        },
        confirmButton = {},
    )
}

@Composable
private fun ResultMacro(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = chopCalColors.ink)
        Text(text = label, fontSize = 10.sp, color = chopCalColors.inkMuted)
    }
}

// ── 搜索菜品（降级兜底） ──────────────────────────────────────

@Composable
private fun SearchDishStage(state: RecognizeUiState, onIntent: (RecognizeIntent) -> Unit) {
    var query by remember { mutableStateOf(state.searchQuery) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = chopCalColors.inkMuted,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onIntent(RecognizeIntent.BackToCandidates) },
            )
            Text(
                text = stringResource(R.string.search_dish_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it; onIntent(RecognizeIntent.SearchDish(it)) },
            label = { Text(stringResource(R.string.search_dish_hint)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        )

        Column(
            modifier = Modifier.padding(top = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.searchResults.forEach { dish ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(chopCalColors.card, RoundedCornerShape(12.dp))
                        .clickable { onIntent(RecognizeIntent.SelectSearchResult(dish)) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = dish.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = chopCalColors.ink,
                        )
                        Text(
                            text = "${dish.category} · ${dish.method}",
                            fontSize = 10.sp,
                            color = chopCalColors.inkMuted,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.search_not_found),
                fontSize = 11.sp,
                color = chopCalColors.inkMuted,
            )
        }
    }
}

// ── 添加食材（搜食材库） ──────────────────────────────────────

@Composable
private fun IngredientPickerDialog(
    ingredients: List<Ingredient>,
    onQueryChange: (String) -> Unit,
    onAdd: (Ingredient, Double) -> Unit,
    onDismiss: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Ingredient?>(null) }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = chopCalColors.card,
        title = { Text(stringResource(R.string.add_ingredient), color = chopCalColors.ink) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it; onQueryChange(it) },
                    label = { Text(stringResource(R.string.search_ingredient_hint)) },
                )
                if (selected == null) {
                    Column(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .heightIn(max = 240.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(chopCalColors.screen, RoundedCornerShape(10.dp))
                                    .clickable { selected = ingredient }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                            ) {
                                Text(ingredient.name, fontSize = 12.sp, color = chopCalColors.ink)
                            }
                        }
                    }
                } else {
                    Column(Modifier.padding(top = 10.dp)) {
                        Text(
                            text = selected!!.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = chopCalColors.ink,
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text(stringResource(R.string.ingredient_weight)) },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Text(
                            text = "${selected!!.kcalPer100g.roundToInt()} kcal / 100g",
                            fontSize = 10.sp,
                            color = chopCalColors.inkFaint,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (selected != null) {
                TextButton(onClick = { onAdd(selected!!, amount.toDoubleOrNull() ?: 0.0) }) {
                    Text(stringResource(R.string.common_confirm), color = chopCalColors.forestGreen)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel), color = chopCalColors.inkMuted)
            }
        },
    )
}

// ── 做法选择弹窗 ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CookingMethodSheet(
    current: CookingMethod,
    onSelect: (CookingMethod) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = chopCalColors.screen) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.method_sheet_title),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
            )
            Text(
                text = stringResource(R.string.method_sheet_hint),
                fontSize = 10.sp,
                color = chopCalColors.inkMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
            CookingMethod.entries.chunked(2).forEach { rowMethods ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    rowMethods.forEach { method ->
                        MethodCell(
                            label = method.label,
                            coefficient = method.coefficient,
                            selected = method == current,
                            onClick = { onSelect(method) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (rowMethods.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun MethodCell(
    label: String,
    coefficient: Double,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = if (selected) chopCalColors.surfaceMint else chopCalColors.card,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "$label · $coefficient",
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) chopCalColors.forestGreen else chopCalColors.inkMuted,
        )
    }
}
