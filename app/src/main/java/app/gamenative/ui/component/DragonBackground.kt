package app.gamenative.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun DragonBackground(
    touchX: Float,
    touchY: Float,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { ctx ->
            DragonBackgroundView(ctx).also {
                if (touchX >= 0f) it.setTouchPosition(touchX, touchY)
            }
        },
        update = { view ->
            if (touchX >= 0f) view.setTouchPosition(touchX, touchY)
        },
        modifier = modifier.fillMaxSize(),
    )
}
