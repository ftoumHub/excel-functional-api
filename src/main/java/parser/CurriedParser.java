package parser;

import io.vavr.control.Either;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Function;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

public abstract class CurriedParser<A>  {

    private final CurriedParser<A> _self = this;

    /**
     * On crée un nouveau Parser en supprimant la référence vers le nom de la cellule.
     *
     * @param workbook
     * @return
     */
    public abstract Either<ParserError, A> parse(Workbook workbook);


    public static <A, B> CurriedParser<B> map(CurriedParser<A> fa, Function<A, B> f) {
        return flatMap(fa, a -> success(f.apply(a)));
    }

    public <B> CurriedParser<B> flatMap(Function<A, CurriedParser<B>> f) {
        return new CurriedParser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return _self.parse(workbook)
                        .flatMap(a -> f.apply(a).parse(workbook)
                );
            }
        };
    }

    public <B> CurriedParser<B> flatMapF(Function<A, Either<ParserError, B>> f) {
        return flatMap(a -> lift(f.apply(a)));
    }

    static <A, B> CurriedParser<B> flatMap(CurriedParser<A> fa, Function<A, CurriedParser<B>> f) {
        return new CurriedParser<B>() {
            @Override
            public Either<ParserError, B> parse(Workbook workbook) {
                return fa.parse(workbook).flatMap(a ->
                        f.apply(a).parse(workbook)
                );
            }
        };
    }

    static <A> CurriedParser<A> success(A a) {
        return new CurriedParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return right(a);
            }
        };
    }

    static <A> CurriedParser<A> fail(ParserError error) {
        return new CurriedParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return left(error);
            }
        };
    }

    static <A> CurriedParser<A> lift(Either<ParserError, A> res) {
        return new CurriedParser<A>() {
            @Override
            public Either<ParserError, A> parse(Workbook workbook) {
                return res;
            }
        };
    }

}
