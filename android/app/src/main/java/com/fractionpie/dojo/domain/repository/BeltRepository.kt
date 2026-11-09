package com.fractionpie.dojo.domain.repository

import com.fractionpie.dojo.domain.model.Belt
import com.fractionpie.dojo.domain.model.ScrollTerm

interface BeltRepository {
    fun belts(): List<Belt>
    fun scrollTerms(): List<ScrollTerm>
}
