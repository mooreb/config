package com.mooreb.configcomparator;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class App {
    public static void main(String[] argv) throws IOException {
        if (2 != argv.length) {
            System.err.println("Compare properties found at url1 to properties found at url2");
            System.err.println("Usage: url1 url2");
            System.exit(1);
        }
        final String url1String = argv[0];
        final String url2String = argv[1];
        final String html_diff = HTMLGenerator.generate_html_diff(url1String, url2String, true);
        final File htmlDiffFile = File.createTempFile("diff.", ".html");
        FileUtils.writeStringToFile(htmlDiffFile, html_diff);
        System.out.println("wrote html to file://" + htmlDiffFile);
    }
}