export interface ExecutionRequest {
  workflowId: number;
  userInput: string;
}

export interface ExecutionResponse {
  executionId: number;
  workflowId: number;
  status: string;
  userInput: string;
  finalOutput: string | null;
  errorMessage: string | null;
  isDebug: boolean;
  nodeResults: NodeExecutionResult[] | null;
  startedAt: string | null;
  finishedAt: string | null;
}

export interface NodeExecutionResult {
  nodeKey: string;
  nodeType: string;
  status: string;
  inputData: string | null;
  outputData: string | null;
  errorMessage: string | null;
  tokenUsage: number | null;
  durationMs: number | null;
  startedAt: string | null;
  finishedAt: string | null;
}

export interface StepResult {
  nodeId: string;
  nodeName: string;
  nodeType: string;
  status: 'running' | 'success' | 'error' | 'idle';
  input: unknown;
  output: unknown;
  error?: string;
  durationMs?: number;
}

export interface LlmProviderResponse {
  id: number;
  name: string;
  baseUrl: string;
  apiKeyMasked: string;
  model: string;
  isDefault: boolean;
}
