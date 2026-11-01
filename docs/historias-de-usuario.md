# Histórias de Usuário

Atores: **Cliente** (usuário individual) e **Agente** (empresa ou banco).

## Acesso

### US01: Cadastro prévio
**Como** cliente ou agente, **quero** me cadastrar com usuário e senha, **para** poder utilizar o sistema.

- O cliente informa RG, CPF, nome, endereço, profissão e até 3 entidades empregadoras com seus rendimentos.
- O agente informa nome, CNPJ e tipo (empresa ou banco).
- Usuário, CPF, RG e CNPJ não podem se repetir, e CPF e CNPJ precisam ser válidos.

### US02: Login
**Como** usuário cadastrado, **quero** entrar com usuário e senha, **para** acessar as funções do meu perfil.

- Nenhuma função é acessível sem login, exceto o próprio cadastro.
- Credenciais inválidas são recusadas sem indicar qual dado está errado.
- Senhas são armazenadas apenas como hash.

## Cliente

### US03: Manter dados pessoais
**Como** cliente, **quero** consultar, alterar e excluir meu cadastro, **para** manter meus dados corretos.

- Um cliente não acessa os dados de outro cliente.
- O limite de 3 rendimentos vale também na alteração.
- Não é possível excluir um cliente que tenha pedidos ou automóveis registrados.

### US04: Introduzir pedido
**Como** cliente, **quero** criar um pedido de aluguel, **para** solicitar um automóvel em um período.

- O pedido informa automóvel, início, término e valor proposto.
- O pedido criado fica com o status **Em análise**.
- O período não pode estar no passado nem coincidir com um contrato existente do mesmo automóvel.

### US05: Consultar pedidos
**Como** cliente, **quero** visualizar meus pedidos e seus status, **para** acompanhar a análise.

- O cliente vê somente os próprios pedidos, com o parecer financeiro quando houver.

### US06: Modificar pedido
**Como** cliente, **quero** modificar um pedido, **para** corrigir seus dados antes da avaliação.

- O cliente só modifica pedidos **Em análise**.

### US07: Cancelar pedido
**Como** cliente, **quero** cancelar um pedido, **para** desistir do aluguel.

- É possível cancelar pedidos **Em análise** ou **Aprovados**, mas não pedidos já contratados.

## Agente

### US08: Consultar clientes e pedidos
**Como** agente, **quero** consultar os pedidos e os dados financeiros dos clientes, **para** embasar minha análise.

- Os pedidos podem ser filtrados por status.
- A renda total do cliente é exibida no pedido.

### US09: Modificar pedido
**Como** agente, **quero** modificar um pedido, **para** ajustar suas condições.

- O agente modifica pedidos **Em análise** ou **Aprovados**.

### US10: Avaliar pedido
**Como** agente, **quero** registrar um parecer financeiro, **para** aprovar ou reprovar o pedido.

- Somente pedidos **Em análise** são avaliados, e o parecer é obrigatório.
- O sistema registra quem avaliou e quando.

### US11: Executar contrato
**Como** agente, **quero** executar o contrato de um pedido com parecer positivo, **para** formalizar o aluguel.

- Somente pedidos **Aprovados** viram contrato, e o status muda para **Contratado**.
- O contrato define se o automóvel será propriedade do cliente, da empresa ou do banco.
- Um mesmo automóvel não pode ter dois contratos no mesmo período.

### US12: Conceder crédito
**Como** agente, **quero** associar um contrato de crédito ao aluguel, **para** financiar a operação por um banco agente.

- O crédito é opcional, deve ser concedido por um banco e não pode superar o valor do pedido.
- O automóvel só pode ser propriedade do banco quando houver crédito desse banco.

### US13: Manter automóveis
**Como** agente, **quero** cadastrar, alterar e excluir automóveis, **para** disponibilizá-los aos clientes.

- O sistema registra matrícula, ano, marca, modelo e placa.
- Somente o proprietário altera ou exclui o automóvel.
- Automóveis com pedidos registrados não podem ser excluídos.

## Todos

### US14: Consultar contratos
**Como** cliente ou agente, **quero** consultar os contratos em que estou envolvido, **para** acompanhar os aluguéis e créditos.
