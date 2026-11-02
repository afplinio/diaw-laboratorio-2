import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

const LINKS_CLIENTE = [
  ['/pedidos', 'Meus pedidos'],
  ['/contratos', 'Contratos'],
  ['/perfil', 'Meu perfil'],
]

const LINKS_AGENTE = [
  ['/pedidos', 'Pedidos'],
  ['/contratos', 'Contratos'],
  ['/automoveis', 'Automóveis'],
  ['/clientes', 'Clientes'],
]

export default function Layout() {
  const { usuario, isAgente, sair } = useAuth()
  const links = isAgente ? LINKS_AGENTE : LINKS_CLIENTE
  const papel = isAgente ? (usuario.tipoAgente === 'BANCO' ? 'Banco' : 'Empresa') : 'Cliente'

  return (
    <>
      <div className="topo">
        <div className="topo-interno">
          <NavLink to="/" className="marca">
            <img src="/logo.svg" alt="" width="28" height="28" />
            AlugaCar
          </NavLink>
          <nav aria-label="Principal">
            {links.map(([para, rotulo]) => (
              <NavLink key={para} to={para}>{rotulo}</NavLink>
            ))}
          </nav>
          <div className="topo-usuario">
            <span><strong>{usuario.nome}</strong> · {papel}</span>
            <button type="button" className="botao botao-discreto" onClick={sair}>Sair</button>
          </div>
        </div>
      </div>
      <main className="conteudo">
        <Outlet />
      </main>
    </>
  )
}
