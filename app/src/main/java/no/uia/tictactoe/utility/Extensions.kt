package no.uia.tictactoe.utility

import no.uia.tictactoe.data.GameState

typealias State = List<List<Int>>
typealias GameServiceCallback = (game: GameState?, error: Int?) -> Unit