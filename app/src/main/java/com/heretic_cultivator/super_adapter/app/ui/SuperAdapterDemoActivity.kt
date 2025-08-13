package com.heretic_cultivator.super_adapter.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.heretic_cultivator.super_adapter.adapter.SuperAdapter
import com.heretic_cultivator.super_adapter.adapter.registerViewHolder
import com.heretic_cultivator.super_adapter.adapter.setupWithRecyclerView
import com.heretic_cultivator.super_adapter.app.databinding.ActivitySuperAdapterDemoBinding
import com.heretic_cultivator.super_adapter.app.databinding.ItemIntDataBinding
import com.heretic_cultivator.super_adapter.app.databinding.ItemStringDataBinding
import com.heretic_cultivator.super_adapter.holder.ViewBindingViewHolder
import com.heretic_cultivator.super_adapter.holder.inject
import kotlin.random.Random

class SuperAdapterDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperAdapterDemoBinding

    private val viewModel by lazy {
        CountViewModel()
    }

    //自定义数据比较规则
    private val diffCallback = object : DiffUtil.ItemCallback<DemoData>() {
        override fun areItemsTheSame(oldItem: DemoData, newItem: DemoData): Boolean {
            return oldItem.generateId == newItem.generateId
        }

        override fun areContentsTheSame(oldItem: DemoData, newItem: DemoData): Boolean {
            return oldItem == newItem
        }
    }

    private val adapter by lazy {
        SuperAdapter(
            //可空字段 自定义数据differ
            diffCallback = diffCallback
        ).apply {
            //注册holer类型 基于holder自动识别数据类型
            registerViewHolder(
                viewHolderClass = IntDataViewHolder::class.java,
                onBindViewHolder = { holder, data, payloads: List<Any> ->
                    //onBind回调 可用于曝光或者日志
                },
                onViewHolderCreated = {
                    //做一次性的初始化逻辑
                }
            )
            //注册holer类型（主动和数据类型绑定）
            registerViewHolder(StringData::class.java, StringDataViewHolder::class.java)
            //绑定recyclerView
            setupWithRecyclerView(binding.recyclerView)
            //注入viewModel实例
            inject(viewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuperAdapterDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //给adapter绑定数据
        val data = generateDemoData()
        adapter.submitList(data)
    }

    private fun generateDemoData(): List<DemoData> {
        val demoList = mutableListOf<DemoData>()
        kotlin.repeat(105) {
            val data: DemoData = if (Random.nextBoolean()) {
                IntData(it.toString(), Random.nextInt(1, 1000))
            } else {
                StringData(it.toString(), "随机字符串 ${Random.nextInt(1, 100)}")
            }
            demoList.add(data)
        }
        return demoList
    }
}

// 密封类定义
sealed class DemoData : DataPrimaryKey

interface DataPrimaryKey {
    val generateId: String
}

data class IntData(
    override val generateId: String,
    val value: Int,
) : DemoData()

data class StringData(
    override val generateId: String,
    val text: String
) : DemoData()

class IntDataViewHolder(binding: ItemIntDataBinding) : ViewBindingViewHolder<IntData, ItemIntDataBinding>(binding) {

    val vm by inject<CountViewModel>()

    override fun onBindData(data: IntData) {
        val text = "data: ${data.value} vm Count:${vm.getCount()}"
        binding.tvIntValue.text = text
    }

}

class StringDataViewHolder(binding: ItemStringDataBinding) : ViewBindingViewHolder<StringData, ItemStringDataBinding>(binding) {
    override fun onBindData(data: StringData) {
        binding.tvStringValue.text = data.text
    }
}

class CountViewModel : ViewModel() {

    private var count = 2

    fun getCount() = count

}