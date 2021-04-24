package no.uia.tictactoe.data

typealias State = List<List<Int>>

data class GameState(val players: MutableList<String>, val gameId: String, val state: State)


