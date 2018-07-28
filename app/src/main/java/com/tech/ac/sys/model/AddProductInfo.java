package com.tech.ac.sys.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vicky on 20-Sep-17.
 */

public class AddProductInfo implements Serializable {

    public String UId;
    public String ProductId;
    public String productImg;
    public String productTitle;
    public String productDescription;
    public String productFullDetails;
    public String timeStamp;
    public HashMap<String,LikeInfo>like_table = new HashMap<>();

    public AddProductInfo() {
    }


}
