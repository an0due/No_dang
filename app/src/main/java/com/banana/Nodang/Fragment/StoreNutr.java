package com.banana.Nodang.Fragment;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class StoreNutr {
    public String productName, cont1, cont2, cont3, cont4, cont5, cont6, cont7, cont8, cont9;

    public StoreNutr() { }
    public StoreNutr(String productName, String cont1, String cont2, String cont3, String cont4, String cont5, String cont6, String cont7, String cont8, String cont9) {
        this.productName = productName;
        this.cont1 = cont1; // 칼로리
        this.cont2 = cont2; // 탄수화물
        this.cont3 = cont3; // 단백질
        this.cont4 = cont4; // 지방
        this.cont5 = cont5; // 당류
        this.cont6 = cont6; // 나트륨
        this.cont7 = cont7; // 콜레스테롤
        this.cont8 = cont8;  // 포화지방산
        this.cont9 = cont9; // 트랜스지방
    }

    public void setProductName(String productName) {this.productName = productName;}
    public void setCont1(String cont1) {
        this.cont1 = cont1;
    }
    public void setCont2(String cont2) {
        this.cont2 = cont2;
    }
    public void setCont3(String cont3) { this.cont3 = cont3; }
    public void setCont4(String cont4) { this.cont4 = cont4; }
    public void setCont5(String cont5) {
        this.cont5 = cont5;
    }
    public void setCont6(String cont6) {
        this.cont6 = cont6;
    }
    public void setCont7(String cont7) {
        this.cont7 = cont7;
    }
    public void setCont8(String cont8) {
        this.cont8 = cont8;
    }
    public void setCont9(String cont9) {
        this.cont9 = cont9;
    }

    public String getProductName() {
        return productName;
    }
    public String getCont1() {
        return cont1;
    }
    public String getCont2() {
        return cont2;
    }
    public String getCont3() {
        return cont3;
    }
    public String getCont4() {
        return cont4;
    }
    public String getCont5() {
        return cont5;
    }
    public String getCont6() {
        return cont6;
    }
    public String getCont7() {
        return cont7;
    }
    public String getCont8() {
        return cont8;
    }
    public String getCont9() {
        return cont9;
    }

}
