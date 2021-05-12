package no.uia.tictactoe.data

import no.uia.tictactoe.utility.State

data class GameState(val players: MutableList<String>, val gameId: String, val state: State) {
    override fun toString(): String {
        return "Players = $players, gameId = $gameId, state = $state"
    }
}



