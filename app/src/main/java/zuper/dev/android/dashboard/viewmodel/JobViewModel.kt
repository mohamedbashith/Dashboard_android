package zuper.dev.android.dashboard.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import zuper.dev.android.dashboard.data.DataRepository
import zuper.dev.android.dashboard.data.model.InvoiceApiModel
import zuper.dev.android.dashboard.data.model.JobApiModel
import zuper.dev.android.dashboard.data.remote.ApiDataSource

class JobViewModel : ViewModel() {

    private val apiSource = ApiDataSource()
    private val dataRepository = DataRepository(apiSource)

    private val _jobsList = mutableStateOf<List<JobApiModel>>(emptyList())
    val jobsList: State<List<JobApiModel>> = _jobsList

    private val _initialJobList = MutableStateFlow(emptyList<JobApiModel>())
    val initialJobList = _initialJobList

    private val _invoicesList = MutableStateFlow(emptyList<InvoiceApiModel>())
    val invoicesList = _invoicesList

    private val _refreshData = MutableStateFlow(emptyList<JobApiModel>())
    val refreshData = _refreshData

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    fun setJobList(jobList: List<JobApiModel>) {
        _jobsList.value = jobList
    }

    private fun getInitialInvoices() =
        viewModelScope.launch {
            dataRepository.observeInvoices().collect {
                _invoicesList.value = it
            }
        }


    private fun getInitialJobs() =
        viewModelScope.launch {
            dataRepository.observeJobs().collect {
                _initialJobList.value = it
            }
        }


    fun refreshList() = viewModelScope.launch {
        _isRefreshing.value = true
        dataRepository.observeJobs().take(1).collect {
            _refreshData.value = it
            _isRefreshing.value = false
        }
    }

    init {
        getInitialJobs()
        getInitialInvoices()
    }

    override fun onCleared() {
        super.onCleared()
        _invoicesList.value = emptyList()
        _initialJobList.value = emptyList()
        _refreshData.value = emptyList()
    }
}