package com.featuremodule.featureAImpl.ui

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.featuremodule.core.ui.BaseVM
import com.featuremodule.featureAApi.FeatureADestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FeatureVM @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseVM<State, Event>() {
    init {
        Log.d("FeatureA", "Recreated VM")
        // ViewModel can get arguments from SavedStateHandle, but Screen cannot
        launch {
            savedStateHandle
                .getStateFlow(FeatureADestination.ARG_NUM, 0)
                .collect { savedArgNum ->
                    Log.d("FeatureA", "ViewModel's savedArgNum = $savedArgNum")
                    setState {
                        copy(argNum = savedArgNum)
                    }
                }
        }
    }

    override fun initialState() = State()

    override fun handleEvent(event: Event) {
        TODO()
    }
}
