package com.example.expensetracker.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar

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

        binding.rowBudget.setOnClickListener {
            findNavController().navigate(R.id.budgetFragment)
        }


        binding.rowAbout.setOnClickListener {
            findNavController().navigate(R.id.aboutUsFragment)
        }

        binding.rowShare.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Stay tuned! This feature is coming soon 🚀",
                Toast.LENGTH_SHORT
            ).show()

        }

        binding.rowRate.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Stay tuned! This feature is coming soon 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rowTheme.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Stay tuned! This feature is coming soon 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}