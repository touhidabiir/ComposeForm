package com.touhid.composeform.flow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.formbuilder.fieldsWithOptionsUrl
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.withOptions
import kotlinx.coroutines.launch

sealed interface FormFlowState {
    data object Loading : FormFlowState
    data class Error(val message: String) : FormFlowState
    data class Page(
        val schema: FormSchema,
        val submitUrl: String?,
        val nextFormUrl: String?,
        val initialValues: Map<String, FormValue> = emptyMap(),
    ) : FormFlowState
    data object Completed : FormFlowState
}

private data class HistoryEntry(val url: String, val page: FormFlowState.Page)

class FormFlowViewModel : ViewModel() {

    var state by mutableStateOf<FormFlowState>(FormFlowState.Loading)
        private set

    private val accumulated = mutableMapOf<String, FormValue>()
    private var currentUrl = DemoFormApi.START_URL
    private val backStack = ArrayDeque<HistoryEntry>()

    val canGoBack: Boolean get() = backStack.isNotEmpty()

    init {
        loadPage(currentUrl)
    }

    fun retry() = loadPage(currentUrl)

    fun goBack(): Boolean {
        val entry = backStack.removeLastOrNull() ?: return false
        currentUrl = entry.url
        state = entry.page
        return true
    }

    private fun loadPage(url: String) {
        currentUrl = url
        state = FormFlowState.Loading
        viewModelScope.launch {
            runCatching {
                val response = DemoFormApi.fetchPage(url)
                val schema = response.schema.fieldsWithOptionsUrl().fold(response.schema) { schema, field ->
                    schema.withOptions(field.key, DemoFormApi.fetchOptions(field.optionsUrl))
                }
                FormFlowState.Page(schema, response.submitUrl, response.nextFormUrl)
            }.onSuccess { state = it }
                .onFailure { state = FormFlowState.Error(it.message ?: "Failed to load form") }
        }
    }

    // Each page's submitUrl decides whether the data collected so far goes out now (and the
    // accumulator resets) or keeps riding along to the next page - not a hardcoded page index.
    fun onPageSubmit(values: Map<String, FormValue>) {
        val page = state as? FormFlowState.Page ?: return
        val pageUrl = currentUrl
        accumulated += values
        viewModelScope.launch {
            val submitUrl = page.submitUrl
            if (submitUrl != null) {
                val snapshot = accumulated.toMap()
                val result = runCatching { DemoFormApi.submit(submitUrl, snapshot) }
                if (result.isFailure) {
                    state = FormFlowState.Error(result.exceptionOrNull()?.message ?: "Submit failed")
                    return@launch
                }
                accumulated.clear()
            }
            val nextFormUrl = page.nextFormUrl
            if (nextFormUrl != null) {
                backStack.addLast(HistoryEntry(pageUrl, page.copy(initialValues = values)))
                loadPage(nextFormUrl)
            } else {
                state = FormFlowState.Completed
            }
        }
    }
}
