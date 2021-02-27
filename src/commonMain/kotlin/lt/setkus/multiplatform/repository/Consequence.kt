package lt.setkus.multiplatform.repository

sealed class Consequence<out A> {

    fun getOrElse(default: @UnsafeVariance A): A = when (this) {
        is Success -> this.value
        else -> default
    }

    class Failure<out A> internal constructor(val exception: RuntimeException) : Consequence<A>() {
        override fun toString() = "Failure: ${exception.message}"
    }

    class Success<out A> internal constructor(val value: A) : Consequence<A>() {
        override fun toString() = "Success: $value"
    }

    companion object {
        operator fun <A> invoke(a: A?): Consequence<A> = when (a) {
            null -> Failure(NullPointerException())
            else -> Success(a)
        }

        fun <A> failure(exception: Exception): Consequence<A> = Failure(IllegalStateException(exception))
    }
}
