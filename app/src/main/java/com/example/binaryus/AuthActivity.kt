package com.example.binaryus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.binaryus.fragments.CreateAccountFragment
import com.example.binaryus.fragments.LogInFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    /*One tap sign in*/
    /*private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true*/

    private lateinit var frameLayout: FrameLayout
    private lateinit var switchText: TextView
    var switch: Int = 0
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        frameLayout = findViewById(R.id.frameLayout)

        val signInButton = findViewById<LinearLayout>(R.id.googlesigninbutton)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("30107728951-0b34mdmvav1lrgqugrv0ti2hfsv17got.apps.googleusercontent.com")
            .requestEmail()
            .build()
        Log.d("siginlog","result ana chahiye")

        val gsc = GoogleSignIn.getClient(this, gso);


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        fragmentTransaction.replace(frameLayout.id, LogInFragment())
        fragmentTransaction.commit()


        signInButton.setOnClickListener(View.OnClickListener {
            Log.d("siginlog","result ana chahiye")
            startActivityForResult(gsc.signInIntent, 100)

            /*One tap sign in*/
            /*oneTapClient = Identity.getSignInClient(this)
            signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.your_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build()


            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("error", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d("error", e.localizedMessage)
                }*/

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, resultCode, data)
        Log.d("siginlog","result aa gya"+resultCode)
        if (requestCode == 100) {
            Log.d("signinlog", "requestCode is 100")

            try{
                val signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (signInAccountTask.isSuccessful) {
                    Log.d("signinlog", "Google sign in successfull")

                    val gsa = signInAccountTask
                        .getResult(ApiException::class.java)
                    if (gsa != null) {
                        val authCredential = GoogleAuthProvider
                            .getCredential(gsa.idToken, null)
                        firebaseAuth.signInWithCredential(authCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    updateui(firebaseAuth)

                                    Log.d("signinlog", "firebase auth sucess")
                                } else
                                    Log.d(
                                        "signinlog",
                                        "Authentication Failed:" + task.exception?.message
                                    )
                            }
                    } else {

                    }
                }
            }
            catch (e:ApiException){
                Log.d("signinlog","error"+e)
            }
            Log.d("signinlog","sign in unsucessful")
        }
    }

    private fun updateui(user: FirebaseAuth) {

        val account: GoogleSignInAccount? =GoogleSignIn.getLastSignedInAccount(this)

        var map = HashMap<String, String>()
        map.put("name", account!!.displayName.toString())
        map.put("email", account!!.email.toString())
        map.put("profileImage", account!!.photoUrl.toString())
        map.put("uid", user!!.uid.toString())
        map.put("following", "0")
        map.put("follower", "0")
        map.put("status", "")

        FirebaseFirestore.getInstance().collection("Users").document(user.uid.toString())
            .set(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainPage::class.java));
                    finish()
                } else {
                    Log.d("error", "" + task.exception!!.message)
                }
            }
    }


    /*One tap sign in*/
    /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
          super.onActivityResult(requestCode, resultCode, data)

          when (requestCode) {
              REQ_ONE_TAP -> {
                  try {
                      val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                      val idToken = googleCredential.googleIdToken
                      when {
                          idToken != null -> {
                              // Got an ID token from Google. Use it to authenticate
                              // with Firebase.
                              val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                              auth.signInWithCredential(firebaseCredential)
                                  .addOnCompleteListener(this) { task ->
                                      if (task.isSuccessful) {
                                          // Sign in success, update UI with the signed-in user's information
                                          Log.d("error", "signInWithCredential:success")
                                          val user = auth.currentUser
                                          updateUI(user)
                                      } else {
                                          // If sign in fails, display a message to the user.
                                          Log.w("error", "signInWithCredential:failure", task.exception)
                                          updateUI(null)
                                      }
                                  }
                          }
                          else -> {
                              // Shouldn't happen.
                              Log.d("error", "No ID token!")
                          }
                      }
                  } catch (e: ApiException) {
                      // ...
                  }
              }
          }
      }
      override fun onStart() {
          super.onStart()
          // Check if user is signed in (non-null) and update UI accordingly.
          val currentUser = auth.currentUser
          updateUI(currentUser)
      }

      private fun updateUI(currentUser: FirebaseUser?) {
          startActivity(Intent(this,MainPage::class.java))
          finish()
      }*/

     public fun setFragment(fragment:Fragment) {
        val switchfragment:Fragment
        if (fragment is LogInFragment) {
            switchfragment = CreateAccountFragment()

        } else {
            switchfragment = LogInFragment()

        }
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )

        if (fragment is CreateAccountFragment) {
            fragmentTransaction.addToBackStack(null)
        }

        fragmentTransaction.replace(frameLayout.id, switchfragment)
        fragmentTransaction.commit()
    }
    
}
