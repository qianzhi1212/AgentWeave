import React from 'react';
import { Input, InputNumber, Slider, Select, Form } from 'antd';
import { useWorkflowStore } from '../../store/workflowStore';
import { NodeType } from '../../types/node';

export const NodeConfigPanel: React.FC = () => {
  const selectedNodeId = useWorkflowStore((s) => s.selectedNodeId);
  const nodes = useWorkflowStore((s) => s.nodes);
  const updateNodeData = useWorkflowStore((s) => s.updateNodeData);

  const selectedNode = nodes.find((n) => n.id === selectedNodeId);

  if (!selectedNode) {
    return (
      <div style={{ padding: '16px', color: '#999', textAlign: 'center' }}>
        请选择一个节点进行配置
      </div>
    );
  }

  const data = selectedNode.data as Record<string, unknown>;

  if (data.type === NodeType.UserInput || data.type === NodeType.End) {
    return (
      <div style={{ padding: '16px' }}>
        <h4>{String(data.label)}</h4>
        <p style={{ color: '#666', fontSize: '12px' }}>
          {data.type === NodeType.UserInput ? '用户输入节点：接收用户文本输入' : '结束节点：流程终止'}
        </p>
      </div>
    );
  }

  if (data.type === NodeType.LLM) {
    return (
      <div style={{ padding: '16px' }}>
        <h4>🤖 {String(data.label)} 配置</h4>
        <Form layout="vertical" size="small">
          <Form.Item label="模型名称">
            <Input
              value={String(data.model || 'gpt-4o')}
              onChange={(e) => updateNodeData(selectedNodeId!, { model: e.target.value })}
            />
          </Form.Item>
          <Form.Item label="API Key">
            <Input.Password
              value={String(data.apiKey || '')}
              onChange={(e) => updateNodeData(selectedNodeId!, { apiKey: e.target.value })}
              placeholder="sk-..."
            />
          </Form.Item>
          <Form.Item label="Base URL">
            <Input
              value={String(data.baseUrl || '')}
              onChange={(e) => updateNodeData(selectedNodeId!, { baseUrl: e.target.value })}
              placeholder="https://api.deepseek.com/v1"
            />
            <div style={{ fontSize: '11px', color: '#999' }}>兼容 OpenAI 格式的 API 地址，默认使用 DeepSeek</div>
          </Form.Item>
          <Form.Item label="温度 (Temperature)">
            <Slider
              min={0} max={2} step={0.1}
              value={Number(data.temperature || 0.7)}
              onChange={(v) => updateNodeData(selectedNodeId!, { temperature: v })}
            />
          </Form.Item>
          <Form.Item label="最大 Tokens">
            <InputNumber
              value={Number(data.maxTokens || 2048)}
              onChange={(v) => updateNodeData(selectedNodeId!, { maxTokens: v })}
              min={1} max={32000}
            />
          </Form.Item>
          <Form.Item label="系统提示词">
            <Input.TextArea
              rows={3}
              value={String(data.systemPrompt || '')}
              onChange={(e) => updateNodeData(selectedNodeId!, { systemPrompt: e.target.value })}
            />
          </Form.Item>
          <Form.Item label="用户提示词模板">
            <Input.TextArea
              rows={3}
              value={String(data.userPromptTemplate || '{{input}}')}
              onChange={(e) => updateNodeData(selectedNodeId!, { userPromptTemplate: e.target.value })}
            />
            <div style={{ fontSize: '11px', color: '#999' }}>使用 {'{{input}}'} 引用用户输入，{'{{nodeKey.text}}'} 引用上游节点输出</div>
          </Form.Item>
        </Form>
      </div>
    );
  }

  if (data.type === NodeType.AudioSynthesis) {
    return (
      <div style={{ padding: '16px' }}>
        <h4>🔊 {String(data.label)} 配置</h4>
        <Form layout="vertical" size="small">
          <Form.Item label="音色">
            <Select
              value={String(data.voiceId || 'alloy')}
              onChange={(v) => updateNodeData(selectedNodeId!, { voiceId: v })}
              options={[
                { value: 'alloy', label: 'Alloy (中性)' },
                { value: 'echo', label: 'Echo (男声)' },
                { value: 'fable', label: 'Fable (故事)' },
                { value: 'onyx', label: 'Onyx (深沉)' },
                { value: 'nova', label: 'Nova (女声)' },
                { value: 'shimmer', label: 'Shimmer (柔和)' },
              ]}
            />
          </Form.Item>
          <Form.Item label="语速">
            <Slider
              min={0.5} max={2} step={0.1}
              value={Number(data.speed || 1.0)}
              onChange={(v) => updateNodeData(selectedNodeId!, { speed: v })}
            />
          </Form.Item>
          <Form.Item label="输出格式">
            <Select
              value={String(data.format || 'mp3')}
              onChange={(v) => updateNodeData(selectedNodeId!, { format: v })}
              options={[
                { value: 'mp3', label: 'MP3' },
                { value: 'wav', label: 'WAV' },
                { value: 'pcm', label: 'PCM' },
              ]}
            />
          </Form.Item>
        </Form>
      </div>
    );
  }

  return null;
};
