import { useEffect, useState } from 'react'
import { api } from '../api/client.js'
import { useAuth } from '../context/AuthContext.jsx'
import { Alerta, Campo } from './ui.jsx'

export default function ContratoForm({ pedido, aoConcluir }) {
  const { usuario } = useAuth()
  const [tipoProprietario, setTipoProprietario] = useState('CLIENTE')
  const [comCredito, setComCredito] = useState(false)
  const [credito, setCredito] = useState({ bancoId: '', valor: pedido.valorProposto, taxaJurosMensal: '', parcelas: '' })
  const [bancos, setBancos] = useState([])
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    api.get('/agentes/bancos').then(setBancos).catch((e) => setErro(e.message))
  }, [])

  const usarCredito = comCredito || tipoProprietario === 'BANCO'

  const campo = (nome) => ({
    value: credito[nome],
    onChange: (e) => setCredito({ ...credito, [nome]: e.target.value }),
    required: true,
  })

  const executar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setErros({})
    setEnviando(true)
    try {
      await api.post(`/pedidos/${pedido.id}/contrato`, {
        tipoProprietario,
        credito: usarCredito
          ? {
              bancoId: Number(credito.bancoId),
              valor: Number(credito.valor),
              taxaJurosMensal: Number(credito.taxaJurosMensal),
              parcelas: Number(credito.parcelas),
            }
          : null,
      })
      await aoConcluir()
    } catch (e) {
      setErro(e.message)
      setErros(e.campos ?? {})
      setEnviando(false)
    }
  }

  return (
    <form className="cartao formulario" onSubmit={executar}>
      <h2>Execução do contrato</h2>
      <Alerta>{erro}</Alerta>
      <Campo rotulo="Registrar o automóvel como propriedade de" erro={erros.tipoProprietario}>
        <select value={tipoProprietario} onChange={(e) => setTipoProprietario(e.target.value)}>
          <option value="CLIENTE">Cliente ({pedido.clienteNome})</option>
          {usuario.tipoAgente === 'EMPRESA' && <option value="EMPRESA">Empresa ({usuario.nome})</option>}
          <option value="BANCO">Banco (concedente do crédito)</option>
        </select>
      </Campo>
      <label className="caixa">
        <input
          type="checkbox"
          checked={usarCredito}
          disabled={tipoProprietario === 'BANCO'}
          onChange={(e) => setComCredito(e.target.checked)}
        />
        Associar contrato de crédito
      </label>
      {usarCredito && (
        <fieldset className="grupo">
          <legend>Contrato de crédito</legend>
          <Campo rotulo="Banco" erro={erros['credito.bancoId']}>
            <select {...campo('bancoId')}>
              <option value="">Selecione…</option>
              {bancos.map((b) => <option key={b.id} value={b.id}>{b.nome}</option>)}
            </select>
          </Campo>
          <div className="grade">
            <Campo rotulo="Valor (R$)" erro={erros['credito.valor']}>
              <input type="number" min="0.01" step="0.01" {...campo('valor')} />
            </Campo>
            <Campo rotulo="Juros ao mês (%)" erro={erros['credito.taxaJurosMensal']}>
              <input type="number" min="0" step="0.01" {...campo('taxaJurosMensal')} />
            </Campo>
            <Campo rotulo="Parcelas" erro={erros['credito.parcelas']}>
              <input type="number" min="1" max="120" step="1" {...campo('parcelas')} />
            </Campo>
          </div>
        </fieldset>
      )}
      <div className="acoes acoes-formulario">
        <button className="botao botao-primario" disabled={enviando}>{enviando ? 'Executando…' : 'Executar contrato'}</button>
      </div>
    </form>
  )
}
