package kfx

interface State<S, out T> : Functor<State<S, *>, T>, Monad<State<S, *>, T> {

    val run: (S) -> Pair<T, S>

    override fun <R> map(transform: (T) -> R): State<S, R> = State { input: S ->
        run(input).let { Pair(transform(it.first), it.second) }
    }

    override fun <R> flatMap(bind: (T) -> Monad<State<S, *>, R>): State<S, R> = State { input: S ->
        run(input).let { (bind(it.first) as State<S, R>).run(it.second) }
    }

    companion object {

        operator fun <S, T> invoke(func: (S) -> Pair<T, S>) = object : State<S, T> {
            override val run = func
        }

    }

}

infix operator fun <S, T, R> State<S, T>.rangeTo(next: State<S, R>) = this.flatMap { next }