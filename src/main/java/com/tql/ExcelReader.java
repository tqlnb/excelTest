package com.tql;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.ResourceLoader;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.Scanner;

public class ExcelReader {
    public static void main(String[] args) {
        //String filePath = "C:\\Users\\tql\\Documents\\WeChat Files\\wxid_vdevrjsnr23l22\\FileStorage\\File\\2024-05\\data.xlsx";
        //String keyword = "成本";
        Scanner scanner = null;

        Scanner sc = new Scanner(System.in, "UTF-8");
        /*System.out.println("请输入答案文件位置：");
        String filePath = sc.nextLine();*/
        //String filePath = ExcelReader.class.getClassLoader().getResource("data.xlsx").getPath();
        //String filePath = ResourceLoader.class.getClassLoader().getResource("data.xlsx").getPath();
        //System.out.println("请输入关键字：");
        String keyword ;
        StringBuffer sb = new StringBuffer();
        // 读取输入的线程
        new Thread(() -> {
            while (true) {
                String str = sc.nextLine();
                if (str != null && !str.equals("")) {
                    sb.append(str);
                }
            }
        }).start();

        // 读取剪切板的线程
        new Thread(() -> {
            ClipboardMonitor monitor = new ClipboardMonitor();
            while (true) {
                try {
                    String str = monitor.checkClipboard();
                    if (str != null && !str.equals("")) {
                        sb.append(str);
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    continue;
                }
            }
        }).start();

        try (InputStream fis = ExcelReader.class.getClassLoader().getResourceAsStream("data.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            System.out.println("请输入关键字或者复制：");

            while (true) {
                /*keyword = sc.nextLine();
                keyword = normalizeString(keyword); // 标准化关键字字符串*/
                if (sb.length() == 0) {
                    continue;
                }
                keyword = sb.toString();
                keyword = normalizeString(keyword); // 标准化关键字字符串
                try {
                    for (Sheet sheet : workbook) {
                        for (Row row : sheet) {
                            Cell questionCell = row.getCell(5); // 题干所在的列索引，从0开始
                            if (questionCell != null && questionCell.getCellType() == CellType.STRING) {
                                String question = questionCell.getStringCellValue();
                                question = normalizeString(question); // 标准化题干字符串
                                if (question.contains(keyword)) {
                                    String answer = row.getCell(6).getStringCellValue();
                                    System.out.println("题干：" + question);
                                    System.out.println("答案：" + answer);
                                    if (row.getCell(1).getStringCellValue().equals("单选题") || row.getCell(1).getStringCellValue().equals("多选题")) {
                                        Cell optionACell = row.getCell(8);
                                        Cell optionBCell = row.getCell(9);
                                        Cell optionCCell = row.getCell(10);
                                        Cell optionDCell = row.getCell(11);
                                        Cell optionECell = row.getCell(12);
                                        Cell optionFCell = row.getCell(13);

                                        System.out.println("选项A：" + optionACell.getStringCellValue());
                                        System.out.println("选项B：" + optionBCell.getStringCellValue());
                                        System.out.println("选项C：" + optionCCell.getStringCellValue());
                                        System.out.println("选项D：" + optionDCell.getStringCellValue());
                                        if(row.getCell(1).getStringCellValue().equals("多选题")) {
                                            System.out.println("选项E：" + optionECell.getStringCellValue());
                                            System.out.println("选项F：" + optionFCell.getStringCellValue());
                                        }
                                    }
                                    System.out.println("---------------");
                                }
                            }
                        }
                    }
                    keyword = null;
                    sb.setLength(0);
                } catch (Exception e) {
                    continue;
                }
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println();
                System.out.println("===============");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 添加一个方法来标准化字符串
    private static String normalizeString(String str) {
        // 替换全角空格为半角空格
        str = str.replaceAll("　", " ");
        // 移除全角和半角标点，并转换为小写
        str = str.replaceAll("[\\p{Punct}]", " ").replaceAll("[，、 。？！；：“”【】（）]", " ").toLowerCase();
        // 移除多余的空格
        str = str.replaceAll("\\s+", " ").trim();
        // 移除重音符号
        str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // 移除空格和多个连在一起的空格
        str = str.replaceAll("\\s", "");
        return str;
    }

}
