package zuper.dev.android.dashboard.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.commons.CommonSubTitleStyle
import zuper.dev.android.dashboard.data.model.StackedBarItem

@Composable
fun HorizontalStackedBarChart(
    modifier: Modifier = Modifier,
    stackedBarItems: List<StackedBarItem>,
    totalItems: String = stringResource(R.string.total_items),
    completed: String = stringResource(R.string.completed),
    isExplainNeeded: Boolean = false,
) {
    val maxValue = stackedBarItems.sumOf { it.value.toInt() }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(7.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = totalItems, style = CommonSubTitleStyle(color = Color.Black))
            Text(text = completed, style = CommonSubTitleStyle(color = Color.Black))
        }
        Surface(modifier = modifier.height(20.dp), shape = RoundedCornerShape(7.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startX = 0f

                val sortedItems = stackedBarItems.sortedByDescending { it.value }

                sortedItems.forEach { item ->
                    val width = size.width * (item.value / maxValue)
                    drawRect(
                        color = item.color,
                        size = Size(width, size.height),
                        topLeft = Offset(startX, 0f)
                    )
                    startX += width
                }
            }
        }

        if (isExplainNeeded) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(top = 5.dp),
            ) {
                StackedItem(stackedBarItems)
            }
        } else {
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}
