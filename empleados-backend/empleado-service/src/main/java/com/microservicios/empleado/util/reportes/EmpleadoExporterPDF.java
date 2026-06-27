package com.microservicios.empleado.util.reportes;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.microservicios.empleado.entidad.Empleado;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

public class EmpleadoExporterPDF {

    private final List<Empleado> empleados;

    public EmpleadoExporterPDF(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    public void exportar(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4.rotate());
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            com.lowagie.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            fontTitulo.setColor(new Color(102, 126, 234));
            document.add(new Paragraph("Listado de Empleados", fontTitulo));

            if (empleados == null || empleados.isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("No hay empleados registrados.",
                        FontFactory.getFont(FontFactory.HELVETICA, 12)));
            } else {
                PdfPTable tabla = new PdfPTable(9);
                tabla.setWidthPercentage(100);
                tabla.setSpacingBefore(15);

                String[] headers = {"ID", "Nombre", "Apellido", "Email", "Telefono", "Sexo", "Cargo", "Departamento", "Salario"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE)));
                    cell.setBackgroundColor(new Color(102, 126, 234));
                    cell.setPadding(5);
                    tabla.addCell(cell);
                }

                com.lowagie.text.Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 7);
                for (Empleado e : empleados) {
                    tabla.addCell(new Phrase(String.valueOf(e.getId()), fontData));
                    tabla.addCell(new Phrase(e.getNombre(), fontData));
                    tabla.addCell(new Phrase(e.getApellido(), fontData));
                    tabla.addCell(new Phrase(e.getEmail(), fontData));
                    tabla.addCell(new Phrase(e.getTelefono(), fontData));
                    tabla.addCell(new Phrase(e.getSexo(), fontData));
                    tabla.addCell(new Phrase(e.getCargo(), fontData));
                    tabla.addCell(new Phrase(e.getDepartamento(), fontData));
                    tabla.addCell(new Phrase("S/ " + e.getSalario(), fontData));
                }
                document.add(tabla);
            }

            document.close();
        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        }
    }
}
