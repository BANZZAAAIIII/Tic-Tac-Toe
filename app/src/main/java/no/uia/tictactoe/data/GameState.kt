package no.uia.tictactoe.data


data class GameState(val players: MutableList<String>, val gameID: String, val states: List<List<Int>>)
