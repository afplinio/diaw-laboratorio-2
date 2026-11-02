import { useCallback, useState } from 'react'
import { api } from '../api/client.js'
import { Alerta, Cabecalho, Campo, Carregando, Vazio } from '../components/ui.jsx'
import { useCarregar } from '../components/useCarregar.js'
import { useAuth } from '../context/AuthContext.jsx'
import { TIPO_PROPRIETARIO } from '../formatos.js'

const VAZIO = { matricula: '', ano: '', marca: '', modelo: '', placa: '' }

export default function AutomoveisPage() {
  const { usuario } = useAuth()
  const buscar = useCallback(() => api.get('/automoveis'), [])
  const { dados: automoveis, erro: erroCarga, carregando, recarregar } = useCarregar(buscar)
  const [form, setForm] = useState(null)
  const [editandoId, setEditandoId] = useState(null)
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  const abrir = (automovel) => {
    setErro('')
    setErros({})
    setEditandoId(automovel?.id ?? null)
    setForm(automovel
      ? { matricula: automovel.matricula, ano: automovel.ano, marca: automovel.marca, modelo: automovel.modelo, placa: automovel.placa }
      : VAZIO)
  }

  const salvar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setErros({})
    setEnviando(true)
    try {
      const corpo = { ...form, ano: Number(form.ano) }
      if (editandoId) await api.put(`/automoveis/${editandoId}`, corpo)
      else await api.post('/automoveis', corpo)
      setForm(null)
      await recarregar()
    } catch (e) {
      setErro(e.message)
      setErros(e.campos ?? {})
    } finally {
      setEnviando(false)
    }
  }

  const excluir = async (automovel) => {
    if (!window.confirm(`Excluir o automóvel de placa ${automovel.placa}?`)) return
    setErro('')
    try {
      await api.delete(`/automoveis/${automovel.id}`)
      await recarregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  const campo = (nome) => ({
    value: form[nome],
    onChange: (e) => setForm({ ...form, [nome]: e.target.value }),
    required: true,
  })

  return (
    <>
      <Cabecalho titulo="Automóveis" descricao="Frota disponível e proprietários registrados.">
        {!form && <button type="button" className="botao botao-primario" onClick={() => abrir(null)}>Novo automóvel</button>}
      </Cabecalho>
      <Alerta>{erroCarga}</Alerta>
      {!form && <Alerta>{erro}</Alerta>}

      {form && (
        <form className="cartao formulario" onSubmit={salvar}>
          <h2>{editandoId ? 'Editar automóvel' : 'Novo automóvel'}</h2>
          <Alerta>{erro}</Alerta>
          <div className="grade">
            <Campo rotulo="Matrícula" erro={erros.matricula}><input {...campo('matricula')} maxLength={30} /></Campo>
            <Campo rotulo="Placa" erro={erros.placa}><input {...campo('placa')} maxLength={8} /></Campo>
            <Campo rotulo="Marca" erro={erros.marca}><input {...campo('marca')} maxLength={60} /></Campo>
            <Campo rotulo="Modelo" erro={erros.modelo}><input {...campo('modelo')} maxLength={60} /></Campo>
            <Campo rotulo="Ano" erro={erros.ano}><input type="number" min="1950" {...campo('ano')} /></Campo>
          </div>
          <div className="acoes acoes-formulario">
            <button className="botao botao-primario" disabled={enviando}>{enviando ? 'Salvando…' : 'Salvar'}</button>
            <button type="button" className="botao botao-secundario" onClick={() => setForm(null)}>Cancelar</button>
          </div>
        </form>
      )}

      {carregando && !automoveis && <Carregando />}
      {automoveis?.length === 0 && <Vazio titulo="Nenhum automóvel cadastrado">Cadastre o primeiro para receber pedidos.</Vazio>}
      {automoveis?.length > 0 && (
        <div className="tabela-rolagem cartao">
          <table>
            <thead>
              <tr><th>Automóvel</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Proprietário</th><th><span className="sr-only">Ações</span></th></tr>
            </thead>
            <tbody>
              {automoveis.map((a) => (
                <tr key={a.id}>
                  <td><strong>{a.marca} {a.modelo}</strong></td>
                  <td>{a.ano}</td>
                  <td className="nowrap">{a.placa}</td>
                  <td>{a.matricula}</td>
                  <td>{a.proprietarioNome} <span className="etiqueta">{TIPO_PROPRIETARIO[a.tipoProprietario]}</span></td>
                  <td className="acoes-linha">
                    {a.proprietarioId === usuario.id && (
                      <>
                        <button type="button" className="botao botao-discreto" onClick={() => abrir(a)}>Editar</button>
                        <button type="button" className="botao botao-discreto texto-perigo" onClick={() => excluir(a)}>Excluir</button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
