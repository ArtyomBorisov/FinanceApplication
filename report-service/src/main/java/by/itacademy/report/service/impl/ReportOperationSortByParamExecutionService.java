package by.itacademy.report.service.impl;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Operation;
import by.itacademy.report.dto.Params;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.ReportExecutionService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReportOperationSortByParamExecutionService implements ReportExecutionService {

    private final RestHelper restHelper;
    private final Generator generator;
    private final String fontName;
    private final int defaultDayInterval;

    private static final String SHEET_NAME = "ReportExpenseByDate";

    public ReportOperationSortByParamExecutionService(RestHelper restHelper,
                                                      Generator generator,
                                                      @Value("${font_name}") String fontName,
                                                      @Value("${default_day_interval}") int defaultDayInterval) {
        this.restHelper = restHelper;
        this.generator = generator;
        this.fontName = fontName;
        this.defaultDayInterval = defaultDayInterval;
    }

    @Override
    public ByteArrayOutputStream execute(Params params) throws ServerException {
        Set<UUID> accounts = params.getAccounts();
        Set<UUID> categories = params.getCategories();
        LocalDate from = params.getFrom();
        LocalDate to = params.getTo();

        if (to == null) {
            to = generator.now().toLocalDate();
            params.setTo(to);
        }
        if (from == null) {
            from = to.minusDays(defaultDayInterval);
            params.setFrom(from);
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook();) {

            Sheet sheet = workbook.createSheet(SHEET_NAME);

            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 12 * 256);
            sheet.setColumnWidth(2, 35 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 60 * 256);
            sheet.setColumnWidth(5, 10 * 256);
            sheet.setColumnWidth(6, 10 * 256);

            List<Operation> operations = restHelper.getOperations(params);

            Set<UUID> currencies = operations.stream()
                    .map(Operation::getCurrency)
                    .collect(Collectors.toSet());

            Map<UUID, String> currenciesTitleMap = restHelper.getTitles(currencies, UrlType.CURRENCY);
            Map<UUID, String> categoriesTitleMap = restHelper.getTitles(categories, UrlType.CATEGORY);
            Map<UUID, String> accountsTitleMap = restHelper.getTitles(accounts, UrlType.ACCOUNT);

            fillHeader(workbook);
            fillSheet(workbook,
                    operations,
                    currenciesTitleMap,
                    categoriesTitleMap,
                    accountsTitleMap,
                    from,
                    to);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new ServerException(MessageError.REPORT_MAKING_EXCEPTION, e);
        }
    }

    private void fillHeader(XSSFWorkbook workbook) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);

        Row rowHeader = sheet.createRow(0);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        XSSFFont fontHeaderRow = createFont(workbook, fontName, (short) 14, true, false);
        XSSFFont fontHeaderSheet = createFont(workbook, fontName, (short) 12, false, true);

        CellStyle cellStyleHeaderRow = createCellStyle(workbook, fontHeaderRow, true, false);
        cellStyleHeaderRow.setAlignment(HorizontalAlignment.CENTER);

        CellStyle cellStyleHeaderSheet = createCellStyle(workbook, fontHeaderSheet, true, true);

        Row rowTitles = sheet.createRow(4);

        int numberCell = -1;

        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Дата");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Время");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Счёт");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Категория");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Описание операции");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Сумма");
        createCell(rowTitles, ++numberCell, cellStyleHeaderSheet, "Валюта");

        createCell(rowHeader, 0, cellStyleHeaderRow, "Отчёт по операциям");
    }

    private void fillSheet(XSSFWorkbook workbook,
                           List<Operation> operations,
                           Map<UUID, String> currencyTitleMap,
                           Map<UUID, String> categoryTitleMap,
                           Map<UUID, String> accountTitleMap,
                           LocalDate from,
                           LocalDate to) {
        Sheet sheet = workbook.getSheet(SHEET_NAME);
        String pattern = "dd.MM.yyyy";

        Row rowHeader1 = sheet.createRow(1);
        Row rowHeader2 = sheet.createRow(2);
        Row rowHeader3 = sheet.createRow(3);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

        XSSFFont fontSheet = createFont(workbook, fontName, (short) 11, false, false);

        CellStyle cellStyleRows = createCellStyle(workbook, fontSheet, true, false);
        CellStyle cellStyleSheet = createCellStyle(workbook, fontSheet, true, true);

        CellStyle cellStyleSheetRed = createCellStyle(workbook, fontSheet, true, true);
        cellStyleSheetRed.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        cellStyleSheetRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle cellStyleSheetGreen = createCellStyle(workbook, fontSheet, true, true);
        cellStyleSheetGreen.setFillForegroundColor(IndexedColors.LIME.getIndex());
        cellStyleSheetGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowHeader1, 0, cellStyleRows,
                "Даты с " + from.format(DateTimeFormatter.ofPattern(pattern))
                        + " по " + to.format(DateTimeFormatter.ofPattern(pattern)));

        String temp = accountTitleMap.values().toString();
        createCell(rowHeader2, 0, cellStyleRows,
                "Счета: " + temp.substring(1, temp.length() - 1));

        temp = categoryTitleMap.values().toString();
        createCell(rowHeader3, 0, cellStyleRows,
                "Категории: " + temp.substring(1, temp.length() - 1));

        int numberRow = sheet.getLastRowNum();
        for (Operation operation : operations) {
            Row row = sheet.createRow(++numberRow);

            LocalDateTime date = operation.getDate();
            int numberCell = -1;
            createCell(row, ++numberCell, cellStyleSheet,
                    date.toLocalDate().format(DateTimeFormatter.ofPattern(pattern)));

            createCell(row, ++numberCell, cellStyleSheet,
                    date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            createCell(row, ++numberCell, cellStyleSheet,
                    operation.getAccount().getTitle());

            String title = categoryTitleMap.get(operation.getCategory());
            createCell(row, ++numberCell, cellStyleSheet, title);

            String description = operation.getDescription();
            createCell(row, ++numberCell, cellStyleSheet, description != null ? description : "");

            Cell value = row.createCell(++numberCell);
            double sum = operation.getValue();
            value.setCellValue(sum);
            if (sum < 0) {
                value.setCellStyle(cellStyleSheetRed);
            } else {
                value.setCellStyle(cellStyleSheetGreen);
            }

            String currency = currencyTitleMap.get(operation.getCurrency());
            createCell(row, ++numberCell, cellStyleSheet, currency);
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

    private Cell createCell(Row row, int numberCell, CellStyle cellStyle, String value) {
        Cell cell = row.createCell(numberCell);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);

        return cell;
    }
}

























