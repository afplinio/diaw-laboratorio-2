import { useCallback, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import AvaliacaoForm from '../components/AvaliacaoForm.jsx'
import { Alerta, Cabecalho, Carregando, StatusPedido } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { useAuth } from '../context/AuthContext.jsx'
import { descreverAutomovel, formatarData, formatarMoeda } from '../formatos.js'

export default function PedidoDetalhePage() {
  const { id } = useParams()
  const { isAgente, isCliente } = useAuth()
  const buscar = useCallback(() => api.get(`/pedidos/${id}`), [id])
  const { dados: pedido, erro: erroCarga, carregando, recarregar } = useCarregar(buscar)
  const [erro, setErro] = useState('')

  if (carregando && !pedido) return <Carregando />
  if (erroCarga) return <Alerta>{erroCarga}</Alerta>
  if (!pedido) return null

  const emAnalise = pedido.status === 'EM_ANALISE'
  const aprovado = pedido.status === 'APROVADO'
  const podeModificar = emAnalise || (isAgente && aprovado)
  const podeCancelar = isCliente && (emAnalise || aprovado)

  const cancelar = async () => {
    if (!window.confirm('Cancelar este pedido? Esta ação não pode ser desfeita.')) return
    setErro('')
    try {
      await api.post(`/pedidos/${id}/cancelamento`)
      await recarregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <p className="migalha"><Link to="/pedidos">← Pedidos</Link></p>
      <Cabecalho titulo={`Pedido nº ${pedido.id}`}>
        {podeModificar && <Link className="botao botao-secundario" to={`/pedidos/${id}/editar`}>Modificar</Link>}
        {podeCancelar && <button type="button" className="botao botao-perigo" onClick={cancelar}>Cancelar pedido</button>}
      </Cabecalho>
      <Alerta>{erro}</Alerta>

      <section className="cartao">
        <div className="detalhe-status"><StatusPedido status={pedido.status} /></div>
        <dl className="detalhes">
          <div><dt>Automóvel</dt><dd>{descreverAutomovel(pedido.automovel)}</dd></div>
          <div><dt>Período</dt><dd>{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</dd></div>
          <div><dt>Valor proposto</dt><dd>{formatarMoeda(pedido.valorProposto)}</dd></div>
          <div><dt>Enviado em</dt><dd>{formatarData(pedido.criadoEm)}</dd></div>
          {isAgente && <div><dt>Cliente</dt><dd>{pedido.clienteNome}</dd></div>}
          {isAgente && <div><dt>Renda total do cliente</dt><dd>{formatarMoeda(pedido.rendaTotalCliente)}</dd></div>}
          {pedido.observacoes && <div className="largo"><dt>Observações</dt><dd>{pedido.observacoes}</dd></div>}
          {pedido.parecerFinanceiro && (
            <div className="largo">
              <dt>Parecer financeiro</dt>
              <dd>{pedido.parecerFinanceiro} <span className="texto-suave">— {pedido.avaliadorNome}, {formatarData(pedido.avaliadoEm)}</span></dd>
            </div>
          )}
        </dl>
      </section>

      {isAgente && emAnalise && <AvaliacaoForm pedidoId={pedido.id} aoConcluir={recarregar} />}
    </>
  )
}
