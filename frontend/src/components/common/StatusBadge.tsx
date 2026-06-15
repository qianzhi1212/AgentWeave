import React from 'react';
import { NodeStatus } from '../../types/node';
import { useDebugStore } from '../../store/debugStore';

export const StatusBadge: React.FC<{ nodeId: string }> = ({ nodeId }) => {
  const stepResults = useDebugStore((s) => s.stepResults);
  const step = stepResults.find(r => r.nodeId === nodeId);

  if (!step) return null;

  const statusMap: Record<NodeStatus, { color: string; text: string }> = {
    [NodeStatus.Idle]: { color: '#999', text: '等待' },
    [NodeStatus.Running]: { color: '#1890ff', text: '运行中' },
    [NodeStatus.Success]: { color: '#52c41a', text: '成功' },
    [NodeStatus.Error]: { color: '#ff4d4f', text: '错误' },
  };

  const { color, text } = statusMap[step.status as NodeStatus] || statusMap[NodeStatus.Idle];

  return (
    <span
      style={{
        fontSize: '10px',
        color,
        background: `${color}20`,
        padding: '2px 6px',
        borderRadius: '4px',
        marginLeft: '4px',
      }}
    >
      {text}
    </span>
  );
};
