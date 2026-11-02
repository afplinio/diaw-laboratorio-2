import { Navigate, Outlet, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import { Carregando } from './components/ui.jsx'
import { useAuth } from './context/AuthContext.jsx'
import CadastroPage from './pages/CadastroPage.jsx'
import ClientesPage from './pages/ClientesPage.jsx'
import LoginPage from './pages/LoginPage.jsx'
import PedidosPage from './pages/PedidosPage.jsx'
import PerfilPage from './pages/PerfilPage.jsx'

function Protegida({ papel }) {
  const { usuario, carregando } = useAuth()
  if (carregando) return <Carregando />
  if (!usuario) return <Navigate to="/login" replace />
  if (papel && usuario.papel !== papel) return <Navigate to="/pedidos" replace />
  return <Outlet />
}

function SomenteVisitante() {
  const { usuario, carregando } = useAuth()
  if (carregando) return <Carregando />
  return usuario ? <Navigate to="/pedidos" replace /> : <Outlet />
}

export default function App() {
  return (
    <Routes>
      <Route element={<SomenteVisitante />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<CadastroPage />} />
      </Route>
      <Route element={<Protegida />}>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/pedidos" replace />} />
          <Route path="/pedidos" element={<PedidosPage />} />
          <Route element={<Protegida papel="CLIENTE" />}>
            <Route path="/perfil" element={<PerfilPage />} />
          </Route>
          <Route element={<Protegida papel="AGENTE" />}>
            <Route path="/clientes" element={<ClientesPage />} />
          </Route>
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
