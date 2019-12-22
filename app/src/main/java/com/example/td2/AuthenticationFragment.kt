package com.example.td2

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.authentication_fragment.view.*

class AuthenticationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.authentication_fragment, null)
        view.login.setOnClickListener { findNavController().navigate(R.id.action_authentication_fragment_to_login_fragment) }
        view.signup.setOnClickListener { findNavController().navigate(R.id.action_authentication_fragment_to_signup_fragment) }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREF_TOKEN_KEY, "")!=""){
            val mainActivity = Intent(activity, MainActivity::class.java)
            startActivity(mainActivity)
        }
    }
}