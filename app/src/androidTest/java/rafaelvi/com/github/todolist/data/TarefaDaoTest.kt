package rafaelvi.com.github.todolist.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TarefaDaoTest {

    private lateinit var database: TarefaDatabase
    private lateinit var dao: TarefaDao

    @Before
    fun criarBanco() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TarefaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.tarefaDao()
    }

    @After
    fun fecharBanco() {
        database.close()
    }

    @Test
    fun inserirTarefaEListar() = runTest {
        val tarefa = Tarefa(titulo = "Estudar Room", descricao = "Aprender Entity e DAO")
        dao.inserir(tarefa)

        val tarefas = dao.listarTodas().first()
        assertEquals(1, tarefas.size)
        assertEquals("Estudar Room", tarefas[0].titulo)
        assertFalse(tarefas[0].concluida)
    }

    @Test
    fun marcarTarefaComoConcluida() = runTest {
        dao.inserir(Tarefa(titulo = "Tarefa 1", descricao = ""))
        val inserida = dao.listarTodas().first().first()

        dao.atualizar(inserida.copy(concluida = true))

        val atualizada = dao.listarTodas().first().first()
        assertTrue(atualizada.concluida)
    }

    @Test
    fun deletarTarefa() = runTest {
        dao.inserir(Tarefa(titulo = "Para deletar", descricao = ""))
        val inserida = dao.listarTodas().first().first()

        dao.deletar(inserida)

        val tarefas = dao.listarTodas().first()
        assertTrue(tarefas.isEmpty())
    }

    @Test
    fun tarefasComPrazoAparecemAntesDeAvulsasEOrdenadasPorProximidade() = runTest {
        val agora = System.currentTimeMillis()
        dao.inserir(Tarefa(titulo = "Avulsa", descricao = ""))
        dao.inserir(Tarefa(titulo = "Prazo distante", descricao = "", dataHora = agora + 100_000))
        dao.inserir(Tarefa(titulo = "Prazo proximo", descricao = "", dataHora = agora + 10_000))

        val tarefas = dao.listarTodas().first()

        assertEquals("Prazo proximo", tarefas[0].titulo)
        assertEquals("Prazo distante", tarefas[1].titulo)
        assertEquals("Avulsa", tarefas[2].titulo)
    }
}
