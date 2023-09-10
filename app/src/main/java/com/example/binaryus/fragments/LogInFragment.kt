package com.example.binaryus.fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.example.binaryus.AuthActivity
import com.example.binaryus.MainPage
import com.example.binaryus.modifiedviews.AutoResizeTextView
import com.example.binaryus.R
import com.google.firebase.auth.FirebaseAuth


class LogInFragment : Fragment() {
    lateinit var emaillogin: EditText
    lateinit var passwordlogin: EditText
    lateinit var forgetpass: TextView
    lateinit var loginButton: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var progressBar: ProgressBar
    lateinit var switchtextlogin: AutoResizeTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        init(view)
        clickListener()

    }


    private fun init(view: View) {
        emaillogin = view.findViewById(R.id.EmailLogin)
        passwordlogin = view.findViewById(R.id.Passwordlogin)
        loginButton = view.findViewById(R.id.loginbutton)
        forgetpass = view.findViewById(R.id.forgotpass)
        progressBar=view.findViewById(R.id.loginprogressbar)
        switchtextlogin=view.findViewById(R.id.Loginswitchtext)
    }

    private fun clickListener() {
        loginButton.setOnClickListener {
            progressBar.visibility=VISIBLE
            val email: String = emaillogin.text.toString()
            val password: String = passwordlogin.text.toString()

            if (!isValidEmail(email)) {
                progressBar.visibility= GONE
                emaillogin!!.error = "Please input valid email"
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        if (!firebaseUser!!.isEmailVerified) {
                            Toast.makeText(context, "Plese verify your email", Toast.LENGTH_SHORT)
                                .show()
                            firebaseUser?.sendEmailVerification()
                            progressBar.visibility=GONE
                        }else{
                            startActivity(Intent(context, MainPage::class.java))
                            activity?.finish()
                            progressBar.visibility=GONE
                        }
                    } else {
                        val s=""+task.exception!!.message
                        passwordlogin.error=s
                        progressBar.visibility= GONE
                    }
                }
        }
        switchtextlogin.setOnClickListener{
            (activity as AuthActivity).setFragment(LogInFragment())
        }
        forgetpass.setOnClickListener{
            val email: String = emaillogin.text.toString()
            if (!isValidEmail(email)) {
                emaillogin!!.error = "Please input valid email for resetting your password"
                return@setOnClickListener
            }
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {task->
                if (task.isSuccessful){
                    Toast.makeText(context,"Password reset link sent to your email address",Toast.LENGTH_LONG).show()
                    emaillogin.setText("")
                }
                else{
                    Toast.makeText(context,"Error"+ task.exception!!.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
