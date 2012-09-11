package com.zaren.HdhomerunSignalMeterLib.data;

//A Helper class because you can't set a string passed in as an argument in JNI code...
public class JniString
{
   private String string = "";

   /**
    * @param string
    */
   public JniString(String string)
   {
      super();
      this.string = string;
   }

   /**
    * 
    */
   public JniString()
   {
      super();
   }

   /**
    * @return the string
    */
   public String getString()
   {
      return string;
   }

   /**
    * @param string the string to set
    */
   public void setString(String string)
   {
      this.string = string;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return getString();
   }
}
