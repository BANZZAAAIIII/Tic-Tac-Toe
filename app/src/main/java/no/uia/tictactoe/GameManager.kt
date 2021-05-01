package no.uia.tictactoe

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.uia.tictactoe.utility.GameManagerCallback
import no.uia.tictactoe.utility.Marks
import no.uia.tictactoe.utility.State

object GameManager {
    private val LOG_TAG = "GameManager"

    var gameId: String? = null

    private val startingState: State = mutableListOf(
            mutableListOf("0", "0", "0"),
            mutableListOf("0", "0", "0"),
            mutableListOf("0", "0", "0")
    )

    private var _currentState = MutableLiveData(startingState)
    val currentState: LiveData<State> get() = _currentState

    private var _currentPlayer = MutableLiveData(Marks.blank)
    val currentPlayer: LiveData<String> get() = _currentPlayer

    private var _players =  MutableLiveData<List<String>>()
    val players: LiveData<List<String>> = _players

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage


    fun resetSnackbar(){
        _snackbarMessage.value = null
    }

    fun createGame(playerName: String, callback: GameManagerCallback) {
        GameService.createGame(playerName, startingState) { game, error ->
            if (error != null) {
                Log.e(LOG_TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"
            } else if (game != null) {
                reset()
                gameId = game.gameId
                _players.value = listOf(playerName)
                callback(Unit)
            }
        }
    }

    fun joinGame(playerName: String, id: String, callback: GameManagerCallback) {
        GameService.joinGame(playerName, id) { game, error ->
            if (error != null) {
                Log.e(LOG_TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"
            } else if (game != null) {
                reset()
                gameId = game.gameId
                _players.value = listOf(playerName)
                callback(Unit)
            }
        }
    }

    private fun reset() {
        gameId = null
        _players.value = null
        _currentState.value = startingState
        _currentPlayer.value = Marks.blank
    }

    private fun updateRemoteGame(state: State) {
        if (_players.value != null && gameId != null ) {
            GameService.updateGame(_players.value!!, gameId!!, state) { game, error ->
                if (error != null) {
                    Log.e(LOG_TAG, "Error connecting to server: $error")
                    _snackbarMessage.value = "Error connecting to server: $error"
                } else if (game != null) {
                    Log.v(LOG_TAG, "Updated game state: $game")
                    _currentState.value = game.state
                }
            }
        }
    }

    fun updateGame(newState: State) {
        updateState(newState) {
            updateRemoteGame(newState)
        }
    }

    fun startGame(): String? {
        if (gameId != null) {
            object : CountDownTimer(500000000, 5000) {
                override fun onFinish() {
                    _snackbarMessage.value = "Game has ended"
                }
                override fun onTick(millisUntilFinished: Long) {
                    pollGame(gameId!!)
                }
            }.start()

            return gameId
        } else {
            Log.e(LOG_TAG, "No gameId found, gameId: $gameId")
            _snackbarMessage.value = "No game ID found"
            return null
        }
    }

    private fun pollGame(gameId: String) {
        GameService.pollGame(gameId) { game, error ->
            if (error != null) {
                Log.e(LOG_TAG, "Error connecting to server: $error")
                _snackbarMessage.value = "Error connecting to server: $error"

            } else if (game != null) {
                with(game.players) {
                    if (_players.value != this) {
                        _players.value = this
                        _currentPlayer.value = Marks.X
                    }
                }

                with(game.state) {
                    updateState(this) {
                        _currentState.value = this
                    }
                }
            }
        }
    }

    private fun updateState(newState: State, updater: (Unit) -> Unit) {
        if (_currentState.value != newState) {
            _currentState.value!!.forEachIndexed{ i, list ->
                list.forEachIndexed{j, value ->
                    if (value != newState[i][j]) {
                        if (value == Marks.blank) {
                            updater.invoke(Unit)

                            when (newState[i][j]) {
                                Marks.O -> _currentPlayer.value = Marks.X
                                Marks.X -> _currentPlayer.value = Marks.O
                            }

                            // Assumes there is only one change in the state
                            return
                        }
                    }
                }
            }
        }
    }
}