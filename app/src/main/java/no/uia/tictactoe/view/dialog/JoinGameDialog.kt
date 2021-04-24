package no.uia.tictactoe.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import no.uia.tictactoe.GameService
import no.uia.tictactoe.databinding.JoinGameDialogBinding

class JoinGameDialog : BottomSheetDialogFragment() {
    private lateinit var binding: JoinGameDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JoinGameDialogBinding.inflate(layoutInflater)

        binding.apply {
            dialogButton.setOnClickListener {
                val game_ID = dialogEdittext.text.toString()
                GameService.joinGame(game_ID)
            }
        }

        return binding.root
    }


}