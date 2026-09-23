package rafaelvi.com.github.todolist.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class DataHoraUtilTest {

    @Test
    fun formatarDataHoraRetornaTextoNoFormatoBrasileiro() {
        val millis = Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.JULY, 15, 14, 30)
        }.timeInMillis

        assertEquals("15/07/2026 às 14:30", formatarDataHora(millis))
    }

    @Test
    fun extrairDataDoDatePickerLeAnoMesDiaEmUtc() {
        val millisUtc = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.JULY, 15)
        }.timeInMillis

        val (ano, mes, dia) = extrairDataDoDatePicker(millisUtc)

        assertEquals(2026, ano)
        assertEquals(Calendar.JULY, mes)
        assertEquals(15, dia)
    }

    @Test
    fun paraMillisUtcDoDatePickerEExtrairDataDoDatePickerSaoInversas() {
        val millisUtc = paraMillisUtcDoDatePicker(2026, Calendar.JULY, 15)

        val (ano, mes, dia) = extrairDataDoDatePicker(millisUtc)

        assertEquals(2026, ano)
        assertEquals(Calendar.JULY, mes)
        assertEquals(15, dia)
    }

    @Test
    fun combinarDataHoraGeraTimestampComComponentesCorretos() {
        val resultado = combinarDataHora(ano = 2026, mes = Calendar.JULY, dia = 15, hora = 14, minuto = 30)

        val calendario = Calendar.getInstance().apply { timeInMillis = resultado }
        assertEquals(2026, calendario.get(Calendar.YEAR))
        assertEquals(Calendar.JULY, calendario.get(Calendar.MONTH))
        assertEquals(15, calendario.get(Calendar.DAY_OF_MONTH))
        assertEquals(14, calendario.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, calendario.get(Calendar.MINUTE))
    }
}
