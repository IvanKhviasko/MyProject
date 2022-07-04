package space.kis.myproject.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import space.kis.myproject.domain.CountryModel
import space.kis.myproject.domain.QuizRepository
import space.kis.myproject.util.Constants.ANSWERS_TO_WIN_COUNT
import space.kis.myproject.util.Event
import space.kis.myproject.util.Resource
import space.kis.myproject.util.Status
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private var allCountries: MutableList<CountryModel>? = null
    private var solution: CountryModel? = null

    private var correctAnswerCounter = 0
    private var incorrectAnswerCounter = 0

    private var timer = AtomicInteger(30)
    private var timerJob: Job? = null
    private val timerFlow = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }.flowOn(Dispatchers.IO).onEach {
        tick()
    }

    val questions = MutableLiveData<Event<Resource<List<CountryModel>>>>()
    val correctAnswersCount = MutableLiveData<Event<Int>>()
    val time = MutableLiveData<Event<Int>>()
    val extraTime = MutableLiveData<Event<Int>>()
    val result = MutableLiveData<Event<Pair<Int, Int>>>()
    val gameWon = MutableLiveData<Event<Pair<Int, Int>>>()

    fun play() {
        allCountries?.let {
            setJob()
            it.shuffle()
            prepareQuestions(it.take(4))
        } ?: getCountries()
    }

    private fun setJob() {
        if (timerJob == null)
            timerJob = createJob()
    }

    private fun createJob() = timerFlow.launchIn(viewModelScope)

    private fun tick() {
        time.value = Event(timer.decrementAndGet())
        if (timer.get() <= 0)
            endGame()
    }

    private fun prepareQuestions(list: List<CountryModel>) {
        val correctAnswerIndex = Random.nextInt(0, 4)
        for ((index, value) in list.withIndex()) {
            value.correct = correctAnswerIndex == index
        }
        solution = list[correctAnswerIndex]
        questions.value = Event(Resource(Status.SUCCESS, list))
    }

    private fun getCountries() {
        questions.value = Event(Resource(Status.LOADING, null))
        viewModelScope.launch {
            repository.getAllCountries().let {
                if (it.status == Status.SUCCESS) {
                    allCountries = it.data?.toMutableList()
                    play()
                } else
                    questions.value = Event(Resource(Status.ERROR, null))
            }
        }
    }

    fun answer(country: CountryModel) {
        when (country) {
            solution -> handleCorrectAnswer()
            else -> handleIncorrectAnswer()
        }
    }

    private fun handleCorrectAnswer() {
        correctAnswerCounter++
        if (correctAnswerCounter == ANSWERS_TO_WIN_COUNT) {
            winGame()
            return
        }
        updateCounter(1)
        play()
        correctAnswersCount.value = Event(correctAnswerCounter)
    }

    private fun handleIncorrectAnswer() {
        incorrectAnswerCounter++
        updateCounter(-1)
    }

    private fun updateCounter(value: Int) {
        extraTime.value = Event(value)
        if (timer.addAndGet(value) <= 0)
            endGame()
    }

    private fun winGame() {
        finishTimerJob()
        resetUI()

        gameWon.value = Event(Pair(correctAnswerCounter, incorrectAnswerCounter))

        resetValues()
    }

    private fun endGame() {
        finishTimerJob()
        resetUI()

        result.value = Event(Pair(correctAnswerCounter, incorrectAnswerCounter))

        resetValues()
    }

    private fun finishTimerJob() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun resetUI() {
        time.value = Event(30)
        correctAnswersCount.value = Event(0)
    }

    private fun resetValues() {
        timer.addAndGet(30)
        correctAnswerCounter = 0
        incorrectAnswerCounter = 0
    }
}