# IDEA 兼容性手工验证清单

适用于生成文档相关改动的发布前验证。使用 `build/distributions/` 下新生成的插件 ZIP，分别安装到最低支持版本 IntelliJ IDEA 2021.1.1 与目标当前版本。

## 安装与基础操作

- 安装 ZIP 并重启 IDE，确认插件加载且没有 `NoClassDefFoundError`、线程访问断言或初始化异常。
- 对带有 `@GetMapping`、`@PostMapping`、`@PutMapping`、`@PatchMapping`、`@DeleteMapping` 的控制器分别执行单接口和类级文档生成，确认请求方法和路径正确。
- 执行 Postman、Markdown、AMP、OneAPI 与 cURL 导出，确认内容一致且剪贴板、文件写入和提示在任务结束后正常完成。

## 多模块与配置

- 在两个模块各放置 `docer-config.properties`，从两个模块分别触发生成，确认各自只使用同模块配置。
- 在某模块未放置配置文件时触发生成，确认不会随机读取其他模块配置；仅允许使用项目根目录默认配置。
- 修改已生效的配置文件后再次触发生成，确认新配置立即生效。

## HTML 与稳定性

- 在接口说明、链接和图片标题中包含 `<`、`&`、引号及 `javascript:` URL，导出的 HTML 应显示为文本，不能执行脚本。
- 生成包含列数不一致的 Markdown 表格，确认导出不失败。
- 连续批量生成并取消一次任务，确认 IDE 无 EDT/ReadAction 异常、界面无卡死，后续生成仍可正常执行。
