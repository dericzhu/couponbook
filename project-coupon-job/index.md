---
description: 定时任务部署
---

# index



```text
 登录运维管理平台
http://yw.biostime.com/
项目名字：集团优惠券WEB服务-定时任务
```

**web服务器（glassfish4.1.1\[docker\]）**

```text
生产服务器：
192.168.2.10
预发布服务器：
192.168.2.93

通过全局变量key=PUBLISH_COUPON_V5_SERVER_IP， 可以切换在跑任务的服务器
```

#### version

current version

* 2.0.5

   5.7.2-SNAPSHOT&lt;/coupon-web-api.version&gt;

* 2.0.4

   5.6.6-SNAPSHOT&lt;/coupon-web-api.version&gt;

历史版本

* 2.0.3

   5.6.5-SNAPSHOT&lt;/coupon-web-api.version&gt;

* 2.0.2

   5.6.4-SNAPSHOT&lt;/coupon-web-api.version&gt;

  * 2.0.1 

    5.6.3-SNAPSHOT&lt;/coupon-web-api.version&gt;

#### dev-svn

svn://svn.biostime.com/development/coupon/coupon-job/branches/coupon-job

#### trunk-svn

svn://svn.biostime.com/development/coupon/coupon-job/trunk/【current version】/prd/coupon-job

#### dependency

```text
<coupon-web-api.version>5.6.3-SNAPSHOT</coupon-web-api.version>
    <!-- coupon-web-api -->
    <dependency>
        <groupId>com.mama100</groupId>
        <version>${coupon-web-api.version}</version>
        <artifactId>coupon-biostime-web</artifactId>
    </dependency>
```

#### 后台监控

**测试环境**

root/root [http://test-01.biostime.us:8089/\#](http://test-01.biostime.us:8089/#)

**生产环境**

guest/guest [http://mkt.biostime.com:8083/](http://mkt.biostime.com:8083/)

#### memo

* 201812R

新增任务IssuseSwisseCouponByCouponDefinitionTplJob

* 201807R

  增加任务CountCouponIssuedAmountJob

