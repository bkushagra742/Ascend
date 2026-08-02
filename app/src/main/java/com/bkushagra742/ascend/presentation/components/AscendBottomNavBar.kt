package com.bkushagra742.ascend.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.bkushagra742.ascend.core.theme.AscendColors

/** Matches the 5-tab bottom nav visible across every screen in the mockup:
 * Home / Missions / Progress / Shop / Profile.
 *
 * Icons below are stock Material Symbols as a functional stand-in — the mockup's custom
 * icon style isn't in the asset set yet. Swap `iconFor()` to painterResource(R.drawable.ic_nav_*)
 * once those 5 vectors exist; no other code here needs to change. */
enum class AscendBottomTab(val label: String) {
    HOME("Home"),
    MISSIONS("Missions"),
    PROGRESS("Progress"),
    SHOP("Shop"),
    PROFILE("Profile"),
}

@Composable
fun AscendBottomNavBar(
    selectedTab: AscendBottomTab,
    onTabSelected: (AscendBottomTab) -> Unit,
) {
    NavigationBar(containerColor = AscendColors.Graphite) {
        AscendBottomTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(iconFor(tab), contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AscendColors.CrimsonRed,
                    selectedTextColor = AscendColors.CrimsonRed,
                    unselectedIconColor = AscendColors.CoolGray,
                    unselectedTextColor = AscendColors.CoolGray,
                    indicatorColor = AscendColors.SurfaceElevated,
                ),
            )
        }
    }
}

private fun iconFor(tab: AscendBottomTab) = when (tab) {
    AscendBottomTab.HOME -> Icons.Filled.Home
    AscendBottomTab.MISSIONS -> Icons.Filled.CheckCircle
    AscendBottomTab.PROGRESS -> Icons.Filled.TrendingUp
    AscendBottomTab.SHOP -> Icons.Filled.ShoppingCart
    AscendBottomTab.PROFILE -> Icons.Filled.Person
}
