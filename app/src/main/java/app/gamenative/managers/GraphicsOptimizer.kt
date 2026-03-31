package app.gamenative.managers

import android.content.Context
import android.os.Build
import timber.log.Timber

enum class GameType {
    RPG,
    HEAVY_3D,
    INDIE_2D,
    UNKNOWN,
}

data class GraphicsProfile(
    val gameType: GameType,
    val recommendedWidth: Int,
    val recommendedHeight: Int,
    val targetFps: Int,
    val dxvkHud: Boolean,
    val extraWineArgs: String,
    val description: String,
)

class GraphicsOptimizer(private val context: Context) {

    companion object {
        const val UNISOC_T760_TOTAL_RAM_MB = 6000
        const val UNISOC_T760_GPU = "IMG BXE-4-32"
        const val MOTO_G35_SCREEN_WIDTH = 1080
        const val MOTO_G35_SCREEN_HEIGHT = 2400

        private val RPG_KEYWORDS = setOf(
            "rpg", "role", "fantasy", "dragon", "quest", "legend", "souls", "witcher",
            "elden", "final", "zelda", "persona", "stardew", "hades", "path", "divinity",
        )
        private val HEAVY_KEYWORDS = setOf(
            "battlefield", "cyberpunk", "gta", "red dead", "assassin", "cod", "war",
            "doom", "resident", "metro", "control", "forza", "horizon", "watch dogs",
        )
    }

    fun detectGameType(gameName: String): GameType {
        val lower = gameName.lowercase()
        return when {
            RPG_KEYWORDS.any { lower.contains(it) } -> GameType.RPG
            HEAVY_KEYWORDS.any { lower.contains(it) } -> GameType.HEAVY_3D
            else -> GameType.UNKNOWN
        }
    }

    fun getOptimizedProfile(gameName: String, currentTemp: Float = 30f): GraphicsProfile {
        val gameType = detectGameType(gameName)
        return when (gameType) {
            GameType.RPG -> rpgProfile(currentTemp)
            GameType.HEAVY_3D -> heavy3dProfile(currentTemp)
            GameType.INDIE_2D -> indie2dProfile()
            GameType.UNKNOWN -> defaultProfile(currentTemp)
        }.also {
            Timber.d("[GraphicsOptimizer] Game='$gameName' Type=$gameType Profile=${it.description}")
        }
    }

    private fun rpgProfile(temp: Float) = GraphicsProfile(
        gameType = GameType.RPG,
        recommendedWidth = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 800 else 1080,
        recommendedHeight = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 600 else 800,
        targetFps = if (temp >= ThermalOptimizationManager.CRITICAL_TEMP_C) 30 else 45,
        dxvkHud = false,
        extraWineArgs = "-dx11",
        description = "RPG otimizado para Unisoc T760 - balanceado",
    )

    private fun heavy3dProfile(temp: Float) = GraphicsProfile(
        gameType = GameType.HEAVY_3D,
        recommendedWidth = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 640 else 800,
        recommendedHeight = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 480 else 600,
        targetFps = if (temp >= ThermalOptimizationManager.CRITICAL_TEMP_C) 24 else 30,
        dxvkHud = true,
        extraWineArgs = "-dx9",
        description = "Jogo pesado 3D - prioridade estabilidade térmica",
    )

    private fun indie2dProfile() = GraphicsProfile(
        gameType = GameType.INDIE_2D,
        recommendedWidth = 1280,
        recommendedHeight = 720,
        targetFps = 60,
        dxvkHud = false,
        extraWineArgs = "",
        description = "Jogo indie 2D - performance máxima",
    )

    private fun defaultProfile(temp: Float) = GraphicsProfile(
        gameType = GameType.UNKNOWN,
        recommendedWidth = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 800 else 1024,
        recommendedHeight = if (temp >= ThermalOptimizationManager.HOT_TEMP_C) 600 else 768,
        targetFps = if (temp >= ThermalOptimizationManager.CRITICAL_TEMP_C) 30 else 45,
        dxvkHud = false,
        extraWineArgs = "-dx11",
        description = "Perfil padrão Unisoc T760 / Moto G35",
    )

    fun getDeviceInfo(): Map<String, String> = mapOf(
        "chipset" to "Unisoc T760",
        "gpu" to UNISOC_T760_GPU,
        "screen_resolution" to "${MOTO_G35_SCREEN_WIDTH}x${MOTO_G35_SCREEN_HEIGHT}",
        "android_version" to Build.VERSION.RELEASE,
        "sdk_int" to Build.VERSION.SDK_INT.toString(),
        "device" to "${Build.MANUFACTURER} ${Build.MODEL}",
    )

    fun getOptimizedDxvkConfig(profile: GraphicsProfile): String = buildString {
        appendLine("[dxvk]")
        appendLine("# Gamekt Pro - Perfil para Unisoc T760")
        appendLine("dxvk.hud = ${if (profile.dxvkHud) "fps,memory" else "none"}")
        appendLine("dxvk.presentInterval = ${if (profile.targetFps >= 60) 1 else 2}")
        appendLine("dxgi.maxFrameRate = ${profile.targetFps}")
        appendLine("dxgi.maxDeviceMemory = ${UNISOC_T760_TOTAL_RAM_MB / 2}")
        appendLine("dxgi.numBackBuffers = 2")
    }
}
