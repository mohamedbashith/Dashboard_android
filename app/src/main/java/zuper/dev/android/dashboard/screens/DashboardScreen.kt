package zuper.dev.android.dashboard.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.commons.CommonBorderStroke
import zuper.dev.android.dashboard.commons.CommonSubTitleStyle
import zuper.dev.android.dashboard.commons.CommonTitleStyle
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.utils.convertInvoiceToStackList
import zuper.dev.android.dashboard.utils.convertToStackList
import zuper.dev.android.dashboard.views.HorizontalStackedBarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    jobsList: List<JobApiModel>,
    invoiceList: List<InvoiceApiModel>,
    navRoute: (jobsList: List<JobApiModel>) -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Dashboard", style = CommonTitleStyle(fontSize = 25.sp)) })
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Divider(thickness = 2.dp, color = Color.LightGray)
            UserDetail()
            JobDetails(navRoute, jobsList)
            InvoiceStats(invoiceList)
        }
    }
}

@Composable
fun UserDetail() {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CommonBorderStroke()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val context = LocalContext.current
            Column {
                Text(
                    text = context.getString(R.string.sample_name),
                    style = CommonTitleStyle(fontSize = 20.sp)
                )
                Text(
                    text = "Monday Feb 5 2024", style = CommonSubTitleStyle()
                )
            }
            Image(
                painter = painterResource(id = R.drawable.profile_pic),
                contentScale = ContentScale.Crop,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(width = 50.dp, height = 50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .border(CommonBorderStroke(thickness = 3.dp))

            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetails(routeToJobScreen: (jobsList: List<JobApiModel>) -> Unit, jobsList: List<JobApiModel>) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CommonBorderStroke(),
        onClick = {
            routeToJobScreen(jobsList)
        }
    ) {
        Column {
            val context = LocalContext.current
            Text(
                text = context.getString(R.string.title_job),
                style = CommonTitleStyle(),
                modifier = Modifier.padding(all = 15.dp)
            )
            Divider(thickness = 2.dp, color = Color.LightGray)

            HorizontalStackedBarChart(
                stackedBarItems = jobsList.convertToStackList(), modifier = Modifier.padding(
                    top = 5.dp, start = 10.dp, end = 10.dp
                ), isExplainNeeded = true,
                totalItems = String.format(context.getString(R.string.total_jobs), jobsList.size),
                completed = String.format(
                    context.getString(R.string.completed_jobs),
                    jobsList.filter { it.status == JobStatus.Completed }.size,
                    jobsList.size
                )
            )
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
fun InvoiceStats(invoiceList: List<InvoiceApiModel>) {
    Card(
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CommonBorderStroke()
    ) {
        Column {
            val context = LocalContext.current
            Text(
                text = context.getString(R.string.title_invoice),
                style = CommonTitleStyle(),
                modifier = Modifier.padding(all = 15.dp)
            )
            Divider(thickness = 2.dp, color = Color.LightGray)

            val totalAmountAndPaid = invoiceList.totalAmountAndPaid(context)
            HorizontalStackedBarChart(
                modifier = Modifier.padding(
                    top = 5.dp, start = 10.dp, end = 10.dp
                ),
                stackedBarItems = invoiceList.convertInvoiceToStackList(),
                isExplainNeeded = true,
                totalItems = totalAmountAndPaid.first,
                completed = totalAmountAndPaid.second
            )
            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

private fun List<InvoiceApiModel>.totalAmountAndPaid(context: Context): Pair<String, String> {
    var total = 0
    this.forEach { invoice ->
        total += invoice.total
    }
    val paid: Int = this.filter { it.status == InvoiceStatus.Paid }.sumOf { it.total }
    return Pair(
        String.format(context.getString(R.string.total_value), total),
        String.format(context.getString(R.string.collected), paid)
    )
}