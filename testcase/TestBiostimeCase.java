package com.biostime.coupon.biostimeweb.controller;

import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget_Accounts_channel_baby;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget_Accounts_department_headquarter;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Budget_Accounts_status_enable;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Coupon_Type_Product_Condition_Free_Gift;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Prop_brands;
import static com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum.Settlement_Oa_payment_cash;
import static com.biostime.coupon.biostimeweb.common.constant.RespMessageEnum.Logic_custom;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.biostime.coupon.biostimeweb.bean.BiostimeCampaignNCouponDefBean;
import com.biostime.coupon.biostimeweb.bean.CouponBudgetInfo;
import com.biostime.coupon.biostimeweb.bean.ProductInfo;
import com.biostime.coupon.biostimeweb.bean.common.Pager;
import com.biostime.coupon.biostimeweb.bean.util.CommonPropertiesBeanUtil;
import com.biostime.coupon.biostimeweb.common.constant.CouponConfigEnum;
import com.biostime.coupon.biostimeweb.common.constant.EnumUtil;
import com.biostime.coupon.biostimeweb.common.util.BeanUtil;
import com.biostime.coupon.biostimeweb.common.util.DateUtil;
import com.biostime.coupon.biostimeweb.common.util.ExcelUtil;
import com.biostime.coupon.biostimeweb.common.util.ValidateUtil;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.CouponDealerSettlementOa;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.NcChannelCode;
import com.biostime.coupon.biostimeweb.coupon.settlement.bean.NcOfficeCode;
import com.biostime.coupon.biostimeweb.finance.bean.BudgetAccounts;
import com.biostime.coupon.biostimeweb.http.bean.DealerInfo;
import com.biostime.coupon.biostimeweb.permission.service.PermissionServiceUtil;
import com.biostime.coupon.biostimeweb.product.service.ProductServiceUtil;
import com.biostime.coupon.biostimeweb.query.bean.BudgetAccountsQuery;
import com.biostime.coupon.biostimeweb.query.bean.CampaignGroupQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponDefinitionInOutputRatioQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponDefinitionOaQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponDefinitionQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponOaQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeMarketActQuery;
import com.biostime.coupon.biostimeweb.query.bean.CouponResultBiostimeQuery;
import com.biostime.coupon.biostimeweb.rule.CouponConfigRule;
import com.biostime.coupon.biostimeweb.scene.bean.AutoPublish;
import com.biostime.coupon.biostimeweb.user.bean.LoginUser;
import com.biostime.coupon.biostimeweb.util.ParamUtil;
import com.biostime.coupon.biostimeweb.util.RepoUtil;
import com.biostime.coupon.biostimeweb.validate.service.ValidateServiceUtil;
import com.biostime.coupon.biostimeweb.webservice.WebserviceUtil;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.HrmResponse;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.HrmUserInfo;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.OaFlowK24;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.OaRequestLevel;
import com.biostime.coupon.biostimeweb.webservice.oa.bean.OaResult;
import com.biostime.coupon.biostimeweb.webservice.oa.inout.OaB31ChildChannelRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class TestBiostimeCase {

	@Test
	public void test239() {

		String json = "";

		json = "[{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":363,\"auditMonth\":\"2019-01\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"明丽的各种优惠券\",\"groupId\":10318734,\"defId\":10325299,\"couponType\":1,\"couponTypeName\":\"提货券\",\"title\":\"4000分换超级呵护2段\",\"areaName\":\"华南大区\",\"areaCode\":\"0106\",\"officeName\":\"合生元广州办\",\"officeCode\":\"010604\",\"channelName\":\"婴线\",\"channelCode\":\"01\",\"terminalCode\":\"10002\",\"terminalName\":\"广东广州白云爱婴宝宝\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":769,\"settlementNolendprodAmount\":769,\"campaignPm\":\"3742\",\"campaignPmName\":\"刘943\",\"createdBy\":\"777771\",\"createdByName\":\"测试账号\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1537804800000,\"validityEndDate\":1546703999000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561972631000,\"applyDateStr\":\"2019-07-01\",\"oaCode\":\"NK03-20190701031\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":96},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":363,\"auditMonth\":\"2019-01\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"明丽的各种优惠券\",\"groupId\":10318734,\"defId\":10325299,\"couponType\":1,\"couponTypeName\":\"提货券\",\"title\":\"4000分换超级呵护2段\",\"areaName\":\"华南大区\",\"areaCode\":\"0106\",\"officeName\":\"合生元广州办\",\"officeCode\":\"010604\",\"channelName\":\"婴线\",\"channelCode\":\"01\",\"terminalCode\":\"10002\",\"terminalName\":\"广东广州白云爱婴宝宝\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":769,\"settlementNolendprodAmount\":769,\"campaignPm\":\"3742\",\"campaignPmName\":\"刘943\",\"createdBy\":\"777771\",\"createdByName\":\"测试账号\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1537804800000,\"validityEndDate\":1546703999000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561967283000,\"applyDateStr\":\"2019-07-01\",\"oaCode\":\"NK03-20190701028\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":93},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561087728000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621009\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":52},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":2,\"notStatus\":null,\"statusName\":\"已核销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086342000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621004\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":38},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561100254000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621013\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":59},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561105374000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621014\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":61},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086849000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621005\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":40}]";

		json = "[{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":363,\"auditMonth\":\"2019-01\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"明丽的各种优惠券\",\"groupId\":10318734,\"defId\":10325299,\"couponType\":1,\"couponTypeName\":\"提货券\",\"title\":\"4000分换超级呵护2段\",\"areaName\":\"华南大区\",\"areaCode\":\"0106\",\"officeName\":\"合生元广州办\",\"officeCode\":\"010604\",\"channelName\":\"婴线\",\"channelCode\":\"01\",\"terminalCode\":\"10002\",\"terminalName\":\"广东广州白云爱婴宝宝\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":769,\"settlementNolendprodAmount\":769,\"campaignPm\":\"3742\",\"campaignPmName\":\"刘943\",\"createdBy\":\"777771\",\"createdByName\":\"测试账号\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1537804800000,\"validityEndDate\":1546703999000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561972631000,\"applyDateStr\":\"2019-07-01\",\"oaCode\":\"NK03-20190701031\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":96},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":363,\"auditMonth\":\"2019-01\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"明丽的各种优惠券\",\"groupId\":10318734,\"defId\":10325299,\"couponType\":1,\"couponTypeName\":\"提货券\",\"title\":\"4000分换超级呵护2段\",\"areaName\":\"华南大区\",\"areaCode\":\"0106\",\"officeName\":\"合生元广州办\",\"officeCode\":\"010604\",\"channelName\":\"婴线\",\"channelCode\":\"01\",\"terminalCode\":\"10002\",\"terminalName\":\"广东广州白云爱婴宝宝\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":769,\"settlementNolendprodAmount\":769,\"campaignPm\":\"3742\",\"campaignPmName\":\"刘943\",\"createdBy\":\"777771\",\"createdByName\":\"测试账号\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1537804800000,\"validityEndDate\":1546703999000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1562038341000,\"applyDateStr\":\"2019-07-02\",\"oaCode\":\"NK03-20190702001\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":98},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":363,\"auditMonth\":\"2019-01\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"明丽的各种优惠券\",\"groupId\":10318734,\"defId\":10325299,\"couponType\":1,\"couponTypeName\":\"提货券\",\"title\":\"4000分换超级呵护2段\",\"areaName\":\"华南大区\",\"areaCode\":\"0106\",\"officeName\":\"合生元广州办\",\"officeCode\":\"010604\",\"channelName\":\"婴线\",\"channelCode\":\"01\",\"terminalCode\":\"10002\",\"terminalName\":\"广东广州白云爱婴宝宝\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":769,\"settlementNolendprodAmount\":769,\"campaignPm\":\"3742\",\"campaignPmName\":\"刘943\",\"createdBy\":\"777771\",\"createdByName\":\"测试账号\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1537804800000,\"validityEndDate\":1546703999000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561967283000,\"applyDateStr\":\"2019-07-01\",\"oaCode\":\"NK03-20190701028\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":93},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":2,\"notStatus\":null,\"statusName\":\"已核销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086342000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621004\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":38},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561087728000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621009\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":52},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561100254000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621013\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":59},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561105374000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621014\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":61},{\"type\":null,\"campaignId\":null,\"customerIdFromMobile\":null,\"customerId\":null,\"campaignGroupId\":null,\"couponDefIds\":null,\"couponCode\":null,\"ids\":null,\"couponDealersettlementId\":361,\"auditMonth\":\"2018-12\",\"auditMonthStart\":null,\"auditMonthEnd\":null,\"groupName\":\"总部直邮测试--ZY\",\"groupId\":10318883,\"defId\":10325489,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"areaName\":null,\"areaCode\":null,\"officeName\":\"总部\",\"officeCode\":\"01\",\"channelName\":\"商超\",\"channelCode\":\"02\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"chainTerminalCode\":null,\"chainTerminalName\":null,\"budgetName\":\"【经销商】testmod-channel\",\"budgetId\":41,\"couponNum\":1,\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"statusRec\":null,\"dataCreatedTime\":null,\"validityStartDate\":1541952000000,\"validityEndDate\":1544630399000,\"batchId\":null,\"dealerCode\":null,\"dealerName\":null,\"status\":3,\"notStatus\":null,\"statusName\":\"已撤销\",\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086849000,\"applyDateStr\":\"2019-06-21\",\"oaCode\":\"NK03-20190621005\",\"oaId\":null,\"archiveTime\":null,\"updatedTime\":null,\"updatedBy\":null,\"createdByAreaCode\":null,\"createdByOfficeCode\":\"01\",\"couponDealersettlementOaId\":40}]";
		List<CouponDealerSettlementOa> d = JSON.parseArray(json, CouponDealerSettlementOa.class);
		System.out.println(d.size());

		// step 1
		// find status=2 , get [couponDealersettlementId] 例如[361],
		// find status = 3 and couponDealersettlementId , get
		// [couponDealersettlementOaId]
		/*
		 * select id from GC5_COUPON_DEALERSETTLEMENT_oa gcdo where
		 * gcdo.coupon_dealersettlement_id in (361 , 363) and status = 3
		 */
		// Set<Long> statusSuccess = new HashSet<Long>();

		List<CouponDealerSettlementOa> dataStatusSuccess = d.stream()
				.filter(o -> o.getStatus().intValue() == CouponConfigEnum.Dealer_Settlement_Oa_status_success.getCode())
				.collect(Collectors.toList());
		System.out.println("dataStatusSuccess size=" + dataStatusSuccess.size());
		System.out.println("success data json \n" + JSON.toJSONString(dataStatusSuccess));
		// List<Long> ids = dataStatusSuccess.stream().map(o ->
		// o.getCouponDealersettlementId()).collect(Collectors.toList());
		RepoUtil.getCouponDealerSettlementOaUpdateNullByIdSql(
				dataStatusSuccess.stream().map(o -> o.getCouponDealersettlementId()).collect(Collectors.toList()));

		// step 1 > extract get[coupon_dealersettlement_oa_id] in
		// [coupon_dealersettlement_id] and status = 2
		List<CouponDealerSettlementOa> dataStatusReturnExceptSuccess = d.stream()
				.filter(o -> dataStatusSuccess.stream().allMatch(
						seco -> seco.getCouponDealersettlementId().intValue() == o.getCouponDealersettlementId()))
				.filter(o -> o.getStatus().intValue() == CouponConfigEnum.Dealer_Settlement_Oa_status_return.getCode())
				.collect(Collectors.toList());
		System.out.println("dataStatusReturnExceptSuccess size=" + dataStatusReturnExceptSuccess.size());
		System.out.println("success data json \n" + JSON.toJSONString(dataStatusReturnExceptSuccess));
		RepoUtil.getCouponDealerSettlementOaUpdateNullByOaIdSql(dataStatusReturnExceptSuccess.stream()
				.map(o -> o.getCouponDealersettlementOaId()).collect(Collectors.toList()));

		// step 2
//		not in [step 1]couponDealersettlementId 例如排除　[361] and status 3 ,  get [couponDealersettlementId]
		List<CouponDealerSettlementOa> statusReturnNotInStatusSuccess = d.stream()
				.filter(o -> dataStatusSuccess.stream()
						.noneMatch(seco -> o.getCouponDealersettlementId().intValue() == seco
								.getCouponDealersettlementId().intValue()))
				// .peek(o -> System.out.println("###noneMatch\n" +
				// o.getCouponDealersettlementId() + "###status="+o.getStatus()))
				.filter(o -> o.getStatus().intValue() == CouponConfigEnum.Dealer_Settlement_Oa_status_return.getCode())
				.collect(Collectors.toList());
		System.out.println("statusReturnNotInStatusSuccess size=" + statusReturnNotInStatusSuccess.size());
		System.out.println(
				"statusReturnNotInStatusSuccess data json \n" + JSON.toJSONString(statusReturnNotInStatusSuccess));

		// step 3 > group by [couponDealersettlementId] , max
		// [couponDealersettlementOaId] , get
		// [couponDealersettlementOaId] ,
		List<Long> maxIdRec = statusReturnNotInStatusSuccess.stream()
				.collect(Collectors.groupingBy(CouponDealerSettlementOa::getCouponDealersettlementId,
						Collectors.collectingAndThen(
								Collectors.maxBy(Comparator.comparing(p -> p.getCouponDealersettlementOaId())),
								optional -> optional.get().getCouponDealersettlementOaId())))
				.values().stream().collect(Collectors.toList());
		System.out.println("maxIdRec size=" + maxIdRec.size());
		System.out.println("maxIdRec data json \n" + JSON.toJSONString(maxIdRec));

		//
		// 排除 maxIdRec
		List<CouponDealerSettlementOa> exceptMaxIdRec = statusReturnNotInStatusSuccess.stream()
				.filter(o -> maxIdRec.stream().noneMatch(seco -> seco.intValue() == o.getCouponDealersettlementOaId()))
				.collect(Collectors.toList());
		System.out.println("exceptMaxIdRec size=" + exceptMaxIdRec.size());
		System.out.println("exceptMaxIdRec data json \n" + JSON.toJSONString(exceptMaxIdRec));
		RepoUtil.getCouponDealerSettlementOaUpdateNullByOaIdSql(
				exceptMaxIdRec.stream().map(o -> o.getCouponDealersettlementOaId()).collect(Collectors.toList()));

		List<CouponDealerSettlementOa> updateIds = 	new ArrayList<>();
		updateIds.addAll(dataStatusReturnExceptSuccess);
		updateIds.addAll(exceptMaxIdRec);
		
		System.out.println("updateIds size=" + exceptMaxIdRec.size());
		System.out.println("updateIds data json \n" + JSON.toJSONString(updateIds));
		RepoUtil.getCouponDealerSettlementOaUpdateNullByOaIdSql(
				updateIds.stream().map(o -> o.getCouponDealersettlementOaId()).collect(Collectors.toList()));

		
		
	}

	@Test
	public void test238() {
		String json = "[{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086342000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621004\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000},{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561087728000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621009\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000},{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086849000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621005\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000}]";

		List<CouponDealerSettlementOa> d = JSON.parseArray(json, CouponDealerSettlementOa.class);
		Map<Object, Boolean> map = new ConcurrentHashMap<>();

		Boolean b = map.putIfAbsent(d.get(0).getCouponDealersettlementId(), Boolean.TRUE);
		System.out.println(b);
		b = map.putIfAbsent(d.get(0).getCouponDealersettlementId(), Boolean.TRUE);
		System.out.println(b);

	}

	@Test
	public void test237() {

		String json = "[{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086342000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621004\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000},{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561087728000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621009\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000},{\"applicantId\":\"2929\",\"applicantName\":\"梁521\",\"applyDate\":1561086849000,\"applyDateStr\":\"2019-06-21\",\"auditMonth\":\"2018-12\",\"budgetId\":41,\"budgetName\":\"【经销商】testmod-channel\",\"campaignPm\":\"777771\",\"campaignPmName\":\"测试账号\",\"channelCode\":\"02\",\"channelName\":\"商超\",\"couponDealersettlementId\":361,\"couponNum\":1,\"couponType\":2,\"couponTypeName\":\"买赠券\",\"createdBy\":\"12487\",\"createdByName\":\"张93\",\"defId\":10325489,\"groupId\":10318883,\"groupName\":\"总部直邮测试--ZY\",\"oaCode\":\"NK03-20190621005\",\"officeCode\":\"01\",\"officeName\":\"总部\",\"settlementAmount\":140,\"settlementNolendprodAmount\":1,\"status\":3,\"statusName\":\"已撤销\",\"terminalCode\":\"1301414\",\"terminalName\":\"去去去\",\"title\":\"买呵护+超金，送益生菌（总部直邮）测试--ZY\",\"validityEndDate\":1544630399000,\"validityStartDate\":1541952000000}]";

		List<CouponDealerSettlementOa> d = JSON.parseArray(json, CouponDealerSettlementOa.class);

		System.out.println(JSON.toJSONString(d));
		System.out.println("before size = " + d.size());
		// 去重 , 留唯一
		List<CouponDealerSettlementOa> after = d.stream().filter(distinctByKey(p -> p.getCouponDealersettlementId()))
				.collect(Collectors.toList());
		System.out.println(JSON.toJSONString(after));
		System.out.println("after size = " + after.size());
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		System.out.println("###map size=" + map.size());
		Predicate<T> p = t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		System.out.println("###map putIfAbsent size=" + map.size());
		return p;
	}

	@Test
	public void test236() {
		// List<String> s = new ArrayList<>();
		List<String> s = IntStream.rangeClosed(1, 13).mapToObj(i -> "?").collect(Collectors.toList());
		System.out.println(BeanUtil.joinString(s));

		String sql = RepoUtil.getInsertParamNum(13);
		System.out.println(sql);
	}

	@Test
	public void test235() {
		String type = "getCouponDealerSettlementOaSql";

		String r = "";

		// LoginUser u = getTestLoginUser("0111");
		LoginUser u = getTestLoginUser("011100");

		String area = "";
		String office = "";

		// [0]=area , [1] office
		String[] org = new String[2];

		BiConsumer<String, String> f = (a, o) -> {
			org[0] = a;
			org[1] = o;

		};

		PermissionServiceUtil.setOrgParamByType(type.equals("getCouponDealerSettlementOaSql"), org, "gcds.area_code",
				"gcds.office_code");

		area = org[0];
		office = org[1];
		r = PermissionServiceUtil.getPermission(u, type, area, office);
		System.out.println(r);
//		if (type.equals("getCouponDealerSettlementOaSql")) {
//			area = "gcds.area_code";
//			office = "gcds.office_code";
		// r = getPermission(u, type, area, office);
//
//		}

	}

	@Test
	public void test234() {

		Pager p = new Pager();
		CouponDealerSettlementOa q = new CouponDealerSettlementOa();
		q.setStatus(1);
		q.setIds("361,313,367");
		// q.setDealerCode("1000078");
		// q.setDealerName("测试经销商名字");
//		q.setAreaCode("0111");
//		q.setOfficeCode("010202");
//		q.setChannelCode("01");
//		q.setTerminalCode("173490");
//		q.setBudgetName("经销商");
//		q.setCouponDefIds("10325489");
//		q.setCampaignPm("777771");
//		q.setAuditMonthStart("2018-01");
//		q.setAuditMonthEnd("2019-06");

		System.out.println(JSON.toJSONString(q));
//		LoginUser u = getTestLoginUser("0106");
		// LoginUser u = getTestLoginUser("010604");
		LoginUser u = getTestLoginUser("01");

		System.out.println(RepoUtil.getCouponDealerSettlementOaSql(q, u, p));

	}

	@Test
	public void test233() {

		RepoUtil.getUserInfoSqlV2();
		System.out.println(RepoUtil.getTerminalInfoSql());

		System.out.println(RepoUtil.decodeFieldCnNameByConfigEnum("r.COUPON_TYPE", "COUPON_TYPE_NAME", "Coupon_Type"));
		System.out.println(
				RepoUtil.decodeFieldCnNameByConfigEnum("r.status", "status_name", "Dealer_Settlement_Oa_status"));

	}

	@Test
	public void test_resttemplate_getDealerInfo() throws IOException {
		// {"groupId":"","groupName":"","campaignPM":"","groupType":"","createdStartTime":"2019-05-26","createdEndTime":""}
		String url = "http://10.50.115.4:9129/terminal2.0/v1/terminal-service-oacallback/api/terminal/getDealerInfo";

		// url =
		// "http://10.50.115.19:2136/biostime-coupon-web/b/coupon/q/CampaignGroup.action";
		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> params = new HashMap<String, String>();
		params.put("pageNo", "2");
		params.put("loginId", "0970");

		DealerInfo q = new DealerInfo();
		q.setName("财显");
		q.setPage(1);
		q.setPageSize(20);

		// 创建请求头
		HttpHeaders h = new HttpHeaders();

		h.setContentType(MediaType.APPLICATION_JSON);
		// entity包含请求的对象和消息头；
//		RequestEntity<CampaignGroupQuery> request = new RequestEntity<CampaignGroupQuery>(q,h, HttpMethod.POST,
//				URI.create(url));
//
//		ResponseEntity<String> responseEntity = restTemplate.exchange(request, String.class);

		HttpEntity<DealerInfo> requestEntity = new HttpEntity<>(q, h);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class,
				params);

		//
		String jsonResult = responseEntity.getBody();//
		System.out.println(jsonResult);

		DealerInfo d = get_test_obj(jsonResult, DealerInfo.class);
		System.out.println(d.getItems().size());
		System.out.println("dealer json\n" + JSON.toJSONString(d.getItems().get(0)));
		System.out.println(JSON.toJSONString(d));

	}

	@Test
	public void test232() {
		CouponDealerSettlementOa q = new CouponDealerSettlementOa();
		q.setStatus(4);
		q.setBatchId(1l);
		q.setOaCode("oa-1");

		System.out.println(JSON.toJSONString(q));

	}

	@Test
	public void test_resttemplate_get() throws IOException {
		// {"groupId":"","groupName":"","campaignPM":"","groupType":"","createdStartTime":"2019-05-26","createdEndTime":""}
		String url = "http://10.50.115.19:2136/biostime-coupon-web/c/user/info.action?loginId=777771&mobile=13642670000";

		url = "http://10.50.115.19:2136/biostime-coupon-web/c/user/info.action?loginId={loginId}&mobile= {mobile}";

		Map<String, String> params = new HashMap<String, String>();
		params.put("loginId", "777771");
		params.put("mobile", "mobile");

		RestTemplate restTemplate = new RestTemplate();

		// 创建请求头
		HttpHeaders h = new HttpHeaders();

		h.setContentType(MediaType.APPLICATION_JSON);
		// entity包含请求的对象和消息头；
//		RequestEntity<CampaignGroupQuery> request = new RequestEntity<CampaignGroupQuery>(q,h, HttpMethod.POST,
//				URI.create(url));
//
//		ResponseEntity<String> responseEntity = restTemplate.exchange(request, String.class);

//		HttpEntity<CampaignGroupQuery> requestEntity = new HttpEntity<>(q, h);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class, params);

		//
		String jsonResult = responseEntity.getBody();//
		System.out.println(jsonResult);

		DummyResult d = get_test_obj(jsonResult, DummyResult.class);
		System.out.println(JSON.toJSONString(d));

	}

	@Test
	public void test_resttemplate_post() throws IOException {
		// {"groupId":"","groupName":"","campaignPM":"","groupType":"","createdStartTime":"2019-05-26","createdEndTime":""}
		String url = "http://10.50.115.19:2136/biostime-coupon-web/b/coupon/q/CampaignGroup.action?loginId={loginId}&pageNo={pageNo}";

		// url =
		// "http://10.50.115.19:2136/biostime-coupon-web/b/coupon/q/CampaignGroup.action";
		RestTemplate restTemplate = new RestTemplate();

		Map<String, String> params = new HashMap<String, String>();
		params.put("pageNo", "2");
		params.put("loginId", "0970");
		CampaignGroupQuery q = new CampaignGroupQuery();
		q.setCreatedStartTime("2019-05-26");
		q.setNumIsDeleted(0);

		// 创建请求头
		HttpHeaders h = new HttpHeaders();

		h.setContentType(MediaType.APPLICATION_JSON);
		// entity包含请求的对象和消息头；
//		RequestEntity<CampaignGroupQuery> request = new RequestEntity<CampaignGroupQuery>(q,h, HttpMethod.POST,
//				URI.create(url));
//
//		ResponseEntity<String> responseEntity = restTemplate.exchange(request, String.class);

		HttpEntity<CampaignGroupQuery> requestEntity = new HttpEntity<>(q, h);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class,
				params);

		//
		String jsonResult = responseEntity.getBody();//
		System.out.println(jsonResult);

		DummyResult d = get_test_obj(jsonResult, DummyResult.class);
		System.out.println(JSON.toJSONString(d));

	}

	// x andthen y andthen z.appply()

	public List<NcOfficeCode> getData(String filePath) {

		List<NcOfficeCode> data = new ArrayList();

		Function<HSSFRow, NcOfficeCode> z = (HSSFRow row) -> {

			// HSSFRow row = childSheet.getRow(j);// 3
			NcOfficeCode o = null;
			if (row != null) {

				o = new NcOfficeCode();

				o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(1)));
				o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));

			}
			return o;
		};

		return data;
	}

	@Test
	public void test_func6_main() throws IOException {
		String filePath = "F:/biostime2/file/nc_channel.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		// List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		Function<HSSFRow, NcChannelCode> f = (HSSFRow row) -> {
			NcChannelCode o = new NcChannelCode();

			o.setChannelCode(ExcelUtil.getIntStrFromNumCell(row.getCell(0)));
			o.setNcChannelCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));
			return o;
		};

		List<NcChannelCode> data = getXlsDataCommon(wb, f);
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));

		// 2 map
		Map<String, String> dMap = data.stream().filter(
				o -> ParamUtil.isParamNotEmpty(o.getChannelCode()) && ParamUtil.isParamNotEmpty(o.getNcChannelCode()))
				.collect(Collectors.toMap(NcChannelCode::getChannelCode, NcChannelCode::getNcChannelCode));
		System.out.println(JSON.toJSONString(dMap));

	}

	@Test
	public void test_func5_main() throws IOException {
		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		// List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		Function<HSSFRow, NcOfficeCode> f = (HSSFRow r) -> {
			NcOfficeCode o = new NcOfficeCode();

			o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(1)));
			o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(2)));
			return o;
		};

		List<NcOfficeCode> data = getXlsDataCommon(wb, f);
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));
		// 2 map
		Map<String, String> dMap = data.stream().filter(
				o -> ParamUtil.isParamNotEmpty(o.getOfficeCode()) && ParamUtil.isParamNotEmpty(o.getNcOfficeCode()))
				.collect(Collectors.toMap(NcOfficeCode::getOfficeCode, NcOfficeCode::getNcOfficeCode));
		System.out.println(JSON.toJSONString(dMap));

	}

	public <T> List<T> getXlsDataCommon(HSSFWorkbook wbo, Function<HSSFRow, T> f) throws IOException {

		Function<HSSFWorkbook, List<T>> f2 = (HSSFWorkbook wb) -> {
			List<T> data = new ArrayList();
			IntStream.range(0, wb.getNumberOfSheets()).forEach((i) -> {

				HSSFSheet childSheet = wb.getSheetAt(i);// 2
				// int rowNum = childSheet.getPhysicalNumberOfRows();

				IntStream.range(1, childSheet.getPhysicalNumberOfRows()).forEach((j) -> {
					/** 从第二行开始读取 */
					HSSFRow row = childSheet.getRow(j);// 3

					if (row != null) {

						data.add(f.apply(row));
					}

				});

			});
			return data;

		};

		return f2.apply(wbo);
	}

	@Test
	public void test_func4_main() throws IOException {
		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		// List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		Function<HSSFRow, NcOfficeCode> f = (HSSFRow r) -> {
			NcOfficeCode o = new NcOfficeCode();

			o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(1)));
			o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(2)));
			return o;
		};

		List<NcOfficeCode> data = test_func4(wb, f);
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));
	}

	public List<NcOfficeCode> test_func4(HSSFWorkbook wbo, Function<HSSFRow, NcOfficeCode> f) throws IOException {
		/*
		 * Function<HSSFRow, NcOfficeCode> f = (HSSFRow r) -> { NcOfficeCode o = new
		 * NcOfficeCode();
		 * 
		 * o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(1)));
		 * o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(2))); return o; };
		 */

		Function<HSSFWorkbook, List<NcOfficeCode>> f2 = (HSSFWorkbook wb) -> {
			List<NcOfficeCode> data = new ArrayList();
			IntStream.range(0, wb.getNumberOfSheets()).forEach((i) -> {

				HSSFSheet childSheet = wb.getSheetAt(i);// 2
				// int rowNum = childSheet.getPhysicalNumberOfRows();

				IntStream.range(1, childSheet.getPhysicalNumberOfRows()).forEach((j) -> {
					/** 从第二行开始读取 */
					HSSFRow row = childSheet.getRow(j);// 3

					if (row != null) {

						data.add(f.apply(row));
					}

				});

			});
			return data;

		};

		return f2.apply(wbo);
	}

	@Test
	public void test_func3() throws IOException {

		Function<HSSFRow, NcOfficeCode> f = (HSSFRow r) -> {
			NcOfficeCode o = new NcOfficeCode();

			o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(1)));
			o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(2)));
			return o;
		};

		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		IntStream.range(0, wb.getNumberOfSheets()).forEach((i) -> {

			HSSFSheet childSheet = wb.getSheetAt(i);// 2
			// int rowNum = childSheet.getPhysicalNumberOfRows();

			IntStream.range(1, childSheet.getPhysicalNumberOfRows()).forEach((j) -> {
				/** 从第二行开始读取 */
				HSSFRow row = childSheet.getRow(j);// 3

				if (row != null) {

//					NcOfficeCode o = new NcOfficeCode();
//
//					o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(1)));
//					o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));
//
//					data.add(o);
					data.add(f.apply(row));
				}

			});

		});
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));
	}

	@Test
	public void test_func2() throws IOException {

		Function<HSSFRow, NcOfficeCode> f = (HSSFRow r) -> {
			NcOfficeCode o = new NcOfficeCode();

			o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(1)));
			o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(r.getCell(2)));
			return o;
		};

		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		IntStream.range(0, wb.getNumberOfSheets()).forEach((i) -> {

			HSSFSheet childSheet = wb.getSheetAt(i);// 2
			// int rowNum = childSheet.getPhysicalNumberOfRows();

			IntStream.range(1, childSheet.getPhysicalNumberOfRows()).forEach((j) -> {
				/** 从第二行开始读取 */
				HSSFRow row = childSheet.getRow(j);// 3

				if (row != null) {

//					NcOfficeCode o = new NcOfficeCode();
//
//					o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(1)));
//					o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));
//
//					data.add(o);
					data.add(f.apply(row));
				}

			});

		});
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));
	}

	@Test
	public void test_func1() throws IOException {

		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);

		IntStream.range(0, wb.getNumberOfSheets()).forEach((i) -> {

			HSSFSheet childSheet = wb.getSheetAt(i);// 2
			// int rowNum = childSheet.getPhysicalNumberOfRows();

			IntStream.range(1, childSheet.getPhysicalNumberOfRows()).forEach((j) -> {
				/** 从第二行开始读取 */
				HSSFRow row = childSheet.getRow(j);// 3

				if (row != null) {

					NcOfficeCode o = new NcOfficeCode();

					o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(1)));
					o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));

					data.add(o);
				}
			});

		});
		System.out.println(data.size());
		System.out.println(JSON.toJSONString(data));
	}

	@Test
	public void test_func() throws IOException {

		String filePath = "F:/biostime2/file/common_department2nc_pk.xls";// 1

		InputStream io = Files.newInputStream(Paths.get(filePath));

		List<NcOfficeCode> data = new ArrayList();

		HSSFWorkbook wb = new HSSFWorkbook(io);
		int sheetNum = wb.getNumberOfSheets();

		for (int i = 0; i < sheetNum; i++) {

			HSSFSheet childSheet = wb.getSheetAt(i);// 2
			int rowNum = childSheet.getPhysicalNumberOfRows();
			System.out.println("###getNcOfficeCodeFromInputStream rownum=" + rowNum);

			// validate if empty

			//
			for (int j = 1; j < rowNum; j++) {
				/** 从第二行开始读取 */
				HSSFRow row = childSheet.getRow(j);// 3
				if (row == null)
					continue;
				NcOfficeCode o = new NcOfficeCode();

				o.setOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(1)));
				o.setNcOfficeCode(ExcelUtil.getIntStrFromNumCell(row.getCell(2)));

				data.add(o);
				// read

			}
		}
		// return data;
		System.out.println(JSON.toJSONString(data));

	}

	@Test
	public void test231() throws JsonParseException, JsonMappingException, IOException {

		String json = "{\"totalSize\":1,\"code\":\"1\",\"dataList\":[{\"tempresidentnumber\":\"\",\"createdate\":\"2015-11-09\",\"language\":\"\",\"subcompanyid1\":\"141\",\"subcompanyname\":\"华北大区 North China Region\",\"joblevel\":\"\",\"startdate\":\"\",\"password\":\"\",\"subcompanycode\":\"00014410000000000IN6\",\"jobactivitydesc\":\"\",\"bememberdate\":\"\",\"modified\":\"2019-05-07 09:15:54\",\"id\":\"493\",\"mobilecall\":\"\",\"nativeplace\":\"\",\"certificatenum\":\"110101195817051031\",\"height\":\"0\",\"loginid\":\"2168\",\"created\":\"2019-05-07 09:15:53\",\"degree\":\"\",\"bepartydate\":\"\",\"weight\":\"0\",\"telephone\":\"\",\"residentplace\":\"\",\"lastname\":\"张蒙\",\"healthinfo\":\"1\",\"enddate\":\"2010-04-30\",\"maritalstatus\":\"已婚\",\"departmentname\":\"北京办婴线团队 BeiJ Baby Specialty\",\"folk\":\"汉族\",\"status\":\"5\",\"birthday\":\"1974-05-25\",\"accounttype\":\"\",\"jobcall\":\"501\",\"managerid\":\"0\",\"assistantid\":\"\",\"departmentcode\":\"10014410000000000QPK\",\"email\":\"\",\"seclevel\":\"10\",\"policy\":\"群众\",\"jobtitle\":\"9818\",\"workcode\":\"2168\",\"sex\":\"男\",\"departmentid\":\"1302\",\"homeaddress\":\"\",\"mobile\":\"\",\"lastmoddate\":\"2015-11-09\",\"educationlevel\":\"0\",\"islabouunion\":\"1\",\"locationid\":\"21\",\"regresidentplace\":\"\",\"dsporder\":\"493\"}],\"pageSize\":20,\"page\":1}";

		HrmUserInfo hu = new HrmUserInfo();
		hu.setWorkcode("2168");
		System.out.println(JSON.toJSONString(hu));

		HrmResponse json2Obj = BeanUtil.convertJson2Bean(json, HrmResponse.class);
		System.out.println(JSON.toJSONString(json2Obj));

	}

	@Test
	public void test230() {
		System.out.println(DateUtil.formatSimpleDbDate(new Date()));

	}

	@Test
	public void test229() {
		String prefix = "K24-";
		String yearmonth = DateUtil.convertToAuditMonth();
		Long id = 1l;

		// 5位
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		String str = String.format("%05d", id);
		String r = prefix + yearmonth + str;
		System.out.println(r);

	}

	@Test
	public void test228() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		OaFlowK24 o = new OaFlowK24();
		o.setCreatorId("1021");
		o.setApplyDate("111");
		o.setWorkflowId("14909");
		o.setApplyId("settlement");
		o.setEndDateDt1("2019-04-29");
		o.setStartDateDt2("2019-04-29");

		System.out.println(JSON.toJSONString(PropertyUtils.describe(o)));

		Map<String, String> reqMap = new HashMap<String, String>();
		Map<String, String> reqFieldMainMap = new HashMap<String, String>();
		Map<String, String> reqFieldDetail1Map = new HashMap<String, String>();
		Map<String, String> reqFieldDetail2Map = new HashMap<String, String>();

		for (Field f : o.getClass().getDeclaredFields()) {

			f.setAccessible(true);
			if (f.isAnnotationPresent(XmlElement.class)) {

				XmlElement xe = f.getAnnotation(XmlElement.class);
				// System.out.println(xe.name());
				if (ParamUtil.isParamNotEmpty(xe.name()) && xe.name().equals("WorkflowMainTableInfo")) {
					reqFieldMainMap.put(f.getName(), (String) f.get(o));
				} else if (ParamUtil.isParamNotEmpty(xe.name()) && xe.name().equals("WorkflowDetailTableInfos1")) {
					reqFieldDetail1Map.put(f.getName(), (String) f.get(o));
				} else if (ParamUtil.isParamNotEmpty(xe.name()) && xe.name().equals("WorkflowDetailTableInfos2")) {
					reqFieldDetail2Map.put(f.getName(), (String) f.get(o));
				} else {
					reqMap.put(f.getName(), (String) f.get(o));
				}

			}

		}
		System.out.println(JSON.toJSONString(reqMap));
		System.out.println(JSON.toJSONString(reqFieldMainMap));
		System.out.println(JSON.toJSONString(reqFieldDetail1Map));
		System.out.println(JSON.toJSONString(reqFieldDetail2Map));

	}

	@Test
	public void test227() {

		CouponOaQuery query = new CouponOaQuery();
		query.setDefId(10325803l);
		query.setPayment(Settlement_Oa_payment_cash.getCode());

		ArrayList<CouponOaQuery> dataList = new ArrayList<CouponOaQuery>();

		dataList.add(get_test_data1());
		dataList.add(get_test_data1());
		// dataList.add(obj);

		query.setDataList(dataList);

		System.out.println(JSON.toJSONString(query));

		// map[InputDepartmentCode + InputChannelCode , list[CouponOaQuery]]
		Map<String, List<CouponOaQuery>> userDataList = query.getDataList().stream()
				.collect(Collectors.toMap(obj -> obj.getInputDepartmentCode() + "-" + obj.getInputChannelCode(),

						obj -> {
							List<CouponOaQuery> l = new ArrayList<>();
							CouponOaQuery o = new CouponOaQuery();
							BeanUtils.copyProperties(obj, o);
							l.add(o);
							return l;
						},

						(List<CouponOaQuery> value1, List<CouponOaQuery> value2) -> {
							value1.addAll(value2);
							return value1;
						}));
		System.out.println("###userDataList\n" + JSON.toJSONString(userDataList));
	}

	private CouponOaQuery get_test_data1() {
		CouponOaQuery obj = new CouponOaQuery();

		// obj.setInputDepartmentCode("010704");
		obj.setInputDepartmentCode("010604");
		obj.setInputChannelCode("01");
		obj.setBudgetUnit("w用户选择");
		obj.setBudgetUnitCode("1");
		obj.setBudgetDepartment("w用户选择");
		obj.setBudgetDepartmentCode("2");
		obj.setBudgetCategory("w用户选择");
		obj.setBudgetCategoryCode("3");
		obj.setBudgetProject("w用户选择");
		obj.setBudgetProjectCode("4");
		return obj;
	}

	@Test
	public void test226() {
		BiostimeCampaignNCouponDefBean campaignCoupon = new BiostimeCampaignNCouponDefBean();

		campaignCoupon.setCouponType(Coupon_Type_Product_Condition_Free_Gift.getCode());
		campaignCoupon.setAnum(0);

		System.out.println(JSON.toJSONString(campaignCoupon));
		System.out.println(campaignCoupon.getAnum());

		if (campaignCoupon.getCouponType() == Coupon_Type_Product_Condition_Free_Gift.getCode()) {
			if (campaignCoupon.getAnum() <= 0) {
				System.out.println("###购买商品数量件数不正确,请检查");
			} else {
				System.out.println("checked");
			}
		}

	}

	@Test
	public void test_EnumUtil() {

		// String status = "4";
		String status = "3";
		List<CouponConfigEnum> enums = EnumUtil.getCouponConfigEnumByPrefix("Settlement_Oa_status");
		System.out.println(enums.toString());

		boolean b = enums.stream().anyMatch(e -> e.getCode() == ParamUtil.convertParam2Integer(status));
		System.out.println(b);
	}

	@Test
	public void test225() {

		Pager p = new Pager();
		CouponDefinitionQuery q = new CouponDefinitionQuery();

		System.out.println(JSON.toJSONString(q));
		LoginUser u = getTestLoginUser("010704");

		System.out.println(RepoUtil.getCouponDefinitionQuerySqlV2(q, u, p));

	}

	@Test
	public void test224() {

		CouponOaQuery obj = new CouponOaQuery();
		obj.setDefId(10325898l);
		obj.setBudgetUnit("w用户选择");
		obj.setBudgetUnitCode("1");
		obj.setBudgetDepartment("w用户选择");
		obj.setBudgetDepartmentCode("2");
		obj.setBudgetCategory("w用户选择");
		obj.setBudgetCategoryCode("3");
		obj.setBudgetProject("w用户选择");
		obj.setBudgetProjectCode("4");
		obj.setPayment(Settlement_Oa_payment_cash.getCode());

		System.out.println(JSON.toJSONString(obj));
	}

	@Test
	public void test223() {
		/*
		 * 申请人工号 applicant_id 流程审批状态 status( 1=申请中 , 2 = 已归档 , 3 = 已退回 ) 查询条件 提交时间
		 * created_time 查询条件 归档时间 archive_time 查询条件
		 */
		/*
		 * Untitled 1 1 申请日期 apply_date 查询条件 2 归档日期 archive_date
		 * 通过表GC5_COUPON_DEFINITION_OA获取 查询条件 4 大区 申请人所在大区 查询条件 17 OA流程编号
		 * 通过表GC5_COUPON_DEFINITION_OA获取 查询条件 20 终端编码 code terminal_code 用券终端code
		 * 关联CRM_TERMINAL 查询条件 36 申请人工号 通过表GC5_COUPON_DEFINITION_OA获取 查询条件
		 */

		Pager p = new Pager();
		CouponOaQuery q = new CouponOaQuery();

//		q.setStatus(1);
//		q.setApplicantId("10593");
//		q.setApplyDateStartDate("2019-04-01");
//		q.setApplyDateEndDate("2019-06-01");
//		q.setArchiveTimeStartDate("2019-04-01");
//		q.setArchiveTimeEndDate("2019-06-01");
		q.setDefId(123l);
		//
//		q.setAreaCode("111");
//		q.setOaCode("222");
//		q.setTerminalCode("333");

		System.out.println(JSON.toJSONString(q));
		LoginUser u = getTestLoginUser("010704");

		System.out.println(RepoUtil.getCouponOaQuerySql(q, u, p));

	}

	@Test
	public void test222() {
		// RepoUtil.getCountBudgetAccoutsCouponDefSql();

		Pager p = new Pager();
		CouponDefinitionOaQuery q = new CouponDefinitionOaQuery();

		q.setStatus(1);
		// q.setApplicantId("10593");
		q.setCreatedTimeStartDate("2019-04-01");
		q.setCreatedTimeEndDate("2019-06-01");
		/*
		 * q.setArchiveTimeStartDate("2018-04-01");
		 * q.setArchiveTimeEndDate("2018-06-01");
		 */
		q.setDefId(123l);
		System.out.println(JSON.toJSONString(q));
		LoginUser u = getTestLoginUser("010704");

		System.out.println(RepoUtil.getCouponDefinitionOaQuerySql(q, u, p));

	}

	@Test
	public void test221() {
		CouponDefinitionOaQuery q = new CouponDefinitionOaQuery();
		q.setStatus(1);
		q.setApplicantId("10593");
		q.setCreatedTimeStartDate("2018-04-01");
		q.setCreatedTimeEndDate("2018-06-01");
		q.setArchiveTimeStartDate("2018-04-01");
		q.setArchiveTimeEndDate("2018-06-01");
		System.out.println(JSON.toJSONString(q));

	}

	@Test
	public void test220() {

		CouponConfigRule r = new CouponConfigRule();

		String s = "d-test【品牌dodie】";

	}

	@Test
	public void test219() {
		String keyWord = "【经销商】";
		String budgetName = "【经销商】Dodie经销商推广费-沃尔玛";
		System.out.println(budgetName.indexOf(keyWord));
		if (budgetName.indexOf(keyWord) > 0) {
			System.out.println("find");
		}
	}

	@Test
	public void test218() {

		String couponDefIds = "aaa,111";
		couponDefIds = "222,111";

		if (StringUtils.isEmpty(couponDefIds)) {
			// return new ArrayList<Long>();
			System.out.println("优惠券ID:请输入整数");
		}

		List<Long> l = null;

		String[] couponDefIdsAry = couponDefIds.split(",");
		System.out.println(couponDefIdsAry);
		boolean b = Arrays.stream(couponDefIdsAry).anyMatch(o -> !StringUtils.isNumeric(o));

		System.out.println(b);

		if (b) {
			System.out.println(JSON.toJSONString(ValidateServiceUtil.getResultCustom(Logic_custom, "优惠券ID:请输入整数")));
			// return ValidateServiceUtil.getResultCustom(Logic_custom, "优惠券ID:请输入整数");
		}

		//
		if (ParamUtil.validateParamNotNumber(couponDefIds)) {
			System.out.println(JSON.toJSONString(ValidateServiceUtil.getResultCustom(Logic_custom, "优惠券ID:请输入整数")));
		}
	}

	@Test
	public void test217() {

		String auditMonth = DateUtil.convertToAuditMonth();
		Calendar auditMonthCalendar = DateUtil.getCalendarByDefineTimeStr(auditMonth, DateUtil.SIMPLE_MONTH_FORMAT_STR);
		System.out.println("before=" + DateUtil.formatSimpleDbDate(auditMonthCalendar.getTime()));
		auditMonthCalendar.add(Calendar.MONTH, -1);
		System.out.println("after=" + DateUtil.formatSimpleDbDate(auditMonthCalendar.getTime()));

		auditMonth = DateUtil.getSpecifiedAuditmonthRelativeCurrent(auditMonth, -1);
		System.out.println("after auditMonth =  " + auditMonth);

	}

	@Test
	public void test216() {

		// Long id = 261l;
		Long id = 263l;

		List<Long> ids = new ArrayList<Long>();
		ids.add(261l);
		ids.add(262l);

		System.out.println(id.intValue() == ids.get(0).intValue());

		boolean b = ids.stream().anyMatch(o -> id.intValue() == o.intValue());
		System.out.println(b);

	}

	@Test
	public void test215() {

		System.out.println(RepoUtil.getSubSqlConnectUserInfo());

	}

	@Test
	public void testoa() throws Exception {
		String url = "http://10.50.115.128/services/WorkflowService?wsdl";
		OaB31ChildChannelRequest oaB31ChildChannelRequest = new OaB31ChildChannelRequest();
		oaB31ChildChannelRequest.setApplyId("99999");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		String date = sdf.format(new Date());
		oaB31ChildChannelRequest.setApplyDate(date);
		oaB31ChildChannelRequest.setCreatorId("0970");
		oaB31ChildChannelRequest.setRequestLevel(OaRequestLevel.NORMAL.toString());
		oaB31ChildChannelRequest.setRequestName("新建终端");

		System.out.println(JSON.toJSON(oaB31ChildChannelRequest));

		OaResult rev = WebserviceUtil.submitOA(oaB31ChildChannelRequest, url);
		System.out.println(JSON.toJSON(rev));
		Assert.assertTrue(rev == OaResult.SUCCESS);
	}

	/*
	 * @Test public void test215() { QName SERVICE_NAME = new
	 * QName("webservices.services.weaver.com.cn", "WorkflowService"); URL wsdlURL =
	 * WorkflowService.WSDL_LOCATION; WorkflowService ss = new
	 * WorkflowService(wsdlURL, SERVICE_NAME); WorkflowServicePortType port =
	 * ss.getWorkflowServiceHttpPort();
	 * 
	 * System.out.println("Invoking doCreateWorkflowRequest...");
	 * com.biostime.coupon.biostimeweb.webservice.oa.WorkflowRequestInfo
	 * _doCreateWorkflowRequest_in0 = null; int _doCreateWorkflowRequest_in1 = 0;
	 * 
	 * // 201902R test oa
	 * 
	 * java.lang.String _doCreateWorkflowRequest__return =
	 * port.doCreateWorkflowRequest(_doCreateWorkflowRequest_in0,
	 * _doCreateWorkflowRequest_in1);
	 * System.out.println("doCreateWorkflowRequest.result=" +
	 * _doCreateWorkflowRequest__return); }
	 */

	/*
	 * @Test public void test214() { QName SERVICE_NAME = new
	 * QName("http://www.oorsprong.org/websamples.countryinfo",
	 * "CountryInfoService"); URL wsdlURL = CountryInfoService.WSDL_LOCATION;
	 * 
	 * CountryInfoService ss = new CountryInfoService(wsdlURL, SERVICE_NAME);
	 * CountryInfoServiceSoapType port = ss.getCountryInfoServiceSoap12();
	 * 
	 * System.out.println("Invoking countryFlag..."); java.lang.String
	 * _countryFlag_sCountryISOCode = "CN"; java.lang.String _countryFlag__return =
	 * port.countryFlag(_countryFlag_sCountryISOCode);
	 * System.out.println("countryFlag.result=" + _countryFlag__return);
	 * 
	 * }
	 */

	@Test
	public void test213() throws JsonParseException, JsonMappingException, IOException {

		// 201901R bug aproudcts sku repeat
		String json = "{\"amountPerCustomer\":1,\"anum\":1,\"applySystemCodes\":\"2\",\"aprice\":0,\"aproducts\":[{\"isMandatory\":0,\"sku\":105},{\"isMandatory\":0,\"sku\":177},{\"isMandatory\":0,\"sku\":176},{\"isMandatory\":0,\"sku\":104},{\"isMandatory\":0,\"sku\":355},{\"isMandatory\":0,\"sku\":354},{\"isMandatory\":0,\"sku\":185},{\"isMandatory\":0,\"sku\":186},{\"isMandatory\":0,\"sku\":187},{\"isMandatory\":0,\"sku\":279},{\"isMandatory\":0,\"sku\":3299},{\"isMandatory\":0,\"sku\":100},{\"isMandatory\":0,\"sku\":101},{\"isMandatory\":0,\"sku\":296},{\"isMandatory\":0,\"sku\":99},{\"isMandatory\":0,\"sku\":163},{\"isMandatory\":0,\"sku\":103},{\"isMandatory\":0,\"sku\":102},{\"isMandatory\":0,\"sku\":302},{\"isMandatory\":0,\"sku\":253408},{\"isMandatory\":0,\"sku\":253309},{\"isMandatory\":0,\"sku\":253409},{\"isMandatory\":0,\"sku\":253407},{\"isMandatory\":0,\"sku\":253413},{\"isMandatory\":0,\"sku\":253412},{\"isMandatory\":0,\"sku\":253411},{\"isMandatory\":0,\"sku\":253410},{\"isMandatory\":0,\"sku\":253614},{\"isMandatory\":0,\"sku\":253414},{\"isMandatory\":0,\"sku\":253415},{\"isMandatory\":0,\"sku\":253312},{\"isMandatory\":0,\"sku\":262007},{\"isMandatory\":0,\"sku\":266508},{\"isMandatory\":0,\"sku\":3085},{\"isMandatory\":0,\"sku\":261307},{\"isMandatory\":0,\"sku\":3082},{\"isMandatory\":0,\"sku\":261908},{\"isMandatory\":0,\"sku\":266407},{\"isMandatory\":0,\"sku\":261308},{\"isMandatory\":0,\"sku\":3084},{\"isMandatory\":0,\"sku\":261907},{\"isMandatory\":0,\"sku\":266507},{\"isMandatory\":0,\"sku\":261407},{\"isMandatory\":0,\"sku\":3083},{\"isMandatory\":0,\"sku\":261307},{\"isMandatory\":0,\"sku\":3082},{\"isMandatory\":0,\"sku\":266508},{\"isMandatory\":0,\"sku\":262007}],\"areaCodes\":[],\"bindSystemCodes\":\"HYT,WEIXIN,MOBILE,HT_WEIXIN,DODIE_WEIXIN\",\"bnum\":0,\"bprice\":0,\"bproducts\":[],\"campaignGroupClass\":0,\"campaignGroupId\":\"\",\"campaignGroupName\":\"六盘水2月常规活动\",\"campaignGroupType\":1,\"campaignPM\":\"12900\",\"campaignScope\":3,\"channelCodes\":[\"01\",\"02\",\"03\",\"08\"],\"conditions\":[{\"condition\":1,\"value\":\"\"}],\"coupon633Type\":[],\"couponBudgetInfos\":[{\"budgetId\":41,\"ration\":1}],\"couponDeadline\":1,\"couponEndDate\":\"2019-02-25\",\"couponName\":\"新客购大听送400g\",\"couponPlatforms\":\"-1\",\"couponRemark\":\"新客购大听送400g，所购买的产品及赠品均不再享受合生元 积分\",\"couponStartDate\":\"2019-01-28\",\"couponTitle\":\"新客购大听送400g\",\"couponType\":2,\"customerFileTypes\":[],\"customerIdTimeStamp\":\"\",\"customerPhoneTimeStamp\":\"\",\"customerType\":4,\"description\":\"六盘水2月常规活动，所购买的产品不再享受合生元积分\",\"excludedFlag\":0,\"giftGroups\":[{\"costMoney\":0,\"costPoint\":0,\"discount\":0,\"discountPoint\":0,\"giftAmount\":1,\"giftProducts\":[{\"amount\":1,\"giftName\":\"合生元金装较大婴儿配方奶粉 400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":446},{\"amount\":1,\"giftName\":\"合生元金装婴儿配方奶粉400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":276},{\"amount\":1,\"giftName\":\"合生元超级金装婴儿配方奶粉400克\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":127},{\"amount\":1,\"giftName\":\"合生元超级金装较大婴儿配方奶粉400克 (400g)2 阶段\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":436},{\"amount\":1,\"giftName\":\"合生元阿尔法星较大婴儿配方奶粉400克 /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":267208},{\"amount\":1,\"giftName\":\"合生元阿尔法星婴儿配方奶粉400克 /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":267207},{\"amount\":1,\"giftName\":\"合生元贝塔星较大婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253311},{\"amount\":1,\"giftName\":\"合生元贝塔星婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253310},{\"amount\":1,\"giftName\":\"合生元派星较大婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253711},{\"amount\":1,\"giftName\":\"合生元派星婴儿配方奶粉 （400克）\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":253615},{\"amount\":1,\"giftName\":\"HT有机婴儿配方奶粉 400g\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":3085},{\"amount\":1,\"giftName\":\"Healthy Times爱斯时光婴儿配方奶粉400G /罐\",\"giftPrice\":0,\"giftPriceRatio\":0,\"isDeliveryAddress\":0,\"sku\":261607}],\"groupName\":\"商品领取\",\"presentType\":1,\"ration\":0}],\"giftPoint\":false,\"giftRecycleTerminalPoint\":false,\"isAllowBusinessPublish\":0,\"isAppointP\":0,\"isSameProduct\":false,\"isSameSeries\":false,\"isTerminalImport\":0,\"isTerminalQuota\":0,\"marketAct\":false,\"maxAmount\":100,\"maxCost\":137.06,\"memberListId\":[],\"orderPoint\":false,\"platformChannels\":\"-1\",\"productItems\":\"203\",\"productPoint\":false,\"productScope\":0,\"productionRatio\":\"2.62% ~ 44.95%\",\"prop\":{\"abnormalUser\":\"\",\"autoPublishedCfg\":false,\"autoPublishedStatus\":0,\"biostime\":false,\"biostimeCampaign\":false,\"coupon_purpose\":\"1\",\"custPay\":\"0\",\"deductTerminalGiftPoint\":\"0\",\"excludedAbnormalUser\":\"0\",\"isSpecifiedCustomersSrc\":0,\"mama100\":false,\"marketAct\":false,\"newCustomerStorehouse\":\"0\",\"orderPoint\":false,\"refresh\":false,\"specifiedCampaignCustomers\":\"\",\"specifiedCustomers\":false,\"specifiedMobiles\":false,\"updateValidityEndDate\":false},\"publishType\":3,\"publishWay\":2,\"publisher\":0,\"recycleTerminalPoint\":false,\"sameProduct\":false,\"sameSeries\":false,\"settleRatio\":100,\"skuItems\":\"\",\"sourceSystem\":\"PC\",\"specifiedPrice\":\"0\",\"templateName\":\"买赠券\",\"themeId\":\"101\",\"totalExpense\":0,\"validTerminals\":[\"14311\",\"1348517\",\"1350841\",\"1350843\",\"1325890\",\"1349844\",\"1344934\",\"1325886\",\"1342194\",\"1336545\",\"168122\",\"102203\",\"168123\",\"120252\",\"170267\",\"10513\",\"174156\",\"181552\",\"1343201\",\"17111\",\"176108\",\"112970\"],\"whatGive\":\"product\"}";

		BiostimeCampaignNCouponDefBean query = get_test_obj(json, BiostimeCampaignNCouponDefBean.class);
		System.out.println(JSON.toJSONString(query));

		Map<Long, List<Long>> aproductsMap = query.getAproducts().stream().collect(Collectors.toMap(ProductInfo::getSku,

				obj -> {
					List<Long> l = new ArrayList<>();
					l.add(obj.getSku());
					return l;
				},

				(List<Long> value1, List<Long> value2) -> {
					value1.addAll(value2);
					return value1;
				}));

		for (ProductInfo p : query.getAproducts()) {
			if (aproductsMap.get(p.getSku()).size() > 1) {
				System.out.println(p.getSku());
			}
		}
	}

	@Test
	public void test212() throws JsonParseException, JsonMappingException, IOException {
		BiostimeCampaignNCouponDefBean campaignCoupon = new BiostimeCampaignNCouponDefBean();
		List<CouponBudgetInfo> couponBudgetInfos = new ArrayList<>();

		CouponBudgetInfo o = new CouponBudgetInfo();
		o.setBudgetId(183l);
		o.setRation(BigDecimal.valueOf(1l));

		couponBudgetInfos.add(o);
		campaignCoupon.setCouponBudgetInfos(couponBudgetInfos);
		System.out.println(JSON.toJSONString(campaignCoupon));

		String json = "{\"amountPerCustomer\":1,\"anum\":0,\"apoint\":0,\"aprice\":0,\"bnum\":0,\"bprice\":0,\"campaignScope\":0,\"couponBudgetInfos\":[{\"budgetId\":183,\"ration\":1}],\"couponDeadline\":0,\"couponPlatforms\":\"-1\",\"couponType\":0,\"customerType\":-1,\"excludedFlag\":0,\"giftPoint\":false,\"giftRecycleTerminalPoint\":false,\"isAllowBusinessPublish\":0,\"isAppointP\":0,\"isTerminalImport\":0,\"isTerminalQuota\":0,\"marketAct\":false,\"maxAmount\":1,\"maxCost\":0,\"orderPoint\":false,\"platformChannels\":\"-1\",\"productPoint\":false,\"productScope\":0,\"publishType\":3,\"publishWay\":0,\"publisher\":0,\"recycleTerminalPoint\":false,\"sameProduct\":false,\"sameSeries\":false,\"sourceSystem\":\"PC\",\"totalExpense\":0,\"whatGive\":\"\"}";

		BiostimeCampaignNCouponDefBean query = get_test_obj(json, BiostimeCampaignNCouponDefBean.class);
		System.out.println(JSON.toJSONString(query));
	}

	@Test
	public void test211() {
		AutoPublish autoPublish = new AutoPublish();
		String custMobile = "19965865057";
		String validateMsg;

		if (!ValidateUtil.isPhoneNum(custMobile)) {
			validateMsg = "错误的手机号";
			autoPublish.setValidatedMsg(validateMsg);
			System.out.println(JSON.toJSONString(autoPublish));
		} else {
			System.out.println("校验ok");
		}

	}

	@Test
	public void test210() {
		String prefix = "Budget_Accounts_department";
		List<String> s = EnumUtil.getAllValueByEnumPrefix(prefix);
		System.out.println(RepoUtil.convertList2SqlInParam(s));
	}

	@Test
	public void test209() {
		// RepoUtil.getCountBudgetAccoutsCouponDefSql();

		Pager p = new Pager();
		BudgetAccountsQuery query = new BudgetAccountsQuery();

		query.setId(144l);
		query.setBudgetQuotaDepartmentCode("010201");

		// query.setAuditMonth("201812");
		query.setBudgetStatus(Budget_Accounts_status_enable.getCode());
		query.setDepartmentCode(EnumUtil.getKeyByDesc(Budget_Accounts_department_headquarter.getDesc()));
		query.setChannelCode(EnumUtil.getKeyByDesc(Budget_Accounts_channel_baby.getDesc()));
		query.setBudgetName("预算名");

		System.out.println(JSON.toJSONString(query));
		LoginUser u = getTestLoginUser("01");

		System.out.println(RepoUtil.getBudgetAccountsQuerySql(query, u, p));

	}

	@Test
	public void test208() {
		System.out.println(RepoUtil.getSubQueryCnNameByLoginId("MABA.UPDATE_NAME", "updated_by"));

		System.out.println(RepoUtil.getSubQueryCnNameByLoginId("MABA.CREATE_NAME", "created_by"));

		System.out.println(RepoUtil.decodeFieldCnNameByConfigEnumReadDescAsKeyValue("MABA.DEPARTMENT_CODE",
				"budget_department_code_name", "Budget_Accounts_department"));

		System.out.println(RepoUtil.decodeFieldCnNameByConfigEnum("MABA.BUDGET_STATUS", "BUDGET_STATUS_name",
				"Budget_Accounts_status"));

		// 读取desc作为key-value
		System.out.println(RepoUtil.decodeFieldCnNameByConfigEnumReadDescAsKeyValue("MABC.CHANNEL_CODE",
				"channel_code_name", "Budget_Accounts_channel"));
	}

	@Test
	public void test207() {
		RepoUtil.getCountBudgetAccoutsCouponDefSql();

	}

	@Test
	public void test206() {
		// RepoUtil.getCountBudgetAccoutsCouponDefSql();

		Pager p = new Pager();
		BudgetAccounts query = new BudgetAccounts();

		System.out.println(JSON.toJSONString(query));
		query.setAuditMonth("201812");
		query.setDepartmentCode("01");
		LoginUser u = getTestLoginUser("01");

		System.out.println(JSON.toJSONString(query));
		System.out.println(RepoUtil.getBudgetAccountsQuerySql2(query, u, p));

	}

	@Test
	public void test205() {
		List<String> codes = Arrays.asList("1", "2", "3", "4");
		List<String> codesall = Arrays.asList("1", "2", "3", "5", "6");

		List<String> not = BeanUtil.getNotListFromYesList(codes, codesall);
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

		// query.setCouponDefIds("10325300");
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
