package com.example.fasolapp.utils

import android.content.Context
import android.os.Environment
import com.example.fasolapp.data.CompletedTask
import com.example.fasolapp.data.Employee
import com.example.fasolapp.data.Shift
import com.example.fasolapp.viewmodel.StatsViewModel
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {
    fun generateReport(
        context: Context,
        employees: List<Employee>,
        shifts: List<Shift>,
        completedTasks: List<CompletedTask>,
        period: StatsViewModel.Period
    ) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "FasolReport_${System.currentTimeMillis()}.pdf"
        )
        val pdfDoc = PdfDocument(PdfWriter(file))
        val document = Document(pdfDoc)

        document.add(Paragraph("Отчёт по КПД сотрудников"))
        document.add(Paragraph("Период: ${if (period == StatsViewModel.Period.WEEK) "Неделя" else "Месяц"}"))
        document.add(Paragraph("Дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())}"))

        val table = Table(floatArrayOf(200f, 100f, 100f))
        table.addCell("Сотрудник")
        table.addCell("Часы работы")
        table.addCell("Задачи")

        employees.forEach { employee ->
            val employeeShifts = shifts.filter { it.employeeId == employee.id }
            val totalHours = employeeShifts.sumOf { (it.endTime ?: System.currentTimeMillis()) - it.startTime } / (1000 * 60 * 60)
            val tasksCount = completedTasks.count { it.employeeId == employee.id }

            table.addCell(employee.fullName)
            table.addCell(totalHours.toString())
            table.addCell(tasksCount.toString())
        }

        document.add(table)
        document.close()
    }
}