package by.itacademy.report.service.execution;

import by.itacademy.report.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.report.enums.ReportType;
import by.itacademy.report.service.UserHolder;
import by.itacademy.report.service.api.IReportExecutionService;
import by.itacademy.report.exception.MessageError;
import by.itacademy.report.exception.ValidationError;
import by.itacademy.report.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class ReportOperationSortByParamExecutionService implements IReportExecutionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final UserHolder userHolder;

    @Value("${classifier_currency_backend_url}")
    private String currencyBackendUrl;

    @Value("${classifier_category_backend_url}")
    private String categoryBackendUrl;

    @Value("${operation_backend_url}")
    private String operationBackendUrl;

    @Value("${account_backend_url}")
    private String accountBackendUrl;

    @Value("${account_url}")
    private String accountUrl;

    @Value("${font_name}")
    private String fontName;

    @Value("${default_day_interval}")
    private int defaultDayInterval;

    private final String sheetName = "ReportExpenseByDate";
    private final String typeParam = "type";
    private final String accountsParam = "accounts";
    private final String categoriesParam = "categories";
    private final String fromParam = "from";
    private final String toParam = "to";

    public ReportOperationSortByParamExecutionService(ObjectMapper mapper,
                                                      UserHolder userHolder) {
        this.mapper = mapper;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ByteArrayOutputStream execute(Map<String, Object> rawParams) throws Exception {
        Map<String, Object> checkedParams = checkParams(rawParams);

        Set<UUID> accountsUuidSet = (Set<UUID>) checkedParams.get(accountsParam);
        Set<UUID> categoriesUuidSet = (Set<UUID>) checkedParams.get(categoriesParam);
        LocalDateTime from = (LocalDateTime) checkedParams.get(fromParam);
        LocalDateTime to = (LocalDateTime) checkedParams.get(toParam);

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 35 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 60 * 256);
        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 10 * 256);

        HttpHeaders headers = createHeaders();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(checkedParams, headers);
        List<Map<String, Object>> operationList = new ArrayList<>();

        try {
            boolean lastPage = false;
            int page = -1;

            while (!lastPage) {
                String pageJson = restTemplate.postForObject(
                        operationBackendUrl + "?page=" + ++page + "&size=20",
                        entity,
                        String.class);

                Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

                operationList.addAll((List<Map<String, Object>>) pageMap.get("content"));

                lastPage = Boolean.parseBoolean(pageMap.get("last").toString());
            }

            Set<UUID> currenciesUuidSet = operationList.stream()
                            .map(map -> UUID.fromString((String) map.get("currency")))
                            .collect(Collectors.toSet());

            Map<UUID, String> currenciesTitleMap = getTitles(currencyBackendUrl, currenciesUuidSet);
            Map<UUID, String> categoriesTitleMap = getTitles(categoryBackendUrl, categoriesUuidSet);
            Map<UUID, String> accountsTitleMap = getTitles(accountBackendUrl, accountsUuidSet);

            fillHeader(workbook);
            fillSheet(workbook, operationList,
                    currenciesTitleMap, categoriesTitleMap, accountsTitleMap,
                    from.toLocalDate(), to.toLocalDate());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            workbook.close();

            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException(MessageError.REPORT_ERROR, e);
        }
    }

    private Map<String, Object> checkParams(Map<String, Object> rawParams) {
        Map<String, Object> checkedParams = new HashMap<>();

        Object accountsObj = rawParams.get(accountsParam);
        Object categoriesObj = rawParams.get(categoriesParam);
        Object fromDateObj = rawParams.get(fromParam);
        Object toDateObj = rawParams.get(toParam);

        List<ValidationError> errors = new ArrayList<>();

        try {
            ReportType sort = ReportType.valueOf((String) rawParams.get(typeParam));
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError(typeParam, MessageError.INVALID_FORMAT));
        }

        Set<UUID> accountsUuidSet = checkParamCollectionUuids(accountsObj, accountsParam, accountUrl, errors);
        Set<UUID> categoriesUuidSet = checkParamCollectionUuids(
                categoriesObj, categoriesParam, categoryBackendUrl, errors);

        LocalDateTime from = checkParamDate(fromDateObj, LocalTime.MIN, fromParam, errors);
        LocalDateTime to = checkParamDate(toDateObj, LocalTime.MAX, toParam, errors);

        if (from != null && to == null) {
            to = LocalDateTime.of(from.plusDays(defaultDayInterval).toLocalDate(), LocalTime.MAX);
        } else if (from == null && to != null) {
            from = LocalDateTime.of(to.minusDays(defaultDayInterval).toLocalDate(), LocalTime.MIN);
        } else if (from == null && to == null) {
            errors.add(new ValidationError(fromParam + ", " + toParam,
                    "Требуется передать как минимум один параметр из указанных"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        checkedParams.put(accountsParam, accountsUuidSet);
        checkedParams.put(categoriesParam, categoriesUuidSet);
        checkedParams.put(fromParam, from);
        checkedParams.put(toParam, to);

        return checkedParams;
    }

    private Map<UUID, String> getTitles(String url, Set<UUID> uuids) throws JsonProcessingException {
        Map<UUID, String> data = new HashMap<>();
        boolean lastPage = false;
        int page = -1;

        HttpHeaders headers = createHeaders();

        HttpEntity<Set<UUID>> request = new HttpEntity<>(uuids, headers);

        while (!lastPage) {
            String pageJson = restTemplate.postForObject(
                    url + "?page=" + ++page + "&size=20",
                    request,
                    String.class);

            Map<String, Object> pageMap = mapper.readValue(pageJson, Map.class);

            List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");

            for (Map<String, Object> map : content) {
                data.put(UUID.fromString((String) map.get("uuid")), (String) map.get("title"));
            }

            lastPage = Boolean.parseBoolean(pageMap.get("last").toString());
        }

        return data;
    }

    private Set<UUID> checkParamCollectionUuids(Object obj,
                                                String paramName,
                                                String url,
                                                List<ValidationError> errors) {
        Set<UUID> set = new HashSet<>();

        if (obj instanceof Collection) {
            try {
                set = ((Collection<?>) obj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
            }

            HttpHeaders headers = createHeaders();
            HttpEntity<Object> entity = new HttpEntity(headers);

            for (UUID id : set) {
                try {
                    String urlWithId = url + "/" + id;
                    restTemplate.exchange(urlWithId, HttpMethod.GET, entity, String.class);
                } catch (HttpStatusCodeException e) {
                    errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                }
            }
        } else if (obj != null) {
            errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
        }

        return set;
    }

    private LocalDateTime checkParamDate(Object obj, LocalTime time, String paramName, List<ValidationError> errors) {
        LocalDateTime ldt = null;

        if (obj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) obj);
            ldt = LocalDateTime.of(dt, time);
        } else if (obj != null) {
            errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
        }

        return ldt;
    }

    private void fillHeader(XSSFWorkbook workbook) {
        Sheet sheet = workbook.getSheet(this.sheetName);

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
                           List<Map<String, Object>> operations,
                           Map<UUID, String> currencyTitleMap,
                           Map<UUID, String> categoryTitleMap,
                           Map<UUID, String> accountTitleMap,
                           LocalDate from,
                           LocalDate to) throws JsonProcessingException {
        Sheet sheet = workbook.getSheet(sheetName);
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
        for (Map<String, Object> operation : operations) {
            Row row = sheet.createRow(++numberRow);

            int numberCell = -1;
            createCell(row, ++numberCell, cellStyleSheet,
                    longToLDT(((Number) operation.get("date")).longValue())
                            .toLocalDate().format(DateTimeFormatter.ofPattern(pattern)));

            createCell(row, ++numberCell, cellStyleSheet,
                    longToLDT(((Number) operation.get("date")).longValue())
                            .toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));


            Map<String, Object> map = (Map<String, Object>) operation.get("account");
            createCell(row, ++numberCell, cellStyleSheet,
                    (String) map.get("title"));

            createCell(row, ++numberCell, cellStyleSheet,
                    categoryTitleMap.get(UUID.fromString((String) operation.get("category"))));

            createCell(row, ++numberCell, cellStyleSheet,
                    operation.get("description") == null ? "" : operation.get("description").toString());

            Cell value = row.createCell(++numberCell);
            double sum = (Double) operation.get("value");
            value.setCellValue(sum);
            if (sum < 0) {
                value.setCellStyle(cellStyleSheetRed);
            } else {
                value.setCellStyle(cellStyleSheetGreen);
            }

            createCell(row, ++numberCell, cellStyleSheet,
                    currencyTitleMap.get(UUID.fromString((String) operation.get("currency"))));
        }
    }

    private LocalDateTime longToLDT(Long num) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(num), TimeZone.getDefault().toZoneId());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return headers;
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

























