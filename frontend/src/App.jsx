import { Navigate, Route, Routes } from 'react-router-dom'
import CadastroPage from './pages/CadastroPage.jsx'
import LoginPage from './pages/LoginPage.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/cadastro" element={<CadastroPage />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  )
}
