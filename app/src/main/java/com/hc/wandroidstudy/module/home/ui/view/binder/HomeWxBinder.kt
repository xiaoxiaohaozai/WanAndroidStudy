package com.hc.wandroidstudy.module.home.ui.view.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ResourceUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder

import com.hc.wandroidstudy.R

import com.hc.wandroidstudy.data.bean.WxData

import com.hc.wandroidstudy.databinding.HomeLayoutWxGridBinding

import com.hc.wandroidstudy.module.home.ui.model.WxUIModel


/**
 * @author ace
 * @createDate 2021/12/27
 * @explain
 *  公众号
 */
class HomeWxBinder : QuickViewBindingItemBinder<WxUIModel, HomeLayoutWxGridBinding>() {
    override fun convert(holder: BinderVBHolder<HomeLayoutWxGridBinding>, data: WxUIModel) {
        (holder.viewBinding.rv.adapter as WxItemAdapter).setList(data.items)
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): HomeLayoutWxGridBinding {
        val binding = HomeLayoutWxGridBinding.inflate(layoutInflater, parent, false)
        binding.rv.layoutManager = object : GridLayoutManager(context, 4) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        val wxItemAdapter = WxItemAdapter()
        binding.rv.adapter = wxItemAdapter
        return binding
    }


    class WxItemAdapter : BaseQuickAdapter<WxData, BaseViewHolder>(R.layout.home_layout_wx_item) {
        override fun convert(holder: BaseViewHolder, item: WxData) {
            holder.setText(R.id.tv_name, item.name)
            holder.setImageResource(R.id.iv, ResourceUtils.getMipmapIdByName("ic_${holder.adapterPosition + 1}"))
        }
    }
}