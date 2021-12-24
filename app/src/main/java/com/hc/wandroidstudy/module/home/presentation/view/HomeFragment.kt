package com.hc.wandroidstudy.module.home.presentation.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.drake.brv.BindingAdapter
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.layoutmanager.HoverGridLayoutManager
import com.drake.brv.utils.*
import com.hc.wandroidstudy.R
import com.hc.wandroidstudy.common.data.BannerData
import com.hc.wandroidstudy.common.data.HotProjectItemData
import com.hc.wandroidstudy.common.data.WxData
import com.hc.wandroidstudy.common.mvrx.MvRxFragment
import com.hc.wandroidstudy.databinding.*
import com.hc.wandroidstudy.module.home.presentation.data.BannerUIModel
import com.hc.wandroidstudy.module.home.presentation.data.CategoryItemUIModel
import com.hc.wandroidstudy.module.home.presentation.data.Footer
import com.hc.wandroidstudy.module.home.presentation.data.WxUIModel
import com.hc.wandroidstudy.module.home.presentation.vm.HomeViewModel
import com.hc.wandroidstudy.utils.viewbinding.viewBinding
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator


class HomeFragment : MvRxFragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by fragmentViewModel()

    private val binding: FragmentHomeBinding by viewBinding()

    var adapter: BindingAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //初始化视图模型
        buildAdapterModels()
    }

    @SuppressLint("SetTextI18n")
    private fun buildAdapterModels() {
        val layoutManager = HoverGridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //TODO -1 会报错
                if (position <0) {
                    return 1
                }
                //根据类型设置跨度
                return when (binding.recyclerview.bindingAdapter.getItemViewType(position)) {
                    R.layout.home_layout_banner,
                    R.layout.home_layout_wx_grid,
                    R.layout.home_layout_project_title -> 2
                    else -> 1
                }
            }
        }
        binding.recyclerview.layoutManager = layoutManager
        adapter = binding.recyclerview
                /**分隔线*/
                .divider {
                    setDrawable(R.drawable.divider_horizontal)
                    orientation = DividerOrientation.GRID
                    endVisible = true
                    startVisible = true
                }
                /**adapter多布局*/
                .setup {
                    //设置支持的布局类型
                    addType<BannerUIModel>(R.layout.home_layout_banner)
                    addType<WxUIModel>(R.layout.home_layout_wx_grid)
                    addType<CategoryItemUIModel>(R.layout.home_layout_category_item)
                    addType<String>(R.layout.home_layout_project_title)
                    addType<HotProjectItemData>(R.layout.home_layout_project_item)
                    //TODO 嵌套在最好在onCreate,避免重新设置
                    onCreate {
                        when (it) {//TODO 这里itemViewType 还没赋值,需用it 就是参数viewType
                            R.layout.home_layout_banner -> {
                                val binding = HomeLayoutBannerBinding.bind(itemView)
                                binding.banner.setBannerRound(SizeUtils.dp2px(6f).toFloat())
                                binding.banner.setAdapter(object : BannerImageAdapter<BannerData>(emptyList()) {
                                    override fun onBindView(holder: BannerImageHolder, data: BannerData, position: Int, size: Int) {
                                        holder.imageView.load(data.imagePath)
                                    }
                                }).indicator = CircleIndicator(context)
                            }
                            R.layout.home_layout_wx_grid -> {
                                val binding = HomeLayoutWxGridBinding.bind(itemView)
                                binding.rv.grid(4).setup {
                                    addType<WxData>(R.layout.home_layout_wx_item)
                                    addType<Footer>(R.layout.home_layout_wx_item_more)
                                    onBind {
                                        if (itemViewType == R.layout.home_layout_wx_item) {
                                            val itemBinding = HomeLayoutWxItemBinding.bind(itemView)
                                            itemBinding.tvName.text = getModel<WxData>().name
                                        }
                                    }
                                    binding.rv.isNestedScrollingEnabled = false
                                }
                                binding.rv.bindingAdapter.addFooter(Footer(), animation = false)
                            }
                        }

                        //绑定时
                        onBind {
                            when (itemViewType) {
                                R.layout.home_layout_banner -> {
                                    val binding = HomeLayoutBannerBinding.bind(itemView)
                                    binding.banner.adapter.setDatas(getModel<BannerUIModel>().items)
                                }
                                R.layout.home_layout_wx_grid -> {
                                    val binding = HomeLayoutWxGridBinding.bind(itemView)
                                    (binding.rv.adapter as BindingAdapter).models = getModel<WxUIModel>().items
                                }
                                R.layout.home_layout_category_item -> {
                                    val binding = HomeLayoutCategoryItemBinding.bind(itemView)
                                    binding.tvSystem.text = getModel<CategoryItemUIModel>().title
                                }

                                R.layout.home_layout_project_title -> {

                                }
                                R.layout.home_layout_project_item -> {
                                    val binding = HomeLayoutProjectItemBinding.bind(itemView)
                                    val data = getModel<HotProjectItemData>()
                                    binding.iv.load(data.envelopePic)
                                    binding.tvName.text = data.title
                                    val name = if (data.author.isBlank()) data.shareUser else data.author
                                    binding.tvBottom.text = "${data.niceDate} $name"
                                }
                            }
                        }
                    }
                }
        /**下拉刷新*/
        binding.page.onRefresh {
            LogUtils.e("onRefresh")
            viewModel.refreshHomeData()

        }
        /**上拉加载*/
        binding.page.onLoadMore {
            LogUtils.e("onLoadMore")
            viewModel.loadHomeDataMore()
        }


    }

    /**
     * 数据变化时
     */
    override fun invalidate() {
        withState(viewModel) {
            if (!it.isLoading) {
                adapter?.setDifferModels(it.items)
                LogUtils.e("invalidate",it.hasMore)
                binding.page.finish(hasMore = it.hasMore)
            }
        }

    }

}