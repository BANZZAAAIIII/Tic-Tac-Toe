package no.uia.tictactoe

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.data.GameState
import org.json.JSONObject

object GameService {

    private val LOG_TAG = "GameService"

    private val context = App.context

    private val requestQue: RequestQueue = Volley.newRequestQueue(context)

    private enum class Endpoints(val url: String) {
//        CREATE_GAME("https://generic-game-service.herokuapp.com/Game"),
        CREATE_GAME("${context.getString(R.string.hostname)}${context.getString(R.string.create_game)}"),
        UPDATE_GAME("${context.getString(R.string.hostname)}${context.getString(R.string.Game_ID)}${context.getString(R.string.poll_game)}"),
        JOIN_GAME("${context.getString(R.string.hostname)}${context.getString(R.string.Game_ID)}${context.getString(R.string.poll_game)}"),
        POLL_GAME("${context.getString(R.string.hostname)}${context.getString(R.string.Game_ID)}${context.getString(R.string.poll_game)}")
    }


    fun createGame(player: String) {
        val data = JSONObject()
        data.put("player", player)
        data.put("state", null)

        Log.v(LOG_TAG,"Payload: $data")
        val request = object : JsonObjectRequest(Method.POST, Endpoints.CREATE_GAME.url, data,
            Response.Listener { response ->
                Log.v(LOG_TAG, "Response from server: $response")
                val gameState = Gson().fromJson(response.toString(), GameState::class.java)
                println(gameState)
            },
            Response.ErrorListener { error ->
                Log.e(LOG_TAG, "Error connecting to server: $error")
            }
        ) { override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json"
                header["Game-Service-Key"] = context.getString(R.string.API_key)
                Log.v(LOG_TAG, "Header: $header")
                return header
            }
        }

        requestQue.add(request)
    }

    fun updateGame(game_ID: String) {

    }

    fun joinGame(game_ID: String) {
        val data = JSONObject()
        data.put("player", "Test")
        data.put("gameId", context.getString(R.string.Game_ID))

        Log.v(LOG_TAG,"Payload: $data")
        val request = object : JsonObjectRequest(Method.POST, Endpoints.JOIN_GAME.url, data,
            Response.Listener { response ->
                Log.v(LOG_TAG, "Response from server: $response")

                val gameState = Gson().fromJson(response.toString(), GameState::class.java)

                val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
                with(preference.edit()) {
                    putString(context.getString(R.string.Pref_Game_ID), "Game ID here")
                    commit() // use apply() for async saving
                }
            },
            Response.ErrorListener { error ->
                Log.e(LOG_TAG, "Error connecting to server: $error")
            }
        ) { override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.API_key)
                Log.v(LOG_TAG,"Headers: $headers")
                return headers
            }
        }

        requestQue.add(request)
    }

    fun pollGame(player: String) {

    }

}

