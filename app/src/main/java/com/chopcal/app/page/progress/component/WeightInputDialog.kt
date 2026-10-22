package com.chopcal.app.page.progress.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.WeightUnit
import com.chopcal.app.page.progress.formatFullDate
import com.chopcal.app.page.progress.weightToKg
import java.time.LocalDate

private const val MILLIS_PER_DAY = 86_400_000L

/**
 * 记录体重弹窗（数字输入 + 日期选择 + 保存）。
 *
 * - 单位跟随用户全局设置；输入值按当前单位换算成 kg 后落库
 * - 日期默认今天，可回选过去日期补录
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightInputDialog(
    weightUnit: WeightUnit,
    onConfirm: (weightKg: Double, dateEpochDay: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var dateEpochDay by remember { mutableStateOf(LocalDate.now().toEpochDay()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val unitLabel = if (weightUnit == WeightUnit.IMPERIAL) "lb" else "kg"

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateEpochDay * MILLIS_PER_DAY,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dateEpochDay = millis / MILLIS_PER_DAY
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.progress_weight_input_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = {
                        Text(stringResource(R.string.progress_weight_input_label, unitLabel))
                    },
                    suffix = { Text(unitLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.progress_weight_input_date),
                        fontSize = 13.sp,
                        color = chopCalColors.inkMuted,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = formatFullDate(dateEpochDay),
                        fontSize = 13.sp,
                        color = chopCalColors.ink,
                    )
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = stringResource(R.string.progress_weight_input_date),
                        tint = chopCalColors.inkMuted,
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(16.dp),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    text.toDoubleOrNull()?.let {
                        onConfirm(weightToKg(it, weightUnit), dateEpochDay)
                    }
                },
                enabled = text.toDoubleOrNull() != null,
            ) {
                Text(
                    stringResource(R.string.progress_weight_input_save),
                    color = if (text.toDoubleOrNull() != null) chopCalColors.forestGreen else chopCalColors.inkFaint,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        },
    )
}
