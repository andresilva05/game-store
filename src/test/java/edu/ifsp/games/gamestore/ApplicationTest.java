package edu.ifsp.games.gamestore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationTest {

    @Test
    @DisplayName("Garante que o ambiente de testes JUnit 5 esta operacional")
    void contextoCarregaComSucesso() {
        assertTrue(true, "O ambiente de testes deve estar funcional no pipeline.");
    }
}
