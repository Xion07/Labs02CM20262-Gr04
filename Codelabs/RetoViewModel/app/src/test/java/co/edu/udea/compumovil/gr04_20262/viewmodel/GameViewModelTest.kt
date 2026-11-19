package co.edu.udea.compumovil.gr04_20262.viewmodel

import co.edu.udea.compumovil.gr04_20262.viewmodel.data.MAX_NO_OF_WORDS
import co.edu.udea.compumovil.gr04_20262.viewmodel.data.SCORE_INCREASE
import co.edu.udea.compumovil.gr04_20262.viewmodel.data.allWords
import co.edu.udea.compumovil.gr04_20262.viewmodel.ui.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameViewModelTest {
    private val viewModel = GameViewModel()

    // Busca la palabra original a partir de la desordenada (mismas letras)
    private fun getUnscrambledWord(scrambled: String): String =
        allWords.first { it.toCharArray().sorted() == scrambled.toCharArray().sorted() }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val state = viewModel.uiState.value
        assertTrue(state.currentScrambledWord.isNotEmpty())
        assertNotEquals(getUnscrambledWord(state.currentScrambledWord), state.currentScrambledWord)
        assertEquals(0, state.score)
        assertEquals(1, state.currentWordCount)
        assertFalse(state.isGuessedWordWrong)
        assertFalse(state.isGameOver)
    }

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        viewModel.updateUserGuess(getUnscrambledWord(viewModel.uiState.value.currentScrambledWord))
        viewModel.checkUserGuess()
        val state = viewModel.uiState.value
        assertFalse(state.isGuessedWordWrong)
        assertEquals(SCORE_INCREASE, state.score)
        assertEquals(2, state.currentWordCount)
        assertEquals("", viewModel.userGuess)
    }

    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        viewModel.updateUserGuess("and")
        viewModel.checkUserGuess()
        val state = viewModel.uiState.value
        assertTrue(state.isGuessedWordWrong)
        assertEquals(0, state.score)
        assertEquals(1, state.currentWordCount)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        repeat(MAX_NO_OF_WORDS) {
            expectedScore += SCORE_INCREASE
            viewModel.updateUserGuess(getUnscrambledWord(viewModel.uiState.value.currentScrambledWord))
            viewModel.checkUserGuess()
            assertEquals(expectedScore, viewModel.uiState.value.score)
        }
        assertEquals(MAX_NO_OF_WORDS, viewModel.uiState.value.currentWordCount)
        assertTrue(viewModel.uiState.value.isGameOver)
    }

    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        viewModel.skipWord()
        val state = viewModel.uiState.value
        assertEquals(0, state.score)
        assertEquals(2, state.currentWordCount)
    }

    @Test
    fun gameViewModel_ResetGame_StateRestored() {
        viewModel.updateUserGuess(getUnscrambledWord(viewModel.uiState.value.currentScrambledWord))
        viewModel.checkUserGuess()
        viewModel.resetGame()
        val state = viewModel.uiState.value
        assertEquals(0, state.score)
        assertEquals(1, state.currentWordCount)
        assertFalse(state.isGameOver)
    }

    @Test
    fun gameViewModel_SkipAfterWrongGuess_ErrorFlagCleared() {
        viewModel.updateUserGuess("and")
        viewModel.checkUserGuess()
        assertTrue(viewModel.uiState.value.isGuessedWordWrong)
        viewModel.skipWord()
        assertFalse(viewModel.uiState.value.isGuessedWordWrong)
    }

    @Test
    fun gameViewModel_ResetGame_ClearsGuessAndScore() {
        viewModel.updateUserGuess(getUnscrambledWord(viewModel.uiState.value.currentScrambledWord))
        viewModel.checkUserGuess()
        viewModel.updateUserGuess("abc")
        viewModel.resetGame()
        assertEquals("", viewModel.userGuess)
        assertEquals(0, viewModel.uiState.value.score)
    }
}
