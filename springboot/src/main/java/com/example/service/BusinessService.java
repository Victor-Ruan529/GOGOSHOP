package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.common.enums.StatusEnum;
import com.example.entity.Business;
import com.example.mapper.AccountMapper;
import com.example.mapper.BusinessMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商家业务处理
 */
@Service
public class BusinessService extends AbstractAccountService<Business> {

    @Resource
    private BusinessMapper businessMapper;

    @Override
    public RoleEnum role() {
        return RoleEnum.BUSINESS;
    }

    @Override
    protected AccountMapper<Business> mapper() {
        return businessMapper;
    }

    @Override
    protected Business newInstance() {
        return new Business();
    }

    /** 新商家（无论注册还是后台添加）默认待审核，只有管理员修改时才能改审核状态 */
    @Override
    protected void beforeInsert(Business business) {
        if (business.getStatus() == null) {
            business.setStatus(StatusEnum.CHECKING.status);
        }
    }

    /** 商家不能自己修改审核状态 */
    @Override
    protected void clearProtectedFields(Business business) {
        business.setStatus(null);
    }

    public boolean isApproved(Integer businessId) {
        Business business = businessMapper.selectById(businessId);
        return business != null && StatusEnum.CHECK_OK.status.equals(business.getStatus());
    }
}
