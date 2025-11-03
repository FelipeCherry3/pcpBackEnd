package com.rubim.pcpBackEnd.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JsonParserUtil {

    private static final DateTimeFormatter ISO_DATE      = DateTimeFormatter.ISO_LOCAL_DATE;       // yyyy-MM-dd
    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;  // yyyy-MM-ddTHH:mm:ss
    private static final DateTimeFormatter ISO_OFFSET    = DateTimeFormatter.ISO_OFFSET_DATE_TIME; // ...Z / ...-03:00
    private static final DateTimeFormatter DMY_SLASH     = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter DMY_DASH      = DateTimeFormatter.ofPattern("dd-MM-uuuu");
    private static final DateTimeFormatter YMD_SLASH     = DateTimeFormatter.ofPattern("uuuu/MM/dd");

    public static Long toLong(Object obj) {
        if (obj instanceof Number number) {
            return number.longValue();
        }
        if (obj instanceof String str) {
            try {
                return Long.valueOf(str);
            } catch (NumberFormatException e) {
                // Ignora
            }
        }
        return null;
    }

    // Converte para OffsetDateTime
    public static OffsetDateTime toOffsetDateTime(Object o, ZoneId zone) {
    if (o == null) return null;

    if (o instanceof OffsetDateTime odt) return odt;
    if (o instanceof ZonedDateTime zdt)  return zdt.toOffsetDateTime();
    if (o instanceof LocalDateTime ldt)  return ldt.atZone(zone).toOffsetDateTime();
    if (o instanceof java.sql.Timestamp ts) return ts.toInstant().atZone(zone).toOffsetDateTime();
    if (o instanceof java.util.Date ud)  return ud.toInstant().atZone(zone).toOffsetDateTime();
    if (o instanceof java.sql.Date d)     return d.toLocalDate().atStartOfDay(zone).toOffsetDateTime();
    if (o instanceof LocalDate ld)        return ld.atStartOfDay(zone).toOffsetDateTime();
    if (o instanceof String s) {
        try { return OffsetDateTime.parse(s); } catch (Exception ignore) {}
        try { return LocalDateTime.parse(s).atZone(zone).toOffsetDateTime(); } catch (Exception ignore) {}
        return null;
    }
    // Debug opcional pra ver exatamente o que está vindo:
    // System.out.println("DEBUG data tipo: " + o.getClass().getName() + " -> " + o);
    return null;
}

        /** Converte para Integer (aceita Number ou String). */
    public static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) return null;
        return Integer.valueOf(s);
    }

    /**
     * Converte para BigDecimal.
     * Aceita Number e String (suporta vírgula como separador decimal).
     */
    public static BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal b) return b;
        if (o instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) return null;
        // troca vírgula por ponto para casos "123,45"
        s = s.replace(",", ".");
        return new BigDecimal(s);
    }

    /** Converte para LocalDate (ISO-8601, ex: 2023-01-12). */
    public static LocalDate toLocalDate(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) return null;
        return LocalDate.parse(s); // ISO por padrão
    }

    /** Leniente: tolera valores inválidos do Bling, outros formatos e datetime. */
    public static LocalDate toLocalDateLenient(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) return null;

        // placeholders comuns do Bling
        if ("0000-00-00".equals(s) || "0000-00-00 00:00:00".equals(s)) return null;

        // 1) ISO date
        try { return LocalDate.parse(s, ISO_DATE); } catch (DateTimeParseException ignored) {}

        // 2) ISO datetime com offset (Z / -03:00) ou sem offset
        try { return OffsetDateTime.parse(s, ISO_OFFSET).toLocalDate(); } catch (DateTimeParseException ignored) {}
        try { return LocalDateTime.parse(s, ISO_DATE_TIME).toLocalDate(); } catch (DateTimeParseException ignored) {}

        // 3) Caso venha "yyyy-MM-dd HH:mm:ss" -> pega só a parte da data
        if (s.length() >= 10 && s.charAt(4) == '-' && s.charAt(7) == '-') {
            try { return LocalDate.parse(s.substring(0, 10), ISO_DATE); } catch (DateTimeParseException ignored) {}
        }

        // 4) Formatos brasileiros/comuns
        for (DateTimeFormatter f : new DateTimeFormatter[]{DMY_SLASH, DMY_DASH, YMD_SLASH}) {
            try { return LocalDate.parse(s, f); } catch (DateTimeParseException ignored) {}
        }

        // 5) Último recurso: tentar dd/MM/yy → virar dd/MM/20yy
        if (s.contains("/")) {
            String[] p = s.split("/");
            if (p.length == 3) {
                try {
                    int d = Integer.parseInt(p[0]);
                    int m = Integer.parseInt(p[1]); // "08" funciona normal aqui
                    String yy = p[2];
                    int y = (yy.length() == 2) ? Integer.parseInt("20" + yy) : Integer.parseInt(yy);
                    return LocalDate.of(y, m, d);
                } catch (Exception ignored) {}
            }
        }

        // não conseguiu parsear -> trata como null para não quebrar fluxo
        return null;
    }

    /** Converte para String "limpa" (trim); null => null. */
    public static String toStringSafe(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
    }

    /** Converte para Boolean básico (true/false/1/0/"true"/"false"). */
    public static Boolean toBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        String s = String.valueOf(o).trim().toLowerCase();
        if (s.isEmpty()) return null;
        if (s.equals("true") || s.equals("1")) return true;
        if (s.equals("false") || s.equals("0")) return false;
        return null;
    }


    public  static Object firstNonNull(Object... objs) {
        if (objs == null) return null;
        for (Object o : objs) {
            if (o != null) return o;
        }
        return null;
    }
}