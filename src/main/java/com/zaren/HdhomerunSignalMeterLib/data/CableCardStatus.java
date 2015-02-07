package com.zaren.HdhomerunSignalMeterLib.data;

public class CableCardStatus
{
    public static final String READY = "ready";
    public static final String NONE = "none";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String OOB_WEAK = "weak";

    private String mCard = "none";
    private String mAuth = "failure";
    private String mOob = "failure";
    private String mAct = "failure";

    public String getCard()
    {
        return mCard;
    }

    public void setCard( String aCard )
    {
        mCard = aCard;
    }

    public String getAuth()
    {
        return mAuth;
    }

    public void setAuth( String aAuth )
    {
        mAuth = aAuth;
    }

    public String getOob()
    {
        return mOob;
    }

    public void setOob( String aOob )
    {
        mOob = aOob;
    }

    public String getVal()
    {
        return mAct;
    }

    public void setVal( String aVal )
    {
        mAct = aVal;
    }

    @Override
    public String toString()
    {
        return "CableCardStatus [mCard=" + mCard + ", mAuth=" + mAuth + ", mOob=" + mOob + ", mVal=" + mAct + "]";
    }
}
