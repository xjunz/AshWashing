package com.ashwashing.pro.api.bean;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.util.UniUtils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AccountInfo implements Serializable {

    private static final String NO_SUCH_THING = "no-such-thing";
    private static final String NAME_ACCOUNT_INFO = "account_info";
    private static AccountInfo sMyAccount;

    private static RegisterInfo sRegisterInfo;

    public int AccID;
  /*  public int GroupID;
    public int PrjID;
*/
    private String access_token = NO_SUCH_THING;
    @SerializedName("UserName")
    private String username;
    @SerializedName("TelPhone")
    private String phone;
    @SerializedName("PrjName")
    private String academy;
    @SerializedName("VipTime")
    private String sbpDueDate;


    public String getRegisterTime() {
        return registerTime;
    }

    @SerializedName("RegisterTime")
    private String registerTime;
    private int avatarIndex;

    @DrawableRes

    private int avatarRes;

    public String getUsername() {
        return username;
    }

    public String getPhone() {
        return phone;
    }

    public String getAcademy() {
        return academy;
    }

    public int getAvatarRes() {
        if (avatarRes == 0 && avatarIndex != 0) {
            this.avatarRes = AshApp.APP_CONTEXT.get()
                    .getResources()
                    .getIdentifier("avatar_" + avatarIndex, "mipmap", AshApp.APP_CONTEXT.get().getPackageName());
        }
        return avatarRes;
    }

    public boolean isSubscriptionDue() {
        return TextUtils.isEmpty(sbpDueDate);
    }

    public String getSubscriptionDueDate() {
        return sbpDueDate == null ? "" : sbpDueDate;
    }


    public static AccountInfo mine() {
        if (sMyAccount == null) {
            sMyAccount = AshApp.getSerializedObjManager().read(NAME_ACCOUNT_INFO, AccountInfo.class);
            if (sMyAccount == null) {
                sMyAccount = new AccountInfo();
                sMyAccount.access_token = NO_SUCH_THING;
            }
        }
        return sMyAccount;
    }

    public void generateAvatar() {
        this.avatarIndex = UniUtils.random(1, 8);
    }

    public String getAccessToken() {
        return access_token;
    }

    public void set(AccountInfo info) {
        String token = sMyAccount.access_token;
        int avatarIndex = sMyAccount.avatarIndex;
        sMyAccount = info;
        if (info.access_token == null || info.access_token.equals(NO_SUCH_THING)) {
            sMyAccount.access_token = token;
        }
        sMyAccount.avatarIndex = avatarIndex;
    }

    public boolean exists() {
        return access_token != null && !access_token.equals(NO_SUCH_THING);
    }


    public void invalidate() {
        sMyAccount = new AccountInfo();
        sMyAccount.access_token = NO_SUCH_THING;
        AshApp.getSerializedObjManager().delete(NAME_ACCOUNT_INFO);
    }

    public void persist() {
        AshApp.getSerializedObjManager().write(sMyAccount, NAME_ACCOUNT_INFO);
    }

    public static RegisterInfo getRegisterInfo() {
        if (sRegisterInfo == null) {
            sRegisterInfo = new RegisterInfo();
        }
        return sRegisterInfo;
    }

    public static class RegisterInfo {
        public int userType;
        public String phone, password;
        public String username, accountPwd;
    }

}
