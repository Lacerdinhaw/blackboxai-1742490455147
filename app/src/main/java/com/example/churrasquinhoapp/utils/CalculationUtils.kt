package com.example.churrasquinhoapp.utils

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

object CalculationUtils {
    private const val DEFAULT_SCALE = 2
    private const val PERCENTAGE_SCALE = 1
    private const val HUNDRED = 100.0

    /**
     * Currency calculations
     */
    fun calculateTotal(quantity: Double, unitPrice: Double): Double {
        return roundCurrency(quantity * unitPrice)
    }

    fun calculateProfit(sellingPrice: Double, costPrice: Double): Double {
        return roundCurrency(sellingPrice - costPrice)
    }

    fun calculateProfitMargin(sellingPrice: Double, costPrice: Double): Double {
        return if (costPrice > 0) {
            roundPercentage((sellingPrice - costPrice) / costPrice * HUNDRED)
        } else {
            0.0
        }
    }

    fun calculateMarkup(sellingPrice: Double, costPrice: Double): Double {
        return if (sellingPrice > 0) {
            roundPercentage((sellingPrice - costPrice) / sellingPrice * HUNDRED)
        } else {
            0.0
        }
    }

    /**
     * Inventory calculations
     */
    fun calculateReorderPoint(
        averageDailySales: Double,
        leadTimeInDays: Int,
        safetyStock: Int
    ): Int {
        val leadTimeDemand = averageDailySales * leadTimeInDays
        return (leadTimeDemand + safetyStock).roundToInt()
    }

    fun calculateSafetyStock(
        maxDailyDemand: Double,
        averageDailyDemand: Double,
        leadTimeInDays: Int
    ): Int {
        return ((maxDailyDemand - averageDailyDemand) * leadTimeInDays).roundToInt()
    }

    fun calculateStockTurnover(
        totalSales: Double,
        averageInventory: Double
    ): Double {
        return if (averageInventory > 0) {
            roundToScale(totalSales / averageInventory, 1)
        } else {
            0.0
        }
    }

    /**
     * Sales calculations
     */
    fun calculateDailyAverage(totalSales: Double, numberOfDays: Int): Double {
        return if (numberOfDays > 0) {
            roundCurrency(totalSales / numberOfDays)
        } else {
            0.0
        }
    }

    fun calculateGrowthRate(
        currentValue: Double,
        previousValue: Double
    ): Double {
        return if (previousValue > 0) {
            roundPercentage((currentValue - previousValue) / previousValue * HUNDRED)
        } else {
            0.0
        }
    }

    /**
     * Rounding functions
     */
    fun roundCurrency(value: Double): Double {
        return roundToScale(value, DEFAULT_SCALE)
    }

    fun roundPercentage(value: Double): Double {
        return roundToScale(value, PERCENTAGE_SCALE)
    }

    fun roundToScale(value: Double, scale: Int): Double {
        return BigDecimal(value)
            .setScale(scale, RoundingMode.HALF_EVEN)
            .toDouble()
    }

    /**
     * Validation functions
     */
    fun isValidQuantity(value: Double): Boolean {
        return value >= 0
    }

    fun isValidPrice(value: Double): Boolean {
        return value >= 0
    }

    fun isValidPercentage(value: Double): Boolean {
        return value in 0.0..100.0
    }

    /**
     * Conversion functions
     */
    fun gramsToKilograms(grams: Double): Double {
        return roundToScale(grams / 1000.0, DEFAULT_SCALE)
    }

    fun kilogramsToGrams(kilograms: Double): Double {
        return roundToScale(kilograms * 1000.0, DEFAULT_SCALE)
    }

    fun millilitersToLiters(milliliters: Double): Double {
        return roundToScale(milliliters / 1000.0, DEFAULT_SCALE)
    }

    fun litersToMilliliters(liters: Double): Double {
        return roundToScale(liters * 1000.0, DEFAULT_SCALE)
    }

    /**
     * Statistical functions
     */
    fun calculateAverage(values: List<Double>): Double {
        return if (values.isNotEmpty()) {
            roundToScale(values.sum() / values.size, DEFAULT_SCALE)
        } else {
            0.0
        }
    }

    fun calculateMedian(values: List<Double>): Double {
        return if (values.isNotEmpty()) {
            val sortedValues = values.sorted()
            val middle = sortedValues.size / 2
            if (sortedValues.size % 2 == 0) {
                roundToScale((sortedValues[middle - 1] + sortedValues[middle]) / 2, DEFAULT_SCALE)
            } else {
                roundToScale(sortedValues[middle], DEFAULT_SCALE)
            }
        } else {
            0.0
        }
    }

    fun calculateStandardDeviation(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0

        val average = calculateAverage(values)
        val variance = values.map { (it - average) * (it - average) }.average()
        return roundToScale(kotlin.math.sqrt(variance), DEFAULT_SCALE)
    }

    /**
     * Range calculations
     */
    fun isInRange(value: Double, min: Double, max: Double): Boolean {
        return value in min..max
    }

    fun clamp(value: Double, min: Double, max: Double): Double {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }
}