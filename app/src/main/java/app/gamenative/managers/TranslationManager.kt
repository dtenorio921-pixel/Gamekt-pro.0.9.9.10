package app.gamenative.managers

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import timber.log.Timber

class TranslationManager(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        initializeTts()
    }

    private fun initializeTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("pt", "BR"))
                isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
                Timber.d("[TranslationManager] TTS initialized: isReady=$isTtsReady")
            } else {
                Timber.e("[TranslationManager] TTS initialization failed with status $status")
            }
        }
    }

    fun speak(text: String, utteranceId: String = "gamekt_tts") {
        if (!isTtsReady) {
            Timber.w("[TranslationManager] TTS not ready yet")
            return
        }
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun setLocale(locale: Locale) {
        val result = tts?.setLanguage(locale)
        isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    fun setOnDoneListener(onDone: () -> Unit) {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onDone() }
            override fun onError(utteranceId: String?) {}
        })
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isTtsReady = false
    }

    fun simpleTranslate(text: String, targetLocale: Locale): String {
        return if (targetLocale.language == "pt") {
            translateToPtBR(text)
        } else {
            text
        }
    }

    private fun translateToPtBR(text: String): String {
        val translations = mapOf(
            "Play" to "Jogar",
            "Install" to "Instalar",
            "Download" to "Baixar",
            "Settings" to "Configurações",
            "Library" to "Biblioteca",
            "Store" to "Loja",
            "Profile" to "Perfil",
            "Friends" to "Amigos",
            "Update" to "Atualizar",
            "Uninstall" to "Desinstalar",
            "Launch" to "Iniciar",
            "Cancel" to "Cancelar",
            "OK" to "OK",
            "Error" to "Erro",
            "Loading" to "Carregando",
            "Game" to "Jogo",
            "Games" to "Jogos",
            "Login" to "Entrar",
            "Logout" to "Sair",
            "Password" to "Senha",
            "Username" to "Usuário",
        )
        var result = text
        translations.forEach { (en, pt) ->
            result = result.replace(Regex("\\b$en\\b", RegexOption.IGNORE_CASE), pt)
        }
        return result
    }
}

@Composable
fun TranslationOverlayButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val manager = remember { TranslationManager(context) }
    var isSpeaking by remember { mutableStateOf(false) }
    var showOverlay by remember { mutableStateOf(false) }
    val sampleText = "Bem-vindo ao Gamekt Pro! Jogue seus jogos favoritos no Android."

    DisposableEffect(Unit) {
        manager.setOnDoneListener { isSpeaking = false }
        onDispose { manager.release() }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AnimatedVisibility(visible = showOverlay, enter = fadeIn(), exit = fadeOut()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD1A0A00)),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Translate, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                        Text(" Tradução IA + Voz", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = sampleText,
                        color = Color(0xFFEFEFEF),
                        fontSize = 12.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSpeaking) Color(0xFF4A0000) else Color(0xFF2A0A00))
                                .clickable {
                                    if (isSpeaking) {
                                        manager.stop()
                                        isSpeaking = false
                                    } else {
                                        manager.speak(sampleText)
                                        isSpeaking = true
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isSpeaking) Icons.Filled.StopCircle else Icons.Filled.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = if (isSpeaking) Color(0xFFFF5252) else Color(0xFF80CBC4),
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (isSpeaking) "Parar" else "Ler em voz alta",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xCC1A0000))
                .clickable { showOverlay = !showOverlay },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Translate,
                contentDescription = "Tradução / TTS",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
