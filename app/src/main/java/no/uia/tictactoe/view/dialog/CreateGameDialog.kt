package no.uia.tictactoe.view.dialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.R
import no.uia.tictactoe.databinding.CreateGameDialogBinding
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.Marks

class CreateGameDialog : BottomSheetDialogFragment() {

    private lateinit var binding: CreateGameDialogBinding

    private val context = App.context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CreateGameDialogBinding.inflate(layoutInflater, container, false)

        val sharedPref = context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        binding.apply {
            // Sets users last player name in text box
            dialogPlayername.setText(sharedPref.getString(context.getString(R.string.Pref_Player1), ""))

            dialogButton.setOnClickListener {
                val player = dialogPlayername.text.toString()

                GameManager.createGame(player) {
                    with(sharedPref.edit()) {
                        putString(context.getString(R.string.Pref_Player1), player)
                        apply() // use apply() for async saving
                    }

                    val args = CreateGameDialogDirections.actionCreateGameDialogToGameFragment(Marks.X, player)
                    findNavController().navigate(args)
                }
            }
        }

        return binding.root
    }
}