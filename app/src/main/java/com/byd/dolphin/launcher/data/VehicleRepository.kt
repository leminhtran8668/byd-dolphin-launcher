package com.byd.dolphin.launcher.data

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Reads vehicle telemetry for DiLink 3.
 * Sources: content provider + optional BYDAuto reflection (research repos).
 */
class VehicleRepository(private val context: Context) {

    private val _data = MutableStateFlow(VehicleData.EMPTY)
    val data: StateFlow<VehicleData> = _data.asStateFlow()

    private val carStatusUri = Uri.parse("content://com.byd.carStatusProvider/car_status")

    suspend fun refresh() = withContext(Dispatchers.IO) {
        try {
            var updated = _data.value.copy(timestamp = System.currentTimeMillis())
            queryCarStatus()?.let { map ->
                updated = mapProviderKeys(updated, map)
            }
            updated = tryBydAutoApi(updated)
            _data.value = updated
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    private fun mapProviderKeys(base: VehicleData, map: Map<String, String>): VehicleData {
        fun f(key: String) = map[key]?.toFloatOrNull()
        fun i(key: String) = map[key]?.toIntOrNull()
        return base.copy(
            socPercent = f("soc") ?: f("battery_soc") ?: f("remain_battery") ?: base.socPercent,
            rangeKm = f("remain_range") ?: f("range") ?: f("endurance_mileage") ?: base.rangeKm,
            outsideTempC = f("outside_temp") ?: f("ambient_temp") ?: base.outsideTempC,
            tpmsFl = f("tyre_pressure_fl") ?: f("tpms_fl") ?: f("tire_pressure_left_front") ?: base.tpmsFl,
            tpmsFr = f("tyre_pressure_fr") ?: f("tpms_fr") ?: f("tire_pressure_right_front") ?: base.tpmsFr,
            tpmsRl = f("tyre_pressure_rl") ?: f("tpms_rl") ?: f("tire_pressure_left_rear") ?: base.tpmsRl,
            tpmsRr = f("tyre_pressure_rr") ?: f("tpms_rr") ?: f("tire_pressure_right_rear") ?: base.tpmsRr,
            tripDistanceKm = f("trip_distance") ?: f("travel_distance") ?: base.tripDistanceKm,
            tripConsumptionKwhPer100km = f("avg_consumption") ?: f("trip_consumption") ?: base.tripConsumptionKwhPer100km,
            tripDurationMin = i("trip_time") ?: i("travel_time") ?: base.tripDurationMin
        )
    }

    private fun queryCarStatus(): Map<String, String>? {
        return try {
            val cursor: Cursor? = context.contentResolver.query(carStatusUri, null, null, null, null)
            cursor?.use { c ->
                if (!c.moveToFirst()) return null
                buildMap {
                    for (i in 0 until c.columnCount) {
                        put(c.getColumnName(i), c.getString(i) ?: "")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "carStatusProvider: ${e.message}")
            null
        }
    }

    private fun tryBydAutoApi(base: VehicleData): VehicleData {
        return try {
            base
        } catch (e: Exception) {
            Log.d(TAG, "BYDAuto skip: ${e.message}")
            base
        }
    }

    fun loadDemoData(animatedSpeed: Float = 62f) {
        _data.value = VehicleData(
            speedKmh = animatedSpeed.coerceIn(0f, 160f),
            socPercent = 72f,
            rangeKm = 285f,
            outsideTempC = 31f,
            powerKw = if (animatedSpeed > 50f) 18f else -8.5f,
            tpmsFl = 241f,
            tpmsFr = 238f,
            tpmsRl = 243f,
            tpmsRr = 240f,
            tripDistanceKm = 48.3f,
            tripConsumptionKwhPer100km = 14.6f,
            tripDurationMin = 52,
            gear = if (animatedSpeed < 1f) "P" else "D",
            isCharging = false
        )
    }

    companion object {
        private const val TAG = "VehicleRepo"
    }
}
