package com.fractionpie.dojo.domain.usecase

import com.fractionpie.dojo.domain.model.Bout
import com.fractionpie.dojo.domain.model.Fraction

class CheckAnswerUseCase {

    fun checkSlice(bout: Bout, sliceCount: Int, filled: Int): Boolean {
        if (filled <= 0) return false
        if (sliceCount !in bout.allowedSlices) return false
        return Fraction(filled, sliceCount).reduced() == bout.target.reduced()
    }

    fun checkMatch(bout: Bout, option: Int): Boolean =
        option >= 0 && option == bout.correctOption

    fun checkCompare(bout: Bout, side: Int): Boolean = when (side) {
        0 -> bout.leftIsLarger
        1 -> !bout.leftIsLarger
        else -> false
    }
}
