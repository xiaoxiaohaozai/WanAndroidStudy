package com.hc.wandroidstudy.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.databinding.CommonLayoutListBinding
import com.hc.wandroidstudy.module.home.presentation.view.binder.*


/**
 * @author ace
 * @createDate 2021/12/29
 * @explain 通用列表
 *
 * 1 支持缺省页
 * 2 支持上拉加载,下拉刷新
 * 3 支持设置layoutManager
 */
abstract class CommonListFragment : BaseFragment<CommonLayoutListBinding>() {

    private var baseBinderAdapter: BaseBinderAdapter = BaseBinderAdapter()
    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): CommonLayoutListBinding {
        return CommonLayoutListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresh()
        initLoadMore()
        initAdapter()
    }

    @SuppressLint("SetTextI18n")
    private fun initAdapter() {
        baseBinderAdapter.apply {
            addItemBinder(HomeBannerBinder())
            addItemBinder(HomeWxBinder())
            addItemBinder(HomeCategoryBinder(), HomeCategoryBinder.Differ())
            addItemBinder(HomeTitleBinder(), HomeTitleBinder.Differ())
            addItemBinder(HomeProjectBinder(), HomeProjectBinder.Differ())
        }
        binding.list.layoutManager = getLayoutManager() ?: LinearLayoutManager(requireContext())
        binding.list.adapter = baseBinderAdapter
    }

    /**
     * 获取布局管理器
     */
    abstract fun getLayoutManager(): RecyclerView.LayoutManager?

    /**
     * 上拉加载是否可用
     */
    abstract fun isEnableLoadMore(): Boolean

    /**
     * 下拉刷新是否可用
     */
    abstract fun isEnableRefresh(): Boolean

    /**
     * 加载更多
     */
    abstract fun onLoadMore()

    /**
     * 下拉刷新
     */
    abstract fun onRefresh()


    private fun initLoadMore() {
        baseBinderAdapter.loadMoreModule.isAutoLoadMore = true
        baseBinderAdapter.loadMoreModule.setOnLoadMoreListener {
            onLoadMore()
        }

    }


    private fun initRefresh() {
        binding.refresh.setOnRefreshListener {
            // 这里的作用是防止下拉刷新的时候还可以上拉加载
            baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
            onRefresh()
        }
    }
}