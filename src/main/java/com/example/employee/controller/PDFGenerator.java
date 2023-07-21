package com.example.employee.controller;

import java.util.List;
 
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PDFGenerator {

    protected Map<String, Object>  saleInfo;
    protected final String SALON = "salon";
    protected final String COCINA = "cocina";

    public PDFGenerator( Map<String, Object> saleInfo) throws IOException {
        this.saleInfo = saleInfo;
    }

    public void createPDFKitchen(String printerName) throws IOException, PrinterException {
        printPDF(printerName);
    }

    protected void printPDF( String printerName ) throws PrinterException, IOException {
        PrintService printService = findPrintService(printerName);
   

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(printService);
        // Crear una instancia de Paper para establecer el tamaño de la página
        Paper paper = new Paper();

        
        paper.setSize(200, 500); // Establecer el tamaño de la página en puntos (1 punto = 1/72 de pulgada)

        // Establecer el tamaño de la imagen imprimible dentro de la página
        double scale = 1; // Escala personalizada (por ejemplo, 1.5 significa aumentar el tamaño en un 50%)
        double imageableWidth = paper.getWidth() * scale;
        double imageableHeight = paper.getHeight() * scale;
        double imageableX = (paper.getWidth() - imageableWidth) / 2;
        double imageableY = (paper.getHeight() - imageableHeight) / 2;
        paper.setImageableArea(imageableX, imageableY, imageableWidth, imageableHeight);

        // Establecer el Paper en la PageFormat
        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);
    
        /* Version actuliazada x */
        String place = (String) saleInfo.get("place");

        printerJob.setPrintable((graphics, pf, pageIndex) -> {
        if (pageIndex == 0) { // Solo dibujar en la primera página (pageIndex = 0)
        Graphics2D g2d = (Graphics2D) graphics;

        // Obtener las dimensiones de la página escalada
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // Establecer la fuente y el tamaño de la letra para el texto "Cliente: customer"
        String cliente = (String)saleInfo.get("cliente");
        Font font12 = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(font12);
        String customerText = "Cliente: " + cliente;
        g2d.drawString(customerText, 0, font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "username: {user}   fecha: {fechaActual}"
        Font font12Bold = new Font("Arial", Font.PLAIN, 10);
        g2d.setFont(font12Bold);
        String user = (String)saleInfo.get("username");
       // String dateCurrently = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        LocalDateTime dateTimeCurrently = LocalDateTime.now();
        String dateCurrently = dateTimeCurrently.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        //String userInfoText = "username: " + user + "   fecha: " + dateCurrently;
        String userInfoText =  user + "   fecha: " + dateCurrently;
        g2d.drawString(userInfoText, 0, 2 * font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "Mesa: 1     Nro: 323"
        Font font16 = new Font("Arial", Font.PLAIN, 16);
        g2d.setFont(font16);
        String table = (String)saleInfo.get("nroMesa");
        int nroPedido = (Integer)saleInfo.get("nroPedido");
        String mesaText = "Mesa: " + table;
        if(place.equals(COCINA)){
            mesaText += " Nro: " + nroPedido;
        }
        g2d.drawString(mesaText, 0, 3 * font16.getSize());

        // Establecer la fuente y el tamaño de la letra para la tabla
        Font fontTable = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTable);

        // Definir los datos de la tabla
        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");
        String[][] data = new String[orders.size() + 1][3];
        data[0] = new String[]{"Nombre", "Can.", "Precio"};
        for (int i = 0; i < orders.size(); i++) {
            Map<String, Object> order = orders.get(i);
            String nombre = (String) order.get("nombre");
            int cantidad = (int) order.get("cantidad");
            double precioUnitario = (Integer) order.get("precio_venta");
            double precioTotal = cantidad * precioUnitario;
            data[i + 1] = new String[] {nombre, String.valueOf(cantidad), String.valueOf(precioTotal)};
        }

        // Calcular el ancho máximo de cada columna
        int[] columnWidths = new int[data[0].length];
        for (int i = 0; i < data[0].length; i++) {
            int maxWidth = 0;
            for (int j = 0; j < data.length; j++) {
                int valueWidth = g2d.getFontMetrics().stringWidth(data[j][i]);
                if (valueWidth > maxWidth) {
                    maxWidth = valueWidth;
                }
            }
            columnWidths[i] = maxWidth;
        }

        // Calcular el ancho total de la tabla
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width;
        }

        // Ajustar el ancho de las columnas proporcionalmente para que encajen dentro de la tabla
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = (int) ((double) columnWidths[i] / totalWidth * (pageWidth)); // Ajuste de posición en X
        }

        // Calcular la posición para dibujar la tabla
        int x = 0; // Ajuste de posición en X
        int y = 6 * fontTable.getSize();

        // Dibujar los encabezados de la tabla
        int currentX = x;
        for (int i = 0; i < data[0].length; i++) {
            String header = data[0][i];
            g2d.drawString(header, currentX, y);
            currentX += columnWidths[i];
        }

        // Dibujar las filas de la tabla
        int currentY = y + fontTable.getSize();
        for (int i = 1; i < data.length; i++) {
            currentX = x;
            for (int j = 0; j < data[i].length; j++) {
                String value = data[i][j];
                g2d.drawString(value, currentX, currentY);
                currentX += columnWidths[j];
            }
            currentY += fontTable.getSize();
        }

        // Establecer la fuente y el tamaño de la letra para el texto "Total=154"
        if(place.equals(SALON)){
            Font fontTotal = new Font("Arial", Font.PLAIN, 12);
            g2d.setFont(fontTotal);
            String totalText = "Total " + saleInfo.get("cantidadTotal");
            int totalWidth2 = g2d.getFontMetrics().stringWidth(totalText);
            int totalX = (int) (pageWidth - totalWidth2);
            g2d.drawString(totalText, totalX, (int) (currentY + fontTotal.getSize()));
        }
        /* Agregar nota */
        if(place.equals(COCINA)){
            String note = (String) saleInfo.get("orderNote");
            if(note.length() > 0){
                Font fontNote = new Font("Arial", Font.PLAIN, 12);
                g2d.drawString(note,0, currentY + fontNote.getSize() );
            }
        }
        return Printable.PAGE_EXISTS;
    } else {
        return Printable.NO_SUCH_PAGE;
    }
    }, pageFormat);


    try {
        printerJob.print();
    } catch (PrinterException e) {
        e.printStackTrace();
    }

    }
    public static PrintService findPrintService(String printerName) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printService : printServices) {
            if (printService.getName().equalsIgnoreCase(printerName)) {
                return printService;
            }
        }

        return null;
    }
     // Método para calcular la altura correspondiente para mantener la relación de aspecto
}