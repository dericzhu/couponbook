# 测试用例

package com.biostime.coupon.biostimeweb.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.file.Files; import java.nio.file.Paths; import java.util.ArrayList; import java.util.HashSet; import java.util.List; import java.util.Set;

import org.junit.Before; import org.junit.Test; import org.junit.runner.RunWith; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.MediaType; import org.springframework.mock.web.MockMultipartFile; import org.springframework.test.context.ContextConfiguration; import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests; import org.springframework.test.context.junit4.SpringJUnit4ClassRunner; import org.springframework.test.context.web.WebAppConfiguration; import org.springframework.test.web.servlet.MockMvc; import org.springframework.test.web.servlet.ResultActions; import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON; import com.biostime.coupon.biostimeweb.bean.common.Pager; import com.biostime.coupon.biostimeweb.bean.merchandiser.SKU; import com.biostime.coupon.biostimeweb.bean.search.CouponDefinitionSearch; import com.biostime.coupon.biostimeweb.cache.CacheService; import com.biostime.coupon.biostimeweb.common.task.IssuseSwisseCouponByCouponDefinitionTplTask; import com.biostime.coupon.biostimeweb.common.task.UpdateCouponStatusTask; import com.biostime.coupon.biostimeweb.common.util.DateUtil; import com.biostime.coupon.biostimeweb.coupon.service.CouponQueryService; import com.biostime.coupon.biostimeweb.department.bean.DepartmentOrganization; import com.biostime.coupon.biostimeweb.department.service.DepartmentServiceUtil; import com.biostime.coupon.biostimeweb.product.service.ProductServiceUtil; import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeQuery; import com.biostime.coupon.biostimeweb.task.TaskServiceUtil; import com.biostime.coupon.biostimeweb.user.bean.LoginUser; import com.biostime.coupon.biostimeweb.user.service.UserServiceUtil; import com.biostime.coupon.biostimeweb.util.ParamUtil; import com.mama100.merchandise.vo.exception.MerchandiseException;

@RunWith\(SpringJUnit4ClassRunner.class\) /\*

* @ContextConfiguration\(locations =
* {"classpath:/spring-conf/coupon-spring.xml",
* "classpath:/spring-conf/coupon-dao-conf.xml",
* "classpath:/spring-conf/rpc-client.xml", "classpath:/servlet-context.xml"},
* inheritLocations = true\) \*/ @ContextConfiguration\(locations = { "classpath:/spring-conf/coupon-spring.xml", "classpath:/servlet-context.xml" }, inheritLocations = true\) @WebAppConfiguration public class TestBiostimeController extends AbstractJUnit4SpringContextTests {

  // private HttpMessageConverter mappingJackson2HttpMessageConverter; // private MediaType contentType = new // MediaType\(MediaType.APPLICATION\_JSON.getType\(\), // MediaType.APPLICATION\_JSON.getSubtype\(\), Charset.forName\("utf8"\)\); private MockMvc mockMvc; /\*

  * String a0 = ""; String loginId = "2929";// 总部 String a1 =
  * "/coupon/systemMgrController/getPMName.action"; String a2 =
  * "/coupon/couponMgrController/getCouponDefList.action"; String a3 =
  * "/coupon/couponMgrController/importTerminalTxt.action"; String a4 =
  * "/coupon/couponTerminalMgrController/importCouponTerminal.action";
  * * String a5 = ""; String a6 = "/b/coupon/config/create.action"; String a7 =
  * "/coupon/couponMgrController/getCouponDefDetails.action"; String a8 = "";
  * String a9 = ""; String a10 = ""; String a11 =
  * "/b/coupon/q/CouponResult.action"; String a12 = ""; String a13 = ""; String
  * a14 = "/b/coupon/q/scene1/CouponResult.action"; String a15 = ""; String a16 =
  * ""; String a17 = "";
  * * String a21 = "/b/coupon/config/q/CouponDefinition.action";
  * * String a28 = "/b/coupon/config/q/inoutratio/CouponDefinition.action";
  * * String a35 =
  * "/b/coupon/config/department/office/q/CampaignDepartmentQuota.action"; String
  * a38 =
  * "/coupon/couponTerminalMgrController/getCouponTerminalQuotaList.action";
  * String a40 = "/b/coupon/q/CampaignGroup.action"; String a46 =
  * "/coupon/couponBudgetMgrController/getCouponBudgetLimit.action"; String a48 =
  * "/b/coupon/v55/budgetAccountQuery.action";
  * * String a49 = "/b/coupon/q/marketAct/CouponResult.action"; \*/

    String a0 = ""; String loginId = "2929";// 总部 String a1 = "/coupon/systemMgrController/getPMName.action"; String a1\_1 = a1 + "?" + "pmId=" + loginId + "&longiId=" + loginId; String a2 = "/coupon/couponMgrController/getCouponDefList.action"; String a3 = "/coupon/couponMgrController/importTerminalTxt.action"; String a4 = "/coupon/couponTerminalMgrController/importCouponTerminal.action"; // String a4R = "/b/coupon/config/department/importCouponTerminal.action";

    String a5 = "/coupon/couponMgrController/calculateProductionRatio.action"; String a6 = "/b/coupon/config/create.action"; String a6\_201809R = "/b/coupon/config/v2/create.action"; String a7 = "/coupon/couponMgrController/getCouponDefDetails.action"; String a8 = "/b/coupon/q/scene1/CampaignGroup.action"; String a9 = "/b/coupon/config/tpl/q/detail/CouponDefinitionTpl.action"; String a10 = "/m/coupon/config/q/CouponDefinition.action"; String a11 = "/b/coupon/q/CouponResult.action"; String a12 = "/coupon/couponMgrController/v2/calculateProductionRatio.action"; String a13 = "/b/coupon/finance/calculateProductionRatio.action"; String a14 = "/b/coupon/q/scene1/CouponResult.action"; String a15 = "/b/coupon/config/q/detail/CampaignGroup.action"; String a16 = "/b/coupon/export/scene1/CouponResult.action"; String tbda17 = "/coupon/couponMgrController/importCustomerTxt.action";

    String a18 = "/b/coupon/config/tpl/q/CouponDefinitionTpl.action"; String a19 = "/coupon/merchandiserMgrController/getCouponCategories.action"; String a20 = "/b/coupon/q/enum.action"; String a21 = "/b/coupon/config/q/CouponDefinition.action"; String a22 = "/b/coupon/config/tpl/create.action"; String a23 = "/b/coupon/config/batch/publish.action"; String a24 = "/coupon/couponMgrController/listNationCoupon.action"; String a25 = "/m/coupon/v55/manjian/create.action";

    String a26 = "/m/coupon/v55/xianjin/create.action"; String a27 = "/m/coupon/v55/queryCouponDefDetail.action"; String a28 = "/b/coupon/config/q/inoutratio/CouponDefinition.action"; String a29 = "/coupon/couponDepartmentMgrController/getCampaignDepartmentByCampaignId.action"; String a29R = "/b/coupon/config/department/q/CampaignDepartment.action";

    String a30 = "/b/coupon/v55/exportCouponTerminalQuota.action"; // String a30R = // "/b/coupon/config/department/export/CouponTerminalQuota.action";

    String a31 = "/coupon/couponDepartmentMgrController/getCouponDepartmentQuotaList.action"; String a31R = "/b/coupon/config/department/q/CampaignDepartmentQuota.action";

    String a32 = "/coupon/couponDepartmentMgrController/importCouponDepartment.action"; String a32R = "/b/coupon/config/department/import/CampaignDepartmentQuota.action"; String a33 = "/b/coupon/config/department/export/CampaignDepartmentQuota.action"; // String a34 = "/b/coupon/v55/exportCouponDepartmentQuota.action"; String a35 = "/b/coupon/config/department/office/q/CampaignDepartmentQuota.action"; String a36 = "/b/coupon/config/department/office/export/CampaignDepartmentQuota.action"; String a37 = "/b/coupon/config/department/office/import/CampaignDepartmentQuota.action"; String a38 = "/coupon/couponTerminalMgrController/getCouponTerminalQuotaList.action"; String a39 = "/b/coupon/config/export/inoutratio/CouponDefinition.action";

    String a40 = "/b/coupon/q/CampaignGroup.action"; String a41 = "/b/coupon/config/update.action"; String a42 = "/admin/testcase/UserTerminalScene/isUse.action"; String a43 = "/m/coupon/v55/product/import.action"; String a44 = "/m/coupon/config/update.action";

    String a45 = "/coupon/systemMgrController/getOfficeList.action"; String a46 = "/coupon/couponBudgetMgrController/getCouponBudgetLimit.action";

    String a47 = "/coupon/couponBudgetMgrController/getCouponBudgetLimitDetail.action"; String a48 = "/b/coupon/v55/budgetAccountQuery.action";

    String a48\_201812R = "/b/coupon/finance/budget/q/BudgetAccounts.action";

    String a49 = "/b/coupon/q/marketAct/CouponResult.action";

    String a50 = "/coupon/couponIntegralMgrController/getCouponDefinitionById.action";

    String a51 = "/b/coupon/v55/exportBudgetList.action"; String a52 = "/b/coupon/v55/addBudgetAccount.action";

    String a52\_201812R = "/b/coupon/finance/budget/create/BudgetAccounts.action";

    String a53 = "/coupon/couponBudgetMgrController/importCouponBudget.action";

    String a53\_201812R = "/b/coupon/finance/budget/import/BudgetAccountQuota.action"; String a54 = "/coupon/couponBudgetMgrController/exportCouponBudgetTemplate.action"; String a55 = "/m/coupon/v55/issueCoupon.action";

    String a56 = "/coupon/couponMgrController/getMktActivitiesBudget.action"; String a56\_201812R = "/b/coupon/finance/budget/q/scene1/BudgetAccounts.action"; String a57 = "/b/coupon/finance/getBalanceOfCurrentMonth.action";

    String a58 = "/m/coupon/v55/terminal/import.action"; String a59 = "/m/coupon/v55/customer/import.action";

    String a60 = "/m/coupon/v55/product/import.action"; String a61 = "/b/coupon/v55/budgetAccountUpdate.action";

    String a61\_201812R = "/b/coupon/finance/budget/disable/BudgetAccounts.action";

    String a62 = "/b/coupon/finance/budget/getBalanceOfCurrentMonth.action"; String a62\_v2 = "/b/coupon/finance/budget/getBalanceOfCurrentMonth.action"; String a63 = "/b/coupon/finance/budget/q/report/BudgetAccounts.action"; String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action";

    // String a65 = "/b/coupon/config/update.action"; // String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action"; // String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action"; // String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action"; // String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action";

    @Autowired private WebApplicationContext webApplicationContext;

```text
@Test
public void test_import_file_xls_importCouponDepartment_R_isarea() throws Exception {
    loginId = "0289";//

    loginId = "0278";//
    String fileName = "";
    fileName = "F:/biostime2/file/优惠券办事处配额-0910.xls";

    fileName = "F:/biostime2/file/response-CampaignDepartmentQuota-0911.xls";

    fileName = "F:/biostime2/file/response-CampaignDepartmentQuota-ok.xls";

    fileName = "F:/biostime2/file/response-CampaignDepartmentQuotaOffice-testdata-ok-0913.xls";

    // debug -0921
    fileName = "F:/biostime2/file/response-CampaignDepartmentQuotaOffice-testdata-0921.xls";

    // debug -0926
    fileName = "F:/biostime2/file/优惠券办事处配额-debug-0926.xls";

    // debug -20190102
    fileName = "F:/biostime2/file/couponOfficeQuota-debug-20190102.xls";


    fileName =  "F:/biostime2/file/couponOfficeQuota-debug-v2.xls";
    String json = "";

    json = "";

    String couponDefId = "169706";

    couponDefId = "10325183";

    // 201809R test 更新办事处配额的数据
    couponDefId = "10325216";

    //

    // debug 0921 201809R 场景:大区创券 全国券=是 使用门店配额=是
    loginId = "0289";//

    couponDefId = "10325275";

    // debug 20190102
    loginId = "0289";//
    loginId = "0022";
    couponDefId = "10325657";

    MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc
            .perform(fileUpload(a37).file(file).param("loginId", loginId).param("couponDefId", couponDefId)

                    .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_getCouponResultBiostimeQueryV2() {

    Pager p = new Pager();

    CouponResultBiostimeQuery query = new CouponResultBiostimeQuery();

    // LoginUser u = new LoginUser();

    // u = getTestLoginUser("01");

    query.setStartDate("2018-12-21 00:00:00");
    query.setEndDate("2018-12-31 23:59:59");

    LoginUser u = getTestLoginUser("01");

    CouponQueryService q = BiostimeWebApplicationContextHolder.getApplicationContext()
            .getBean(CouponQueryService.class);

    q.getCouponResultBiostimeQuery(p, query, u);

}

@Test
public void test_export_exportBudgetList() throws Exception {

    // &departmentCode=1&budgetStatus=&channelCode=&budgetName=
    // loginId = "0289";
    String fileName = "F:/biostime2/file/response-exportBudgetList.xls";

    ResultActions resultActions = mockMvc.perform(get(a51).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("departmentCode", "1").param("pageSize", "32")

    );
    Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_cfg_update() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201812R test_cfg_update
    json = "{\"couponTitle\":\"购买满1000元立减20元\",\"couponName\":\" \",\"couponRemark\":\"d-test-1218\",\"couponDefId\":\"10325589\",\"couponType\":5,\"campaignId\":20012160,\"campaignGroupId\":10318893,\"couponStartDate\":\"2018-12-18\",\"couponEndDate\":\"2018-12-31\",\"amountPerCustomer\":1,\"maxCost\":10.78,\"maxAmount\":120000,\"couponBudgetInfos\":[{\"budgetId\":121,\"ration\":1}],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\"}";
    ResultActions resultActions = mockMvc
            .perform(post(a41).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_query_detail_campaigngroup() throws Exception {
    String groupId = "10318891";

    String json = "";

    json = "";

    ResultActions resultActions = mockMvc.perform(
            get(a15).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("groupId", groupId)

    );
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_b_customer_import() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";

    // 201812R test todo1
    fileName = "F:/biostime2/file/指定顾客手机号码模板-debug-20181218.txt";

    fileName = "F:/biostime2/file/指定顾客手机号码模板-debug.txt";
    String json = "";

    json = "";

    String mobile_type = "1";

    MockMultipartFile file = new MockMultipartFile("receiveTxt", "orgin-receiveTxt.txt", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc
            .perform(fileUpload(tbda17).file(file).param("loginId", loginId).param("customerFileType", mobile_type)

                    .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_export_BudgetAccounts() throws Exception {
    // loginId = "0289";
    String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";

    ResultActions resultActions = mockMvc
            .perform(get(a64).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")// .param("pageSize",
                                                                                                                    // "32")

            );
    Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_BudgetAccounts() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201812R test_q_BudgetAccounts
    json = "{\"auditMonth\":\"201901\"}";

    ResultActions resultActions = mockMvc.perform(
            post(a63).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_old_page_getCouponBudgetLimitDetail() throws Exception {

    loginId = "2929";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"pageNo\":1,\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishStartDate\":\"\",\"publishEndDate\":\"\"}";

    // 201812R test_q_old_page_getCouponBudgetLimitDetail
    json = "{\"departmentCode\":\"01\",\"yearmonth\":\"201812\",\"pageNo\":1,\"pageSize\":10}";

    ResultActions resultActions = mockMvc.perform(
            post(a47).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_budget_getBalanceOfCurrentMonth() throws Exception {

    String json = "";

    // json = " {\"budgetAccountId\":121}";
    String budgetAccountId = "121";

    budgetAccountId = "41";

    ResultActions resultActions = mockMvc.perform(get(a62).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("budgetAccountId", budgetAccountId)

    );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_budget_getBalanceOfCurrentMonth_v2() throws Exception {

    String json = "";

    json = " {\"budgetAccountId\":41}";
    String budgetAccountId = "121";

    budgetAccountId = "41";

    ResultActions resultActions = mockMvc
            .perform(post(a62_v2).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).content(json)
            // .param("budgetAccountId", budgetAccountId)

            );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_disable_BudgetAccounts_201812R() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201812R test_disable_BudgetAccounts
    json = "{\"id\":121}";

    ResultActions resultActions = mockMvc
            .perform(post(a61_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
                    // .param("id", "125")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_disable_BudgetAccounts() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201812R test_disable_BudgetAccounts
    json = "{}";

    ResultActions resultActions = mockMvc
            .perform(post(a61).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("id", "125")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_m_config_create() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201808R m create test
    json = "{\"anum\":0,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"groupName\":\"立减金额\",\"costPoint\":0,\"discount\":\"2\",\"giftAmount\":0,\"presentTypeA\":3,\"presentTypeB\":3,\"giftProducts\":[],\"ration\":0}],\"templateName\":\"满额立减券\",\"couponTitle\":\"满1000立减2元\",\"couponName\":\"d-test-0823\",\"couponRemark\":\"d-test-0823\",\"productScope\":\"0\",\"couponType\":\"5\",\"aprice\":\"1000\",\"bprice\":0,\"couponStartDate\":\"2018-08-23\",\"couponEndDate\":\"2018-08-24\",\"createdBy\":\"\",\"productionRatio\":\"\",\"discountStrength\":\"\",\"amountPerCustomer\":-1,\"maxAmount\":-1,\"settlRatio\":0,\"publisher\":1,\"pmId\":\"2929\",\"isSameProduct\":true,\"isSameSeries\":true,\"terminalCode\":\"\",\"pageCampaignScope\":\"6\",\"pageProductScope\":9,\"terminalCodes\":[\"90018\"],\"validCustomers\":[],\"couponCondition\":\"\",\"publish\":{\"customerType\":-1,\"publishWay\":\"2\",\"productItems\":\"\",\"settlement\":1},\"prop\":{\"brands\":\"1,2,3,4\"}}";

    // 201812R test_m_config_create
    json = " {\"tplId\":176, \"anum\":0,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"groupName\":\"立减金额\",\"costPoint\":0,\"discount\":\"1\",\"giftAmount\":0,\"presentTypeA\":3,\"presentTypeB\":3,\"giftProducts\":[],\"ration\":0}],\"templateName\":\"满额立减券\",\"couponTitle\":\"满1000立减1元\",\"couponName\":\"d-m-test-20181211\",\"couponRemark\":\"d-m-test-20181211\",\"productScope\":\"0\",\"couponType\":\"5\",\"aprice\":\"1000\",\"bprice\":0,\"couponStartDate\":\"2018-12-11\",\"couponEndDate\":\"2018-12-31\",\"createdBy\":\"\",\"productionRatio\":\"\",\"discountStrength\":\"\",\"amountPerCustomer\":-1,\"maxAmount\":-1,\"settlRatio\":0,\"publisher\":1,\"pmId\":\"2929\",\"isSameProduct\":true,\"isSameSeries\":true,\"terminalCode\":\"\",\"pageCampaignScope\":\"6\",\"pageProductScope\":9,\"terminalCodes\":[\"912362\"],\"validCustomers\":[],\"couponCondition\":\"\",\"publish\":{\"customerType\":-1,\"publishWay\":\"2\",\"productItems\":\"\",\"settlement\":1},\"prop\":{\"brands\":\"1,4\"}}";

    ResultActions resultActions = mockMvc
            .perform(post(a25).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_m_issueCoupon() throws Exception {
    String couponDefId = "10325459";

    String customerIds = "32737295";

    ResultActions resultActions = mockMvc.perform(get(a55).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("couponDefId=", couponDefId).param("customerIds", customerIds)

    );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_task_issuseSwisseCouponByCouponDefinitionTpl() throws Exception {

    IssuseSwisseCouponByCouponDefinitionTplTask t = TaskServiceUtil
            .getServiceIssuseSwisseCouponByCouponDefinitionTplTask();
    t.process();
}

@Test
public void test_q_page_tpl() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"status\":\"\",\"id\":\"176\"}";

    ResultActions resultActions = mockMvc.perform(post(a18).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("pageNo", "1").param("pageSize", "20")

            .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_tpl_create() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201808R test
    json = "{\"tplConfig\":{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"日常活动\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":false,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-0817-2\",\"couponRemark\":\"d-test-0817-2\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"productionRatio\":\"-0.59%\",\"settleRatio\":\"99\",\"isSameProduct\":false,\"isSameSeries\":false,\"marketAct\":false,\"maxCost\":0,\"couponUsage\":0.8,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"themeId\":\"undefined\",\"prop\":{\"newCustomerStorehouse\":\"0\"}},\"groupType\":\"1\",\"couponType\":5,\"status\":1,\"customerType\":\"1\",\"productCategory\":\"7\"}";

    // 201812R test_tpl_create
    json = "{\"tplConfig\":{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"重点活动\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"12\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满1000元立减12元\",\"couponName\":\"d-test-tpl-im-1211\",\"couponRemark\":\"d-test-tpl-im-1211\",\"publishType\":2,\"couponType\":5,\"aprice\":1000,\"bprice\":0,\"productionRatio\":\"1.20%\",\"settleRatio\":\"100\",\"isSameProduct\":false,\"isSameSeries\":false,\"marketAct\":false,\"maxCost\":\"9.24\",\"couponUsage\":0.8,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"themeId\":\"undefined\",\"prop\":{\"newCustomerStorehouse\":\"0\"}},\"groupType\":\"2\",\"couponType\":5,\"status\":1,\"customerType\":\"2\",\"productCategory\":\"2\"}";

    ResultActions resultActions = mockMvc
            .perform(post(a22).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_config_q_enum() throws Exception {
    String cts = DateUtil.makeDateSeqNo();
    String key = "Coupon_Cfg_Tpl_productCategory_";
```

// key = "Coupon_Cfg\_Tpl\_productCategory_"; // // String json = ""; // // key = "Prop_brands_"; // // key = "Prop_coupon\_purpose_"; // // key = "Prop_abnormal\_user_"; // // key = "Coupon_Cfg\_Tpl\_customerType_";

```text
    ResultActions resultActions = mockMvc.perform(get(a20).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("key", key).param("cts", cts)

    );
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_ProductServiceUtil() throws MerchandiseException {

    // 接口querySKUList
    // 枚举 com.mama100.merchandise.enums.BrandId

    /*
     * BIOSTIME(1, "合生元"), SWISSE(26509, "SWISSE"), HEALTHY_TIMES(21706,
     * "Healthy Times"), DODIE(33206, "DODIE"),
     */
    String brand_swisse = ParamUtil.convertInteger2Str(com.mama100.merchandise.enums.BrandId.SWISSE.getId());// "26509";
    List<Long> skus = new ArrayList<Long>();
    skus.add(4l);

    // List<SKU> products = ProductServiceUtil.getProductByBrandId(brand_swisse,
    // skus);
    List<SKU> products = ProductServiceUtil.getProductByBrandId(brand_swisse, null);
    System.out.println(JSON.toJSONString(products));

    //
    String brand_biostime = ParamUtil.convertInteger2Str(com.mama100.merchandise.enums.BrandId.BIOSTIME.getId());// "1";
    List<SKU> products_b = ProductServiceUtil.getProductByBrandId(brand_biostime, null);
    System.out.println(JSON.toJSONString(products_b));

    String brand_ht = ParamUtil.convertInteger2Str(com.mama100.merchandise.enums.BrandId.HEALTHY_TIMES.getId());// "1";
    List<SKU> products_ht = ProductServiceUtil.getProductByBrandId(brand_ht, null);
    System.out.println(JSON.toJSONString(products_ht));

}

@Test
public void test_m_product_import() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";

    // 201812R test todo1
    fileName = "F:/biostime2/file/skudemo-20181210.txt";

    fileName = "F:/biostime2/file/skudemo-20181210-logicerror1.txt";
    String json = "";

    json = "";

    String brands = "1,4";
    // brands = "1";

    MockMultipartFile file = new MockMultipartFile("receiveTxt", "orgin-receiveTxt.txt", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc
            .perform(fileUpload(a60).file(file).param("loginId", loginId).param("brands", brands)

                    .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_m_customer_import() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";

    // 201812R test todo1
    fileName = "F:/biostime2/file/customerdemo-20181210.txt";

    // fileName = "F:/biostime2/file/customerdemo-20181210-logicerror1.txt";
    String json = "";

    json = "";

    // String couponDefId = "169706";

    MockMultipartFile file = new MockMultipartFile("receiveTxt", "orgin-receiveTxt.txt", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc.perform(fileUpload(a59).file(file).param("loginId", loginId)
            // .param("couponDefId", couponDefId)

            .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_m_terminal_import() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";

    // 201812R test todo1
    fileName = "F:/biostime2/file/virtualterminaldemo-20181210.txt";

    // fileName = "F:/biostime2/file/virtualterminaldemo-20181210-logicerror1.txt";
    String json = "";

    json = "";

    // String couponDefId = "169706";

    MockMultipartFile file = new MockMultipartFile("receiveTxt", "orgin-receiveTxt.txt", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc.perform(fileUpload(a58).file(file).param("loginId", loginId)
            // .param("couponDefId", couponDefId)

            .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_task_remindGoingToExpireCoupon() {

    // TaskServiceUtil.remindGoingToExpireCoupon();
    int range = 500;
    // 多少小时内过期的
    int afterHour = 48;
    UpdateCouponStatusTask t = TaskServiceUtil.getServiceUpdateCouponStatusTask();
    t.remindGoingToExpireCoupon(afterHour, range);
}

@Test
public void test_getBalanceOfCurrentMonth() throws Exception {

    String json = "";

    json = "";

    ResultActions resultActions = mockMvc
            .perform(get(a57).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

            );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_getMktActivitiesBudget_201812R() throws Exception {

    String json = "";

    json = "";

    ResultActions resultActions = mockMvc
            .perform(get(a56_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

            );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_getMktActivitiesBudget() throws Exception {

    String json = "";

    json = "";

    ResultActions resultActions = mockMvc
            .perform(get(a56).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

            );

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_importCouponBudget_201812R() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";
    fileName = "F:/biostime2/file/优惠券费用报表导入模板.xls";

    fileName = "F:/biostime2/file/优惠券费用报表导入模板-20181203.xls";

    String json = "";

    json = "";

    // String couponDefId = "169706";

    MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc.perform(fileUpload(a53_201812R).file(file).param("loginId", loginId)
            // .param("couponDefId", couponDefId)

            .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_importCouponBudget() throws Exception {
    loginId = "2929";//
    // loginId = "0278";//

    String fileName = "";
    fileName = "F:/biostime2/file/优惠券费用报表导入模板.xls";

    fileName = "F:/biostime2/file/优惠券费用报表导入模板-20181203.xls";

    String json = "";

    json = "";

    // String couponDefId = "169706";

    MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc.perform(fileUpload(a53).file(file).param("loginId", loginId)
            // .param("couponDefId", couponDefId)

            .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_create_BudgetAccounts_201812R() throws Exception {

    String json = "";

    // 201812R test
    json = "{\"budgetName\":\"d-l-test-20181203\",\"channelCode\":\"01,02,03,08,13\",\"departmentCode\":\"1\"}";

    ResultActions resultActions = mockMvc
            .perform(post(a52_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
                    /*
                     * .param("budgetName", "d-l-test-20181203") .param("channelCode",
                     * "01,02,03,08,13") .param("departmentCode", "1")
                     */

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_create_BudgetAccounts() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201812R test
    json = "{}";

    ResultActions resultActions = mockMvc.perform(post(a52).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("budgetName", "d-l-test-20181203")
            .param("channelCode", "01,02,03,08,13").param("departmentCode", "1")

            .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_old_page_budgetAccountQuery_201812R() throws Exception {

    loginId = "2929";

    String json = "";

    json = "{\"departmentCode\":\"1\",\"loginId\":\"2929\",\"pageNo\":1,\"pageSize\":20,\"budgetStatus\":1}";

    ResultActions resultActions = mockMvc
            .perform(post(a48_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
                    /*
                     * .param("pageNo", "1") .param("pageSize", "20") .param("departmentCode", "1")
                     */

                    .content(json));

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_query_detail() throws Exception {
    String couponDefId = "10324888";

    couponDefId = "10324921";

    // 201808R test
    couponDefId = "10325052";
    couponDefId = "10325051";// no bindsystem

    couponDefId = "10325055";
    // 201812R test_query_details
    couponDefId = "10325592";

    String json = "";

    json = "";

    ResultActions resultActions = mockMvc.perform(get(a7).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("couponDefId", couponDefId)

    );
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_page_getCouponTerminalQuotaList() throws Exception {

    loginId = "10317";// isoffice
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";
    // 201809R test 办事处账号 查询 门店配额
    json = "{\"campaignId\":\"20011787\",\"pageNo\":1,\"pageSize\":10}";

    // debug-0927
    loginId = "12580";// isoffice
    json = "{\"campaignId\":\"20011836\",\"pageNo\":1,\"pageSize\":10}";

    // debug-1115
    loginId = "1149";// isarea
    json = "{\"campaignId\":20012066,\"pageNo\":1,\"pageSize\":10}";

    ResultActions resultActions = mockMvc.perform(
            post(a38).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Before
public void setup() throws Exception {
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
}

@Test
public void test_q_page_getCouponDepartmentQuotaList_office_R_isarea() throws Exception {

    loginId = "10317";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"couponDefIds\":\"10325183\"}";

    // 201809R test 办事处账号 【修改办事处配额】 查询
    json = "{\"couponDefIds\":\"10325216\"}";

    // debug-090102
    loginId = "0289";
    loginId = "0022";
    json = " {\"couponDefIds\":10325657,\"pageNo\":1,\"pageSize\":10}";

    ResultActions resultActions = mockMvc.perform(
            post(a35).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_page_getCouponDepartmentQuotaList_office_R_isoffice() throws Exception {

    loginId = "10317";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"couponDefIds\":\"10325183\"}";

    // 201809R test 办事处账号 【修改办事处配额】 查询
    json = "{\"couponDefIds\":\"10325216\"}";

    // debug 0927
    loginId = "12580";
    // debug-1114
    loginId = "2929";
    json = "{\"couponDefIds\":10325492,\"orgCodeOrName\":\"010105\",\"pageNo\":1,\"pageSize\":10}";

    ResultActions resultActions = mockMvc.perform(
            post(a35).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_create_13() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201807R test create 13
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test0717\",\"campaignPM\":\"2929\",\"description\":\"d-test0717\",\"productScope\":\"\",\"anum\":3,\"aproducts\":[{\"isMandatory\":0,\"sku\":104}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftProducts\":[{\"amount\":\"1\",\"giftName\":\"我是礼品dtest\",\"isMandatory\":1,\"sku\":\"\",\"giftPoint\":\"10\",\"giftPrice\":1,\"giftPriceRatio\":0.5,\"isDeliveryAddress\":\"0\"}],\"giftAmount\":\"1\",\"groupName\":\"礼品领取\",\"presentType\":2}],\"productPoint\":false,\"giftPoint\":false,\"templateName\":\"收分赠品券\",\"couponTitle\":\"买商品送我是礼品dtest（价值10积分）\",\"couponName\":\"微信玩积分，好礼送不停\",\"couponRemark\":\"d-test0717\",\"publishType\":\"3\",\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-07-17\",\"couponEndDate\":\"2018-07-31\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":\"2\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":320,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318815\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"0.0\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":1,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"custPay\":100,\"abnormalUser\":\"\"},\"tplId\":\"\"}";

    // debug
    // json =
    // "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0726\",\"campaignPM\":\"2929\",\"description\":\"d-test-0726\",\"totalExpense\":0,\"productScope\":\"\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":185},{\"isMandatory\":0,\"sku\":236353},{\"isMandatory\":0,\"sku\":235952},{\"isMandatory\":0,\"sku\":235754},{\"isMandatory\":0,\"sku\":235753},{\"isMandatory\":0,\"sku\":234176},{\"isMandatory\":0,\"sku\":234177},{\"isMandatory\":0,\"sku\":187},{\"isMandatory\":0,\"sku\":354},{\"isMandatory\":0,\"sku\":186},{\"isMandatory\":0,\"sku\":279}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"买赠券\",\"couponTitle\":\"13元购合生元400g礼包\",\"couponName\":\"\",\"couponRemark\":\"dddddd\",\"publishType\":3,\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-07-26\",\"couponEndDate\":\"2018-08-26\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":4,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":201,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318818\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"13\"},\"tplId\":\"\"}";

    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0726\",\"campaignPM\":\"2929\",\"description\":\"d-test-0726\",\"totalExpense\":0,\"productScope\":\"\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":185},{\"isMandatory\":0,\"sku\":236353},{\"isMandatory\":0,\"sku\":235952},{\"isMandatory\":0,\"sku\":235754},{\"isMandatory\":0,\"sku\":235753},{\"isMandatory\":0,\"sku\":234176},{\"isMandatory\":0,\"sku\":234177},{\"isMandatory\":0,\"sku\":187},{\"isMandatory\":0,\"sku\":354},{\"isMandatory\":0,\"sku\":186},{\"isMandatory\":0,\"sku\":279}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"买赠券\",\"couponTitle\":\"13元购合生元400g礼包\",\"couponName\":\"\",\"couponRemark\":\"dddddd\",\"publishType\":3,\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-07-26\",\"couponEndDate\":\"2018-08-26\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":4,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":201,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318818\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"13\"},\"tplId\":\"\"}";

    // debug 20180806
    // json =
    // "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"12580\",\"campaignPM\":\"12580\",\"description\":\"12580\",\"productScope\":\"\",\"anum\":999,\"aproducts\":[{\"isMandatory\":0,\"sku\":141},{\"isMandatory\":0,\"sku\":140},{\"isMandatory\":0,\"sku\":142},{\"isMandatory\":0,\"sku\":233052},{\"isMandatory\":0,\"sku\":443},{\"isMandatory\":0,\"sku\":444},{\"isMandatory\":0,\"sku\":445},{\"isMandatory\":0,\"sku\":131},{\"isMandatory\":0,\"sku\":133},{\"isMandatory\":0,\"sku\":280},{\"isMandatory\":0,\"sku\":283},{\"isMandatory\":0,\"sku\":299}],\"bnum\":999,\"bproducts\":[{\"isMandatory\":0,\"sku\":263},{\"isMandatory\":0,\"sku\":189},{\"isMandatory\":0,\"sku\":190},{\"isMandatory\":0,\"sku\":264},{\"isMandatory\":0,\"sku\":2000},{\"isMandatory\":0,\"sku\":346},{\"isMandatory\":0,\"sku\":344},{\"isMandatory\":0,\"sku\":343},{\"isMandatory\":0,\"sku\":347},{\"isMandatory\":0,\"sku\":345},{\"isMandatory\":0,\"sku\":236555},{\"isMandatory\":0,\"sku\":236554},{\"isMandatory\":0,\"sku\":236553},{\"isMandatory\":0,\"sku\":236552},{\"isMandatory\":0,\"sku\":236556},{\"isMandatory\":0,\"sku\":236952},{\"isMandatory\":0,\"sku\":402},{\"isMandatory\":0,\"sku\":401},{\"isMandatory\":0,\"sku\":400},{\"isMandatory\":0,\"sku\":2001},{\"isMandatory\":0,\"sku\":265}],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftProducts\":[{\"amount\":\"99\",\"giftName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券2门店同期不能开更开通大力度的活动，如六送\",\"isMandatory\":1,\"sku\":\"\",\"giftPoint\":\"\",\"giftPrice\":\"9999999\",\"giftPriceRatio\":1,\"isDeliveryAddress\":\"0\"}],\"giftAmount\":\"99\",\"groupName\":\"礼品领取\",\"presentType\":2}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"新客礼包券\",\"couponTitle\":\"999999元购合生元400g礼包开通赠品券的门店同期不能开\",\"couponName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"couponRemark\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"publishType\":\"3\",\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-08-06\",\"couponEndDate\":\"2018-08-06\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":2,\"areaCodes\":[\"0101\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":3,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":99999999,\"couponDeadline\":\"999\",\"amountPerCustomer\":99,\"productItems\":\"\",\"brandItem\":0,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"999999\"},\"tplId\":\"\"}";

    // ok
    // json =
    // "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"12580\",\"campaignPM\":\"12580\",\"description\":\"12580\",\"productScope\":\"\",\"anum\":999,\"aproducts\":[{\"isMandatory\":0,\"sku\":141},{\"isMandatory\":0,\"sku\":140},{\"isMandatory\":0,\"sku\":142},{\"isMandatory\":0,\"sku\":233052},{\"isMandatory\":0,\"sku\":443},{\"isMandatory\":0,\"sku\":444},{\"isMandatory\":0,\"sku\":445},{\"isMandatory\":0,\"sku\":131},{\"isMandatory\":0,\"sku\":133},{\"isMandatory\":0,\"sku\":280},{\"isMandatory\":0,\"sku\":283},{\"isMandatory\":0,\"sku\":299}],\"bnum\":999,\"bproducts\":[{\"isMandatory\":0,\"sku\":263},{\"isMandatory\":0,\"sku\":189},{\"isMandatory\":0,\"sku\":190},{\"isMandatory\":0,\"sku\":264},{\"isMandatory\":0,\"sku\":2000},{\"isMandatory\":0,\"sku\":346},{\"isMandatory\":0,\"sku\":344},{\"isMandatory\":0,\"sku\":343},{\"isMandatory\":0,\"sku\":347},{\"isMandatory\":0,\"sku\":345},{\"isMandatory\":0,\"sku\":236555},{\"isMandatory\":0,\"sku\":236554},{\"isMandatory\":0,\"sku\":236553},{\"isMandatory\":0,\"sku\":236552},{\"isMandatory\":0,\"sku\":236556},{\"isMandatory\":0,\"sku\":236952},{\"isMandatory\":0,\"sku\":402},{\"isMandatory\":0,\"sku\":401},{\"isMandatory\":0,\"sku\":400},{\"isMandatory\":0,\"sku\":2001},{\"isMandatory\":0,\"sku\":265}],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftProducts\":[{\"amount\":\"99\",\"giftName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券2门店同期不能开更开通大力度的活动，如六送\",\"isMandatory\":1,\"sku\":\"\",\"giftPoint\":\"\",\"giftPrice\":\"999\",\"giftPriceRatio\":1,\"isDeliveryAddress\":\"0\"}],\"giftAmount\":\"99\",\"groupName\":\"礼品领取\",\"presentType\":2}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"新客礼包券\",\"couponTitle\":\"999999元购合生元400g礼包开通赠品券的门店同期不能开\",\"couponName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"couponRemark\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"publishType\":\"3\",\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-08-06\",\"couponEndDate\":\"2018-08-06\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":2,\"areaCodes\":[\"0101\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":3,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":99999999,\"couponDeadline\":\"999\",\"amountPerCustomer\":99,\"productItems\":\"\",\"brandItem\":0,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"999999\"},\"tplId\":\"\"}";

    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"12580\",\"campaignPM\":\"12580\",\"description\":\"12580\",\"productScope\":\"\",\"anum\":999,\"aproducts\":[{\"isMandatory\":0,\"sku\":141},{\"isMandatory\":0,\"sku\":140},{\"isMandatory\":0,\"sku\":142},{\"isMandatory\":0,\"sku\":233052},{\"isMandatory\":0,\"sku\":443},{\"isMandatory\":0,\"sku\":444},{\"isMandatory\":0,\"sku\":445},{\"isMandatory\":0,\"sku\":131},{\"isMandatory\":0,\"sku\":133},{\"isMandatory\":0,\"sku\":280},{\"isMandatory\":0,\"sku\":283},{\"isMandatory\":0,\"sku\":299}],\"bnum\":999,\"bproducts\":[{\"isMandatory\":0,\"sku\":263},{\"isMandatory\":0,\"sku\":189},{\"isMandatory\":0,\"sku\":190},{\"isMandatory\":0,\"sku\":264},{\"isMandatory\":0,\"sku\":2000},{\"isMandatory\":0,\"sku\":346},{\"isMandatory\":0,\"sku\":344},{\"isMandatory\":0,\"sku\":343},{\"isMandatory\":0,\"sku\":347},{\"isMandatory\":0,\"sku\":345},{\"isMandatory\":0,\"sku\":236555},{\"isMandatory\":0,\"sku\":236554},{\"isMandatory\":0,\"sku\":236553},{\"isMandatory\":0,\"sku\":236552},{\"isMandatory\":0,\"sku\":236556},{\"isMandatory\":0,\"sku\":236952},{\"isMandatory\":0,\"sku\":402},{\"isMandatory\":0,\"sku\":401},{\"isMandatory\":0,\"sku\":400},{\"isMandatory\":0,\"sku\":2001},{\"isMandatory\":0,\"sku\":265}],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftProducts\":[{\"amount\":\"99\",\"giftName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券2门店同期不能开更开通大力度的活动，如六送\",\"isMandatory\":1,\"sku\":\"\",\"giftPoint\":\"\",\"giftPrice\":\"10000\",\"giftPriceRatio\":1,\"isDeliveryAddress\":\"0\"}],\"giftAmount\":\"99\",\"groupName\":\"礼品领取\",\"presentType\":2}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"新客礼包券\",\"couponTitle\":\"999999元购合生元400g礼包开通赠品券的门店同期不能开\",\"couponName\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"couponRemark\":\"开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送开通赠品券的门店同期不能开更大力度的活动，如六送开通赠品券的门店同期不能开更开通大力度的活动，如六送\",\"publishType\":\"3\",\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-08-06\",\"couponEndDate\":\"2018-08-06\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":2,\"areaCodes\":[\"0101\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":3,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":99999999,\"couponDeadline\":\"999\",\"amountPerCustomer\":99,\"productItems\":\"\",\"brandItem\":0,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"999999\"},\"tplId\":\"\"}";

    // 201808R test
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0815\",\"campaignPM\":\"2929\",\"description\":\"d-test-0815\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-0815\",\"couponRemark\":\"d-test-0815\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-08-15\",\"couponEndDate\":\"2018-08-31\",\"productionRatio\":\"-0.60%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"10\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":\"2\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318843\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"newCustomerStorehouse\":\"1\",\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0},\"tplId\":\"\"}";

    // debug 20180820
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0815\",\"campaignPM\":\"2929\",\"description\":\"d-test-0815\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"10\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减10元\",\"couponName\":\"dd\",\"couponRemark\":\"ddd\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-08-20\",\"couponEndDate\":\"2018-08-31\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"10\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":9,\"publishWay\":\"1\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":11,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":0,\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"201808200335121534750512634\",\"customerFileTypes\":[2],\"isTerminalImport\":0,\"campaignGroupId\":\"10318843\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // 201808R 增加品牌属性
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0815\",\"campaignPM\":\"2929\",\"description\":\"d-test-0815\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-0815\",\"couponRemark\":\"d-test-0815\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-08-15\",\"couponEndDate\":\"2018-08-31\",\"productionRatio\":\"-0.60%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"10\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":\"2\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318843\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"newCustomerStorehouse\":\"1\",\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"brands\":\"1,2,3,4\"},\"tplId\":\"\"}";

    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0815\",\"campaignPM\":\"2929\",\"description\":\"d-test-0815\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-0815\",\"couponRemark\":\"d-test-0815\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-08-22\",\"couponEndDate\":\"2018-08-22\",\"productionRatio\":\"-0.60%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"10\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":\"2\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318843\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"newCustomerStorehouse\":\"1\",\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"brands\":\"1,2,3,4\"},\"tplId\":\"\"}";

    // debug 20180903
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-20180903\",\"campaignPM\":\"777771\",\"description\":\"d-test-20180903\",\"totalExpense\":0,\"productScope\":0,\"anum\":99,\"aproducts\":[{\"isMandatory\":0,\"sku\":354}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":-12000,\"discount\":0,\"giftProducts\":[{\"amount\":1,\"giftName\":\"合生元呵护4段 3-7岁（巧克力味）\",\"isMandatory\":1,\"sku\":354}],\"giftAmount\":1,\"groupName\":\"商品领取\",\"presentType\":1,\"ration\":0},{\"costPoint\":\"88\",\"discount\":null,\"giftAmount\":0,\"giftProducts\":[],\"groupName\":\"赠送积分\",\"ration\":\"\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":false,\"templateName\":\"买赠券\",\"couponTitle\":\"买商品送同品\",\"couponName\":\"ddd\",\"couponRemark\":\"ddd\",\"publishType\":3,\"couponType\":2,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-09-03\",\"couponEndDate\":\"2018-09-30\",\"productionRatio\":\"-11.02%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":\"2\",\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318853\",\"isSameProduct\":true,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":0,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";
    // fix
    // json
    // ="{\"apoint\":0,\"campaignGroupId\":10318852,\"campaignGroupType\":1,\"campaignGroupName\":\"111\",\"campaignPM\":\"777771\",\"description\":\"11\",\"productScope\":0,\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"spu\":null,\"sku\":299},{\"isMandatory\":0,\"spu\":null,\"sku\":142},{\"isMandatory\":0,\"spu\":null,\"sku\":131},{\"isMandatory\":0,\"spu\":null,\"sku\":140},{\"isMandatory\":0,\"spu\":null,\"sku\":141},{\"isMandatory\":0,\"spu\":null,\"sku\":133},{\"isMandatory\":0,\"spu\":null,\"sku\":444},{\"isMandatory\":0,\"spu\":null,\"sku\":445},{\"isMandatory\":0,\"spu\":null,\"sku\":233052},{\"isMandatory\":0,\"spu\":null,\"sku\":443},{\"isMandatory\":0,\"spu\":null,\"sku\":280},{\"isMandatory\":0,\"spu\":null,\"sku\":283}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"新客礼包券\",\"couponTitle\":\"11元购合生元400g礼包\",\"couponName\":\"111\",\"couponRemark\":\"111\",\"publishType\":3,\"couponType\":13,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2018-09-03\",\"couponEndDate\":\"2018-09-03\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":100,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":4,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"value\":\"\"}],\"coupon633Type\":[],\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"srcCouponDefId\":10325141,\"productItems\":\"203\",\"brandItem\":null,\"skuItems\":null,\"customerFileTypes\":[],\"isTerminalImport\":0,\"isSameProduct\":0,\"isSameSeries\":0,\"maxCost\":0,\"specifiedPrice\":\"11\",\"isTerminalQuota\":0,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"2\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":null,\"abnormalUser\":null,\"custPay\":\"11\",\"newCustomerStorehouse\":\"0\"},\"tplId\":167}";

    // 201809R test 总部 create
    loginId = "2929";
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"d-test-20180904\",\"campaignPM\":\"2929\",\"description\":\"d-test-20180904\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-20180912\",\"couponRemark\":\"d-test-20180912\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-09-12\",\"couponEndDate\":\"2018-09-30\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":81,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":3,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":500,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":0,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":1,\"campaignGroupId\":\"10318854\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":1,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // pm=0289 大区
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"d-test-20180904\",\"campaignPM\":\"0289\",\"description\":\"d-test-20180904\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-test-20180912\",\"couponRemark\":\"d-test-20180912\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-09-12\",\"couponEndDate\":\"2018-09-30\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":81,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":3,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":500,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":0,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":1,\"campaignGroupId\":\"10318854\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":1,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // debug 0904
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"99优惠购\",\"campaignPM\":\"10173\",\"description\":\"99优惠购\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"11\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":true,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满111元立减11元\",\"couponName\":\"\",\"couponRemark\":\"1\",\"publishType\":2,\"couponType\":5,\"aprice\":111,\"bprice\":0,\"couponStartDate\":\"2018-09-14\",\"couponEndDate\":\"2018-09-14\",\"productionRatio\":\"0.10%\",\"couponBudgetInfos\":[{\"budgetId\":81,\"ration\":1}],\"settleRatio\":\"1\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":10318856,\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"0.08\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"108\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // 201809R test 大区 create
    loginId = "0289";
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"d-area-20180907\",\"campaignPM\":\"0289\",\"description\":\"d-area-20180907\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"d-0921\",\"couponRemark\":\"d-0921\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-09-21\",\"couponEndDate\":\"2018-10-31\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":101,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":3,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":500,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":0,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":1,\"campaignGroupId\":\"10318855\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":1,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // 201810R test create

    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"d-t-20181010\",\"campaignPM\":\"2929\",\"description\":\"d-t-20181010\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"19\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":true,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减19元\",\"couponName\":\"\",\"couponRemark\":\"d-t-20181010\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-10-19\",\"couponEndDate\":\"2018-11-30\",\"productionRatio\":\"0.19%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318865\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"14.63\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"coupon_purpose\":\"2\",\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // debug-1119
    loginId = "1149";
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"d-test-20181115\",\"campaignPM\":\"1149\",\"description\":\"d-test-20181115\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"5\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减5元\",\"couponName\":\"d-l-20181119\",\"couponRemark\":\"d-test-20181119\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-11-19\",\"couponEndDate\":\"2018-11-30\",\"productionRatio\":\"-0.57%\",\"couponBudgetInfos\":[{\"budgetId\":101,\"ration\":1}],\"settleRatio\":\"50\",\"campaignScope\":3,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":200,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":0,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":1,\"campaignGroupId\":\"10318886\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":1,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\"},\"tplId\":\"\"}";

    // 201812R test_create_13
    loginId = "2929";
    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-1218\",\"campaignPM\":\"2929\",\"description\":\"d-test-1218\",\"totalExpense\":0,\"productScope\":\"\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":240257}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"20\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满1000元立减20元\",\"couponName\":\"\",\"couponRemark\":\"d-test-1218\",\"publishType\":2,\"couponType\":5,\"aprice\":1000,\"bprice\":0,\"couponStartDate\":\"2018-12-18\",\"couponEndDate\":\"2018-12-31\",\"productionRatio\":\"1.40%\",\"couponBudgetInfos\":[{\"budgetId\":121,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":120000,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318893\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"10.78\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1},\"tplId\":\"\"}";

    json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-1218\",\"campaignPM\":\"2929\",\"description\":\"d-test-1218\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"ddd\",\"publishType\":2,\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2018-12-19\",\"couponEndDate\":\"2018-12-31\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":121,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318893\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"deductTerminalGiftPoint\":\"1\",\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1},\"tplId\":\"\"}";
    ResultActions resultActions = mockMvc
            .perform(post(a6).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_page_b_scene1_couponresult() throws Exception {
    String s = "111";
    long si = 111;

    String json = "";

    json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"10325300\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

    ResultActions resultActions = mockMvc.perform(
            post(a14).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

// 201810R d9 test
@Test
public void test_q_page_b__marketActcouponresult() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"couponDefIds\":\"10325300\",\"couponCode\":\"\",\"custMobile\":\"\",\"pageSize\":20}";

    ResultActions resultActions = mockMvc.perform(
            post(a49).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_page_b_couponresult_isarea() throws Exception {

    // loginId = "0278";

    // loginId = "0289";

    loginId = "12580";//

    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    // 201807R q b-couponresult
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"\",\"couponCode\":\"149411051202\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // debug 20180731
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"aa,bb,111\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // debug 20180801
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"477179\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // debug 20180809
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"491876,490159,493969,493965\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // debug 20180917
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"10325237\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // 201809R todo-3
    json = "{\"byUsedTerminalOrgCode\":\"1\",\"campaignGroupId\":\"\",\"couponDefIds\":\"10325237\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

    // 201810R d8 test
    json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"10325300\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";

    // 201812R jira
    json = "{\"campaignGroupId\":\"123\",\"couponDefIds\":\"\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"2018-12-31 23:59:59\",\"pageNo\":\"1\",\"startDate\":\"2018-12-21 00:00:00\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";

    ResultActions resultActions = mockMvc.perform(
            post(a11).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_page_CouponDefinition() throws Exception {

    // loginId = "12580";
    loginId = "2929";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"couponDefId\":\"\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"2018-10-26\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

    // 201812R test_q_page_CouponDefinition
    json = "{\"tplId\":\"176\",\"couponDefId\":\"\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"2018-11-26\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

    json = "{\"couponDefId\":\"\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"2018-12-21\",\"createEndDate\":\"2018-12-31\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";
    ResultActions resultActions = mockMvc.perform(
            post(a21).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

// 201809R 投产比报表
@Test
public void test_q_page_inoutratio_CouponDefinition() throws Exception {

    loginId = "12580";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";
    // 201809R test 投产比报表
    json = "{\"areaCode\":\"0106\",\"campaignId\":10318826,\"couponDefId\":\"10325019\",\"couponEndTime\":\"2018-08-16\",\"couponStartTime\":\"2018-08-01\",\"couponStatus\":\"0\",\"couponType\":\"13\",\"createdEndTime\":\"2018-08-18\",\"createdStartTime\":\"2018-08-02\",\"officeCode\":\"010604\"}";

    ResultActions resultActions = mockMvc.perform(
            post(a28).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_campaigngroup() throws Exception {

    String json = "{\"groupType\":1}";

    // debug 0929
    loginId = "0278";
    loginId = "4578";

    json = "{\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"groupType\":\"\",\"createdStartTime\":\"\",\"createdEndTime\":\"\"}";

    // 201810R d7 test
    json = "{\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"groupType\":\"\",\"createdStartTime\":\"2018-10-26\",\"createdEndTime\":\"\"}";

    // debug-1114
    loginId = "1149";
    json = "{\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"groupType\":\"\",\"createdStartTime\":\"\",\"createdEndTime\":\"\"}";
    ResultActions resultActions = mockMvc
            .perform(post(a40).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
                    // .param("pageNo", "1")
                    .param("pageNo", "506").param("pageSize", "10").content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_old_page_budgetAccountQuery() throws Exception {

    loginId = "2929";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"pageNo\":1,\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishStartDate\":\"\",\"publishEndDate\":\"\"}";

    // &budgetName=&budgetStatus=&channelCode=&departmentCode=1&pageNo=1&pageSize=20
    // d8 test
    json = "{}";

    ResultActions resultActions = mockMvc.perform(post(a48).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("pageNo", "1").param("pageSize", "20").param("departmentCode", "1")

            .content(json));

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_q_old_page_getCouponBudgetLimit() throws Exception {

    loginId = "2929";
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = "{\"pageNo\":1,\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishStartDate\":\"\",\"publishEndDate\":\"\"}";

    // 201810R d6 test
    json = "{\"departmentCode\":\"\",\"channelCode\":\"00\",\"startDate\":\"201810\",\"endDate\":\"\",\"pageNo\":1,\"pageSize\":10}";

    // debug-1113
    json = "{\"departmentCode\":\"0101\",\"channelCode\":\"00\",\"startDate\":\"\",\"endDate\":\"\",\"pageNo\":1,\"pageSize\":10}";
    json = "{\"departmentCode\":\"010105\",\"channelCode\":\"00\",\"startDate\":\"\",\"endDate\":\"\",\"pageNo\":1,\"pageSize\":10}";

    // debug-1204
    json = "{\"departmentCode\":\"\",\"channelCode\":\"00\",\"startDate\":\"201812\",\"endDate\":\"\",\"pageNo\":1,\"pageSize\":10}";

    // 201812R test_q_old_page_getCouponBudgetLimit
    json = "{\"departmentCode\":\"\",\"channelCode\":\"00\",\"startDate\":\"201812\",\"endDate\":\"\",\"pageNo\":1,\"pageSize\":10}";

    ResultActions resultActions = mockMvc.perform(
            post(a46).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

                    .content(json));

    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

// 201810R test d2
@Test
public void test_isLoginUserManageedBySomeOne() throws Exception {

    LoginUser u = getTestLoginUser("010106");

    LoginUser uCreated = getTestLoginUser("0101");

    boolean b = UserServiceUtil.isLoginUserManageedBySomeOne(u, uCreated);
    System.out.println("b=" + b);
    //
    boolean b2 = UserServiceUtil.isLoginUserManageedBySomeOneV2(u, uCreated);
    System.out.println("b2=" + b2);

}

// 201810R d1
@Test
public void test_DepartmentServiceUtil() throws Exception {

    String orgCode = "010106";
    System.out.println("orgCode=" + orgCode);

    DepartmentOrganization newAreaCode = DepartmentServiceUtil.getSuperiorOrgCode(orgCode);
    System.out.println("newAreaCode=" + JSON.toJSONString(newAreaCode));

    //
    CacheService c = BiostimeWebApplicationContextHolder.getApplicationContext().getBean(CacheService.class);
    DepartmentOrganization newAreaCodeS = c.getDepartmentSuperiorOrgCode(orgCode);
    System.out.println("newAreaCodeS=" + JSON.toJSONString(newAreaCodeS));

    boolean isSuperior = DepartmentServiceUtil.isSuperior(orgCode, "0101");
    System.out.println("isSuperior=" + isSuperior);

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

/***
 * 方法描述: fastjson
 *
 * @throws Exception
 * @author 0970
 * @createDate 2016年12月14日 下午5:09:47
 */
@Test
public void testjson() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));
}

@Test
public void test_import_file_xls() throws Exception {
    String fileName = "F:/biostime2/file/优惠券门店-debug0608.xls";

    fileName = "F:/biostime2/file/优惠券门店-debug-20181115.xls";

    fileName = "F:/biostime2/file/优惠券门店配额-debug-20181122.xls";

    String json = "";

    json = "";

    // debug-1122
    loginId = "2505";
    String couponDefId = "10325547";

    MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc
            .perform(fileUpload(a4).file(file).param("loginId", loginId).param("couponDefId", couponDefId)

                    .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void test_import_file() throws Exception {
    String fileName = "F:/biostime2/file/连锁门店sc-20180709.txt";

    fileName = "F:/biostime2/file/东北上海门店-debug-20181115.txt";

    String json = "";

    json = "";

    loginId = "1149";

    MockMultipartFile file = new MockMultipartFile("receiveTxt", "orgin-receiveTxt", null,
            Files.readAllBytes(Paths.get(fileName)));

    ResultActions resultActions = mockMvc.perform(
            fileUpload(a3).file(file).param("loginId", loginId).param("couponType", "5").param("excludedFlag", "0")

                    .content(json));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void testRequestBody() throws Exception {
    CouponDefinitionSearch o = new CouponDefinitionSearch();
    o.setGroupId(123456l);
    System.out.println(JSON.toJSONString(o));

    String json = "";

    json = JSON.toJSONString(o);

    ResultActions resultActions = mockMvc.perform(post(a2).contentType(MediaType.APPLICATION_JSON)
            .param("loginId", loginId).param("pmId", "").content(json));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}

@Test
public void testGet() throws Exception {

    ResultActions resultActions = mockMvc.perform(get(a1).param("loginId", loginId).param("pmId", loginId));
    // .param("tsno", "20160805093800001").param("devid", "123")
    // .param("accessToken", token).param("authData", "123"));
    System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

}
```

}

