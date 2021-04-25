package no.uia.tictactoe.data

import no.uia.tictactoe.utility.State

data class GameState(val players: MutableList<String>, val gameId: String, val state: State)


