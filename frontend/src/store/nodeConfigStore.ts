import { create } from 'zustand';
import { FlowNodeData } from '../types/node';
import { useWorkflowStore } from './workflowStore';

interface NodeConfigState {
  editingNodeId: string | null;
  draftConfig: Record<string, unknown>;

  startEditing: (nodeId: string, currentData: FlowNodeData) => void;
  updateDraft: (key: string, value: unknown) => void;
  commitDraft: () => void;
  cancelEditing: () => void;
}

export const useNodeConfigStore = create<NodeConfigState>((set, get) => ({
  editingNodeId: null,
  draftConfig: {},

  startEditing: (nodeId, currentData) => set({
    editingNodeId: nodeId,
    draftConfig: { ...currentData } as Record<string, unknown>,
  }),

  updateDraft: (key, value) => set({
    draftConfig: { ...get().draftConfig, [key]: value },
  }),

  commitDraft: () => {
    const { editingNodeId, draftConfig } = get();
    if (editingNodeId) {
      useWorkflowStore.getState().updateNodeData(editingNodeId, draftConfig as Partial<FlowNodeData>);
    }
    set({ editingNodeId: null, draftConfig: {} });
  },

  cancelEditing: () => set({ editingNodeId: null, draftConfig: {} }),
}));
