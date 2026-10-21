package com.chopcal.app.core.design.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.BmiBlue
import com.chopcal.app.core.design.theme.BmiGreen
import com.chopcal.app.core.design.theme.BmiOrange
import com.chopcal.app.core.design.theme.ForestGreen
import com.chopcal.app.core.design.theme.Ink
import com.chopcal.app.domain.model.BmiBand
import java.util.Locale

/**
 * BMI 环形展示：数字 + 分档标签。
 */
@Composable
fun BmiRing(
    bmi: Double,
    band: BmiBand,
    modifier: Modifier = Modifier,
) {
    val ringColor = when (band) {
        BmiBand.UNDERWEIGHT -> BmiBlue
        BmiBand.NORMAL -> BmiGreen
        BmiBand.OVERWEIGHT -> BmiOrange
    }
    Box(
        modifier = modifier
            .size(190.dp)
            .border(6.dp, ringColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format(Locale.US, "%.1f", bmi),
                fontSize = 52.sp,
                letterSpacing = (-3.6).sp,
                fontWeight = FontWeight.Medium,
                color = Ink,
            )
            Text(
                text = "● ${stringResource(band.labelRes)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ForestGreen,
            )
        }
    }
}
