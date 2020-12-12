package ru.meanmail

import com.jetbrains.python.packaging.PyPackageVersionNormalizer

interface Logical {

    fun check(values: Map<String, String?>): Boolean

}

class Or(private vararg val items: Logical) : Logical {
    override fun check(values: Map<String, String?>): Boolean {
        if (items.isEmpty()) {
            return true
        }
        return items.any { it.check(values) }
    }

}

class And(private vararg val items: Logical) : Logical {
    override fun check(values: Map<String, String?>): Boolean {
        if (items.isEmpty()) {
            return false
        }
        return items.all { it.check(values) }
    }

}

class Expression(
    private val variable: String, private val operation: String, private var value: String
) : Logical {
    override fun check(values: Map<String, String?>): Boolean {
        val actual = values[variable] ?: return false

        if (operation == "===") {
            return actual == value
        }

        if (variable in VERSION_VARIABLES) {
            return compareVersions(
                PyPackageVersionNormalizer.normalize(actual),
                operation,
                PyPackageVersionNormalizer.normalize(value)
            )
        }

        return when (operation) {
            "==" -> {
                actual == value
            }
            "!=" -> {
                actual != value
            }
            else -> false
        }

    }

}

class True : Logical {
    override fun check(values: Map<String, String?>): Boolean {
        return true
    }

}

class False : Logical {
    override fun check(values: Map<String, String?>): Boolean {
        return false
    }

}
