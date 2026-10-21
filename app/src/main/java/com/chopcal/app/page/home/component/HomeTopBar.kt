package com.chopcal.app.page.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.chopCalColors

/**
 * 首页顶部栏：头像（个人中心）+ 标题 + Pro + 记录。
 * 顺序已确认：Pro 在左、记录在右。
 */
@Composable
fun HomeTopBar(
    nickname: String?,
    onOpenProfile: () -> Unit,
    onOpenPro: () -> Unit,
    onOpenRecord: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 头像 → 个人中心（白底）
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, chopCalColors.line, CircleShape)
                .clickable(onClick = onOpenProfile),
            contentAlignment = Alignment.Center,
        ) {
            if (nickname.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = chopCalColors.forestGreen,
                    modifier = Modifier.size(22.dp),
                )
            } else {
                Text(
                    text = nickname.take(1).uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = chopCalColors.forestGreen,
                )
            }
        }

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "ChopCal",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp,
                color = chopCalColors.ink,
            )
            Text(
                text = "EAT WELL, FEEL WELL",
                fontSize = 8.sp,
                letterSpacing = 1.3.sp,
                color = chopCalColors.inkMuted,
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pro
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFFF4EBD4))
                    .clickable(onClick = onOpenPro)
                    .padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFB58835),
                    modifier = Modifier.size(13.dp),
                )
                Text(
                    text = "PRO",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8E6A2D),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }

            // 记录
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(chopCalColors.surfaceMuted)
                    .clickable(onClick = onOpenRecord),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = chopCalColors.forestGreen,
                    modifier = Modifier.size(17.dp),
                )
            }
        }
    }
}
