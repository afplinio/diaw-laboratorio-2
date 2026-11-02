# Modelos UML

Cada versão fica em uma pasta, com o código-fonte PlantUML (`.puml`) e a imagem gerada (`.png`).

| Diagrama | v1: modelagem inicial | v2: revisada após a implementação |
| :--- | :---: | :---: |
| Casos de uso | [Ver](v1/casos-de-uso.png) | [Ver](v2/casos-de-uso.png) |
| Classes | [Ver](v1/classes.png) | [Ver](v2/classes.png) |
| Pacotes (visão lógica) | [Ver](v1/pacotes.png) | [Ver](v2/pacotes.png) |

As histórias de usuário estão em [`../historias-de-usuario.md`](../historias-de-usuario.md).

## Modificações da v1 para a v2

As mudanças abaixo foram necessárias para o funcionamento do software.

### Casos de uso

| Modificação | Motivo |
| :--- | :--- |
| Ator **Visitante** no cadastro | O cadastro é a única função usada antes do login |
| Atores Empresa e Banco reunidos em **Agente** | Ambos têm as mesmas permissões. A diferença está nas regras do contrato |
| Novos casos: manter dados pessoais, consultar clientes, manter automóveis e consultar contratos | Necessários ao CRUD de cliente, à análise financeira e à oferta de automóveis |
| Novo caso: registrar propriedade do automóvel | Toda execução de contrato define o proprietário |

### Classes

| Modificação | Motivo |
| :--- | :--- |
| `Empresa` e `Banco` substituídos por `Agente` + `TipoAgente` | Só diferem no tipo, e a herança criaria tabelas e classes sem atributos próprios |
| Propriedade do automóvel passou a ser uma associação única com `Usuario` | Evita duas associações opcionais que poderiam se contradizer |
| `Contrato` renomeado para `ContratoAluguel`, com `tipoProprietario` | Distingue do contrato de crédito e preserva o que foi definido na assinatura |
| `valorProposto` e `observacoes` em `PedidoAluguel` | A análise financeira e o limite do crédito dependem do valor do pedido |
| `avaliadoEm`, `criadoEm` e parecer como `parecerFinanceiro` | Registram quando cada etapa ocorreu |
| `versao` em `PedidoAluguel` | Impede que alterações simultâneas se sobrescrevam |
| `senha` substituída por `senhaHash` | A senha nunca é armazenada em texto puro |
| Enums `Papel` e `TipoProprietario` e métodos de domínio | As regras ficam junto dos dados, e a autorização usa o papel do usuário |

### Pacotes

| Modificação | Motivo |
| :--- | :--- |
| Novo pacote `security` | Implementa login, JWT e a restrição de acesso |
| Novo pacote `dto` | Separa a API das entidades e evita expor dados como o hash da senha |
| Novo pacote `exception` | Padroniza as respostas de erro |
| Novos pacotes `components` e `context` no front-end | Reaproveitam componentes e compartilham o estado de login entre as páginas |

## Como gerar as imagens

```bash
docker run --rm --user "$(id -u):$(id -g)" -v "$PWD/docs/uml:/data" -w /data plantuml/plantuml -tpng v1/*.puml v2/*.puml
```
