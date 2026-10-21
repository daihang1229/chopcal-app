package com.chopcal.app.page.home.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors
import com.chopcal.app.domain.model.MealSummary
import com.chopcal.app.domain.model.MealType

/**
 * 四餐占位：早餐 / 午餐 / 加餐 / 晚餐。
 * 空态 = 虚线圆 + "+"，记录态 = 缩略图 + kcal。
 */
@Composable
fun MealSection(
    meals: Map<MealType, MealSummary>,
    onMealClick: (MealType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(chopCalColors.card, RoundedCornerShape(20.dp))
            .border(1.dp, chopCalColors.line, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        MealType.entries.forEach { mealType ->
            val meal = meals[mealType] ?: MealSummary()
            MealCircle(
                mealType = mealType,
                meal = meal,
                onClick = { onMealClick(mealType) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MealCircle(
    mealType: MealType,
    meal: MealSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val recorded = meal.kcal > 0
        if (recorded) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3E9D0))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mealType.emoji(),
                    fontSize = 20.sp,
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(50.dp)) {
                    drawCircle(
                        color = Color(0xFFD8DED5),
                        radius = size.minDimension / 2f,
                        style = Stroke(
                            width = 1.2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                        ),
                    )
                }
                Text(
                    text = "+",
                    fontSize = 16.sp,
                    color = Color(0xFFA0AAA0),
                )
            }
        }
        Text(
            text = stringResource(mealType.labelRes),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (recorded) chopCalColors.ink else chopCalColors.inkMuted,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = if (recorded) "${meal.kcal} kcal" else stringResource(R.string.home_not_recorded),
            fontSize = 8.sp,
            color = if (recorded) chopCalColors.inkMuted else chopCalColors.inkFaint,
        )
    }
}

private fun MealType.emoji(): String = when (this) {
    MealType.BREAKFAST -> "🥣"
    MealType.LUNCH -> "🍱"
    MealType.SNACK -> "🍎"
    MealType.DINNER -> "🍲"
}
