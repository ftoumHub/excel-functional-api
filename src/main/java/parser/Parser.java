package parser;

import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Function;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

@FunctionalInterface
public interface Parser<A>  {

    Either<ParserError, A> parse(Workbook workbook, String name);

    /**
     * Description d'un flatMap pour Parser
     */
    static <A, B> Parser<B> flatMap(Parser<A> fa,
                                    Function<A, Parser<B>> f) {
        return (workbook, name) ->
                fa.parse(workbook, name)
                        .flatMap(a -> f.apply(a).parse(workbook, name));
    }

    static <A> Parser<A> success(A a) {
        return (workbook, name) -> right(a);
    }

    static <A> Parser<A> fail(Function<String, ParserError> error) {
        return (workbook, name) -> left(error.apply(name));
    }

    static <A> Parser<A> lift(Either<ParserError, A> res) {
        return (workbook, name) -> res;
    }
}
