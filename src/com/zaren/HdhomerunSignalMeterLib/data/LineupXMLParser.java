package com.zaren.HdhomerunSignalMeterLib.data;

import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

import android.util.Xml;

public class LineupXMLParser
{
   // We don't use namespaces
   private static final String ns = null;
   
   //tags we're interested in
   private static final String LINEUP = "Lineup";
   private static final String PROGRAM = "Program";
   private static final String GUIDE_NAME = "GuideName";
   private static final String GUIDE_NUMBER = "GuideNumber";

   

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
            catch ( NumberFormatException e )
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
      Integer theGuideMajorNumber = 0;
      Integer theGuideMinorNumber = 0;
      String theGuideName = null;

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
            parseGuideNumber( theGuideNumberStr, theGuideMajorNumber, theGuideMinorNumber );
         }
         else if( name.equals( GUIDE_NAME ) )
         {
            theGuideName = readGuideName( aParser );
         }
         else
         {
            skip( aParser );
         }
      }
      mProgramNum++;
      return new ChannelScanProgram( theGuideName, mProgramNum, theGuideMajorNumber, theGuideMinorNumber, ChannelScanProgram.PROGRAM_VCHANNEL, theGuideName, true );
   }

   private void parseGuideNumber( String aGuideNumberStr, Integer aGuideMajorNumber, Integer aGuideMinorNumber )
   {
      int thePointIndex = aGuideNumberStr.indexOf( '.' );
      
      if( thePointIndex > -1 )
      {
         String theMajorNum = aGuideNumberStr.substring( 0, thePointIndex );
         String theMinorNum = aGuideNumberStr.substring( thePointIndex + 1, aGuideNumberStr.length() );
         
         aGuideMajorNumber = Integer.parseInt( theMajorNum );
         aGuideMinorNumber = Integer.parseInt( theMinorNum );
      }
      else
      {
         aGuideMajorNumber = Integer.parseInt( aGuideNumberStr );
         aGuideMinorNumber = 0;
      }
      
   }

   private String readGuideNumber( XmlPullParser aParser ) throws IOException, XmlPullParserException
   {
      String theGuideNumber = "";
      
      try
      {
         aParser.require( XmlPullParser.START_TAG, ns, GUIDE_NUMBER );
         theGuideNumber = readText( aParser );
         aParser.require( XmlPullParser.END_TAG, ns, GUIDE_NUMBER );         
      }
      catch( NumberFormatException e )
      {
         HDHomerunLogger.e( "Couldn't parse guide number " + e );         
      }
      
      return theGuideNumber;
   }
   
   private String readGuideName( XmlPullParser aParser ) throws IOException, XmlPullParserException
   {     
      aParser.require( XmlPullParser.START_TAG, ns, GUIDE_NAME );
      String theGuideName = readText( aParser );
      aParser.require( XmlPullParser.END_TAG, ns, GUIDE_NAME );         
      
      return theGuideName;
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
