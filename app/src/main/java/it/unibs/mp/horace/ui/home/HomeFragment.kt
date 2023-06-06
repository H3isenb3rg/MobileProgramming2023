package it.unibs.mp.horace.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import it.unibs.mp.horace.R
import it.unibs.mp.horace.TopLevelFragment
import it.unibs.mp.horace.databinding.FragmentHomeBinding

class HomeFragment : TopLevelFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // If the fragment is reached after a successful sign up,
        // show a snack bar
        if (args.signUpSuccessful) {
            Snackbar.make(
                view, getString(R.string.signed_up_successfully), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}