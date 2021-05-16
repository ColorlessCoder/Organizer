class NumberUtils {
    companion object {
        data class ValueWithNextIndex(val value: Double, val nextIndex: Int)

        fun isNumberChar(char: Char): Boolean = char.isDigit() || char == '.'
        fun isStartingBracket(char: Char): Boolean = char == '('
        fun hasHighPrecedence(char: Char): Boolean = char == '*' || char == '/'
        fun isAllowedOperator(char: Char): Boolean =
            char == '*' || char == '/' || char == '+' || char == '-'

        fun doSimpleCalculation(a: Double, b: Double, operator: Char): Double {
            if (operator == '+') return a + b
            if (operator == '-') return a - b
            if (operator == '*') return a * b
            if (operator == '/') {
                if (b.equals(0.0)) throw Exception("Cannot Divide by zero")
                return a / b
            }
            throw java.lang.Exception("Unknown operator '$operator'")
        }

        fun getNextNumber(str: String, startIndex: Int): ValueWithNextIndex {
            if (startIndex < str.length && isStartingBracket(str[startIndex])) return calculateValueFromString(
                str,
                startIndex + 1,
                ')'
            )
            var indexOfNextNonNumberChar = str.length
            for (index in (startIndex + 1) until str.length) {
                if (!isNumberChar(str[index])) {
                    indexOfNextNonNumberChar = index
                    break
                }
            }
            var result = 0.0
            if (startIndex < indexOfNextNonNumberChar) {
                try {
                    result = str.substring(
                        startIndex,
                        indexOfNextNonNumberChar
                    ).trim().toDouble()
                } catch (ex: java.lang.Exception) {
                    throw Exception("Unable to parse expression")
                }
            }

            return ValueWithNextIndex(result, indexOfNextNonNumberChar)
        }

        fun getNextValue(str: String, startIndex: Int): ValueWithNextIndex {
            val firstVWI = getNextNumber(str, startIndex)
            if (firstVWI.nextIndex < str.length && hasHighPrecedence(str[firstVWI.nextIndex])) {
                val secondVWI = getNextValue(str, firstVWI.nextIndex + 1)
                return ValueWithNextIndex(
                    doSimpleCalculation(
                        firstVWI.value,
                        secondVWI.value,
                        str[firstVWI.nextIndex]
                    ), secondVWI.nextIndex
                )
            }
            return firstVWI
        }

        fun calculateValueFromString(
            str: String,
            startIndex: Int,
            endChar: Char? = null
        ): ValueWithNextIndex {
            var result = 0.0
            var index = startIndex
            var lastOperator = '+'
            while (index < str.length && str[index] != endChar) {
                val currentVWI = getNextValue(str, index)
                result = doSimpleCalculation(result, currentVWI.value, lastOperator)
                index = currentVWI.nextIndex
                if (index < str.length && str[index] != endChar) {
                    lastOperator = str[index]
                    if (!isAllowedOperator(lastOperator)) {
                        throw java.lang.Exception("Excepted an operator but found '$lastOperator'")
                    }
                    index++
                }
            }
            if (endChar != null) {
                if (index >= str.length || endChar != str[index]) {
                    throw java.lang.Exception("Missing symbol '$endChar'")
                }
                index++
            }
            return ValueWithNextIndex(result, index)
        }
    }
}