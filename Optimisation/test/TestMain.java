import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests pour vérifier le fonctionnement des fonctions d'activation
 * (sigmoide et tangente hyperbolique)
 */
public class TestFonctionsActivation {


    @Test
    @DisplayName("Test fonction Sigmoïde 1")
    void test_function_sigmoid_1() {

        // Given
        double entree = -1.0;
        double attenduResultat = 0.26894;
        double attenduDerivee = 0.19661;

        // When
        double obtenuResultat = sigmoid.evaluate(entree);
        double obtenuDerivee = sigmoid.evaluateDer(attenduResultat);

        // Then
        assertEquals(attenduResultat, obtenuResultat, EPS);
        assertEquals(attenduDerivee, obtenuDerivee, EPS);

    }
}