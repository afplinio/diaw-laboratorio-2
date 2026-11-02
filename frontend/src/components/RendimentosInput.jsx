import { Campo } from './ui.jsx'

const MAX_RENDIMENTOS = 3

export const rendimentoVazio = () => ({ entidadeEmpregadora: '', valor: '' })

export default function RendimentosInput({ rendimentos, onChange, erros }) {
  const alterar = (indice, campo, valor) => {
    onChange(rendimentos.map((r, i) => (i === indice ? { ...r, [campo]: valor } : r)))
  }

  return (
    <fieldset className="grupo">
      <legend>Entidades empregadoras e rendimentos (máximo {MAX_RENDIMENTOS})</legend>
      {erros?.rendimentos && <p className="campo-erro">{erros.rendimentos}</p>}
      {rendimentos.length === 0 && <p className="campo-dica">Nenhum rendimento informado.</p>}
      {rendimentos.map((rendimento, i) => (
        <div className="linha-rendimento" key={i}>
          <Campo rotulo="Entidade empregadora" erro={erros?.[`rendimentos[${i}].entidadeEmpregadora`]}>
            <input
              value={rendimento.entidadeEmpregadora}
              onChange={(e) => alterar(i, 'entidadeEmpregadora', e.target.value)}
              maxLength={120}
              required
            />
          </Campo>
          <Campo rotulo="Rendimento mensal (R$)" erro={erros?.[`rendimentos[${i}].valor`]}>
            <input
              type="number"
              min="0.01"
              step="0.01"
              value={rendimento.valor}
              onChange={(e) => alterar(i, 'valor', e.target.value)}
              required
            />
          </Campo>
          <button
            type="button"
            className="botao botao-discreto"
            onClick={() => onChange(rendimentos.filter((_, j) => j !== i))}
          >
            Remover
          </button>
        </div>
      ))}
      <button
        type="button"
        className="botao botao-secundario"
        disabled={rendimentos.length >= MAX_RENDIMENTOS}
        onClick={() => onChange([...rendimentos, rendimentoVazio()])}
      >
        Adicionar rendimento
      </button>
    </fieldset>
  )
}
