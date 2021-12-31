package com.hc.wandroidstudy.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.hc.wandroidstudy.R
import com.hc.wandroidstudy.module.home.data.HomeRepository

class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BarUtils.transparentStatusBar(this)
        BarUtils.setStatusBarLightMode(this,true)
        setContentView(R.layout.activity_main)
    }
}