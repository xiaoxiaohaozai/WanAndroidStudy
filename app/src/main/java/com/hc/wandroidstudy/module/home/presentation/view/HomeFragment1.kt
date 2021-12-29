package com.hc.wandroidstudy.module.home.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hc.wandroidstudy.common.base.BaseFragment
import com.hc.wandroidstudy.databinding.FragmentHomeBinding
import com.hc.wandroidstudy.module.home.presentation.vm.HomeViewModel1


class HomeFragment1 : BaseFragment<FragmentHomeBinding>() {

    val viewModel = viewModels<HomeViewModel1>()

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}