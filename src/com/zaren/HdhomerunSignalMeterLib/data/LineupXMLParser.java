package com.zaren.HdhomerunSignalMeterLib.data;

import android.util.Xml;
import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineupXMLParser
{
    // We don't use namespaces
    private static final String ns = null;

    //tags we're interested in
    private static final String LINEUP = "Lineup";
    private static final String PROGRAM = "Program";
    private static final String GUIDE_NAME = "GuideName";
    private static final String GUIDE_NUMBER = "GuideNumber";
    private static final String TAGS = "Tags";

    private int mProgramNum = 0;

    public ProgramsList parse( InputStream aLineupInput ) throws XmlPullParserException, IOException
    {
        try
        {
            XmlPullParser theParser = Xml.newPullParser();
            theParser.setFeature( XmlPullParser.FEATURE_PROCESS_NAMESPACES, false );
            theParser.setInput( aLineupInput, null );
            theParser.nextTag();
            return readFeed( theParser );
        }
        finally
        {
            aLineupInput.close();
            mProgramNum = 0;
        }
    }

    private ProgramsList readFeed( XmlPullParser aParser ) throws XmlPullParserException, IOException
    {
        ProgramsList thePrograms = new ProgramsList();

        aParser.require( XmlPullParser.START_TAG, ns, LINEUP );
        while( aParser.next() != XmlPullParser.END_TAG )
        {
            if( aParser.getEventType() != XmlPullParser.START_TAG )
            {
                continue;
            }
            String name = aParser.getName();
            // Starts by looking for the program tag
            if( name.equals( "Program" ) )
            {
                try
                {
                    ChannelScanProgram theProgram = readProgram( aParser );
                    thePrograms.append( theProgram.programNumber, theProgram );
                }
                catch( NumberFormatException e )
                {
                    HDHomerunLogger.e( "Failed to parse program from xml: " + e );
                }
            }
            else
            {
                skip( aParser );
            }
        }
        return thePrograms;
    }

    private ChannelScanProgram readProgram( XmlPullParser aParser ) throws XmlPullParserException, IOException
    {
        aParser.require( XmlPullParser.START_TAG, ns, PROGRAM );

        ChannelScanProgram theProgram = new ChannelScanProgram();

        while( aParser.next() != XmlPullParser.END_TAG )
        {
            if( aParser.getEventType() != XmlPullParser.START_TAG )
            {
                continue;
            }

            String name = aParser.getName();
            if( name.equals( GUIDE_NUMBER ) )
            {
                String theGuideNumberStr = readGuideNumber( aParser );
                parseGuideNumber( theGuideNumberStr, theProgram );
            }
            else if( name.equals( GUIDE_NAME ) )
            {
                String theName = readGuideName( aParser );
                theProgram.name = theName;
                theProgram.programString = theName;
            }
            else if( name.equals( TAGS ) )
            {
                List<String> theTags = readTags( aParser );
                theProgram.tags = theTags;
            }
            else
            {
                skip( aParser );
            }
        }
        mProgramNum++;

        theProgram.programNumber = mProgramNum;
        theProgram.type = ChannelScanProgram.PROGRAM_VCHANNEL;

        return theProgram;
    }

    private void parseGuideNumber( String aGuideNumberStr, ChannelScanProgram aProgram )
    {
        int thePointIndex = aGuideNumberStr.indexOf( '.' );

        if( thePointIndex > -1 )
        {
            String theMajorNum = aGuideNumberStr.substring( 0, thePointIndex );
            String theMinorNum = aGuideNumberStr.substring( thePointIndex + 1, aGuideNumberStr.length() );

            aProgram.virtualMajor = Integer.parseInt( theMajorNum );
            aProgram.virtualMinor = Integer.parseInt( theMinorNum );
        }
        else
        {
            aProgram.virtualMajor = Integer.parseInt( aGuideNumberStr );
            aProgram.virtualMinor = 0;
        }
    }

    private String readGuideNumber( XmlPullParser aParser ) throws IOException, XmlPullParserException
    {
        aParser.require( XmlPullParser.START_TAG, ns, GUIDE_NUMBER );
        String theGuideNumber = readText( aParser );
        aParser.require( XmlPullParser.END_TAG, ns, GUIDE_NUMBER );
        return theGuideNumber;
    }

    private String readGuideName( XmlPullParser aParser ) throws IOException, XmlPullParserException
    {
        aParser.require( XmlPullParser.START_TAG, ns, GUIDE_NAME );
        String theGuideName = readText( aParser );
        aParser.require( XmlPullParser.END_TAG, ns, GUIDE_NAME );

        return theGuideName;
    }

    private List<String> readTags( final XmlPullParser aParser ) throws IOException, XmlPullParserException
    {
        aParser.require( XmlPullParser.START_TAG, ns, TAGS );
        String theTags = readText( aParser );
        String[] theTagList = theTags.split( "," );
        ArrayList<String> theReturnList = new ArrayList<String>( Arrays.asList( theTagList ) );
        aParser.require( XmlPullParser.END_TAG, ns, TAGS );

        return theReturnList;
    }

    private String readText( XmlPullParser aParser ) throws IOException, XmlPullParserException
    {
        String theResult = "";
        if( aParser.next() == XmlPullParser.TEXT )
        {
            theResult = aParser.getText();
            aParser.nextTag();
        }
        return theResult;
    }

    private void skip( XmlPullParser aParser ) throws XmlPullParserException, IOException
    {
        if( aParser.getEventType() != XmlPullParser.START_TAG )
        {
            throw new IllegalStateException();
        }
        int theDepth = 1;
        while( theDepth != 0 )
        {
            switch( aParser.next() )
            {
                case XmlPullParser.END_TAG:
                    theDepth--;
                    break;
                case XmlPullParser.START_TAG:
                    theDepth++;
                    break;
            }
        }
    }
}
