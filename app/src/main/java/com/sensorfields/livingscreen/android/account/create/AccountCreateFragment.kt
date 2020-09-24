package com.sensorfields.livingscreen.android.account.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.SignInWithGoogle
import com.sensorfields.livingscreen.android.databinding.AccountCreateFragmentBinding
import com.sensorfields.livingscreen.android.producer
import com.sensorfields.livingscreen.android.viewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AccountCreateFragment : Fragment(R.layout.account_create_fragment) {

    @Inject
    lateinit var factory: Provider<AccountCreateViewModel>

    private val viewModel by viewModels<AccountCreateViewModel> { producer { factory.get() } }

    private val viewBinding by viewState({ AccountCreateFragmentBinding.bind(it) })

    private val signInWithGoogle = registerForActivityResult(
        SignInWithGoogle(),
        ::onGoogleSignInCompleted
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        viewBinding.googleSignInButton.setOnClickListener { onGoogleSignInButtonClicked() }
    }

    private fun onGoogleSignInButtonClicked() {
        signInWithGoogle.launch(viewModel.googleSignInOptions)
    }

    private fun onGoogleSignInCompleted(account: GoogleSignInAccount?) {
        if (account != null) navigateToMain()
    }

    private fun navigateToMain() {
        findNavController().navigate(AccountCreateFragmentDirections.main())
    }
}
