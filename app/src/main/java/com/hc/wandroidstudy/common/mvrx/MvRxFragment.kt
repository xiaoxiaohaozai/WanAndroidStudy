package com.hc.wandroidstudy.common.mvrx


import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.airbnb.mvrx.MavericksView


abstract class MvRxFragment(@LayoutRes containerLayoutId: Int) : Fragment(containerLayoutId), MavericksView