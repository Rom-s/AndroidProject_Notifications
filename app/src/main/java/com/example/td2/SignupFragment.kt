package com.example.td2

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.td2.network.Api
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.signup_fragment, null)
        view.signup.setOnClickListener{ signup() }
        return view
    }

    private val coroutineScope = MainScope()

    private fun signup() {
        val firstname = view?.firstname?.text
        val lastname = view?.lastname?.text
        val email = view?.email?.text
        val password = view?.password?.text
        val password_confirmation = view?.password_confirmation?.text

        if (firstname==null||lastname==null||email==null||password==null||password_confirmation==null) {
            Toast.makeText(context, "All fields are required.", Toast.LENGTH_LONG).show()
            return
        }

        if (password.toString()!=password_confirmation.toString()) {
            Toast.makeText(context, "Those passwords didn't match. Try again.", Toast.LENGTH_LONG).show()
            return
        }

        if (password.toString().length<6) {
            Toast.makeText(context, "Password is too short (minimum is 6 characters)", Toast.LENGTH_LONG).show()
            return
        }

        val signupForm = SignupForm(firstname.toString(), lastname.toString(), email.toString(), password.toString(), password_confirmation.toString())
        coroutineScope.launch {
            val fetchedToken = Api.INSTANCE.userService.signup(signupForm)
            if(!fetchedToken.isSuccessful){
                Toast.makeText(context, "An error occurred. Try Again."+fetchedToken.body().toString(), Toast.LENGTH_LONG).show()
            }
            else {
                PreferenceManager.getDefaultSharedPreferences(context).edit {
                    putString(SHARED_PREF_TOKEN_KEY, fetchedToken.body()?.token)
                }
                val mainActivity = Intent(activity, MainActivity::class.java)
                startActivity(mainActivity)
            }
        }
    }
}