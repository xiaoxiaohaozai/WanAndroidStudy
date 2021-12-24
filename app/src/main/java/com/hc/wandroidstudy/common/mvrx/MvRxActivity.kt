package com.hc.wandroidstudy.common.mvrx



import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.MavericksView


abstract class MvRxActivity(@LayoutRes containerLayoutId: Int) : AppCompatActivity(containerLayoutId), MavericksView