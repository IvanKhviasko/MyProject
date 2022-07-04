package space.kis.myproject.domain

data class CountryModel(
    val name: String,
    val region: String?,
    val flag: String,
    var correct: Boolean?
)