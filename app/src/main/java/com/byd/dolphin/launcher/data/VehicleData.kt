package com.byd.dolphin.launcher.data

/**
 * Snapshot of live vehicle data that the launcher displays.
 * Values come from Content Providers / BYD Auto API / CAN (via research findings).
 */
data class VehicleData(
    val speedKmh: Float? = null,
    val socPercent: Float? = null,          // State of Charge
    val rangeKm: Float? = null,
    val outsideTempC: Float? = null,
    val powerKw: Float? = null,

    // TPMS (kPa) - front left, front right, rear left, rear right
    val tpmsFl: Float? = null,
    val tpmsFr: Float? = null,
    val tpmsRl: Float? = null,
    val tpmsRr: Float? = null,

    // Trip
    val tripDistanceKm: Float? = null,
    val tripConsumptionKwhPer100km: Float? = null,
    val tripDurationMin: Int? = null,

    val gear: String? = null,               // P / R / N / D
    val isCharging: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        val EMPTY = VehicleData()
    }
}
