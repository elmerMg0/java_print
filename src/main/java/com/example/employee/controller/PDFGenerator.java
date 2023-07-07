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
        //createItems();
        //fillItems();
        /* String place = (String) saleInfo.get("place");
        if(place.equals(SALON)){
            createTotalPrice();
        }
        closeContentStream("order"); */
        printPDF(printerName);
    }

    protected void printPDF( String printerName ) throws PrinterException, IOException {
        PrintService printService = findPrintService(printerName);
   

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(printService);
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
        //Font font12Bold = font12.deriveFont(Font.BOLD);
        g2d.setFont(font12);
        String user = (String)saleInfo.get("username");
        String dateCurrently = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        String userInfoText = "username: " + user + "   fecha: " + dateCurrently;
        g2d.drawString(userInfoText, 0, 2 * font12.getSize());

        // Establecer la fuente y el tamaño de la letra para el texto "Mesa: 1     Nro: 323"
        Font font16 = new Font("Arial", Font.PLAIN, 16);
        g2d.setFont(font16);
        String table = (String)saleInfo.get("nroMesa");
        String nroPedido = (String)saleInfo.get("nroPedido");
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
        data[0] = new String[]{"Nombre", "Cant.", "Precio"};
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
        String total = (String)saleInfo.get("cantidadTotal");
        Font fontTotal = new Font("Arial", Font.PLAIN, 12);
        g2d.setFont(fontTotal);
        String totalText = "Total " + total;
        int totalWidth2 = g2d.getFontMetrics().stringWidth(totalText);
        int totalX = (int) (pageWidth - totalWidth2);
        if(place.equals(SALON)){
            g2d.drawString(totalText, totalX, (int) (pageHeight - fontTotal.getSize()));
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