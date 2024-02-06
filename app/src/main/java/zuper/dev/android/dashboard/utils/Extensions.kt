package zuper.dev.android.dashboard.utils

import androidx.compose.ui.graphics.Color
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.InvoiceStatus
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.model.JobStatus
import zuper.dev.android.dashboard.data.model.StackedBarItem
import zuper.dev.android.dashboard.data.model.TabItem
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun JobStatus.getColor(): Color {
    return when (this) {
        JobStatus.Completed -> "#74caa5".colorFromHex()
        JobStatus.YetToStart -> "#9ea8db".colorFromHex()
        JobStatus.InProgress -> "#81d5fa".colorFromHex()
        JobStatus.Canceled -> "#fcce57".colorFromHex()
        JobStatus.Incomplete -> "#e57373".colorFromHex()
    }
}

fun InvoiceStatus.getColor(): Color {
    return when (this) {
        InvoiceStatus.BadDebt -> "#e57373".colorFromHex()
        InvoiceStatus.Paid -> "#74caa5".colorFromHex()
        InvoiceStatus.Draft -> "#fcce57".colorFromHex()
        InvoiceStatus.Pending -> "#81d5fa".colorFromHex()
    }
}

fun String.colorFromHex(): Color = Color(android.graphics.Color.parseColor(this))

fun List<InvoiceApiModel>.convertInvoiceToStackList(): List<StackedBarItem> {
    val groupedList = this.groupBy { it.status }
    val badDebtList = StackedBarItem(
        color = InvoiceStatus.BadDebt.getColor(),
        value = groupedList[InvoiceStatus.BadDebt]?.size?.toFloat() ?: 0F,
        title = InvoiceStatus.BadDebt.name
    )
    val paidList = StackedBarItem(
        color = InvoiceStatus.Paid.getColor(),
        value = groupedList[InvoiceStatus.Paid]?.size?.toFloat() ?: 0F,
        title = InvoiceStatus.Paid.name
    )
    val draftList = StackedBarItem(
        color = InvoiceStatus.Draft.getColor(),
        value = groupedList[InvoiceStatus.Draft]?.size?.toFloat() ?: 0F,
        title = InvoiceStatus.Draft.name
    )
    val pendingList = StackedBarItem(
        color = InvoiceStatus.Pending.getColor(),
        value = groupedList[InvoiceStatus.Pending]?.size?.toFloat() ?: 0F,
        title = InvoiceStatus.Pending.name
    )

    return listOf(badDebtList, paidList, draftList, pendingList)
}

fun List<JobApiModel>.convertToStackList(): List<StackedBarItem> {
    val groupedList = this.groupBy { it.status }
    val completedList = StackedBarItem(
        color = JobStatus.Completed.getColor(),
        value = groupedList[JobStatus.Completed]?.size?.toFloat() ?: 0F,
        title = JobStatus.Completed.name
    )

    val inProgressList = StackedBarItem(
        color = JobStatus.InProgress.getColor(),
        value = groupedList[JobStatus.InProgress]?.size?.toFloat() ?: 0F,
        title = JobStatus.InProgress.name
    )

    val canceledList = StackedBarItem(
        color = JobStatus.Canceled.getColor(),
        value = groupedList[JobStatus.Canceled]?.size?.toFloat() ?: 0F,
        title = JobStatus.Canceled.name
    )

    val inCompleteList = StackedBarItem(
        color = JobStatus.Incomplete.getColor(),
        value = groupedList[JobStatus.Incomplete]?.size?.toFloat() ?: 0F,
        title = JobStatus.Incomplete.name
    )

    val yetToStartList = StackedBarItem(
        color = JobStatus.YetToStart.getColor(),
        value = groupedList[JobStatus.YetToStart]?.size?.toFloat() ?: 0F,
        title = JobStatus.YetToStart.name
    )

    return listOf(yetToStartList, inProgressList, canceledList, completedList, inCompleteList)
}

fun List<JobApiModel>.splitForTabs(): List<TabItem> {
    val groupedList = this.groupBy { it.status }
    val inProgressList = groupedList[JobStatus.InProgress]
    val yetToStartList = groupedList[JobStatus.YetToStart]
    val cancelledList = groupedList[JobStatus.Canceled]
    val completedList = groupedList[JobStatus.Completed]
    val inCompletedList = groupedList[JobStatus.Incomplete]
    return listOf(
        TabItem("Yet to Start (${yetToStartList?.size})"),
        TabItem("InProgress (${inProgressList?.size})"),
        TabItem("Cancelled (${cancelledList?.size})"),
        TabItem("Completed (${completedList?.size})"),
        TabItem("In-Complete (${inCompletedList?.size})"),
    )
}

fun convertDateStringToReadableFormat(dateString1: String, dateString2: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

    val date1: LocalDateTime = LocalDateTime.parse(dateString1, formatter)
    val date2: LocalDateTime = LocalDateTime.parse(dateString2, formatter)
    val currentDate = LocalDateTime.now(ZoneId.systemDefault())

    return if (isToday(date1, currentDate) && isToday(date2, currentDate)) {
        val outputFormatToday = DateTimeFormatter.ofPattern("Today, hh:mm a", Locale.getDefault())
        val startTime = outputFormatToday.format(date1)
        val endTime = outputFormatToday.format(date2)
        "Today, $startTime - $endTime"
    } else {
        val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a", Locale.getDefault())
        "${outputFormat.format(date1)} - ${outputFormat.format(date2)}"
    }
}

fun isToday(date: LocalDateTime, currentDate: LocalDateTime): Boolean {
    return date.toLocalDate() == currentDate.toLocalDate()
}