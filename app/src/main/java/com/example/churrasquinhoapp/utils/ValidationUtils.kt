package com.example.churrasquinhoapp.utils

object ValidationUtils {
    /**
     * Validates item input data
     */
    fun validateItemInput(
        name: String,
        quantity: Int,
        costPrice: Double,
        sellingPrice: Double,
        minimumStock: Int,
        unit: String
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate name
        if (name.isBlank()) {
            errors.add(ValidationError.EMPTY_NAME)
        }

        // Validate quantity
        if (quantity < 0) {
            errors.add(ValidationError.INVALID_QUANTITY)
        }

        // Validate prices
        if (costPrice <= 0) {
            errors.add(ValidationError.INVALID_COST_PRICE)
        }
        if (sellingPrice <= 0) {
            errors.add(ValidationError.INVALID_SELLING_PRICE)
        }
        if (sellingPrice <= costPrice) {
            errors.add(ValidationError.SELLING_PRICE_TOO_LOW)
        }

        // Validate minimum stock
        if (minimumStock < 0) {
            errors.add(ValidationError.INVALID_MINIMUM_STOCK)
        }

        // Validate unit
        if (unit.isBlank()) {
            errors.add(ValidationError.EMPTY_UNIT)
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Validates sale input data
     */
    fun validateSaleInput(
        itemId: Long,
        quantity: Int,
        availableQuantity: Int,
        unitPrice: Double,
        totalValue: Double
    ): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Validate item selection
        if (itemId <= 0) {
            errors.add(ValidationError.INVALID_ITEM)
        }

        // Validate quantity
        if (quantity <= 0) {
            errors.add(ValidationError.INVALID_QUANTITY)
        } else if (quantity > availableQuantity) {
            errors.add(ValidationError.INSUFFICIENT_STOCK)
        }

        // Validate prices
        if (unitPrice <= 0) {
            errors.add(ValidationError.INVALID_UNIT_PRICE)
        }
        if (totalValue <= 0) {
            errors.add(ValidationError.INVALID_TOTAL_VALUE)
        }

        // Validate calculation
        val expectedTotal = quantity * unitPrice
        if (Math.abs(totalValue - expectedTotal) > 0.01) {
            errors.add(ValidationError.INVALID_CALCULATION)
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    /**
     * Validation result sealed class
     */
    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val errors: List<ValidationError>) : ValidationResult()
    }

    /**
     * Validation error enum
     */
    enum class ValidationError {
        EMPTY_NAME,
        INVALID_QUANTITY,
        INVALID_COST_PRICE,
        INVALID_SELLING_PRICE,
        SELLING_PRICE_TOO_LOW,
        INVALID_MINIMUM_STOCK,
        EMPTY_UNIT,
        INVALID_ITEM,
        INSUFFICIENT_STOCK,
        INVALID_UNIT_PRICE,
        INVALID_TOTAL_VALUE,
        INVALID_CALCULATION;

        fun getErrorMessage(): String {
            return when (this) {
                EMPTY_NAME -> "Nome é obrigatório"
                INVALID_QUANTITY -> "Quantidade inválida"
                INVALID_COST_PRICE -> "Preço de custo inválido"
                INVALID_SELLING_PRICE -> "Preço de venda inválido"
                SELLING_PRICE_TOO_LOW -> "Preço de venda deve ser maior que o preço de custo"
                INVALID_MINIMUM_STOCK -> "Estoque mínimo inválido"
                EMPTY_UNIT -> "Unidade é obrigatória"
                INVALID_ITEM -> "Selecione um item"
                INSUFFICIENT_STOCK -> "Quantidade maior que o estoque disponível"
                INVALID_UNIT_PRICE -> "Preço unitário inválido"
                INVALID_TOTAL_VALUE -> "Valor total inválido"
                INVALID_CALCULATION -> "Erro no cálculo do valor total"
            }
        }
    }

    /**
     * Extension function to get all error messages
     */
    fun List<ValidationError>.getErrorMessages(): List<String> {
        return map { it.getErrorMessage() }
    }

    /**
     * Extension function to get first error message
     */
    fun List<ValidationError>.getFirstErrorMessage(): String {
        return firstOrNull()?.getErrorMessage() ?: ""
    }
}