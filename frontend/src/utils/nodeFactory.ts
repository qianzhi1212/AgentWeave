import { NodeType } from '../types/node';
import { DEFAULT_NODE_DATA } from '../constants/nodeTypes';
import { Node } from '@xyflow/react';

let nodeCounter = 0;

export const nodeFactory = {
  create(type: NodeType, position: { x: number; y: number }): Node {
    const id = `${type}-${++nodeCounter}`;
    const data = { ...DEFAULT_NODE_DATA[type] };
    return {
      id,
      type,
      position,
      data,
    };
  },
};
