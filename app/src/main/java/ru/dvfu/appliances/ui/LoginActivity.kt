package ru.dvfu.appliances.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ru.dvfu.appliances.R
import ru.dvfu.appliances.databinding.ActivityLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.dvfu.appliances.Logger
import ru.dvfu.appliances.compose.MainActivity
import ru.dvfu.appliances.model.repository.entity.User
import ru.dvfu.appliances.compose.viewmodels.LoginViewModel
import kotlin.collections.ArrayList

class LoginActivity() : AppCompatActivity() {
    private var TAG = "LoginActivity"

    private val viewModel: LoginViewModel by viewModel()
    private val logger: Logger by inject()
    private lateinit var vb: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var provider: OAuthProvider.Builder

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(vb.root)

        lifecycleScope.launch {
            viewModel.subscribe().collect { state ->
                when (state) {
                    is BaseViewState.Success<*> -> onSuccess(state.data as User?)
                    is BaseViewState.Loading -> onLoading()
                    is BaseViewState.Error -> handleError(state.error)
                }
            }
        }

        auth = FirebaseAuth.getInstance()

        setListeners()
    }

    private fun setListeners() {
        vb.microsoftSignInButton.setOnClickListener {
            setProgressVisibility(true)
            startMicrosoftLogin()
        }
        vb.googleSignInButton.setOnClickListener {
            setProgressVisibility(true)
            startGoogleLogin()
        }
        vb.guestSignInButton.setOnClickListener {
            setProgressVisibility(true)
            startGuestLogin()
        }
    }

    private fun onLoading() {
        setViews(true)
    }

    private fun setViews(isLoading: Boolean) {
        if (isLoading) {
            vb.progressBar.visibility = View.VISIBLE
            //vb.warning.visibility = View.GONE
        } else {
            vb.progressBar.visibility = View.INVISIBLE
        }
        vb.googleSignInButton.isClickable = !isLoading
        vb.guestSignInButton.isClickable = !isLoading
    }

    private fun onSuccess(user: User?) {
        setViews(false)

        if (user != null) {
            //vb.progressAnimationView.playAnimation()
            //Timer().schedule(2250) {
                startMainActivity()
            //}
        }
    }

    private fun handleError(error: Throwable?) {
        setViews(false)
        Toast.makeText(this, error?.message ?: getString(R.string.error_occured), Toast.LENGTH_SHORT).show()
        logger.log(error?.message)
    }

    private fun startMainActivity() {
        successfulSignIn(auth.currentUser!!)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startMicrosoftLogin() {
        provider = OAuthProvider.newBuilder("microsoft.com");
        val scopes: ArrayList<String> = arrayListOf(
            //("mail.read"),
            ("calendars.read")
        )
        provider.scopes = scopes

        val pendingResultTask: Task<AuthResult>? = auth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // authResult.getCredential().getAccessToken().
                    // The OAuth ID token can also be retrieved:
                    // authResult.getCredential().getIdToken().
                }
                .addOnFailureListener { e->
                    handleError(e)
                    setProgressVisibility(false)
                }
        } else {
            // There's no pending result so you need to start the sign-in flow.
            auth.startActivityForSignInWithProvider( /* activity= */this, provider.build())
                .addOnSuccessListener { authResult ->
                    // User is signed in.
                    // IdP data available in
                    showSuccessToast(authResult.user?.email)
                    authResult.additionalUserInfo?.profile?.toString()?.let {
                        Log.d("PROFILE",
                            it
                        )
                    }
                    authResult.user?.providerData.let {
                        Log.d("PROVIDER_DATA",
                            it.toString()
                        )
                    }
                    authResult.user?.metadata.let {
                        Log.d("META_DATA",
                            it.toString()
                        )
                    }
                    authResult.credential?.let {
                        Log.d("META_DATA",
                            it.toString()
                        )
                    }
                    //startMainActivity()
                    //FirebaseAuth.getInstance().signOut()
                        // The OAuth access token can also be retrieved:
                        // authResult.getCredential().getAccessToken().
                        // The OAuth ID token can also be retrieved:
                        // authResult.getCredential().getIdToken().
                    }
                }
                .addOnFailureListener { e->
                    handleError(e)
                    setProgressVisibility(false)
                }
        }

    private fun setProgressVisibility(visibility: Boolean) {
        if (visibility) vb.progressBar.visibility = View.VISIBLE
        else vb.progressBar.visibility = View.INVISIBLE
    }

    private fun startGoogleLogin() {
        // Configure GOOGLE sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //.requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun startGuestLogin() {
        setProgressVisibility(true)
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
                    //startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    handleError(task.exception!!)
                    //TODO("Guest Login Failed")
                }
            }
    }



    private fun successfulSignIn(user: FirebaseUser) {
        if (user.isAnonymous) {
            showSuccessToast(null)
//        } else if (firebaseUsersRepo.isUserInDatabase(user.uid)) {
//            showSuccessToast(user.email!!)
        } else {
            //firebaseUsersRepo.putUserToDatabase(user)
            showSuccessToast(user.email ?: user.displayName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val e = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    //presenter.googleLoginSuccess(task.getResult(ApiException::class.java)!!)
                    val account = task.getResult(ApiException::class.java)!!
                    handleSignInResult(task) //for checking!!!!!!!!!!!!!!!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    handleError(e)
                }
            } else {
                handleError(e!!)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInActivity", "signInWithCredential:success")
                    //startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    handleError(task.exception)
                    //TODO("DIALOG WINDOW WITH ERROR")
                }
            }
    }

    private fun showSuccessToast(email: String?) {
        email?.let {
            Toast.makeText(applicationContext, "Успешный вход в $email", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(applicationContext, "Успешный вход", Toast.LENGTH_SHORT).show()


    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: "No Token provided"
            Log.i("Google ID Token", googleIdToken)

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

}