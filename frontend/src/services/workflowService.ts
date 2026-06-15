import apiClient from './apiClient';
import { WorkflowPayload, SerializedNode, SerializedEdge } from '../types/workflow';

export const workflowService = {
  // Workflow CRUD
  create: (name: string, description?: string) =>
    apiClient.post<WorkflowPayload>('/workflows', { name, description }),

  get: (id: number) =>
    apiClient.get<WorkflowPayload>(`/workflows/${id}`),

  list: () =>
    apiClient.get<WorkflowPayload[]>('/workflows'),

  update: (id: number, name: string, description?: string) =>
    apiClient.put<WorkflowPayload>(`/workflows/${id}`, { name, description }),

  delete: (id: number) =>
    apiClient.delete(`/workflows/${id}`),

  // Node operations
  addNode: (workflowId: number, payload: {
    nodeKey: string; name: string; nodeType: string;
    config?: string; positionX?: number; positionY?: number;
  }) => apiClient.post<SerializedNode>(`/workflows/${workflowId}/nodes`, payload),

  deleteNode: (workflowId: number, nodeId: number) =>
    apiClient.delete(`/workflows/${workflowId}/nodes/${nodeId}`),

  // Edge operations
  addEdge: (workflowId: number, payload: {
    sourceNodeId: number; targetNodeId: number;
    sourcePort?: string; targetPort?: string;
  }) => apiClient.post<SerializedEdge>(`/workflows/${workflowId}/edges`, payload),

  deleteEdge: (workflowId: number, edgeId: number) =>
    apiClient.delete(`/workflows/${workflowId}/edges/${edgeId}`),
};
