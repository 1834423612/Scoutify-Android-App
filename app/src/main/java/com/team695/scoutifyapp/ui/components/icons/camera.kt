//https://github.com/tailwindlabs/heroicons/blob/master/LICENSE copyright

package com.team695.scoutifyapp.ui.components.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val VideoCamera: ImageVector
    get() {
        if (_VideoCamera != null) return _VideoCamera!!

        _VideoCamera = ImageVector.Builder(
            name = "VideoCamera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                stroke = SolidColor(Color(0xFF0F172A)),
                strokeLineWidth = 1.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15.75f, 10.5f)
                lineTo(20.4697f, 5.78033f)
                curveTo(20.9421f, 5.30786f, 21.75f, 5.64248f, 21.75f, 6.31066f)
                verticalLineTo(17.6893f)
                curveTo(21.75f, 18.3575f, 20.9421f, 18.6921f, 20.4697f, 18.2197f)
                lineTo(15.75f, 13.5f)
                moveTo(4.5f, 18.75f)
                horizontalLineTo(13.5f)
                curveTo(14.7426f, 18.75f, 15.75f, 17.7426f, 15.75f, 16.5f)
                verticalLineTo(7.5f)
                curveTo(15.75f, 6.25736f, 14.7426f, 5.25f, 13.5f, 5.25f)
                horizontalLineTo(4.5f)
                curveTo(3.25736f, 5.25f, 2.25f, 6.25736f, 2.25f, 7.5f)
                verticalLineTo(16.5f)
                curveTo(2.25f, 17.7426f, 3.25736f, 18.75f, 4.5f, 18.75f)
                close()
            }
        }.build()

        return _VideoCamera!!
    }

private var _VideoCamera: ImageVector? = null

