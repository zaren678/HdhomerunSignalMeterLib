package com.zaren.HdhomerunSignalMeterLib.data;

import com.zaren.HdhomerunSignalMeterLib.util.ErrorHandler;
import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;
import com.zaren.HdhomerunSignalMeterLib.util.Utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class HdhomerunDevice implements Serializable
{
    private static final long serialVersionUID = 1942906208628106963L;
    public static final String DEVICE_CABLECARD = "cablecard";
    public static final String DEVICE_ATSC = "atsc";
    public static final String DEVICE_TC_ATSC = "tc_atsc";

    //reset constants
    public static final String SELF = "self";
    public static final String CABLECARD = "cablecard";


    private int cPointer;
    private long deviceId;
    private int ipAddr;
    private int tuner;
    private String[] channelMaps;
    private String[] transcodeProfiles;
    private String deviceName;
    private TunerStatus prevTunerStatus;
    private String prevChannelMap;
    private String deviceType;
    private transient ChannelList channelList = new ChannelList(); //this is transient because it is not serializable

    /*
     * this is used to load the native library on application startup. The
     * library has already been unpacked into /data/data/PROJECT/lib/C-FILE.so at
     * installation time by the package manager.
     */
    static
    {
        System.loadLibrary( "hdhomerun" );
    }

    public HdhomerunDevice( long deviceId_val, int ipAddr_val, int tuner_val ) throws HdhomerunCommErrorException
    {
        String supportedString;

        this.deviceId = deviceId_val;
        this.ipAddr = ipAddr_val;
        this.tuner = tuner_val;

        cPointer = JNIcreateNewDevice( deviceId_val, ipAddr_val, tuner_val );

        deviceName = Long.toHexString( deviceId_val ) + "-" + tuner_val;

        HDHomerunLogger.d( "Device created id:" + deviceName );

        // Now Lets collect the supported channelmaps
        supportedString = JNIgetSupported( cPointer );
        HDHomerunLogger.d( supportedString );
        StringTokenizer st = new StringTokenizer( supportedString, "\n" );

        while( st.hasMoreTokens() == true )
        {
            processSupportedString( st.nextToken() );
        }

        //now the device type
        String deviceModel = getModel();

        HDHomerunLogger.d( "Device model " + deviceModel );

        if( deviceModel.contains( "cablecard" ) )
        {
            deviceType = DEVICE_CABLECARD;
        }
        else if( deviceModel.equals( "hdhomeruntc_atsc" ) )
        {
            deviceType = DEVICE_TC_ATSC;
        }
        else
        {
            deviceType = DEVICE_ATSC;
        }

        HDHomerunLogger.d( "Device type " + deviceType );
    }

    private void processSupportedString( String token )
    {
        StringTokenizer st = new StringTokenizer( token );

        // need at least two tokens, one for the key and 1 for the value, if not
        // at least 2 its worthless
        if( st.countTokens() > 1 )
        {
            String key = st.nextToken();

            if( key.equals( "channelmap:" ) )
            {
                int i = 0; // just a counter
                channelMaps = new String[ st.countTokens() ];
                while( st.hasMoreTokens() == true )
                {
                    channelMaps[ i ] = st.nextToken();
                    i++;
                }
            }
            else if( key.equals( "modulation:" ) )
            {
                // don't care about this yet
            }
            else if( key.equals( "auto-modulation:" ) )
            {
                // don't care about this yet
            }
            else if( key.equals( "transcode:" ) )
            {
                int i = 0; // just a counter
                transcodeProfiles = new String[ st.countTokens() ];
                while( st.hasMoreTokens() == true )
                {
                    transcodeProfiles[ i ] = st.nextToken();
                    i++;
                }
            }
            else
            {
                HDHomerunLogger.w( "Unhandled token type: " + key );
            }
        }
    }

    public void destroy()
    {
        HDHomerunLogger.d( "Destroying device " + deviceName );
        JNIdestroy( cPointer );
        cPointer = -1;
    }

    synchronized public int setChannelMap( String channelMap )
    {
        HDHomerunLogger.d( "Set ChannelMap to " + channelMap + " for device " + deviceName );
        int status = JNIsetChannelMap( this.cPointer, channelMap );

        //TODO need to recalc channellist

        return status;
    }

    private native int JNIgetTunerChannel( int aCPointer, JniString aChannel );

    public int getTunerChannel( JniString aChannel )
    {
        int theRetVal = JNIgetTunerChannel( cPointer, aChannel );

        HDHomerunLogger.d( "getTunerChannel: return val " + theRetVal + " channel: " + aChannel.getString() );

        return theRetVal;
    }

    synchronized public int setTunerChannel( String channel )
    {
        HDHomerunLogger.d( "Set Channel to " + channel + " for device " + deviceName );
        int status;

        status = JNIsetTunerChannel( cPointer, channel );


        //TODO for some reason this is crashing when we hit the end of a channel scan for the cable channelmaps
        //checkForError(status, "SetTunerChannel");

        if( status == -1 )
        {
            // network error, we need to give up
        }

        return status;
    }

    synchronized public TunerStatus getTunerStatus()
    {
        TunerStatus tunerStatus = JNIgetTunerStatus( cPointer );

        boolean error = checkForError( tunerStatus.returnStatus, "GetTunerStatus" );

        if( error == false )
        {
            prevTunerStatus = tunerStatus;
            return tunerStatus;
        }
        else
        {
            return prevTunerStatus;
        }
    }

    public synchronized int updateTunerStatus( TunerStatus tunerStatus )
    {
        int status = JNIupdateTunerStatus( cPointer, tunerStatus );

        checkForError( status, "UpdateTunerStatus" );

        return status;
    }

    private synchronized native int JNIcreateNewDevice( long deviceId, long ipAddr, int tuner );

    private synchronized native int JNIsetChannelMap( int cPointer, String channelMap );

    private synchronized native int JNIsetTunerChannel( int cPointer, String channel );

    private synchronized native int JNIsetTunerVChannel( int cPointer, String channel );

    private synchronized native int JNIwaitForLock( int cPointer, TunerStatus tunerStatus );

    synchronized public int waitForLock( TunerStatus tunerStatus )
    {
        int retVal = JNIwaitForLock( cPointer, tunerStatus );

        ErrorHandler.HandleError( retVal, "Wait for Lock" );

        return retVal;
    }

    private synchronized native int JNIgetTunerStreamInfo( int cPointer, JniString streamInfo );

    synchronized public int getTunerStreamInfo( ProgramsList thePrograms )
    {
        JniString streamInfo = new JniString();
        int retVal = JNIgetTunerStreamInfo( cPointer, streamInfo );

        ErrorHandler.HandleError( retVal, "Get Tuner Stream Info" );

        if( retVal > 0 )
        {
            convertStreamInfoToPrograms( streamInfo.getString(), thePrograms );
        }

        return retVal;
    }

    private void convertStreamInfoToPrograms( String streamInfo, ProgramsList thePrograms )
    {
        StringTokenizer theProgramStrings = new StringTokenizer( streamInfo, "\n" );

        while( theProgramStrings.hasMoreTokens() )
        {
            try
            {
                String theProgramString = "";

                if( theProgramStrings.hasMoreTokens() )
                {
                    theProgramString = theProgramStrings.nextToken();
                }

                HDHomerunLogger.d( "Parsing program string: " + theProgramString );

                StringTokenizer theProgNumAndName = new StringTokenizer( theProgramString, ":" );

                ChannelScanProgram theProgram = new ChannelScanProgram();
                theProgram.programString = theProgramString;

                while( theProgNumAndName.hasMoreTokens() )
                {
                    theProgram.programNumber = Integer.parseInt( theProgNumAndName.nextToken() );

                    processProgramName( theProgNumAndName.nextToken(), theProgram );
                }

                thePrograms.append( theProgram.programNumber, theProgram );
            }
            catch( NumberFormatException e )
            {
                HDHomerunLogger.e( "Error Parsing String: " + e );
            }
            catch( NoSuchElementException e )
            {
                HDHomerunLogger.e( "NoSuchElementException: " + e );
            }
        }
    }

    private static final String[] theTypes = new String[]{ "control", "encrypted", "no data", "internet" };

    private void processProgramName( String aProgramName, ChannelScanProgram aProgram )
    {
        int theOpeningParen = aProgramName.indexOf( "(" );
        int theClosingParen = aProgramName.indexOf( ")" );

        String theJustProgramName = aProgramName;

        if( theOpeningParen != -1 && theClosingParen != -1 )
        {
            //This is the string that says "control" or "encrypted" or "no data"
            String theTypeString = aProgramName.substring( theOpeningParen + 1, theClosingParen );
            List<String> theTypeStrings = Arrays.asList( theTypes );

            if( theTypeStrings.contains( theTypeString ) )
            {
                aProgram.type = theTypeString;
                theJustProgramName = aProgramName.substring( 0, theOpeningParen );
            }
        }

        StringTokenizer theStrings = new StringTokenizer( theJustProgramName, " " );
        String theChannelNumbers = theStrings.nextToken();
        StringTokenizer theChannelStrings = new StringTokenizer( theChannelNumbers, "." );
        aProgram.virtualMajor = Integer.parseInt( theChannelStrings.nextToken() );

        if( theChannelStrings.hasMoreTokens() )
        {
            aProgram.virtualMinor = Integer.parseInt( theChannelStrings.nextToken() );
        }

        StringBuilder theNameStringBuilder = new StringBuilder();
        while( theStrings.hasMoreTokens() )
        {
            String theString = theStrings.nextToken();

            if( theNameStringBuilder.length() > 0 )
            {
                theNameStringBuilder.append( " " );
            }
            theNameStringBuilder.append( theString );
        }

        aProgram.name = theNameStringBuilder.toString();
    }

    private synchronized native TunerStatus JNIgetTunerStatus( int cPointer );

    private synchronized native String JNIgetSupported( int cPointer );

    private synchronized native void JNIdestroy( int cPointer );

    private synchronized native String JNIgetChannelMap( int cPointer );

    private synchronized native int JNIupdateTunerStatus( int cPointer, TunerStatus tunerStatus );

    private synchronized native int JNIupdateTunerVStatus( int cPointer, TunerVStatus tunerVStatus );

    public int updateTunerVStatus( TunerVStatus tunerVStatus )
    {
        int status = JNIupdateTunerVStatus( cPointer, tunerVStatus );

        checkForError( status, "UpdateTunerVStatus" );

        return status;
    }

    private synchronized native TunerVStatus JNIgetTunerVStatus( int cPointer );

    public TunerVStatus getTunerVStatus()
    {
        TunerVStatus tunerVStatus = JNIgetTunerVStatus( cPointer );
        return tunerVStatus;
    }

    private synchronized native String JNIgetModel( int cPointer );

    public String getModel() throws HdhomerunCommErrorException
    {
        String retVal = JNIgetModel( cPointer );

        if( retVal == null )
        {
            throw new HdhomerunCommErrorException( "Failed to get model information" );
        }

        return retVal;
    }

    private synchronized native int JNIcreateChannelList( String channelMap, ChannelList channelList );

    public int createChannelList( String channelMap, ChannelList channelList )
    {
        int status = JNIcreateChannelList( channelMap, channelList );
        this.channelList = channelList;
        return status;
    }

    /**
     * @return the channelMaps
     */
    public String[] getChannelMaps()
    {
        return channelMaps;
    }

    /**
     * @return the transcode profiles
     */
    public String[] getTranscodeProfiles()
    {
        return transcodeProfiles;
    }

    synchronized public String getCurrentChannelMap()
    {
        String channelMap = JNIgetChannelMap( cPointer );

        boolean error = checkForError( channelMap, "GetCurrentChannelMap" );

        if( error == false )
        {
            prevChannelMap = channelMap;
            return channelMap;
        }
        else
        {
            return prevChannelMap;
        }
    }

    private boolean checkForError( int returnStatus, String message )
    {
        //ErrorHandler.HandleError(returnStatus, message);
        if( returnStatus == 0 )
        {
            return true;
        }
        else if( returnStatus == -1 )
        {
            return true;
        }
        return false;
    }

    private boolean checkForError( String returnString, String message )
    {
        if( returnString.equals( "rejected" ) )
        {
            //ErrorHandler.HandleError(0, message);
            return true;
        }
        else if( returnString.equals( "Network failure" ) )
        {
            //ErrorHandler.HandleError(-1, message);
            return true;
        }
        return false;
    }

    public int getCurrentChannel()
    {
        TunerStatus tunerStatus = getTunerStatus();

        String channelString = tunerStatus.channel;

        if( channelString.equals( "none" ) )
        {
            return -1;
        }
        else
        {
            String[] splitString = channelString.split( ":" );
            return Integer.parseInt( splitString[ 1 ] );
        }
    }

    String buildChannelStringFromTunerStatus( String channel, String lockStr )
    {
        HDHomerunLogger.v( "BuildChannelString: " + channel + " " + lockStr );
        if( channel.equals( "none" ) )
        {
            return channel;
        }
        else
        {
            String[] splitString = channel.split( ":" );
            if( splitString.length > 1 )
            {
                int channel_int = Integer.parseInt( splitString[ 1 ] );
                if( channel_int > 1000 )
                {
                    //this must be a frequency value
                    channel_int = frequencyToChannelNumber( channel_int );

                    if( channel_int == 0 )
                    {
                        //for some reason we couldn't match up the frequency to channel number, just return the frequency
                        return lockStr + ":" + splitString[ 1 ];
                    }

                    return lockStr + ":" + channel_int;
                }
                else
                {
                    return lockStr + ":" + splitString[ 1 ];
                }
            }
            else
            {
                //something was wrong with the channel from the device
                return "none";
            }
        }
    }

    protected String getChannelNumberFromChannelString( String channelString )
    {
        HDHomerunLogger.v( "getChannelNumberFromChannelString(): " + channelString );
        if( channelString.equals( "none" ) )
        {
            return "";
        }

        //first split on a comma, sometimes the channelString comes back as two different channelmaps, not sure why
        String channelMapstrings[] = channelString.split( "," );

        //just use the first one
        String strings[] = channelMapstrings[ 0 ].split( ":" );

        //if it didn't return more than two results that means for some reason we don't have a channel number
        if( strings.length < 2 )
        {
            return "";
        }

        int channelInt = Integer.parseInt( strings[ 1 ] );

        if( channelInt > 1000 )
        {
            //this must be a frequency value
            int convertedChannelInt = frequencyToChannelNumber( channelInt );

            if( convertedChannelInt == 0 )
            {
                //for some reason we couldn't match up the frequency to channel number, just return the frequency
                return "" + channelInt;
            }

            return "" + convertedChannelInt;
        }
        else
        {
            return "" + channelInt;
        }
    }

    public int frequencyToChannelNumber( int frequency )
    {
        return channelList.frequencyToNumber( frequency );
    }

    /**
     * @return the cPointer
     */
    public int getcPointer()
    {
        return cPointer;
    }

    /**
     * @return the deviceName
     */
    public String getDeviceName()
    {
        return deviceName;
    }

    /**
     * @return the deviceId
     */
    public long getDeviceId()
    {
        return deviceId;
    }

    /**
     * @return the ipAddr
     */
    public int getIpAddr()
    {
        return ipAddr;
    }

    /**
     * @return the ipAddr as a byte array
     */
    public byte[] getIpAddrArray()
    {
        return Utils.HdHrIpAddressToByteArray( ipAddr );
    }

    /**
     * @return the tuner
     */
    public int getTuner()
    {
        return tuner;
    }

    /**
     * @return the deviceType
     */
    public String getDeviceType()
    {
        return deviceType;
    }

    public int setTunerVChannel( String channel )
    {
        HDHomerunLogger.d( "Set Virtual Channel to " + channel + " for device " + deviceName );
        int status;

        status = JNIsetTunerVChannel( cPointer, channel );

        //TODO for some reason this is crashing when we hit the end of a channel scan for the cable channelmaps
        //checkForError(status, "SetTunerChannel");

        if( status == -1 )
        {
            // network error, we need to give up
        }

        return status;
    }

    private native int JNIgetTunerVChannel( int aCPointer, JniString aVChannel );

    public int getTunerVChannel( JniString aVChannel )
    {
        int theRetVal = JNIgetTunerVChannel( cPointer, aVChannel );

        HDHomerunLogger.d( "getTunerVChannel: return val " + theRetVal + " program: " + aVChannel.getString() );

        return theRetVal;
    }

    private native String JNIgetFirmwareVersion( int cPointer2 );

    public String getFirmwareVersion()
    {
        return JNIgetFirmwareVersion( cPointer );
    }

    private native String JNIgetLockkeyOwner( int cPointer2 );

    public String getLockkeyOwner()
    {
        return JNIgetLockkeyOwner( cPointer );
    }

    private native String JNIgetTunerTarget( int cPointer2 );

    public String getTargetIp()
    {
        return JNIgetTunerTarget( cPointer );
    }

    private native int JNIsetVar( int cPointer, String var, String value );

    public int setVar( String var, String value )
    {
        return JNIsetVar( cPointer, var, value );
    }

    private native int JNIgetVar( int cPointer, String var, JniString aValue, JniString aError );

    public int getVar( String var, JniString aValue, JniString aError )
    {
        return JNIgetVar( cPointer, var, aValue, aError );
    }

    private native int JNItunerLockeyRequest( int cPointer, JniString error );

    public int tunerLockeyRequest( JniString error )
    {
        int retVal = JNItunerLockeyRequest( cPointer, error );

        HDHomerunLogger.d( "tunerLockeyRequest: return val " + retVal + " error: " + error.getString() );

        return retVal;
    }

    private native int JNItunerLockeyRelease( int cPointer );

    public int tunerLockeyRelease()
    {
        int retVal = JNItunerLockeyRelease( cPointer );

        HDHomerunLogger.d( "tunerLockeyRelease: return val " + retVal );

        return retVal;
    }

    private native int JNItunerLockeyForce( int cPointer );

    public int tunerLockeyForce()
    {
        int retVal = JNItunerLockeyForce( cPointer );

        HDHomerunLogger.d( "tunerLockeyForce: return val " + retVal );

        return retVal;
    }

    private native int JNIgetTunerProgram( int cPointer, JniString program );

    public int getTunerProgram( JniString program )
    {
        int retVal = JNIgetTunerProgram( cPointer, program );

        //HDHomerunLogger.d("getTunerProgram: return val " + retVal + " program: " + program.getString());

        return retVal;
    }

    private native int JNIsetTunerProgram( int cPointer, String progNumber );

    public int setTunerProgram( String progNumber )
    {
        int retVal = JNIsetTunerProgram( cPointer, progNumber );

        HDHomerunLogger.d( "setTunerProgram: return val " + retVal );

        return retVal;
    }

    private native int JNIsetTunerTarget( int cPointer, String targetString );

    public int setTargetIP( String targetString )
    {
        int retVal = JNIsetTunerTarget( cPointer, targetString );

        HDHomerunLogger.d( "setTargetIP: " + targetString + " return val " + retVal );

        return retVal;
    }

    private native void JNIstreamStop( int cPointer );

    public void stopStreaming()
    {
        HDHomerunLogger.d( "Device: stopStreaming" );
        JNIstreamStop( cPointer );
    }

    public CableCardStatus getCardStatus()
    {
        JniString theValue = new JniString();
        JniString theError = new JniString();

        int theStatus = getVar( "/card/status", theValue, theError );

        HDHomerunLogger.d( "getCardStatus: theStatus = " + theStatus + " theValue = " + theValue + " theError = " + theError );

        CableCardStatus theReturn = new CableCardStatus();

        if( theStatus == DeviceResponse.SUCCESS )
        {
            //process response
            processCardStatus( theValue, theReturn );
        }

        return theReturn;
    }

    private void processCardStatus( JniString aValue, CableCardStatus aReturn )
    {
        StringTokenizer theKeyValPairs = new StringTokenizer( aValue.getString(), " " );

        while( theKeyValPairs.hasMoreTokens() )
        {
            String theKeyVal = theKeyValPairs.nextToken();

            int theEqualsIndex = theKeyVal.indexOf( '=' );
            if( theEqualsIndex != -1 )
            {
                String theKey = theKeyVal.substring( 0, theEqualsIndex );
                String theVal = theKeyVal.substring( theEqualsIndex + 1, theKeyVal.length() );

                if( theKey.equals( "card" ) )
                {
                    aReturn.setCard( theVal );
                }
                else if( theKey.equals( "auth" ) )
                {
                    aReturn.setAuth( theVal );
                }
                else if( theKey.equals( "oob" ) )
                {
                    aReturn.setOob( theVal );
                }
                else if( theKey.equals( "val" ) )
                {
                    aReturn.setVal( theVal );
                }
            }
        }
    }

} //end class HdhomerunDevice
