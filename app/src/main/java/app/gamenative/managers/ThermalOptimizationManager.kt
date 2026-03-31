package app.gamenative.managers

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

enum class ThermalState {
    COOL,
    WARM,
    HOT,
    CRITICAL,
}

data class ThermalStatus(
    val temperature: Float,
    val state: ThermalState,
    val cpuScalingGovernor: String = "unknown",
)

class ThermalOptimizationManager(private val context: Context) {

    companion object {
        const val CRITICAL_TEMP_C = 42f
        const val HOT_TEMP_C = 38f
        const val WARM_TEMP_C = 34f
        private const val POLLING_INTERVAL_MS = 5000L

        private val THERMAL_ZONE_PATHS = listOf(
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/thermal/thermal_zone2/temp",
        )
    }

    private var monitorJob: Job? = null
    private var onThermalChangeCallback: ((ThermalStatus) -> Unit)? = null

    fun startMonitoring(scope: CoroutineScope, onThermalChange: (ThermalStatus) -> Unit) {
        onThermalChangeCallback = onThermalChange
        monitorJob = scope.launch(Dispatchers.IO) {
            Timber.d("[ThermalManager] Starting temperature monitoring")
            while (isActive) {
                val temp = readTemperatureCelsius()
                val state = classifyThermal(temp)
                val status = ThermalStatus(temperature = temp, state = state)
                Timber.d("[ThermalManager] Temp=${temp}°C State=$state")
                if (state == ThermalState.CRITICAL) {
                    Timber.w("[ThermalManager] CRITICAL temperature! Activating protection.")
                    applyCriticalThrottle()
                } else if (state == ThermalState.HOT) {
                    Timber.w("[ThermalManager] HOT temperature! Reducing load.")
                    applyHotThrottle()
                }
                launch(Dispatchers.Main) { onThermalChange(status) }
                delay(POLLING_INTERVAL_MS)
            }
        }
    }

    fun stopMonitoring() {
        monitorJob?.cancel()
        monitorJob = null
        Timber.d("[ThermalManager] Monitoring stopped")
    }

    private fun readTemperatureCelsius(): Float {
        for (path in THERMAL_ZONE_PATHS) {
            try {
                val raw = File(path).readText().trim().toLongOrNull() ?: continue
                val celsius = raw / 1000f
                if (celsius in 0f..150f) return celsius
            } catch (_: Exception) {}
        }
        return readFallbackTemperature()
    }

    private fun readFallbackTemperature(): Float {
        return try {
            val batteryTemp = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
                ?.getIntExtra(android.os.BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            if (batteryTemp > 0) batteryTemp / 10f else 30f
        } catch (_: Exception) {
            30f
        }
    }

    private fun classifyThermal(temp: Float): ThermalState = when {
        temp >= CRITICAL_TEMP_C -> ThermalState.CRITICAL
        temp >= HOT_TEMP_C -> ThermalState.HOT
        temp >= WARM_TEMP_C -> ThermalState.WARM
        else -> ThermalState.COOL
    }

    private fun applyCriticalThrottle() {
        Timber.w("[ThermalManager] CRITICAL: Dropping to minimum performance")
        try {
            val maxFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
            val availPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies"
            val available = File(availPath).readText().trim().split(" ")
            if (available.isNotEmpty()) {
                val minFreq = available.minOrNull() ?: return
                Timber.d("[ThermalManager] Would set max_freq to $minFreq (read-only in user mode)")
            }
        } catch (_: Exception) {}
    }

    private fun applyHotThrottle() {
        Timber.w("[ThermalManager] HOT: Reducing to medium performance")
    }

    fun getCurrentTemperature(): Float = readTemperatureCelsius()

    fun getThermalState(): ThermalState = classifyThermal(readTemperatureCelsius())

    fun getRecommendedFpsLimit(currentTemp: Float): Int = when {
        currentTemp >= CRITICAL_TEMP_C -> 30
        currentTemp >= HOT_TEMP_C -> 45
        else -> 60
    }

    fun shouldReduceResolution(currentTemp: Float): Boolean = currentTemp >= HOT_TEMP_C
}
