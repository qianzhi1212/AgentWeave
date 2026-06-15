export enum NodeType {
  UserInput = 'userInput',
  LLM = 'llm',
  AudioSynthesis = 'audioSynthesis',
  End = 'end',
}

export enum NodeStatus {
  Idle = 'idle',
  Running = 'running',
  Success = 'success',
  Error = 'error',
}

export interface UserInputNodeData {
  label: string;
  type: NodeType.UserInput;
  variableName: string;
  placeholder: string;
}

export interface LLMNodeData {
  label: string;
  type: NodeType.LLM;
  providerId: number | null;
  apiKey: string;
  baseUrl: string;
  model: string;
  temperature: number;
  maxTokens: number;
  systemPrompt: string;
  userPromptTemplate: string;
}

export interface AudioSynthesisNodeData {
  label: string;
  type: NodeType.AudioSynthesis;
  voiceId: string;
  speed: number;
  volume: number;
  format: 'mp3' | 'wav' | 'pcm';
}

export interface EndNodeData {
  label: string;
  type: NodeType.End;
  outputVariable: string;
}

export type FlowNodeData =
  | UserInputNodeData
  | LLMNodeData
  | AudioSynthesisNodeData
  | EndNodeData;
