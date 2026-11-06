package com.voxai.cc.cdr;

import com.voxai.core.po.CallLogPo;

/** Opt-in callback contract. v1 continues to use CallLogPo without new metadata. */
public class CdrNotifyV2 extends CallLogPo {
    public int getCdrVersion() { return 2; }
    public String getTimestampUnit() { return "epochMillis"; }
    public String getDurationUnit() { return "milliseconds"; }
}
