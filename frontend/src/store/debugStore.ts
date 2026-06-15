import { create } from 'zustand';
import { StepResult } from '../types/api';
import { executionService } from '../services/executionService';
import { useWorkflowStore } from './workflowStore';
import { message } from 'antd';

interface DebugState {
  isDrawerOpen: boolean;
  debugInput: string;
  isExecuting: boolean;
  executionId: number | null;
  stepResults: StepResult[];
  audioUrl: string | null;

  openDrawer: () => void;
  closeDrawer: () => void;
  setDebugInput: (text: string) => void;

  startExecution: () => Promise<void>;
  resetExecution: () => void;
}

export const useDebugStore = create<DebugState>((set, get) => ({
  isDrawerOpen: false,
  debugInput: '',
  isExecuting: false,
  executionId: null,
  stepResults: [],
  audioUrl: null,

  openDrawer: () => set({ isDrawerOpen: true }),
  closeDrawer: () => set({ isDrawerOpen: false }),

  setDebugInput: (text) => set({ debugInput: text }),

  startExecution: async () => {
    const { debugInput } = get();
    const { workflowId, nodes } = useWorkflowStore.getState();

    if (!workflowId) {
      message.error('请先保存工作流');
      return;
    }

    if (!debugInput.trim()) {
      message.error('请输入调试文本');
      return;
    }

    set({ isExecuting: true, stepResults: [], audioUrl: null });

    // Map nodes to step results (initial state)
    const initialSteps: StepResult[] = nodes.map(n => ({
      nodeId: n.id,
      nodeName: (n.data as any).label || n.id,
      nodeType: n.type || '',
      status: 'idle',
      input: null,
      output: null,
    }));
    set({ stepResults: initialSteps });

    try {
      const response = await executionService.debug({
        workflowId,
        userInput: debugInput,
      });

      const data = response.data;

      if (data.nodeResults) {
        // Update step results with execution data
        const updatedSteps: StepResult[] = data.nodeResults.map((nr) => {
          const matchingNode = nodes.find(n => n.id === nr.nodeKey || (n.data as any).nodeKey === nr.nodeKey);
          return {
            nodeId: nr.nodeKey,
            nodeName: matchingNode ? (matchingNode.data as any).label : nr.nodeKey,
            nodeType: nr.nodeType,
            status: nr.status === 'SUCCESS' ? 'success' : nr.status === 'FAILED' ? 'error' : 'running',
            input: nr.inputData ? JSON.parse(nr.inputData) : null,
            output: nr.outputData ? JSON.parse(nr.outputData) : null,
            error: nr.errorMessage,
            durationMs: nr.durationMs,
          };
        });
        set({ stepResults: updatedSteps });
      }

      // Extract audio URL from final output
      if (data.finalOutput) {
        try {
          const finalData = JSON.parse(data.finalOutput);
          if (finalData.audioUrl) {
            set({ audioUrl: finalData.audioUrl });
          }
        } catch {
          // Ignore parse errors
        }
      }

      set({
        isExecuting: false,
        executionId: data.executionId,
      });

      if (data.status === 'SUCCESS') {
        message.success('工作流执行成功');
      } else {
        message.error(`工作流执行失败: ${data.errorMessage || '未知错误'}`);
      }

    } catch (error: any) {
      set({ isExecuting: false });
      message.error(`执行失败: ${error.message || '网络错误'}`);
    }
  },

  resetExecution: () => set({
    isExecuting: false,
    executionId: null,
    stepResults: [],
    audioUrl: null,
    debugInput: '',
  }),
}));
