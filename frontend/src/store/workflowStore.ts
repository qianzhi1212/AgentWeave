import { create } from 'zustand';
import {
  Node,
  Edge,
  OnNodesChange,
  OnEdgesChange,
  OnConnect,
  applyNodeChanges,
  applyEdgeChanges,
  Connection,
} from '@xyflow/react';

interface WorkflowState {
  workflowId: number | null;
  workflowName: string;
  isDirty: boolean;
  isExecuting: boolean;

  nodes: Node[];
  edges: Edge[];

  selectedNodeId: string | null;

  setWorkflowId: (id: number | null) => void;
  setWorkflowName: (name: string) => void;
  setIsExecuting: (val: boolean) => void;

  addNode: (node: Node) => void;
  updateNodeData: (id: string, data: Partial<Record<string, unknown>>) => void;
  removeNode: (id: string) => void;

  addEdge: (edge: Edge) => void;
  removeEdge: (id: string) => void;

  setSelectedNodeId: (id: string | null) => void;

  onNodesChange: OnNodesChange;
  onEdgesChange: OnEdgesChange;
  onConnect: OnConnect;

  resetWorkflow: () => void;
}

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
  workflowId: null,
  workflowName: '未命名工作流',
  isDirty: false,
  isExecuting: false,
  nodes: [],
  edges: [],
  selectedNodeId: null,

  setWorkflowId: (id) => set({ workflowId: id }),
  setWorkflowName: (name) => set({ workflowName: name, isDirty: true }),
  setIsExecuting: (val) => set({ isExecuting: val }),

  addNode: (node) => set({
    nodes: [...get().nodes, node],
    isDirty: true,
  }),

  updateNodeData: (id, data) => set({
    nodes: get().nodes.map(n =>
      n.id === id ? { ...n, data: { ...n.data as Record<string, unknown>, ...data } } : n
    ),
    isDirty: true,
  }),

  removeNode: (id) => set({
    nodes: get().nodes.filter(n => n.id !== id),
    edges: get().edges.filter(e => e.source !== id && e.target !== id),
    selectedNodeId: get().selectedNodeId === id ? null : get().selectedNodeId,
    isDirty: true,
  }),

  addEdge: (edge) => set({
    edges: [...get().edges, edge],
    isDirty: true,
  }),

  removeEdge: (id) => set({
    edges: get().edges.filter(e => e.id !== id),
    isDirty: true,
  }),

  setSelectedNodeId: (id) => set({ selectedNodeId: id }),

  onNodesChange: (changes) => set({
    nodes: applyNodeChanges(changes, get().nodes),
    isDirty: true,
  }),

  onEdgesChange: (changes) => set({
    edges: applyEdgeChanges(changes, get().edges),
    isDirty: true,
  }),

  onConnect: (connection: Connection) => {
    const { nodes } = get();
    const sourceNode = nodes.find(n => n.id === connection.source);
    const targetNode = nodes.find(n => n.id === connection.target);

    if (!sourceNode || !targetNode) return;

    // Prevent self-loop
    if (connection.source === connection.target) return;

    // Check for duplicate edge
    const existing = get().edges.find(
      e => e.source === connection.source && e.target === connection.target
    );
    if (existing) return;

    const edge: Edge = {
      id: `edge-${connection.source}-${connection.target}`,
      source: connection.source,
      target: connection.target,
      sourceHandle: connection.sourceHandle,
      targetHandle: connection.targetHandle,
    };

    set({ edges: [...get().edges, edge], isDirty: true });
  },

  resetWorkflow: () => set({
    workflowId: null,
    workflowName: '未命名工作流',
    isDirty: false,
    isExecuting: false,
    nodes: [],
    edges: [],
    selectedNodeId: null,
  }),
}));
