# WebView Pool

在Android应用中经常使用H5的场景，需要对WebView的加载复用来优化速度。

- 预初始化WebView
- 缓存复用WebView

为了简单实用WebView，不关心复用WebView。本库实现：

- [x] 预初始化WebView
- [ ] 空闲时，预初始化WebView
- [x] 缓存复用WebView，使用LFU算法或自定义处理缓存
- [x] 清理和复用WebView

### WebView状态

1. IDLE 空闲状态，可以重新使用
2. IN_USE 正在使用
3. DIRTY 被回收但未清理