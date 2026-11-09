package com.fractionpie.dojo.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fractionpie.dojo.core.config.GameConfig
import com.fractionpie.dojo.domain.model.Belt
import com.fractionpie.dojo.domain.model.Bout
import com.fractionpie.dojo.domain.model.BoutResult
import com.fractionpie.dojo.domain.model.BoutType
import com.fractionpie.dojo.domain.model.SessionResult
import com.fractionpie.dojo.domain.repository.BeltRepository
import com.fractionpie.dojo.domain.repository.OptionsRepository
import com.fractionpie.dojo.domain.usecase.AwardBeltUseCase
import com.fractionpie.dojo.domain.usecase.CheckAnswerUseCase
import com.fractionpie.dojo.domain.usecase.GenerateBoutUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class GameViewModel(
    beltIndex: Int,
    beltRepository: BeltRepository,
    optionsRepository: OptionsRepository,
    private val generateBout: GenerateBoutUseCase,
    private val checkAnswer: CheckAnswerUseCase,
    private val awardBelt: AwardBeltUseCase,
) : ViewModel() {

    private val belts: List<Belt> = beltRepository.belts()
    private val belt: Belt = belts[beltIndex.coerceIn(0, belts.size - 1)]
    private val random: Random = Random.Default

    private var bout: Bout = generateBout(belt, 0, random)
    private var feedbackJob: Job? = null
    private var bladeDrag: Float = 0f
    private var longestStreak: Int = 0

    private val _state = MutableStateFlow(
        GameUiState(
            phase = Phase.READY,
            beltIndex = belt.index,
            beltNameRes = belt.nameRes,
            beltColor = belt.color,
            boutType = bout.type,
            boutIndex = 0,
            boutCount = GameConfig.BOUTS_PER_SESSION,
            target = bout.target,
            addendA = bout.addendA,
            addendB = bout.addendB,
            sliceCount = startingSlices(bout),
            filled = emptySet(),
            bladeAngle = 0f,
            options = bout.options,
            selectedOption = -1,
            left = bout.left,
            right = bout.right,
            pickedSide = -1,
            leftCorrect = bout.leftIsLarger,
            score = 0,
            streak = 0,
            misses = 0,
            clean = 0,
            results = emptyList(),
            revealSlices = bout.canonicalSlices,
            revealFilled = bout.canonicalFilled,
            largeNumerals = optionsRepository.largeNumerals(),
            hapticsOn = optionsRepository.hapticsOn(),
            soundOn = optionsRepository.soundOn(),
            paused = false,
        ),
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<SessionResult>(Channel.BUFFERED)
    val events: Flow<SessionResult> = _events.receiveAsFlow()

    fun attach() {
        val current = _state.value
        if (current.paused) return
        if (current.phase == Phase.CORRECT || current.phase == Phase.WRONG) {
            scheduleAdvance(0L)
        }
    }

    fun pause() {
        feedbackJob?.cancel()
        feedbackJob = null
    }

    fun requestPause() {
        feedbackJob?.cancel()
        feedbackJob = null
        _state.update { it.copy(paused = true) }
    }

    fun resume() {
        _state.update { it.copy(paused = false) }
        attach()
    }

    fun onBladeDrag(delta: Float) {
        if (_state.value.inputLocked) return
        if (_state.value.boutType != BoutType.SLICE) return
        bladeDrag += delta
        applySliceCount(
            GameConfig.MIN_SLICES + (bladeDrag / GameConfig.BLADE_DRAG_PER_SLICE_PX).roundToInt(),
        )
    }

    fun onSliceDelta(delta: Int) {
        if (_state.value.inputLocked) return
        if (_state.value.boutType != BoutType.SLICE) return
        val next = _state.value.sliceCount.coerceAtLeast(GameConfig.MIN_SLICES) + delta
        bladeDrag = (next - GameConfig.MIN_SLICES) * GameConfig.BLADE_DRAG_PER_SLICE_PX
        applySliceCount(next)
    }

    fun onWedgeTap(index: Int) {
        val current = _state.value
        if (current.inputLocked) return
        if (current.boutType != BoutType.SLICE) return
        if (current.sliceCount < GameConfig.MIN_SLICES) return
        val filled = current.filled.toMutableSet()
        if (!filled.add(index)) filled.remove(index)
        _state.update { it.copy(filled = filled) }
    }

    fun onPickOption(index: Int) {
        val current = _state.value
        if (current.inputLocked) return
        if (current.boutType != BoutType.MATCH) return
        _state.update { it.copy(selectedOption = index) }
    }

    fun onPickSide(side: Int) {
        val current = _state.value
        if (current.inputLocked) return
        if (current.boutType != BoutType.COMPARE) return
        _state.update { it.copy(pickedSide = side) }
        resolve(checkAnswer.checkCompare(bout, side))
    }

    fun onServe() {
        val current = _state.value
        if (current.inputLocked) return
        val correct = when (current.boutType) {
            BoutType.SLICE -> checkAnswer.checkSlice(bout, current.sliceCount, current.filled.size)
            BoutType.MATCH -> checkAnswer.checkMatch(bout, current.selectedOption)
            BoutType.COMPARE -> checkAnswer.checkCompare(bout, current.pickedSide)
        }
        resolve(correct)
    }

    private fun applySliceCount(raw: Int) {
        val count = raw.coerceIn(GameConfig.MIN_SLICES, GameConfig.MAX_SLICES)
        if (count == _state.value.sliceCount) return
        _state.update {
            it.copy(
                phase = Phase.SLICING,
                sliceCount = count,
                filled = emptySet(),
                bladeAngle = it.bladeAngle + 360f / count,
            )
        }
    }

    private fun resolve(correct: Boolean) {
        feedbackJob?.cancel()
        val current = _state.value
        val streak = if (correct) current.streak + 1 else 0
        longestStreak = maxOf(longestStreak, streak)
        val bonus = if (correct && streak > 1) {
            minOf(GameConfig.STREAK_BONUS * (streak - 1), GameConfig.STREAK_BONUS_CAP)
        } else {
            0
        }
        val gain = if (correct) GameConfig.SCORE_BASE + bonus else 0
        val result = BoutResult(
            target = bout.target,
            slices = bout.canonicalSlices,
            filled = bout.canonicalFilled,
            cleared = correct,
        )
        _state.update {
            it.copy(
                phase = if (correct) Phase.CORRECT else Phase.WRONG,
                score = it.score + gain,
                streak = streak,
                misses = it.misses + if (correct) 0 else 1,
                clean = it.clean + if (correct) 1 else 0,
                results = it.results + result,
                revealSlices = bout.canonicalSlices,
                revealFilled = bout.canonicalFilled,
            )
        }
        val animation = if (correct) {
            GameConfig.CORRECT_SPIN_MS.toLong()
        } else {
            GameConfig.WRONG_FAN_MS.toLong()
        }
        scheduleAdvance(animation + GameConfig.FEEDBACK_HOLD_MS)
    }

    private fun scheduleAdvance(waitMs: Long) {
        feedbackJob?.cancel()
        feedbackJob = viewModelScope.launch {
            if (waitMs > 0L) delay(waitMs)
            advance()
        }
    }

    private fun advance() {
        val current = _state.value
        val nextIndex = current.boutIndex + 1
        if (nextIndex >= GameConfig.BOUTS_PER_SESSION) {
            _state.update { it.copy(phase = Phase.FINISHED) }
            val session = awardBelt(
                beltIndex = belt.index,
                score = current.score,
                clean = current.clean,
                misses = current.misses,
                longestStreak = longestStreak,
                results = current.results,
            )
            _events.trySend(session)
            return
        }
        bout = generateBout(belt, nextIndex, random)
        bladeDrag = 0f
        _state.update {
            it.copy(
                phase = Phase.READY,
                boutType = bout.type,
                boutIndex = nextIndex,
                target = bout.target,
                addendA = bout.addendA,
                addendB = bout.addendB,
                sliceCount = startingSlices(bout),
                filled = emptySet(),
                options = bout.options,
                selectedOption = -1,
                left = bout.left,
                right = bout.right,
                pickedSide = -1,
                leftCorrect = bout.leftIsLarger,
                revealSlices = bout.canonicalSlices,
                revealFilled = bout.canonicalFilled,
            )
        }
    }

    private fun startingSlices(value: Bout): Int =
        if (value.type == BoutType.SLICE) 1 else value.canonicalSlices
}
