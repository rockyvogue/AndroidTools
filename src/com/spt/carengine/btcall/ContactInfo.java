
package com.spt.carengine.btcall;

//RecordInfo类 ，用来存储通话记录 
public class ContactInfo {

    private String name = "";
    private String number = "";
    private String date = "";
    private String calltype = "";
    private long contactId;

    public ContactInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String sName) {
        this.name = sName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String sNumber) {
        this.number = sNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String sDate) {
        this.date = sDate;
    }

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String scalltype) {
        this.calltype = scalltype;
    }

    public void setContactId(long ncontactId) {
        this.contactId = ncontactId;
    }

    public long getContactId() {
        return contactId;
    }
}
