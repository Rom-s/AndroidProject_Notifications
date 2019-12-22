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
import com.example.td2.network.UserService
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, null)
        view.login.setOnClickListener{ login() }
        return view
    }

    private val coroutineScope = MainScope()

    private fun login() {
        val email = view?.email?.text
        val password = view?.password?.text
        if(email==null||password==null){
            Toast.makeText(context, "All fields are required.", Toast.LENGTH_LONG).show()
            return
        }

        val loginForm = LoginForm(email.toString(), password.toString())
        coroutineScope.launch {
            val fetchedToken = Api.INSTANCE.userService.login(loginForm)
            if(!fetchedToken.isSuccessful){
                Toast.makeText(context, "Your email or password is incorrect.", Toast.LENGTH_LONG).show()
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