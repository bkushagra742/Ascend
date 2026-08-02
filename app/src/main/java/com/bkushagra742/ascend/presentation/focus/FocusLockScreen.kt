package com.bkushagra742.ascend.presentation.focus

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors

/**
 * FR-FOC-01/02/05: explanation-first, opt-in, always-visible master switch. The actual
 * Accessibility Service permission can't be requested via a normal runtime dialog —
 * Android requires sending the user to system Settings for that (FR-FOC-05), which is
 * what "Enable in System Settings" below does. Turning OFF the in-app switch here does
 * NOT revoke the system permission (Android has no API for that), but it does make
 * ShouldBlockDistractingAppUseCase always return false — functionally off either way.
 */
@Composable
fun FocusLockScreen(
    onBack: () -> Unit,
    viewModel: FocusLockViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AscendColors.ObsidianBlack),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = AscendColors.SoftWhite)
            }
            Text(
                "Focus Lock",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = AscendColors.SoftWhite,
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "When you turn this on, opening an app you've chosen below will remind " +
                    "you to finish today's Missions first — with a link straight back " +
                    "here, never a hard lock. You can turn this off anytime.",
                style = MaterialTheme.typography.bodyMedium,
                color = AscendColors.CoolGray,
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AscendColors.Graphite)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Focus Lock", style = MaterialTheme.typography.titleMedium, color = AscendColors.SoftWhite)
                    Text("Requires enabling in system Accessibility settings", style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
                }
                Switch(
                    checked = state.settings.isEnabled,
                    onCheckedChange = viewModel::onToggleEnabled,
                    colors = SwitchDefaults.colors(checkedTrackColor = AscendColors.CrimsonRed),
                )
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AscendColors.Graphite),
            ) {
                Text("Enable in System Settings", color = AscendColors.RoyalGold)
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 20.dp))

            Text("Apps to limit", style = MaterialTheme.typography.titleMedium, color = AscendColors.SoftWhite)
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                color = AscendColors.CrimsonRed,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(state.installedApps, key = { it.packageName }) { app ->
                    val isBlocked = app.packageName in state.settings.blockedPackages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = isBlocked,
                            onCheckedChange = { checked -> viewModel.onToggleApp(app.packageName, checked) },
                            colors = CheckboxDefaults.colors(checkedColor = AscendColors.CrimsonRed),
                        )
                        Text(app.label, color = AscendColors.SoftWhite, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
