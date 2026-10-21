package com.chopcal.app.page.home.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.core.design.theme.chopCalColors
import kotlin.math.abs

private val ITEM_HEIGHT = 44.dp
private val VIEWPORT_HEIGHT = 180.dp

/**
 * 底部滚轮选择器（水/步数）。
 *
 * - 白底弹窗
 * - 中间高亮当前值（高亮条在文字下方，避免遮挡数字）
 * - 确定按钮在右上角，取消可点外部关闭
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WheelPickerSheet(
    title: String,
    unit: String,
    values: List<Int>,
    initialValue: Int,
    confirmText: String,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState,
        snapPosition = SnapPosition.Center,
    )
    var selected by remember { mutableIntStateOf(initialValue) }

    // 初始滚动到当前值（contentPadding 使首项也能居中）
    LaunchedEffect(Unit) {
        val index = values.indexOf(initialValue).coerceAtLeast(0)
        listState.scrollToItem(index)
    }

    // 监听滚动，更新选中值（取最接近视口中心的项）
    LaunchedEffect(listState) {
        snapshotFlow { centerIndex(listState) }.collect { index ->
            if (index in values.indices) selected = values[index]
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
    ) {
        Column(Modifier.padding(bottom = 24.dp)) {
            // 标题 + 单位（左） · 确定（右上角）
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = chopCalColors.ink,
                    )
                    Text(
                        text = unit,
                        fontSize = 9.sp,
                        color = chopCalColors.inkMuted,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                TextButton(onClick = { onConfirm(selected) }) {
                    Text(
                        text = confirmText,
                        color = chopCalColors.forestGreen,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .height(VIEWPORT_HEIGHT),
                contentAlignment = Alignment.Center,
            ) {
                // 中间高亮条（先绘制，位于列表文字之下，不遮挡数字）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .background(chopCalColors.surfaceMint, RoundedCornerShape(12.dp)),
                )
                LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    contentPadding = PaddingValues(
                        vertical = (VIEWPORT_HEIGHT - ITEM_HEIGHT) / 2,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(values.size) { index ->
                        val value = values[index]
                        val isSelected = value == selected
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(ITEM_HEIGHT),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = value.toString(),
                                fontSize = if (isSelected) 22.sp else 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) chopCalColors.forestGreen else chopCalColors.inkMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 计算最接近视口中心的 item index。 */
private fun centerIndex(listState: androidx.compose.foundation.lazy.LazyListState): Int {
    val info = listState.layoutInfo
    val viewportCenter = (info.viewportStartOffset + info.viewportEndOffset) / 2
    return info.visibleItemsInfo
        .minByOrNull { item -> abs(item.offset + item.size / 2 - viewportCenter) }
        ?.index ?: -1
}
