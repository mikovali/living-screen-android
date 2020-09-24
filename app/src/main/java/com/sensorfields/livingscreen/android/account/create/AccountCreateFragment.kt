package com.sensorfields.livingscreen.android.account.create

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sensorfields.livingscreen.android.R
import com.sensorfields.livingscreen.android.SignInWithGoogle
import com.sensorfields.livingscreen.android.producer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class AccountCreateFragment : GuidedStepSupportFragment() {

    @Inject
    lateinit var factory: Provider<AccountCreateViewModel>

    private val viewModel by viewModels<AccountCreateViewModel> { producer { factory.get() } }

    private val signInWithGoogle = registerForActivityResult(
        SignInWithGoogle(),
        ::onGoogleSignInCompleted
    )

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance {
        return GuidanceStylist.Guidance(
            getString(R.string.application_name),
            getString(R.string.account_create_description),
            null,
            ResourcesCompat.getDrawable(resources, R.mipmap.ic_launcher, null)
        )
    }

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(GOOGLE_SIGN_IN)
                .title(R.string.common_signin_button_text_long)
                .icon(R.drawable.common_google_signin_btn_icon_dark)
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            GOOGLE_SIGN_IN -> onGoogleSignInButtonClicked()
        }
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

private const val GOOGLE_SIGN_IN = 1L
