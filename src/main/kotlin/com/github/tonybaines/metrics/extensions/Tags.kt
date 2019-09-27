package com.github.tonybaines.metrics.extensions

typealias Tags = Map<String, String>


fun List<String>.asTags(tagValuePattern: Regex): Tags = this
    .filter { tagPairs -> tagPairs.contains('=') }
    .map { tagPair -> tagPair.split('=') }
    .associate { pair -> pair[0] to pair[1] }
    .validateTags(valuePattern = tagValuePattern)

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
private val INTRINSIC_TAGS = setOf("unit", "mtype")
private val isIntrinsic: (String) -> Boolean = { key -> INTRINSIC_TAGS.contains(key) }
private val CARBON_TAG_VALUE_PATTERN = """[_+%\-/\w]+""".toRegex()

fun List<String>.intrinsicTags(): Tags =
    this.asTags(CARBON_TAG_VALUE_PATTERN).filterKeys(isIntrinsic).validateContains(
        INTRINSIC_TAGS
    )

fun List<String>.extrinsicTags(): Tags =
    this.asTags(CARBON_TAG_VALUE_PATTERN).filterKeys {
        !isIntrinsic(
            it
        )
    }

private fun Tags.validateContains(requiredKeys: Set<String>): Tags =
    if (this.keys == requiredKeys) this
    else throw IllegalStateException("Required keys $requiredKeys not found in ${this.keys}")
