package no.uia.tictactoe.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.GameService
import no.uia.tictactoe.R
import no.uia.tictactoe.databinding.JoinGameDialogBinding
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.Marks

class JoinGameDialog : BottomSheetDialogFragment() {

    private lateinit var binding: JoinGameDialogBinding

    private val context = App.context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = JoinGameDialogBinding.inflate(layoutInflater, container, false)

        val sharedPref = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        binding.apply {
            // Sets users last player name in text box
            dialogPlayername.setText(sharedPref.getString(context.getString(R.string.Pref_Player), ""))

            dialogButton.setOnClickListener {
                val gameId = dialogGameId.text.toString()
                val player = dialogPlayername.text.toString()

                if (player != "" && player.length <= 50) {
                    GameManager.joinGame(player, gameId) {
                        with(sharedPref.edit()) {
                            putString(context.getString(R.string.Pref_Player), player)
                            apply()
                        }

                        val args = JoinGameDialogDirections.actionJoinGameDialogToGameFragment(Marks.O, player)
                        findNavController().navigate(args)
                    }
                } else {
                    Snackbar.make(root, "Invalid username", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }


}