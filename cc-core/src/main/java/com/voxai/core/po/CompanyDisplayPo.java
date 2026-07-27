package com.voxai.core.po;

import com.voxai.core.entity.CompanyDisplay;
import com.voxai.core.entity.CompanyPhone;

import java.util.List;

public class CompanyDisplayPo extends CompanyDisplay {

    /**
     * 号码
     */
    private List<CompanyPhone> companyPhoneList;

    public List<CompanyPhone> getCompanyPhoneList() {
        return companyPhoneList;
    }

    public void setCompanyPhoneList(List<CompanyPhone> companyPhoneList) {
        this.companyPhoneList = companyPhoneList;
    }
}
