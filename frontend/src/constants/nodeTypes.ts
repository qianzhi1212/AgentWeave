import { NodeType } from '../types/node';

export const NODE_TYPE_LABELS: Record<NodeType, string> = {
  [NodeType.UserInput]: '用户输入',
  [NodeType.LLM]: '大模型',
  [NodeType.AudioSynthesis]: '音频合成',
  [NodeType.End]: '结束',
};

export const NODE_TYPE_COLORS: Record<NodeType, string> = {
  [NodeType.UserInput]: '#52c41a',
  [NodeType.LLM]: '#1890ff',
  [NodeType.AudioSynthesis]: '#722ed1',
  [NodeType.End]: '#ff4d4f',
};

export const DEFAULT_NODE_DATA = {
  [NodeType.UserInput]: {
    label: '用户输入',
    type: NodeType.UserInput,
    variableName: 'input',
    placeholder: '请输入文本...',
  },
  [NodeType.LLM]: {
    label: '大模型',
    type: NodeType.LLM,
    providerId: null,
    apiKey: '',
    baseUrl: 'https://api.deepseek.com/v1',
    model: 'deepseek-chat',
    temperature: 0.7,
    maxTokens: 2048,
    systemPrompt: '',
    userPromptTemplate: '{{input}}',
  },
  [NodeType.AudioSynthesis]: {
    label: '音频合成',
    type: NodeType.AudioSynthesis,
    voiceId: 'alloy',
    speed: 1.0,
    volume: 0.8,
    format: 'mp3' as const,
  },
  [NodeType.End]: {
    label: '结束',
    type: NodeType.End,
    outputVariable: 'result',
  },
};
