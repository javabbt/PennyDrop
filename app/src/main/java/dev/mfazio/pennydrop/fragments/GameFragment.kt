package dev.mfazio.pennydrop.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

import dev.mfazio.pennydrop.databinding.FragmentGameBinding
import dev.mfazio.pennydrop.viewmodels.GameViewModel

class GameFragment : Fragment() {

    private val gameViewModel by activityViewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentGameBinding
            .inflate(inflater, container, false)
            .apply {
                vm = gameViewModel

                textCurrentTurnInfo.movementMethod = ScrollingMovementMethod()

                lifecycleOwner = viewLifecycleOwner
            }

        return binding.root
    }
}
