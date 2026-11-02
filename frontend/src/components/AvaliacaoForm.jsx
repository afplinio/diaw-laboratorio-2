import { useState } from 'react'
import { api } from '../api/client.js'
import { Alerta, Campo } from './ui.jsx'

export default function AvaliacaoForm({ pedidoId, aoConcluir }) {
  const [parecer, setParecer] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  const avaliar = async (aprovado) => {
    if (!parecer.trim()) {
      setErro('Informe o parecer financeiro.')
      return
    }
    setErro('')
    setEnviando(true)
    try {
      await api.post(`/pedidos/${pedidoId}/avaliacao`, { aprovado, parecer })
      await aoConcluir()
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <section className="cartao formulario">
      <h2>Análise financeira</h2>
      <Alerta>{erro}</Alerta>
      <Campo rotulo="Parecer financeiro">
        <textarea rows={3} maxLength={500} value={parecer} onChange={(e) => setParecer(e.target.value)} />
      </Campo>
      <div className="acoes acoes-formulario">
        <button type="button" className="botao botao-primario" disabled={enviando} onClick={() => avaliar(true)}>Aprovar</button>
        <button type="button" className="botao botao-perigo" disabled={enviando} onClick={() => avaliar(false)}>Reprovar</button>
      </div>
    </section>
  )
}
