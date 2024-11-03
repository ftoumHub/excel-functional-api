package parser;

import io.vavr.control.Either;
import org.junit.jupiter.api.Test;

import static io.vavr.API.*;
import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.*;

class S01_Introduction {

    /**
     * Un exemple de méthode dite "partielle", susceptible de retourner une exception.
     */
    public Boolean estAdulte_PF(Integer age) {
        if (age < 0) throw new IllegalArgumentException("l'age doit être un entier positif");
        return age >= 18;
    }

    @Test
    void estAdultePartielleTest() {
        assertFalse(estAdulte_PF(4));
        assertTrue(estAdulte_PF(25));
        assertThrows(IllegalArgumentException.class, () -> estAdulte_PF(-10));
    }

    /**
     * On a donc un problème car lorsqu'on appelle cette méthode, on ne sait pas si il s'agit
     * d'une méthode partielle ou totale (sauf si on indique throws ... dans la signature de la méthode).
     * On doit regarder l'implémentation ou la doc...
     * <p>
     * Le seul moyen de traiter l'erreur du point de vu de l'appelant est donc de catcher
     * la ou les exceptions qui peuvent survenir.
     * <p>
     * Une technique consiste à "augmenter" le type de retour en utilisant par exemple Either.
     * <p>
     * On indique dans la signature le type d'erreur.
     */
    public Either<String, Boolean> estAdulte(Integer age) {
        if (isNull(age))    return Either.left("l'age ne peut pas être null");
        if (age < 0)        return Either.left("l'age doit être un entier positif");
        else                return Either.right(age >= 18);
    }

    @Test
    void estAdulteTest() {
        assertEquals(Right(false), estAdulte(4));
        assertEquals(Right(true), estAdulte(25));

        assertEquals(Left("l'age ne peut pas être null"), estAdulte(null));
        assertEquals(Left("l'age doit être un entier positif"), estAdulte(-2));
    }


    /**
     * Avec vavr, l'écriture de la fonction estAdulte devient :
     */
    public Either<String, Boolean> estAdulte_Vavr(Integer age) {
        return Option(age)
                .fold(() -> Either.left("l'age ne peut pas être null"),
                        a -> {
                            if (a < 0) return Either.left("l'age doit être un entier positif");
                            else return Either.right(age >= 18);
                        });
    }

    @Test
    void estAdulteVavrTest() {
        assertEquals(Right(false), estAdulte_Vavr(4));
        assertEquals(Right(true),  estAdulte_Vavr(25));

        assertEquals(Left("l'age ne peut pas être null"), estAdulte_Vavr(null));
        assertEquals(Left("l'age doit être un entier positif"), estAdulte_Vavr(-2));
    }
}