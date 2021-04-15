package ru.students.dvfu.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.students.dvfu.R
import ru.students.dvfu.databinding.ActivityLoginBinding
import ru.students.dvfu.mvp.presenter.LoginPresenter
import ru.students.dvfu.mvp.view.LoginView

class LoginActivity : MvpAppCompatActivity(), LoginView {

    private var TAG = "LoginActivity"

    private val presenter by moxyPresenter { LoginPresenter(this) }
    private var vb: ActivityLoginBinding? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 120
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(vb?.root)
        auth = FirebaseAuth.getInstance();

        vb?.googleSignInButton?.setOnClickListener {
            presenter.googleAuthClicked()
        }
        vb?.guestSignInButton?.setOnClickListener {
            presenter.guestAuthClicked(auth)
        }
    }

    override fun setProgressVisibility(visibility: Boolean) {
        if (visibility) vb?.progressBar?.visibility = View.VISIBLE
        else vb?.progressBar?.visibility = View.INVISIBLE
    }

    override fun startGoogleLogin() {
        // Configure GOOGLE sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun startGuestLogin() {
        setProgressVisibility(true)
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInAnonymously:success")
                    val user = auth.currentUser
                    startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInAnonymously:failure", task.exception)
                    setProgressVisibility(false)
                    TODO("Guest Login Failed")
                }
            }
    }

    override fun startMainActivity() {
        setProgressVisibility(false)
        presenter.successfulSignIn(auth.currentUser?.email)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    //presenter.googleLoginSuccess(task.getResult(ApiException::class.java)!!)
                    val account = task.getResult(ApiException::class.java)!!
                    handleSignInResult(task) //for checking!!!!!!!!!!!!!!!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SignInActivity", "Google sign in failed", e)
                }
            } else {
                Log.w("SignInActivity", exception.toString())
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
                    startMainActivity()
                } else {
                    // If sign in fails, display a message to the user.
                    setProgressVisibility(false)
                    vb?.let { Snackbar.make(it.loginLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show() };

                    Log.d("SignInActivity", "signInWithCredential:failure")
                    TODO("DIALOG WINDOW WITH ERROR")
                }
            }
    }

    override fun showSuccessToast(email: String) {
        Toast.makeText(applicationContext, "Successful login in $email", Toast.LENGTH_SHORT).show()
    }

    fun toastAuthFailed() {
        Toast.makeText(
            baseContext, "Authentication failed.",
            Toast.LENGTH_SHORT
        ).show()
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