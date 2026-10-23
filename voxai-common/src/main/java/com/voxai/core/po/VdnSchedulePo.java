package com.voxai.core.po;

import com.voxai.core.entity.VdnConfig;
import com.voxai.core.entity.VdnDtmf;
import com.voxai.core.entity.VdnSchedule;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class VdnSchedulePo extends VdnConfig implements Comparable<VdnSchedulePo> {

    /**
     * 每个字码日程有多个按键导航
     */
    private List<VdnDtmf> dtmfList;

    /**
     * 日程
     */
    private VdnSchedule scheduleConfig;


    public List<VdnDtmf> getDtmfList() {
        return dtmfList;
    }

    public void setDtmfList(List<VdnDtmf> dtmfList) {
        this.dtmfList = dtmfList;
    }

    public VdnSchedule getScheduleConfig() {
        return scheduleConfig;
    }

    public void setScheduleConfig(VdnSchedule scheduleConfig) {
        this.scheduleConfig = scheduleConfig;
    }

    @Override
    public int compareTo(VdnSchedulePo o) {
        return this.getScheduleConfig().getLevelValue() - o.getScheduleConfig().getLevelValue();
    }

    public boolean isEffectiveSchedule() {
        if (scheduleConfig == null) {
            return false;
        }
        LocalDate localDate = LocalDate.now();
        int week = localDate.getDayOfWeek().getValue();

        switch (week) {
            case 1:
                if (this.scheduleConfig.getMon() == 0) {
                    return false;
                }
                break;
            case 2:
                if (this.scheduleConfig.getTue() == 0) {
                    return false;
                }
                break;
            case 3:
                if (this.scheduleConfig.getWed() == 0) {
                    return false;
                }
                break;
            case 4:
                if (this.scheduleConfig.getThu() == 0) {
                    return false;
                }
                break;
            case 5:
                if (this.scheduleConfig.getFri() == 0) {
                    return false;
                }
                break;
            case 6:
                if (this.scheduleConfig.getSat() == 0) {
                    return false;
                }
                break;
            case 7:
                if (this.scheduleConfig.getSun() == 0) {
                    return false;
                }
                break;

            default:
                break;
        }


        //判断是否在生效日期内
        int dayOfMonth = localDate.getDayOfMonth();
        if (StringUtils.isNotBlank(scheduleConfig.getStartDay())) {
            if (scheduleConfig.getType() == 1) {
                //指定时间：start_day/end_day 为完整日期 yyyy-MM-dd
                LocalDate startDate = LocalDate.parse(scheduleConfig.getStartDay(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (startDate.isAfter(localDate)) {
                    return false;
                }
            } else {
                //相对时间：start_day/end_day 为天号(1~31)
                if (Integer.parseInt(scheduleConfig.getStartDay().trim()) > dayOfMonth) {
                    return false;
                }
            }
        }
        if (StringUtils.isNotBlank(scheduleConfig.getEndDay())) {
            if (scheduleConfig.getType() == 1) {
                LocalDate endDate = LocalDate.parse(scheduleConfig.getEndDay(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (endDate.isBefore(localDate)) {
                    return false;
                }
            } else {
                if (Integer.parseInt(scheduleConfig.getEndDay().trim()) < dayOfMonth) {
                    return false;
                }
            }
        }

        //判断是否在生效的时间范围内（当天）
        LocalTime nowTime = LocalTime.now();
        if (StringUtils.isNotBlank(scheduleConfig.getStartTime())) {
            LocalTime startTime = LocalTime.parse(scheduleConfig.getStartTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (startTime.isAfter(nowTime)) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(scheduleConfig.getEndTime())) {
            LocalTime endTime = LocalTime.parse(scheduleConfig.getEndTime(), DateTimeFormatter.ofPattern("HH:mm:ss"));
            if (endTime.isBefore(nowTime)) {
                return false;
            }
        }
        return true;
    }
}
