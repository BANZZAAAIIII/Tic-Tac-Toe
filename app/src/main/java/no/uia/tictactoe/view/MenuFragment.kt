package no.uia.tictactoe.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import no.uia.tictactoe.GameManager
import no.uia.tictactoe.R
import no.uia.tictactoe.databinding.MenuFragmentBinding

class MenuFragment : Fragment() {

    lateinit var binding: MenuFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = MenuFragmentBinding.inflate(layoutInflater)

        binding.startGame.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_createGameDialog)
        }

        binding.joinGame.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_joinGameDialog)
        }

        GameManager.snackbarMessage.observe(viewLifecycleOwner, { message ->
            if (!message.isNullOrBlank()) {
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
                // reset live date value to avoid it being shown again
                GameManager.resetSnackbar()
            }
        })

        return binding.root
    }
}

