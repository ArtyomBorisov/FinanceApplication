package by.itacademy.report.service.execution;

import by.itacademy.report.controller.web.controllers.utils.JwtTokenUtil;
import by.itacademy.report.model.api.ReportType;
import by.itacademy.report.service.UserHolder;
import by.itacademy.report.service.api.IReportExecutionService;
import by.itacademy.report.service.api.MessageError;
import by.itacademy.report.service.api.ValidationError;
import by.itacademy.report.service.api.ValidationException;
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

    public ReportOperationSortByParamExecutionService(ObjectMapper mapper,
                                                      UserHolder userHolder) {
        this.mapper = mapper;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public ByteArrayOutputStream execute(Map<String, Object> params) {
        String typeParam = "type";
        String accountsParam = "accounts";
        String categoriesParam = "categories";
        String fromParam = "from";
        String toParam = "to";

        Object accountsObj = params.get(accountsParam);
        Object categoriesObj = params.get(categoriesParam);
        Object fromDateObj = params.get(fromParam);
        Object toDateObj = params.get(toParam);

        Set<UUID> accountsUuidSet = new HashSet<>();
        Set<UUID> categoriesUuidSet = new HashSet<>();
        LocalDateTime from = null;
        LocalDateTime to = null;

        HttpHeaders headers = this.createHeaders();
        List<ValidationError> errors = new ArrayList<>();

        try {
            ReportType sort = ReportType.valueOf((String) params.get(typeParam));
        } catch (IllegalArgumentException e) {
            errors.add(new ValidationError(fromParam, MessageError.INVALID_FORMAT));
        }

        this.checkParamCollectionUuids(accountsObj, accountsParam, accountsUuidSet, this.accountUrl, errors);
        this.checkParamCollectionUuids(categoriesObj, categoriesParam, categoriesUuidSet,
                this.categoryBackendUrl, errors);

        if (fromDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) fromDateObj);
            from = LocalDateTime.of(dt, LocalTime.MIN);
        } else if (fromDateObj != null) {
            errors.add(new ValidationError(fromParam, MessageError.INVALID_FORMAT));
        }

        if (toDateObj instanceof Number) {
            LocalDate dt = LocalDate.ofEpochDay((int) toDateObj);
            to = LocalDateTime.of(dt, LocalTime.MAX);
        } else if (toDateObj != null) {
            errors.add(new ValidationError(toParam, MessageError.INVALID_FORMAT));
        }

        if (from != null && to == null) {
            to = LocalDateTime.of(from.plusDays(this.defaultDayInterval).toLocalDate(), LocalTime.MAX);
        } else if (from == null && to != null) {
            from = LocalDateTime.of(to.minusDays(this.defaultDayInterval).toLocalDate(), LocalTime.MIN);
        } else if (from == null && to == null) {
            errors.add(new ValidationError(fromParam + ", " + toParam,
                    "Требуется передать как минимум один параметр из указанных"));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(this.sheetName);

        sheet.setColumnWidth(0, 12 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 35 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 60 * 256);
        sheet.setColumnWidth(5, 10 * 256);
        sheet.setColumnWidth(6, 10 * 256);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        List<Map<String, Object>> operationList = new ArrayList<>();

        try {
            boolean lastPage = false;
            int page = -1;

            while (!lastPage) {
                String pageJson = this.restTemplate.postForObject(
                        this.operationBackendUrl + "?page=" + ++page + "&size=20",
                        entity,
                        String.class);

                Map<String, Object> pageMap = this.mapper.readValue(pageJson, Map.class);

                operationList.addAll((List<Map<String, Object>>) pageMap.get("content"));

                lastPage = Boolean.parseBoolean(pageMap.get("last").toString());
            }

            Set<UUID> currenciesUuidSet = operationList.stream()
                            .map(map -> UUID.fromString((String) map.get("currency")))
                            .collect(Collectors.toSet());

            Map<UUID, String> currenciesTitleMap = this.getTitles(this.currencyBackendUrl, currenciesUuidSet);
            Map<UUID, String> categoriesTitleMap = this.getTitles(this.categoryBackendUrl, categoriesUuidSet);
            Map<UUID, String> accountsTitleMap = this.getTitles(this.accountBackendUrl, accountsUuidSet);

            this.fillHeader(workbook);
            this.fillSheet(workbook, operationList,
                    currenciesTitleMap, categoriesTitleMap, accountsTitleMap,
                    from.toLocalDate(), to.toLocalDate());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            workbook.close();

            return outputStream;

        } catch (IOException e) {
            throw new RuntimeException("Проблемы с созданием отчёта", e);
        }
    }

    private Map<UUID, String> getTitles(String url, Set<UUID> uuids) throws JsonProcessingException {
        Map<UUID, String> data = new HashMap<>();
        boolean lastPage = false;
        int page = -1;

        HttpHeaders headers = this.createHeaders();

        HttpEntity<Set<UUID>> request = new HttpEntity<>(uuids, headers);

        while (!lastPage) {
            String pageJson = this.restTemplate.postForObject(
                    url + "?page=" + ++page + "&size=20",
                    request,
                    String.class);

            Map<String, Object> pageMap = this.mapper.readValue(pageJson, Map.class);

            List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");

            for (Map<String, Object> map : content) {
                data.put(UUID.fromString((String) map.get("uuid")), (String) map.get("title"));
            }

            lastPage = Boolean.parseBoolean(pageMap.get("last").toString());
        }

        return data;
    }

    private void checkParamCollectionUuids(Object obj,
                                           String paramName,
                                           Set<UUID> set,
                                           String url,
                                           List<ValidationError> errors) {
        if (obj instanceof Collection) {
            try {
                set = ((Collection<?>) obj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toSet());
            } catch (IllegalArgumentException e) {
                errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
            }

            HttpHeaders headers = this.createHeaders();
            HttpEntity<Object> entity = new HttpEntity(headers);

            for (UUID id : set) {
                try {
                    String urlWithId = url + "/" + id;
                    this.restTemplate.exchange(urlWithId, HttpMethod.GET, entity, String.class);
                } catch (HttpStatusCodeException e) {
                    errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                }
            }
        } else if (obj != null) {
            errors.add(new ValidationError(paramName, MessageError.INVALID_FORMAT));
        }
    }

    private void fillHeader(XSSFWorkbook workbook) {
        Sheet sheet = workbook.getSheet(this.sheetName);

        Row rowHeader = sheet.createRow(0);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        XSSFFont fontHeaderRow = workbook.createFont();
        fontHeaderRow.setFontName(this.fontName);
        fontHeaderRow.setFontHeightInPoints((short) 14);
        fontHeaderRow.setItalic(true);

        XSSFFont fontHeaderSheet = workbook.createFont();
        fontHeaderSheet.setFontName(this.fontName);
        fontHeaderSheet.setFontHeightInPoints((short) 12);
        fontHeaderSheet.setBold(true);

        CellStyle cellStyleHeaderRow = workbook.createCellStyle();
        cellStyleHeaderRow.setWrapText(true);
        cellStyleHeaderRow.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeaderRow.setFont(fontHeaderRow);

        CellStyle cellStyleHeaderSheet = workbook.createCellStyle();
        cellStyleHeaderSheet.setWrapText(true);
        cellStyleHeaderSheet.setFont(fontHeaderSheet);
        cellStyleHeaderSheet.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderTop(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderRight(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderLeft(BorderStyle.MEDIUM);

        Row rowTitles = sheet.createRow(4);

        Cell title1 = rowTitles.createCell(0);
        title1.setCellValue("Дата");
        title1.setCellStyle(cellStyleHeaderSheet);

        Cell title2 = rowTitles.createCell(1);
        title2.setCellValue("Время");
        title2.setCellStyle(cellStyleHeaderSheet);

        Cell title3 = rowTitles.createCell(2);
        title3.setCellValue("Счёт");
        title3.setCellStyle(cellStyleHeaderSheet);

        Cell title4 = rowTitles.createCell(3);
        title4.setCellValue("Категория");
        title4.setCellStyle(cellStyleHeaderSheet);

        Cell title5 = rowTitles.createCell(4);
        title5.setCellValue("Описание операции");
        title5.setCellStyle(cellStyleHeaderSheet);

        Cell title6 = rowTitles.createCell(5);
        title6.setCellValue("Сумма");
        title6.setCellStyle(cellStyleHeaderSheet);

        Cell title7 = rowTitles.createCell(6);
        title7.setCellValue("Валюта");
        title7.setCellStyle(cellStyleHeaderSheet);

        Cell headerCell = rowHeader.createCell(0);
        headerCell.setCellValue("Отчёт по операциям");
        headerCell.setCellStyle(cellStyleHeaderRow);
    }

    private void fillSheet(XSSFWorkbook workbook,
                           List<Map<String, Object>> operations,
                           Map<UUID, String> currencyTitleMap,
                           Map<UUID, String> categoryTitleMap,
                           Map<UUID, String> accountTitleMap,
                           LocalDate from,
                           LocalDate to) throws JsonProcessingException {
        Sheet sheet = workbook.getSheet(this.sheetName);
        String pattern = "dd.MM.yyyy";

        Row rowHeader1 = sheet.createRow(1);
        Row rowHeader2 = sheet.createRow(2);
        Row rowHeader3 = sheet.createRow(3);

        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

        XSSFFont fontSheet = workbook.createFont();
        fontSheet.setFontName(this.fontName);
        fontSheet.setFontHeightInPoints((short) 11);

        CellStyle cellStyleRows = workbook.createCellStyle();
        cellStyleRows.setWrapText(true);
        cellStyleRows.setFont(fontSheet);

        CellStyle cellStyleSheet = workbook.createCellStyle();
        cellStyleSheet.setWrapText(true);
        cellStyleSheet.setFont(fontSheet);
        cellStyleSheet.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderTop(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderRight(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderLeft(BorderStyle.MEDIUM);

        CellStyle cellStyleSheetRed = workbook.createCellStyle();
        cellStyleSheetRed.setWrapText(true);
        cellStyleSheetRed.setFont(fontSheet);
        cellStyleSheetRed.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleSheetRed.setBorderTop(BorderStyle.MEDIUM);
        cellStyleSheetRed.setBorderRight(BorderStyle.MEDIUM);
        cellStyleSheetRed.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleSheetRed.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        cellStyleSheetRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle cellStyleSheetGreen = workbook.createCellStyle();
        cellStyleSheetGreen.setWrapText(true);
        cellStyleSheetGreen.setFont(fontSheet);
        cellStyleSheetGreen.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleSheetGreen.setBorderTop(BorderStyle.MEDIUM);
        cellStyleSheetGreen.setBorderRight(BorderStyle.MEDIUM);
        cellStyleSheetGreen.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleSheetGreen.setFillForegroundColor(IndexedColors.LIME.getIndex());
        cellStyleSheetGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell cellHeader = rowHeader1.createCell(0);
        cellHeader.setCellValue("Даты с " + from.format(DateTimeFormatter.ofPattern(pattern))
                + " по " + to.format(DateTimeFormatter.ofPattern(pattern)));
        cellHeader.setCellStyle(cellStyleRows);

        cellHeader = rowHeader2.createCell(0);
        String temp = accountTitleMap.values().toString();
        cellHeader.setCellValue("Счета: " + temp.substring(1, temp.length() - 1));
        cellHeader.setCellStyle(cellStyleRows);

        cellHeader = rowHeader3.createCell(0);
        temp = categoryTitleMap.values().toString();
        cellHeader.setCellValue("Категории: " + temp.substring(1, temp.length() - 1));
        cellHeader.setCellStyle(cellStyleRows);

        int numberRow = sheet.getLastRowNum();
        for (Map<String, Object> operation : operations) {
            Row row = sheet.createRow(++numberRow);

            Cell date = row.createCell(0);
            date.setCellValue(this.longToLDT(((Number) operation.get("date")).longValue())
                    .toLocalDate().format(DateTimeFormatter.ofPattern(pattern)));
            date.setCellStyle(cellStyleSheet);

            Cell time = row.createCell(1);
            time.setCellValue(this.longToLDT(((Number) operation.get("date")).longValue())
                    .toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            time.setCellStyle(cellStyleSheet);

            Map<String, Object> map = (Map<String, Object>) operation.get("account");
            Cell account = row.createCell(2);
            account.setCellValue((String) map.get("title"));
            account.setCellStyle(cellStyleSheet);

            Cell category = row.createCell(3);
            category.setCellValue(categoryTitleMap.get(UUID.fromString((String) operation.get("category"))));
            category.setCellStyle(cellStyleSheet);

            Cell description = row.createCell(4);
            description.setCellValue(operation.get("description") == null ? "" : operation.get("description").toString());
            description.setCellStyle(cellStyleSheet);

            Cell value = row.createCell(5);
            double sum = (Double) operation.get("value");
            value.setCellValue(sum);
            if (sum < 0) {
                value.setCellStyle(cellStyleSheetRed);
            } else {
                value.setCellStyle(cellStyleSheetGreen);
            }

            Cell currency = row.createCell(6);
            currency.setCellValue(currencyTitleMap.get(UUID.fromString((String) operation.get("currency"))));
            currency.setCellStyle(cellStyleSheet);
        }
    }

    private LocalDateTime longToLDT(Long num) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(num), TimeZone.getDefault().toZoneId());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(this.userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return headers;
    }
}

























