# WebView Pool

在Android应用中经常使用H5的场景，需要对WebView的加载复用来优化速度。

- 预初始化WebView
- 缓存复用WebView

为了简单实用WebView，不关心复用WebView。本库实现：

- [x] 预初始化WebView
- [x] 空闲时，预初始化WebView
- [x] 缓存复用WebView，使用LFU算法或自定义处理缓存
- [x] 清理和复用WebView

### 使用方法

#### 1. 添加jitpack仓库
```agsl
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
#### 2. 添加依赖到模块的build.gradle
```agsl
dependencies {
    // 使用系统默认的WebView
    implementation 'com.github.classops.webpool:webpool:1.0.1'
    
    // 如果使用 腾讯X5 服务的WebView，使用下面的依赖
    implementation 'com.github.classops.webpool:webpool-tbs:1.0.1'
    // 添加 腾讯X5 依赖
    implementation 'com.tencent.tbs:tbssdk:44286'
}
```
#### 3. 代码获取WebView

```kotlin

// 预初始化 
WebManager.get(this).preCreate()
// 启动时预创建WebView会增加启动时间，可选择空闲时创建
WebManager.get(this).idleCreate()

// 获取WebView（创建或缓存的），页面关闭后会自动缓存或回收
val webView = WebManager.get(this).getWebView(this)
// 添加后退检测，判断是否关闭
onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() {
        if (WebManager.canGoBack(webView)) {
            webView.goBack()
        } else {
            finish()
        }
    }
})
```


### WebView状态

1. IDLE 空闲状态，可以重新使用
2. IN_USE 正在使用
3. DIRTY 被回收但未清理