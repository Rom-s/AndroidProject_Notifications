package com.example.td2


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.td2.network.Api
import kotlinx.android.synthetic.main.header_fragment.*
import kotlinx.android.synthetic.main.header_fragment.view.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class HeaderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.header_fragment, container)
        view.image_view.setOnClickListener{goToUserInfoActivity()}
        return view

    }

    private val coroutineScope = MainScope()

    override fun onResume() {
        super.onResume()
        coroutineScope.launch {

            var avatar = Api.userService.getInfo().body()?.avatar
            Glide.with(this@HeaderFragment).load(avatar).apply(RequestOptions.circleCropTransform()).into(image_view)
        }
        //Glide.with(this).load("https://goo.gl/gEgYUd").into(image_view)




    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun goToUserInfoActivity(){
        val userActivity = Intent(activity,UserInfoActivity::class.java)
        startActivity(userActivity)
    }
}