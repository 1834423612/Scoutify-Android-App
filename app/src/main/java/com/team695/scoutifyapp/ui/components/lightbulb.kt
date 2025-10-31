//https://github.com/tailwindlabs/heroicons/blob/master/LICENSE copyright

package com.team695.scoutifyapp.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LightBulb: ImageVector
    get() {
        if (_LightBulb != null) return _LightBulb!!

        _LightBulb = ImageVector.Builder(
            name = "LightBulb",
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
                moveTo(12f, 18f)
                verticalLineTo(12.75f)
                moveTo(12f, 12.75f)
                curveTo(12.5179f, 12.75f, 13.0206f, 12.6844f, 13.5f, 12.561f)
                moveTo(12f, 12.75f)
                curveTo(11.4821f, 12.75f, 10.9794f, 12.6844f, 10.5f, 12.561f)
                moveTo(14.25f, 20.0394f)
                curveTo(13.5212f, 20.1777f, 12.769f, 20.25f, 12f, 20.25f)
                curveTo(11.231f, 20.25f, 10.4788f, 20.1777f, 9.75f, 20.0394f)
                moveTo(13.5f, 22.422f)
                curveTo(13.007f, 22.4736f, 12.5066f, 22.5f, 12f, 22.5f)
                curveTo(11.4934f, 22.5f, 10.993f, 22.4736f, 10.5f, 22.422f)
                moveTo(14.25f, 18f)
                verticalLineTo(17.8083f)
                curveTo(14.25f, 16.8254f, 14.9083f, 15.985f, 15.7585f, 15.4917f)
                curveTo(17.9955f, 14.1938f, 19.5f, 11.7726f, 19.5f, 9f)
                curveTo(19.5f, 4.85786f, 16.1421f, 1.5f, 12f, 1.5f)
                curveTo(7.85786f, 1.5f, 4.5f, 4.85786f, 4.5f, 9f)
                curveTo(4.5f, 11.7726f, 6.00446f, 14.1938f, 8.24155f, 15.4917f)
                curveTo(9.09173f, 15.985f, 9.75f, 16.8254f, 9.75f, 17.8083f)
                verticalLineTo(18f)
            }
        }.build()

        return _LightBulb!!
    }

private var _LightBulb: ImageVector? = null

