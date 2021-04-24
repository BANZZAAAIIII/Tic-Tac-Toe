package no.uia.tictactoe.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.GameService
import no.uia.tictactoe.R
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
                val gameId = dialogGameId.text.toString()
                val player = dialogPlayername.text.toString()
                GameManager.joinGame(player, gameId)

                findNavController().navigate(R.id.action_joinGameDialog_to_gameFragment)
            }
        }

        return binding.root
    }


}