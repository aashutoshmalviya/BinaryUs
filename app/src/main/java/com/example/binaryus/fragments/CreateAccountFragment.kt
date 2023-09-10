package com.example.binaryus.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.binaryus.AuthActivity
import com.example.binaryus.modifiedviews.AutoResizeTextView
import com.example.binaryus.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap


class CreateAccountFragment : Fragment() {

    lateinit var namesignup: EditText
    lateinit var emailsignup: EditText
    lateinit var passsignup: EditText
    lateinit var cpasssignup: EditText
    lateinit var signUpBtn: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var switchtextsignup:AutoResizeTextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        clicklistener()
    }

    private fun clicklistener() {

        signUpBtn!!.setOnClickListener {
            val name: String = namesignup!!.text.toString()
            val email: String = emailsignup!!.text.toString()
            val password: String = passsignup!!.text.toString()
            val cpassword: String = cpasssignup!!.text.toString()
            if (name.isEmpty() || name.equals(" ")) {
                namesignup!!.error = "Please input valid name"
                return@setOnClickListener
            }
            if (!isValidEmail(email)) {
                emailsignup!!.error = "Please input valid email"
                return@setOnClickListener
            }
            if (!isValidPassword(password)) {
                passsignup!!.error = "Please input a strong password"
                return@setOnClickListener
            }
            if (!password.equals(cpassword)) {
                cpasssignup!!.error = "Password Not matched"
                return@setOnClickListener
            }
            createAccount(name, email, password)
        }

        switchtextsignup.setOnClickListener{
            (activity as AuthActivity).setFragment(CreateAccountFragment())
        }
    }

    private fun init(view: View) {
        namesignup = view.findViewById(R.id.Name)
        emailsignup = view.findViewById(R.id.Email)
        passsignup = view.findViewById(R.id.Password)
        cpasssignup = view.findViewById(R.id.ConfirmPassword)
        progressBar=view.findViewById(R.id.signupprogressbar)
        signUpBtn = view.findViewById(R.id.signupbutton)
        firebaseAuth = FirebaseAuth.getInstance()
        switchtextsignup=view.findViewById(R.id.Signupswitchtext)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun createAccount(name: String, email: String, password: String) {
        progressBar.visibility=VISIBLE
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    firebaseAuth.currentUser?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(context, "Verification link sent," +
                                    "Please Log in with Credentials after verification", Toast.LENGTH_LONG)
                                .show()
                            (activity as AuthActivity).setFragment(CreateAccountFragment())
                            progressBar.visibility= GONE
                            uploadUser(user, name, email)
                        }
                } else {
                    Toast.makeText(context, "" + task.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                    progressBar.visibility= GONE
                }
            }
    }

    private fun uploadUser(user: FirebaseUser?, name: String, email: String) {
        var map = HashMap<String, String>()
        map.put("name", name)
        map.put("email", email)
        map.put("profileImage", " ")
        map.put("uid", user!!.uid)
        map.put("following", "0")
        map.put("follower", "0")
        map.put("status", "")
        map.put("posts", "")

        FirebaseFirestore.getInstance().collection("Users").document(user.uid)
            .set(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("error", "data successfull added" )
                } else {
                    Log.d("error", "" + task.exception!!.message)
                }
            }
    }

}


