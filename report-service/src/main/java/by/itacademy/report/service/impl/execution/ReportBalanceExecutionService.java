package by.itacademy.report.service.impl.execution;

import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Account;
import by.itacademy.report.dto.Params;
import by.itacademy.report.service.ReportDataExecutionService;
import by.itacademy.report.service.RestHelper;
import by.itacademy.report.utils.Generator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportBalanceExecutionService implements ReportDataExecutionService {

    private final RestHelper restHelper;
    private final Generator generator;
    private final String fontName;

    private static final String SHEET_NAME = "ReportBalance";

    public ReportBalanceExecutionService(RestHelper restHelper,
                                         Generator generator,
                                         @Value("${font_name}") String fontName) {
        this.restHelper = restHelper;
        this.generator = generator;
        this.fontName = fontName;
    }

    @Override
    public byte[] execute(Params params) throws IOException {
        Set<UUID> uuids = params.getAccounts();
        LocalDateTime time = generator.now();

        boolean empty = (uuids == null) || uuids.isEmpty();
        List<Account> accounts = empty ? restHelper.getAccounts() : restHelper.getAccounts(uuids);

        Set<UUID> currencies = accounts.stream().map(Account::getCurrency).collect(Collectors.toSet());
        Map<UUID, String> titleCurrency = restHelper.getTitles(currencies, UrlType.CURRENCY);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            createColumns(sheet);
            fillHeader(workbook, time);
            fillSheet(workbook, accounts, titleCurrency);
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createColumns(Sheet sheet) {
        sheet.setColumnWidth(0, 35 * 256);
        sheet.setColumnWidth(1, 17 * 256);
        sheet.setColumnWidth(2, 10 * 256);
        sheet.setColumnWidth(3, 10 * 256);
    }

    private void fillHeader(XSSFWorkbook workbook,
                            LocalDateTime time) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        Row rowHeader1 = sheet.createRow(0);
        Row rowHeader2 = sheet.createRow(1);
        Row rowTitles = sheet.createRow(2);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        XSSFFont fontHeaderRow = createFont(workbook, fontName, (short) 14, true, false);
        XSSFFont fontHeaderSheet = createFont(workbook, fontName, (short) 12, false, true);

        CellStyle cellStyleHeaderRow = createCellStyle(workbook, fontHeaderRow, true, false);
        cellStyleHeaderRow.setAlignment(HorizontalAlignment.CENTER);

        CellStyle cellStyleHeaderSheet = createCellStyle(workbook, fontHeaderSheet, true, true);
        cellStyleHeaderSheet.setAlignment(HorizontalAlignment.CENTER);

        createCell(rowHeader1, 0, cellStyleHeaderRow, "Отчёт по балансам счетов");
        createCell(rowHeader2, 0, cellStyleHeaderRow,
                "Составлен на " + time.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));

        int numberCell = -1;

        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Счёт");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Тип");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Валюта");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Баланс");
    }

    private void fillSheet(XSSFWorkbook workbook,
                           List<Account> accounts,
                           Map<UUID, String> titleCurrency) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        XSSFFont fontSheet = createFont(workbook, fontName, (short) 11, false, false);

        CellStyle cellStyleSheet = createCellStyle(workbook, fontSheet, true, true);

        int numberRow = 2;
        for (Account acc : accounts) {
            Row row = sheet.createRow(++numberRow);

            int numberCell = -1;
            UUID idCurrency = acc.getCurrency();
            createCell(row, ++numberCell, cellStyleSheet, acc.getTitle());
            createCell(row, ++numberCell, cellStyleSheet, acc.getType());
            createCell(row, ++numberCell, cellStyleSheet, titleCurrency.get(idCurrency));
            createCell(row, ++numberCell, cellStyleSheet, acc.getBalance());
        }
    }

    private CellStyle createCellStyle(XSSFWorkbook workbook, XSSFFont font,
                                      boolean isWrapText, boolean isBorder) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setWrapText(isWrapText);
        cellStyle.setFont(font);

        if (isBorder) {
            cellStyle.setBorderBottom(BorderStyle.MEDIUM);
            cellStyle.setBorderTop(BorderStyle.MEDIUM);
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        }

        return cellStyle;
    }

    private XSSFFont createFont(XSSFWorkbook workbook, String fontName, short height,
                                boolean isItalic, boolean isBold) {
        XSSFFont font = workbook.createFont();

        font.setFontName(fontName);
        font.setFontHeightInPoints(height);
        font.setItalic(isItalic);
        font.setBold(isBold);

        return font;
    }

    private Cell createCell(Row row, int numberCell, CellStyle cellStyle, Object value) {
        Cell cell = row.createCell(numberCell);
        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        cell.setCellStyle(cellStyle);

        return cell;
    }
}