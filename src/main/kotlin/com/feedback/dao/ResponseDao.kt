package com.feedback.dao

import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class ResponseDao(private val sql: DSLContext) {

    private val responseTable = ResponseTable.instance
}
