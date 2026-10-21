package com.chopcal.app.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.DisplayNumberStyle
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.InkOnGreen
import com.chopcal.app.domain.model.DailyTarget
import java.time.LocalDate
import java.util.Locale

/**
 * 结果页绿色大数字卡：每日卡路里 + 蛋白 + 预计达成。
 */
@Composable
fun ResultHeroCard(
    target: DailyTarget,
    modifier: Modifier = Modifier,
) {
    val date = LocalDate.ofEpochDay(target.projectedDateEpochDay)
    val dateStr = String.format(Locale.US, "%d.%02d", date.monthValue, date.dayOfMonth)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ForestGreen, RoundedCornerShape(21.dp))
            .padding(vertical = 23.dp, horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.result_daily_energy_target),
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            color = Color(0xFFC5D7C9),
        )
        Text(
            text = target.dailyKcal.toString(),
            style = DisplayNumberStyle,
            color = InkOnGreen,
        )
        Text(
            text = stringResource(R.string.result_kcal_day),
            fontSize = 11.sp,
            color = Color(0xFFD4E2D7),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.result_protein, target.proteinG),
                    fontSize = 10.sp,
                    color = Color(0xFFD3E0D4),
                )
            }
            Column {
                Text(
                    text = stringResource(R.string.result_target_date, dateStr),
                    fontSize = 10.sp,
                    color = Color(0xFFD3E0D4),
                )
            }
        }
    }
}
