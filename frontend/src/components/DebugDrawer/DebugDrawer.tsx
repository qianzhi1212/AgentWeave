import React from 'react';
import { Drawer, Input, Button, Timeline, message } from 'antd';
import { useDebugStore } from '../../store/debugStore';
import { useWorkflowStore } from '../../store/workflowStore';

export const DebugDrawer: React.FC = () => {
  const {
    isDrawerOpen,
    closeDrawer,
    debugInput,
    setDebugInput,
    isExecuting,
    stepResults,
    audioUrl,
    startExecution,
    resetExecution,
  } = useDebugStore();

  const workflowId = useWorkflowStore((s) => s.workflowId);
  const nodes = useWorkflowStore((s) => s.nodes);
  const edges = useWorkflowStore((s) => s.edges);

  const handleExecute = async () => {
    if (!debugInput.trim()) {
      message.warning('请输入调试文本');
      return;
    }
    if (!workflowId) {
      message.warning('请先创建工作流');
      return;
    }

    try {
      await startExecution();
    } catch (error: any) {
      message.error('执行失败: ' + (error.message || '未知错误'));
    }
  };

  const statusColorMap: Record<string, string> = {
    running: 'blue',
    success: 'green',
    error: 'red',
    idle: 'gray',
  };

  return (
    <Drawer
      title="调试面板"
      placement="right"
      width={480}
      open={isDrawerOpen}
      onClose={closeDrawer}
      extra={
        <Button size="small" onClick={resetExecution} disabled={isExecuting}>
          重置
        </Button>
      }
    >
      <div style={{ marginBottom: '16px' }}>
        <Input.TextArea
          rows={4}
          value={debugInput}
          onChange={(e) => setDebugInput(e.target.value)}
          placeholder="输入测试文本..."
          disabled={isExecuting}
        />
        <Button
          type="primary"
          onClick={handleExecute}
          loading={isExecuting}
          style={{ marginTop: '8px', width: '100%' }}
        >
          {isExecuting ? '执行中...' : '开始调试'}
        </Button>
      </div>

      {stepResults.length > 0 && (
        <Timeline
          items={stepResults.map((step) => ({
            color: statusColorMap[step.status] || 'gray',
            children: (
              <div>
                <div style={{ fontWeight: 'bold' }}>
                  {step.nodeName} ({step.nodeType})
                </div>
                <div style={{ fontSize: '12px', color: '#666' }}>
                  状态: {step.status === 'running' ? '运行中' : step.status === 'success' ? '成功' : step.status === 'error' ? '错误' : '等待'}
                  {step.durationMs && ` · ${step.durationMs}ms`}
                </div>
                {step.output && (
                  <div style={{ fontSize: '11px', marginTop: '4px', background: '#f5f5f5', padding: '4px 8px', borderRadius: '4px' }}>
                    {typeof step.output === 'object' ? JSON.stringify(step.output, null, 2).substring(0, 200) : String(step.output).substring(0, 200)}
                  </div>
                )}
                {step.error && (
                  <div style={{ fontSize: '11px', color: '#ff4d4f', marginTop: '4px' }}>
                    错误: {step.error}
                  </div>
                )}
              </div>
            ),
          }))}
        />
      )}

      {audioUrl && (
        <div style={{ marginTop: '16px', padding: '12px', background: '#f6f6f6', borderRadius: '8px' }}>
          <div style={{ fontWeight: 'bold', marginBottom: '8px' }}>🎧 生成音频</div>
          <audio controls style={{ width: '100%' }} src={audioUrl}>
            您的浏览器不支持音频播放。
          </audio>
          <div style={{ fontSize: '11px', color: '#666', marginTop: '4px' }}>
            音频 URL: {audioUrl}
          </div>
        </div>
      )}
    </Drawer>
  );
};
