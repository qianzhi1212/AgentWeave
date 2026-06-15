import apiClient from './apiClient';
import { ExecutionRequest, ExecutionResponse } from '../types/api';

export const executionService = {
  execute: (request: ExecutionRequest) =>
    apiClient.post<ExecutionResponse>('/executions', request),

  debug: (request: ExecutionRequest) =>
    apiClient.post<ExecutionResponse>('/executions/debug', request),

  get: (executionId: number) =>
    apiClient.get<ExecutionResponse>(`/executions/${executionId}`),
};
