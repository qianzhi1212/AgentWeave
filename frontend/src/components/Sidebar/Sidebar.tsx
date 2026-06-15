import React from 'react';
import { NodeType } from '../../types/node';
import { NODE_TYPE_LABELS, NODE_TYPE_COLORS } from '../../constants/nodeTypes';

interface DraggableNodeItemProps {
  type: NodeType;
}

const DraggableNodeItem: React.FC<DraggableNodeItemProps> = ({ type }) => {
  const onDragStart = (event: React.DragEvent) => {
    event.dataTransfer.setData('application/reactflow', type);
    event.dataTransfer.effectAllowed = 'move';
  };

  const iconMap: Record<NodeType, string> = {
    [NodeType.UserInput]: '📝',
    [NodeType.LLM]: '🤖',
    [NodeType.AudioSynthesis]: '🔊',
    [NodeType.End]: '🏁',
  };

  return (
    <div
      draggable
      onDragStart={onDragStart}
      style={{
        padding: '10px 16px',
        margin: '4px 8px',
        borderRadius: '6px',
        border: `2px solid ${NODE_TYPE_COLORS[type]}`,
        background: `${NODE_TYPE_COLORS[type]}10`,
        cursor: 'grab',
        fontSize: '13px',
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
      }}
    >
      <span>{iconMap[type]}</span>
      <span>{NODE_TYPE_LABELS[type]}</span>
    </div>
  );
};

export const Sidebar: React.FC = () => {
  return (
    <div
      style={{
        width: '240px',
        background: '#fafafa',
        borderRight: '1px solid #e8e8e8',
        padding: '16px 0',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <div style={{ padding: '0 16px', fontWeight: 'bold', fontSize: '15px', marginBottom: '12px' }}>
        节点面板
      </div>
      <div style={{ padding: '0 8px', fontSize: '12px', color: '#666', marginBottom: '8px' }}>
        大模型节点
      </div>
      <DraggableNodeItem type={NodeType.UserInput} />
      <DraggableNodeItem type={NodeType.LLM} />
      <div style={{ padding: '0 8px', fontSize: '12px', color: '#666', marginTop: '8px', marginBottom: '8px' }}>
        工具节点
      </div>
      <DraggableNodeItem type={NodeType.AudioSynthesis} />
      <DraggableNodeItem type={NodeType.End} />
    </div>
  );
};
