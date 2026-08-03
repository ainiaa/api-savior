<!-- PDLC-TRACE -->
<!-- 功能ID: B20260803-203714 -->
<!-- 功能名称: generation-state-isolation -->
<!-- 阶段: impl -->
<!-- 前置文档: docs/02_design/generation-state-isolation.md -->
<!-- 创建时间: 2026-08-03T20:43:59+08:00 -->

# 生成任务状态串扰修复记录

## 根因

插件将当前项目配置、字段过滤条件、泛型解析缓存和字段序号保存在进程级静态可变对象中。后台进度任务会复用线程，因此不同项目或不同生成任务可能读到前一任务的值；配置缓存还会通过 `putAll` 保留旧键。

## 影响范围

- 右键和批量的 Markdown、HTML、Postman、AMP、OneAPI 生成；
- 请求/响应字段的 hidden 与 only 过滤；
- 泛型实际类型解析和字段序号。

## 修复

- 将四类短生命周期状态改为 `ThreadLocal`；
- 配置加载改为清空后覆盖；
- 解析请求和响应结构时使用 `finally` 清理过滤上下文；
- 批量及右键后台任务结束时清理配置、泛型缓存和字段序号。

## 回归测试

- 不同线程读取配置时互不影响；
- 同线程重新初始化配置时不保留旧键；
- 不同线程的字段过滤、泛型缓存和字段序号互不影响；
- `sh gradlew -Dorg.gradle.java.home=/Users/liuwenyuan/Library/Java/JavaVirtualMachines/temurin-11.0.24/Contents/Home check --console=plain` 通过。

## 人工验证

在两个不同项目窗口分别配置不同的 `docer-config.properties`，交替执行一次右键生成和一次批量导出；确认目录、URL 前缀与模板配置均取自当前项目，并重复执行后不出现前一项目的值。
