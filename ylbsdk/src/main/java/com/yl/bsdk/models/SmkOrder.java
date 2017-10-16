package com.yl.bsdk.models;


import java.io.Serializable;

/**
 * 市民卡订单信息
 */
public class SmkOrder implements Serializable {

    /** 订单编号 */
    private String orderNo;
    /** 商户编号 */
    private String merCode;
    /** 商户签名 */
    private String mersign;
    /** 门店编号 */
    private String storeid;
    /** 商品信息 */
    private String goods;
    /** 交易金额 */
    private String amount;
    /** 交易类型： PSRC：充值 ；PSTX：消费 ；TXDB：担保支付 */
    private String mertxtypeid;
    /** 被充值卡号 */
    private String cardnumber;
    /** 交易时间 */
    private String dateTime;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMerCode() {
        return merCode;
    }

    public void setMerCode(String merCode) {
        this.merCode = merCode;
    }

    public String getMersign() {
        return mersign;
    }

    public void setMersign(String mersign) {
        this.mersign = mersign;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMertxtypeid() {
        return mertxtypeid;
    }

    public void setMertxtypeid(String mertxtypeid) {
        this.mertxtypeid = mertxtypeid;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}