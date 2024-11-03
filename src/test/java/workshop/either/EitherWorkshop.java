package workshop.either;

import io.vavr.API;
import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.vavr.API.For;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EitherWorkshop {

    @Test
    void create_successful_Right_Either_with_value_1() {
        Either<String, Integer> success = right(1);

        assertThat(success.isRight()).isTrue();
        assertEquals(1, success.get());
    }

    @DisplayName("conversion: Option -> Either")
    @Test
    void conversion_Option_to_Either() {
        //given:
        Option<Integer> some = some(1);
        Option<Integer> none = none();
        var message = "option was empty";
        //when:
        Either<String, Integer> eitherFromSome = some.toEither(message);
        Either<String, Integer> eitherFromNone = none.toEither(message);
        //then:
        assertEquals(right(1), eitherFromSome);
        assertEquals(Either.left(message), eitherFromNone);
    }

    @DisplayName("sum values of right Eithers sequence or return the first failure")
    @Test
    void sum_values_of_right_Eithers_sequence_or_return_the_first_failure() {
        //given:
        Either<String, Integer> n1 = right(1);
        Either<String, Integer> n2 = right(2);
        Either<String, Integer> n3 = right(3);
        Either<String, Integer> n4 = right(4);
        Either<String, Integer> failure1 = Either.left("cannot parse integer a");
        Either<String, Integer> failure2 = Either.left("cannot parse integer b");
        //and:
        List<Either<String, Integer>> from1To4 = List.of(n1, n2, n3, n4);
        List<Either<String, Integer>> all = List.of(n1, n2, n3, n4, failure1, failure2);
        //when:
        Either<String, Number> sum = Either.sequenceRight(from1To4).map(seq -> seq.sum().intValue()); // hint: sequenceRight, map, sum
        Either<String, Number> fail = Either.sequenceRight(all).map(seq -> seq.sum().intValue()); // hint: sequenceRight, map, sum

        //then:
        assertThat(sum.isRight()).isTrue();
        assertEquals(10, sum.get());

        //and:
        assertEquals(fail, Either.left("cannot parse integer a"));
    }

    /**
     * https://stackoverflow.com/questions/59661039/combining-eithers-in-vavr
     */
    @Test
    void working_with_multiple_Either() {
        //given:
        Either<String, Integer> n1 = right(1);
        Either<String, Integer> n2 = right(2);
        Either<String, Integer> n3 = right(3);

        var sum1 = n1.flatMap(i1 ->
                n2.flatMap(i2 ->
                        n3.flatMap(i3 -> sommeDe(i1, i2, i3))
                )
        );

        assertEquals(6, sum1.get());

        var sum2 = For(n1, n2, n3)
                .yield(Tuple::of)
                .map(t -> sommeDe(t._1, t._2, t._3)).get();

        assertEquals(6, sum2.get());
    }

    private Either<String, Integer> sommeDe(Integer i1, Integer i2, Integer i3) {
        return right(i1 + i2 + i3);
    }
}
