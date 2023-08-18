package ltm;

import io.cucumber.plugin.event.DataTableArgument;

import java.util.List;

public class DataTableFormatter {
    private final List<?> list;

    public DataTableFormatter(DataTableArgument dtArgument) {
        this.list = dtArgument.cells();
    }

    public StringBuilder generateTabularFormat() {
        StringBuilder result = new StringBuilder();

        appendTable(result, this.list, getDepth(this.list));

        return result;
    }

    private static int getDepth(List<?> list) {
        return getDepthRecursive(list, 0);
    }

    private static int getDepthRecursive(List<?> list, int currentDepth) {
        if (list.isEmpty() || !(list.get(0) instanceof List)) {
            return currentDepth;
        }

        int maxDepth = currentDepth + 1;
        for (Object item : list) {
            if (item instanceof List) {
                int subDepth = getDepthRecursive((List<?>) item, currentDepth + 1);
                maxDepth = Math.max(maxDepth, subDepth);
            }
        }
        return maxDepth;
    }

    private void appendTable(StringBuilder result, List<?> data, int depth) {
        if (data.isEmpty()) {
            return;
        }

        if (data.get(0) instanceof List) {
            int numCols = ((List<?>) data.get(0)).size();
            int[] columnWidths = new int[numCols];

            for (Object row : data) {
                List<?> rowData = (List<?>) row;
                for (int colIndex = 0; colIndex < numCols; colIndex++) {
                    int cellLength = getCellLength(rowData.get(colIndex));
                    if (cellLength > columnWidths[colIndex]) {
                        columnWidths[colIndex] = cellLength;
                    }
                }
            }

            for (Object row : data) {
                List<?> rowData = (List<?>) row;
                appendTableRow(result, rowData, columnWidths, depth);
            }
        } else {
            for (Object value : data) {
                appendTableRow(result, value.toString(), depth);
            }
        }
    }

    private int getCellLength(Object cell) {
        return cell.toString().length();
    }

    private void appendTableRow(StringBuilder result, String cell, int depth) {
        result.append(getIndentation(depth)).append("| ").append(padString(cell, cell.length())).append("|\n");
    }

    private void appendTableRow(StringBuilder result, List<?> row, int[] columnWidths, int depth) {
        result.append(getIndentation(depth)).append("|");
        for (int colIndex = 0; colIndex < row.size(); colIndex++) {
            String cell = row.get(colIndex).toString();
            result.append(" ").append(padString(cell, columnWidths[colIndex])).append(" |");
        }
        result.append("\n");
    }

    private String padString(String value, int length) {
        return String.format("%-" + length + "s", value);
    }

    private static String getIndentation(int depth) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indentation.append("    "); // Use 4 spaces for each level of indentation
        }
        return indentation.toString();
    }
}
