import { Outlet } from 'react-router-dom';

function App() {
  return (
    <div className="app-container">
      {/* Outlet is where the child routes (Login, Dashboard) will appear */}
      <Outlet />
    </div>
  );
}

export default App;