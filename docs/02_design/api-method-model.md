<!-- PDLC-TRACE -->
<!-- 功能ID: F20260803-205826 -->
<!-- 功能名称: api-method-model -->
<!-- 阶段: design -->
<!-- 前置文档: docs/02_design/generation-state-isolation.md -->
<!-- 创建时间: 2026-08-03T21:05:05+08:00 -->
<!-- 关系: depends_on=B20260803-203714,B20260803-205116 -->

# API 方法语义模型

## 目标

将一次 API 方法解析得到的项目、接口类名、PSI 方法、注释信息、请求结构和响应结构收敛为不可变的 `ApiMethodInfo`。格式生成器只消费该模型，不再通过长参数列表传递同一组语义数据。

## 当前接入

`AbstractSavior` 负责构造 `ApiMethodInfo`。Markdown、Postman、AMP、OneAPI 与 cURL 的格式生成回调均改为接收该模型，因此生成内容、模板、文件和远端请求行为保持不变。

## 后续边界

批量导出的 `otherMap` 与文件/网络输出编排仍维持现状，作为 P1.2 单独重构，避免与本次模型提取混合。
