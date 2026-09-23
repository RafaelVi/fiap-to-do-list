package rafaelvi.com.github.todolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rafaelvi.com.github.todolist.data.Tarefa
import rafaelvi.com.github.todolist.viewmodel.TarefaViewModel

@Composable
fun ListaTarefasScreen(
    viewModel: TarefaViewModel,
    onNovaTarefa: () -> Unit,
    onEditarTarefa: (Int) -> Unit
) {
    val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()

    ListaTarefasContent(
        tarefas = tarefas,
        onNovaTarefa = onNovaTarefa,
        onEditarTarefa = onEditarTarefa,
        onCheckedChange = { tarefa, concluida ->
            viewModel.atualizar(tarefa.copy(concluida = concluida))
        },
        onDeletar = { tarefa -> viewModel.deletar(tarefa) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTarefasContent(
    tarefas: List<Tarefa>,
    onNovaTarefa: () -> Unit,
    onEditarTarefa: (Int) -> Unit,
    onCheckedChange: (Tarefa, Boolean) -> Unit,
    onDeletar: (Tarefa) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Minhas Tarefas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNovaTarefa) {
                Icon(Icons.Default.Add, contentDescription = "Nova tarefa")
            }
        }
    ) { padding ->
        if (tarefas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma tarefa cadastrada.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tarefas, key = { it.id }) { tarefa ->
                    TarefaItem(
                        tarefa = tarefa,
                        onCheckedChange = { concluida -> onCheckedChange(tarefa, concluida) },
                        onEditar = { onEditarTarefa(tarefa.id) },
                        onDeletar = { onDeletar(tarefa) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarefaItem(
    tarefa: Tarefa,
    onCheckedChange: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onDeletar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEditar)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarefa.concluida,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarefa.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else TextDecoration.None
                )
                if (tarefa.descricao.isNotBlank()) {
                    Text(
                        text = tarefa.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            IconButton(onClick = onDeletar) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar tarefa")
            }
        }
    }
}

@Preview(showBackground = true, name = "Lista com tarefas")
@Composable
private fun ListaTarefasContentPreview() {
    ListaTarefasContent(
        tarefas = listOf(
            Tarefa(id = 1, titulo = "Estudar Room", descricao = "Revisar anotações e DAO", concluida = false),
            Tarefa(id = 2, titulo = "Enviar atividade", descricao = "Upload no portal da FIAP", concluida = true)
        ),
        onNovaTarefa = {},
        onEditarTarefa = {},
        onCheckedChange = { _, _ -> },
        onDeletar = {}
    )
}

@Preview(showBackground = true, name = "Lista vazia")
@Composable
private fun ListaTarefasContentVaziaPreview() {
    ListaTarefasContent(
        tarefas = emptyList(),
        onNovaTarefa = {},
        onEditarTarefa = {},
        onCheckedChange = { _, _ -> },
        onDeletar = {}
    )
}

@Preview(showBackground = true, name = "Item pendente")
@Composable
private fun TarefaItemPreview() {
    TarefaItem(
        tarefa = Tarefa(id = 1, titulo = "Estudar Room", descricao = "Revisar anotações e DAO", concluida = false),
        onCheckedChange = {},
        onEditar = {},
        onDeletar = {}
    )
}

@Preview(showBackground = true, name = "Item concluído")
@Composable
private fun TarefaItemConcluidaPreview() {
    TarefaItem(
        tarefa = Tarefa(id = 2, titulo = "Enviar atividade", descricao = "Upload no portal da FIAP", concluida = true),
        onCheckedChange = {},
        onEditar = {},
        onDeletar = {}
    )
}
