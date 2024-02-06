package zuper.dev.android.dashboard.commons

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonTitleStyle(fontSize: TextUnit = 16.sp): TextStyle {
    return TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        color = Color.Black
    )
}

@Composable
fun CommonSubTitleStyle(color: Color = Color.LightGray): TextStyle {
    return TextStyle(
        fontSize = 13.sp,
        color = color,
        fontStyle = FontStyle.Normal
    )
}


@Composable
fun CommonBorderStroke(thickness: Dp = 1.dp): BorderStroke =
    BorderStroke(width = thickness, color = Color.LightGray)
