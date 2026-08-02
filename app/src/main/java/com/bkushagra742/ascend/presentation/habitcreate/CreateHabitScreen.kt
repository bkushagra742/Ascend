package com.bkushagra742.ascend.presentation.habitcreate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bkushagra742.ascend.core.theme.AscendColors
import com.bkushagra742.ascend.domain.model.HabitTemplate
import com.bkushagra742.ascend.domain.model.HabitTemplates

/** "Add Habit" flow — two tabs of ready-made templates (Good Habits / Bad Habits to
 * Break), tapping one creates it immediately with sensible attribute rewards already
 * assigned. Framing is deliberate: the "bad" tab is "things to avoid", never phrased as
 * a way to punish yourself for slipping (FR-HAB-02). */
@Composable
fun CreateHabitScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateHabitViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(HabitTab.GOOD) }

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
                "Add Habit",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = AscendColors.SoftWhite,
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(AscendColors.Graphite),
        ) {
            TabChip(
                label = "Good Habits",
                selected = selectedTab == HabitTab.GOOD,
                color = AscendColors.Emerald,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = HabitTab.GOOD },
            )
            TabChip(
                label = "Bad Habits to Break",
                selected = selectedTab == HabitTab.BAD,
                color = AscendColors.CrimsonRed,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = HabitTab.BAD },
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        val templates = if (selectedTab == HabitTab.GOOD) HabitTemplates.positive else HabitTemplates.negative

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(templates) { template ->
                TemplateCard(template = template, onClick = { viewModel.addFromTemplate(template) })
            }
        }

        state.errorMessage?.let {
            Text(it, color = AscendColors.Ruby, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private enum class HabitTab { GOOD, BAD }

@Composable
private fun TabChip(label: String, selected: Boolean, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        color = if (selected) AscendColors.ObsidianBlack else AscendColors.CoolGray,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) color else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    )
}

@Composable
private fun TemplateCard(template: HabitTemplate, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AscendColors.Graphite)
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(template.title, style = MaterialTheme.typography.titleMedium, color = AscendColors.SoftWhite, fontWeight = FontWeight.Medium)
        Text(template.description, style = MaterialTheme.typography.bodySmall, color = AscendColors.CoolGray)
    }
}
