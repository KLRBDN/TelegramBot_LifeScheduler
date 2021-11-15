package org.example;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class YearsDataBase {
    private static YearsDataBase instance;
    private final static String timeZone = "GMT+05:00";
    private Map<Integer, Year> years;

    private YearsDataBase() {
        this.years = new HashMap<Integer, Year>();
    }

    public static YearsDataBase getInstance(){
        if (instance == null){
            instance = new YearsDataBase();
        }
        return instance;
    }

    public Map<Integer, Year> getAllYears(){
        return this.years;
    }

    public Year getYear(Integer yearNumber){
        if (!years.containsKey(yearNumber)){
            if (!tryAddYear(yearNumber))
                return null;
        }
        return years.get(yearNumber);
    }

    private Boolean tryAddYear(Integer yearNumber){
        if (yearNumber < 2021 || years.containsKey(yearNumber))
            return false;
        years.put(yearNumber, new Year(yearNumber));
        return true;
    }

    public static DayInterface getToday() {
        var yearsDateBase = YearsDataBase.getInstance();
        var zoneId = TimeZone.getTimeZone(timeZone).toZoneId();
        var year = LocalDate.now(zoneId).getYear();
        var month = LocalDate.now(zoneId).getMonthValue();
        var day = LocalDate.now(zoneId).getDayOfMonth();
        return yearsDateBase.getYear(year).getMonth(month).getDay(day);
    }

    public static DayInterface getDay(LocalDate date) {
        var yearsDateBase = YearsDataBase.getInstance();
        return yearsDateBase.getYear(date.getYear())
                            .getMonth(date.getMonthValue())
                            .getDay(date.getDayOfMonth());
    }

    public static DayInterface getDay(int year, int month, int day) {
        var yearsDateBase = YearsDataBase.getInstance();
        return yearsDateBase.getYear(year)
                            .getMonth(month)
                            .getDay(day);
    }
}