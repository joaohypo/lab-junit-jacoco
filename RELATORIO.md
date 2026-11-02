# Relatório do Laboratório: JUnit 5

**Aluno:** João Guilherme de Souza Barboza (matrícula 304260), trabalho individual  
**Link do fork:** https://github.com/joaohypo/lab-junit-jacoco

## 1. Resultado final

Cole aqui o resumo que aparece no final de `java scripts/Rodar.java`:

```
Test run finished after 95 ms
[         8 containers found      ]
[         0 containers skipped    ]
[         8 containers started    ]
[         0 containers aborted    ]
[         8 containers successful ]
[         0 containers failed     ]
[        34 tests found           ]
[         0 tests skipped         ]
[        34 tests started         ]
[         0 tests aborted         ]
[        34 tests successful      ]
[         0 tests failed          ]
```

## 2. Nível 1: `if`s de `Conta`

Para cada `if` de `Conta.java`, diga qual teste faz o `if` ser **verdadeiro** e qual faz ser **falso**
(escreva o nome do método de teste).

| `if` (condição) | Teste que torna **verdadeiro** | Teste que torna **falso** |
|---|---|---|
| saldo inicial negativo (construtor) | `saldoInicialNegativoDeveVirarZero` (extra) | `contaNovaDeveGuardarTitularESaldoInicial` |
| valor do depósito `<= 0` | `depositoComValorNegativoDeveSerRecusado` | `depositoValidoDeveAumentarOSaldo` |
| valor do saque `<= 0` | `saqueComValorZeroOuNegativoDeveSerRecusado` (extra) | `saqueValidoDeveDiminuirOSaldo` |
| saque maior que o saldo | `saqueMaiorQueOSaldoDeveSerRecusado` | `saqueValidoDeveDiminuirOSaldo`, `saqueDoSaldoExatoDeveSerPermitidoEZerarAConta` |

## 3. Nível 2: regras de `ServicoTransferencia`

| Regra | Teste(s) que a exercita(m) |
|---|---|
| R1 mesma conta | `mesmaContaEValorInvalidoDevemSerRecusados` |
| R2 valor <= 0 | `mesmaContaEValorInvalidoDevemSerRecusados` (0 e -50) |
| R3 acima do limite de 5.000 (e 5.000 exatos permitido) | `transferenciaAcimaDoLimiteDeveSerRecusada` (5.000,01) e `transferenciaDeExatamente5000DeveSerAceita` (extra) |
| R4 saldo insuficiente para valor + tarifa (nada é alterado); tarifa de 1% acima de 1.000 (1.000 exatos sem tarifa) | `saldoInsuficienteParaValorMaisTarifaNaoAlteraNenhumaConta`; tarifa: `transferenciaAcimaDe1000PagaTarifaDe1Porcento` e `transferenciaDeExatamente1000NaoPagaTarifa` (1.000 e 1.000,01) |
| R5 transferência OK: saldos da origem e do destino | `transferenciaPequenaDeveSerAceitaSemTarifa`, `transferenciaAcimaDe1000PagaTarifaDe1Porcento`, `transferenciaDeExatamente1000NaoPagaTarifa`, `transferenciaDeExatamente5000DeveSerAceita` |

## 4. Nível 3: regras de `AnalisadorEmprestimo`

| Regra | Cenário escolhido (entradas → decisão esperada) | Método de teste |
|---|---|---|
| 1. entradas inválidas → DADOS_INVALIDOS (cada uma das 6 condições) | score -1 e 1001, renda 0, valor 0, parcelas 0 e 361 → DADOS_INVALIDOS. Nos limites válidos (renda 50.000, valor 10.000): score 0 → NEGADO; score 1000, 1 parcela e 360 parcelas → APROVADO | `entradasNasFronteirasDaValidacao` (parametrizado) |
| 2. score < 300 → NEGADO | renda 100.000, valor 1.000, 12x: score 299 → NEGADO; score 300 → ANALISE_MANUAL | `scoreAbaixoDe300DeveSerNegado` |
| 3. taxa por faixa de score (800 / 600) | 800 → 12%; 799 e 600 → 24%; 599 → 36% | `taxaAnualDependeDaFaixaDeScore` (parametrizado) |
| 4. parcela (Price) | 10.000 a 1% a.m. em 12x → 888,49 | `parcelaDeveSeguirATabelaPrice` |
| 5. comprometimento > 50% → NEGADO; > 30% → ANÁLISE MANUAL | score 800, 10.000 em 12x (parcela 888,49): renda 1.776,97 → NEGADO; 1.776,98 → ANALISE_MANUAL; 2.961,62 → ANALISE_MANUAL; 2.961,63 → APROVADO | `comprometimentoAcimaDe50PorcentoDeveSerNegado`, `comprometimentoAcimaDe30PorcentoVaiParaAnaliseManual` |
| 6. comprometimento <= 30%: score >= 600 aprovado; abaixo, análise manual | renda 5.000, 10.000 em 12x: score 600 → APROVADO; score 599 → ANALISE_MANUAL | `comprometimentoAte30PorcentoSoAprovaComScore600OuMais` |

## 5. Reflexão (2 a 4 linhas cada)

1. **Os dois lados do `if`:** qual `if` ficou sem teste de um dos lados no começo? Que bug passaria
   despercebido por isso?

   Dois: o do saldo inicial negativo (construtor) e o `valor <= 0` do `sacar`, que só tinham o lado
   falso testado. Se o construtor aceitasse saldo negativo, ou se `sacar(-10)` fosse aceito (o que
   aumentaria o saldo), nenhum teste ia falhar. Por isso criei os dois testes extras no `ContaTest`.

2. **Valor-limite:** qual teste de fronteira (ex.: 1.000,00 / 5.000,00 / score 600) vocês acham
   mais importante? Que bug ele pegaria se o código usasse `>=` no lugar de `>`?

   O de 1.000,00 exatos (`transferenciaDeExatamente1000NaoPagaTarifa`). Com `valor >= FAIXA_ISENTA`,
   quem transfere 1.000 pagaria R$ 10 de tarifa indevida e os testes com 200 e 2.000 continuariam
   verdes: só o teste na fronteira pega. Com 5.000,00 é igual (com `>=` a transferência seria recusada).

3. **Nível 3:** como vocês decidiram quais cenários testar? (classes de equivalência, fronteiras,
   leitura das regras...)

   Fui regra por regra e, para cada `if`, peguei um valor de cada lado da fronteira (score -1/0,
   1000/1001, 299/300, 599/600, 799/800; parcelas 0/1 e 360/361). No comprometimento, calculei a
   parcela na calculadora (888,49) e testei duas rendas com 1 centavo de diferença em cada limite
   (30% e 50%). Os valores esperados saíram das regras do comentário, não de rodar o código.

4. **Teste que passa mas não protege:** descrevam um teste que executa o código e passa, mas não
   verificaria um erro (dica: um teste sem `assert`).

   Um teste que só chama `servico.transferir(origem, destino, 2_000.0)` e não tem nenhum `assert`: o
   cálculo da tarifa e o saque rodam e o teste fica verde, mas continuaria verde mesmo se a tarifa
   fosse 2% ou se o destino recebesse o valor errado.

5. **Divisão do trabalho:** quem escreveu o quê? (uma linha)

   Fiz sozinho.
