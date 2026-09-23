package rafaelvi.com.github.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import rafaelvi.com.github.todolist.ui.FormularioTarefaScreen
import rafaelvi.com.github.todolist.ui.ListaTarefasScreen
import rafaelvi.com.github.todolist.viewmodel.TarefaViewModel

@Composable
fun AppNavigation(viewModel: TarefaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "lista") {
        composable("lista") {
            ListaTarefasScreen(
                viewModel = viewModel,
                onNovaTarefa = { navController.navigate("formulario/0") },
                onEditarTarefa = { id -> navController.navigate("formulario/$id") }
            )
        }
        composable("formulario/{tarefaId}") { backStackEntry ->
            val tarefaId = backStackEntry.arguments?.getString("tarefaId")?.toInt() ?: 0
            FormularioTarefaScreen(
                viewModel = viewModel,
                tarefaId = tarefaId,
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
