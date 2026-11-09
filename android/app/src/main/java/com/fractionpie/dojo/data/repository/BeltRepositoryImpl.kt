package com.fractionpie.dojo.data.repository

import com.fractionpie.dojo.data.sample.SampleData
import com.fractionpie.dojo.domain.model.Belt
import com.fractionpie.dojo.domain.model.ScrollTerm
import com.fractionpie.dojo.domain.repository.BeltRepository

class BeltRepositoryImpl : BeltRepository {

    override fun belts(): List<Belt> = SampleData.belts

    override fun scrollTerms(): List<ScrollTerm> = SampleData.scrollTerms
}
