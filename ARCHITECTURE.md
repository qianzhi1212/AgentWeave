# Architecture

本文档梳理 AI Agent 流图执行面板的项目结构与核心实现原理。

## 整体架构

采用前后端分离架构，通过 RESTful API 通信。

```
┌──────────────────────────────────────────────────────┐
│                    Frontend (React)                   │
│  React Flow 画布  ←→  Zustand Store  ←→  Axios API   │
└────────────────────────┬─────────────────────────────┘
                         │ HTTP (Vite Proxy /api → :8081)
┌────────────────────────▼─────────────────────────────┐
│                  Backend (Spring Boot)                │
│  Controller → Service → Repository → H2 Database     │
│                         ↓                             │
│              WorkflowEngine (执行引擎)                 │
│         TopologicalSorter → NodeExecutor[]            │
└──────────────────────────────────────────────────────┘
```

## 后端架构

### 分层结构

```
controller/          ← API 入口，参数校验，响应封装
  ├── WorkflowController         工作流 CRUD
  ├── WorkflowNodeController     节点 CRUD
  ├── WorkflowEdgeController     边 CRUD
  ├── ExecutionController        执行 / 调试
  └── LlmProviderController      LLM 服务管理

service/             ← 业务逻辑层
  ├── WorkflowService            工作流生命周期
  ├── WorkflowNodeService        节点管理
  ├── WorkflowEdgeService        边管理
  ├── ExecutionService           执行调度
  └── LlmProviderService         Provider 管理

engine/              ← 工作流执行引擎（核心）
  ├── WorkflowEngine             引擎主流程
  ├── TopologicalSorter          拓扑排序（Kahn 算法）
  ├── ExecutionContext            执行上下文（数据传递）
  ├── NodeExecutor               节点执行器接口
  └── executor/                  4 种节点执行器实现
      ├── UserInputNodeExecutor
      ├── LlmNodeExecutor
      ├── AudioSynthesisNodeExecutor
      └── EndNodeExecutor

entity/              ← JPA 数据实体
repository/          ← Spring Data JPA 仓库
dto/                 ← 请求/响应 DTO
enums/               ← 枚举（NodeType, ExecutionStatus, WorkflowStatus）
llm/                 ← LLM 客户端（OpenAI 兼容协议）
audio/               ← 音频合成服务（Mock / 真实）
```

### 数据模型

```
Workflow (1) ──── (N) WorkflowNode ──── (N) WorkflowEdge
                                  ↑
                          (0..1) LlmProvider

ExecutionRecord (1) ──── (N) ExecutionNodeResult
```

核心实体：

| 实体 | 说明 |
|------|------|
| `Workflow` | 工作流（名称、描述、状态、版本号） |
| `WorkflowNode` | 节点（nodeKey、类型枚举、JSON config、坐标、可选 LLM Provider） |
| `WorkflowEdge` | 边（源节点、目标节点、端口标识） |
| `LlmProvider` | LLM 服务配置（名称、Base URL、API Key、模型、是否默认） |
| `ExecutionRecord` | 执行记录（状态、用户输入、最终输出、调试标记） |
| `ExecutionNodeResult` | 节点执行结果（输入/输出数据、耗时、错误信息） |

### 工作流引擎 — 执行流程

`WorkflowEngine.execute()` 是核心方法，执行流程如下：

```
1. 加载工作流 → 查询所有节点和边
         ↓
2. 拓扑排序 → Kahn 算法确定执行顺序
         ↓
3. 创建 ExecutionRecord（状态=RUNNING）
         ↓
4. 创建 ExecutionContext（持有 userInput + nodeOutputs Map）
         ↓
5. 按拓扑序遍历节点：
   ├── 收集上游输入数据（collectUpstreamInput）
   ├── 查找对应 NodeExecutor（通过 executorMap）
   ├── 执行 executor.execute(node, context)
   ├── 将输出存入 context.nodeOutputs
   └── 记录 NodeExecutionResult（状态、耗时、输入输出）
         ↓
6. 更新 ExecutionRecord（SUCCESS / FAILED + 最终输出）
         ↓
7. 构建 ExecutionResponse 返回
```

### 节点执行器 — 策略模式

`NodeExecutor` 接口定义了统一的节点执行契约：

```java
public interface NodeExecutor {
    NodeType supportedType();
    Object execute(WorkflowNode node, ExecutionContext context);
}
```

通过 Spring 自动注入所有实现，构建 `Map<NodeType, NodeExecutor>` 映射表。

| 执行器 | 行为 |
|--------|------|
| `UserInputNodeExecutor` | 将用户输入包装为 `{"text": "..."}` 输出 |
| `LlmNodeExecutor` | 解析 config 获取 API Key/模型/提示词 → 模板插值 → 调用 LLM API → 输出回复 |
| `AudioSynthesisNodeExecutor` | 提取上游文本 → 调用 TTS 服务 → 输出音频 URL |
| `EndNodeExecutor` | 汇总上游输出作为流程最终结果 |

**LLM 节点 API Key 获取优先级**：
1. 节点 config 中的 `apiKey`（前端配置面板直接填写）
2. 节点绑定的 `LlmProvider`
3. 系统默认 Provider（`isDefault=true`）

### 数据传递机制

`ExecutionContext` 维护一个 `Map<String, Object> nodeOutputs`，key 为 `nodeKey`。

- 每个节点执行后调用 `context.setNodeOutput(nodeKey, output)` 存储输出
- 下游节点通过 `collectUpstreamInput()` 沿边收集上游输出
- 提示词模板支持 `{{input}}`（用户输入）和 `{{nodeKey.text}}`（指定节点输出）变量替换

## 前端架构

### 组件层次

```
App
└── ReactFlowProvider（提供画布上下文）
    └── ConfigProvider（Ant Design 国际化）
        └── AppLayout（页面布局）
            ├── Sidebar（节点面板，拖拽源）
            ├── FlowCanvas（React Flow 画布）
            │   ├── UserInputNode / LLMNode / AudioSynthesisNode / EndNode
            │   └── Background / Controls / MiniMap
            ├── NodeConfigPanel（右侧配置面板）
            └── DebugDrawer（调试抽屉）
```

### 状态管理（Zustand）

| Store | 职责 |
|-------|------|
| `workflowStore` | 工作流状态：节点列表、边列表、选中节点、脏标记、CRUD 操作 |
| `debugStore` | 调试状态：抽屉开关、输入文本、执行中标记、步骤结果、音频 URL |
| `nodeConfigStore` | 节点配置面板辅助状态 |

### 关键交互流程

**保存工作流**：
```
handleSave → workflowService.create/update(workflow)
           → workflowService.get(wid) 获取后端现有节点/边
           → 删除后端旧节点/边
           → 逐个创建前端节点到后端（nodeKey = 前端 node.id）
           → 逐个创建前端边到后端（sourceNodeId / targetNodeId 映射）
```

**调试执行**：
```
startExecution → 初始化 stepResults（idle 状态）
              → executionService.debug({workflowId, userInput})
              → 后端返回 nodeResults[]
              → 匹配前端节点（通过 nodeKey）更新 stepResults
              → 提取 audioUrl 展示播放器
```

### API 代理

Vite 开发服务器配置了 `/api` 代理到 `http://localhost:8081`，前端 axios 实例使用 `/api/v1` 作为 baseURL，开发环境无需额外配置跨域。

## 技术选型

| 层级 | 选型 | 理由 |
|------|------|------|
| 前端框架 | React 18 + TypeScript | 生态成熟，类型安全 |
| 画布引擎 | @xyflow/react (React Flow) | 开箱即用的节点拖拽、连线、缩放能力 |
| UI 组件 | Ant Design | 中文友好，组件丰富 |
| 状态管理 | Zustand | 轻量简洁，无 boilerplate |
| 构建工具 | Vite | 极速 HMR，开发体验好 |
| 后端框架 | Spring Boot 3.3 | 企业级标准，JPA/CORS/校验开箱即用 |
| 数据库 | H2 (文件模式) | MVP 阶段零配置，开发简单 |
| LLM 集成 | OpenAI 兼容协议 | 一套代码支持 DeepSeek/OpenAI/通义等所有兼容服务 |
