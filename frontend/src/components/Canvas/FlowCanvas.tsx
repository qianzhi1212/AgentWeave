import React, { useCallback, useRef } from 'react';
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useReactFlow,
} from '@xyflow/react';
import { NodeType } from '../../types/node';
import { nodeFactory } from '../../utils/nodeFactory';
import { useWorkflowStore } from '../../store/workflowStore';
import { UserInputNode, LLMNode, AudioSynthesisNode, EndNode } from '../Nodes/FlowNodes';

const nodeTypes = {
  userInput: UserInputNode,
  llm: LLMNode,
  audioSynthesis: AudioSynthesisNode,
  end: EndNode,
};

export const FlowCanvas: React.FC = () => {
  const reactFlowInstance = useReactFlow();
  const { nodes, edges, onNodesChange, onEdgesChange, onConnect, addNode, setSelectedNodeId } = useWorkflowStore();
  const isExecuting = useWorkflowStore((s) => s.isExecuting);

  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    const type = event.dataTransfer.getData('application/reactflow') as NodeType;
    if (!type) return;

    const position = reactFlowInstance.screenToFlowPosition({
      x: event.clientX,
      y: event.clientY,
    });

    if (position) {
      const newNode = nodeFactory.create(type, position);
      addNode(newNode as any);
    }
  }, [reactFlowInstance, addNode]);

  const onNodeClick = useCallback((_: React.MouseEvent, node: any) => {
    setSelectedNodeId(node.id);
  }, [setSelectedNodeId]);

  const onPaneClick = useCallback(() => {
    setSelectedNodeId(null);
  }, [setSelectedNodeId]);

  const isValidConnection = useCallback((connection: any) => {
    // Prevent self-connection
    if (connection.source === connection.target) return false;

    // Prevent duplicate edges
    const exists = edges.some(
      (e: any) => e.source === connection.source && e.target === connection.target
    );
    if (exists) return false;

    return true;
  }, [edges]);

  return (
    <div style={{ flex: 1, height: '100vh' }}>
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        onNodeClick={onNodeClick}
        onPaneClick={onPaneClick}
        onDragOver={onDragOver}
        onDrop={onDrop}
        nodeTypes={nodeTypes}
        fitView
        snapToGrid
        snapGrid={[15, 15]}
        isValidConnection={isValidConnection}
        nodesDraggable={!isExecuting}
        nodesConnectable={!isExecuting}
        elementsSelectable={!isExecuting}
        deleteKeyCode={['Backspace', 'Delete']}
      >
        <Background />
        <Controls />
        <MiniMap />
      </ReactFlow>
    </div>
  );
};
