package com.bkushagra742.ascend.presentation.questcreate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.presentation.components.DifficultySelector

/** "Add Mission" flow — title, description, difficulty (Easy/Medium/Hard/Boss), and a
 * live XP/Credit preview that updates as difficulty changes. FR-QST-04's caps mean the
 * preview numbers here are exactly what gets awarded, no surprises after saving. */
@Composable
fun CreateQuestScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateQuestViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.didSave) {
        if (state.didSave) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AscendColors.ObsidianBlack)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = AscendColors.SoftWhite)
            }
            Text(
                "New Mission",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = AscendColors.SoftWhite,
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors(),
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))

        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Description (optional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors(),
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 20.dp))

        Text("Difficulty", style = MaterialTheme.typography.titleMedium, color = AscendColors.SoftWhite)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        DifficultySelector(selected = state.difficulty, onSelect = viewModel::onDifficultySelect)

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AscendColors.Graphite)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            PreviewStat(label = "XP", value = "+${state.previewXp}")
            PreviewStat(label = "Credits", value = "+${state.previewCredits}")
        }

        state.errorMessage?.let {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(it, color = AscendColors.Ruby, style = MaterialTheme.typography.bodySmall)
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        Button(
            onClick = viewModel::onSave,
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AscendColors.CrimsonRed),
        ) {
            Text(if (state.isSaving) "Saving..." else "Create Mission")
        }
    }
}

@Composable
private fun PreviewStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = AscendColors.RoyalGold, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = AscendColors.SoftWhite,
    unfocusedTextColor = AscendColors.SoftWhite,
    focusedBorderColor = AscendColors.CrimsonRed,
    unfocusedBorderColor = AscendColors.DividerLow,
    focusedLabelColor = AscendColors.RoyalGold,
    unfocusedLabelColor = AscendColors.CoolGray,
    cursorColor = AscendColors.CrimsonRed,
)
