import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api, definirAoExpirarSessao, lerToken, salvarToken } from '../api/client.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(() => Boolean(lerToken()))

  const sair = useCallback(() => {
    salvarToken(null)
    setUsuario(null)
  }, [])

  useEffect(() => {
    definirAoExpirarSessao(sair)
    if (!lerToken()) return
    api.get('/auth/me')
      .then(setUsuario)
      .catch(() => {})
      .finally(() => setCarregando(false))
  }, [sair])

  const entrar = useCallback(async (login, senha) => {
    const resposta = await api.post('/auth/login', { login, senha })
    salvarToken(resposta.token)
    setUsuario(resposta.usuario)
    return resposta.usuario
  }, [])

  const atualizarNome = useCallback((nome) => {
    setUsuario((atual) => (atual ? { ...atual, nome } : atual))
  }, [])

  const valor = useMemo(() => ({
    usuario,
    carregando,
    entrar,
    sair,
    atualizarNome,
    isAgente: usuario?.papel === 'AGENTE',
    isCliente: usuario?.papel === 'CLIENTE',
  }), [usuario, carregando, entrar, sair, atualizarNome])

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
