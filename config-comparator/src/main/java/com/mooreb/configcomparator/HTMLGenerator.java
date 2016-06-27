package com.mooreb.configcomparator;

import difflib.DiffRow;
import difflib.DiffRowGenerator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class HTMLGenerator {
    public static String generate_html_diff(final String url1String, final String url2String, final boolean includeHeader) throws IOException {
        final URL url1 = new URL(url1String);
        final URL url2 = new URL(url2String);
        final File f1 = File.createTempFile("url1.", ".json");
        FileUtils.copyURLToFile(url1, f1);
        System.out.println("wrote " + url1String + "  to " + f1);
        final File f2 = File.createTempFile("url2.", ".json");
        FileUtils.copyURLToFile(url2, f2);
        System.out.println("wrote " + url2String + "  to " + f2);
        return generate_html_diff(url1String, f1, url2String, f2, includeHeader);
    }

    private static String generate_html_diff(final String label1, final File f1, final String label2, final File f2, final boolean includeHeader) throws IOException {
        final StringBuilder sb = new StringBuilder(65535);
        if(includeHeader) {
            sb.append("<html><head><title>").append(label1).append(" vs. ").append(label2).append("</title>").append("</head>");
            sb.append("<body>\n");
        }
        sb.append("  <table>\n");
        sb.append("    <tr><th>").append(label1).append("</th><th>").append(label2).append("</th></tr>\n\n");
        List<DiffRow> rows = diff_difflib(f1, f2);
        for (final DiffRow row : rows) {
            DiffRow.Tag tag = row.getTag();
            final String oldLine = row.getOldLine();
            final String newLine = row.getNewLine();
            if((null == newLine || "".equals(newLine)) && (DiffRow.Tag.CHANGE == tag)) {
                tag = DiffRow.Tag.DELETE;
            }
            if((null == oldLine || "".equals(oldLine)) && (DiffRow.Tag.CHANGE == tag)) {
                tag = DiffRow.Tag.INSERT;
            }
            switch (tag) {
                case INSERT:
                    sb.append("    <tr bgcolor=\"green\">");
                    sb.append("<td>").append(oldLine).append("</td>");
                    sb.append("<td>").append(newLine).append("</td>");
                    break;
                case CHANGE:
                    sb.append("    <tr bgcolor=\"yellow\">");
                    sb.append("<td>").append(oldLine).append("</td>");
                    sb.append("<td>").append(newLine).append("</td>");
                    break;
                case EQUAL:
                    sb.append("    <tr>");
                    sb.append("<td>").append(oldLine).append("</td>");
                    sb.append("<td>").append(newLine).append("</td>");
                    break;
                case DELETE:
                    sb.append("    <tr bgcolor=\"red\">");
                    sb.append("<td>").append(oldLine).append("</td>");
                    sb.append("<td>").append(newLine).append("</td>");
                    break;
                default:
                    throw new UnsupportedOperationException("Should not reach here");
                    // break;
            }
            sb.append("</tr>\n");
        }
        sb.append("  </table>\n");
        if(includeHeader) {
            sb.append("</body></html>");
        }
        return sb.toString();
    }

    private static List<DiffRow> diff_difflib(final File f1, final File f2) throws IOException {
        List<String> originalList = FileUtils.readLines(f1);
        List<String> revisedList = FileUtils.readLines(f2);
        DiffRowGenerator.Builder builder = new DiffRowGenerator.Builder();
        boolean sideBySide = true;  //default -> inline
        builder.showInlineDiffs(!sideBySide);
        builder.columnWidth(120);
        DiffRowGenerator dfg = builder.build();
        List<DiffRow> rows = dfg.generateDiffRows(originalList, revisedList);
        return rows;
    }
}