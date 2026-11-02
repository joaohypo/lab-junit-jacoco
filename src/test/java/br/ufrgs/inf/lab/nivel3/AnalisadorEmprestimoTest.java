package br.ufrgs.inf.lab.nivel3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * NÍVEL 3 (do zero): AnalisadorEmprestimo.
 * Valores esperados calculados na mão a partir das regras 1 a 6.
 */
class AnalisadorEmprestimoTest {

    private static final double DELTA = 0.001;

    private final AnalisadorEmprestimo analisador = new AnalisadorEmprestimo();

    // Regra 1: os dois lados de cada validação, nas fronteiras
    @ParameterizedTest
    @CsvSource({
        "-1,   50000, 10000, 12,  DADOS_INVALIDOS",
        "0,    50000, 10000, 12,  NEGADO",
        "1000, 50000, 10000, 12,  APROVADO",
        "1001, 50000, 10000, 12,  DADOS_INVALIDOS",
        "800,  0,     10000, 12,  DADOS_INVALIDOS",
        "800,  50000, 0,     12,  DADOS_INVALIDOS",
        "800,  50000, 10000, 0,   DADOS_INVALIDOS",
        "800,  50000, 10000, 1,   APROVADO",
        "800,  50000, 10000, 360, APROVADO",
        "800,  50000, 10000, 361, DADOS_INVALIDOS"
    })
    void entradasNasFronteirasDaValidacao(int score, double renda, double valor, int parcelas,
                                          DecisaoEmprestimo esperada) {
        assertEquals(esperada, analisador.analisar(score, renda, valor, parcelas));
    }

    // Regra 2
    @Test
    void scoreAbaixoDe300DeveSerNegado() {
        // renda alta: só o score pode negar
        assertEquals(DecisaoEmprestimo.NEGADO, analisador.analisar(299, 100_000.0, 1_000.0, 12));
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(300, 100_000.0, 1_000.0, 12));
    }

    // Regra 3
    @ParameterizedTest
    @CsvSource({"800, 0.12", "799, 0.24", "600, 0.24", "599, 0.36"})
    void taxaAnualDependeDaFaixaDeScore(int score, double taxaEsperada) {
        assertEquals(taxaEsperada, analisador.taxaAnual(score), DELTA);
    }

    // Regra 4
    @Test
    void parcelaDeveSeguirATabelaPrice() {
        assertEquals(888.4879, analisador.calcularParcela(10_000.0, 0.01, 12), DELTA);
    }

    // Regra 5: score 800 (1% a.m.), 10.000 em 12x -> parcela de 888,49.
    // O limite de 50% cai entre as rendas 1.776,97 e 1.776,98; o de 30%, entre 2.961,62 e 2.961,63.
    @Test
    void comprometimentoAcimaDe50PorcentoDeveSerNegado() {
        assertEquals(DecisaoEmprestimo.NEGADO, analisador.analisar(800, 1_776.97, 10_000.0, 12));
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(800, 1_776.98, 10_000.0, 12));
    }

    @Test
    void comprometimentoAcimaDe30PorcentoVaiParaAnaliseManual() {
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(800, 2_961.62, 10_000.0, 12));
        assertEquals(DecisaoEmprestimo.APROVADO, analisador.analisar(800, 2_961.63, 10_000.0, 12));
    }

    // Regra 6
    @Test
    void comprometimentoAte30PorcentoSoAprovaComScore600OuMais() {
        // renda 5.000: parcelas de 945,60 (2% a.m.) e 1.004,62 (3% a.m.) ficam abaixo de 30%
        assertEquals(DecisaoEmprestimo.APROVADO, analisador.analisar(600, 5_000.0, 10_000.0, 12));
        assertEquals(DecisaoEmprestimo.ANALISE_MANUAL, analisador.analisar(599, 5_000.0, 10_000.0, 12));
    }
}
