package no.uia.tictactoe

import android.content.Context
import android.util.Log
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.data.State
import no.uia.tictactoe.utility.App

object GameManager {
    private val LOG_TAG = "GameManager"
    private val context = App.context

    private val startingState: State = listOf(listOf(0,0,0),listOf(0,0,0),listOf(0,0,0))

    var player1: String? = null
    var player2: String? = null
    var gameId: String? = null

    fun createGame(playerName: String) {

        GameService.createGame(playerName, startingState) { game: GameState?, error: Int? ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
                with(preference.edit()) {
                    putString(context.getString(R.string.Pref_Game_ID), game.gameId)
                    putString(context.getString(R.string.Pref_Player1), game.players[0])
                    commit() // use apply() for async saving
                }
            }
        }
    }

    fun joinGame(playerName: String, id: String) {
        GameService.joinGame(playerName, id) { game: GameState?, error: Int? ->
            if (error != null) {
                // Toast or something
                Log.e(LOG_TAG, "Error connecting to server: $error")

            } else if (game != null) {
                val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
                with(preference.edit()) {
                    putString(context.getString(R.string.Pref_Game_ID), game.gameId)
                    putString(context.getString(R.string.Pref_Player1), game.players[0])
                    commit() // use apply() for async saving
                }
            }
        }
    }
}