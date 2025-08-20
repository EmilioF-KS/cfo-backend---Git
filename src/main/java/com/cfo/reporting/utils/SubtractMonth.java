package com.cfo.reporting.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SubtractMonth {
    public static String subtractOneMonth(String fecha) {
        if (fecha == null || fecha.length() != 6) {
            throw new IllegalArgumentException("La fecha debe tener 6 dígitos (yyyyMM)");
        }

        try {
            // Convertir a números para validación básica
            int año = Integer.parseInt(fecha.substring(0, 4));
            int mes = Integer.parseInt(fecha.substring(4, 6));

            if (mes < 1 || mes > 12) {
                throw new IllegalArgumentException("Mes inválido: " + mes);
            }

            // Usar LocalDate para el cálculo
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate localDate = LocalDate.parse(fecha + "01", formatter);

            LocalDate fechaRestada = localDate.minusMonths(1);

            return fechaRestada.format(DateTimeFormatter.ofPattern("yyyyMM"));

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La fecha debe contener solo números");
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida: " + fecha);
        }
    }
}
