package no.uia.tictactoe.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.R
import no.uia.tictactoe.databinding.GameFragmentBinding
import no.uia.tictactoe.utility.App
import no.uia.tictactoe.utility.State

class GameFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: GameFragmentBinding

    private val context = App.context
    private val LOG_TAG = "GameFragment"

    private var state: State = listOf(listOf(1,0,0),listOf(0,0,0),listOf(0,0,0))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(layoutInflater)

        // Start the game, aka polling
        GameManager.startGame()

        val sharedPref = App.context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)
        binding.Player1.text = sharedPref.getString(context.getString(R.string.Pref_Player1), "")
        binding.gameId.text = sharedPref.getString(context.getString(R.string.Pref_Game_ID), "")

        binding.position1.setOnClickListener(this)
        binding.position2.setOnClickListener(this)
        binding.position3.setOnClickListener(this)
        binding.position4.setOnClickListener(this)

        GameManager.snackbarMessage.observe(viewLifecycleOwner, {
            if (it != "") {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
                // reset live date value to avoid it being shown again
                GameManager.resetSnackbar()
            }
        })

        GameManager.state.observe(viewLifecycleOwner, {
            Log.v(LOG_TAG, "Live data state update: $it")
        })

        GameManager.players.observe(viewLifecycleOwner, {
            Log.v(LOG_TAG, "Live data players update: $it")

        })

        return binding.root
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.position1 -> println("blarg")
                R.id.position2 -> updateState(2)
                R.id.position3 -> GameManager.updateGame(listOf(listOf(1,1,0),listOf(0,0,0),listOf(0,0,0)))
                R.id.position4 -> GameManager.updateGame(listOf(listOf(1,1,1),listOf(0,0,0),listOf(0,0,0)))
                else -> Unit
            }
        }
    }

    private fun updateState(pos: Int){

    }

}


