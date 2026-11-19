package co.edu.udea.compumovil.gr04_20262.viewmodel.ui

// Estado de la UI del juego
data class GameUiState(
    val currentScrambledWord: String = "",
    val currentWordCount: Int = 1,
    val score: Int = 0,
    val isGuessedWordWrong: Boolean = false,
    val isGameOver: Boolean = false
)
