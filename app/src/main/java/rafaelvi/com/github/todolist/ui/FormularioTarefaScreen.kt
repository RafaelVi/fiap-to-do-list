package rafaelvi.com.github.todolist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rafaelvi.com.github.todolist.data.Tarefa
import rafaelvi.com.github.todolist.util.combinarDataHora
import rafaelvi.com.github.todolist.util.extrairDataDoDatePicker
import rafaelvi.com.github.todolist.util.paraMillisUtcDoDatePicker
import rafaelvi.com.github.todolist.viewmodel.TarefaViewModel
import java.util.Calendar

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
        dataHoraInicial = tarefaExistente?.dataHora,
        onSalvar = { titulo, descricao, dataHora ->
            if (tarefaId == 0) {
                viewModel.inserir(Tarefa(titulo = titulo, descricao = descricao, dataHora = dataHora))
            } else {
                tarefaExistente?.let {
                    viewModel.atualizar(it.copy(titulo = titulo, descricao = descricao, dataHora = dataHora))
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
    dataHoraInicial: Long?,
    onSalvar: (titulo: String, descricao: String, dataHora: Long?) -> Unit,
    onVoltar: () -> Unit
) {
    var titulo by remember(tituloInicial) { mutableStateOf(tituloInicial) }
    var descricao by remember(descricaoInicial) { mutableStateOf(descricaoInicial) }
    var temDataHora by remember(dataHoraInicial) { mutableStateOf(dataHoraInicial != null) }

    val calendarioInicial = remember(dataHoraInicial) {
        dataHoraInicial?.let { Calendar.getInstance().apply { timeInMillis = it } }
    }
    var ano by remember(dataHoraInicial) { mutableStateOf(calendarioInicial?.get(Calendar.YEAR)) }
    var mes by remember(dataHoraInicial) { mutableStateOf(calendarioInicial?.get(Calendar.MONTH)) }
    var dia by remember(dataHoraInicial) { mutableStateOf(calendarioInicial?.get(Calendar.DAY_OF_MONTH)) }
    var hora by remember(dataHoraInicial) { mutableStateOf(calendarioInicial?.get(Calendar.HOUR_OF_DAY)) }
    var minuto by remember(dataHoraInicial) { mutableStateOf(calendarioInicial?.get(Calendar.MINUTE)) }

    var mostrarSeletorData by remember { mutableStateOf(false) }
    var mostrarSeletorHora by remember { mutableStateOf(false) }

    if (mostrarSeletorData) {
        val estadoDatePicker = rememberDatePickerState(
            initialSelectedDateMillis = if (ano != null) paraMillisUtcDoDatePicker(ano!!, mes!!, dia!!) else null
        )
        DatePickerDialog(
            onDismissRequest = { mostrarSeletorData = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoDatePicker.selectedDateMillis?.let { millisUtc ->
                        val (a, m, d) = extrairDataDoDatePicker(millisUtc)
                        ano = a
                        mes = m
                        dia = d
                    }
                    mostrarSeletorData = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSeletorData = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoDatePicker)
        }
    }

    if (mostrarSeletorHora) {
        val estadoTimePicker = rememberTimePickerState(
            initialHour = hora ?: 12,
            initialMinute = minuto ?: 0,
            is24Hour = true
        )
        Dialog(onDismissRequest = { mostrarSeletorHora = false }) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePicker(state = estadoTimePicker)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { mostrarSeletorHora = false }) { Text("Cancelar") }
                    TextButton(onClick = {
                        hora = estadoTimePicker.hour
                        minuto = estadoTimePicker.minute
                        mostrarSeletorHora = false
                    }) { Text("OK") }
                }
            }
        }
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Definir data e horário", modifier = Modifier.weight(1f))
                Switch(checked = temDataHora, onCheckedChange = { temDataHora = it })
            }
            if (temDataHora) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { mostrarSeletorData = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (ano != null) String.format("%02d/%02d/%04d", dia, mes!! + 1, ano)
                            else "Selecionar data"
                        )
                    }
                    OutlinedButton(
                        onClick = { mostrarSeletorHora = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            if (hora != null) String.format("%02d:%02d", hora, minuto)
                            else "Selecionar hora"
                        )
                    }
                }
            }
            Button(
                onClick = {
                    val dataHora = if (temDataHora) {
                        combinarDataHora(ano!!, mes!!, dia!!, hora!!, minuto!!)
                    } else {
                        null
                    }
                    onSalvar(titulo.trim(), descricao.trim(), dataHora)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = titulo.isNotBlank() && (!temDataHora || (ano != null && hora != null))
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
        dataHoraInicial = null,
        onSalvar = { _, _, _ -> },
        onVoltar = {}
    )
}

@Preview(showBackground = true, name = "Editar tarefa avulsa")
@Composable
private fun FormularioTarefaContentEditarAvulsaPreview() {
    FormularioTarefaContent(
        isEdicao = true,
        tituloInicial = "Estudar Room",
        descricaoInicial = "Revisar anotações e DAO",
        dataHoraInicial = null,
        onSalvar = { _, _, _ -> },
        onVoltar = {}
    )
}

@Preview(showBackground = true, name = "Editar tarefa com data/hora")
@Composable
private fun FormularioTarefaContentEditarComPrazoPreview() {
    val calendario = Calendar.getInstance().apply { set(2026, Calendar.JULY, 15, 14, 30) }
    FormularioTarefaContent(
        isEdicao = true,
        tituloInicial = "Entregar atividade",
        descricaoInicial = "Upload no portal da FIAP",
        dataHoraInicial = calendario.timeInMillis,
        onSalvar = { _, _, _ -> },
        onVoltar = {}
    )
}
