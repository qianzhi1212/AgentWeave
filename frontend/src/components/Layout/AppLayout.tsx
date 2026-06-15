import React from 'react';
import { Button, message } from 'antd';
import { Sidebar } from '../Sidebar/Sidebar';
import { FlowCanvas } from '../Canvas/FlowCanvas';
import { NodeConfigPanel } from '../NodeConfig/NodeConfigPanel';
import { DebugDrawer } from '../DebugDrawer/DebugDrawer';
import { useWorkflowStore } from '../../store/workflowStore';
import { useDebugStore } from '../../store/debugStore';
import { workflowService } from '../../services/workflowService';

const NODE_TYPE_TO_BACKEND: Record<string, string> = {
  userInput: 'USER_INPUT',
  llm: 'LLM',
  audioSynthesis: 'AUDIO_SYNTHESIS',
  end: 'END',
};

export const AppLayout: React.FC = () => {
  const workflowId = useWorkflowStore((s) => s.workflowId);
  const workflowName = useWorkflowStore((s) => s.workflowName);
  const nodes = useWorkflowStore((s) => s.nodes);
  const edges = useWorkflowStore((s) => s.edges);
  const isDirty = useWorkflowStore((s) => s.isDirty);
  const setWorkflowId = useWorkflowStore((s) => s.setWorkflowId);
  const { openDrawer } = useDebugStore();

  const [isSaving, setIsSaving] = React.useState(false);

  const handleSave = async () => {
    setIsSaving(true);
    try {
      let wid = workflowId;

      // 1. Create or update workflow
      if (!wid) {
        const res = await workflowService.create(workflowName || '未命名工作流');
        wid = res.data.id;
        setWorkflowId(wid);
      } else {
        await workflowService.update(wid, workflowName || '未命名工作流');
      }

      // 2. Fetch current backend state
      const fullWorkflow = await workflowService.get(wid);
      const backendNodes = fullWorkflow.data.nodes || [];
      const backendEdges = fullWorkflow.data.edges || [];

      // 3. Delete all existing backend edges (before deleting nodes)
      for (const edge of backendEdges) {
        await workflowService.deleteEdge(wid, edge.id);
      }

      // 4. Delete all existing backend nodes
      for (const node of backendNodes) {
        await workflowService.deleteNode(wid, node.id);
      }

      // 5. Create new backend nodes, building frontendId → backendId map
      const nodeIdMap: Record<string, number> = {};
      for (const node of nodes) {
        const data = node.data as Record<string, unknown>;
        let config: string | undefined;
        if (data.type === 'llm') {
          config = JSON.stringify({
            model: data.model,
            apiKey: data.apiKey,
            baseUrl: data.baseUrl,
            temperature: data.temperature,
            maxTokens: data.maxTokens,
            systemPrompt: data.systemPrompt,
            userPromptTemplate: data.userPromptTemplate,
          });
        } else if (data.type === 'audioSynthesis') {
          config = JSON.stringify({
            voiceId: data.voiceId,
            speed: data.speed,
            volume: data.volume,
            format: data.format,
          });
        }

        const res = await workflowService.addNode(wid, {
          nodeKey: node.id,
          name: (data.label as string) || node.id,
          nodeType: NODE_TYPE_TO_BACKEND[node.type || ''] || node.type || '',
          config,
          positionX: Math.round(node.position.x),
          positionY: Math.round(node.position.y),
        });
        nodeIdMap[node.id] = res.data.id;
      }

      // 6. Create new backend edges
      for (const edge of edges) {
        const sourceBackendId = nodeIdMap[edge.source];
        const targetBackendId = nodeIdMap[edge.target];
        if (sourceBackendId && targetBackendId) {
          await workflowService.addEdge(wid, {
            sourceNodeId: sourceBackendId,
            targetNodeId: targetBackendId,
            sourcePort: edge.sourceHandle || 'output',
            targetPort: edge.targetHandle || 'input',
          });
        }
      }

      message.success(`工作流已保存（${nodes.length} 个节点, ${edges.length} 条连线）`);
    } catch (error: any) {
      console.error('Save failed:', error);
      message.error('保存失败: ' + (error.response?.data?.message || error.message));
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div style={{ display: 'flex', height: '100vh', background: '#fff' }}>
      {/* Sidebar */}
      <Sidebar />

      {/* Canvas area */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* Top bar */}
        <div
          style={{
            height: '48px',
            borderBottom: '1px solid #e8e8e8',
            display: 'flex',
            alignItems: 'center',
            padding: '0 16px',
            justifyContent: 'space-between',
            background: '#fafafa',
          }}
        >
          <div style={{ fontWeight: 'bold', fontSize: '16px' }}>
            🔄 {workflowName || 'AI Agent 流图'}
          </div>
          <div style={{ display: 'flex', gap: '8px' }}>
            <Button onClick={handleSave} disabled={!isDirty} loading={isSaving}>
              保存工作流
            </Button>
            <Button type="primary" onClick={openDrawer}>
              调试 ▶
            </Button>
          </div>
        </div>

        {/* Canvas */}
        <div style={{ flex: 1, display: 'flex' }}>
          <FlowCanvas />
          {/* Config panel - show when node selected */}
          <div
            style={{
              width: '280px',
              borderLeft: '1px solid #e8e8e8',
              background: '#fff',
              overflowY: 'auto',
            }}
          >
            <NodeConfigPanel />
          </div>
        </div>
      </div>

      {/* Debug Drawer */}
      <DebugDrawer />
    </div>
  );
};
