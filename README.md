# SuperAdapter
是一个小巧的 android Adapter库

## 功能
1、支持数据驱动的多holder类型
2、给holder动态注入实例

## 仓库依赖
```kotlin
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.hereticcultivator:SuperAdapter:1.0.0'
}
```

## 效果图：
![image](https://github.com/hereticcultivator/SuperAdapter/blob/develop/app%E6%95%88%E6%9E%9C%E5%9B%BE.png)

## 代码示例：
```kotlin
class SuperAdapterDemoActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySuperAdapterDemoBinding

    private val viewModel by lazy {
        CountViewModel()
    }

    private val adapter by lazy {
        SuperAdapter(
            object : DiffUtil.ItemCallback<DemoData>() {
                override fun areItemsTheSame(oldItem: DemoData, newItem: DemoData): Boolean {
                    return oldItem.generateId == newItem.generateId
                }

                override fun areContentsTheSame(oldItem: DemoData, newItem: DemoData): Boolean {
                    return oldItem == newItem
                }
            }
        ).apply {
            registerViewHolder(IntDataViewHolder::class.java)
            registerViewHolder(StringDataViewHolder::class.java)
            setupWithRecyclerView(binding.recyclerView)
            inject(viewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySuperAdapterDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
sealed class DemoData: DataPrimaryKey

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

class CountViewModel: ViewModel() {

    private var count = 2

    fun getCount() = count

}
```

PS: 感谢@yangjinyang 提供的技术支持




