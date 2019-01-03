# 优惠劵关联销售报表



功能：

* 使用明细 ， 关联销售报表

  备注 涉及表gc5\_coupon , GC5\_CUSTOMER\_CAMPAIGN\_DETAILS , GC5\_CONSUME\_PRODUCT\_GIFT

#### Url

`/b/coupon/q/scene1/CouponResult.action`

#### Method

`POST`

#### URL Params

#### Post Data Params

| 查询参数field | type | desc |
| :--- | :--- | :--- |
| pageNo | string | 分页，第几页 |
| buyJoinTimeStart | String | 用券开始时间 |
| buyJoinTimeEnd | String | 用券结束时间 |
| usedTerminalCode | String | 用券终端编码 |
| usedTerminalChannelCode | String | 用券渠道 |
| usedTerminalOfficeCode | String | 用券办事处 |
| usedTerminalAreaCode | String | 用券大区 |
| useStatus | int | 券定义状态 ， -1代表全部 |
| couponDefIds | String | 优惠券id |
| campaignGroupId | Long | 活动ID |
| couponType | String | 优惠券类型 |
| couponCode | String | 优惠券编码 |

| 报表field | type | desc |
| :--- | :--- | :--- |
| campaignGroupName | String | 活动组名称 |
| couponDefId | Long | 优惠券ID |
| couponTitle | String | 优惠券标题 |
| couponTypeName | String | 券类型\(中文\) |
| areaName | String | 用券的所属大区\(中文\) |
| officeName | String | 用券的所属办事处\(中文\) |
| channelName | String | 用券的所属渠道\(中文\) |
| terminalCode | String | 用券终端编码 |
| terminalCodelName | String | 用券终端名称 |
| chainTerminalType | String | 用劵终端连锁类型 |
| chainTerminalCode | String | 用劵终端一级连锁编号 |
| chainTerminalCodeName | String | 用劵终端一级连锁名称 |
| buyJoinTimeStr | String | 用券时间 |
| customerId | Long | 会员ID |
| custName | String | 会员姓名 |
| custMobile | String | 会员手机 |
| buyProductName | String | 用券购买产品名称 |
| buyProductSkuNum | String | 用券购买产品数量 |

#### Permisstion

#### Validate

#### Response

| code | desc |
| :--- | :--- |
| 200 | 成功 |

**Notes**

#### 场景

**Request**

`post /b/coupon/q/scene1/CouponResult.action?loginId=2929&pageNo=1`

**post data**

```text

```

**Response**

```text

```

**Notes**

201807R

* 新增报表

