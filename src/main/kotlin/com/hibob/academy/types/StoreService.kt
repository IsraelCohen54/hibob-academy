package com.hibob.academy.types

import org.springframework.stereotype.Component
import java.time.LocalDate
import Cart
import Payment
import Check
import Statuses


object Logger {
    fun fail(message: String): Nothing? {
        throw IllegalStateException(message)
    }
}

@Component
class StoreService {

    fun pay(cart: List<Cart>, payment: Payment): Map<String, Check> {
        return cart.associate { c ->
            c.clientId to checkout(c, payment)
        }
    }

    companion object {
        private const val ZERO_TOTAL_COSTS_ON_FAILED_BUY = 0.0
        private const val VISA_LEGIT_NUM_LENGTH = 10
    }

    private fun checkout(cart: Cart, payment: Payment): Check {
        val totalPricesOfLegitProducts = cart.products
            .filter { product -> product.custom is Boolean && product.custom }
            .sumOf { product -> product.price }

        val status: Statuses = when(payment) {
            is Payment.Cash -> {
                Logger.fail("payment is Cash")
                Statuses.FAILURE
            }

            is Payment.CreditCard -> {
                when {
                    payment.expiryDate < LocalDate.now() -> Statuses.FAILURE
                    payment.limit < totalPricesOfLegitProducts -> Statuses.FAILURE
                    payment.type !in listOf(CreditCardType.VISA, CreditCardType.MASTERCARD) -> Statuses.FAILURE
                    payment.number.length != VISA_LEGIT_NUM_LENGTH -> Statuses.FAILURE
                    else -> Statuses.SUCCESS
                }
            }

            is Payment.PayPal -> if (!payment.email.contains('@')) Statuses.FAILURE else Statuses.SUCCESS
            else -> Statuses.FAILURE // todo delete, currently claims it's not exhaustive even though its sealed class
        }
    return Check(cart.clientId, status, if (status == Statuses.SUCCESS) totalPricesOfLegitProducts else ZERO_TOTAL_COSTS_ON_FAILED_BUY)
}
}
