package com.example.employee.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

import java.util.List;
 
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.DocPrintJob;
import javax.print.SimpleDoc;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterName;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class PDFGenerator {

    protected Map<String, Object>  saleInfo;
    protected PDPageContentStream contentStream;
    protected PDPage page;
    protected PDDocument document;
    protected float height;
    protected double totalPrice;
    protected float total_height;
    protected final String SALON = "salon";
    protected final String COCINA = "cocina";

    public PDFGenerator( Map<String, Object> saleInfo) throws IOException {
        this.saleInfo = saleInfo;
        this.totalPrice = 0;

       /*  this.document = new PDDocument();
        this.page = new PDPage(PDRectangle.A4);
        this.document.addPage(page);
        this.contentStream = new PDPageContentStream(document, page); */

        document = new PDDocument();

            // Crear una nueva página con el tamaño adecuado para la impresora de 80 mm
        float width = 100; // Ancho del área de impresión en mm
        float height = calcularAltura(width); // Calcular la altura correspondiente para mantener la relación de aspecto
        page = new PDPage(new PDRectangle(width, height));
        document.addPage(page);

            // Crear un nuevo flujo de contenido en la página
        contentStream = new PDPageContentStream(document, page);
    }

    public void createPDFKitchen(String printerName) throws IOException, PrinterException {
        createItems();
        fillItems();
        String place = (String) saleInfo.get("place");
        if(place.equals(SALON)){
            createTotalPrice();
        }
        closeContentStream("order");
        printPDF(printerName);
    }

    protected void printPDF( String printerName ) throws PrinterException, IOException {
        PrintService printService = findPrintService(printerName);
     /*    if (printService != null) {
            try {
                // Cargar el documento PDF
                // Crear un objeto SimpleDoc con el PDDocument
                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
                Doc doc = new SimpleDoc(document, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);

                // Obtener el trabajo de impresión
                DocPrintJob printJob = printService.createPrintJob();

                // Establecer atributos de impresión
                PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
                attributeSet.add(new PrinterName(printerName, null));

                // Imprimir el documento
                printJob.print(doc, attributeSet);

                // Cerrar el documento
                document.close();   
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se encontró la impresora con el nombre: " + printerName);
        } */

  // Crear un conjunto de atributos de solicitud de impresión
    /*         PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();

            // Establecer los límites de impresión en el área de impresión del PDF
            float x = 0; // Coordenada X del área de impresión en puntos (1 punto = 1/72 pulgadas)
            float y = 0; // Coordenada Y del área de impresión en puntos (1 punto = 1/72 pulgadas)
            float printWidth = 80 * 72; // Ancho del área de impresión en puntos
            float printHeight = calcularAltura(printWidth); // Calcular la altura correspondiente para mantener la relación de aspecto
            attributeSet.add(new MediaPrintableArea(x, y, printWidth, printHeight, MediaPrintableArea.INCH));

            // Crear un objeto PDFPrintable con el documento
            String filePath = "src\\main\\java\\pdf\\2023-07-05order.pdf";
            PDDocument document2 = PDDocument.load(new File(filePath));
                        // Obtener una instancia de PrinterJob
            String path = "path.pdf";
            PDFPrintable printable = new PDFPrintable(document2);
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            PageFormat pageFormat = printerJob.defaultPage();
                  Paper paper = new Paper();
        paper.setSize(300, 400); 

          double scale = 1.5; // Escala personalizada (por ejemplo, 1.5 significa aumentar el tamaño en un 50%)
            double imageableWidth = paper.getWidth() * scale;
            double imageableHeight = paper.getHeight() * scale;
            double imageableX = (paper.getWidth() - imageableWidth) / 2;
            double imageableY = (paper.getHeight() - imageableHeight) / 2;
            paper.setImageableArea(imageableX, imageableY, imageableWidth, imageableHeight);

            // Establecer el Paper en la PageFormat
            pageFormat.setPaper(paper);

                 printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
            // Aquí puedes dibujar el contenido a imprimir en el contexto Graphics
            // Utiliza las dimensiones proporcionadas por pageFormat para adaptar el contenido a la página escalada
            // ...

            return Printable.PAGE_EXISTS;
        }, pageFormat);
            // Establecer el servicio de impresión y el PDFPrintable en el PrinterJob
            printerJob.setPrintService(printService);
            printerJob.setPrintable(printable);

            // Establecer los atributos de solicitud de impresión en el PrinterJob
            printerJob.print(attributeSet); */


        
    /*     PDFPrintable printable = new PDFPrintable(document);

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        
        printerJob.setPrintService(printService);

        PageFormat pageFormat = printerJob.defaultPage();
        pageFormat.setScale(1.5); 

        printerJob.setPrintable(printable, pageFormat);
        printerJob.print(); */
       /*  if (printerJob.printDialog()) {
            printerJob.setPageable(new PDFPageable(document));
            PrintService service = findPrintService(printerName);
            printerJob.setPrintService(service);
            printerJob.print();
        } */




    /*     PDFPrintable printable = new PDFPrintable(document);

        PrinterJob printerJob = PrinterJob.getPrinterJob();

        printerJob.setPrintService(printService);

        PageFormat pageFormat = printerJob.defaultPage();

        double scale = 2; // Aumenta la escala a 1.5 (150%)
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(scale, scale);
        Paper paper = pageFormat.getPaper();
        paper.setImageableArea(paper.getImageableX(), paper.getImageableY(), paper.getWidth() * scale, paper.getHeight() * scale);
        pageFormat.setPaper(paper);
        pageFormat = printerJob.validatePage(pageFormat);

        printerJob.setPrintable(printable, pageFormat);
        printerJob.print(); */
      /*   PDFPrintable printable = new PDFPrintable(document);

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(printService);

        PageFormat pageFormat = printerJob.defaultPage();

        double scale = 5; // Aumenta la escala a 1.5 (150%)
        pageFormat = printerJob.validatePage(pageFormat);

        final Printable finalPrintable = printable; // Crear una variable final

        printerJob.setPrintable((graphics, printablePageFormat, pageIndex) -> {
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(printablePageFormat.getImageableX(), printablePageFormat.getImageableY());
            g2d.scale(scale, scale);
            return finalPrintable.print(graphics, printablePageFormat, pageIndex); // Utilizar la variable final
        }, pageFormat);

        printerJob.print(); */
 //version semifuncion , casi bien 
    /*     PrinterJob printerJob = PrinterJob.getPrinterJob();

        // Obtener la página por defecto del PrinterJob
        PageFormat defaultPageFormat = printerJob.defaultPage();

        // Crear una instancia de Paper para establecer el tamaño de la página
        Paper paper = new Paper();
        paper.setSize(80, 30); // Establecer el tamaño de la página en puntos (1 punto = 1/72 de pulgada)

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

        // Establecer el Printable en el PrinterJob
      printerJob.setPrintable((graphics, pf, pageIndex) -> {
    if (pageIndex == 0) { // Solo dibujar en la primera página (pageIndex = 0)
        Graphics2D g2d = (Graphics2D) graphics;

        // Obtener las dimensiones de la página escalada
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // Establecer el ancho máximo para el texto según el ancho de papel de 80 mm
        int maxWidth = (int) (pageWidth * 0.8); // El 80% del ancho de la página

        // Establecer la fuente y el tamaño de la letra
        Font font = new Font("Arial", Font.PLAIN, 20);
        g2d.setFont(font);

        // Obtener el tamaño del texto
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth("Hola, mundo");
        int textHeight = fontMetrics.getHeight();

        // Calcular la posición para centrar el texto en el ancho de papel de 80 mm
        int x = (maxWidth - textWidth) / 2;
        int y = (int) ((pageHeight - textHeight) / 2) + fontMetrics.getAscent();

        // Dibujar el texto en la posición calculada
        g2d.drawString("alho, odnum", x, y);

        return Printable.PAGE_EXISTS;
        } else {
            return Printable.NO_SUCH_PAGE;
        }
    }, pageFormat);

        // Imprimir
        try {
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        } */

        PrinterJob printerJob = PrinterJob.getPrinterJob();

        // Obtener la página por defecto del PrinterJob
        //PageFormat defaultPageFormat = printerJob.defaultPage();

        // Crear una instancia de Paper para establecer el tamaño de la página
        Paper paper = new Paper();

        
        paper.setSize(200, 120); // Establecer el tamaño de la página en puntos (1 punto = 1/72 de pulgada)

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
    
     /*    printerJob.setPrintable((graphics, pf, pageIndex) -> {
    if (pageIndex == 0) { // Solo dibujar en la primera página (pageIndex = 0)
        Graphics2D g2d = (Graphics2D) graphics;

        // Obtener las dimensiones de la página escalada
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // Establecer el ancho máximo para la tabla según el ancho de papel de 80 mm
        int tableWidth = (int) (pageWidth * 0.95); // El 80% del ancho de la página

        // Establecer la fuente y el tamaño de la letra
        Font font = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(font);

        // Definir los datos de la tabla
        String[][] data = {
            {"Producto 1", "10", "$50.00"},
            {"Producto 2", "5", "$30.00"},
            {"Producto 3", "8", "$20.00"}
        };

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
            columnWidths[i] = (int) ((double) columnWidths[i] / totalWidth * tableWidth);
        }

        // Calcular la posición para dibujar la tabla
        int x = (int) ((pageWidth - tableWidth) / 2);
        int y = (int) ((pageHeight - data.length * font.getSize()) / 2);

        // Dibujar los encabezados de la tabla
        int currentX = x;
        for (int i = 0; i < data[0].length; i++) {
            String header = data[0][i];
            g2d.drawString(header, currentX, y);
            currentX += columnWidths[i];
        }

        // Dibujar las filas de la tabla
        int currentY = y + font.getSize();
        for (int i = 1; i < data.length; i++) {
            currentX = x;
            for (int j = 0; j < data[i].length; j++) {
                String value = data[i][j];
                g2d.drawString(value, currentX, currentY);
                currentX += columnWidths[j];
            }
            currentY += font.getSize();
        }

        return Printable.PAGE_EXISTS;
    } else {
        return Printable.NO_SUCH_PAGE;
    }
}, pageFormat);

 */
/* Esta version no esta nada mal */
   /*  printerJob.setPrintable((graphics, pf, pageIndex) -> {
    if (pageIndex == 0) { // Solo dibujar en la primera página (pageIndex = 0)
        Graphics2D g2d = (Graphics2D) graphics;

        // Obtener las dimensiones de la página escalada
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // Establecer la fuente y el tamaño de la letra para el texto "Cliente: customer"
        Font font12 = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(font12);
        String customerText = "Cliente: customer";
        g2d.drawString(customerText, 0, font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "username: {user}   fecha: {fechaActual}"
        Font font12Bold = font12.deriveFont(Font.BOLD);
        g2d.setFont(font12Bold);
        String user = "{user}";
        String fechaActual = "{fechaActual}";
        String userInfoText = "username: " + user + "   fecha: " + fechaActual;
        g2d.drawString(userInfoText, 0, 2 * font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "Mesa: 1     Nro: 323"
        Font font16 = new Font("Arial", Font.PLAIN, 16);
        g2d.setFont(font16);
        String mesaText = "Mesa: 1     Nro: 323";
        g2d.drawString(mesaText, 0, 2 * font16.getSize());

        Font font4 = new Font("Arial", Font.PLAIN, 4);
        g2d.setFont(font4);
        String space = " ";
        g2d.drawString(space, 0, 1 * font4.getSize());


        // Establecer la fuente y el tamaño de la letra para la tabla
        Font fontTable = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTable);

        // Definir los datos de la tabla
        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");
        String[][] data = new String[orders.size() + 1][3];
        data[0] = new String[]{"Nombre", "Cantidad", "Precio"};
        for (int i = 0; i < orders.size(); i++) {
            Map<String, Object> order = orders.get(i);
            String nombre = (String) order.get("nombre");
            String cantidad = String.valueOf(order.get("cantidad"));
            String precio = String.valueOf(order.get("precio_vente"));
            data[i + 1] = new String[]{nombre, cantidad, precio};
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
            columnWidths[i] = (int) ((double) columnWidths[i] / totalWidth * pageWidth);
        }

        // Calcular la posición para dibujar la tabla
        int x = (int) ((pageWidth - totalWidth) / 2);
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

        // Establecer la fuente y el tamaño de la letra para el texto "Total=154 justificado a la izquierda"
        Font fontTotal = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTotal);
        String totalText = "Total=154";
        int totalX = (int) (pageWidth - g2d.getFontMetrics().stringWidth(totalText));
        g2d.drawString(totalText, totalX, (int) (pageHeight - fontTotal.getSize()));

        return Printable.PAGE_EXISTS;
    } else {
        return Printable.NO_SUCH_PAGE;
    }
        }, pageFormat);

           try {
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }  */
/* Version actuliazada x */
        printerJob.setPrintable((graphics, pf, pageIndex) -> {
    if (pageIndex == 0) { // Solo dibujar en la primera página (pageIndex = 0)
        Graphics2D g2d = (Graphics2D) graphics;

        // Obtener las dimensiones de la página escalada
        double pageWidth = pf.getImageableWidth();
        double pageHeight = pf.getImageableHeight();

        // Establecer la fuente y el tamaño de la letra para el texto "Cliente: customer"
        Font font12 = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(font12);
        String customerText = "Cliente: customer";
        g2d.drawString(customerText, 0, font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "username: {user}   fecha: {fechaActual}"
        Font font12Bold = font12.deriveFont(Font.BOLD);
        g2d.setFont(font12Bold);
        String user = "{user}";
        String fechaActual = "{fechaActual}";
        String userInfoText = "username: " + user + "   fecha: " + fechaActual;
        g2d.drawString(userInfoText, 0, 2 * font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "Mesa: 1     Nro: 323"
        Font font16 = new Font("Arial", Font.PLAIN, 16);
        g2d.setFont(font16);
        String mesaText = "Mesa: 1     Nro: 323";
        g2d.drawString(mesaText, 0, 3 * font16.getSize());

        // Establecer la fuente y el tamaño de la letra para la tabla
        Font fontTable = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTable);

        // Definir los datos de la tabla
        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");
        String[][] data = new String[orders.size() + 1][3];
        data[0] = new String[]{"Nombre", "Cantidad", "Precio"};
        for (int i = 0; i < orders.size(); i++) {
            Map<String, Object> order = orders.get(i);
            String nombre = (String) order.get("nombre");
            int cantidad = (int) order.get("cantidad");
            double precioUnitario = (Integer) order.get("precio_venta");
            double precioTotal = cantidad * precioUnitario;
            data[i + 1] = new String[]{nombre, String.valueOf(cantidad), String.valueOf(precioTotal)};
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
            columnWidths[i] = (int) ((double) columnWidths[i] / totalWidth * (pageWidth - 0)); // Ajuste de posición en X
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

        // Establecer la fuente y el tamaño de la letra para el texto "Total=154 justificado a la derecha"
        Font fontTotal = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTotal);
        String totalText = "Total=154 justificado a la derecha";
        int totalWidth2 = g2d.getFontMetrics().stringWidth(totalText);
        int totalX = (int) (pageWidth - totalWidth2);
        g2d.drawString(totalText, totalX, (int) (pageHeight - fontTotal.getSize()));

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


        /* 


        PrinterService printerService = new PrinterService();
          printerService.printString("ZKP8008", "\n\n testing testing 1 2 3eeeee \n HOLA \n hola \n");

         ((PrinterJob) printService).print(); */
           //  byte[] cutP = new byte[] { 0x1d, 'V', 1 };

       // printerService.printBytes("ZKP8008", cutP);
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
    private static float calcularAltura(float width) {
        float aspectRatio = 80 / 80; // Relación de aspecto para una impresora de 80 mm
        return width / aspectRatio;
    }

    protected void createItems() throws IOException {
        height = page.getMediaBox().getHeight();
        System.out.println(height);
        String place = (String) saleInfo.get("place");
        String customer = (String) saleInfo.get("cliente");
        if(place.equals(COCINA)){
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.newLineAtOffset(0, 0);
            contentStream.showText("Cliente: " + customer );
            contentStream.endText();
        }
       
     /*    contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);

        contentStream.newLineAtOffset(65, height - 10);
        contentStream.showText("Dima's Restaurant");
        contentStream.endText(); */
 
        String username = (String) saleInfo.get("username");
        contentStream.beginText();
        contentStream.newLineAtOffset(25, height - 25);
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.showText(username);
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(120, height - 25);
        contentStream.showText("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        contentStream.endText();

        String nroMesa = (String) saleInfo.get("nroMesa");
        int nroPedido = (int) saleInfo.get("nroPedido");

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 20);
        contentStream.newLineAtOffset(25, height - 50);
        contentStream.showText("Mesa: "+ nroMesa+"  ");
        
        if(place.equals(COCINA)){
            contentStream.showText("Nro: " + nroPedido );
        }
        contentStream.endText();

        Table myTable = Table.builder()
                .addColumnsOfWidth(165, 20, 55)
                .padding(2)
                .font(PDType1Font.HELVETICA_BOLD)
                .addRow(Row.builder().fontSize(10)
                        .add(TextCell.builder().text("Item").borderWidth(0.5F).borderColor(Color.black)
                                .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.CENTER).build())
                        .add(TextCell.builder().text("Ca.").borderWidth(0.5f).borderColor(Color.black)
                                .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.CENTER).build())
                        .add(TextCell.builder().text("Total").borderWidth(0.5f).borderColor(Color.black)
                                .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.CENTER).build())
                        .build())
                .build();

        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(2)
                .startY(height - 65)
                .table(myTable)
                .build();
        tableDrawer.draw();
    }

    protected void fillItems() {
        AtomicReference<Double> finaltotal = new AtomicReference<>((double) 0);
        Object tableBuilder = Table.builder()
                .addColumnsOfWidth(160, 25, 55)
                .padding(4)
                .font(PDType1Font.HELVETICA)
                .borderColor(Color.WHITE);

        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");

        orders.forEach((valor) -> {
            Map<String, Object> item = valor;   

            Integer cantidad = (Integer) item.get("cantidad");
            Integer precioVenta = (Integer) item.get("precio_venta");


            double totalDouble = cantidad.doubleValue() * precioVenta.doubleValue();
            String total = String.format("%.0f", totalDouble);
            finaltotal.set(finaltotal.get() + totalDouble);

            ((TableBuilder) tableBuilder).addRow(Row.builder().fontSize(16)
                    .add(TextCell.builder().text(item.get("nombre") + "").borderWidth(0.5F)
                            .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.LEFT).build())
                    .add(TextCell.builder().text( item.get("cantidad") + "").borderWidth(0.5F)
                            .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.RIGHT).build())
                    .add(TextCell.builder().text(total + "").borderWidth(0.5F)
                            .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.RIGHT).build())
                    .build());
        });
        Table myTable = ((TableBuilder) tableBuilder).build();

        TableDrawer tableDrawer = TableDrawer.builder()
                .contentStream(contentStream)
                .startX(2)
                .startY(height - 80)
                .table(myTable)
                .build();
        tableDrawer.draw();
        this.totalPrice = finaltotal.get();
    }

    protected void createTotalPrice() throws IOException {
        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");
        PDFont font= PDType1Font.HELVETICA;
        total_height = height - 80 - (26 *  orders.size());
        int size = 16;
        float paddingRight = 0;
        float pagewidth = page.getMediaBox().getWidth();

        float text_width = (font.getStringWidth(String.valueOf(totalPrice)) / 1000.0f) * size;
        float x = pagewidth - ((paddingRight * 2) + text_width);
        putItemPdf(x - 365, total_height, String.valueOf(totalPrice), PDType1Font.HELVETICA);

        String texto = "total:";
        float text_width2 = (font.getStringWidth(texto) / 1000.0f) * size;
        float x2 = pagewidth - ((paddingRight * 2) + text_width2);
        putItemPdf(x2- 417, total_height, texto, PDType1Font.HELVETICA_BOLD);
    }

    protected void putItemPdf(float posX, float posY, String texto, PDFont font) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(posX, posY);
        contentStream.setFont(font, 16);
        contentStream.showText(texto);
        contentStream.endText();
    }

    protected void closeContentStream(String tipe) throws IOException {
        contentStream.close();
       document.save("src/main/java/pdf/" + LocalDate.now() + tipe+ ".pdf");
    }
}