package jatx.russianrocksongbook.voicecommands.util

fun String.voiceFilter() = lowercase()
    .replace("ё", "е")
    .filter {
        (it in 'а'..'я')
            .or(it in 'a'..'z')
            .or(it in '0'..'9')
    }

fun String.aliases(): List<String> {
    val lower = this.lowercase()
    artistAliases.keys.forEach { key ->
        if (lower.startsWith(key)) {
            return artistAliases[key]!!
                .map { lower.replace(key, it) }
        }
    }
    return listOf(lower)
}

private val artistAliases = hashMapOf (
    "fleur" to listOf("flеur", "fleur"),
    "знаки" to listOf("znaki", "знаки"),
    "мультфильмы" to listOf("мультfильмы", "мультфильмы"),
    "князь" to listOf("княzz", "князь"),
    "люмен" to listOf("lumen", "люмен"),
    "черный" to listOf("черный", "чёрный"),
    "пожертвование" to listOf("пожертвования", "пожертвование")
)
