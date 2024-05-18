package com.tql;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipboardMonitor {

    private Clipboard clipboard;
    private Transferable oldContent;

    public ClipboardMonitor() {
        this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.oldContent = clipboard.getContents(null);
    }

    /**
     * 检查剪切板中的新内容。
     * 
     * @return 如果有新内容，则返回新内容字符串；否则返回 null。
     */
    public String checkClipboard() {
        Transferable newContent = clipboard.getContents(null);
        if (newContent != null && newContent.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String oldText = getTextFromTransferable(oldContent);
            String newText = getTextFromTransferable(newContent);

            if (newText != null && !newText.equals(oldText)) {
                oldContent = newContent; // 更新旧内容
                return newText; // 返回新内容
            }
        }
        return null; // 如果没有新内容，返回 null
    }

    /**
     * 从 Transferable 中提取字符串内容。
     * 
     * @param content Transferable 对象
     * @return 字符串内容，如果无法提取，则返回 null
     */
    private String getTextFromTransferable(Transferable content) {
        try {
            if (content != null && content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) content.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ClipboardMonitor monitor = new ClipboardMonitor();
        while (true) {
            String newClipboardContent = monitor.checkClipboard();
            if (newClipboardContent != null) {
                System.out.println("New clipboard content: " + newClipboardContent);
            }
            try {
                Thread.sleep(100); // 每秒检查一次剪切板内容
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
