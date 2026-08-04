<!-- PDLC-TRACE -->
<!-- 功能ID: B20260803-203714 -->
<!-- 功能名称: generation-state-isolation -->
<!-- 阶段: design -->
<!-- 前置文档: 无 -->
<!-- 创建时间: 2026-08-03T20:38:44+08:00 -->

# 生成任务状态隔离设计

## 问题

文档生成会在 IDEA 的 UI 线程和后台进度线程之间切换。当前配置、字段过滤条件、泛型缓存和字段序号使用进程级静态可变状态；并行任务或线程复用时，状态可能串到另一个项目或另一次生成。

## 目标

- 每个执行线程只读取自己的配置和解析状态。
- 配置重新初始化时完全替换旧值。
- 成功、异常和取消路径均释放线程状态。
- 不改变现有 Action、配置文件和生成结果的外部行为。

## 方案

将四类临时状态改为 `ThreadLocal`：

- `PluginSettingHelper` 的当前配置；
- `ResolverContextHolder` 的 hidden/only 过滤条件；
- `PsiTypeUtil` 的泛型参数缓存；
- `IndexIncrementUtil` 的字段序号。

批量和右键生成任务在后台线程开始时初始化配置，在 `finally` 中清理。请求/响应类型解析使用 `finally` 清理过滤上下文。

配置文件定位依赖 `FilenameIndex`，因此 `PluginSettingHelper.initConfig` 必须在共享入口内通过 `ReadAction` 执行，不能依赖调用方恰好已持有读锁。

## 不在本次范围

不提取 `ApiDefinition`，不重构批量导出器，不改变 Markdown、HTML、Postman、AMP 或 OneAPI 的格式逻辑。
