package com.chopcal.app.page.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chopcal.app.R
import com.chopcal.app.core.design.theme.chopCalColors

/**
 * 隐私政策 + 权限说明页（引导页 / 个人中心共用）。
 */
@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(chopCalColors.paper)
            .statusBarsPadding(),
    ) {
        // 顶部栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(chopCalColors.surfaceMuted)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back),
                    tint = chopCalColors.forestGreen,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = stringResource(R.string.privacy_title),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 12.dp),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
        ) {
            Text(
                text = stringResource(R.string.privacy_intro),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = chopCalColors.inkMuted,
            )

            Spacer(Modifier.height(20.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_data),
                body = stringResource(R.string.privacy_section_data_body),
            )
            PrivacySection(
                title = stringResource(R.string.privacy_section_use),
                body = stringResource(R.string.privacy_section_use_body),
            )
            PrivacySection(
                title = stringResource(R.string.privacy_section_storage),
                body = stringResource(R.string.privacy_section_storage_body),
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.privacy_permissions_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = chopCalColors.ink,
            )
            Spacer(Modifier.height(8.dp))

            PermissionRow(
                title = stringResource(R.string.permission_camera),
                desc = stringResource(R.string.permission_camera_desc),
            )
            PermissionRow(
                title = stringResource(R.string.permission_notification),
                desc = stringResource(R.string.permission_notification_desc),
            )
            PermissionRow(
                title = stringResource(R.string.permission_internet),
                desc = stringResource(R.string.permission_internet_desc),
            )
            PermissionRow(
                title = stringResource(R.string.permission_health),
                desc = stringResource(R.string.permission_health_desc),
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 18.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = chopCalColors.ink,
        )
        Text(
            text = body,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
private fun PermissionRow(title: String, desc: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(chopCalColors.forestGreen),
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = chopCalColors.ink,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Text(
            text = desc,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = chopCalColors.inkMuted,
            modifier = Modifier.padding(start = 14.dp, top = 3.dp),
        )
    }
}
