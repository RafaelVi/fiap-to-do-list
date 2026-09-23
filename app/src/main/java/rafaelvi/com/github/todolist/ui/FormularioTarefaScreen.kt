package rafaelvi.com.github.todolist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rafaelvi.com.github.todolist.data.Tarefa
import rafaelvi.com.github.todolist.viewmodel.TarefaViewModel

@Composable
fun FormularioTarefaScreen(
    viewModel: TarefaViewModel,
    tarefaId: Int,
    onVoltar: () -> Unit
) {
    val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()
    val tarefaExistente = remember(tarefas, tarefaId) {
        tarefas.find { it.id == tarefaId }
    }

    FormularioTarefaContent(
        isEdicao = tarefaId != 0,
        tituloInicial = tarefaExistente?.titulo ?: "",
        descricaoInicial = tarefaExistente?.descricao ?: "",
        onSalvar = { titulo, descricao ->
            if (tarefaId == 0) {
                viewModel.inserir(Tarefa(titulo = titulo, descricao = descricao))
            } else {
                tarefaExistente?.let {
                    viewModel.atualizar(it.copy(titulo = titulo, descricao = descricao))
                }
            }
            onVoltar()
        },
        onVoltar = onVoltar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTarefaContent(
    isEdicao: Boolean,
    tituloInicial: String,
    descricaoInicial: String,
    onSalvar: (titulo: String, descricao: String) -> Unit,
    onVoltar: () -> Unit
) {
    var titulo by remember(tituloInicial) { mutableStateOf(tituloInicial) }
    var descricao by remember(descricaoInicial) { mutableStateOf(descricaoInicial) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdicao) "Editar Tarefa" else "Nova Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Button(
                onClick = { onSalvar(titulo.trim(), descricao.trim()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = titulo.isNotBlank()
            ) {
                Text("Salvar")
            }
        }
    }
}

@Preview(showBackground = true, name = "Nova tarefa")
@Composable
private fun FormularioTarefaContentNovaPreview() {
    FormularioTarefaContent(
        isEdicao = false,
        tituloInicial = "",
        descricaoInicial = "",
        onSalvar = { _, _ -> },
        onVoltar = {}
    )
}

@Preview(showBackground = true, name = "Editar tarefa")
@Composable
private fun FormularioTarefaContentEditarPreview() {
    FormularioTarefaContent(
        isEdicao = true,
        tituloInicial = "Estudar Room",
        descricaoInicial = "Revisar anotações e DAO",
        onSalvar = { _, _ -> },
        onVoltar = {}
    )
}
