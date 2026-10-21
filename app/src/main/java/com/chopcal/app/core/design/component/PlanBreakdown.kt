package com.chopcal.app.core.design.component
import com.chopcal.app.core.design.theme.chopCalColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.domain.model.DailyTarget

/**
 * 「计划是怎样算出来的」拆解（默认展开，不折叠）+ 地板说明。
 */
@Composable
fun PlanBreakdown(
    target: DailyTarget,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, chopCalColors.line, RoundedCornerShape(15.dp))
                .background(chopCalColors.card, RoundedCornerShape(15.dp))
                .padding(14.dp),
        ) {
            Text(
                text = stringResource(R.string.breakdown_title),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
            )
            Column(modifier = Modifier.padding(top = 4.dp)) {
                BreakdownLine(stringResource(R.string.breakdown_bmr), "${target.bmr} kcal")
                BreakdownLine(stringResource(R.string.breakdown_tdee), "${target.tdee} kcal")
                BreakdownLine(stringResource(R.string.breakdown_adjustment), formatAdjustment(target.adjustment))
            }
        }
        if (target.floorApplied) {
            Text(
                text = stringResource(R.string.breakdown_floor_note),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                color = Color(0xFF92734E),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F4E8), RoundedCornerShape(11.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun BreakdownLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
        Text(text = label, fontSize = 11.sp, color = chopCalColors.inkMuted)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, fontSize = 11.sp, color = chopCalColors.ink, fontWeight = FontWeight.Medium)
    }
}

private fun formatAdjustment(adjustment: Int): String {
    if (adjustment == 0) return "0 kcal"
    val sign = if (adjustment < 0) "−" else "+"
    return "$sign${kotlin.math.abs(adjustment)} kcal"
}
