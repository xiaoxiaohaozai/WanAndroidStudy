package com.hc.wandroidstudy.module.home.presentation.view.binder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.binder.QuickViewBindingItemBinder
import com.hc.wandroidstudy.databinding.HomeLayoutCategoryItemBinding
import com.hc.wandroidstudy.module.home.presentation.model.CategoryItemUIModel

/**
  * @author ace
  * @createDate 2021/12/27
  * @explain 分类
 */
class HomeCategoryBinder : QuickViewBindingItemBinder<CategoryItemUIModel, HomeLayoutCategoryItemBinding>() {
    override fun convert(holder: BinderVBHolder<HomeLayoutCategoryItemBinding>, data: CategoryItemUIModel) {

    }

    override fun onCreateViewBinding(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): HomeLayoutCategoryItemBinding {
        val binding = HomeLayoutCategoryItemBinding.inflate(layoutInflater, parent, false)
        return binding
    }

    /**
     * 如果需要 Diff，可以自行实现如下内容
     */
    class Differ : DiffUtil.ItemCallback<CategoryItemUIModel>() {
        override fun areItemsTheSame(oldItem: CategoryItemUIModel, newItem: CategoryItemUIModel): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: CategoryItemUIModel, newItem: CategoryItemUIModel): Boolean {
            return true
        }
    }
}