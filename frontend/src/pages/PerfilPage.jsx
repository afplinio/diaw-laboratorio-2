import { useCallback, useEffect, useState } from 'react'
import { api } from '../api/client.js'
import RendimentosInput from '../components/RendimentosInput.jsx'
import { Alerta, Cabecalho, Campo, Carregando } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { useAuth } from '../context/AuthContext.jsx'
import { formatarMoeda } from '../formatos.js'

export default function PerfilPage() {
  const { usuario, atualizarNome, sair } = useAuth()
  const buscar = useCallback(() => api.get(`/clientes/${usuario.id}`), [usuario.id])
  const { dados, erro: erroCarga, carregando } = useCarregar(buscar)
  const [form, setForm] = useState(null)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [sucesso, setSucesso] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (dados) setForm({ ...dados, novaSenha: '' })
  }, [dados])

  if (carregando && !form) return <Carregando />
  if (erroCarga) return <Alerta>{erroCarga}</Alerta>
  if (!form) return null

  const campo = (nome) => ({
    value: form[nome],
    onChange: (e) => setForm({ ...form, [nome]: e.target.value }),
  })

  const salvar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setErros({})
    setSucesso('')
    setEnviando(true)
    try {
      const atualizado = await api.put(`/clientes/${usuario.id}`, {
        novaSenha: form.novaSenha || null,
        rg: form.rg,
        cpf: form.cpf,
        nome: form.nome,
        endereco: form.endereco,
        profissao: form.profissao,
        rendimentos: form.rendimentos,
      })
      setForm({ ...atualizado, novaSenha: '' })
      atualizarNome(atualizado.nome)
      setSucesso('Dados atualizados.')
    } catch (e) {
      setErro(e.message)
      setErros(e.campos ?? {})
    } finally {
      setEnviando(false)
    }
  }

  const excluir = async () => {
    if (!window.confirm('Excluir sua conta permanentemente? Esta ação não pode ser desfeita.')) return
    setErro('')
    setSucesso('')
    try {
      await api.delete(`/clientes/${usuario.id}`)
      sair()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <Cabecalho titulo="Meu perfil" descricao={`Usuário: ${form.login} · Renda total: ${formatarMoeda(form.rendaTotal)}`} />
      <form className="cartao formulario" onSubmit={salvar}>
        <Alerta>{erro}</Alerta>
        <Alerta tipo="sucesso">{sucesso}</Alerta>
        <Campo rotulo="Nome completo" erro={erros.nome}><input {...campo('nome')} maxLength={120} required /></Campo>
        <div className="grade">
          <Campo rotulo="RG" erro={erros.rg}><input {...campo('rg')} maxLength={20} required /></Campo>
          <Campo rotulo="CPF" erro={erros.cpf}><input {...campo('cpf')} inputMode="numeric" required /></Campo>
        </div>
        <Campo rotulo="Endereço" erro={erros.endereco}><input {...campo('endereco')} maxLength={255} required /></Campo>
        <Campo rotulo="Profissão" erro={erros.profissao}><input {...campo('profissao')} maxLength={80} required /></Campo>
        <RendimentosInput
          rendimentos={form.rendimentos}
          onChange={(rendimentos) => setForm({ ...form, rendimentos })}
          erros={erros}
        />
        <Campo rotulo="Nova senha" erro={erros.novaSenha} dica="Deixe em branco para manter a senha atual.">
          <input type="password" {...campo('novaSenha')} autoComplete="new-password" minLength={8} maxLength={72} />
        </Campo>
        <div className="acoes acoes-formulario">
          <button className="botao botao-primario" disabled={enviando}>{enviando ? 'Salvando…' : 'Salvar alterações'}</button>
          <button type="button" className="botao botao-perigo" onClick={excluir}>Excluir conta</button>
        </div>
      </form>
    </>
  )
}
