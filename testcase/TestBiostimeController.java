package com.biostime.coupon.biostimeweb.controller;

import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget_Accounts_channel_baby;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget_Accounts_status_enable;
import static com.biostime.coupon.biostimeweb.common.constant.RespMessageEnum.Logic_custom;
import static com.biostime.coupon.biostimeweb.common.util.ExcelUtil.getXlsTpl;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON;
import com.biostime.coupon.biostimeweb.bean.BiostimeCampaignNCouponDefBean;
import com.biostime.coupon.biostimeweb.bean.JsonResult;
import com.biostime.coupon.biostimeweb.bean.common.Pager;
import com.biostime.coupon.biostimeweb.bean.merchandiser.SKU;
import com.biostime.coupon.biostimeweb.bean.search.CouponDefinitionSearch;
import com.biostime.coupon.biostimeweb.cache.CacheService;
import com.biostime.coupon.biostimeweb.common.constant.BiostimeGroupEnum;
import com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum;
import com.biostime.coupon.biostimeweb.common.constant.EnumUtil;
import com.biostime.coupon.biostimeweb.common.task.IssuseSwisseCouponByCouponDefinitionTplTask;
import com.biostime.coupon.biostimeweb.common.task.UpdateCouponStatusTask;
import com.biostime.coupon.biostimeweb.common.util.BeanUtil;
import com.biostime.coupon.biostimeweb.common.util.DateUtil;
import com.biostime.coupon.biostimeweb.coupon.service.CouponQueryService;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.CouponDefinitionOa;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.CouponOa;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.NcChannelCode;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.NcOfficeCode;
import com.biostime.coupon.biostimeweb.coupon.settlement.repository.SettlementRepository;
import com.biostime.coupon.biostimeweb.department.bean.DepartmentOrganization;
import com.biostime.coupon.biostimeweb.department.repository.DepartmentRepository;
import com.biostime.coupon.biostimeweb.department.service.DepartmentServiceUtil;
import com.biostime.coupon.biostimeweb.finance.bean.DepartmentOrganizationBudgetAccounts;
import com.biostime.coupon.biostimeweb.finance.service.BudgetScene;
import com.biostime.coupon.biostimeweb.finance.service.SettlementScene;
import com.biostime.coupon.biostimeweb.product.service.ProductServiceUtil;
import com.biostime.coupon.biostimeweb.query.bean.BudgetAccountsQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeScene1Query;
import com.biostime.coupon.biostimeweb.query.bean.OrgAllBudgetAccountsQuery;
import com.biostime.coupon.biostimeweb.rule.CouponConfigRule;
import com.biostime.coupon.biostimeweb.scene.repository.ReadOnlyRepository;
import com.biostime.coupon.biostimeweb.scene.service.CreateCouponScene;
import com.biostime.coupon.biostimeweb.task.TaskServiceUtil;
import com.biostime.coupon.biostimeweb.user.bean.LoginUser;
import com.biostime.coupon.biostimeweb.user.service.UserServiceUtil;
import com.biostime.coupon.biostimeweb.util.ParamUtil;
import com.biostime.coupon.biostimeweb.validate.service.ValidateServiceUtil;
import com.biostime.coupon.biostimeweb.webservice.WebserviceUtil;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.OaRequestLevel;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.OaResult;
import com.biostime.coupon.biostimeweb.webservice.oa.inout.OaB27EShopRequest;
import com.biostime.coupon.biostimeweb.webservice.oa.inout.OaB31ChildChannelRequest;
import com.biostime.coupon.biostimeweb.webservice.oa.inout.OnlineInfoAttr;
import com.biostime.coupon.model.domain.CouponBudgetAccount;
import com.biostime.coupon.model.domain.CouponBudgetLimit;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mama100.merchandise.vo.exception.MerchandiseException;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = { "classpath:/spring-conf/coupon-spring.xml",
		"classpath:/servlet-context.xml" }, inheritLocations = true)
@WebAppConfiguration
public class TestBiostimeController extends AbstractJUnit4SpringContextTests {

	private MockMvc mockMvc;

	String t1 = "/admin/testcase/WebserviceUtil/getOaUserinfo.action";
	String t2 = "/admin/testcase/UserTerminalScene/isUse.action";
	String t3 = "/admin/testcase/task/CouponBudgetLimitDetailReport.action";

	String a0 = "";
	String loginId = "2929";// 总部
	String a1 = "/coupon/systemMgrController/getPMName.action";
	String a1_1 = a1 + "?" + "pmId=" + loginId + "&longiId=" + loginId;
	String a2 = "/coupon/couponMgrController/getCouponDefList.action";
	String a3 = "/coupon/couponMgrController/importTerminalTxt.action";
	String a4 = "/coupon/couponTerminalMgrController/importCouponTerminal.action";
	// String a4R = "/b/coupon/config/department/importCouponTerminal.action";

	String a5 = "/coupon/couponMgrController/calculateProductionRatio.action";
	String a6 = "/b/coupon/config/create.action";
	String a6_201809R = "/b/coupon/config/v2/create.action";
	String a7 = "/coupon/couponMgrController/getCouponDefDetails.action";
	String a8 = "/b/coupon/q/scene1/CampaignGroup.action";
	String a9 = "/b/coupon/config/tpl/q/detail/CouponDefinitionTpl.action";
	String a10 = "/m/coupon/config/q/CouponDefinition.action";
	String a11 = "/b/coupon/q/CouponResult.action";
	String a12 = "/coupon/couponMgrController/v2/calculateProductionRatio.action";
	String a13 = "/b/coupon/finance/calculateProductionRatio.action";
	String a14 = "/b/coupon/q/scene1/CouponResult.action";
	String a15 = "/b/coupon/config/q/detail/CampaignGroup.action";
	String a16 = "/b/coupon/export/scene1/CouponResult.action";
	String tbda17 = "/coupon/couponMgrController/importCustomerTxt.action";

	String a18 = "/b/coupon/config/tpl/q/CouponDefinitionTpl.action";
	String a19 = "/coupon/merchandiserMgrController/getCouponCategories.action";
	String a20 = "/b/coupon/q/enum.action";
	String a21 = "/b/coupon/config/q/CouponDefinition.action";
	String a22 = "/b/coupon/config/tpl/create.action";
	String a23 = "/b/coupon/config/batch/publish.action";
	String a24 = "/coupon/couponMgrController/listNationCoupon.action";
	String a25 = "/m/coupon/v55/manjian/create.action";

	String a26 = "/m/coupon/v55/xianjin/create.action";
	String a27 = "/m/coupon/v55/queryCouponDefDetail.action";
	String a28 = "/b/coupon/config/q/inoutratio/CouponDefinition.action";
	String a29 = "/coupon/couponDepartmentMgrController/getCampaignDepartmentByCampaignId.action";
	String a29R = "/b/coupon/config/department/q/CampaignDepartment.action";

	String a30 = "/b/coupon/v55/exportCouponTerminalQuota.action";
	// String a30R =
	// "/b/coupon/config/department/export/CouponTerminalQuota.action";

	String a31 = "/coupon/couponDepartmentMgrController/getCouponDepartmentQuotaList.action";
	String a31R = "/b/coupon/config/department/q/CampaignDepartmentQuota.action";

	String a32 = "/coupon/couponDepartmentMgrController/importCouponDepartment.action";
	String a32R = "/b/coupon/config/department/import/CampaignDepartmentQuota.action";
	String a33 = "/b/coupon/config/department/export/CampaignDepartmentQuota.action";
	//
	String a34 = "/b/coupon/v55/exportCouponDepartmentQuota.action";
	String a35 = "/b/coupon/config/department/office/q/CampaignDepartmentQuota.action";
	String a36 = "/b/coupon/config/department/office/export/CampaignDepartmentQuota.action";
	String a37 = "/b/coupon/config/department/office/import/CampaignDepartmentQuota.action";
	String a38 = "/coupon/couponTerminalMgrController/getCouponTerminalQuotaList.action";
	String a39 = "/b/coupon/config/export/inoutratio/CouponDefinition.action";

	String a40 = "/b/coupon/q/CampaignGroup.action";
	String a41 = "/b/coupon/config/update.action";

	// String a43 = "/m/coupon/v55/product/import.action";
	String a44 = "/m/coupon/config/update.action";

	String a45 = "/coupon/systemMgrController/getOfficeList.action";
	String a46 = "/coupon/couponBudgetMgrController/getCouponBudgetLimit.action";

	String a47 = "/coupon/couponBudgetMgrController/getCouponBudgetLimitDetail.action";
	String a48 = "/b/coupon/v55/budgetAccountQuery.action";

	String a48_201812R = "/b/coupon/finance/budget/q/BudgetAccounts.action";

	String a49 = "/b/coupon/q/marketAct/CouponResult.action";

	String a50 = "/coupon/couponIntegralMgrController/getCouponDefinitionById.action";

	String a51 = "/b/coupon/v55/exportBudgetList.action";
	String a52 = "/b/coupon/v55/addBudgetAccount.action";

	String a52_201812R = "/b/coupon/finance/budget/create/BudgetAccounts.action";

	String a53 = "/coupon/couponBudgetMgrController/importCouponBudget.action";

	// String a53_201812R =
	// "/b/coupon/finance/budget/import/BudgetAccountQuota.action";
	String a54 = "/coupon/couponBudgetMgrController/exportCouponBudgetTemplate.action";

	String a55 = "/m/coupon/v55/issueCoupon.action";

	String a56 = "/coupon/couponMgrController/getMktActivitiesBudget.action";
	String a56_201812R = "/b/coupon/finance/budget/q/scene1/BudgetAccounts.action";
	String a57 = "/b/coupon/finance/getBalanceOfCurrentMonth.action";

	String a58 = "/m/coupon/v55/terminal/import.action";
	String a59 = "/m/coupon/v55/customer/import.action";

	String a60 = "/m/coupon/v55/product/import.action";
	String a61 = "/b/coupon/v55/budgetAccountUpdate.action";

	String a61_201812R = "/b/coupon/finance/budget/disable/BudgetAccounts.action";

	String a62 = "/b/coupon/finance/budget/getBalanceOfCurrentMonth.action";
	String a62_v2 = "/b/coupon/finance/budget/getBalanceOfCurrentMonth.action";
	String a63 = "/b/coupon/finance/budget/q/report/BudgetAccounts.action";
	String a64 = "/b/coupon/finance/budget/export/report/BudgetAccounts.action";

	String a65 = "/b/coupon/finance/budget/export/BudgetAccounts.action";
	String a66 = "/b/coupon/finance/budget/quota/template/export/BudgetAccounts.action";
	String a67 = "/b/coupon/finance/budget/quota/import/BudgetAccounts.action";

	String a68 = "/coupon/merchandiserMgrController/getSPUByCate.action";
	String a69 = "/b/coupon/v55/exportCouponBudgetLimitDetail.action";

	String a71 = "/b/coupon/config/CommonGlobal.action";
	String a72 = "/b/coupon/report/export/CouponBudgetLimitDetail.action";
	String a73 = "/b/coupon/report/export/exist/CouponBudgetLimitDetail.action";
	String a74 = "/b/coupon/config/export/CouponDefinition.action";

	String a75 = "/b/coupon/settlement/import/NcBudgetData.action";

	String a76 = "/b/coupon/settlement/export/NcBudgetData.action";
	String a77 = "/b/coupon/settlement/template/export/NcBudgetData.action";
	String a78 = "/b/coupon/settlement/q/NcBudgetData.action";

	String a79 = "/b/coupon/settlement/calculate/discount/CouponDefinitionOa.action";

	String a80 = "/b/coupon/settlement/q/CouponDefinitionOaQuery.action";
	String a81 = "/b/coupon/settlement/export/CouponDefinitionOaQuery.action";

	String a82 = "/b/coupon/settlement/q/CouponOaQuery.action";
	String a83 = "/b/coupon/settlement/export/CouponOaQuery.action";
	String a84 = "/b/coupon/settlement/apply/CouponnOa.action";

	String a85 = "/b/coupon/settlement/CouponDefinitionOa/status/update.action";

	String a86 = "/admin/user/info.action";

	String a87 = "/b/coupon/settlement/q/NcOfficeCode.action";
	String a88 = "/b/coupon/settlement/q/NcChannelCode.action";

	@Test
	public void test_t1() throws Exception {

		ResultActions resultActions = mockMvc
				.perform(get(t1).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

				);

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Test
	public void test_q_NcChannelCode() throws Exception {

		String json = "";

//		loginId = "0022";// office
//		loginId = "10593";// office
		json = "{}";

		ResultActions resultActions = mockMvc
				.perform(post(a88).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_q_NcOfficeCode() throws Exception {

		String json = "";

//		loginId = "0022";// office
//		loginId = "10593";// office
		json = "{}";

		ResultActions resultActions = mockMvc
				.perform(post(a87).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_read_nc_office_code() throws Exception {
		// String filePath = FileUtil.getFilePathCommon(FileUtil.getDirNc(),
		// NcBudgetDataXlsName);
		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";

		SettlementScene settlementScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(SettlementScene.class);

		List<NcOfficeCode> xlsData = null;

		// src data

		xlsData = settlementScene.getNcOfficeCodeFromInputStream(Files.newInputStream(Paths.get(filePath)));
		System.out.println("###xlsData size=" + xlsData.size());
		System.out.println(JSON.toJSONString(xlsData));

		// v2
		xlsData = settlementScene.getNcOfficeCodeFromInputStreamV2(Files.newInputStream(Paths.get(filePath)));
		System.out.println("###xlsData v2 size=" + xlsData.size());
		System.out.println(JSON.toJSONString(xlsData));

		List<NcOfficeCode> l = BeanUtil.convertJson2List(NcOfficeCode.nc_office_code_json, NcOfficeCode.class);
		System.out.println(JSON.toJSONString(l));

	}

	@Test
	public void test_read_nc_channel() throws Exception {
		// String filePath = FileUtil.getFilePathCommon(FileUtil.getDirNc(),
		// NcBudgetDataXlsName);
		String filePath = "F:/biostime2/file/nc_channel.xls";

		SettlementScene settlementScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(SettlementScene.class);

		List<NcChannelCode> xlsData = null;

		// src data

		xlsData = settlementScene.getNcChannelCodeFromInputStream(Files.newInputStream(Paths.get(filePath)));
		System.out.println("###xlsData size=" + xlsData.size());
		System.out.println(JSON.toJSONString(xlsData));

		// v2
		xlsData = settlementScene.getNcChannelCodeFromInputStreamV2(Files.newInputStream(Paths.get(filePath)));
		System.out.println("###xlsData v2 size=" + xlsData.size());
		System.out.println(JSON.toJSONString(xlsData));

		List<NcChannelCode> l = BeanUtil.convertJson2List(NcChannelCode.nc_channel_json, NcChannelCode.class);
		System.out.println(JSON.toJSONString(l));

	}

	// 201904R oa getHrmUserInfoWithPage
	@Test
	public void test_oa_getHrmUserInfoWithPage() throws Exception {

		OaResult oaResult = WebserviceUtil.getOaUserinfo();
		System.out.println(JSON.toJSON(oaResult));

		if (oaResult == OaResult.Hrm_response_success) {
			System.out.println(OaResult.Hrm_response_success.getDesc());
		} else {
			System.out.println(OaResult.Hrm_response_fail.getDesc());
		}

	}

	@Test
	public void test_getDepartmentOrganizationList() throws Exception {

		DepartmentRepository departmentRepository = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(DepartmentRepository.class);

		List<DepartmentOrganization> departmentOrganizationObjs = departmentRepository
				.getDepartmentOrganizationList(BiostimeGroupEnum.Department_Organization_tier_area.getCode(), "", true);

		System.out.println(JSON.toJSONString(departmentOrganizationObjs));
	}

	// 201904R create oa k24
	@Test
	public void test_submit_oa_k24() throws Exception {

		OaResult oaResult = WebserviceUtil.submitOAK24();
		System.out.println(JSON.toJSON(oaResult));

		if (oaResult == OaResult.SUCCESS) {
			System.out.println(OaResult.SUCCESS.getDesc());
		}

	}

	// 20190423 test_submit_oa
	@Test
	public void test_submit_oa_dummy() throws Exception {

		String url = "http://10.50.115.128/services/WorkflowService?wsdl";
		OaB31ChildChannelRequest oaB31ChildChannelRequest = new OaB31ChildChannelRequest();
		oaB31ChildChannelRequest.setApplyId("99999");// oa字段-- 申请单号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String date = sdf.format(new Date());
		// oaB31ChildChannelRequest.setApplyDate(date);
		oaB31ChildChannelRequest.setApplyDate("2019-04-23");// oa字段-- 申请日期
		oaB31ChildChannelRequest.setCreatorId("0970");// oa字段-- 创建人
		// oaB31ChildChannelRequest.setCreatorId("3588");
		oaB31ChildChannelRequest.setRequestLevel(OaRequestLevel.NORMAL.toString());// oa字段 -- 紧急程度
		oaB31ChildChannelRequest.setRequestName("新建终端");// oa字段-- 标题

		System.out.println(JSON.toJSON(oaB31ChildChannelRequest));

		OaResult rev = WebserviceUtil.submitOADummy(oaB31ChildChannelRequest, url);
		System.out.println(JSON.toJSON(rev));
		Assert.assertTrue(rev == OaResult.SUCCESS);

	}

	@Test
	public void test_q_NcBudgetData2() throws Exception {

		String json = "";

		loginId = "0022";// office
		loginId = "10593";// office
		json = "{}";

		ResultActions resultActions = mockMvc
				.perform(post(a78).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_admin_user() throws Exception {
		CouponDefinitionSearch o = new CouponDefinitionSearch();
		o.setGroupId(123456l);
		System.out.println(JSON.toJSONString(o));

		String json = "";

		loginId = "0278";// office
		json = "";

		ResultActions resultActions = mockMvc
				.perform(post(a86).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_EnumUtil() {

		String status = "4";
		List<CouponConfigEnum> enums = EnumUtil.getCouponConfigEnumByPrefix("Settlement_Oa_status");
		System.out.println(enums.toString());

		boolean b = enums.stream().anyMatch(e -> e.getCode() == ParamUtil.convertParam2Integer(status));
		System.out.println(b);
	}

	@Test
	public void test_CouponDefinitionOa_status_update() throws Exception {

		String oaCode = "123";
		String status = "2";
		// status = "1";
		// status = "4";// Logic_custom, "要修改的状态=" + query.getStatus() + "没有被定义,无法修改"
		ResultActions resultActions = mockMvc.perform(get(a85).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("oaCode", oaCode).param("status", status)

		);

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_UserServiceUtil() throws Exception {
		loginId = "10593";
		LoginUser u = UserServiceUtil.getLoginUser(loginId);
		System.out.println(JSON.toJSONString(u));

	}

	@Test
	public void test_apply_CouponnOa() throws Exception {
		CouponDefinitionSearch o = new CouponDefinitionSearch();
		o.setGroupId(123456l);
		System.out.println(JSON.toJSONString(o));

		String json = "";

		// 201904R test_apply_CouponnOa
		loginId = "10593";// office
		json = "";

		json = "{\"defId\":123}";

		json = "{\"defId\":123}";// Param_custom_empty, "优惠券不存在,请检查!"
		json = "{\"defId\":166388}";// Logic_custom, "申请核销的券必须为满额立减券或者满件立减券"
		json = "{\"defId\":10325953}";// Logic_custom, "该优惠券未过期，请过期后再申请核销"
		json = "{\"defId\":10325938}";// Logic_custom, "该优惠券的门店核销方式是积分结算，无法申请核销"
		json = "{\"defId\":10325948}";// Logic_custom, "该优惠券已经申请核销，不能重复申请"
		json = "{\"budgetCategory\":\"w用户选择\",\"budgetCategoryCode\":\"3\",\"budgetDepartment\":\"w用户选择\",\"budgetDepartmentCode\":\"2\",\"budgetProject\":\"w用户选择\",\"budgetProjectCode\":\"4\",\"budgetUnit\":\"w用户选择\",\"budgetUnitCode\":\"1\",\"defId\":10325898,\"payment\":1}";
		// 事务控制,写从表报错, 主表不写
		json = "{\"budgetCategory\":\"w用户选择\",\"budgetCategoryCode\":\"3\",\"budgetDepartment\":\"w用户选择用户选择用户选择用户选择用户选择用户选择用户选择用户选择\",\"budgetDepartmentCode\":\"2\",\"budgetProject\":\"w用户选择\",\"budgetProjectCode\":\"4\",\"budgetUnit\":\"w用户选择\",\"budgetUnitCode\":\"1\",\"defId\":10325898,\"payment\":1}";
		// 事务控制后
		json = "{\"budgetCategory\":\"w用户选择\",\"budgetCategoryCode\":\"3\",\"budgetDepartment\":\"w用户选择\",\"budgetDepartmentCode\":\"2\",\"budgetProject\":\"w用户选择\",\"budgetProjectCode\":\"4\",\"budgetUnit\":\"w用户选择\",\"budgetUnitCode\":\"1\",\"defId\":10325898,\"payment\":1}";

		// 201904R 20190428 逻辑修改后
		loginId = "0022";// office
		json = "{\"dataList\":[{\"budgetCategory\":\"w用户选择\",\"budgetCategoryCode\":\"3\",\"budgetDepartment\":\"w用户选择\",\"budgetDepartmentCode\":\"2\",\"budgetProject\":\"w用户选择\",\"budgetProjectCode\":\"4\",\"budgetUnit\":\"w用户选择\",\"budgetUnitCode\":\"1\",\"inputChannelCode\":\"01\",\"inputDepartmentCode\":\"010604\"},{\"budgetCategory\":\"w用户选择\",\"budgetCategoryCode\":\"3\",\"budgetDepartment\":\"w用户选择\",\"budgetDepartmentCode\":\"2\",\"budgetProject\":\"w用户选择\",\"budgetProjectCode\":\"4\",\"budgetUnit\":\"w用户选择\",\"budgetUnitCode\":\"1\",\"inputChannelCode\":\"01\",\"inputDepartmentCode\":\"010604\"}],\"defId\":10325803,\"payment\":1}";

		json = "{\"payment\":2,\"defId\":\"10325803\",\"dataList\":[{\"inputDepartmentCode\":\"010604\",\"inputChannelCode\":\"01\",\"budgetCategory\":\"B-品牌与消费者沟通物料\",\"budgetCategoryCode\":\"22\",\"budgetDepartment\":\"华南一区\",\"budgetDepartmentCode\":\"b1\",\"budgetProject\":\"A-婴线其他\",\"budgetProjectCode\":\"12\",\"budgetUnit\":\"华南一区单位\",\"budgetUnitCode\":\"a1\"}]}";
		loginId = "4884";// office
		json = "{\"payment\":2,\"defId\":\"10325758\",\"dataList\":[{\"inputDepartmentCode\":\"010604\",\"inputChannelCode\":\"08\",\"budgetCategory\":\"BNC-教育项目\",\"budgetCategoryCode\":\"2013\",\"budgetDepartment\":\"市场部\",\"budgetDepartmentCode\":\"201\",\"budgetProject\":\"教育-线上及社群[PM文静]\",\"budgetProjectCode\":\"PM0067\",\"budgetUnit\":\"市场部1\",\"budgetUnitCode\":\"201\"},{\"inputDepartmentCode\":\"010604\",\"inputChannelCode\":\"01\",\"budgetCategory\":\"BNC-总裁项目费用\",\"budgetCategoryCode\":\"2045\",\"budgetDepartment\":\"教育业务咨询部\",\"budgetDepartmentCode\":\"203\",\"budgetProject\":\"BNC-中国区总裁[PM朱定平]\",\"budgetProjectCode\":\"PM0056\",\"budgetUnit\":\"教育业务咨询部1\",\"budgetUnitCode\":\"203\"}]}";

		//debug 0523
		loginId = "2305";// office
		json ="{\"payment\":2,\"defId\":\"10326120\",\"dataList\":[{\"inputDepartmentCode\":\"010110\",\"inputChannelCode\":\"03\",\"budgetCategory\":\"BNC-总裁项目费用\",\"budgetCategoryCode\":\"2045\",\"budgetDepartment\":\"BNC-中国区总部\",\"budgetDepartmentCode\":\"202\",\"budgetProject\":\"BNC-中国区总裁[PM朱定平]\",\"budgetProjectCode\":\"PM0056\",\"budgetUnit\":\"BNC-中国区总部1\",\"budgetUnitCode\":\"202\"}]}";
		ResultActions resultActions = mockMvc
				.perform(post(a84).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_calculate_CouponDefinitionOa() throws Exception {
		CouponDefinitionSearch o = new CouponDefinitionSearch();
		o.setGroupId(123456l);
		System.out.println(JSON.toJSONString(o));

		String json = "";

		// 201904R test_calculate_CouponDefinitionOa
		loginId = "10593";// office
		json = "";

		json = "{\"defId\":123}";

		json = "{\"defId\":123}";// Param_custom_empty, "优惠券不存在,请检查!"
		json = "{\"defId\":166388}";// Logic_custom, "申请核销的券必须为满额立减券或者满件立减券"
		json = "{\"defId\":10325953}";// Logic_custom, "该优惠券未过期，请过期后再申请核销"
		json = "{\"defId\":10325938}";// Logic_custom, "该优惠券的门店核销方式是积分结算，无法申请核销"
		json = "{\"defId\":10325948}";// Logic_custom, "该优惠券已经申请核销，不能重复申请"
		json = "{\"defId\":10325898}";

		// debug
		loginId = "0022";
		json = "{\"defId\":10325803}";

		json = " {\"defId\":\"10325803\"}";
		json = " {\"defId\":\"454\"}"; // 不存在券
		// debug 20190510
		loginId = "4884";
		json = " {\"defId\":\"10325758\"}"; //

		ResultActions resultActions = mockMvc
				.perform(post(a79).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_export_CouponOaQuery() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/CouponOaQuery-export.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a83).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)// .param("", "")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_q_page_CouponOaQuery() throws Exception {
		CouponDefinitionSearch o = new CouponDefinitionSearch();
		o.setGroupId(123456l);
		System.out.println(JSON.toJSONString(o));

		String json = "";

		// 201904R test_q_page_CouponOaQuery
		loginId = "10593";// office
		json = "{\"applicantId\":\"10593\",\"applyDateEndDate\":\"2019-06-01\",\"applyDateStartDate\":\"2019-04-01\",\"archiveTimeEndDate\":\"2019-06-01\",\"archiveTimeStartDate\":\"2019-04-01\",\"areaCode\":\"111\",\"defId\":123,\"oaCode\":\"222\",\"status\":1,\"terminalCode\":\"333\"}";

		json = "{\"oaCode\":\"\",\"applicantId\":\"gfd\",\"applyDateStartDate\":\"2019-04-22 00:00:00\",\"applyDateEndDate\":\"2019-04-22 23:59:59\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";
		// debug 20190520
		json = "{\"areaCode\":\"\",\"oaCode\":\"\",\"terminalCode\":\"\",\"applicantId\":\"\",\"applyDateStartDate\":\"\",\"applyDateEndDate\":\"\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";

		// debug0513
		loginId = "4884";
		json = "{\"areaCode\":\"0106\",\"oaCode\":\"\",\"terminalCode\":\"\",\"applicantId\":\"\",\"applyDateStartDate\":\"\",\"applyDateEndDate\":\"\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";

		ResultActions resultActions = mockMvc.perform(post(a82).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageNo", "1").param("pageSize", "20")

				.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_export_CouponDefinitionOaQuery() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/CouponDefinitionOaQuery-export.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a81).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)// .param("", "")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_q_page_CouponDefinitionOaQuery() throws Exception {
		CouponDefinitionSearch o = new CouponDefinitionSearch();
		o.setGroupId(123456l);
		System.out.println(JSON.toJSONString(o));

		String json = "";

		// 201904R test_q_page_CouponDefinitionOaQuery
		loginId = "10593";// office
		json = "{\"applicantId\":\"10593\",\"archiveTimeEndDate\":\"2019-06-01\",\"archiveTimeStartDate\":\"2019-04-01\",\"createdTimeEndDate\":\"2019-06-01\",\"createdTimeStartDate\":\"2019-04-01\",\"status\":1}";

		json = "{\"applicantId\":\"10593\",\"archiveTimeEndDate\":\"\",\"archiveTimeStartDate\":\"\",\"createdTimeEndDate\":\"2019-06-01\",\"createdTimeStartDate\":\"2019-04-01\",\"status\":1}";

		json = "{\"createdTimeEndDate\":\"2019-06-01\",\"createdTimeStartDate\":\"2019-04-01\",\"defId\":123,\"status\":1}";

		loginId = "777771";
		json = "{\"oaCode\":\"\",\"applicantId\":\"10593\",\"createdTimeStartDate\":\"\",\"createdTimeEndDate\":\"\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";

		json = "{\"oaCode\":\"\",\"applicantId\":\"10593\",\"createdTimeStartDate\":\"2019-04-22 00:00:00\",\"createdTimeEndDate\":\"2019-04-22 23:59:59\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";

		json = "{\"oaCode\":\"\",\"applicantId\":\"gfd\",\"createdTimeStartDate\":\"2019-04-22 00:00:00\",\"createdTimeEndDate\":\"2019-04-22 23:59:59\",\"archiveTimeStartDate\":\"\",\"archiveTimeEndDate\":\"\"}";

		ResultActions resultActions = mockMvc.perform(post(a80).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageNo", "1").param("pageSize", "20")

				.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_save_CouponOa() throws Exception {
		// INSERT INTO MAMA100_OWNER.GC5_COUPON_OA ( ID,
		// APPLY_DATE, BUSINESS_UNIT, AREA_NAME, AREA_CODE, BUDGET_UNIT,
		// BUDGET_UNIT_CODE, BUDGET_DEPARTMENT, BUDGET_DEPARTMENT_CODE, BUDGET_CHANNEL,
		// INPUT_DEPARTMENT, INPUT_DEPARTMENT_CODE, INPUT_CHANNEL, INPUT_CHANNEL_CODE,
		// EXPENSE_POOL, BUDGET_CATEGORY, BUDGET_CATEGORY_CODE, BUDGET_PROJECT,
		// BUDGET_PROJECT_CODE, EXPENSE_NAME, DISCOUNT_AMOUNT, DESCRIPTION,
		// TERMINAL_NAME, TERMINAL_CODE, DATA_SOURCE, ACCOUNTANT, PAY_TO,
		// COUPON_DEFINITION_OA_ID) VALUES (
		// TRANSACTION_MODULE.GET_KEY_AUTO_TRANS('gc5_coupon_oa'),

		SettlementRepository repo = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(SettlementRepository.class);
		List<CouponOa> objs = new ArrayList<CouponOa>();

		CouponOa obj = new CouponOa();
		obj.setApplyDate(new Date());
		obj.setBusinessUnit("事业部");
		obj.setAreaName("大区");
		obj.setAreaCode("010704");
		obj.setBudgetUnit("用户选择");
		obj.setBudgetUnitCode("1");
		obj.setBudgetDepartment("用户选择");
		obj.setBudgetDepartmentCode("2");
		obj.setBudgetChannel("预算渠道");
		obj.setInputDepartment("投产部门");
		obj.setInputDepartmentCode("010704");
		obj.setInputChannel("投产渠道");
		obj.setInputChannelCode("8");
		obj.setExpensePool("营销费用");
		obj.setBudgetCategory("用户选择");
		obj.setBudgetCategoryCode("3");
		obj.setBudgetProject("用户选择");
		obj.setBudgetProjectCode("4");
		obj.setExpenseName("消费者奖励");
		obj.setDiscountAmount(ParamUtil.convertParam2BigDecimal("100.99"));
		obj.setDescription("满减券核销");
		obj.setTerminalName("终端");
		obj.setTerminalCode("900001");
		obj.setDataSource("营销通");
		obj.setAccountant("财务部");
		obj.setPayTo("对公");
		obj.setCouponDefinitionOaId(2l);

		objs.add(obj);
		objs.add(obj);

		repo.saveCouponOa(objs);

	}

	@Test
	public void test_before_save_CouponDefinitionOa() throws Exception {
		SettlementRepository repo = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(SettlementRepository.class);

		Long id = repo.getPk("gc5_coupon_definition_oa");
		System.out.println(id);

	}

	@Test
	public void test_save_CouponDefinitionOa() throws Exception {
		// INSERT INTO MAMA100_OWNER.GC5_COUPON_DEFINITION_OA ( ID,
		// APPLICANT_NAME, APPLICANT_ID, AREA_NAME, AREA_CODE, OFFICE_NAME, OFFICE_CODE,
		// DEF_ID,
		// DISCOUNT_AMOUNT, OA_CODE, STATUS, CREATED_TIME, ARCHIVE_TIME, OA_ID,
		// UPDATED_TIME, UPDATED_BY, PAYMENT)
		// VALUES ( TRANSACTION_MODULE.GET_KEY_AUTO_TRANS('gc5_coupon_definition_oa'),
		// ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )

		SettlementRepository repo = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(SettlementRepository.class);
		CouponDefinitionOa o = new CouponDefinitionOa();
		Long id = repo.getPk("gc5_coupon_definition_oa");
		System.out.println(id);
		//
		o.setId(id);
		o.setApplicantName("申请人");
		o.setApplicantId("10593");
		o.setApplicantId("2929");
		o.setAreaName("大区");
		o.setAreaCode("010101");
		o.setOfficeName("办事处");
		o.setOfficeCode("0101");
		o.setDefId(123l);
		o.setDiscountAmount(ParamUtil.convertParam2BigDecimal("100.99"));
		o.setOaCode("999999");
		o.setStatus(1);
		o.setCreatedTime(new Date());
		o.setArchiveTime(null);
		o.setOaId(null);
		o.setUpdatedTime(null);
		o.setUpdatedBy(null);
		o.setPayment(1);

		int i = repo.saveCouponDefinitionOa(o);
		System.out.println(i);

	}

	@Test
	public void test_q_NcBudgetData() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/NcBudgetData-export.xls";

		String loginId = "10593";// 淄博办
		loginId = "0022";
		ResultActions resultActions = mockMvc
				.perform(post(a78).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_export_NcBudgetData() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/NcBudgetData-export.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a76).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_export_template_NcBudgetData() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/NcBudgetData-tpl.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a77).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201904R test_import_NcBudgetData
	@Test
	public void test_import_NcBudgetData() throws Exception {
		loginId = "2929";//
		// loginId = "0278";//

		String fileName = "";
		fileName = "F:/biostime2/file/nc_budget_data.xls";
		fileName = "F:/biostime2/file/nc_budget_data-logic1.xls";// check cell empty
		fileName = "F:/biostime2/file/nc_budget_data-logic2.xls";// check org code

		fileName = "F:/biostime2/file/优惠券Nc基础数据-debug.xls";// c

		String json = "";

		json = "";

		// String couponDefId = "169706";
		loginId = "0022";//
		MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
				Files.readAllBytes(Paths.get(fileName)));

		ResultActions resultActions = mockMvc.perform(fileUpload(a75).file(file).param("loginId", loginId)
				// .param("couponDefId", couponDefId)

				.content(json));
		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201904R test export CouponDefinition
	@Test
	public void test_export_CouponDefinition() throws Exception {

		String fileName = "F:/biostime2/CouponDefinition.xls";

		loginId = "2929";

		ResultActions resultActions = mockMvc
				.perform(get(a74).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)// .param("pageNo",
																									// "1")

						.param("tplId", "166")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		// System.out.println("res deric: " +
		// resultActions.andReturn().getResponse().getContentAsString());

	}

	/***
	 * 
	 * 1 收分赠品券、新客礼包券不得选择“经销商dodie、经销商HT推广费”2个费用池
	 * 2)使用经销商dodie推广费设券时，购买产品必须是dodie，经销商HT推广费设券时，购买产品必须是HT，如果不是，则判断办事处不得使用该费用池
	 * 3)如果在使用经销商dodie/HT推广费时，“满额立减”“满减立减”2种类型券，门店核销方式中不得勾选以积分方式结算，自动勾选”其他线下方式结算“，需要办事处填写公司承担比例是多少
	 * 
	 * --- rule 涉及配券接口协议 , 券种 , 预算科目 , sKu[品牌 (Prop_brands_HT_5 ,
	 * Prop_brands_Dodie_6)] , 核销计算方式
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_validate_couponconfigrule() throws Exception {

		CreateCouponScene createCouponScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(CreateCouponScene.class);

		// create json
		String create_json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"deric-test-20190404\",\"campaignPM\":\"2929\",\"description\":\"deric-test-20190404\",\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":241755},{\"isMandatory\":0,\"sku\":241455},{\"isMandatory\":0,\"sku\":102},{\"isMandatory\":0,\"sku\":240853},{\"isMandatory\":0,\"sku\":163},{\"isMandatory\":0,\"sku\":239352},{\"isMandatory\":0,\"sku\":236754},{\"isMandatory\":0,\"sku\":235555},{\"isMandatory\":0,\"sku\":233052},{\"isMandatory\":0,\"sku\":239252},{\"isMandatory\":0,\"sku\":239156},{\"isMandatory\":0,\"sku\":103},{\"isMandatory\":0,\"sku\":237353},{\"isMandatory\":0,\"sku\":237352},{\"isMandatory\":0,\"sku\":237252},{\"isMandatory\":0,\"sku\":237253},{\"isMandatory\":0,\"sku\":236953},{\"isMandatory\":0,\"sku\":127},{\"isMandatory\":0,\"sku\":302},{\"isMandatory\":0,\"sku\":436}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"deric-test-20190404\",\"publishType\":\"4\",\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2019-04-04\",\"couponEndDate\":\"2019-05-04\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":221,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":5,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";
		BiostimeCampaignNCouponDefBean campaignCoupon = get_test_obj(create_json, BiostimeCampaignNCouponDefBean.class);

		CouponConfigRule couponConfigRule = createCouponScene.getCouponConfigRule1(campaignCoupon);
		System.out.println(JSON.toJSONString(couponConfigRule));

		// validate
		JsonResult result = createCouponScene.validateCouponConfigRule1(couponConfigRule);
		System.out.println(JSON.toJSONString(result));

	}

	@Test
	public void test_exist_CouponBudgetLimitDetailReport() throws Exception {
		String yearmonth = "201902";

		yearmonth = "201904";

		ResultActions resultActions = mockMvc.perform(
				get(a73).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("yearmonth", yearmonth)

		);

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201902R test_report_CouponBudgetLimitDetail
	@Test
	public void test_report_CouponBudgetLimitDetail() throws Exception {
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/getCouponBudgetLimitDetail-download.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a72).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
						// .param("yearmonth", "201902")
						.param("yearmonth", "201903")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_CommonGlobal() throws Exception {
//?key=file_server&refresh=true&loginId=12580
		ResultActions resultActions = mockMvc.perform(get(a71).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("key", "file_server").param("refresh", "true")

		);

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_admin_CouponBudgetLimitDetailReport() throws Exception {
		String yearmonth = "201902";

		yearmonth = "201904";

		ResultActions resultActions = mockMvc.perform(
				get(t3).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("yearmonth", yearmonth)

		);

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201902R test_task_getCouponBudgetLimitDetailReport
	@Test
	public void test_task_getCouponBudgetLimitDetailReport() throws Exception {

		TaskServiceUtil.getCouponBudgetLimitDetailReport();
	}

	@Test
	public void test_exportCouponBudgetLimitDetail_201902R() throws Exception {
		// loginId =
		// "0289";http://test-01.biostime.us/biostime-coupon-web/b/coupon/finance/budget/export/report/BudgetAccounts.action?loginId=2929
		// &auditMonth=201902&departmentCode=0113&pageNo=1&pageSize=9999
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/response-exportCouponBudgetLimitDetail.xls";

		ResultActions resultActions = mockMvc.perform(get(a69).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageNo", "1").param("yearmonth", "201902")
				.param("departmentCode", "01,0106,010206").param("pageSize", "9999")

		);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_exportCouponBudgetLimitDetail() throws Exception {
		// loginId =
		// "0289";http://test-01.biostime.us/biostime-coupon-web/b/coupon/finance/budget/export/report/BudgetAccounts.action?loginId=2929
		// &auditMonth=201902&departmentCode=0113&pageNo=1&pageSize=9999
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/response-exportCouponBudgetLimitDetail.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a69).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")
						.param("yearmonth", "201903").param("departmentCode", "01").param("pageSize", "9999")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201904R test_submit_oa
	@Test
	public void test_submit_oa_2() throws Exception {

		String url = "http://10.50.115.128/services/WorkflowService?wsdl";

		OaB27EShopRequest req = new OaB27EShopRequest();
		req.setApplyId("99999");

		req.setApplyDate("2019-04-23");
		req.setCreatorId("0970");

		req.setRequestLevel(OaRequestLevel.NORMAL.toString());
		req.setRequestName("线上会员店申请流程");

		OnlineInfoAttr[] onlineInfoAttrs = new OnlineInfoAttr[1];
//		 private String platName;//电商平台名称
//
//			private String onLineStoreName;//店名
//
//			private String onLineStoreId;//id号
//
//			private String onLineStoreHttp;//网址
		OnlineInfoAttr o1 = new OnlineInfoAttr();
		o1.setPlatName("电商平台名称xx");
		o1.setOnLineStoreName("店名xxx");
		o1.setOnLineStoreId("id名xxx");
		o1.setOnLineStoreHttp("网址http");
		onlineInfoAttrs[0] = o1;
		req.setOnlineInfoAttrs(onlineInfoAttrs);

		System.out.println(JSON.toJSON(req));

		OaResult rev = WebserviceUtil.submitOAB27(req, url);
		System.out.println(JSON.toJSON(rev));
		Assert.assertTrue(rev == OaResult.SUCCESS);

	}

	// 201904R test_submit_oa
	@Test
	public void test_submit_oa() throws Exception {

		String url = "http://10.50.115.128/services/WorkflowService?wsdl";
		OaB31ChildChannelRequest oaB31ChildChannelRequest = new OaB31ChildChannelRequest();
		oaB31ChildChannelRequest.setApplyId("99999");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String date = sdf.format(new Date());
		// oaB31ChildChannelRequest.setApplyDate(date);
		oaB31ChildChannelRequest.setApplyDate("2019-04-23");
		oaB31ChildChannelRequest.setCreatorId("0970");
		// oaB31ChildChannelRequest.setCreatorId("3588");
		oaB31ChildChannelRequest.setRequestLevel(OaRequestLevel.NORMAL.toString());
		oaB31ChildChannelRequest.setRequestName("新建终端");

		System.out.println(JSON.toJSON(oaB31ChildChannelRequest));

		OaResult rev = WebserviceUtil.submitOA(oaB31ChildChannelRequest, url);
		System.out.println(JSON.toJSON(rev));
		Assert.assertTrue(rev == OaResult.SUCCESS);

	}

	@Test
	public void test_export_page_b_scene1_couponresult() throws Exception {

		String fileName = "F:/biostime2/file/b-scene1-couponresult.xls";

		String s = "111";
		long si = 111;
		CouponResultBiostimeScene1Query o = new CouponResultBiostimeScene1Query();

		o.setUsedTerminalCode("121217");
		o.setCouponCode("149411051202");

		System.out.println(JSON.toJSONString(o));

		String json = "";

		// 201807R q b-scene1-couponresult
		json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"\",\"couponCode\":\"149411051202\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\"}";

		// json =
		// "{\"campaignGroupId\":\"\",\"couponDefIds\":\"\",\"couponCode\":\"149411051202\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"-1\"}";

		//

		json = JSON.toJSONString(o);
		//// 201810R prd
		loginId = "2929";
		//
//		http://test-01.biostime.us/biostime-coupon-web/b/coupon/export/scene1/CouponResult.action?loginId=2929
		// &usedTerminalAreaCode=&usedTerminalOfficeCode=&usedTerminalChannelCode=&usedTerminalCode=&campaignGroupId=&couponType=
		// &useStatus=-1&couponDefIds=10325755&couponCode=&buyJoinTimeStart=&buyJoinTimeEnd=&pageNo=1

		ResultActions resultActions = mockMvc
				.perform(get(a16).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")
						// .param("couponCode", "149411051202")
						/*
						 * .param("usedTerminalAreaCode", "0111") .param("usedTerminalOfficeCode",
						 * "011105") .param("buyJoinTimeStart",
						 * "2018-02-26 00:00:00").param("buyJoinTimeEnd", "2018-10-20 23:59:59")
						 */
						.param("couponDefIds", "10325755").param("useStatus", "-1")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_q_campaigngroup_scene1() throws Exception {

		// loginId = "12580";//isoffice

		String json = "{\"groupType\":\"\",\"groupId\":\"\",\"groupName\":\"\"}";

		// debug-20190130
		json = "{\"groupType\":1}";

		ResultActions resultActions = mockMvc.perform(post(a8).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageNo", "1").content(json));
		// .param("tsno", "20160805093800001").param("devid", "123")
		// .param("accessToken", token).param("authData", "123"));
		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_getSPUByCate() throws Exception {
		String groupId = "10318891";

		String json = "";

		json = "";

		ResultActions resultActions = mockMvc.perform(get(a68).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageSize", "100").param("page", "1").param("cateId", "13947")

		);
		// .param("tsno", "20160805093800001").param("devid", "123")
		// .param("accessToken", token).param("authData", "123"));
		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201901R test_import_quota_BudgetAccounts
	@Test
	public void test_import_quota_BudgetAccounts() throws Exception {
		loginId = "2929";//
		// loginId = "0278";//

		String fileName = "";
		fileName = "F:/biostime2/file/优惠券费用报表导入模板.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v2.xls";

		// fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-field-empty.xls";

		// fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-logicerror1.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v3-logicerror2.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v3.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v4-logicerror2.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-d.xls";

		String json = "";

		json = "";

		// String couponDefId = "169706";

		MockMultipartFile file = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
				Files.readAllBytes(Paths.get(fileName)));

		ResultActions resultActions = mockMvc.perform(fileUpload(a67).file(file).param("loginId", loginId)
				// .param("couponDefId", couponDefId)

				.content(json));
		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	// 201901R test_import_file_xls_read_data_OrgAllBudgetAccountsQuery

	@Test
	public void test_import_file_xls_read_data_OrgAllBudgetAccountsQuery() throws Exception {

		OrgAllBudgetAccountsQuery query = new OrgAllBudgetAccountsQuery();

		BudgetScene budgetScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(BudgetScene.class);
		JsonResult jsonResult = JsonResult.init();

		// loginId = "0289";// isarea
		String auditMonth = DateUtil.convertToAuditMonth(new Date());
		LoginUser u = UserServiceUtil.getLoginUser(loginId);
		// String officeCode = getUserOfficeCodeBySession();
		String officeCode = u.getOfficeCode();
		System.out.println("###officecode=" + officeCode);
		System.out.println("###loginid=" + u.getLoginId());

		String fileName = "";
		fileName = "";

		// fileName =
		// "F:/biostime2/file/response-CampaignDepartmentQuota-0911.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v2.xls";

		// fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-field-empty.xls";

		// fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-logicerror1.xls";

		MockMultipartFile receiveXls = new MockMultipartFile("receiveXls", "orgin-receiveXls", null,
				Files.readAllBytes(Paths.get(fileName)));

		// 校验模板
		jsonResult = ValidateServiceUtil.validateExcelTpl(receiveXls, getXlsTpl(OrgAllBudgetAccountsQuery.class));
		if (!jsonResult.isSuccess()) {
			// return jsonResult;
			System.out.println(JSON.toJSONString(jsonResult));
			return;
		}

		// read data
		// Map<String, OrgAllBudgetAccountsQuery> xlsData = null;
		List<OrgAllBudgetAccountsQuery> xlsData = null;
		try {
			xlsData = budgetScene.getImportOrgAllBudgetAccountsQueryFromXls(receiveXls,
					OrgAllBudgetAccountsQuery.class);
		} catch (Exception e) {
			if (ParamUtil.isParamNotEmpty(e.getMessage())) {
				// return ValidateServiceUtil.getResultCustom(Logic_custom, e.getMessage());
				System.out
						.println(JSON.toJSONString(ValidateServiceUtil.getResultCustom(Logic_custom, e.getMessage())));
				return;
			}
		}
		System.out.println("###read xlsdata size = " + xlsData.size());
		System.out.println(JSON.toJSONString(xlsData));

		// Pager pager = Pager.getDefaultExportPager(request);
		Pager pager = Pager.getDefaultPager();

		List<OrgAllBudgetAccountsQuery> dataFromDb = budgetScene.getOrgAllBudgetAccountsQuery(pager, query, u);

		// validate data
		try {
			budgetScene.validateImportOrgAllBudgetAccountsQueryBudgetIdIfExists(xlsData, dataFromDb);
		} catch (Exception e1) {
			if (ParamUtil.isParamNotEmpty(e1.getMessage())) {
				// return ValidateServiceUtil.getResultCustom(Logic_custom, e1.getMessage());
				System.out
						.println(JSON.toJSONString(ValidateServiceUtil.getResultCustom(Logic_custom, e1.getMessage())));
				return;
			}
		}
//
		try {
			budgetScene.validateImportOrgAllBudgetAccountsQueryBudgetIdNoBelongOrgCode(xlsData, dataFromDb);
		} catch (Exception e) {
			if (ParamUtil.isParamNotEmpty(e.getMessage())) {
				// return ValidateServiceUtil.getResultCustom(Logic_custom, e.getMessage());
				System.out
						.println(JSON.toJSONString(ValidateServiceUtil.getResultCustom(Logic_custom, e.getMessage())));
				return;
			}
		}
		//
		Map<String, Integer> orgCodeHasSumQuotaMap = xlsData.stream()
				.collect(Collectors.groupingBy(OrgAllBudgetAccountsQuery::getOrgCode,
						Collectors.summingInt(obj -> obj.getBudgetAccountQuota().intValue())));
		System.out.println(JSON.toJSONString(orgCodeHasSumQuotaMap));

		// save data CouponBudgetLimit
		orgCodeHasSumQuotaMap.forEach((orgCode, quota) -> {
			int count = budgetScene.cntExistingBudgetDepartment(auditMonth, orgCode);

			//
			CouponBudgetLimit couponBudgetLimit = new CouponBudgetLimit();
			couponBudgetLimit.setYearmonth(ParamUtil.convertParam2Integer(auditMonth));
			couponBudgetLimit.setDepartmentCode(orgCode);
			couponBudgetLimit.setDepartmentBudgetLimit(BigDecimal.valueOf(quota));
			couponBudgetLimit.setBabylineBudgetLimit(BeanUtil.getBigDecimailDefaultVal());
			couponBudgetLimit.setMedicinelineBudgetLimit(BeanUtil.getBigDecimailDefaultVal());
			couponBudgetLimit.setSupermarketBudgetLimit(BeanUtil.getBigDecimailDefaultVal());
			couponBudgetLimit.setUpdatedBy(u.getLoginId());
			couponBudgetLimit.setUpdatedTime(DateUtil.getCurrentDate());

			List<CouponBudgetLimit> couponBudgetLimitList = new ArrayList<>();
			if (count == 0) {
				// save
				//
				couponBudgetLimit.setCreatedBy(u.getLoginId());

				couponBudgetLimit.setCreatedTime(DateUtil.getCurrentDate());
				couponBudgetLimitList.add(couponBudgetLimit);
				budgetScene.saveCouponBudgetLimit(couponBudgetLimitList);
			} else {
				// update
				couponBudgetLimitList.add(couponBudgetLimit);
				budgetScene.updateCouponBudgetLimit(couponBudgetLimitList);
			}

		});
		// CouponBudgetAccount logic

		xlsData.forEach(o -> {
			int delNum = budgetScene.deleteCouponBudgetAccount(auditMonth, o.getOrgCode(), o.getBudgetAccountId());
			logger.info("###删除优惠券预算科目配额记录数：" + delNum);
			//
			// CouponBudgetAccount couponBudgetAccount = null;
			List<CouponBudgetAccount> couponBudgetAccountList = new ArrayList<>();
			/// save budget quota
			// if (!CollectionUtil.isEmptyList(importCouponBudget.getCouponBudgetInfos())) {

			// for (CouponBudgetInfo couponBudgetInfo :
			// importCouponBudget.getCouponBudgetInfos()) {

			CouponBudgetAccount couponBudgetAccount = new CouponBudgetAccount();
			couponBudgetAccount.setId(budgetScene.getKeyGc5CouponBudgetAccountKey());

			couponBudgetAccount.setYearmonth(ParamUtil.convertParam2Integer(auditMonth));
			couponBudgetAccount.setDepartmentCode(o.getOrgCode());
			couponBudgetAccount.setBudgetAccountId(o.getBudgetAccountId());
			couponBudgetAccount.setBudgetAccountQuota(o.getBudgetAccountQuota());
			couponBudgetAccountList.add(couponBudgetAccount);
			// }
			//
			System.out.println("###saveCouponBudgetAccount before save couponBudgetAccountList \n"
					+ JSON.toJSONString(couponBudgetAccountList));
			budgetScene.saveCouponBudgetAccount(couponBudgetAccountList);
			// }

		});

		// old logic
		/*
		 * Map<String, ImportCouponBudget> importCouponBudgetMap = null;
		 * Collection<ImportCouponBudget> importCouponBudgets =
		 * importCouponBudgetMap.values();
		 * 
		 * for (ImportCouponBudget importCouponBudget : importCouponBudgets) { int count
		 * = couponBudgetMgrRepository.cntExistingBudgetDepartment(importCouponBudget.
		 * getYearMonth(), importCouponBudget.getDepartmentCode());
		 * 
		 * CouponBudgetLimit couponBudgetLimit = new CouponBudgetLimit();
		 * couponBudgetLimit.setYearmonth(importCouponBudget.getYearMonth());
		 * couponBudgetLimit.setDepartmentCode(importCouponBudget.getDepartmentCode());
		 * couponBudgetLimit.setDepartmentBudgetLimit(importCouponBudget.
		 * getDepartmentBudgetLimit());
		 * couponBudgetLimit.setBabylineBudgetLimit(importCouponBudget.
		 * getBabylineBudgetLimit());
		 * couponBudgetLimit.setMedicinelineBudgetLimit(importCouponBudget.
		 * getMedicinelineBudgetLimit());
		 * couponBudgetLimit.setSupermarketBudgetLimit(importCouponBudget.
		 * getSupermarketBudgetLimit());
		 * couponBudgetLimit.setUpdatedBy(sessionUser.getSysuser().getUserName());
		 * couponBudgetLimit.setUpdatedTime(new Date());
		 * 
		 * List<CouponBudgetLimit> couponBudgetLimitList = new ArrayList<>(); if (count
		 * == 0) { // save //
		 * couponBudgetLimit.setCreatedBy(sessionUser.getSysuser().getUserName());
		 * couponBudgetLimit.setCreatedBy(u.getLoginId());
		 * 
		 * couponBudgetLimit.setCreatedTime(new Date());
		 * couponBudgetLimitList.add(couponBudgetLimit);
		 * couponBudgetMgrRepository.saveCouponBudgetLimit(couponBudgetLimitList); }
		 * else { // update couponBudgetLimitList.add(couponBudgetLimit);
		 * couponBudgetMgrRepository.updateCouponBudgetLimit(couponBudgetLimitList); }
		 * 
		 * int delNum =
		 * couponBudgetMgrRepository.deleteCouponBudgetAccount(importCouponBudget.
		 * getYearMonth(), importCouponBudget.getDepartmentCode());
		 * logger.info("删除优惠券预算科目配额记录数：" + delNum);
		 * 
		 * CouponBudgetAccount couponBudgetAccount = null; List<CouponBudgetAccount>
		 * couponBudgetAccountList = new ArrayList<>(); // save budget quota if
		 * (!CollectionUtil.isEmptyList(importCouponBudget.getCouponBudgetInfos())) {
		 * for (CouponBudgetInfo couponBudgetInfo :
		 * importCouponBudget.getCouponBudgetInfos()) { couponBudgetAccount = new
		 * CouponBudgetAccount(); couponBudgetAccount
		 * .setId(keyGenerateRepository.getKey(CouponBudgetAccount.
		 * GC5_COUPON_BUDGET_ACCOUNT_KEY));
		 * couponBudgetAccount.setYearmonth(importCouponBudget.getYearMonth());
		 * couponBudgetAccount.setDepartmentCode(importCouponBudget.getDepartmentCode())
		 * ; couponBudgetAccount.setBudgetAccountId(couponBudgetInfo.getBudgetId());
		 * couponBudgetAccount.setBudgetAccountQuota(couponBudgetInfo.getQuota());
		 * couponBudgetAccountList.add(couponBudgetAccount); } // System.out.
		 * println("###saveCouponBudgetAccount before save couponBudgetAccountList \n" +
		 * JSON.toJSONString(couponBudgetAccountList));
		 * couponBudgetMgrRepository.saveCouponBudgetAccount(couponBudgetAccountList); }
		 * }
		 */

	}

	// 201901R test_export_all_org_BudgetAccountsQuery
	@Test
	public void test_export_all_org_BudgetAccountsQuery() throws Exception {
		// loginId = "0289";
		String fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v2.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v3.xls";
		fileName = "F:/biostime2/file/优惠券费用报表导入模板-2019-debug-v5.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a66).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")
				/*
				 * .param("budgetName", "项目").param("budgetStatus", "1") .param("channelCode",
				 * "01").param("departmentCode", "1")
				 */

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		// System.out.println("res deric: " +
		// resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_export_BudgetAccountsQuery() throws Exception {
		// loginId = "0289";
		String fileName = "F:/biostime2/file/response-BudgetAccountsQuery.xls";

		ResultActions resultActions = mockMvc.perform(get(a65).contentType(MediaType.APPLICATION_JSON)
				.param("loginId", loginId).param("pageNo", "1").param("budgetName", "项目").param("budgetStatus", "1")
				.param("channelCode", "01").param("departmentCode", "1")

		);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_DepartmentOrganizationBudgetAccounts_export() {

		Pager p = new Pager();
		p.setPageNum(1);
		p.setPageSize(Pager.defaultExportPageSize);
		BudgetAccountsQuery query = new BudgetAccountsQuery();

		// query.setAuditMonth("201812");
		query.setBudgetStatus(Budget_Accounts_status_enable.getCode());
		// query.setDepartmentCode(EnumUtil.getKeyByDesc(Budget_Accounts_department_headquarter.getDesc()));

		//
		String prefix = "Budget_Accounts_department";
		List<String> ls = EnumUtil.getAllValueByEnumPrefix(prefix);
		// System.out.println(ParamUtil.convertPararmList2StringByDelim(ls));
		query.setDepartmentCode(ParamUtil.convertPararmList2StringByDelim(ls));

		// query.setChannelCode(EnumUtil.getKeyByDesc(Budget_Accounts_channel_baby.getDesc()));
		// query.setBudgetName("预算名");

		System.out.println(JSON.toJSONString(query));
		LoginUser u = getTestLoginUser("01");

		BudgetScene budgetScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(BudgetScene.class);

		List<BudgetAccountsQuery> budgetAccountsList = budgetScene.getBudgetAccountsQuery(p, query, u);
		System.out.println("budgetAccountsList size=" + budgetAccountsList.size());
		System.out.println(JSON.toJSONString(budgetAccountsList));
		//
		// HashMap

		CacheService c = BiostimeWebApplicationContextHolder.getApplicationContext().getBean(CacheService.class);
		List<DepartmentOrganization> orgAll = c.getDepartmentOrganizationAll();

		System.out.println(JSON.toJSONString(orgAll));
		//
		List<DepartmentOrganizationBudgetAccounts> orgBudgetAccounts = new ArrayList();
		List<BudgetAccountsQuery> basq = null;
		for (DepartmentOrganization org : orgAll) {
			DepartmentOrganizationBudgetAccounts orgObj = new DepartmentOrganizationBudgetAccounts();

			orgObj.setOrg(org);
			basq = budgetAccountsList.stream().filter(bas -> {
				boolean b = false;
				if (DepartmentServiceUtil.isHeadQuarter(org.getOrgCode())) {
					return b = budgetScene.isHeadQuarter(bas.getBudgetDepartmentCode());
				}
				if (DepartmentServiceUtil.isOrgArea(org.getOrgCode())) {
					return b = budgetScene.isArea(bas.getBudgetDepartmentCode());
				}
				if (DepartmentServiceUtil.isOrgOffice(org.getOrgCode())) {
					return b = budgetScene.isOffice(bas.getBudgetDepartmentCode());
				}
				System.out.println("b false org=" + org.getOrgCode());
				return b;
			}).collect(Collectors.toList());
			System.out.println("org=" + org.getOrgCode());
			System.out.println("basq\n" + JSON.toJSONString(basq));

			orgObj.setBudgetAccounts(basq);

			orgBudgetAccounts.add(orgObj);
			basq = null;

		}
		System.out.println(JSON.toJSONString(orgBudgetAccounts));

	}

	@Test
	public void test_DepartmentOrganizationBudgetAccounts() {

		Pager p = new Pager();
		p.setPageNum(1);
		p.setPageSize(Pager.defaultExportPageSize);
		BudgetAccountsQuery query = new BudgetAccountsQuery();

		// query.setAuditMonth("201812");
		query.setBudgetStatus(Budget_Accounts_status_enable.getCode());
		// query.setDepartmentCode(EnumUtil.getKeyByDesc(Budget_Accounts_department_headquarter.getDesc()));

		//
		String prefix = "Budget_Accounts_department";
		List<String> ls = EnumUtil.getAllValueByEnumPrefix(prefix);
		// System.out.println(ParamUtil.convertPararmList2StringByDelim(ls));
		query.setDepartmentCode(ParamUtil.convertPararmList2StringByDelim(ls));

		// query.setChannelCode(EnumUtil.getKeyByDesc(Budget_Accounts_channel_baby.getDesc()));
		// query.setBudgetName("预算名");

		System.out.println(JSON.toJSONString(query));
		LoginUser u = getTestLoginUser("01");

		BudgetScene budgetScene = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(BudgetScene.class);

		List<BudgetAccountsQuery> budgetAccountsList = budgetScene.getBudgetAccountsQuery(p, query, u);
		System.out.println("budgetAccountsList size=" + budgetAccountsList.size());
		System.out.println(JSON.toJSONString(budgetAccountsList));
		//
		// HashMap

		CacheService c = BiostimeWebApplicationContextHolder.getApplicationContext().getBean(CacheService.class);
		List<DepartmentOrganization> orgAll = c.getDepartmentOrganizationAll();

		System.out.println(JSON.toJSONString(orgAll));
		//
		List<DepartmentOrganizationBudgetAccounts> orgBudgetAccounts = new ArrayList();
		List<BudgetAccountsQuery> basq = null;
		for (DepartmentOrganization org : orgAll) {
			DepartmentOrganizationBudgetAccounts orgObj = new DepartmentOrganizationBudgetAccounts();

			orgObj.setOrg(org);
			basq = budgetAccountsList.stream().filter(bas -> {
				boolean b = false;
				if (DepartmentServiceUtil.isHeadQuarter(org.getOrgCode())) {
					return b = budgetScene.isHeadQuarter(bas.getBudgetDepartmentCode());
				}
				if (DepartmentServiceUtil.isOrgArea(org.getOrgCode())) {
					return b = budgetScene.isArea(bas.getBudgetDepartmentCode());
				}
				if (DepartmentServiceUtil.isOrgOffice(org.getOrgCode())) {
					return b = budgetScene.isOffice(bas.getBudgetDepartmentCode());
				}
				System.out.println("b false org=" + org.getOrgCode());
				return b;
			}).collect(Collectors.toList());
			System.out.println("org=" + org.getOrgCode());
			System.out.println("basq\n" + JSON.toJSONString(basq));

			orgObj.setBudgetAccounts(basq);

			orgBudgetAccounts.add(orgObj);
			basq = null;

		}
		System.out.println(JSON.toJSONString(orgBudgetAccounts));

	}

	@Test
	public void test_getBudgetAccountsQuery() {

		Pager p = new Pager();
		p.setPageNum(1);
		p.setPageSize(Pager.defaultExportPageSize);
		BudgetAccountsQuery query = new BudgetAccountsQuery();

		// query.setAuditMonth("201812");
		query.setBudgetStatus(Budget_Accounts_status_enable.getCode());
		// query.setDepartmentCode(EnumUtil.getKeyByDesc(Budget_Accounts_department_headquarter.getDesc()));

		//
		String prefix = "Budget_Accounts_department";
		List<String> ls = EnumUtil.getAllValueByEnumPrefix(prefix);
		// System.out.println(ParamUtil.convertPararmList2StringByDelim(ls));
		query.setDepartmentCode(ParamUtil.convertPararmList2StringByDelim(ls));

		query.setChannelCode(EnumUtil.getKeyByDesc(Budget_Accounts_channel_baby.getDesc()));
		query.setBudgetName("预算名");

		System.out.println(JSON.toJSONString(query));
		LoginUser u = getTestLoginUser("01");

		BudgetScene q = BiostimeWebApplicationContextHolder.getApplicationContext().getBean(BudgetScene.class);

		q.getBudgetAccountsQuery(p, query, u);

	}

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

		fileName = "F:/biostime2/file/couponOfficeQuota-debug-v2.xls";
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

		// 201901R test_cfg_update
		loginId = "1366";// area 0102
		json = "{\"couponTitle\":\"购买满100元立减30元\",\"couponName\":\" \",\"couponRemark\":\"d-test-area-090116\",\"couponDefId\":\"10325728\",\"couponType\":5,\"campaignId\":20012299,\"campaignGroupId\":10318913,\"couponStartDate\":\"2019-01-16\",\"couponEndDate\":\"2019-02-28\",\"amountPerCustomer\":1,\"maxCost\":22.64,\"maxAmount\":1000,\"couponBudgetInfos\":[{\"budgetId\":161,\"ration\":1}],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\"}";

		// 201902R jira
		loginId = "2929";
		json = "{\"couponTitle\":\"买益生菌买6加100块送1\",\"couponName\":\"益生菌买6加100块送1--生日礼遇专用\",\"couponRemark\":\"益生菌买6加100块送1\",\"couponDefId\":\"10325859\",\"couponType\":2,\"campaignId\":20012430,\"campaignGroupId\":10318912,\"couponStartDate\":\"2019-03-14\",\"couponEndDate\":\"2019-03-31\",\"amountPerCustomer\":1,\"maxCost\":118.58,\"maxAmount\":70,\"couponBudgetInfos\":[{\"ration\":1}],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"2\"}";

		json = "{\"couponTitle\":\"买益生菌买6加100块送1\",\"couponName\":\"益生菌买6加100块送1--生日礼遇专用\",\"couponRemark\":\"益生菌买6加100块送1\",\"couponDefId\":\"10325859\",\"couponType\":2,\"campaignId\":20012430,\"campaignGroupId\":10318912,\"couponStartDate\":\"2019-03-14\",\"couponEndDate\":\"2019-03-31\",\"amountPerCustomer\":1,\"maxCost\":118.58,\"maxAmount\":70,\"couponBudgetInfos\":[{\"budgetId\":262,\"ration\":1}],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"2\"}";
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
	public void test_export_BudgetAccounts_debug() throws Exception {
		// loginId =
		// "0289";http://test-01.biostime.us/biostime-coupon-web/b/coupon/finance/budget/export/report/BudgetAccounts.action?loginId=2929
		// &auditMonth=201902&departmentCode=0113&pageNo=1&pageSize=9999
		String fileName = "F:/biostime2/file/response-BudgetAccounts.xls";
		fileName = "F:/biostime2/file/response-BudgetAccounts-debug.xls";

		ResultActions resultActions = mockMvc
				.perform(get(a64).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")
						.param("auditMonth", "201902").param("departmentCode", "0113").param("pageSize", "9999")

				);
		Files.write(Paths.get(fileName), resultActions.andReturn().getResponse().getContentAsByteArray());

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

		// 201901R test_q_BudgetAccounts
		loginId = "1366";// area 0102
		loginId = "3648";// office 010201

		json = "{\"auditMonth\":\"201901\",\"pageNo\":1,\"pageSize\":10}";

		loginId = "2929";// office 010201
		json = "{\"auditMonth\":\"201902\",\"pageNo\":1,\"pageSize\":10,\"departmentCode\":\"010201\"}";

		// debug-20190216
		json = " {\"auditMonth\":\"201902\",\"departmentCode\":\"01\",\"pageNo\":1,\"pageSize\":10}";
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

		// 201902R json =
		// "{\"departmentCode\":\"01\",\"yearmonth\":\"201812\",\"pageNo\":1,\"pageSize\":10}";
		json = "{\"departmentCode\":\"01\",\"yearmonth\":\"201903\",\"pageNo\":1,\"pageSize\":10}";

		ResultActions resultActions = mockMvc.perform(
				post(a47).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId).param("pageNo", "1")

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Deprecated
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

		// debug-090107
		json = "{\"budgetAccountId\":175}";

		// 201901R test_budget_getBalanceOfCurrentMonth_v2
		loginId = "1366";// isarea 0102
		json = "{\"budgetAccountId\":161}";

		loginId = "3648";// isoffice 010201
		json = "{\"budgetAccountId\":144}";
		/*
		 * String budgetAccountId = "121";
		 * 
		 * budgetAccountId = "41";
		 */

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
		json = "{\"id\":41}";

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
	public void test_ReadOnlyRepository() throws Exception {

		ReadOnlyRepository c = BiostimeWebApplicationContextHolder.getApplicationContext()
				.getBean(ReadOnlyRepository.class);
		c.testReadonly();
	}

	// 201901R 增加只读数据库 读名单
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

		// 201904R q tpl

		json = "{\"status\":\"1\",\"id\":\"\",\"couponTypes\":\"\",\"customerType\":\"0\",\"productCategory\":\"0\",\"groupType\":\"\",\"id\":166}";

		json = "{\"status\":\"1\",\"id\":166,\"couponTypes\":\"2\",\"groupType\":\"\"}";

		// jira 20190507
		json = "{\"status\":\"1\",\"id\":\"\",\"couponTypes\":\"\",\"customerType\":\"\",\"productCategory\":\"\",\"groupType\":\"\"}";
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

//		key = "Coupon_Cfg_Tpl_productCategory_";
//
//		String json = "";
//
//		key = "Prop_brands_";
//
//		key = "Prop_coupon_purpose_";
//
//		key = "Prop_abnormal_user_";
//
//		key = "Coupon_Cfg_Tpl_customerType_";

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
		/*
		 * String brand_swisse =
		 * ParamUtil.convertInteger2Str(com.mama100.merchandise.enums.BrandId.SWISSE.
		 * getId());// "26509"; List<Long> skus = new ArrayList<Long>(); skus.add(4l);
		 */

		// List<SKU> products = ProductServiceUtil.getProductByBrandId(brand_swisse,
		// skus);
		/*
		 * List<SKU> products = ProductServiceUtil.getProductByBrandId(brand_swisse,
		 * null); System.out.println(JSON.toJSONString(products));
		 */

		//
		/*
		 * String brand_biostime =
		 * ParamUtil.convertInteger2Str(com.mama100.merchandise.enums.BrandId.BIOSTIME.
		 * getId());// "1"; List<SKU> products_b =
		 * ProductServiceUtil.getProductByBrandId(brand_biostime, null);
		 * System.out.println(JSON.toJSONString(products_b));
		 */
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

		fileName = "F:/biostime2/file/skudemo-debug-20190107.txt";
		String json = "";

		json = "";

		String brands = "1,4,5,6";
		brands = "4";

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

		// loginId = "12580";
		String json = "";

		json = "{}";

		// 201902R test 重构 创券场景的 查询登录者的预算科目
		ResultActions resultActions = mockMvc.perform(post(a56_201812R).contentType(MediaType.APPLICATION_JSON)
				.param("pageNo", "1").param("pageSize", "70000").param("loginId", loginId).content(json)

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
	public void test_importCouponBudget() throws Exception {
		loginId = "2929";//
		// loginId = "0278";//

		String fileName = "";
		fileName = "F:/biostime2/file/优惠券费用报表导入模板.xls";

		fileName = "F:/biostime2/file/优惠券费用报表导入模板-20181203.xls";

		// debug 20190110
		fileName = "F:/biostime2/file/（总部）优惠券费用报表导入模板1901-debug.xls";
		// fileName = "F:/biostime2/file/（总部）优惠券费用报表导入模板1901-debug-2.xls";
		fileName = "F:/biostime2/file/（总部）优惠券费用报表导入模板1901-debug-3.xls";

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
	public void test_create_BudgetAccounts_R() throws Exception {

		String json = "";

		// 201901R test_create_BudgetAccounts
		json = "{\"budgetName\":\"d-l-test-20190111\",\"channelCode\":\"01,02,03,08,13\",\"departmentCode\":\"1\"}";

		// debug
		json = "{\"departmentCode\":\"1\",\"channelCode\":\"01,02,03,08,13\",\"budgetName\":\"d-test-20190216\"}";
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
	public void test_budgetAccountQuery_detail() throws Exception {

		loginId = "2929";

		String json = "";

		// 201901R 查指定预算科目的配额
		json = "{\"budgetQuotaDepartmentCode\":\"010201\",\"id\":144}";

		ResultActions resultActions = mockMvc
				.perform(post(a48_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.param("pageNo", "1").param("pageSize", "20")

						.content(json));

		System.out.println("res deric: " + resultActions.andReturn().getResponse().getContentAsString());

	}

	@Test
	public void test_budgetAccountQuery() throws Exception {

		loginId = "2929";

		String json = "";

		// 201901R test_budgetAccountQuery
		json = "{\"budgetName\":\"项目\",\"budgetStatus\":1,\"channelCode\":\"01\",\"departmentCode\":\"1\"}";

		// debug-20190216
		json = "{\"departmentCode\":\"1\",\"channelCode\":\"\",\"budgetStatus\":\"\",\"budgetName\":\"\",\"pageNo\":1,\"pageSize\":20}";

		json = "{\"departmentCode\":\"1\",\"channelCode\":\"\",\"budgetStatus\":\"\",\"budgetName\":\"odie\",\"pageNo\":1,\"pageSize\":20}";

		ResultActions resultActions = mockMvc
				.perform(post(a48_201812R).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)

						.param("pageNo", "1").param("pageSize", "20")

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

		loginId = "12580";
		json = "{\"campaignId\":\"671062\",\"pageNo\":1,\"pageSize\":10}";

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

		// debug-090107
		loginId = "0022";
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"0022\",\"campaignPM\":\"0022\",\"description\":\"123\",\"productScope\":1,\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"groupName\":\"立减金额\",\"costPoint\":0,\"costMoney\":0,\"discount\":11,\"giftAmount\":0,\"presentType\":3,\"giftProducts\":[],\"ration\":0,\"discountPoint\":0}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满123元立减11元\",\"couponName\":\"11\",\"couponRemark\":\"11\",\"publishType\":2,\"couponType\":5,\"aprice\":123,\"bprice\":0,\"couponStartDate\":\"2019-01-07\",\"couponEndDate\":\"2019-01-07\",\"productionRatio\":\"0.98%\",\"couponBudgetInfos\":[{\"budgetId\":161,\"ration\":1}],\"settleRatio\":\"11\",\"campaignScope\":2,\"areaCodes\":[\"0106\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":\"1\",\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":11,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"excludedFlag\":1,\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"0.93\",\"specifiedPrice\":0,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"undefined\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":185}";

		// 201901R test_create_13
		loginId = "1366";// area 0102
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-area-090116\",\"campaignPM\":\"1366\",\"description\":\"d-test-area-090116\",\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":304}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"30\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满100元立减30元\",\"couponName\":\"\",\"couponRemark\":\"d-test-area-090116\",\"publishType\":2,\"couponType\":5,\"aprice\":100,\"bprice\":0,\"couponStartDate\":\"2019-01-16\",\"couponEndDate\":\"2019-02-28\",\"productionRatio\":\"29.40%\",\"couponBudgetInfos\":[{\"budgetId\":161,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":2,\"areaCodes\":[\"0102\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1000,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318913\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"22.64\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-area-090116\",\"campaignPM\":\"1366\",\"description\":\"d-test-area-090116\",\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":304}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"30\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满100元立减30元\",\"couponName\":\"\",\"couponRemark\":\"d-test-area-090116\",\"publishType\":2,\"couponType\":5,\"aprice\":100,\"bprice\":0,\"couponStartDate\":\"2019-01-16\",\"couponEndDate\":\"2019-02-28\",\"productionRatio\":\"29.40%\",\"couponBudgetInfos\":[{\"budgetId\":161,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":2,\"areaCodes\":[\"0102\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318913\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"22.64\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		/*
		 * loginId = "3648";// office 010201 json =
		 * "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-office-090116\",\"campaignPM\":\"3648\",\"description\":\"d-test-office-090116\",\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":304}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"30\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满100元立减30元\",\"couponName\":\"\",\"couponRemark\":\"d-test-office-090116\",\"publishType\":2,\"couponType\":5,\"aprice\":100,\"bprice\":0,\"couponStartDate\":\"2019-01-16\",\"couponEndDate\":\"2019-02-28\",\"productionRatio\":\"29.40%\",\"couponBudgetInfos\":[{\"budgetId\":144,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":2,\"areaCodes\":[\"0102\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1000,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"22.64\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";
		 */
		// debug-0129
		loginId = "2929";
		json = "{\"amountPerCustomer\":2,\"anum\":6,\"applySystemCodes\":\"2\",\"aprice\":0,\"aproducts\":[{\"isMandatory\":0,\"sku\":261908},{\"isMandatory\":0,\"sku\":266407},{\"isMandatory\":0,\"sku\":261308},{\"isMandatory\":0,\"sku\":3084},{\"isMandatory\":0,\"sku\":261907},{\"isMandatory\":0,\"sku\":266507},{\"isMandatory\":0,\"sku\":261407},{\"isMandatory\":0,\"sku\":3083},{\"isMandatory\":0,\"sku\":262007},{\"isMandatory\":0,\"sku\":266508},{\"isMandatory\":0,\"sku\":261307},{\"isMandatory\":0,\"sku\":3082}],\"areaCodes\":[],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"bnum\":0,\"bprice\":0,\"bproducts\":[],\"brandItem\":\"\",\"campaignGroupClass\":0,\"campaignGroupId\":\"\",\"campaignGroupName\":\"2月商超活动（1.26-2.25)\",\"campaignGroupType\":1,\"campaignPM\":\"14496\",\"campaignScope\":3,\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"couponBudgetInfos\":[{\"budgetId\":183,\"ration\":1}],\"couponDeadline\":0,\"couponEndDate\":\"2019-02-25\",\"couponName\":\"HT奶粉买6送1\",\"couponPlatforms\":\"-1\",\"couponRemark\":\"HT奶粉买6送1\",\"couponStartDate\":\"2019-01-28\",\"couponTitle\":\"HT奶粉买6送1\",\"couponType\":2,\"customerFileTypes\":[],\"customerIdTimeStamp\":\"\",\"customerPhoneTimeStamp\":\"\",\"customerType\":-1,\"description\":\"2月商超活动（1.26-2.25)\",\"excludedFlag\":0,\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftAmount\":1,\"giftProducts\":[{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光幼儿配方奶粉800G /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261908},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光有机乐活礼包（爱斯时光幼儿配方奶粉800G） /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":266407},{\"amount\":1,\"giftName\":\"Healthy Times  有机乐活礼包（幼儿配方奶粉800G） /礼包\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261308},{\"amount\":1,\"giftName\":\"HT有机幼儿配方奶粉 800克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":3084},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光较大婴儿配方奶粉800G /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261907},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光有机乐活礼包（爱斯时光较大婴儿配方奶粉800G） /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":266507},{\"amount\":1,\"giftName\":\"Healthy Times 有机乐活礼包（较大婴儿配方奶粉800G） /礼包\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261407},{\"amount\":1,\"giftName\":\"HT有机较大婴儿配方奶粉 800克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":3083},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光婴儿配方奶粉800G /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":262007},{\"amount\":1,\"giftName\":\"Healthy Times爱斯 时光有机乐活礼包（爱斯时光婴儿配方奶粉800G） /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":266508},{\"amount\":1,\"giftName\":\"Healthy Times有机乐活礼包（婴儿配方奶粉800G） /礼包\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261307},{\"amount\":1,\"giftName\":\"HT有机婴儿配方奶粉 800克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":3082}],\"groupName\":\"商品领取\",\"presentType\":1,\"ration\":0}],\"giftPoint\":false,\"giftRecycleTerminalPoint\":false,\"isAllowBusinessPublish\":0,\"isAppointP\":0,\"isSameProduct\":false,\"isSameSeries\":false,\"isTerminalImport\":0,\"isTerminalQuota\":0,\"marketAct\":false,\"maxAmount\":30,\"maxCost\":177.1,\"memberListId\":[],\"orderPoint\":false,\"platformChannels\":\"-1\",\"productItems\":\"\",\"productPoint\":false,\"productScope\":0,\"productionRatio\":\"8.26%\",\"prop\":{\"abnormalUser\":\"\",\"autoPublishedCfg\":false,\"autoPublishedStatus\":0,\"biostime\":false,\"biostimeCampaign\":false,\"coupon_purpose\":\"1\",\"custPay\":\"0\",\"deductTerminalGiftPoint\":\"0\",\"excludedAbnormalUser\":\"0\",\"isSpecifiedCustomersSrc\":0,\"mama100\":false,\"marketAct\":false,\"newCustomerStorehouse\":\"0\",\"orderPoint\":false,\"refresh\":false,\"specifiedCampaignCustomers\":\"\",\"specifiedCustomers\":false,\"specifiedMobiles\":false,\"updateValidityEndDate\":false},\"publishType\":3,\"publishWay\":2,\"publisher\":0,\"recycleTerminalPoint\":false,\"sameProduct\":false,\"sameSeries\":false,\"settleRatio\":100,\"skuItems\":\"\",\"sourceSystem\":\"PC\",\"specifiedPrice\":\"0\",\"templateName\":\"买赠券\",\"themeId\":\"101\",\"totalExpense\":0,\"validTerminals\":[\"20675\"],\"whatGive\":\"product\"}";
		// debug 20190130 aproducts 262007 repeat
		json = "{\"amountPerCustomer\":1,\"anum\":1,\"applySystemCodes\":\"2\",\"aprice\":0,\"aproducts\":[{\"isMandatory\":0,\"sku\":105},{\"isMandatory\":0,\"sku\":177},{\"isMandatory\":0,\"sku\":176},{\"isMandatory\":0,\"sku\":104},{\"isMandatory\":0,\"sku\":355},{\"isMandatory\":0,\"sku\":354},{\"isMandatory\":0,\"sku\":185},{\"isMandatory\":0,\"sku\":186},{\"isMandatory\":0,\"sku\":187},{\"isMandatory\":0,\"sku\":279},{\"isMandatory\":0,\"sku\":3299},{\"isMandatory\":0,\"sku\":100},{\"isMandatory\":0,\"sku\":101},{\"isMandatory\":0,\"sku\":296},{\"isMandatory\":0,\"sku\":99},{\"isMandatory\":0,\"sku\":163},{\"isMandatory\":0,\"sku\":103},{\"isMandatory\":0,\"sku\":102},{\"isMandatory\":0,\"sku\":302},{\"isMandatory\":0,\"sku\":253408},{\"isMandatory\":0,\"sku\":253309},{\"isMandatory\":0,\"sku\":253409},{\"isMandatory\":0,\"sku\":253407},{\"isMandatory\":0,\"sku\":253413},{\"isMandatory\":0,\"sku\":253412},{\"isMandatory\":0,\"sku\":253411},{\"isMandatory\":0,\"sku\":253410},{\"isMandatory\":0,\"sku\":253614},{\"isMandatory\":0,\"sku\":253414},{\"isMandatory\":0,\"sku\":253415},{\"isMandatory\":0,\"sku\":253312},{\"isMandatory\":0,\"sku\":262007},{\"isMandatory\":0,\"sku\":266508},{\"isMandatory\":0,\"sku\":3085},{\"isMandatory\":0,\"sku\":261307},{\"isMandatory\":0,\"sku\":3082},{\"isMandatory\":0,\"sku\":261908},{\"isMandatory\":0,\"sku\":266407},{\"isMandatory\":0,\"sku\":261308},{\"isMandatory\":0,\"sku\":3084},{\"isMandatory\":0,\"sku\":261907},{\"isMandatory\":0,\"sku\":266507},{\"isMandatory\":0,\"sku\":261407},{\"isMandatory\":0,\"sku\":3083},{\"isMandatory\":0,\"sku\":261307},{\"isMandatory\":0,\"sku\":3082},{\"isMandatory\":0,\"sku\":266508},{\"isMandatory\":0,\"sku\":262007}],\"areaCodes\":[],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"bnum\":0,\"bprice\":0,\"bproducts\":[],\"campaignGroupClass\":0,\"campaignGroupId\":\"\",\"campaignGroupName\":\"六盘水2月常规活动\",\"campaignGroupType\":1,\"campaignPM\":\"12900\",\"campaignScope\":3,\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"couponDeadline\":1,\"couponEndDate\":\"2019-02-25\",\"couponName\":\"新客购大听送400g\",\"couponPlatforms\":\"-1\",\"couponRemark\":\"新客购大听送400g，所购买的产品及赠品均不再享受合生元 积分\",\"couponStartDate\":\"2019-01-28\",\"couponTitle\":\"新客购大听送400g\",\"couponType\":2,\"customerFileTypes\":[],\"customerIdTimeStamp\":\"\",\"customerPhoneTimeStamp\":\"\",\"customerType\":4,\"description\":\"六盘水2月常规活动，所购买的产品不再享受合生元积分\",\"excludedFlag\":0,\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftAmount\":1,\"giftProducts\":[{\"amount\":1,\"giftName\":\"合生元金装较大婴儿配方奶粉 400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":446},{\"amount\":1,\"giftName\":\"合生元金装婴儿配方奶粉400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":276},{\"amount\":1,\"giftName\":\"合生元超级金装婴儿配方奶粉400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":127},{\"amount\":1,\"giftName\":\"合生元超级金装较大婴儿配方奶粉400克 (400g)2 阶段\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":436},{\"amount\":1,\"giftName\":\"合生元阿尔法星较大婴儿配方奶粉400克 /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":267208},{\"amount\":1,\"giftName\":\"合生元阿尔法星婴儿配方奶粉400克 /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":267207},{\"amount\":1,\"giftName\":\"合生元贝塔星较大婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253311},{\"amount\":1,\"giftName\":\"合生元贝塔星婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253310},{\"amount\":1,\"giftName\":\"合生元派星较大婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253711},{\"amount\":1,\"giftName\":\"合生元派星婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253615},{\"amount\":1,\"giftName\":\"HT有机婴儿配方奶粉 400g\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":3085},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光婴儿配方奶粉400G /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261607}],\"groupName\":\"商品领取\",\"presentType\":1,\"ration\":0}],\"giftPoint\":false,\"giftRecycleTerminalPoint\":false,\"isAllowBusinessPublish\":0,\"isAppointP\":0,\"isSameProduct\":false,\"isSameSeries\":false,\"isTerminalImport\":0,\"isTerminalQuota\":0,\"marketAct\":false,\"maxAmount\":100,\"maxCost\":137.06,\"memberListId\":[],\"orderPoint\":false,\"platformChannels\":\"-1\",\"productItems\":\"203\",\"productPoint\":false,\"productScope\":0,\"productionRatio\":\"2.62% ~ 44.95%\",\"prop\":{\"abnormalUser\":\"\",\"autoPublishedCfg\":false,\"autoPublishedStatus\":0,\"biostime\":false,\"biostimeCampaign\":false,\"coupon_purpose\":\"1\",\"custPay\":\"0\",\"deductTerminalGiftPoint\":\"0\",\"excludedAbnormalUser\":\"0\",\"isSpecifiedCustomersSrc\":0,\"mama100\":false,\"marketAct\":false,\"newCustomerStorehouse\":\"0\",\"orderPoint\":false,\"refresh\":false,\"specifiedCampaignCustomers\":\"\",\"specifiedCustomers\":false,\"specifiedMobiles\":false,\"updateValidityEndDate\":false},\"publishType\":3,\"publishWay\":2,\"publisher\":0,\"recycleTerminalPoint\":false,\"sameProduct\":false,\"sameSeries\":false,\"settleRatio\":100,\"skuItems\":\"\",\"sourceSystem\":\"PC\",\"specifiedPrice\":\"0\",\"templateName\":\"买赠券\",\"themeId\":\"101\",\"totalExpense\":0,\"validTerminals\":[\"14311\",\"1348517\",\"1350841\",\"1350843\",\"1325890\",\"1349844\",\"1344934\",\"1325886\",\"1342194\",\"1336545\",\"168122\",\"102203\",\"168123\",\"120252\",\"170267\",\"10513\",\"174156\",\"181552\",\"1343201\",\"17111\",\"176108\",\"112970\"],\"whatGive\":\"product\"}";

		// 201904R test create
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"deric-test-20190404\",\"campaignPM\":\"2929\",\"description\":\"deric-test-20190404\",\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":241755},{\"isMandatory\":0,\"sku\":241455},{\"isMandatory\":0,\"sku\":102},{\"isMandatory\":0,\"sku\":240853},{\"isMandatory\":0,\"sku\":163},{\"isMandatory\":0,\"sku\":239352},{\"isMandatory\":0,\"sku\":236754},{\"isMandatory\":0,\"sku\":235555},{\"isMandatory\":0,\"sku\":233052},{\"isMandatory\":0,\"sku\":239252},{\"isMandatory\":0,\"sku\":239156},{\"isMandatory\":0,\"sku\":103},{\"isMandatory\":0,\"sku\":237353},{\"isMandatory\":0,\"sku\":237352},{\"isMandatory\":0,\"sku\":237252},{\"isMandatory\":0,\"sku\":237253},{\"isMandatory\":0,\"sku\":236953},{\"isMandatory\":0,\"sku\":127},{\"isMandatory\":0,\"sku\":302},{\"isMandatory\":0,\"sku\":436}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"deric-test-20190404\",\"publishType\":\"4\",\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2019-04-04\",\"couponEndDate\":\"2019-05-04\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":221,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":5,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";
		loginId = "777771";
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-10593\",\"campaignPM\":\"10593\",\"description\":\"d-test-10593\",\"totalExpense\":0,\"productScope\":\"2\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"ddd\",\"publishType\":\"4\",\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2019-04-25\",\"couponEndDate\":\"2019-04-25\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":221,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318937\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		// 201904R jira prd
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0426\",\"campaignPM\":\"2929\",\"description\":\"d-test-0426\",\"productScope\":\"\",\"anum\":0,\"aproducts\":[{\"isMandatory\":0,\"sku\":240257}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftProducts\":[{\"amount\":\"1\",\"giftName\":\"aaaa\",\"isMandatory\":1,\"sku\":\"\",\"giftPoint\":\"20\",\"giftPrice\":\"\",\"giftPriceRatio\":0.49,\"isDeliveryAddress\":\"0\"}],\"giftAmount\":\"1\",\"groupName\":\"礼品领取\",\"presentType\":2}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"收分赠品券\",\"couponTitle\":\"买商品送aaaa（价值20积分）\",\"couponName\":\"\",\"couponRemark\":\"dddd\",\"publishType\":\"3\",\"couponType\":12,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2019-04-26\",\"couponEndDate\":\"2019-04-30\",\"productionRatio\":\"-5.42%\",\"couponBudgetInfos\":[{\"budgetId\":282,\"ration\":1}],\"settleRatio\":100,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":2,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT\",\"applySystemCodes\":\"2\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		// 201904R jira2 prd
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"0505\",\"campaignPM\":\"12580\",\"description\":\"111\",\"totalExpense\":0,\"productScope\":\"0\",\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"sku\":238452}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满10000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"ddd\",\"publishType\":\"2\",\"couponType\":5,\"aprice\":10000,\"bprice\":0,\"couponStartDate\":\"2019-05-07\",\"couponEndDate\":\"2019-05-31\",\"productionRatio\":\"-0.59%\",\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"settleRatio\":\"100\",\"campaignScope\":3,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":0,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":1,\"campaignGroupId\":\"10318944\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":1,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		// 201904R jira debug
		json = "{\"apoint\":0,\"campaignGroupId\":10318935,\"campaignGroupType\":1,\"campaignGroupName\":\"deric-test-20190404\",\"campaignPM\":\"2929\",\"description\":\"deric-test-20190404\",\"productScope\":0,\"anum\":4,\"aproducts\":[{\"isMandatory\":0,\"spu\":null,\"sku\":133},{\"isMandatory\":0,\"spu\":null,\"sku\":131},{\"isMandatory\":0,\"spu\":null,\"sku\":445},{\"isMandatory\":0,\"spu\":null,\"sku\":233052},{\"isMandatory\":0,\"spu\":null,\"sku\":444},{\"isMandatory\":0,\"spu\":null,\"sku\":443}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"groupName\":\"商品领取\",\"costPoint\":0,\"costMoney\":0,\"discount\":0,\"giftAmount\":0,\"presentType\":1,\"giftProducts\":[{\"amount\":1,\"giftName\":\"合生元水果粉\",\"sku\":133,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0},{\"amount\":1,\"giftName\":\"合生元婴幼儿胡萝卜大米粉 300g\",\"sku\":444,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0},{\"amount\":1,\"giftName\":\"合生元蔬菜粉\",\"sku\":131,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0},{\"amount\":1,\"giftName\":\"合生元婴幼儿菠菜大米粉 300g\",\"sku\":443,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0},{\"amount\":1,\"giftName\":\"合生元较大婴儿配方液态奶12492-oooo 12瓶/盒\",\"sku\":233052,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0},{\"amount\":1,\"giftName\":\"合生元婴幼儿番茄大米粉 300g\",\"sku\":445,\"spu\":null,\"giftPrice\":0,\"giftPoint\":null,\"giftPriceRatio\":0,\"isDeliveryAddress\":0}],\"ration\":0,\"discountPoint\":0}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"买赠券\",\"couponTitle\":\"买商品送同品\",\"couponName\":\" \",\"couponRemark\":\"111\",\"publishType\":3,\"couponType\":2,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2019-05-07\",\"couponEndDate\":\"2019-05-31\",\"productionRatio\":\"17.99% ~ 20.00%\",\"couponBudgetInfos\":[{\"budgetId\":221,\"ration\":1}],\"settleRatio\":100,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":-1,\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"value\":\"\"}],\"coupon633Type\":[],\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"srcCouponDefId\":10326015,\"productItems\":null,\"brandItem\":null,\"skuItems\":null,\"customerFileTypes\":[],\"isTerminalImport\":0,\"isSameProduct\":1,\"isSameSeries\":0,\"maxCost\":214.06,\"specifiedPrice\":\"0\",\"isTerminalQuota\":0,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"2\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":null,\"abnormalUser\":null,\"custPay\":\"0\",\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":\"1\",\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"d-test-0426\",\"campaignPM\":\"2929\",\"description\":\"d-test-0426\",\"totalExpense\":0,\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":false,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满1000元立减1元\",\"couponName\":\"\",\"couponRemark\":\"d\",\"publishType\":\"2\",\"couponType\":5,\"aprice\":1000,\"bprice\":0,\"couponStartDate\":\"2019-05-08\",\"couponEndDate\":\"2019-05-31\",\"productionRatio\":\"-0.60%\",\"couponBudgetInfos\":[{\"budgetId\":282,\"ration\":1}],\"settleRatio\":\"1\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":\"10318941\",\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";

		loginId = "0022";
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"0022\",\"campaignPM\":\"0022\",\"description\":\"02\",\"productScope\":0,\"anum\":1,\"aproducts\":[{\"isMandatory\":0,\"spu\":null,\"sku\":141},{\"isMandatory\":0,\"spu\":null,\"sku\":140},{\"isMandatory\":0,\"spu\":null,\"sku\":142}],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"groupName\":\"立减金额\",\"costPoint\":0,\"costMoney\":0,\"discount\":11,\"giftAmount\":0,\"presentType\":3,\"giftProducts\":[],\"ration\":0,\"discountPoint\":0}],\"productPoint\":true,\"giftPoint\":false,\"templateName\":\"满件立减券\",\"couponTitle\":\"其他\",\"couponName\":\"\",\"couponRemark\":\"1\",\"publishType\":4,\"couponType\":7,\"aprice\":0,\"bprice\":0,\"couponStartDate\":\"2019-05-08\",\"couponEndDate\":\"2019-05-08\",\"productionRatio\":\"1.24%\",\"couponBudgetInfos\":[{\"budgetId\":201,\"ration\":1}],\"settleRatio\":\"11\",\"campaignScope\":2,\"areaCodes\":[\"0106\"],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":\"1\",\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":111,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"excludedFlag\":1,\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":10318945,\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"0.93\",\"specifiedPrice\":0,\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":\"333\",\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":192}";
		loginId = "12580";
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"1\",\"campaignGroupName\":\"12580\",\"campaignPM\":\"12580\",\"description\":\"12580\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"3\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":true,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满333元立减3元\",\"couponName\":\"\",\"couponRemark\":\"3\",\"publishType\":\"4\",\"couponType\":5,\"aprice\":333,\"bprice\":0,\"couponStartDate\":\"2019-05-10\",\"couponEndDate\":\"2019-05-10\",\"productionRatio\":\"0.30%\",\"couponBudgetInfos\":[{\"budgetId\":341,\"ration\":1}],\"settleRatio\":\"33\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":3,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":10318826,\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":\"0.76\",\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"101\",\"campaignGroupClass\":2,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"1\"},\"tplId\":\"\"}";
		
		//debug 0523
		json = "{\"apoint\":\"\",\"campaignGroupType\":\"2\",\"campaignGroupName\":\"有券未用-测试\",\"campaignPM\":\"777771\",\"description\":\"测试有券未用\",\"productScope\":\"1\",\"anum\":1,\"aproducts\":[],\"bnum\":0,\"bproducts\":[],\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":\"1\",\"giftProducts\":[],\"giftAmount\":0,\"groupName\":\"立减金额\",\"presentType\":3}],\"productPoint\":true,\"giftPoint\":true,\"templateName\":\"满额立减券\",\"couponTitle\":\"购买满1111元立减1元\",\"couponName\":\"1\",\"couponRemark\":\"1\",\"publishType\":\"\",\"couponType\":5,\"aprice\":1111,\"bprice\":0,\"couponStartDate\":\"2019-05-22\",\"couponEndDate\":\"2019-05-31\",\"productionRatio\":\"0.00%\",\"couponBudgetInfos\":[{\"budgetId\":282,\"ration\":1}],\"settleRatio\":\"1\",\"campaignScope\":1,\"areaCodes\":[],\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"customerType\":\"-1\",\"publishWay\":2,\"platformChannels\":\"-1\",\"memberListId\":[],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"customerCouponDefId\":\"\",\"maxAmount\":1,\"couponDeadline\":0,\"amountPerCustomer\":1,\"productItems\":\"\",\"brandItem\":\"\",\"skuItems\":\"\",\"validTerminals\":[],\"excludedFlag\":1,\"customerPhoneTimeStamp\":\"\",\"customerIdTimeStamp\":\"\",\"customerFileTypes\":[],\"isTerminalImport\":0,\"campaignGroupId\":10318949,\"isSameProduct\":false,\"isSameSeries\":false,\"maxCost\":0,\"specifiedPrice\":\"0\",\"giftRecycleTerminalPoint\":false,\"recycleTerminalPoint\":false,\"isTerminalQuota\":null,\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"applySystemCodes\":\"-1\",\"themeId\":\"undefined\",\"campaignGroupClass\":0,\"marketAct\":false,\"prop\":{\"specifiedCampaignCustomers\":\"\",\"excludedAbnormalUser\":0,\"abnormalUser\":\"\",\"custPay\":0,\"newCustomerStorehouse\":\"0\",\"coupon_purpose\":1,\"deductTerminalGiftPoint\":\"0\"},\"tplId\":\"\"}";
		
		
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

		// 201901R test_q_page_b_scene1_couponresult
		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"10325643\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		// debug-20190216
		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"10318916\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"10325755\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"10318883\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"\",\"couponCode\":\"154535285151\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"\",\"couponCode\":\"154252340705\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		// task-20190221
		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"553943,553948,553949,553950,554655,551133\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		// 201902R prd test20190306
		loginId = "2439";
		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"570703\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"2019-02-01 00:00:00\",\"buyJoinTimeEnd\":\"2019-02-28 23:59:59\",\"pageNo\":\"1\",\"pageSize\":20}";

		loginId = "2439";
		json = "{\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"564178\",\"couponCode\":\"\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

		// 201904R prd jira
		loginId = "2929";
		json = " {\"usedTerminalAreaCode\":\"\",\"usedTerminalOfficeCode\":\"\",\"usedTerminalChannelCode\":\"\",\"usedTerminalCode\":\"\",\"campaignGroupId\":\"\",\"couponType\":\"\",\"useStatus\":\"-1\",\"couponDefIds\":\"\",\"couponCode\":\"155632825658\",\"buyJoinTimeStart\":\"\",\"buyJoinTimeEnd\":\"\",\"pageNo\":\"1\",\"pageSize\":20}";

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

		// 201902R optimize-1
		json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"123\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";

		// 20190318 jira
		json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"aaa\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";
		json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"111\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";

		// 201904R ref coupon result
		loginId = "2929";//
		json = "{\"campaignGroupId\":\"\",\"couponDefIds\":\"10325898\",\"couponCode\":\"\",\"customerId\":\"\",\"mobile\":\"\",\"endDate\":\"\",\"pageNo\":\"1\",\"startDate\":\"\",\"consumeSystem\":\"\",\"useStatus\":\"0\",\"byUsedTerminalOrgCode\":\"0\"}";

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

		loginId = "0022";
		json = "{\"couponDefId\":\"10325776\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"2019-01-26\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

		// 201904R test query def
		loginId = "2929";
		json = "{\"couponDefId\":\"10325931\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

		json = "{\"couponDefId\":\"10325327\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10,\"tplId\":166}";

		// 201904R ref q def
		loginId = "0022";// area
		json = "{\"couponDefId\":\"\",\"couponType\":5,\"couponStatus\":0,\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

		// 201904R find coupon def from loginid
		loginId = "10593";// office
		json = " {\"couponDefId\":\"\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

		loginId = "2929";//
		json = " {\"couponDefId\":\"10325898\",\"couponType\":\"\",\"couponStatus\":\"\",\"couponTitle\":\"\",\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"createStartDate\":\"\",\"createEndDate\":\"\",\"publishEndDate\":\"\",\"publishStartDate\":\"\",\"pageSize\":10}";

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

		// debug-0128
		loginId = "2929";
		json = "{\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"groupType\":\"\",\"createdStartTime\":\"2019-01-26\",\"createdEndTime\":\"\"}";

		// 201902R 优化sql
		json = "{\"groupId\":\"\",\"groupName\":\"\",\"campaignPM\":\"\",\"groupType\":\"\",\"createdStartTime\":\"\",\"createdEndTime\":\"\"}";

		ResultActions resultActions = mockMvc
				.perform(post(a40).contentType(MediaType.APPLICATION_JSON).param("loginId", loginId)
						// .param("pageNo", "1")
						.param("pageNo", "1").param("pageSize", "10").content(json));
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

		// 201901R test_q_old_page_getCouponBudgetLimit
		json = "{\"departmentCode\":\"\",\"channelCode\":\"00\",\"startDate\":\"201812\",\"endDate\":\"201812\",\"pageNo\":1,\"pageSize\":10}";

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

	// 201901R test 组织架构
	@Test
	public void test_DepartmentServiceUtil_all() throws Exception {

		//
		CacheService c = BiostimeWebApplicationContextHolder.getApplicationContext().getBean(CacheService.class);
		List<DepartmentOrganization> orgAll = c.getDepartmentOrganizationAll();
		System.out.println(orgAll);

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

		fileName = "F:/biostime2/file/指定门店编码模板-debug-20190128.txt";

		String json = "";

		json = "";

		loginId = "4711";// 010624

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

	private static <C> C get_test_obj(String json, Class<C> cls)
			throws JsonParseException, JsonMappingException, IOException {
		// ObjectMapper objectMapper =
		// Jackson2ObjectMapperBuilder.json().build();//
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().simpleDateFormat("yyyy-MM-dd HH:mm:ss").build();
		// ThemeBean json2Obj = objectMapper.readValue(payloadJsonReq,
		// ThemeBean.class);
		C json2Obj = objectMapper.readValue(json, cls);
		return json2Obj;
	}
}
