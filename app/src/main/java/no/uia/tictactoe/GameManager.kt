package no.uia.tictactoe

import android.content.Context
import android.util.Log
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.State

object GameManager {
    private val LOG_TAG = "GameManager"
    private val context = App.context

    private val startingState: State = listOf(listOf(0,0,0),listOf(0,0,0),listOf(0,0,0))

    fun createGame(playerName: String) {
        GameService.createGame(playerName, startingState) { game, error ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                saveToPref(game.players, game.gameId)
            }
        }
    }

    fun joinGame(playerName: String, id: String) {
        GameService.joinGame(playerName, id) { game, error ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                saveToPref(game.players, game.gameId)
            }
        }
    }

    fun updateGame(state: State) {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        val player1 = preference.getString(context.getString(R.string.Pref_Player1), "")
        val gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
        val players: List<String> = listOf<String>(player1.toString())

        GameService.updateGame(players, gameId!!, state) { game, error ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                Log.v(LOG_TAG, "Updated game state: $game")
            }
        }
    }

    fun pollGame() {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        val gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
        GameService.pollGame(gameId!!) { game, error ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                Log.v(LOG_TAG, "Polled game state: $game")
            }
        }
    }


    private fun saveToPref(players: List<String>, gameId: String) {
        val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        with(preference.edit()) {
            putString(context.getString(R.string.Pref_Game_ID), gameId)

            putString(context.getString(R.string.Pref_Player1), players[0])
            if (players.size > 1){
                putString(context.getString(R.string.Pref_Player1), players[1])
            }
            apply() // use apply() for async saving
        }
    }
}