package com.chopcal.app.page.home

import androidx.lifecycle.viewModelScope
import com.chopcal.app.domain.model.MacroTargets
import com.chopcal.app.domain.repository.RecordRepository
import com.chopcal.app.domain.repository.UserProfileRepository
import com.chopcal.app.domain.usecase.ComputeMacroTargetsUseCase
import com.chopcal.app.domain.usecase.GetDailySummaryUseCase
import com.rainbow.compose.core.contract.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: UserProfileRepository,
    private val recordRepository: RecordRepository,
    private val getDailySummary: GetDailySummaryUseCase,
    private val computeMacroTargets: ComputeMacroTargetsUseCase,
) : BaseMviViewModel<HomeIntent, HomeEffect>() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now().toEpochDay())

    init {
        val today = LocalDate.now()
        val todayEpochDay = today.toEpochDay()
        _selectedDate.value = todayEpochDay
        _uiState.value = HomeUiState(
            loading = true,
            selectedDateEpochDay = todayEpochDay,
            todayEpochDay = todayEpochDay,
            week = buildWeek(todayEpochDay, todayEpochDay),
        )

        viewModelScope.launch {
            val target = profileRepository.dailyTarget.first()
            val profile = profileRepository.profile.first()
            val device = profileRepository.device.first()
            _uiState.update {
                it.copy(
                    dailyTarget = target,
                    goalType = profile?.goalType,
                    nickname = device.nickname,
                    macroTargets = target?.let(computeMacroTargets::invoke) ?: MacroTargets(),
                )
            }
        }

        viewModelScope.launch {
            _selectedDate.collectLatest { date ->
                getDailySummary(date).collect { summary ->
                    _uiState.update { it.copy(summary = summary, loading = false) }
                }
            }
        }
    }

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectDate -> onSelectDate(intent.epochDay)
            is HomeIntent.SetWaterMl -> onSetWaterMl(intent.ml)
            is HomeIntent.SetStepCount -> onSetStepCount(intent.steps)
        }
    }

    private fun onSelectDate(epochDay: Long) {
        val today = _uiState.value.todayEpochDay
        if (epochDay > today) return
        _selectedDate.value = epochDay
        _uiState.update {
            it.copy(
                selectedDateEpochDay = epochDay,
                week = buildWeek(today, epochDay),
            )
        }
    }

    private fun buildWeek(todayEpochDay: Long, selectedEpochDay: Long): List<DayItem> {
        val selected = LocalDate.ofEpochDay(selectedEpochDay)
        val monday = selected.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return (0L until 7L).map { i ->
            val date = monday.plusDays(i)
            DayItem(
                epochDay = date.toEpochDay(),
                weekdayLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                dayOfMonth = date.dayOfMonth,
                isToday = date.toEpochDay() == todayEpochDay,
                isFuture = date.toEpochDay() > todayEpochDay,
                isSelected = date.toEpochDay() == selectedEpochDay,
            )
        }
    }

    private suspend fun onSetWaterMl(ml: Int) {
        recordRepository.setWaterMl(_uiState.value.selectedDateEpochDay, ml)
    }

    private suspend fun onSetStepCount(steps: Int) {
        recordRepository.setStepCount(_uiState.value.selectedDateEpochDay, steps)
    }
}
