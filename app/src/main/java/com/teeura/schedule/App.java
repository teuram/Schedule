package com.teeura.schedule;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {

    public static InputStream get_stream(String urlToRead) throws IOException {
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        return conn.getInputStream();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String schedule(String[] groups) {
        try {
            InputStream inp = get_stream(
                    "https://cloud.nntc.nnov.ru/index.php/s/S5cCa8MWSfyPiqx/download");

            Workbook wb = new XSSFWorkbook(inp);

            Iterator<Sheet> sheets = wb.iterator();
            while (sheets.hasNext()) {
                return getSchedule(sheets.next(), groups);
            }
            inp.close();
            wb.close();
        } catch (IOException e) {}
        return "error: parse xlsx table";
    }

    private static LinkedHashSet<String> getAllGroups(Sheet sheet) {
        LinkedHashSet<String> groups = new LinkedHashSet<>();
        Iterator<Row> rows = sheet.rowIterator();

        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.cellIterator();
            if (cells.hasNext()) {
                String f = cells.next().toString();
                if (f.length() < 16 && f.length() > 2) {
                    if (!f.equals("Группа")) {
                        groups.add(f);
                    }
                }
            }
        }

        return groups;
    }

    private static String formatOutput(ArrayList<String> rowCeils) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (rowCeils.size() - rowCeils.size() % 3); i += 3) {
            String time = rowCeils.get(i);
            String room = rowCeils.get(i + 1);

            ArrayList<String> t = new ArrayList<>();
            Matcher mat = Pattern.compile("[0-9]{1,}").matcher(time);
            while (mat.find()) {
                t.add(mat.group().toString());
            }

            if (t.size() == 4) {
                time = String.format("[%s:%s][%s:%s]", t.get(0), t.get(1), t.get(2), t.get(3));
            } else {
                time = "[None]";
            }

            String[] spl = rowCeils.get(i + 2).split("/");

            if (spl.length == 2) {
                sb.append(String.format("[%d] -> %s -> %s / %s\n\r    %s\n\n", (i / 3) + 1, time, room, spl[1].trim(), spl[0].trim()));
            } else {
                if (spl.length == 1 && spl[0].trim().length() > 1) {
                    sb.append(String.format("[%d] -> %s -> %s\n\r    %s\n\n", (i / 3) + 1, time, room, spl[0].trim()));
                }
            }

        }
        return sb.toString();
    }

    private static String formatOutputCheckIsDay(Cell cell) {
        Pattern pattern = Pattern.compile("[0-9]{1,}.*[а-яА-Я]{1,}.*[0-9]{1,}.*\\([а-яА-Я]{1,}\\)");
        Matcher matcher = pattern.matcher(cell.toString());
        if (matcher.find()) {
            String str = matcher.group().toString().replaceAll("\\.", "");
            return str.replaceAll("\\x{A0}", "");
        }

        return null;
    }

    private static ArrayList<String> cellsToStrings(Iterator<Cell> cells) {
        ArrayList<String> cel = new ArrayList<>();
        while (cells.hasNext()) {
            Cell c = cells.next();
            switch (c.getCellType()) {
                case STRING:
                    cel.add(c.getStringCellValue());
                    break;
                case NUMERIC:
                    cel.add(String.valueOf(((int)c.getNumericCellValue())));
                    break;
                default:
                    break;
            }
        }
        return cel;
    }

    private static String getSchedule(Sheet sheet, String[] groups) {
        StringBuilder sb = new StringBuilder();
        Iterator<Row> rows = sheet.rowIterator();
        boolean is_first_day = true;
        while (rows.hasNext()) {
            Row row = rows.next();
            Iterator<Cell> cells = row.cellIterator();
            if (cells.hasNext()) {
                Cell cell = cells.next();

                String day = formatOutputCheckIsDay(cell);
                if (day != null) {
                    if (!is_first_day) {
                        sb.append("\n\n");
                    } else {
                        is_first_day = false;
                    }
                    sb.append(day);
                    sb.append('\n');
                    continue;
                }

                for (String group : groups) {
                    if (cell.toString().equals(group)) {
                        sb.append(String.format("[%s]", cell.toString())); // add name of group
                        sb.append('\n');

                        ArrayList<String> cel = cellsToStrings(cells); // lessons (RAW)
                        sb.append(formatOutput(cel));
                    }
                }
            }
        }
        return sb.toString();
    }
}

