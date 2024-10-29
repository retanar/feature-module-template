package com.featuremodule.homeImpl.exoplayer

import androidx.media3.exoplayer.ExoPlayer
import com.featuremodule.core.ui.UiEvent
import com.featuremodule.core.ui.UiState

internal data class State(
    val exoplayer: ExoPlayer,
) : UiState

internal sealed interface Event : UiEvent {
}
