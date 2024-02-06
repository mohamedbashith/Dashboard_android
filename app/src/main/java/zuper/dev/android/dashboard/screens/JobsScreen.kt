package zuper.dev.android.dashboard.screens

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import zuper.dev.android.dashboard.R
import zuper.dev.android.dashboard.commons.CommonBorderStroke
import zuper.dev.android.dashboard.commons.CommonSubTitleStyle
import zuper.dev.android.dashboard.commons.CommonTitleStyle
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.model.TabItem
import zuper.dev.android.dashboard.utils.convertDateStringToReadableFormat
import zuper.dev.android.dashboard.utils.convertToStackList
import zuper.dev.android.dashboard.viewmodel.JobViewModel
import zuper.dev.android.dashboard.views.HorizontalStackedBarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(jobsList: List<JobApiModel>, viewModel: JobViewModel, toDashboard: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "Jobs",
                    style = CommonTitleStyle(fontSize = 25.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 5.dp)
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    toDashboard()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "back", modifier = Modifier
                            .padding(start = 5.dp)
                            .size(25.dp)
                    )
                }
            })
    }) { innerPadding ->
        val swipeRefreshState =
            rememberSwipeRefreshState(isRefreshing = viewModel.isRefreshing.value)
        val refreshedData by viewModel.refreshData.collectAsState()
        var isRefreshed by remember {
            mutableStateOf(false)
        }
        SwipeRefresh(state = swipeRefreshState, onRefresh = {
            isRefreshed = true
            viewModel.refreshList()
        }) {
            val list = if (isRefreshed) refreshedData else jobsList
            Column(modifier = Modifier.padding(innerPadding)) {
                Divider(thickness = 2.dp, color = Color.LightGray)

                val completedJobs = list.filter { it.status == JobStatus.Completed }
                val context = LocalContext.current
                HorizontalStackedBarChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    totalItems = String.format(
                        context.getString(R.string.total_jobs),
                        list.size
                    ),
                    completed = String.format(
                        context.getString(R.string.completed_jobs), completedJobs.size,
                        list.size
                    ),
                    stackedBarItems = list.convertToStackList()
                )
                Divider(
                    thickness = 1.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (!isRefreshed) JobTabLayout(jobsList, context)
                else JobTabLayout(jobsList = refreshedData, context)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JobTabLayout(jobsList: List<JobApiModel>, context: Context) {

    val groupedList = jobsList.groupBy { it.status }
    val inProgressList = groupedList[JobStatus.InProgress]
    val yetToStartList = groupedList[JobStatus.YetToStart]
    val cancelledList = groupedList[JobStatus.Canceled]
    val completedList = groupedList[JobStatus.Completed]
    val inCompletedList = groupedList[JobStatus.Incomplete]
    val tabs = listOf(
        TabItem(
            String.format(
                context.getString(R.string.tab_yet_to_start),
                yetToStartList?.size ?: 0
            )
        ),
        TabItem(
            String.format(
                context.getString(R.string.tab_in_progress),
                inProgressList?.size ?: 0
            )
        ),
        TabItem(String.format(context.getString(R.string.tab_cancelled), cancelledList?.size ?: 0)),
        TabItem(String.format(context.getString(R.string.tab_completed), completedList?.size ?: 0)),
        TabItem(
            String.format(
                context.getString(R.string.tab_in_complete),
                inCompletedList?.size ?: 0
            )
        ),
    )

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        tabs.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }
    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            tabs.forEachIndexed { index, tabItem ->
                Tab(
                    selected = index == selectedTabIndex,
                    unselectedContentColor = Color.Gray,
                    selectedContentColor = Color.Black,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(text = tabItem.title)
                    }
                )
            }
        }

        HorizontalPager(state = pagerState) { index ->
            when (index) {
                0 -> TabListItem(yetToStartList ?: emptyList())
                1 -> TabListItem(inProgressList ?: emptyList())
                2 -> TabListItem(cancelledList ?: emptyList())
                3 -> TabListItem(completedList ?: emptyList())
                4 -> TabListItem(inCompletedList ?: emptyList())
            }
        }
    }
}

@Composable
fun TabListItem(jobList: List<JobApiModel>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(jobList) { job ->
            Card(
                elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 5.dp, start = 15.dp, end = 15.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CommonBorderStroke(), shape = RoundedCornerShape(5.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "#${job.jobNumber}",
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                    Spacer(modifier = Modifier.padding(bottom = 6.dp))
                    Text(
                        text = job.title,
                        style = CommonTitleStyle(),
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                    job.apply {
                        Text(
                            text = convertDateStringToReadableFormat(startTime, endTime), style =
                            CommonSubTitleStyle(color = Color.DarkGray),
                            modifier = Modifier.padding(bottom = 8.dp, start = 10.dp)
                        )
                    }
                }
            }

        }
    }
}