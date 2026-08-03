package com.bkushagra742.ascend.presentation.focus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bkushagra742.ascend.R
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.core.theme.AscendTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * FR-FOC-03/04: this is the "explaining what must be finished before access is allowed"
 * screen. Two exits ALWAYS available — going to Missions, or dismissing back to the
 * home screen — because Focus Lock must never function like a hard lock the user can't
 * escape. It's a reminder the user asked for, not a cage.
 */
@AndroidEntryPoint
class FocusBlockActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE)
        setContent {
            AscendTheme {
                FocusBlockScreen(
                    onGoToMissions = {
                        // FocusBlockActivity is a standalone task (launched with
                        // FLAG_ACTIVITY_NEW_TASK from the service) — hand off to
                        // MainActivity's own task rather than trying to navigate
                        // within this activity, which has no NavHost of its own.
                        val mainIntent = android.content.Intent(this, com.bkushagra742.ascend.MainActivity::class.java)
                        mainIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(mainIntent)
                        finish()
                    },
                    onDismiss = {
                        val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN)
                        homeIntent.addCategory(android.content.Intent.CATEGORY_HOME)
                        homeIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(homeIntent)
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
    }
}

@Composable
private fun FocusBlockScreen(onGoToMissions: () -> Unit, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = AscendColors.ObsidianBlack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.focus_block_title),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = AscendColors.RoyalGold,
                textAlign = TextAlign.Center,
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(
                text = stringResource(R.string.focus_block_body),
                style = MaterialTheme.typography.bodyLarge,
                color = AscendColors.CoolGray,
                textAlign = TextAlign.Center,
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 40.dp))
            Button(
                onClick = onGoToMissions,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AscendColors.CrimsonRed),
            ) {
                Text(stringResource(R.string.focus_block_cta))
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AscendColors.CoolGray),
            ) {
                Text(stringResource(R.string.focus_block_dismiss))
            }
        }
    }
}