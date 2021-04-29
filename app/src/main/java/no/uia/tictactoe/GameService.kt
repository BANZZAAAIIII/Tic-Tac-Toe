package no.uia.tictactoe

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.GameServiceCallback
import no.uia.tictactoe.utility.State
import org.json.JSONArray
import org.json.JSONObject
import java.lang.NullPointerException


object GameService {
    private val LOG_TAG = "GameService"

    private val context = App.context

    private val requestQue: RequestQueue = Volley.newRequestQueue(context)

    private enum class APICall {
        CREATE_GAME,
        UPDATE_GAME,
        JOIN_GAME,
        POLL_GAME
    }

    private fun getEndpoint(call: APICall, gameId: String? = null): String {
        val basePath = context.getString(R.string.hostname)
        return when {
            (call == APICall.CREATE_GAME) -> basePath
            (call == APICall.JOIN_GAME)   -> "$basePath/$gameId/${context.getString(R.string.join_game)}"
            (call == APICall.POLL_GAME)   -> "$basePath/$gameId/${context.getString(R.string.poll_game)}"
            (call == APICall.UPDATE_GAME) -> "$basePath/$gameId/${context.getString(R.string.update_game)}"
            else -> ""
        }
    }

    fun createGame(player: String, state: State, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("state", JSONArray(state))

        request(data, getEndpoint(APICall.CREATE_GAME), callback)
    }

    fun updateGame(player: List<String>, gameId: String, state: State, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("gameId", gameId)
        data.put("state", JSONArray(state))

        request(data, getEndpoint(APICall.UPDATE_GAME, gameId), callback)
    }

    fun joinGame(player: String, gameId: String, callback: GameServiceCallback) {
        val data = JSONObject()
        data.put("player", player)
        data.put("gameId", gameId)

        Log.v(LOG_TAG, "Payload: $data")
        request(data, getEndpoint(APICall.JOIN_GAME, gameId), callback)
    }

    fun pollGame(gameId: String, callback: GameServiceCallback) {
        request(null, getEndpoint(APICall.POLL_GAME, gameId), callback)
    }

    private fun request(data: JSONObject?, url: String, callback: GameServiceCallback) {
        val method: Int = if (data != null) {
            Request.Method.POST
        } else {
            Request.Method.GET
        }

        val request = object : JsonObjectRequest(method, url, data,
            Response.Listener { response ->
                Log.v(LOG_TAG, "Response from server: $response")

                val gameState = Gson().fromJson(response.toString(), GameState::class.java)
                callback(gameState, null)
            },
            Response.ErrorListener { error ->
                Log.e(LOG_TAG, "Volley Error: $error")

                try {
                    // statusCode is null if volley timeouts
                    callback(null, error.networkResponse.statusCode)
                } catch (e: NullPointerException) {
                    callback(null, 0)
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val header = HashMap<String, String>()
                header["Content-Type"] = "application/json"
                header["Game-Service-Key"] = context.getString(R.string.API_key)
                return header
            }
        }
        // Change policy to avoid timeout error when heroku server is waking up
        request.retryPolicy = DefaultRetryPolicy(
            context.getString(R.string.TIMEOUT_MS).toInt(),
            context.getString(R.string.MAX_RETRIES).toInt(),
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQue.add(request)
    }


}

