package zuper.dev.android.dashboard.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.data.model.StackedBarItem

@Composable
fun StackedItem(listItems: List<StackedBarItem>) {

    val totalRows = listItems.size / 2
    val lastRowNeeded = (listItems.size % 2) > 0
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        for (i in 1..totalRows) {
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RowItem(stackItem = listItems[2 * i - 2])
                Spacer(modifier = Modifier.padding(8.dp))
                RowItem(stackItem = listItems[2 * i - 1])
            }
        }
        if (lastRowNeeded)
            Row {
                RowItem(stackItem = listItems[listItems.size - 1])
            }
    }
}


@Composable
fun RowItem(stackItem: StackedBarItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = stringResource(R.string.stack_color),
            tint = stackItem.color,
            modifier = Modifier
                .size(15.dp)
                .padding(end = 5.dp)
        )
        Text(
            text = "${stackItem.title} (${stackItem.value.toInt()})", style = TextStyle(
                fontSize = 13.sp
            ), modifier = Modifier.padding(end = 8.dp)
        )
    }

}