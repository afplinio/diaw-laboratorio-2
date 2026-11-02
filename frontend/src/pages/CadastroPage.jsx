import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api/client.js'
import RendimentosInput from '../components/RendimentosInput.jsx'
import { Alerta, Campo } from '../components/ui.jsx'

const CLIENTE_INICIAL = { rg: '', cpf: '', nome: '', endereco: '', profissao: '' }
const AGENTE_INICIAL = { nome: '', cnpj: '', tipo: 'EMPRESA' }

export default function CadastroPage() {
  const navigate = useNavigate()
  const [perfil, setPerfil] = useState('CLIENTE')
  const [conta, setConta] = useState({ login: '', senha: '' })
  const [cliente, setCliente] = useState(CLIENTE_INICIAL)
  const [agente, setAgente] = useState(AGENTE_INICIAL)
  const [rendimentos, setRendimentos] = useState([])
  const [erro, setErro] = useState('')
  const [erros, setErros] = useState({})
  const [enviando, setEnviando] = useState(false)

  const enviar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setErros({})
    setEnviando(true)
    try {
      if (perfil === 'CLIENTE') {
        await api.post('/clientes', { ...conta, ...cliente, rendimentos })
      } else {
        await api.post('/agentes', { ...conta, ...agente })
      }
      navigate('/login', { replace: true, state: { mensagem: 'Cadastro concluído. Faça login para continuar.' } })
    } catch (e) {
      setErro(e.message)
      setErros(e.campos ?? {})
      setEnviando(false)
    }
  }

  const campoCliente = (campo) => ({
    value: cliente[campo],
    onChange: (e) => setCliente({ ...cliente, [campo]: e.target.value }),
    required: true,
  })

  return (
    <main className="acesso">
      <form className="cartao acesso-cartao acesso-largo" onSubmit={enviar}>
        <div className="acesso-marca">
          <img src="/logo.svg" alt="" width="44" height="44" />
          <h1>Criar cadastro</h1>
        </div>
        <div className="seletor" role="radiogroup" aria-label="Tipo de cadastro">
          {[['CLIENTE', 'Sou cliente'], ['AGENTE', 'Sou agente']].map(([valor, rotulo]) => (
            <label key={valor} className={perfil === valor ? 'ativo' : ''}>
              <input type="radio" name="perfil" checked={perfil === valor} onChange={() => setPerfil(valor)} />
              {rotulo}
            </label>
          ))}
        </div>
        <Alerta>{erro}</Alerta>
        <div className="grade">
          <Campo rotulo="Usuário" erro={erros.login}>
            <input value={conta.login} onChange={(e) => setConta({ ...conta, login: e.target.value })} autoComplete="username" required />
          </Campo>
          <Campo rotulo="Senha" erro={erros.senha} dica="Mínimo de 8 caracteres.">
            <input type="password" value={conta.senha} onChange={(e) => setConta({ ...conta, senha: e.target.value })} autoComplete="new-password" minLength={8} maxLength={72} required />
          </Campo>
        </div>

        {perfil === 'CLIENTE' ? (
          <>
            <Campo rotulo="Nome completo" erro={erros.nome}><input {...campoCliente('nome')} maxLength={120} /></Campo>
            <div className="grade">
              <Campo rotulo="RG" erro={erros.rg}><input {...campoCliente('rg')} maxLength={20} /></Campo>
              <Campo rotulo="CPF" erro={erros.cpf}><input {...campoCliente('cpf')} inputMode="numeric" /></Campo>
            </div>
            <Campo rotulo="Endereço" erro={erros.endereco}><input {...campoCliente('endereco')} maxLength={255} /></Campo>
            <Campo rotulo="Profissão" erro={erros.profissao}><input {...campoCliente('profissao')} maxLength={80} /></Campo>
            <RendimentosInput rendimentos={rendimentos} onChange={setRendimentos} erros={erros} />
          </>
        ) : (
          <>
            <Campo rotulo="Nome da instituição" erro={erros.nome}>
              <input value={agente.nome} onChange={(e) => setAgente({ ...agente, nome: e.target.value })} maxLength={120} required />
            </Campo>
            <div className="grade">
              <Campo rotulo="CNPJ" erro={erros.cnpj}>
                <input value={agente.cnpj} onChange={(e) => setAgente({ ...agente, cnpj: e.target.value })} inputMode="numeric" required />
              </Campo>
              <Campo rotulo="Tipo" erro={erros.tipo}>
                <select value={agente.tipo} onChange={(e) => setAgente({ ...agente, tipo: e.target.value })}>
                  <option value="EMPRESA">Empresa</option>
                  <option value="BANCO">Banco</option>
                </select>
              </Campo>
            </div>
          </>
        )}

        <button className="botao botao-primario botao-largo" disabled={enviando}>
          {enviando ? 'Cadastrando…' : 'Cadastrar'}
        </button>
        <p className="acesso-rodape">Já tem cadastro? <Link to="/login">Entrar</Link></p>
      </form>
    </main>
  )
}
