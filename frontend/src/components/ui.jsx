import { STATUS_PEDIDO } from '../formatos.js'

export function Campo({ rotulo, erro, dica, children }) {
  return (
    <label className="campo">
      <span className="campo-rotulo">{rotulo}</span>
      {children}
      {dica && !erro && <span className="campo-dica">{dica}</span>}
      {erro && <span className="campo-erro">{erro}</span>}
    </label>
  )
}

export function Alerta({ tipo = 'erro', children }) {
  if (!children) return null
  return <div className={`alerta alerta-${tipo}`} role={tipo === 'erro' ? 'alert' : 'status'}>{children}</div>
}

export function StatusPedido({ status }) {
  return <span className={`status status-${status.toLowerCase()}`}>{STATUS_PEDIDO[status]}</span>
}

export function Carregando() {
  return <p className="vazio" role="status">Carregando…</p>
}

export function Vazio({ titulo, children }) {
  return (
    <div className="vazio">
      <strong>{titulo}</strong>
      {children && <div>{children}</div>}
    </div>
  )
}

export function Cabecalho({ titulo, descricao, children }) {
  return (
    <header className="pagina-cabecalho">
      <div>
        <h1>{titulo}</h1>
        {descricao && <p>{descricao}</p>}
      </div>
      {children && <div className="acoes">{children}</div>}
    </header>
  )
}
