import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * ClassName: PrintDemo
 * Description: java打印demo
 * Author: Zhao Li
 * Date: 2018/12/20 13:37
 * History:
 */
class PrintDemo {

    @Test
    void test01() throws PrintException {
        File file = new File("D:\\image\\testPic.jpg");
        String printer = "HP7F58AB (HP PageWide Pro 452dw Printer)";
        JPGPrint(file, printer);
    }

    @Test
    void test02() throws Exception {
        String pdfFile = "D:\\image\\JPS_API.pdf";//文件路径
        File file = new File(pdfFile);
        String printerName = "HP7F58AB (HP PageWide Pro 452dw Printer)";//打印机名包含字串
        PDFprint(file, printerName);
    }

    /**
     * 打印jpg文件
     * 打印多彩图片可能会导致卡纸
     *
     * @param file        要打印的jpg文件
     * @param printerName 要使用的打印机名称
     * @throws PrintException 打印出现异常
     */
    private static void JPGPrint(File file, String printerName) throws PrintException {
        if (file == null) {
            System.err.println("缺少打印文件");
        }
        InputStream fis = null;
        try {
            // 设置打印格式，如果未确定类型，可选择autosense
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
            // 设置打印参数
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new Copies(1)); //份数
            //aset.add(MediaSize.ISO.A4); //纸张
            // aset.add(Finishings.STAPLE);//装订
            aset.add(Sides.DUPLEX);//单双面
            // 定位打印服务
            PrintService printService = null;
            if (printerName != null) {
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    System.out.print("打印失败，未找到可用打印机，请检查。");
                    return;
                }
                //匹配指定打印机
                for (PrintService printService1 : printServices) {
                    System.out.println(printService1.getName());
                    if (printService1.getName().contains(printerName)) {
                        printService = printService1;
                        break;
                    }
                }
                if (printService == null) {
                    System.out.print("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
                    return;
                }
            }
            fis = new FileInputStream(file); // 构造待打印的文件流
            Doc doc = new SimpleDoc(fis, flavor, null);
            DocPrintJob job = printService.createPrintJob(); // 创建打印作业
            job.print(doc, aset);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            // 关闭打印的文件流
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打印pdf文件
     *
     * @param file        要打印的pdf文件
     * @param printerName 要使用的打印机名称
     * @throws Exception 打印异常
     */
    static void PDFprint(File file, String printerName) throws Exception {
        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(file.getName());
            if (printerName != null) {
                // 查找并设置打印机
                //获得本台电脑连接的所有打印机
                PrintService[] printServices = PrinterJob.lookupPrintServices();
                if (printServices == null || printServices.length == 0) {
                    System.out.print("打印失败，未找到可用打印机，请检查。");
                    return;
                }
                PrintService printService = null;
                //匹配指定打印机
                for (PrintService printService1 : printServices) {
                    System.out.println(printService1.getName());
                    if (printService1.getName().contains(printerName)) {
                        printService = printService1;
                        break;
                    }
                }
                if (printService != null) {
                    printJob.setPrintService(printService);
                } else {
                    System.out.print("打印失败，未找到名称为" + printerName + "的打印机，请检查。");
                    return;
                }
            }
            //设置纸张及缩放
            PDFPrintable pdfPrintable = new PDFPrintable(document, Scaling.ACTUAL_SIZE);
            //设置多页打印
            Book book = new Book();
            PageFormat pageFormat = new PageFormat();
            //设置打印方向
            pageFormat.setOrientation(PageFormat.PORTRAIT);//纵向
            pageFormat.setPaper(getPaper());//设置纸张
            book.append(pdfPrintable, pageFormat, document.getNumberOfPages());
            printJob.setPageable(book);
            printJob.setCopies(1);//设置打印份数
            //添加打印属性
            HashPrintRequestAttributeSet pars = new HashPrintRequestAttributeSet();
            pars.add(Sides.DUPLEX); //设置单双页
            printJob.print(pars);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取Paper对象
     *
     * @return Page对象
     */
    private static Paper getPaper() {
        Paper paper = new Paper();
        // 默认为A4纸张，对应像素宽和高分别为 595, 842
        int width = 595;
        int height = 842;
        // 设置边距，单位是像素，10mm边距，对应 28px
        int marginLeft = 10;
        int marginRight = 0;
        int marginTop = 10;
        int marginBottom = 0;
        paper.setSize(width, height);
        // 下面一行代码，解决了打印内容为空的问题
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }
}
