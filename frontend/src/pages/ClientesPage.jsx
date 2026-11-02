import { useCallback } from 'react'
import { api } from '../api/client.js'
import { Alerta, Cabecalho, Carregando, Vazio } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { formatarCpf, formatarMoeda } from '../formatos.js'

export default function ClientesPage() {
  const buscar = useCallback(() => api.get('/clientes'), [])
  const { dados: clientes, erro, carregando } = useCarregar(buscar)

  return (
    <>
      <Cabecalho titulo="Clientes" descricao="Dados de identificação e rendimentos para a análise financeira." />
      <Alerta>{erro}</Alerta>
      {carregando && <Carregando />}
      {clientes?.length === 0 && <Vazio titulo="Nenhum cliente cadastrado" />}
      {clientes?.length > 0 && (
        <div className="tabela-rolagem cartao">
          <table>
            <thead>
              <tr><th>Nome</th><th>CPF</th><th>RG</th><th>Endereço</th><th>Profissão</th><th>Empregadores</th><th className="num">Renda total</th></tr>
            </thead>
            <tbody>
              {clientes.map((c) => (
                <tr key={c.id}>
                  <td><strong>{c.nome}</strong></td>
                  <td className="nowrap">{formatarCpf(c.cpf)}</td>
                  <td>{c.rg}</td>
                  <td>{c.endereco}</td>
                  <td>{c.profissao}</td>
                  <td>{c.rendimentos.map((r) => r.entidadeEmpregadora).join(', ') || '—'}</td>
                  <td className="num nowrap">{formatarMoeda(c.rendaTotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
