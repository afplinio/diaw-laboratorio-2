import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import { Alerta, Cabecalho, Campo, Carregando, Vazio } from '../components/ui.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { descreverAutomovel, hojeIso } from '../formatos.js'

const VAZIO = { automovelId: '', dataInicio: '', dataFim: '', valorProposto: '', observacoes: '' }

export default function PedidoFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { isCliente } = useAuth()
  const [form, setForm] = useState(VAZIO)
  const [automoveis, setAutomoveis] = useState(null)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    let ativo = true
    Promise.all([
      api.get('/automoveis?disponiveis=true'),
      id ? api.get(`/pedidos/${id}`) : Promise.resolve(null),
    ]).then(([lista, pedido]) => {
      if (!ativo) return
      setAutomoveis(lista)
      if (pedido) {
        setForm({
          automovelId: pedido.automovel.id,
          dataInicio: pedido.dataInicio,
          dataFim: pedido.dataFim,
          valorProposto: pedido.valorProposto,
          observacoes: pedido.observacoes ?? '',
        })
      }
    }).catch((e) => {
      if (ativo) {
        setErro(e.message)
        setAutomoveis([])
      }
    })
    return () => { ativo = false }
  }, [id])

  const voltar = id ? `/pedidos/${id}` : '/pedidos'

  const salvar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setErros({})
    setEnviando(true)
    try {
      const corpo = { ...form, automovelId: Number(form.automovelId), valorProposto: Number(form.valorProposto) }
      const salvo = id ? await api.put(`/pedidos/${id}`, corpo) : await api.post('/pedidos', corpo)
      navigate(`/pedidos/${salvo.id}`, { replace: true })
    } catch (e) {
      setErro(e.message)
      setErros(e.campos ?? {})
      setEnviando(false)
    }
  }

  if (!automoveis) return <Carregando />

  const campo = (nome) => ({
    value: form[nome],
    onChange: (e) => setForm({ ...form, [nome]: e.target.value }),
  })

  if (automoveis.length === 0 && !erro) {
    return (
      <>
        <Cabecalho titulo="Novo pedido" />
        <Vazio titulo="Nenhum automóvel disponível no momento">
          <Link to="/pedidos">Voltar aos pedidos</Link>
        </Vazio>
      </>
    )
  }

  return (
    <>
      <Cabecalho
        titulo={id ? `Modificar pedido nº ${id}` : 'Novo pedido'}
        descricao={isCliente ? 'Após o envio, o pedido segue para análise financeira.' : 'Ajuste os dados do pedido.'}
      />
      <form className="cartao formulario" onSubmit={salvar}>
        <Alerta>{erro}</Alerta>
        <Campo rotulo="Automóvel" erro={erros.automovelId}>
          <select {...campo('automovelId')} required>
            <option value="">Selecione…</option>
            {automoveis.map((a) => <option key={a.id} value={a.id}>{descreverAutomovel(a)}</option>)}
          </select>
        </Campo>
        <div className="grade">
          <Campo rotulo="Início" erro={erros.dataInicio}>
            <input type="date" min={hojeIso()} {...campo('dataInicio')} required />
          </Campo>
          <Campo rotulo="Término" erro={erros.dataFim}>
            <input type="date" min={form.dataInicio || hojeIso()} {...campo('dataFim')} required />
          </Campo>
        </div>
        <Campo rotulo="Valor proposto (R$)" erro={erros.valorProposto}>
          <input type="number" min="0.01" step="0.01" {...campo('valorProposto')} required />
        </Campo>
        <Campo rotulo="Observações" erro={erros.observacoes}>
          <textarea rows={3} maxLength={500} {...campo('observacoes')} />
        </Campo>
        <div className="acoes acoes-formulario">
          <button className="botao botao-primario" disabled={enviando}>
            {enviando ? 'Salvando…' : id ? 'Salvar alterações' : 'Enviar pedido'}
          </button>
          <Link className="botao botao-secundario" to={voltar}>Cancelar</Link>
        </div>
      </form>
    </>
  )
}
