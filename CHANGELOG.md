# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/lang/zh-CN/).

## [Unreleased]

### Added

- 将插件运行时依赖校验纳入 `check`，防止发布包遗漏源码直接引用的第三方库。

### Changed

- 固定插件字节码为 Java 11，并将 Lombok 升级为仅编译期依赖，以兼容新版本 JDK 构建环境与旧版 IDEA 运行环境。

### Fixed

- 显式打包 Apache Commons Collections、Lang、Codec、IO、HttpCore 和 Gson，避免 IDEA 升级后因内部类加载器变化导致 `NoClassDefFoundError`。

[Unreleased]: https://github.com/ainiaa/api-savior/compare/v2.6.1...HEAD
