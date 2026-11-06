package com.voxai.cc.cdr;

import com.voxai.core.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/** MySQL month-table provisioning; DDL must never implicitly commit the CDR transaction. */
@Component
@EnableScheduling
public class CdrMonthTables {
    @Autowired private JdbcTemplate jdbc;
    private final Set<String> ready = new HashSet<>();

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public synchronized void ensure(String month) {
        validate(month);
        if (ready.contains(month)) { return; }
        for (String table : new String[]{"cc_call_log", "cc_call_device", "cc_call_detail"}) {
            jdbc.execute("create table if not exists " + table + month + " like " + table);
        }
        ready.add(month);
    }

    public static void validate(String month) {
        if (month == null || !month.matches("_[0-9]{4}(0[1-9]|1[0-2])")) {
            throw new IllegalArgumentException("Invalid CDR month suffix");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *")
    public void prepareUpcomingMonths() {
        // These calls run without a CDR transaction; on-demand ensure handles older calls too.
        ensure(DateTimeUtil.getNowMonth());
        ensure("_" + LocalDate.now().plusMonths(1).format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")));
    }
}
