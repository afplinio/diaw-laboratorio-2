import { useCallback } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client.js'
import { Alerta, Cabecalho, Carregando, Vazio } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { descreverAutomovel, formatarData, formatarMoeda, TIPO_PROPRIETARIO } from '../formatos.js'

export default function ContratosPage() {
  const buscar = useCallback(() => api.get('/contratos'), [])
  const { dados: contratos, erro, carregando } = useCarregar(buscar)

  return (
    <>
      <Cabecalho titulo="Contratos" descricao="Contratos de aluguel e de crédito em que você está envolvido." />
      <Alerta>{erro}</Alerta>
      {carregando && <Carregando />}
      {contratos?.length === 0 && <Vazio titulo="Nenhum contrato encontrado" />}
      {contratos?.length > 0 && (
        <div className="tabela-rolagem cartao">
          <table>
            <thead>
              <tr><th>Nº</th><th>Pedido</th><th>Cliente</th><th>Automóvel</th><th>Agente</th><th>Proprietário</th><th>Crédito</th><th>Assinatura</th></tr>
            </thead>
            <tbody>
              {contratos.map((c) => (
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td><Link to={`/pedidos/${c.pedidoId}`}>nº {c.pedidoId}</Link></td>
                  <td>{c.clienteNome}</td>
                  <td>{descreverAutomovel(c.automovel)}</td>
                  <td>{c.agenteNome}</td>
                  <td>{c.proprietarioNome} <span className="etiqueta">{TIPO_PROPRIETARIO[c.tipoProprietario]}</span></td>
                  <td>
                    {c.credito
                      ? `${c.credito.bancoNome}: ${formatarMoeda(c.credito.valor)} em ${c.credito.parcelas}x a ${Number(c.credito.taxaJurosMensal).toLocaleString('pt-BR')}% a.m.`
                      : 'Sem crédito'}
                  </td>
                  <td className="nowrap">{formatarData(c.assinadoEm)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
