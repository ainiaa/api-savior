<!-- PDLC-TRACE -->
<!-- 功能ID: B20260803-205116 -->
<!-- 功能名称: config-read-action -->
<!-- 阶段: impl -->
<!-- 前置文档: docs/02_design/generation-state-isolation.md -->
<!-- 创建时间: 2026-08-03T20:54:06+08:00 -->
<!-- 关系: depends_on=B20260803-203714 -->

# 配置索引读取缺少 ReadAction 修复记录

## 根因

`PluginSettingHelper.initConfig` 调用 `FilenameIndex.getFilesByName` 定位配置文件。右键生成改为后台进度任务后，该调用发生在普通线程中、早于生成内容的 `ReadAction`，新版 IDEA 因此抛出 `Read access is allowed from inside read-action only`。

## 修复

将读锁放入 `PluginSettingHelper.initConfig` 的共享入口，使所有调用方均通过 `ReadAction` 查询配置索引；不在各个 Action 中复制读锁代码。

## 回归与验证

- 回归测试确认后台配置加载使用共享读取入口；
- 全量 `gradlew check` 通过；
- 需在 IDEA 中执行一次 Generate Api Interface Doc，确认不再出现 `RuntimeExceptionWithAttachments`。
