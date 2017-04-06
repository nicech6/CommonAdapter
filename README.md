# CommonAdapter
v1.0.0
一个通用的RecyclerView适配器封装,这周末抽时间详细描述
## 如何使用
### 在你当前项目build.gradle下增加一下代码
allprojects {

    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
### 最后在dependencies 下引用
compile 'com.github.icuihai.CommonAdapter:app:v1.0'
