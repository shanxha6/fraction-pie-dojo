package com.fractionpie.dojo.data.sample

import com.fractionpie.dojo.R
import com.fractionpie.dojo.domain.model.Belt
import com.fractionpie.dojo.domain.model.BoutType
import com.fractionpie.dojo.domain.model.ScrollTerm

object SampleData {

    val belts: List<Belt> = listOf(
        Belt(
            index = 0,
            nameRes = R.string.belt_white,
            skillRes = R.string.skill_white,
            color = 0xFFF3D9B8,
            boutType = BoutType.SLICE,
            denominators = listOf(2, 4),
        ),
        Belt(
            index = 1,
            nameRes = R.string.belt_yellow,
            skillRes = R.string.skill_yellow,
            color = 0xFFE3B23C,
            boutType = BoutType.SLICE,
            denominators = listOf(3, 6),
        ),
        Belt(
            index = 2,
            nameRes = R.string.belt_orange,
            skillRes = R.string.skill_orange,
            color = 0xFFD97B4D,
            boutType = BoutType.SLICE,
            denominators = listOf(5, 8),
        ),
        Belt(
            index = 3,
            nameRes = R.string.belt_green,
            skillRes = R.string.skill_green,
            color = 0xFF7FA46B,
            boutType = BoutType.MATCH,
            denominators = listOf(2, 4, 6, 8),
        ),
        Belt(
            index = 4,
            nameRes = R.string.belt_blue,
            skillRes = R.string.skill_blue,
            color = 0xFF4A7FA4,
            boutType = BoutType.MATCH,
            denominators = listOf(3, 6, 9, 12),
        ),
        Belt(
            index = 5,
            nameRes = R.string.belt_purple,
            skillRes = R.string.skill_purple,
            color = 0xFF7A5A9E,
            boutType = BoutType.COMPARE,
            denominators = listOf(4, 6, 8),
        ),
        Belt(
            index = 6,
            nameRes = R.string.belt_brown,
            skillRes = R.string.skill_brown,
            color = 0xFF8A5A3C,
            boutType = BoutType.COMPARE,
            denominators = listOf(3, 4, 5, 6, 8),
            unlikeDenominators = true,
        ),
        Belt(
            index = 7,
            nameRes = R.string.belt_red,
            skillRes = R.string.skill_red,
            color = 0xFFC8553D,
            boutType = BoutType.SLICE,
            denominators = listOf(4, 6, 8),
            sumTarget = true,
        ),
        Belt(
            index = 8,
            nameRes = R.string.belt_black,
            skillRes = R.string.skill_black,
            color = 0xFF2E1F17,
            boutType = BoutType.SLICE,
            denominators = listOf(6, 12),
            sumTarget = true,
            unlikeDenominators = true,
        ),
        Belt(
            index = 9,
            nameRes = R.string.belt_master,
            skillRes = R.string.skill_master,
            color = 0xFFD9B36C,
            boutType = BoutType.SLICE,
            denominators = listOf(2, 3, 4, 6, 8),
            mixed = true,
        ),
    )

    val scrollTerms: List<ScrollTerm> = listOf(
        ScrollTerm(R.string.term_numerator, R.string.def_numerator, slices = 4, filled = 3),
        ScrollTerm(R.string.term_denominator, R.string.def_denominator, slices = 6, filled = 1),
        ScrollTerm(R.string.term_equivalent, R.string.def_equivalent, slices = 8, filled = 4),
        ScrollTerm(R.string.term_unit, R.string.def_unit, slices = 3, filled = 1),
        ScrollTerm(R.string.term_common, R.string.def_common, slices = 6, filled = 5),
        ScrollTerm(R.string.term_compare, R.string.def_compare, slices = 5, filled = 2),
        ScrollTerm(R.string.term_whole, R.string.def_whole, slices = 4, filled = 4),
    )
}
