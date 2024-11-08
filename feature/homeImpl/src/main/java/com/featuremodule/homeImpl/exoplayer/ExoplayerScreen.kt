package com.featuremodule.homeImpl.exoplayer

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.featuremodule.core.util.getActivity
import com.featuremodule.homeImpl.R
import kotlin.time.Duration.Companion.milliseconds

@OptIn(UnstableApi::class)
@Composable
internal fun ExoplayerScreen(viewModel: ExoplayerVM = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    val exoplayer = state.exoplayer

    // Hide system bars just for this dialog
    DisposableEffect(Unit) {
        val window = context.getActivity()?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)

        insetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            insetsController.apply {
                show(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    var overlayVisibility by rememberSaveable { mutableStateOf(true) }
    var videoSize by remember { mutableStateOf(IntSize(0, 0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            // Indication is not needed and it does not look good
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                overlayVisibility = !overlayVisibility
            },
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    background = context.getDrawable(android.R.color.black)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                    player = exoplayer
                }
            },
            modifier = Modifier.onSizeChanged { intSize ->
                videoSize = intSize
            },
        )

        Overlay(
            state = state.overlayState,
            isVisible = overlayVisibility,
            onPlayPauseClick = { viewModel.postEvent(Event.OnPlayPauseClick) },
            onSeek = { viewModel.postEvent(Event.OnSeekFinished(it)) },
            modifier = Modifier.size(
                with(LocalDensity.current) {
                    DpSize(
                        width = videoSize.width.toDp(),
                        height = videoSize.height.toDp(),
                    )
                },
            ),
        )
    }
}

@Composable
private fun Overlay(
    state: OverlayState,
    isVisible: Boolean,
    onPlayPauseClick: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = modifier.background(Color.Black.copy(alpha = 0.5f)),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    text = state.title,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp),
                )
            }

            // Center
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                IconButton(
                    onClick = onPlayPauseClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White,
                    ),
                ) {
                    if (state.showPlayButton) {
                        Icon(painterResource(id = R.drawable.play), contentDescription = null)
                    } else {
                        Icon(painterResource(id = R.drawable.pause), contentDescription = null)
                    }
                }
            }

            // Bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                var isValueChanging by remember { mutableStateOf(false) }
                var seekPosition by remember { mutableFloatStateOf(0f) }

                Text(
                    text = "${formatMs(state.contentPosition)}/${formatMs(state.contentDuration)}",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 8.dp),
                )

                Slider(
                    value = if (isValueChanging) {
                        seekPosition
                    } else {
                        state.contentPosition.toFloat()
                    },
                    onValueChange = {
                        seekPosition = it
                        isValueChanging = true
                    },
                    valueRange = 0f..state.contentDuration.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    onValueChangeFinished = {
                        onSeek(seekPosition.toLong())
                        isValueChanging = false
                    },
                )
            }
        }
    }
}

fun formatMs(ms: Long): String {
    return ms.milliseconds.toComponents { hours, minutes, seconds, _ ->
        val secondsString = "%02d".format(seconds)
        if (hours == 0L) {
            "$minutes:$secondsString"
        } else {
            val minutesString = "%02d".format(minutes)
            "$hours:$minutesString:$secondsString"
        }
    }
}
