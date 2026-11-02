import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { Alerta, Campo } from '../components/ui.jsx'
import { useAuth } from '../context/AuthContext.jsx'

export default function LoginPage() {
  const { entrar } = useAuth()
  const navigate = useNavigate()
  const { state } = useLocation()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  const enviar = async (evento) => {
    evento.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await entrar(login, senha)
      navigate('/pedidos', { replace: true })
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <main className="acesso">
      <form className="cartao acesso-cartao" onSubmit={enviar}>
        <div className="acesso-marca">
          <img src="/logo.svg" alt="" width="44" height="44" />
          <h1>AlugaCar</h1>
          <p>Entre para gerenciar seus pedidos de aluguel.</p>
        </div>
        <Alerta tipo="sucesso">{state?.mensagem}</Alerta>
        <Alerta>{erro}</Alerta>
        <Campo rotulo="Usuário">
          <input value={login} onChange={(e) => setLogin(e.target.value)} autoComplete="username" autoFocus required />
        </Campo>
        <Campo rotulo="Senha">
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} autoComplete="current-password" required />
        </Campo>
        <button className="botao botao-primario botao-largo" disabled={enviando}>
          {enviando ? 'Entrando…' : 'Entrar'}
        </button>
        <p className="acesso-rodape">Ainda não tem acesso? <Link to="/cadastro">Cadastre-se</Link></p>
      </form>
    </main>
  )
}
