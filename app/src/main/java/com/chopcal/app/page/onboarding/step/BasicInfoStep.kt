package com.chopcal.app.page.onboarding.step

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.component.BigNumberField
import com.chopcal.app.core.design.component.FieldLabel
import com.chopcal.app.core.design.component.SegmentedControl
import com.chopcal.app.core.design.component.StepActions
import com.chopcal.app.core.design.component.StepHeader
import com.chopcal.app.core.design.theme.InkFaint
import com.chopcal.app.domain.model.Gender
import com.chopcal.app.page.onboarding.FieldId
import com.chopcal.app.page.onboarding.OnboardingIntent
import com.chopcal.app.page.onboarding.OnboardingUiState

/**
 * Item 2：性别 + 年龄。
 */
@Composable
fun BasicInfoStep(state: OnboardingUiState, onIntent: (OnboardingIntent) -> Unit) {
    val selectedGender = state.draft.gender?.let { Gender.entries.indexOf(it) } ?: -1

    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(24.dp))
        StepHeader(
            kicker = stringResource(R.string.basic_kicker),
            title = stringResource(R.string.basic_title),
            sub = stringResource(R.string.basic_sub),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .offset(y = (-40).dp),
            verticalArrangement = Arrangement.Center,
        ) {
            FieldLabel(stringResource(R.string.label_sex))
            Spacer(Modifier.height(16.dp))
            SegmentedControl(
                options = Gender.entries.map { stringResource(it.labelRes) },
                selectedIndex = selectedGender,
                onSelect = { onIntent(OnboardingIntent.SelectGender(Gender.entries[it])) },
            )
            Spacer(Modifier.height(30.dp))
            FieldLabel(stringResource(R.string.label_age))
            Spacer(Modifier.height(12.dp))
            BigNumberField(
                value = state.draft.age?.toString() ?: "",
                onValueChange = { v -> onIntent(OnboardingIntent.UpdateAge(v.toIntOrNull())) },
                unit = stringResource(R.string.unit_years),
                placeholder = "28",
                error = state.fieldErrors[FieldId.AGE],
            )
            Spacer(Modifier.height(8.dp))
            Text(text = stringResource(R.string.hint_age_range), fontSize = 11.sp, color = InkFaint)
        }
        StepActions(
            primaryText = stringResource(R.string.common_continue),
            primaryEnabled = state.canProceed,
            onPrimary = { onIntent(OnboardingIntent.Next) },
        )
        Spacer(Modifier.height(20.dp))
    }
}
