/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author C.Vargas
 */
import java.sql.Timestamp;

public class Cupon {
    private int id;
    private String coupon_name;
    private String coupon_desc;
    private int discount;
    private String validity;
    private int status;
    private int client_id;

    // Constructor

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getCoupon_desc() {
        return coupon_desc;
    }

    public void setCoupon_desc(String coupon_desc) {
        this.coupon_desc = coupon_desc;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    // Método toString (opcional)
//    @Override
//    public String toString() {
//        return "Cupon{" +
//                "id=" + id +
//                ", coupon_name='" + coupon_name + '\'' +
//                ", coupon_desc='" + coupon_desc + '\'' +
//                ", discount=" + discount +
//                ", validity='" + validity + '\'' +
//                ", status=" + status +
//                ", client_id=" + client_id +
//                '}';
//    }
}
