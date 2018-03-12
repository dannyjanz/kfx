package kfx

fun <In> reading() = object : TraverseWith<Reader<In, *>> {
    override fun <T, R> traverse(list: List<T>, function: (T) -> Monad<Reader<In, *>, R>): Reader<In, List<R>> =
            Reader { arg: In ->
                list.map { (function(it) as Reader<In, R>)(arg) }
            }
}
