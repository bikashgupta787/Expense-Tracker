package com.example.expensetracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expensetracker.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Navigate to BudgetFragment when Budget row clicked
        binding.rowBudget.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, BudgetFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.rowAbout.setOnClickListener {
            // TODO: show About screen later
        }

        binding.rowShare.setOnClickListener {
            // TODO: implement share intent later
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}