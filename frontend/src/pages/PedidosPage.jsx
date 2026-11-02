import { useCallback, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import { Alerta, Cabecalho, Carregando, StatusPedido, Vazio } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { useAuth } from '../context/AuthContext.jsx'
import { descreverAutomovel, formatarData, formatarMoeda, STATUS_PEDIDO } from '../formatos.js'

export default function PedidosPage() {
  const { isAgente, isCliente } = useAuth()
  const [filtro, setFiltro] = useState('')
  const buscar = useCallback(() => api.get(`/pedidos${filtro ? `?status=${filtro}` : ''}`), [filtro])
  const { dados: pedidos, erro, carregando } = useCarregar(buscar)

  return (
    <>
      <Cabecalho
        titulo={isAgente ? 'Pedidos de aluguel' : 'Meus pedidos'}
        descricao={isAgente ? 'Analise os pedidos e execute os contratos aprovados.' : 'Acompanhe o status dos seus pedidos de aluguel.'}
      >
        {isCliente && <Link className="botao botao-primario" to="/pedidos/novo">Novo pedido</Link>}
      </Cabecalho>

      {isAgente && (
        <label className="filtro">
          Status
          <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
            <option value="">Todos</option>
            {Object.entries(STATUS_PEDIDO).map(([valor, rotulo]) => <option key={valor} value={valor}>{rotulo}</option>)}
          </select>
        </label>
      )}

      <Alerta>{erro}</Alerta>
      {carregando && !pedidos && <Carregando />}
      {pedidos?.length === 0 && (
        <Vazio titulo="Nenhum pedido encontrado">
          {isCliente && <Link to="/pedidos/novo">Crie seu primeiro pedido de aluguel.</Link>}
        </Vazio>
      )}
      {pedidos?.length > 0 && (
        <div className="tabela-rolagem cartao">
          <table>
            <thead>
              <tr>
                <th>Nº</th>
                {isAgente && <th>Cliente</th>}
                <th>Automóvel</th>
                <th>Período</th>
                <th className="num">Valor</th>
                <th>Status</th>
                <th><span className="sr-only">Ações</span></th>
              </tr>
            </thead>
            <tbody>
              {pedidos.map((p) => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  {isAgente && <td>{p.clienteNome}</td>}
                  <td>{descreverAutomovel(p.automovel)}</td>
                  <td className="nowrap">{formatarData(p.dataInicio)} a {formatarData(p.dataFim)}</td>
                  <td className="num nowrap">{formatarMoeda(p.valorProposto)}</td>
                  <td><StatusPedido status={p.status} /></td>
                  <td className="acoes-linha"><Link to={`/pedidos/${p.id}`}>Ver detalhes</Link></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
