package com.hc.wandroidstudy.app.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.MavericksState

import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.blankj.utilcode.util.ColorUtils
import com.hc.wandroidstudy.R
import com.hc.wandroidstudy.common.mvrx.MvRxFragment
import com.hc.wandroidstudy.common.mvrx.MvRxViewModel
import com.hc.wandroidstudy.databinding.FragmentMainBinding
import com.hc.wandroidstudy.utils.viewbinding.viewBinding
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView


data class MainState(val tabs: List<String> = arrayListOf("首页", "热门文章")) : MavericksState

class MainViewModel(initialState: MainState) : MvRxViewModel<MainState>(initialState)

class MainFragment : MvRxFragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding()

    private val viewModel: MainViewModel by fragmentViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初次加载
        withState(viewModel) {
            initIndicator(it.tabs)
            initFragment(it.tabs)
        }
    }


    override fun invalidate() {

    }

    private fun initIndicator(tabs: List<String>) {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int = tabs.size

            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = ColorUtils.string2Int("#898989")
                colorTransitionPagerTitleView.selectedColor = ColorUtils.string2Int("#232323")
                colorTransitionPagerTitleView.textSize = 16f
                colorTransitionPagerTitleView.text = tabs[index]
                colorTransitionPagerTitleView.setOnClickListener { binding.viewpager.currentItem = index }
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        binding.tabs.navigator = commonNavigator

    }

    private fun initFragment(tabs: List<String>) {
        val fragmentAdapter = MainFragmentAdapter(this, tabs)
        binding.viewpager.adapter = fragmentAdapter
        binding.viewpager.registerOnPageChangeCallback(pageChangeCallback)
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.tabs.navigator.onPageSelected(position)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            binding.tabs.navigator.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            binding.tabs.navigator.onPageScrollStateChanged(state)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
    }
}