package com.altynbekova.aikamtask;

import com.altynbekova.aikamtask.exception.ServiceException;
import com.altynbekova.aikamtask.model.Criterion;
import com.altynbekova.aikamtask.model.ErrorOutput;
import com.altynbekova.aikamtask.service.SearchService;
import com.altynbekova.aikamtask.service.StatisticsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class App {
    private static final String SEARCH = "search";
    private static final String CRITERIA = "criterias";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static void main(String[] args) {
        String operationType = args[0];
        String input = args[1];
        String output = args[2];

        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));

        File outputFile = new File(output);
        InputStream inputStream = App.class.getClassLoader().getResourceAsStream(input);
        try {
            if (inputStream == null)
                throw new FileNotFoundException("Cannot find input file " + input);

            JsonNode root = mapper.readTree(inputStream);

            if (SEARCH.equalsIgnoreCase(operationType)) {
                JsonNode criteriaNode = root.path(CRITERIA);
                List<Criterion> criteria = Arrays.asList(mapper.treeToValue(criteriaNode, Criterion[].class));
                SearchService searchService = new SearchService();
                mapper.writeValue(outputFile, searchService.findCustomers(criteria));
            } else {
                LocalDate startDate = LocalDate.parse(root.path(START_DATE).asText());
                LocalDate endDate = LocalDate.parse(root.path(END_DATE).asText());
                StatisticsService statisticsService = new StatisticsService();
                mapper.writeValue(outputFile, statisticsService.getStatistics(startDate, endDate));
            }
        } catch (IOException | ServiceException | DateTimeParseException e) {
            String message;
            if (e instanceof DateTimeParseException)
                message = "StartDate/endDate property is missed or format of its value is invalid " + e.getMessage();
            else
                message = e.getMessage();
            try {
                mapper.writeValue(outputFile, new ErrorOutput(message));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}