import { ReactFlowProvider } from '@xyflow/react';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AppLayout } from './components/Layout/AppLayout';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <ReactFlowProvider>
        <AppLayout />
      </ReactFlowProvider>
    </ConfigProvider>
  );
}

export default App;
