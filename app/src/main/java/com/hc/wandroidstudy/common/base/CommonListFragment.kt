package com.hc.wandroidstudy.common.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseBinderAdapter
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.hc.wandroidstudy.common.adapter.CommonListAdapter

import com.hc.wandroidstudy.databinding.CommonLayoutListBinding


import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener


/**
 * @author ace
 * @createDate 2021/12/29
 * @explain 通用列表
 *
 * 1 支持缺省页
 * 2 支持上拉加载,下拉刷新
 * 3 支持设置layoutManager
 */
abstract class CommonListFragment : BaseFragment<CommonLayoutListBinding>(), OnLoadMoreListener,
    OnRefreshListener {

     var baseBinderAdapter: BaseBinderAdapter = CommonListAdapter()
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CommonLayoutListBinding {
        return CommonLayoutListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initRefresh()
        initLoadMore()
    }

    @SuppressLint("SetTextI18n")
    private fun initAdapter() {
        initAdapter(baseBinderAdapter)
        binding.list.layoutManager = getLayoutManager(baseBinderAdapter)
        binding.list.adapter = baseBinderAdapter

    }

    /**
     * adapter 配置
     */
    abstract fun initAdapter(adapter: BaseBinderAdapter)

    /**
     * 获取布局管理器
     */
    open fun getLayoutManager(adapter: BaseBinderAdapter): RecyclerView.LayoutManager =
        LinearLayoutManager(requireContext())

    /**
     * 上拉加载是否可用
     */
    open fun isEnableLoadMore(): Boolean = true

    /**
     * 下拉刷新是否可用
     */
    open fun isEnableRefresh(): Boolean = true

    /**
     * 上拉加载时触发
     */
    override fun onLoadMore() {
        onLoadMoreEvent()
    }

    abstract fun onLoadMoreEvent()

    abstract fun onRefreshEvent()

    /**
     * 下拉刷新时触发
     */
    override fun onRefresh(refreshLayout: RefreshLayout) {
        onRefreshEvent()
    }




    private fun initLoadMore() {
        if (isEnableLoadMore()) {
            baseBinderAdapter.loadMoreModule.isEnableLoadMore = true
            baseBinderAdapter.loadMoreModule.isAutoLoadMore = true
            baseBinderAdapter.loadMoreModule.setOnLoadMoreListener(this)
        } else {
            baseBinderAdapter.loadMoreModule.isEnableLoadMore = false
        }
    }

    private fun initRefresh() {
        if (isEnableRefresh()) {
            binding.refresh.setEnableRefresh(true)
            binding.refresh.setOnRefreshListener(this)
        } else {
            binding.refresh.setEnableRefresh(false)
        }
    }


}