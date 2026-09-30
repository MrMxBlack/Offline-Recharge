package com.rohan.offlineresearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private var importError by mutableStateOf<String?>(null)
    private var refreshToken by mutableIntStateOf(0)
    private val pickCorpus = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching { CorpusManager(this).importJson(uri) }
            .onSuccess { refreshToken++ }
            .onFailure { importError = "Corpus import failed: ${it.message ?: it::class.simpleName}" }
    }
    private val pickModel = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) runCatching { ModelManager(this).importModel(uri) }
            .onSuccess { refreshToken++ }
            .onFailure { importError = "Model import failed: ${it.message ?: it::class.simpleName}" }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { OfflineResearchScreen(viewModel(), { importError = null; pickModel.launch(arrayOf("application/octet-stream", "*/*")) }, { importError = null; pickCorpus.launch(arrayOf("application/json", "text/plain", "*/*")) }, importError, refreshToken) } }
    }
}

@Composable
private fun OfflineResearchScreen(vm: ResearchViewModel, onPickModel: () -> Unit, onPickCorpus: () -> Unit, importError: String?, refreshToken: Int) {
    val state by vm.state.collectAsState()
    var query by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf<ResearchType?>(null) }
    val context = LocalContext.current
    val manager = remember(refreshToken) { ModelManager(context) }
    val corpusManager = remember(refreshToken) { CorpusManager(context) }
    val labels = listOf(null to "Auto", ResearchType.SYNTHESIS to "Synthesis", ResearchType.COMPARISON to "Compare", ResearchType.MULTI_HOP to "Multi-hop", ResearchType.TIMELINE to "Timeline")

    Scaffold(topBar = { TopAppBar(title = { Text("Offline Research") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("NO NETWORK PERMISSION") })
                AssistChip(onClick = {}, label = { Text(if (manager.hasModel()) "LOCAL GGUF" else "NO MODEL") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onPickModel) { Text("Import GGUF") }
                Text(if (manager.hasModel()) "Model: ${manager.sizeBytes() / 1_000_000} MB" else "Import a verified GGUF", Modifier.padding(top = 12.dp))
            }
            if (manager.hasModel()) Text("SHA-256: ${manager.sha256().take(16)}…", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPickCorpus) { Text("Import Corpus JSON") }
                Text(if (corpusManager.hasImportedCorpus()) "Custom corpus: ${corpusManager.corpusFile().length() / 1_000_000} MB" else "Demo corpus", Modifier.padding(top = 12.dp))
            }
            importError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            Text("Managed storage: ${manager.totalManagedBytes() / (1024 * 1024)} MB / 51200 MB", style = MaterialTheme.typography.bodySmall)

            Text("Research mode", style = MaterialTheme.typography.labelLarge)
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                labels.forEach { (value, label) -> FilterChip(selected = mode == value, onClick = { mode = value }, label = { Text(label) }) }
            }

            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text("Ask a research question…") }, minLines = 3)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = query.isNotBlank() && !state.running, onClick = { vm.ask(query, mode) }) { Text(if (state.running) "Researching…" else "Research") }
                OutlinedButton(onClick = vm::clear) { Text("Clear") }
            }
            Text(state.status, style = MaterialTheme.typography.bodySmall)
            Text("Runtime network proof: NOT VERIFIED • verify on a physical device", style = MaterialTheme.typography.labelSmall)
            state.metrics?.let { Text("Retrieval ${it.retrievalMs} ms • total ${it.totalMs} ms • PSS ${it.pssMb} MB • native=${it.nativeInference}", style = MaterialTheme.typography.bodySmall) }
            HorizontalDivider()
            LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { if (state.answer.isNotBlank()) Card(Modifier.fillMaxWidth()) { Text(state.answer, Modifier.padding(14.dp)) } }
                items(state.sources) { s ->
                    Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp)) { Text("[${s.id}] ${s.title}", style = MaterialTheme.typography.titleSmall); Text(s.snippet, style = MaterialTheme.typography.bodySmall) } }
                }
            }
        }
    }
}
