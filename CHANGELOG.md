# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/lang/zh-CN/).

## [Unreleased]

### Added

- 将插件运行时依赖校验纳入 `check`，防止发布包遗漏源码直接引用的第三方库。
- 为 Gson 自定义序列化、Knife4j 请求参数归一化和 HTTP 响应处理补充回归测试。

### Changed

- 固定插件字节码为 Java 11，并将 Lombok 升级为仅编译期依赖，以兼容新版本 JDK 构建环境与旧版 IDEA 运行环境。
- 使用无溢出的整数比较排序 API 方法，并将文档文件写入改为自动关闭文件流。
- 将单接口和单类文档生成迁移到 IDEA 后台进度任务，完成后再更新剪贴板与弹窗。
- 将 API 方法的 PSI 解析结果收敛为共享 `ApiMethodInfo`，供 Markdown、Postman、AMP、OneAPI 和 cURL 生成器统一消费。

### Fixed

- 显式打包 Apache Commons Collections、Lang、Codec、IO 和 Gson，避免 IDEA 升级后因内部类加载器变化导致 `NoClassDefFoundError`。
- 使 OneAPI 的 `ComplexInfo` 自定义 Gson 序列化器实际生效。
- 保留 Knife4j `includeParameters` 与 `ignoreParameters` 中未带对象前缀的普通参数。
- 为 HTTP 调用增加读取超时、流关闭与错误响应体读取，避免请求无限等待或丢失服务端错误信息。
- 批量导出不再将限定名为空的不同类错误去重。
- 修复并行或连续生成时，项目配置、字段过滤、泛型缓存和字段序号可能串到其他任务的问题。
- 修复新版 IDEA 在后台生成时读取配置索引未持有 ReadAction 而抛出线程访问异常的问题。

[Unreleased]: https://github.com/ainiaa/api-savior/compare/v2.6.1...HEAD
