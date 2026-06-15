import { Handle, Position } from '@xyflow/react';
import { NodeType } from '../../types/node';
import { NODE_TYPE_COLORS } from '../../constants/nodeTypes';
import { StatusBadge } from '../common/StatusBadge';

const nodeStyle: React.CSSProperties = {
  padding: '12px 20px',
  borderRadius: '8px',
  fontSize: '12px',
  color: '#333',
  border: '2px solid',
  minWidth: '150px',
  maxWidth: '220px',
  textAlign: 'center',
};

export function UserInputNode({ data, id }: { data: any; id: string }) {
  const style = {
    ...nodeStyle,
    borderColor: NODE_TYPE_COLORS[NodeType.UserInput],
    background: '#f6ffed',
  };

  return (
    <div style={style}>
      <Handle type="source" position={Position.Right} id="output" style={{ background: '#555' }} />
      <div style={{ fontWeight: 'bold' }}>📝 {data.label || '用户输入'}</div>
      <div style={{ fontSize: '11px', color: '#666' }}>变量: {data.variableName || 'input'}</div>
      <StatusBadge nodeId={id} />
    </div>
  );
}

export function LLMNode({ data, id }: { data: any; id: string }) {
  const style = {
    ...nodeStyle,
    borderColor: NODE_TYPE_COLORS[NodeType.LLM],
    background: '#e6f7ff',
  };

  return (
    <div style={style}>
      <Handle type="target" position={Position.Left} id="input" style={{ background: '#555' }} />
      <Handle type="source" position={Position.Right} id="output" style={{ background: '#555' }} />
      <div style={{ fontWeight: 'bold' }}>🤖 {data.label || '大模型'}</div>
      <div style={{ fontSize: '11px', color: '#666' }}>{data.model || 'gpt-4o'} · T={data.temperature || 0.7}</div>
      <StatusBadge nodeId={id} />
    </div>
  );
}

export function AudioSynthesisNode({ data, id }: { data: any; id: string }) {
  const style = {
    ...nodeStyle,
    borderColor: NODE_TYPE_COLORS[NodeType.AudioSynthesis],
    background: '#f9ebff',
  };

  return (
    <div style={style}>
      <Handle type="target" position={Position.Left} id="input" style={{ background: '#555' }} />
      <Handle type="source" position={Position.Right} id="output" style={{ background: '#555' }} />
      <div style={{ fontWeight: 'bold' }}>🔊 {data.label || '音频合成'}</div>
      <div style={{ fontSize: '11px', color: '#666' }}>音色: {data.voiceId || 'alloy'} · {data.format || 'mp3'}</div>
      <StatusBadge nodeId={id} />
    </div>
  );
}

export function EndNode({ data, id }: { data: any; id: string }) {
  const style = {
    ...nodeStyle,
    borderColor: NODE_TYPE_COLORS[NodeType.End],
    background: '#fff1f0',
  };

  return (
    <div style={style}>
      <Handle type="target" position={Position.Left} id="input" style={{ background: '#555' }} />
      <div style={{ fontWeight: 'bold' }}>🏁 {data.label || '结束'}</div>
      <StatusBadge nodeId={id} />
    </div>
  );
}
