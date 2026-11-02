import { useCallback, useEffect, useRef, useState } from 'react'

/** Carrega dados ao abrir a página e expõe uma função para recarregar. */
export function useCarregar(buscar) {
  const [dados, setDados] = useState(null)
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(true)
  const ultimaRequisicao = useRef(0)

  const recarregar = useCallback(async () => {
    // Somente a requisição mais recente pode atualizar a tela.
    const requisicao = ++ultimaRequisicao.current
    setCarregando(true)
    setErro('')
    try {
      const resultado = await buscar()
      if (requisicao === ultimaRequisicao.current) setDados(resultado)
    } catch (e) {
      if (requisicao === ultimaRequisicao.current) setErro(e.message)
    } finally {
      if (requisicao === ultimaRequisicao.current) setCarregando(false)
    }
  }, [buscar])

  useEffect(() => {
    recarregar()
  }, [recarregar])

  return { dados, erro, carregando, recarregar }
}
