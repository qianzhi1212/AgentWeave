# AI Agent 流图执行面板

可视化 AI 工作流编辑器 — 通过拖拽节点、连线、配置参数搭建 AI 处理流程，实时调试执行结果。

![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=java&logoColor=white)

## 功能特性

- **可视化画布**：基于 React Flow 的拖拽式节点编辑器
- **4 种节点类型**：用户输入、大模型 (LLM)、音频合成、结束
- **节点连线**：通过拖拽手柄连接节点，数据自动沿边流动
- **实时配置**：点击节点即可在右侧面板配置参数
- **大模型集成**：支持 DeepSeek / OpenAI 及所有兼容 OpenAI 协议的 LLM 服务
- **调试执行**：一键运行工作流，逐步查看每个节点的输入/输出/耗时
- **拓扑排序**：自动检测循环依赖，按正确顺序执行节点

## 界面预览

```
┌──────────┬──────────────────────────────────────┬──────────────┐
│ 节点面板  │            画布区域                    │   配置面板    │
│          │                                      │              │
│ 📝用户输入│  📝用户输入 ──→ 🤖大模型 ──→ 🔊音频 ──→ 🏁│  模型: deepseek │
│ 🤖大模型  │                                      │  API Key: ***│
│ 🔊音频合成│                                      │  Base URL: ..│
│ 🏁结束    │                                      │  温度: 0.7   │
└──────────┴──────────────────────────────────────┴──────────────┘
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | React 18 + TypeScript + Vite |
| 画布 | @xyflow/react (React Flow) |
| UI | Ant Design 5 |
| 状态 | Zustand |
| 后端 | Spring Boot 3.3 + Java 17 |
| 数据库 | H2 (内嵌文件模式) |
| LLM | OpenAI 兼容协议 (RestClient) |

## 快速开始

### 环境要求

- **Java 17+**
- **Node.js 18+**
- **Maven 3.9+**

### 1. 编译后端

```bash
cd backend
mvn clean package -DskipTests
```

### 2. 启动后端

```bash
java -jar target/workflow-engine-1.0.0-SNAPSHOT.jar --server.port=8081
```

等待输出 `Started AiWorkflowEngineApplication in x.xxx seconds`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 4. 打开浏览器

访问 http://localhost:5173

## 使用指南

### 搭建工作流

1. 从左侧面板拖拽节点到画布
2. 从节点**右侧小圆点**拖线到下一个节点的**左侧小圆点**
3. 搭建流程：`用户输入 → 大模型 → 音频合成 → 结束`

### 配置大模型节点

点击大模型节点，在右侧配置面板填写：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| 模型名称 | 模型标识 | `deepseek-chat` |
| API Key | API 密钥 | （空，必填） |
| Base URL | API 地址 | `https://api.deepseek.com/v1` |
| 温度 | 0~2 | `0.7` |
| 最大 Tokens | 回复长度上限 | `2048` |
| 系统提示词 | 设定 AI 角色 | （空） |
| 用户提示词模板 | `{{input}}` 引用输入 | `{{input}}` |

> 支持所有兼容 OpenAI `/chat/completions` 接口的服务（DeepSeek、OpenAI、通义千问等）。

### 保存与调试

1. 点击 **「保存工作流」** — 将画布上的节点和连线同步到后端
2. 点击 **「调试 ▶」** — 打开调试面板
3. 输入测试文本，点击 **「开始调试」**
4. 查看时间线中每个节点的执行状态、输出数据和耗时

### 支持的 LLM 服务

| 服务 | Base URL | 模型名称 |
|------|----------|---------|
| DeepSeek | `https://api.deepseek.com/v1` | `deepseek-chat` |
| OpenAI | `https://api.openai.com/v1` | `gpt-4o` |
| 其他兼容服务 | 对应 API 地址 | 对应模型标识 |

## 项目结构

```
ai-agent-flow/
├── backend/                         # 后端 (Spring Boot)
│   ├── pom.xml
│   └── src/main/java/com/ai/workflow/
│       ├── controller/              # REST API 控制器
│       ├── service/                 # 业务逻辑
│       ├── engine/                  # 工作流执行引擎
│       │   ├── WorkflowEngine       #   引擎主流程
│       │   ├── TopologicalSorter    #   拓扑排序 (Kahn)
│       │   ├── ExecutionContext     #   执行上下文
│       │   └── executor/           #   节点执行器
│       ├── entity/                  # JPA 实体
│       ├── repository/              # 数据访问
│       ├── dto/                     # 请求/响应 DTO
│       ├── llm/                     # LLM 客户端
│       ├── audio/                   # 音频合成
│       └── enums/                   # 枚举定义
│
├── frontend/                        # 前端 (React + Vite)
│   ├── package.json
│   └── src/
│       ├── components/              # UI 组件
│       │   ├── Canvas/             #   React Flow 画布
│       │   ├── Nodes/              #   4 种节点组件
│       │   ├── Sidebar/            #   左侧节点面板
│       │   ├── NodeConfig/         #   右侧配置面板
│       │   ├── DebugDrawer/        #   调试抽屉
│       │   └── Layout/             #   页面布局
│       ├── store/                   # Zustand 状态管理
│       ├── services/                # API 调用封装
│       ├── types/                   # TypeScript 类型
│       └── constants/               # 常量定义
│
├── ARCHITECTURE.md                  # 架构设计文档
└── README.md                        # 本文件
```

详细架构设计请参阅 [ARCHITECTURE.md](./ARCHITECTURE.md)。

## 常见问题

**端口被占用**
```bash
# 找到占用 8081 的进程并结束
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

**大模型调用超时**
确认 Base URL 可访问。默认使用 `https://api.deepseek.com/v1`，确保网络可达。

**大模型报错 "No default LLM provider configured"**
在大模型节点配置面板中填写 API Key 即可，无需额外配置 Provider。

**音频无法播放**
当前为 Mock 模式（模拟 URL）。接入真实 TTS 服务请修改 `application.yml`：
```yaml
audio:
  synthesis:
    provider: real
    api-url: https://your-tts-api.com/v1/synthesize
    api-key: your-key
```

## License

MIT

> 特别致谢：[二哥的 PaiAgent](https://gitcode.com/javabetter/PaiAgent) 为本项目提供了重要的学习和参考基础。
