package com.featuremodule.homeImpl

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.featuremodule.core.navigation.HIDE_NAV_BAR
import com.featuremodule.homeApi.HomeDestination
import com.featuremodule.homeImpl.barcode.BarcodeCameraScreen
import com.featuremodule.homeImpl.barcode.BarcodeResultScreen
import com.featuremodule.homeImpl.camera.TakePhotoScreen
import com.featuremodule.homeImpl.exoplayer.ExoplayerScreen
import com.featuremodule.homeImpl.imageUpload.ImageUploadScreen
import com.featuremodule.homeImpl.ui.HomeScreen

fun NavGraphBuilder.registerHome() {
    composable(HomeDestination.ROUTE) { backStackEntry ->
        HomeScreen(route = backStackEntry.destination.route)
    }

    composable(InternalRoutes.ExoplayerDestination.ROUTE) {
        ExoplayerScreen()
    }

    composable(InternalRoutes.ImageUploadDestination.ROUTE) { backStack ->
        val bitmap by backStack.savedStateHandle
            .getStateFlow<Bitmap?>(InternalRoutes.ImageUploadDestination.BITMAP_POP_ARG, null)
            .collectAsStateWithLifecycle()
        ImageUploadScreen(returnedBitmap = bitmap)
    }

    composable(InternalRoutes.TakePhotoDestination.ROUTE) {
        TakePhotoScreen()
    }

    composable(InternalRoutes.BarcodeCameraDestination.ROUTE) {
        BarcodeCameraScreen()
    }

    composable(
        InternalRoutes.BarcodeResultDestination.ROUTE,
        InternalRoutes.BarcodeResultDestination.arguments,
    ) {
        BarcodeResultScreen()
    }
}

internal class InternalRoutes {
    object ExoplayerDestination {
        const val ROUTE = HIDE_NAV_BAR + "exoplayer"

        fun constructRoute() = ROUTE
    }

    object ImageUploadDestination {
        const val ROUTE = "image_upload"
        const val BITMAP_POP_ARG = "bitmap"

        fun constructRoute() = ROUTE
    }

    object TakePhotoDestination {
        const val ROUTE = HIDE_NAV_BAR + "take_photo"

        fun constructRoute() = ROUTE
    }

    object BarcodeCameraDestination {
        const val ROUTE = HIDE_NAV_BAR + "barcode"

        fun constructRoute() = ROUTE
    }

    object BarcodeResultDestination {
        const val ARG_BARCODE = "barcode"
        const val ROUTE = "barcode_result/{$ARG_BARCODE}"

        val arguments = listOf(
            navArgument(ARG_BARCODE) { type = NavType.StringType },
        )

        fun constructRoute(barcodeValue: String) = "barcode_result/$barcodeValue"
    }
}
