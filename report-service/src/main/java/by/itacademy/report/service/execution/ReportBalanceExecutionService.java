package by.itacademy.report.service.execution;

import by.itacademy.report.controller.web.controllers.utils.JwtTokenUtil;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Transactional(readOnly = true)
public class ReportBalanceExecutionService implements IReportExecutionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final UserHolder userHolder;

    @Value("${account_url}")
    private String accountUrl;

    @Value("${classifier_currency_backend_url}")
    private String currencyBackendUrl;

    @Value("${account_backend_url}")
    private String accountBackendUrl;

    @Value("${font_name}")
    private String fontName;

    private final String sheetName = "ReportBalance";

    public ReportBalanceExecutionService(ObjectMapper mapper, UserHolder userHolder) {
        this.mapper = mapper;
        this.userHolder = userHolder;
        this.restTemplate = new RestTemplate();
    }

    @Transactional
    @Override
    public ByteArrayOutputStream execute(Map<String, Object> params) {
        Object obj = params.get("accounts");
        String acc = "accounts: id счёта";
        List<UUID> accountsUuid = null;
        List<ValidationError> errors = new ArrayList<>();

        HttpHeaders headers = this.createHeaders();

        if (obj instanceof Collection) {
            try {
                accountsUuid = ((Collection<?>) obj).stream()
                        .map(uuid -> UUID.fromString((String) uuid))
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                throw new ValidationException(new ValidationError(acc, MessageError.INVALID_FORMAT));
            }

            HttpEntity<Object> entity = new HttpEntity(headers);

            for (UUID id : accountsUuid) {
                try {
                    String url = this.accountUrl + "/" + id;
                    this.restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
                } catch (HttpStatusCodeException e) {
                    errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                }
            }
        } else if (obj != null) {
            errors.add(new ValidationError(acc, MessageError.INVALID_FORMAT));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(this.sheetName);
        LocalDateTime time = LocalDateTime.now();

        sheet.setColumnWidth(0, 35 * 256);
        sheet.setColumnWidth(1, 17 * 256);
        sheet.setColumnWidth(2, 10 * 256);
        sheet.setColumnWidth(3, 10 * 256);

        this.fillHeader(workbook, time);

        List<Map<String, Object>> accountsList = new ArrayList<>();

        try {
            boolean lastPage = false;
            int temp = -1;

            boolean empty = (accountsUuid == null) || accountsUuid.isEmpty();
            HttpEntity<Object> entity = empty ? new HttpEntity<>(headers) : new HttpEntity<>(accountsUuid, headers);

            while (!lastPage) {
                String pageJson;

                if (!empty) {
                    pageJson = this.restTemplate.postForObject(
                            this.accountBackendUrl + "?page=" + ++temp + "&size=20",
                            entity,
                            String.class);
                } else {
                    pageJson = this.restTemplate.exchange(
                            this.accountUrl + "?page=" + ++temp + "&size=20",
                            HttpMethod.GET,
                            entity,
                            String.class).getBody();
                }

                Map<String, Object> pageMap = this.mapper.readValue(pageJson, Map.class);

                accountsList.addAll((List<Map<String, Object>>) pageMap.get("content"));

                lastPage = Boolean.parseBoolean(pageMap.get("last").toString());
            }

            Set<UUID> uuidsCurrency = accountsList.stream()
                    .map(map -> UUID.fromString((String) map.get("currency")))
                    .collect(Collectors.toSet());

            Map<UUID, String> titlesCurrency = this.getTitles(this.currencyBackendUrl, uuidsCurrency);

            this.fillSheet(workbook, accountsList, titlesCurrency);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            workbook.close();

            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка составления отчёта");
        }
    }

    private void fillHeader(XSSFWorkbook workbook,
                            LocalDateTime time) {
        Sheet sheet = workbook.getSheet(this.sheetName);

        Row rowHeader1 = sheet.createRow(0);
        Row rowHeader2 = sheet.createRow(1);
        Row rowTitles = sheet.createRow(2);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));

        XSSFFont fontHeaderRow = workbook.createFont();
        fontHeaderRow.setFontName(this.fontName);
        fontHeaderRow.setFontHeightInPoints((short) 14);
        fontHeaderRow.setItalic(true);

        XSSFFont fontHeaderSheet = workbook.createFont();
        fontHeaderSheet.setFontName(this.fontName);
        fontHeaderSheet.setFontHeightInPoints((short) 12);
        fontHeaderSheet.setBold(true);

        CellStyle cellStyleHeaderRow = workbook.createCellStyle();
        cellStyleHeaderRow.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeaderRow.setFont(fontHeaderRow);

        CellStyle cellStyleHeaderSheet = workbook.createCellStyle();
        cellStyleHeaderSheet.setAlignment(HorizontalAlignment.CENTER);
        cellStyleHeaderSheet.setFont(fontHeaderSheet);
        cellStyleHeaderSheet.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderTop(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderRight(BorderStyle.MEDIUM);
        cellStyleHeaderSheet.setBorderLeft(BorderStyle.MEDIUM);

        Cell headerCell1 = rowHeader1.createCell(0);
        headerCell1.setCellValue("Отчёт по балансам счетов");
        headerCell1.setCellStyle(cellStyleHeaderRow);

        Cell headerCell2 = rowHeader2.createCell(0);
        headerCell2.setCellValue("Составлен на " + time.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy")));
        headerCell2.setCellStyle(cellStyleHeaderRow);

        Cell title1 = rowTitles.createCell(0);
        title1.setCellValue("Счёт");
        title1.setCellStyle(cellStyleHeaderSheet);

        Cell title2 = rowTitles.createCell(1);
        title2.setCellValue("Тип");
        title2.setCellStyle(cellStyleHeaderSheet);

        Cell title3 = rowTitles.createCell(2);
        title3.setCellValue("Валюта");
        title3.setCellStyle(cellStyleHeaderSheet);

        Cell title4 = rowTitles.createCell(3);
        title4.setCellValue("Баланс");
        title4.setCellStyle(cellStyleHeaderSheet);
    }

    private void fillSheet(XSSFWorkbook workbook,
                           List<Map<String, Object>> accounts,
                           Map<UUID, String> titlesCurrency) throws JsonProcessingException {
        Sheet sheet = workbook.getSheet(this.sheetName);

        XSSFFont fontSheet = workbook.createFont();
        fontSheet.setFontName(this.fontName);
        fontSheet.setFontHeightInPoints((short) 11);

        CellStyle cellStyleSheet = workbook.createCellStyle();
        cellStyleSheet.setWrapText(true);
        cellStyleSheet.setFont(fontSheet);
        cellStyleSheet.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderTop(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderRight(BorderStyle.MEDIUM);
        cellStyleSheet.setBorderLeft(BorderStyle.MEDIUM);

        int numberRow = 2;
        for (Map<String, Object> acc : accounts) {
            Row row = sheet.createRow(++numberRow);

            Cell titleCell = row.createCell(0);
            titleCell.setCellValue((String) acc.get("title"));
            titleCell.setCellStyle(cellStyleSheet);

            Cell typeCell = row.createCell(1);
            typeCell.setCellValue((String) acc.get("type"));
            typeCell.setCellStyle(cellStyleSheet);

            Cell currencyCell = row.createCell(2);
            currencyCell.setCellValue(titlesCurrency.get(
                    UUID.fromString((String) acc.get("currency"))));
            currencyCell.setCellStyle(cellStyleSheet);

            Cell balanceCell = row.createCell(3);
            balanceCell.setCellValue((Double) acc.get("balance"));
            balanceCell.setCellStyle(cellStyleSheet);
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

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = JwtTokenUtil.generateAccessToken(this.userHolder.getUser());
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }
}