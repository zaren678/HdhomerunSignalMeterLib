package com.zaren.HdhomerunSignalMeterLib.data;

import timber.log.Timber;

public class ChannelScanRunnable implements Runnable{
    private volatile boolean mIsForward;
    private volatile boolean mIsRunning;
    private HdhomerunDevice mDevice;
    private DeviceController mCntrl;
    private ChannelList mChannelList;
    private int mStartingChannel;
    private volatile boolean mFullChannelScan = false;

    /**
     * @param aIsForward
     * @param aChannelList
     */
    public ChannelScanRunnable( boolean aIsForward, DeviceController aCntrl, ChannelList aChannelList ){
        super();
        mIsForward = aIsForward;
        mDevice = aCntrl.getDevice();
        mCntrl = aCntrl;
        mChannelList = aChannelList;
        mStartingChannel = -1;

        mIsRunning = true;
    }

    public ChannelScanRunnable( boolean aIsForward, DeviceController aCntrl, ChannelList aChannelList, int aStartingChannel ){
        super();
        mIsForward = aIsForward;
        mDevice = aCntrl.getDevice();
        mCntrl = aCntrl;
        mChannelList = aChannelList;
        mStartingChannel = aStartingChannel;

        mIsRunning = true;
    }

    public ChannelScanRunnable( DeviceController aCntrl, ChannelList aChannelList ){
        super();
        mIsForward = true;
        mDevice = aCntrl.getDevice();
        mCntrl = aCntrl;
        mChannelList = aChannelList;
        mStartingChannel = aChannelList.getMinNumber() - 1;

        mFullChannelScan = true;

        mIsRunning = true;
    }

    @Override
    public void run(){
        int theCurrentChannel = mDevice.getCurrentChannel();
        if( mStartingChannel > -1 ){
            theCurrentChannel = mStartingChannel;
        }

        int theMinChannel = mChannelList.getMinNumber();
        int theMaxChannel = mChannelList.getMaxNumber();

        if( theCurrentChannel <= theMinChannel ){
            if( mIsForward ){
                if( theCurrentChannel == theMinChannel ){
                    theCurrentChannel = theMinChannel;
                } else //less than the minChannel
                {
                    theCurrentChannel = theMinChannel - 1; //minus one so the first +1 starts it in the right place
                }
            } else //can't scan below min
            {
                mIsRunning = false;
            }
        } else if( theCurrentChannel >= theMaxChannel ){
            if( mIsForward == false ){
                if( theCurrentChannel == theMaxChannel ){
                    theCurrentChannel = theMaxChannel;
                } else //greater than the maxChannel
                {
                    theCurrentChannel = theMaxChannel + 1; //plus one so the first -1 starts it in the right place
                }
            } else //can't scan above max
            {
                mIsRunning = false;
            }
        }

        int theStep;
        if( mIsForward == true ){
            theStep = 1;
        } else {
            theStep = -1;
        }

        boolean theTunerLocked = false;

        int theStatus = 1;
        DeviceResponse theResponse = new DeviceResponse( theStatus );

        try{
            JniString theError = new JniString();
            theResponse.setStatus( mDevice.tunerLockeyRequest( theError ) );

            if( theResponse.getStatus() != DeviceResponse.SUCCESS ){
                mCntrl.fillOutLockedResponse( theResponse );
                mIsRunning = false;
                return;
            }

            theTunerLocked = true;
            TunerStatus theTunerStatus = new TunerStatus();
            ProgramsList thePrograms = new ProgramsList();

            while( mIsRunning ){
                Timber.d( "Advancing channel" );

                theCurrentChannel = theCurrentChannel + theStep;

                theResponse.setStatus( mDevice.setTunerChannel( "auto:" + theCurrentChannel ) );
                Timber.d( "Status is " + theStatus + " for setTunerChannel" );
                mCntrl.notifyObserversChannelChanged( theResponse, theCurrentChannel );

                if( theResponse.getStatus() != DeviceResponse.SUCCESS ){
                    Timber.d( "Fail to set channel to auto:" + theCurrentChannel );

                    //this happens alot at the end of us cable channel scans, don't mark as error
                    theResponse.setStatus( DeviceResponse.SUCCESS );

                    if( checkForEnd( theCurrentChannel ) ){
                        return;
                    } else {
                        continue;
                    }
                }

                theResponse.setStatus( mDevice.waitForLock( theTunerStatus ) );
                Timber.d( "Lock Status " + theResponse.getStatus() + " lock_supported " + theTunerStatus.lockSupported );

                mCntrl.notifyObserversTunerStatus( theResponse, theTunerStatus, null );

                if( theTunerStatus.lockSupported ){
                    mCntrl.notifyChannelLocked( theTunerStatus );

                    thePrograms.clear();
                    mDevice.getTunerStreamInfo( thePrograms );

                    if( mCntrl.isCableCardSetup() ){
                        for( ChannelScanProgram theProgram : thePrograms ){
                            int theVchannel = theProgram.virtualMajor;

                            mDevice.setTunerVChannel( "" + theVchannel );
                            int theVchannelStatus = mDevice.waitForLock( theTunerStatus );

                            if( theVchannelStatus > 0 ){
                                TunerVStatus theVStatus = mDevice.getTunerVStatus();

                                if( theVStatus.returnStatus == DeviceResponse.SUCCESS ){
                                    Timber.d( " Channel scan vchannel status: " + theVStatus );
                                    theProgram.setVirtualChannelStatus( theVStatus );
                                }
                            }
                        }
                    }

                    mCntrl.notifyObserversProgramListChanged( thePrograms, theCurrentChannel );

                    if( !mFullChannelScan ){
                        mIsRunning = false;
                        return;
                    }


                }

                if( checkForEnd( theCurrentChannel ) ){
                    return;
                }
            }
        } finally {
            if( theTunerLocked ){
                mDevice.tunerLockeyRelease();
            }

            if( checkForEnd( theCurrentChannel ) ){
                mCntrl.notifyChannelScanComplete( theResponse );
            }

            mIsRunning = false;
            mCntrl.setProgressBarBusy( false );

            //Seems like the device needs a little time before it does something else here
            try{
                Thread.sleep( DeviceController.SLEEP_TIME_BETWEEN_TASKS );
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkForEnd( int theCurrentChannel ){
        int theMinChannel = mChannelList.getMinNumber();
        int theMaxChannel = mChannelList.getMaxNumber();

        if( mIsForward == true ){
            if( theCurrentChannel >= theMaxChannel ){
                return true;
            }
        } else {
            if( theCurrentChannel <= theMinChannel ){
                return true;
            }
        }
        return false;
    }

    /**
     * @return the forward
     */
    protected boolean isForward(){
        return mIsForward;
    }

    /**
     * @return the running
     */
    public boolean isRunning(){
        return mIsRunning;
    }

    protected void stop(){
        mIsRunning = false;
    }

    /**
     * @param forward the forward to set
     */
    public void setForward( boolean forward ){
        this.mIsForward = forward;
    }

}
