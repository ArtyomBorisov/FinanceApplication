package by.itacademy.report.service.impl;

import by.itacademy.report.constant.MessageError;
import by.itacademy.report.constant.UrlType;
import by.itacademy.report.dto.Account;
import by.itacademy.report.dto.Params;
import by.itacademy.report.exception.ServerException;
import by.itacademy.report.service.ReportExecutionService;
import by.itacademy.report.service.RestHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class ReportBalanceExecutionService implements ReportExecutionService {

    private final RestHelper restHelper;
    private final String fontName;

    private static final String SHEET_NAME = "ReportBalance";

    public ReportBalanceExecutionService(RestHelper restHelper,
                                         @Value("${font_name}") String fontName) {
        this.restHelper = restHelper;
        this.fontName = fontName;
    }

    @Override
    public ByteArrayOutputStream execute(Params params) throws ServerException {
        Set<UUID> uuids = params.getAccounts();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);
            LocalDateTime time = LocalDateTime.now();

            sheet.setColumnWidth(0, 35 * 256);
            sheet.setColumnWidth(1, 17 * 256);
            sheet.setColumnWidth(2, 10 * 256);
            sheet.setColumnWidth(3, 10 * 256);

            fillHeader(workbook, time);

            boolean empty = (uuids == null) || uuids.isEmpty();
            List<Account> accounts = empty ? restHelper.getAccounts() : restHelper.getAccounts(uuids);

            Set<UUID> currencies = accounts.stream()
                    .map(Account::getCurrency)
                    .collect(Collectors.toSet());

            Map<UUID, String> titlesCurrency = restHelper.getTitles(currencies, UrlType.CURRENCY);

            fillSheet(workbook, accounts, titlesCurrency);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new ServerException(MessageError.REPORT_MAKING_EXCEPTION, e);
        }
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
                           Map<UUID, String> titlesCurrency) {
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
            createCell(row, ++numberCell, cellStyleSheet, titlesCurrency.get(idCurrency));
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