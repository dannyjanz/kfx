package kfx

interface TraverseWith<M> {

    fun <T, R> traverse(list: List<T>, function: (T) -> Monad<M, R>): Monad<M, List<R>>

}

fun <T, R, M : Monad<*, *>> List<T>.traverse(traverseWith: TraverseWith<M>, function: (T) -> Monad<M, R>) =
        traverseWith.traverse(this, function)
