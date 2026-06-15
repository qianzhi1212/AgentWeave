import { Node, Edge } from '@xyflow/react';

export function validateWorkflow(nodes: Node[], edges: Edge[]): string[] {
  const errors: string[] = [];

  const inputNodes = nodes.filter(n => n.type === 'userInput');
  const endNodes = nodes.filter(n => n.type === 'end');

  if (inputNodes.length === 0) {
    errors.push('工作流必须包含至少一个用户输入节点');
  }
  if (endNodes.length === 0) {
    errors.push('工作流必须包含至少一个结束节点');
  }

  // Check connectivity: from any input node to any end node
  if (inputNodes.length > 0 && endNodes.length > 0 && edges.length > 0) {
    const adj: Record<string, string[]> = {};
    for (const node of nodes) {
      adj[node.id] = [];
    }
    for (const edge of edges) {
      if (adj[edge.source]) {
        adj[edge.source].push(edge.target);
      }
    }

    // BFS from each input node
    const reachable = new Set<string>();
    for (const inputNode of inputNodes) {
      const visited = new Set<string>();
      const queue = [inputNode.id];
      while (queue.length > 0) {
        const current = queue.shift()!;
        if (visited.has(current)) continue;
        visited.add(current);
        reachable.add(current);
        for (const next of adj[current] || []) {
          if (!visited.has(next)) queue.push(next);
        }
      }
    }

    for (const endNode of endNodes) {
      if (!reachable.has(endNode.id)) {
        errors.push(`结束节点 "${endNode.data.label}" 无法从输入节点到达`);
      }
    }
  }

  // Check LLM nodes have API key (if configured)
  const llmNodes = nodes.filter(n => n.type === 'llm');
  for (const llmNode of llmNodes) {
    const data = llmNode.data as any;
    if (!data.apiKey && !data.providerId) {
      errors.push(`大模型节点 "${data.label}" 未配置 API Key 或供应商`);
    }
  }

  return errors;
}
