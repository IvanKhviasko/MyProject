package space.kis.myproject.util

data class Resource<out T>(val status: Status, val data: T?)

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}