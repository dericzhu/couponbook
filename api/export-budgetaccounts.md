---
description: 预算科目汇总 -- 导出
---

# export-BudgetAccounts



功能：

* 费用报表 &gt; 预算科目汇总 &gt; 导出

#### Url

\` /b/coupon/finance/budget/export/report/BudgetAccounts.action

#### Method

`get`

#### URL Params

| get查询参数field | type | desc |
| :--- | :--- | :--- |
| pageNo | string | 分页，第几页 |

#### get Data Params

| get 查询参数field | type | desc |
| :--- | :--- | :--- |


| 返回field | type | desc |
| :--- | :--- | :--- |
| name | String | 预算科目\(中文名\) |
| balanceOfCurrentMonth | BigDecimal | 当月可用预算费用 = v1- v4 + v2 + v3\[f12=f1 - f10 + f7\] |
| budgetAccountQuota | BigDecimal | 预算科目的配额 v1 f1 |
| totalCouponDefBudgetLimit | BigDecimal | coupon def 统计budget\_limit v4 f10 |
| releasedBudgetAccountQuota | BigDecimal | 本月实时释放费用 = f4+f5+f6 |
| ongoingBudgetAccountQuota | BigDecimal | 待释放费用 f11 |
| releasedBalance | BigDecimal | v3 |
| expiredReleaseCouponExpense | BigDecimal | v2 |
| budgetAccountId | Long | MKT\_ACTIVITIES\_BUDGET\_ACCOUNTS表字段budget\_id |
| departmentCode | String | 大区办事处 |
| countCouponDef | int | coupon def 统计指定考核月有多少配券 |

#### Permisstion

#### Validate

#### Response

| code | desc |
| :--- | :--- |
| 200 | 成功 |

**Notes**

#### 场景

**Request**

`get /b/coupon/finance/budget/export/report/BudgetAccounts.action?loginId=2929&pageNo=1`

**post data**

```text

```

**Response**

```text

```

**Notes**

201812R

* 新增

