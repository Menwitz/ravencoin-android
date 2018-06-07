package com.platform.entities;

import static android.R.attr.name;

public class WalletInfo {

    /**WalletInfo:

     Key: “wallet-info”

     {
        “classVersion”: 2, //used for versioning the schema
        “creationDate”: 123475859, //Unix timestamp
        “name”: “My Bread”,
        “currentCurrency”: “USD”
     }
     */

    public int classVersion;
    public int creationDate;
    public String name;

}
