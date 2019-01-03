# main

package com.biostime.coupon.biostimeweb.controller;

import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget\_Accounts\_department\_headquarter; import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Prop\_brands;

import java.util.ArrayList; import java.util.Arrays; import java.util.HashSet; import java.util.List; import java.util.Set;

import org.junit.Test;

import com.alibaba.fastjson.JSON; import com.biostime.coupon.biostimeweb.bean.common.Pager; import com.biostime.coupon.biostimeweb.bean.util.CommonPropertiesBeanUtil; import com.biostime.coupon.biostimeweb.common.util.BeanUtil; import com.biostime.coupon.biostimeweb.finance.bean.BudgetAccounts; import com.biostime.coupon.biostimeweb.product.service.ProductServiceUtil; import com.biostime.coupon.biostimeweb.query.bean.CampaignGroupQuery; import com.biostime.coupon.biostimeweb.query.bean.CouponDefinitionInOutputRatioQuery; import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeMarketActQuery; import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeQuery; import com.biostime.coupon.biostimeweb.user.bean.LoginUser; import com.biostime.coupon.biostimeweb.util.ParamUtil; import com.biostime.coupon.biostimeweb.util.RepoUtil;

public class TestBiostimeCase {

```text
@Test
public void test206() {
    //RepoUtil.getCountBudgetAccoutsCouponDefSql();

    Pager p = new Pager();
    BudgetAccounts query = new BudgetAccounts();


    System.out.println(JSON.toJSONString(query));
    query.setAuditMonth("201812");
    query.setDepartmentCode("01");
    LoginUser u = getTestLoginUser("01");

    System.out.println(RepoUtil.getBudgetAccountsQuerySql2(query, u, p));

}

@Test
public void test205() {
    List<String> codes = Arrays.asList("1","2","3","4");
    List<String> codesall = Arrays.asList("1","2","3","5","6");

    List<String>  not = BeanUtil.getNotListFromYesList(codes,codesall);
    System.out.println(not);
}


@Test
public void test77() {
    String brands = "1,2,3,4";
    brands = "1,2,3";
    brands = null;
    brands = "1,4";
    System.out.println(CommonPropertiesBeanUtil.getCnByPropEnumKey(brands, Prop_brands));


}


@Test
public void test7() {
    String brands = "1,4,5,6";

    System.out.println(ProductServiceUtil.mapData2ProductBrand(brands));

}

@Test
public void test204() {

    String key = CommonPropertiesBeanUtil.getKey(Budget_Accounts_department_headquarter);
    System.out.println(key);
}

@Test
public void test203() {
    String orgCode = "0105";
    orgCode = "010503";
    // List<String> terminalCodes = Arrays.asList("1305320", "1319082", "14746",
    // "121217");
    // IntStream.range(0, 997).forEach(i -> terminalCodes.add(i + ""));
    List<String> terminalCodes = new ArrayList();
    // terminalCodes.add("1");
    // IntStream.range(1, 1005).mapToObj(i ->
    // ParamUtil.convertInteger2Str(i)).forEach(terminalCodes::add);
    for (int i = 1; i < 1005; i++) {
        terminalCodes.add(ParamUtil.convertInteger2Str(i));
        System.out.println(terminalCodes.toString());
    }
    System.out.println(terminalCodes.size());
    System.out.println(terminalCodes.toString());
    RepoUtil.getManageTerminalsSql(terminalCodes, orgCode);

}

@Test
public void test202() {
    String orgCode = "0105";
    orgCode = "010503";
    List<String> terminalCodes = Arrays.asList("1305320", "1319082", "14746", "121217");
    // IntStream.range(0, 997).forEach(i -> terminalCodes.add(i + ""));

    // IntStream.range(1, 1005).mapToObj(i ->
    // ParamUtil.convertInteger2Str(i)).forEach(terminalCodes::add);
    /*
     * List<String> terminalCodes = new ArrayList();
     * 
     * for (int i = 1; i < 1005; i++) {
     * terminalCodes.add(ParamUtil.convertInteger2Str(i));
     * System.out.println(terminalCodes.toString()); }
     */
    // System.out.println(terminalCodes.size());
    RepoUtil.getManageTerminalsSqlV2(terminalCodes, orgCode);

}

@Test
public void test201() {

    System.out.println(RepoUtil.getTerminalInfoSql());

}

@Test
public void test200() {
    String orgCode = null;
    orgCode = "0105";
    orgCode = "010510";
    // 201810R test d16
    RepoUtil.getDepartmentOrganizationSql(orgCode);

}

// 201810R d9 test-2
@Test
public void test100() {
    CouponResultBiostimeMarketActQuery query = new CouponResultBiostimeMarketActQuery();

    LoginUser u = new LoginUser();
    u = getTestLoginUser("0106");
    u = getTestLoginUser("010604");

    // u = getTestLoginUser("011305");

    query.setCouponDefIds("10325300");
    // query.setByUsedTerminalOrgCode("1");
    RepoUtil.getCouponResultBiostimeMarketActQuerySql(query, u);

}

// 201810R d8 test-3
@Test
public void test111() {

    RepoUtil.getUserInfoNewMktSqlV2();
}

// 201810R d8 test-2
@Test
public void test1() {
    CouponResultBiostimeQuery query = new CouponResultBiostimeQuery();

    LoginUser u = new LoginUser();
    u = getTestLoginUser("0106");
    u = getTestLoginUser("010604");

    u = getTestLoginUser("01");

    // u = getTestLoginUser("011305");

    //query.setCouponDefIds("10325300");
    // query.setByUsedTerminalOrgCode("1");
    query.setStartDate("2018-12-21 00:00:00");
    query.setEndDate("2018-12-31 23:59:59");
    RepoUtil.getCouponResultBiostimeQuerySqlV2(query, u);

}

private LoginUser getTestLoginUser(String code) {
    LoginUser u = new LoginUser();
    Set<String> officecodes = new HashSet<String>();
    officecodes.add(code);
    u.setOfficecodes(officecodes);

    u.setCnName("我是大区");
    u.setOfficeCode(code);

    return u;
}

@Test
public void test24() {
    Pager p = new Pager();

    // LoginUser u = getTestLoginUser("0101");
    LoginUser u = getTestLoginUser("011302");

    CampaignGroupQuery query = new CampaignGroupQuery();
    // query.setCouponDefIds("10325216");

    RepoUtil.getCampaignGroupQuerySqlV2(query, u);

}

@Test
public void test5() {
    // decode ( length(calm.department_code) , 4 , ( select CD.SHORT_NAME
    // from common_department cd where CD.CODE = calm.department_code ) , 6
    // , ( select CD2.SHORT_NAME from common_department cd
    // ,common_department cd2 where cd.parent_id = cd2.id and CD.CODE =
    // calm.department_code ) ) as areaName

    String field = "calm.department_code";
    // field = "org_data.org_code";

    RepoUtil.getCommonDepartmentOfficeCodeSql(field);

}

@Test
public void test4_2() {

    String field = "calm.department_code";
    // field = "org_data.org_code";

    field = "cd.code";
    // RepoUtil.getCommonDepartmentAreaCodeSql(field);

    field = "officeCode";

    field = "E.DEPARTMENTCODE";
    field = "CBEU.DEPARTMENTCODE";

    field = "M.DEPARTMENTCODE";

    RepoUtil.getCommonDepartmentOfficeCodeSql(field, "office_code", true);

}

@Test
public void test_area_office() {

    String field = "calm.department_code";

    field = "M.DEPARTMENTCODE";

    field = "dq.department_code";

    field = "gcdepartment.DEPARTMENT_CODE";

    field = "cdt.department_code";//

    field = "r.area_code";

    field = "rf.loginuserorgcode";

    RepoUtil.getCommonDepartmentAreaCodeSql(field, "area_code", true);
    RepoUtil.getCommonDepartmentAreaNameSql(field, "area_name");
    RepoUtil.getCommonDepartmentAreaNameSql(field, "areaName");
    RepoUtil.getCommonDepartmentOfficeCodeSql(field, "office_code", true);
    RepoUtil.getCommonDepartmentOfficeNameSql(field, "office_name");
}

@Test
public void test4_4() {
    // decode ( length(calm.department_code) , 4 , ( select CD.SHORT_NAME
    // from common_department cd where CD.CODE = calm.department_code ) , 6
    // , ( select CD2.SHORT_NAME from common_department cd
    // ,common_department cd2 where cd.parent_id = cd2.id and CD.CODE =
    // calm.department_code ) ) as areaName

    String field = "calm.department_code";
    field = "org_data.org_code";

    field = "cd.code";

    field = "cd.code";
    field = "M.DEPARTMENTCODE";

    // RepoUtil.getCommonDepartmentAreaNameSql(field);

    RepoUtil.getCommonDepartmentOfficeNameSql(field, "office_name");

}

@Test
public void test4_3() {
    // decode ( length(calm.department_code) , 4 , ( select CD.SHORT_NAME
    // from common_department cd where CD.CODE = calm.department_code ) , 6
    // , ( select CD2.SHORT_NAME from common_department cd
    // ,common_department cd2 where cd.parent_id = cd2.id and CD.CODE =
    // calm.department_code ) ) as areaName

    String field = "calm.department_code";
    field = "org_data.org_code";

    field = "cd.code";

    field = "cd.code";
    field = "M.DEPARTMENTCODE";

    // RepoUtil.getCommonDepartmentAreaNameSql(field);

    RepoUtil.getCommonDepartmentAreaNameSql(field, "area_name");

}

@Test
public void test4() {

    String field = "calm.department_code";
    // field = "org_data.org_code";

    field = "cd.code";
    // RepoUtil.getCommonDepartmentAreaCodeSql(field);

    field = "officeCode";

    field = "E.DEPARTMENTCODE";
    field = "CBEU.DEPARTMENTCODE";

    field = "M.DEPARTMENTCODE";

    RepoUtil.getCommonDepartmentAreaCodeSql(field, "area_code", true);

}

@Test
public void test3() {
    // decode ( length(calm.department_code) , 4 , ( select CD.SHORT_NAME
    // from common_department cd where CD.CODE = calm.department_code ) , 6
    // , ( select CD2.SHORT_NAME from common_department cd
    // ,common_department cd2 where cd.parent_id = cd2.id and CD.CODE =
    // calm.department_code ) ) as areaName

    String field = "calm.department_code";
    field = "org_data.org_code";

    RepoUtil.getCommonDepartmentOfficeNameSql(field);

}

@Test
public void test1_1() {
    /*
     * select CD.SHORT_NAME from common_department cd where CD.CODE =
     * calm.department_code
     */

    String field = "calm.department_code";

    RepoUtil.getCommonDepartmentNameSql(field);

}

// 201810R d5 test
@Test
public void test9() {

    /*
     * CouponInOutputRatioReportReqBean couponInOutputRatioReportReqBean = new
     * CouponInOutputRatioReportReqBean();
     * couponInOutputRatioReportReqBean.setCouponStartTime("2018-08-01");
     * couponInOutputRatioReportReqBean.setCouponEndTime("2018-08-16");// 券结束日期
     * couponInOutputRatioReportReqBean.setAreaCode("0106");
     * couponInOutputRatioReportReqBean.setOfficeCode("010604");
     * couponInOutputRatioReportReqBean.setCampaignId("10318826");
     * couponInOutputRatioReportReqBean.setCouponDefId("10325019");
     * couponInOutputRatioReportReqBean.setCouponStatus("0");
     * couponInOutputRatioReportReqBean.setCreatedStartTime("2018-08-02");
     * couponInOutputRatioReportReqBean.setCreatedEndTime("2018-08-18");// 创建日期
     */

    Pager p = new Pager();
    CouponDefinitionInOutputRatioQuery query = new CouponDefinitionInOutputRatioQuery();
    query.setCouponDefId("10325019");
    query.setCampaignId(10318826l);
    // query.setCampaignGroupId(10318842l);
    query.setCouponStatus("0");

    query.setCreatedStartTime("2018-08-02");
    query.setCreatedEndTime("2018-08-18");// 创建日期\

    query.setCouponStartTime("2018-08-01");
    query.setCouponEndTime("2018-08-16");// 券结束日期

    query.setAreaCode("0106");
    query.setOfficeCode("010604");
    query.setCouponType("13");

    System.out.println(JSON.toJSONString(query));
    LoginUser u = new LoginUser();

    RepoUtil.getCouponDefinitionInOutputRatioQuerySql(query, u, p);

}
```

}

