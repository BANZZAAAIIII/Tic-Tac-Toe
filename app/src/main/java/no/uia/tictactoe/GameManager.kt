package no.uia.tictactoe

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.GameManagerCallback
import no.uia.tictactoe.utility.Marks
import no.uia.tictactoe.utility.State

object GameManager {
    private val LOG_TAG = "GameManager"
    private val context = App.context

    private var gameId: String? = null

    private var _players =  MutableLiveData<List<String>>()
    val players: LiveData<List<String>> = _players


    private val startingState: State = mutableListOf(
            mutableListOf("0", "0", "0"),
            mutableListOf("0", "0", "0"),
            mutableListOf("0", "0", "0")
    )

    private var _currentState = MutableLiveData(startingState)
    val currentState: LiveData<State> get() = _currentState

    private var _currentPlayer = MutableLiveData<String>()
    val currentPlayer: LiveData<String> get() = _currentPlayer

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
                saveToPref(game.players, game.gameId)
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
                saveToPref(game.players, game.gameId)
                callback(Unit)
            }
        }
    }

    private fun updateGame(state: State) {
        if (_players.value != null && gameId != null ) {
            GameService.updateGame(_players.value!!, gameId!!, state) { game, error ->
                if (error != null) {
                    Log.e(LOG_TAG, "Error connecting to server: $error")
                    _snackbarMessage.value = "Error connecting to server: $error"
                } else if (game != null) {
//                    Log.v(LOG_TAG, "Updated game state: $game")
                    _currentState.value = game.state
                }
            }
        }
    }

    fun updateState(newState: State) {
        updateBoard(newState) {
            updateGame(newState)
        }
    }

    fun startGame(mark: String, player: String) {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
        _players.value = listOf(player)


        object : CountDownTimer(500000000, 5000) {
            override fun onFinish() {
                _snackbarMessage.value = "Game has ended"
            }
            override fun onTick(millisUntilFinished: Long) {
                pollGame(gameId!!)
            }
        }.start()
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
                    updateBoard(this) {
                        _currentState.value = this
                    }
                }
            }
        }
    }

    private fun updateBoard(newState: State, updater: (Unit) -> Unit) {
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

    private fun saveToPref(players: List<String>, gameId: String) {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        with(preference.edit()) {
            putString(context.getString(R.string.Pref_Game_ID), gameId)

            putString(context.getString(R.string.Pref_Player1), players[0])
            if (players.size > 1){
                putString(context.getString(R.string.Pref_Player2), players[1])
            }
            apply() // use apply() for async saving
        }
    }
}