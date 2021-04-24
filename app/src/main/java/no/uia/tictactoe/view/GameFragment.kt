package no.uia.tictactoe.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.R
import no.uia.tictactoe.databinding.GameFragmentBinding
import no.uia.tictactoe.utility.App

class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    private val context = App.context
    private val LOG_TAG = "GameFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = GameFragmentBinding.inflate(layoutInflater)

        val sharedPref = App.context.getSharedPreferences(context.getString(R.string.Preference_file), Context.MODE_PRIVATE)

        Log.v(LOG_TAG, sharedPref.all.toString())

        binding.Player1.text = sharedPref.getString(context.getString(R.string.Pref_Player1), "")
        binding.gameId.text = sharedPref.getString(context.getString(R.string.Pref_Game_ID), "")



        return binding.root
    }

    // TODO Polling fun to update board, and players/ID text
}