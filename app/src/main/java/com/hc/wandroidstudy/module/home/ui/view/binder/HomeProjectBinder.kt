package com.hc.wandroidstudy.module.home.ui.view.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import coil.load

import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.hc.wandroidstudy.data.bean.HotProjectItemData

import com.hc.wandroidstudy.databinding.HomeLayoutProjectItemBinding



/**
  * @author ace
  * @createDate 2021/12/27
  * @explain 项目Item,两个为一组
 */
class HomeProjectBinder : QuickViewBindingItemBinder<HotProjectItemData, HomeLayoutProjectItemBinding>() {
    override fun convert(holder: BinderVBHolder<HomeLayoutProjectItemBinding>, data: HotProjectItemData) {
        holder.viewBinding.tvName.text = data.title
       holder.viewBinding.iv.load(data.envelopePic)
        holder.viewBinding.tvBottom.text = "${data.niceDate} ${data.author}"
    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): HomeLayoutProjectItemBinding {
        val binding = HomeLayoutProjectItemBinding.inflate(layoutInflater, parent, false)
        return binding
    }

    /**
     * 如果需要 Diff，可以自行实现如下内容
     */
    class Differ : DiffUtil.ItemCallback<HotProjectItemData>() {
        override fun areItemsTheSame(oldItem: HotProjectItemData, newItem: HotProjectItemData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HotProjectItemData, newItem: HotProjectItemData): Boolean {
            return oldItem.title == newItem.title
        }
    }
}