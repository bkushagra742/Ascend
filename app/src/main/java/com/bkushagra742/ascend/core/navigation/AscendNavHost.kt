package com.bkushagra742.ascend.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bkushagra742.ascend.presentation.dashboard.DashboardScreen
import com.bkushagra742.ascend.presentation.focus.FocusLockScreen
import com.bkushagra742.ascend.presentation.habitcreate.CreateHabitScreen
import com.bkushagra742.ascend.presentation.habits.HabitsScreen
import com.bkushagra742.ascend.presentation.questcreate.CreateQuestScreen
import com.bkushagra742.ascend.presentation.quests.QuestsScreen

@Composable
fun AscendNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AscendDestination.Dashboard.route,
    ) {
        composable(AscendDestination.Dashboard.route) {
            DashboardScreen(
                onNavigateToQuests = { navController.navigate(AscendDestination.Quests.route) },
                onNavigateToHabits = { navController.navigate(AscendDestination.Habits.route) },
            )
        }
        composable(AscendDestination.Quests.route) {
            QuestsScreen(
                onBack = { navController.popBackStack() },
                onAddMission = { navController.navigate(AscendDestination.CreateQuest.route) },
            )
        }
        composable(AscendDestination.Habits.route) {
            HabitsScreen(
                onBack = { navController.popBackStack() },
                onAddHabit = { navController.navigate(AscendDestination.CreateHabit.route) },
                onOpenFocusLock = { navController.navigate(AscendDestination.FocusLock.route) },
            )
        }
        composable(AscendDestination.CreateQuest.route) {
            CreateQuestScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(AscendDestination.CreateHabit.route) {
            CreateHabitScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }
        composable(AscendDestination.FocusLock.route) {
            FocusLockScreen(onBack = { navController.popBackStack() })
        }
    }
}
