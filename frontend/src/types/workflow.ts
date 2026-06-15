export interface WorkflowPayload {
  id: number;
  name: string;
  description: string;
  status: string;
  version: number;
  nodes: SerializedNode[];
  edges: SerializedEdge[];
  createdAt: string;
  updatedAt: string;
}

export interface SerializedNode {
  id: number;
  nodeKey: string;
  name: string;
  nodeType: string;
  config: string | null;
  positionX: number | null;
  positionY: number | null;
}

export interface SerializedEdge {
  id: number;
  sourceNodeId: number;
  targetNodeId: number;
  sourcePort: string | null;
  targetPort: string | null;
}
