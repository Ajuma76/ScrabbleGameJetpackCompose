package com.example.scrabble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.scrabble.data.MAX_NO_OF_WORDS
import com.example.scrabble.data.SCORE_INCREASE
import com.example.scrabble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {

    //Game Ui State
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    //used to save current scrambled word
    private lateinit var currentWord: String

    //mutable set to store used words in the game
    private val usedWords: MutableSet<String> = mutableSetOf()

   var guessedWord by mutableStateOf("")
    private set

    private fun pickRandomWordAndShuffle(): String {
        //Continue picking a random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun updateUserGuess(word: String){
        guessedWord = word
    }

    //Check user guess

    fun checkUserGuess(){
        if(guessedWord.equals(currentWord, ignoreCase = true)){
            //user guess is correct, update score
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)

        }else{
            //user guess is wrong, show an error
            _uiState.update{ currentState->
                currentState.copy(
                    isGuessedWordWrong = true,
                )
            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size.equals(MAX_NO_OF_WORDS)){
            //LAST round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGameOver = true,
                    score = updatedScore,
                    isGuessedWordWrong = false
                )
            }
        }else{
            //normal round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }
}