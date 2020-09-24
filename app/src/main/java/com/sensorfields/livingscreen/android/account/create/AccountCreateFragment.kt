package com.sensorfields.livingscreen.android.account.create

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
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

    private val signInWithGoogle =
        registerForActivityResult(SignInWithGoogle()) { account: GoogleSignInAccount? ->
            account?.idToken?.let { idToken -> viewModel.signInWithGoogle(idToken) }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        viewModel.state.observe(viewLifecycleOwner, ::onState)
        viewModel.action.observe(viewLifecycleOwner, ::onAction)
    }

    private fun setupViews() {
        viewBinding.googleSignInButton.setOnClickListener {
            signInWithGoogle.launch(
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))
                    .build()
            )
        }
    }

    private fun onState(state: AccountCreateState) {
        viewBinding.googleSignInButton.isEnabled = !state.isInProgress
    }

    private fun onAction(action: AccountCreateAction) {
        when (action) {
            is AccountCreateAction.NavigateToMain -> {
                findNavController().navigate(AccountCreateFragmentDirections.main())
            }
        }
    }
}
