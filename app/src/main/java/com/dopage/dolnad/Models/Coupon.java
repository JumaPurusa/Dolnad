package com.dopage.dolnad.Models;

import com.google.gson.annotations.SerializedName;

public class Coupon {

    @SerializedName("id")
    private int id;

    @SerializedName("linkage_code")
    private String linkage_code;

    @SerializedName("linker")
    private String linker;

    @SerializedName("linked")
    private String linked;

    @SerializedName("linked_id")
    private int linked_id;

    @SerializedName("isLinked")
    private int isLinked;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() {
        return id;
    }

    public String getLinkage_code() {
        return linkage_code;
    }

    public String getLinker() {
        return linker;
    }

    public String getLinked() {
        return linked;
    }

    public int getLinked_id() {
        return linked_id;
    }

    public int getIsLinked() {
        return isLinked;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
