package com.bkushagra742.ascend.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bkushagra742.ascend.domain.model.FocusSettings
import com.bkushagra742.ascend.domain.model.InstalledApp
import com.bkushagra742.ascend.domain.repository.FocusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusLockUiState(
    val isLoading: Boolean = true,
    val settings: FocusSettings = FocusSettings(),
    val installedApps: List<InstalledApp> = emptyList(),
)

@HiltViewModel
class FocusLockViewModel @Inject constructor(
    private val focusRepository: FocusRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusLockUiState())
    val uiState: StateFlow<FocusLockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val apps = focusRepository.getInstalledApps()
            focusRepository.observeSettings().collect { settings ->
                _uiState.value = FocusLockUiState(isLoading = false, settings = settings, installedApps = apps)
            }
        }
    }

    fun onToggleEnabled(enabled: Boolean) {
        viewModelScope.launch { focusRepository.setEnabled(enabled) }
    }

    fun onToggleApp(packageName: String, blocked: Boolean) {
        viewModelScope.launch { focusRepository.toggleBlockedPackage(packageName, blocked) }
    }
}
