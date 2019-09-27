package com.github.tonybaines.metrics.extensions

typealias Tags = Map<String, String>


fun List<String>.asTags(): Tags = this
    .filter { tagPairs -> tagPairs.contains('=') }
    .map { tagPair -> tagPair.split('=') }
    .associate { pair -> pair[0] to pair[1] }

fun Tags.validateTags(valuePattern: Regex): Tags =
    this.onEach { entry ->
        entry.key.ensureValidTagName()
        entry.value.ensureValidTagValue(valuePattern)
    }


private val VALID_TAG_NAME_PATTERN = """[a-zA-Z]+[_+%\-\w]+""".toRegex()
private fun String.ensureValidTagName(): String =
    if (this.matches(VALID_TAG_NAME_PATTERN)) this
    else throw IllegalArgumentException("'$this' is not a valid value for a tag name")

private fun String.ensureValidTagValue(pattern: Regex): String =
    if (this.matches(pattern)) this
    else throw IllegalArgumentException("'$this' is not a valid tag value")


// Carbon 2.0 format tags

fun List<String>.intrinsicTags(): Tags =
    this.asTags().filterKeys(isIntrinsic)

fun List<String>.extrinsicTags(): Tags =
    this.asTags().filterKeys {
        !isIntrinsic(
            it
        )
    }

private val INTRINSIC_TAGS = setOf("unit", "mtype")
private val isIntrinsic: (String) -> Boolean = { key -> INTRINSIC_TAGS.contains(key) }
fun Tags.ensureComplete(): Tags =
    if (this.keys == INTRINSIC_TAGS) this
    else throw IllegalArgumentException("Required keys $INTRINSIC_TAGS not found in ${this.keys}")
