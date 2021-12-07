package dev.mfazio.pennydrop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.mfazio.pennydrop.R

import dev.mfazio.pennydrop.databinding.FragmentPickPlayersBinding
import dev.mfazio.pennydrop.viewmodels.GameViewModel
import dev.mfazio.pennydrop.viewmodels.PickPlayersViewModel
import kotlinx.coroutines.launch

class PickPlayersFragment : Fragment() {

    private val pickPlayersViewModel by activityViewModels<PickPlayersViewModel>()
    private val gameViewModel by activityViewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            FragmentPickPlayersBinding
                .inflate(inflater, container, false)
                .apply {
                    this.vm = pickPlayersViewModel

                    this.buttonPlayGame.setOnClickListener {
                        viewLifecycleOwner.lifecycleScope.launch {
                            gameViewModel.startGame(
                                pickPlayersViewModel.players.value
                                    ?.filter { newPlayer ->
                                        newPlayer.isIncluded.get()
                                    }?.map { newPlayer ->
                                        newPlayer.toPlayer()
                                    } ?: emptyList()
                            )

                            findNavController().navigate(R.id.gameFragment)
                        }
                    }
                }

        return binding.root
    }
}
