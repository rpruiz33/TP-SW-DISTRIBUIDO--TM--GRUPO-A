package com.empuje.web_service.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventPerMonthDTO {

    private String month;
    private List<EventReportDTO> events;




    public static List<EventPerMonthDTO> fromEventReports(List<EventReportDTO> eventReports) {

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es"));

        // Agrupamos por mes
        Map<String, List<EventReportDTO>> grouped = new HashMap<>();
        for (EventReportDTO e : eventReports) {
            LocalDateTime fecha = LocalDateTime.parse(e.getDateRegistration(),FORMATTER);
            String month = fecha.format(monthFormatter);
            grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(e);
        }

        // Ordenamos los meses de más antiguo a más reciente
        List<String> sortedMonths = grouped.keySet().stream()
                .sorted((m1, m2) -> {
                    try {
                        LocalDate d1 = LocalDate.parse("01 " + m1, DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("es")));

                        LocalDate d2 = LocalDate.parse("01 " + m2, DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.forLanguageTag("es")));

                        return d1.compareTo(d2); // más antiguo primero
                    } catch (Exception ex) {
                        return 0;
                    }
                })
                .toList();

        // Creamos la lista final de DTOs
        List<EventPerMonthDTO> result = new ArrayList<>();
        for (String month : sortedMonths) {
            List<EventReportDTO> eventsInMonth = grouped.get(month);
            // Orden descendente dentro del mes
            eventsInMonth.sort((a, b) -> a.getDateRegistration().compareTo(b.getDateRegistration()));
            result.add(new EventPerMonthDTO(month, eventsInMonth));
        }

        return result;
    }


}
