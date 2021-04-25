package no.uia.tictactoe

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.GameManagerCallback
import no.uia.tictactoe.utility.State

object GameManager {
    private val LOG_TAG = "GameManager"
    private val context = App.context

    private var gameId: String? = null
    private var _players =  MutableLiveData<List<String>>()
    val players: LiveData<List<String>> = _players

    private val startingState: State = listOf(listOf(0,0,0),listOf(0,0,0),listOf(0,0,0))

    private var _state = MutableLiveData<State>(startingState)
    val state: LiveData<State> get() = _state

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> get() = _snackbarMessage


    fun resetSnackbar(){
        _snackbarMessage.value = ""
    }

    fun createGame(playerName: String, callback: GameManagerCallback) {
        GameService.createGame(playerName, startingState) { game, error ->
            if (error != null) {
                _snackbarMessage.value = "Error connecting to server: $error"
                Log.e(LOG_TAG, "Error connecting to server: $error")
            } else if (game != null) {
                saveToPref(game.players, game.gameId)
                callback(Unit)
            }
        }
    }

    fun joinGame(playerName: String, id: String, callback: GameManagerCallback) {
        GameService.joinGame(playerName, id) { game, error ->
            if (error != null) {
                _snackbarMessage.value = "Error connecting to server: $error"
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                saveToPref(game.players, game.gameId)
                callback(Unit)
            }
        }
    }

    fun updateGame(state: State) {
        GameService.updateGame(_players.value!!, gameId!!, state) { game, error ->
            if (error != null) {
                _snackbarMessage.value = "Error connecting to server: $error"
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                Log.v(LOG_TAG, "Updated game state: $game")
                _state.value = game.state
            }
        }
    }

    fun startGame() {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        val player1 = preference.getString(context.getString(R.string.Pref_Player1), "")

        gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
        _players.value = listOf(player1.toString())

        object : CountDownTimer(500000000, 5000) {
            override fun onFinish() {
                _snackbarMessage.value = "Game has ended"
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.v(LOG_TAG,"Polling")
                pollGame()
            }
        }.start()
    }

    private fun pollGame() {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        val gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
        GameService.pollGame(gameId!!) { game, error ->
            if (error != null) {
                _snackbarMessage.value = "Error connecting to server: $error"
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                Log.v(LOG_TAG, "Polled game state: $game")
                val p = game.players
                if (p != _players.value)
                    _players.value = p

                val s = game.state
                if (s != _state.value)
                    _state.value = s
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