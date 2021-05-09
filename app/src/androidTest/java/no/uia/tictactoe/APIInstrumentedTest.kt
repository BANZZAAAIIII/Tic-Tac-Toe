package no.uia.tictactoe

import androidx.test.ext.junit.runners.AndroidJUnit4
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.utility.State

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
private val firstPlayer:String = "player1"
private val secondPlayer:String = "player2"
private val startingState: State = mutableListOf(
    mutableListOf("0", "0", "0"),
    mutableListOf("0", "0", "0"),
    mutableListOf("0", "0", "0")
)

@RunWith(AndroidJUnit4::class)
class APIInstrumentedTest {


    private var localGameState: GameState = GameState(mutableListOf(), "", startingState)

    @Test
    fun createGameTest() {
        var done = false

        GameService.createGame(firstPlayer, localGameState.state) { game: GameState?, error: Int? ->
            // Check that request succeeded
            assertNotNull(game)
            assertNull(error)

            // assert Player and game state
            assertEquals(game!!.players[0], firstPlayer)
            assertEquals(game.state, localGameState.state)

            localGameState = game


            done = true
        }

        // makes the executing thread sleep,
        // to make sure the callback is called before the test finishes
        sleep(1000)
        assert(done)
    }

    @Test
    fun joinGameTest() {
        var done = false

        sleep(2000)
        GameService.joinGame(secondPlayer, localGameState.gameId) { game: GameState?, error: Int? ->
            // Check that request succeeded
            assertNotNull(game)
            assertNull(error)

            // assert Player and game state
            assertEquals(game!!.players[0], firstPlayer)
            assertEquals(game.players[1], secondPlayer)
            assertEquals(game.state, localGameState.state)

            localGameState = game

            done = true
        }

        // makes the executing thread sleep,
        // to make sure the callback is called before the test finishes
        sleep(1000)
        assert(done)
    }
}