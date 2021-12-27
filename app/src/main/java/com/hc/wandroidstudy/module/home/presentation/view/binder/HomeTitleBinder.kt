package com.hc.wandroidstudy.module.home.presentation.view.binder


import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.binder.QuickItemBinder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.hc.wandroidstudy.R
import com.hc.wandroidstudy.common.data.HotProjectItemData


/**
 * @author ace
 * @createDate 2021/12/27
 * @explain
 *  标题
 */
class HomeTitleBinder : QuickItemBinder<String>() {
    override fun convert(holder: BaseViewHolder, data: String) {

    }

    override fun getLayoutId(): Int = R.layout.home_layout_project_title


    /**
     * 如果需要 Diff，可以自行实现如下内容
     */
    class Differ : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return true
        }
    }

}