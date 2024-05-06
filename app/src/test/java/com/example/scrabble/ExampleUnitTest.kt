package com.example.scrabble

import com.example.scrabble.data.MAX_NO_OF_WORDS
import com.example.scrabble.data.SCORE_INCREASE
import com.example.scrabble.data.getUnscrambledWord
import com.example.scrabble.ui.GameViewModel
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameViewModelTest {
    private val viewModel = GameViewModel()

    //success path/ happy path
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        //get the current game ui state
        var currentGameUiState = viewModel.uiState.value

        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value

        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(currentGameUiState.isGuessedWordWrong)
        // Assert that score is updated correctly.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value

        // Assert that checkUserGuess() method updates isGuessedWordWrong correctly
        assertTrue(currentGameUiState.isGuessedWordWrong)

        // Assert that score is unchanged
        assertEquals(0, currentGameUiState.score)

    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameUiState.currentWordCount == 1)
        //Assert that the initial score is 0
        assertTrue(gameUiState.score == 0)
        //Assert that the wrong word guessed is false
        assertFalse(gameUiState.isGuessedWordWrong)
        //Assert that the game is not over
        assertFalse(gameUiState.isGameOver)
    }
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly(){
        var expectedScore = 0

        var currentGameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        repeat(MAX_NO_OF_WORDS){
            expectedScore+= SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            currentGameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, currentGameUiState.score)
        }
        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, currentGameUiState.currentWordCount)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(currentGameUiState.isGameOver)

    }
    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased(){
        var currentGameUiState = viewModel.uiState.value
        val currentPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(currentPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        // Assert that score remains unchanged after word is skipped.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, currentGameUiState.score)
        //Assert that word count is increased by 1 after word is skipped
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)

    }

    companion object {
        private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
    }
}