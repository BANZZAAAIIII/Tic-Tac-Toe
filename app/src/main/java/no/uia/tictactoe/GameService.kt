package no.uia.tictactoe

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.data.State
import no.uia.tictactoe.utility.App
import org.json.JSONArray
import org.json.JSONObject


typealias GameServiceCallback = (game: GameState?, error: Int?) -> Unit

object GameService {
    private val LOG_TAG = "GameService"

    private val context = App.context

    private val requestQue: RequestQueue = Volley.newRequestQueue(context)

    // Change policy to avoid timeout error when heroku server is waking up
    val retryPolicy: RetryPolicy = DefaultRetryPolicy(
        context.getString(R.string.TIMEOUT_MS).toInt(),
        context.getString(R.string.MAX_RETRIES).toInt(),
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )


    private enum class APICall() {
        CREATE_GAME,
        UPDATE_GAME,
        JOIN_GAME,
        POLL_GAME
    }

    private fun getEndpoint(call: APICall, gameId: String? = null): String? {
        val basePath = context.getString(R.string.hostname)
        return when {
            (call == APICall.CREATE_GAME) -> {
                return basePath
            }

            (call == APICall.JOIN_GAME) -> {
//                val preference = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
//                val gameId = preference.getString(context.getString(R.string.Pref_Game_ID), "")
                return "$basePath/$gameId/${context.getString(R.string.join_game)}" // TODO: Make this pretty
            }

            else -> null
        }
    }


    fun createGame(player: String, state: State, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("state", JSONArray(state))

        Log.v(LOG_TAG, "Payload: $data")
        val request = object : JsonObjectRequest(Method.POST,
            getEndpoint(APICall.CREATE_GAME),
            data,
            Response.Listener { response ->
                Log.v(LOG_TAG, "Response from server: $response")

                val gameState = Gson().fromJson(response.toString(), GameState::class.java)
                callback(gameState, null)
            },
            Response.ErrorListener { error ->
                Log.e(LOG_TAG, "Volley Error: $error")
                callback(null, error.networkResponse.statusCode)
            }
        ) { override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json"
                header["Game-Service-Key"] = context.getString(R.string.API_key)
//                Log.v(LOG_TAG, "Header: $header")
                return header
            }
        }

        requestQue.add(request)
    }

    fun updateGame(game_ID: String) {

    }

    fun joinGame(player: String, gameId: String, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("gameId", gameId)

        Log.v(LOG_TAG, "Payload: $data")
        val request = object : JsonObjectRequest(Method.POST,
            getEndpoint(APICall.JOIN_GAME, gameId),
            data,
            Response.Listener { response ->
                Log.v(LOG_TAG, "Response from server: $response")

                val gameState = Gson().fromJson(response.toString(), GameState::class.java)
                callback(gameState, null)
            },
            Response.ErrorListener { error ->
                Log.e(LOG_TAG, "Volley Error: $error")
                callback(null, error.networkResponse.statusCode)
            }
        ) { override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.API_key)
//                Log.v(LOG_TAG,"Headers: $headers")
                return headers
            }
        }

        requestQue.add(request)
    }

    fun pollGame(player: String) {

    }


}

