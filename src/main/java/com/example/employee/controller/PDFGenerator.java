package com.example.employee.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.printing.PDFPrintable;
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
import javax.print.attribute.standard.PrinterName;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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

        this.document = new PDDocument();
        this.page = new PDPage(PDRectangle.A4);
        this.document.addPage(page);
        this.contentStream = new PDPageContentStream(document, page);
    }

    public void createPDFKitchen(String printerName) throws IOException, PrinterException {
        createItems();
        fillItems();
        createTotalPrice();
        closeContentStream("order");
        printPDF(printerName);
    }

    protected void printPDF( String printerName ) throws PrinterException {
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

        
        PDFPrintable printable = new PDFPrintable(document);

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        
        printerJob.setPrintService(printService);

        printerJob.setPrintable(printable);
        printerJob.print();
       /*  if (printerJob.printDialog()) {
            printerJob.setPageable(new PDFPageable(document));
            PrintService service = findPrintService(printerName);
            printerJob.setPrintService(service);
            printerJob.print();
        } */
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
    

    protected void createItems() throws IOException {
        height = page.getMediaBox().getHeight();
       
     /*    contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 9);

        contentStream.newLineAtOffset(65, height - 10);
        contentStream.showText("Dima's Restaurant");
        contentStream.endText(); */
 
        String username = (String) saleInfo.get("username");
        contentStream.beginText();
        contentStream.newLineAtOffset(25, height - 25);
        contentStream.setFont(PDType1Font.HELVETICA, 15);
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
        contentStream.showText("Mesa: "+ nroMesa);
        
        String place = (String) saleInfo.get("place");
        System.out.println(place);
        if(place == COCINA){
            contentStream.showText("Nro: " + nroPedido );
        }
        contentStream.endText();

        Table myTable = Table.builder()
                .addColumnsOfWidth(95, 45, 45)
                .padding(2)
                .font(PDType1Font.HELVETICA_BOLD)
                .addRow(Row.builder().fontSize(9)
                        .add(TextCell.builder().text("Item").borderWidth(0.5F).borderColor(Color.black)
                                .backgroundColor(Color.WHITE).horizontalAlignment(HorizontalAlignment.CENTER).build())
                        .add(TextCell.builder().text("Cant.").borderWidth(0.5f).borderColor(Color.black)
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
                .addColumnsOfWidth(95, 45, 45)
                .padding(5)
                .font(PDType1Font.HELVETICA)
                .borderColor(Color.WHITE);

        List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");

        orders.forEach((valor) -> {
            Map<String, Object> item = valor;   

            Integer cantidad = (Integer) item.get("cantidad");
            Integer precioVenta = (Integer) item.get("precio_venta");


            double total = cantidad.doubleValue() * precioVenta.doubleValue();
            finaltotal.set(finaltotal.get() + total);

            ((TableBuilder) tableBuilder).addRow(Row.builder().fontSize(9)
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
        total_height = height - 80 - (20 *  orders.size());
        int size = 9;
        float paddingRight = 0;
        float pagewidth = page.getMediaBox().getWidth();

        float text_width = (font.getStringWidth(String.valueOf(totalPrice)) / 1000.0f) * size;
        float x = pagewidth - ((paddingRight * 2) + text_width);
        putItemPdf(x - 398, total_height, String.valueOf(totalPrice), PDType1Font.HELVETICA);

        String texto = "total:";
        float text_width2 = (font.getStringWidth(texto) / 1000.0f) * size;
        float x2 = pagewidth - ((paddingRight * 2) + text_width2);
        putItemPdf(x2-435, total_height, texto, PDType1Font.HELVETICA_BOLD);
    }

    protected void putItemPdf(float posX, float posY, String texto, PDFont font) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(posX, posY);
        contentStream.setFont(font, 9);
        contentStream.showText(texto);
        contentStream.endText();
    }

    protected void closeContentStream(String tipe) throws IOException {
        contentStream.close();
        //document.save("src/main/java/org/bo/app/pdf/" + LocalDate.now() + tipe+ ".pdf");
    }
}