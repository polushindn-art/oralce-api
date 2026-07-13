package com.example.oracleapi.repository.idhead

import com.example.oracleapi.entity.table.Idhead
import org.springframework.data.jpa.domain.Specification

object IdheadSpecifications {

    fun byStatus(status: Long?): Specification<Idhead>? {
        return status?.let {
            Specification<Idhead> { root, _, cb ->
                cb.equal(root.get<Long>("idStatus"), it)
            }
        }
    }

    fun byDoctype(doctype: Long?): Specification<Idhead>? {
        return doctype?.let {
            Specification<Idhead> { root, _, cb ->
                cb.equal(root.get<Long>("doctype"), it)
            }
        }
    }

    // Для списка значений (IN clause)
    fun byDoctypeRn(doctypeRns: List<Long>?): Specification<Idhead>? {
        return if (doctypeRns.isNullOrEmpty()) {
            null
        } else {
            Specification<Idhead> { root, _, cb ->
                root.get<Long>("doctype").`in`(doctypeRns)
            }
        }
    }
}