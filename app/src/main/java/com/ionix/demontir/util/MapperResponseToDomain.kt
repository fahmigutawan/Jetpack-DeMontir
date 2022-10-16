package com.ionix.demontir.util

import com.ionix.demontir.model.api.response.BengkelResponse
import com.ionix.demontir.model.domain.BengkelDomain

object MapperResponseToDomain {
    fun mapBengkelResponseToDomain(input:BengkelResponse) =
        BengkelDomain(
            bengkel_id = input.bengkel_id ?: "",
            bengkel_name = input.bengkel_name ?: "",
            last_modified = input.last_modified ?: "",
            bengkel_long = input.bengkel_long ?: "0.0",
            bengkel_lat = input.bengkel_lat ?: "0.0"
        )
}