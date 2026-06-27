package com.microservicios.asistencia.util.reportes;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.microservicios.asistencia.entidad.Asistencia;
import com.microservicios.asistencia.entidad.EstadoAsistencia;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

public class AsistenciaExporterPDF {

    private final List<Asistencia> asistencias;
    private final String nombreEmpleado;

    public AsistenciaExporterPDF(List<Asistencia> asistencias, String nombreEmpleado) {
        this.asistencias = asistencias;
        this.nombreEmpleado = nombreEmpleado;
    }

    public void exportar(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            fontTitulo.setColor(new Color(102, 126, 234));
            document.add(new Paragraph("Control de Asistencia - " + nombreEmpleado, fontTitulo));
            document.add(new Paragraph(" "));

            if (asistencias == null || asistencias.isEmpty()) {
                document.add(new Paragraph("No hay registros de asistencia.",
                        FontFactory.getFont(FontFactory.HELVETICA, 12)));
            } else {
                PdfPTable tabla = new PdfPTable(4);
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(10);

                String[] headers = {"#", "Fecha", "Hora", "Estado"};
                com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(102, 126, 234));
                    cell.setPadding(5);
                    tabla.addCell(cell);
                }

                com.lowagie.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
                long presentes = 0, tardanzas = 0, faltas = 0;
                int n = 1;
                for (Asistencia a : asistencias) {
                    tabla.addCell(new Phrase(String.valueOf(n++), dataFont));
                    tabla.addCell(new Phrase(a.getFecha() != null ? a.getFecha().toString() : "", dataFont));
                    tabla.addCell(new Phrase(a.getHora() != null ? a.getHora().toString() : "", dataFont));

                    com.lowagie.text.Font estadoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
                    if (a.getEstado() == EstadoAsistencia.PRESENTE) {
                        estadoFont.setColor(new Color(34, 197, 94));
                        presentes++;
                    } else if (a.getEstado() == EstadoAsistencia.TARDANZA) {
                        estadoFont.setColor(new Color(234, 179, 8));
                        tardanzas++;
                    } else {
                        estadoFont.setColor(new Color(239, 68, 68));
                        faltas++;
                    }
                    tabla.addCell(new Phrase(a.getEstado().name(), estadoFont));
                }

                document.add(tabla);

                document.add(new Paragraph(" "));
                com.lowagie.text.Font resumenFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
                document.add(new Paragraph("Total registros: " + asistencias.size(), resumenFont));
                document.add(new Paragraph("Presentes: " + presentes, resumenFont));
                document.add(new Paragraph("Tardanzas: " + tardanzas, resumenFont));
                document.add(new Paragraph("Faltas: " + faltas, resumenFont));
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}
