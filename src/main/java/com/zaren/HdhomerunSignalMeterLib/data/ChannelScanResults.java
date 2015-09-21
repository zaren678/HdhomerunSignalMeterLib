
package com.zaren.HdhomerunSignalMeterLib.data;

import java.util.ArrayList;

import timber.log.Timber;

public class ChannelScanResults{
    public String channelString;
    protected int channelMap;
    protected int frequency;
    public TunerStatus tunerStatus;
    protected int programCount;
    public ArrayList< ChannelScanProgram > programs;
    boolean transportStreamIdDetected;
    int transportStreamId;

    /**
     *
     */
    public ChannelScanResults(){
        programs = new ArrayList< ChannelScanProgram >();
        tunerStatus = new TunerStatus();
    }

    public void setPrimitiveFields( String channelString,
                                    int channelMap,
                                    int frequency,
                                    int programCount,
                                    boolean transportStreamIdDetected,
                                    int transportStreamId ){
        Timber.d( "ChannelScanResuts channel " + channelString + " program count:" + programCount );
        this.channelString = channelString;
        this.channelMap = channelMap;
        this.frequency = frequency;
        this.programCount = programCount;
        this.transportStreamIdDetected = transportStreamIdDetected;
        this.transportStreamId = transportStreamId;
    }

    public void setTunerStatus( String channel,
                                String lockStr,
                                boolean signalPresent,
                                boolean lockSupported,
                                boolean lockUnsupported,
                                long signalStrength,
                                long snrQuality,
                                long symbolErrorQuality,
                                long rawBitsPerSecond,
                                long packetsPerSecond,
                                int returnStatus ){
        this.tunerStatus.setAllFields( channel,
                lockStr,
                signalPresent,
                lockSupported,
                lockUnsupported,
                signalStrength,
                snrQuality,
                symbolErrorQuality,
                rawBitsPerSecond,
                packetsPerSecond,
                returnStatus );
    }

    public void setProgram( int index,
                            String programString,
                            int programNumber,
                            int virtualMajor,
                            int virtualMinor,
                            String type,
                            String name ){

        Timber.d( "Adding Program[" + index + "]: " + programString );
        programs.add( index, new ChannelScanProgram() );

        programs.get( index ).setAllFields( programString,
                programNumber,
                virtualMajor,
                virtualMinor,
                type,
                name );
    }
}
