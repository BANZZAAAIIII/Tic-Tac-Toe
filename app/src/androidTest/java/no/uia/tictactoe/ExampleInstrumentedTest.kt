package no.uia.tictactoe

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.uia.tictactoe.data.GameState
import no.uia.tictactoe.utility.State

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    var state: State? = null
    val firstPlayer:String = "player1"
    val secondPlayer:String = "player2"
    val startingState = listOf(listOf(0,0,0), listOf(0,0,0), listOf(0,0,0))
    val secondState = listOf(listOf(0,0,0), listOf(0,0,0), listOf(0,0,0))



    @Test
    fun createGame() {
        GameService.createGame(firstPlayer, startingState) { game: GameState?, error: Int? ->
            assertNotNull(game)
            assertNull(error)

            assertEquals(game!!.players[0], firstPlayer)
            assertEquals(game.state, startingState)
        }
    }
}