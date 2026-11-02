const BASE_URL = import.meta.env.VITE_API_URL ?? ''
const CHAVE_TOKEN = 'alugacar.token'

let aoExpirarSessao = () => {}

export class ApiError extends Error {
  constructor(status, mensagem, campos = {}) {
    super(mensagem)
    this.status = status
    this.campos = campos
  }
}

export function lerToken() {
  try {
    return localStorage.getItem(CHAVE_TOKEN)
  } catch {
    return null
  }
}

export function salvarToken(token) {
  try {
    if (token) localStorage.setItem(CHAVE_TOKEN, token)
    else localStorage.removeItem(CHAVE_TOKEN)
  } catch {
    // Sem armazenamento disponível, a sessão dura até a página ser recarregada.
  }
}

export function definirAoExpirarSessao(callback) {
  aoExpirarSessao = callback
}

async function requisicao(metodo, caminho, corpo) {
  const token = lerToken()
  let resposta
  try {
    resposta = await fetch(`${BASE_URL}/api${caminho}`, {
      method: metodo,
      headers: {
        ...(corpo !== undefined && { 'Content-Type': 'application/json' }),
        ...(token && { Authorization: `Bearer ${token}` }),
      },
      body: corpo === undefined ? undefined : JSON.stringify(corpo),
    })
  } catch {
    throw new ApiError(0, 'Não foi possível conectar ao servidor. Tente novamente.')
  }

  const texto = await resposta.text()
  let dados = null
  if (texto) {
    try {
      dados = JSON.parse(texto)
    } catch {
      dados = null
    }
  }
  if (resposta.ok) return dados

  if (resposta.status === 401 && token) aoExpirarSessao()
  throw new ApiError(resposta.status, dados?.mensagem ?? 'Ocorreu um erro inesperado.', dados?.campos ?? {})
}

export const api = {
  get: (caminho) => requisicao('GET', caminho),
  post: (caminho, corpo) => requisicao('POST', caminho, corpo),
  put: (caminho, corpo) => requisicao('PUT', caminho, corpo),
  delete: (caminho) => requisicao('DELETE', caminho),
}
