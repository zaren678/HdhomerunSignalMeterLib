package com.zaren.HdhomerunSignalMeterLib.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import com.zaren.HdhomerunSignalMeterLib.util.HDHomerunLogger;

import android.content.Context;

public class PrimeChannelScanRunnable implements Runnable
{   
   private static final String FILENAME = "channels.xml";
   private DeviceController mDeviceController;
   private Context mContext;
   private URL mLineupUrl;
   private InputStream mFile;

   public PrimeChannelScanRunnable( DeviceController aDeviceController, Context aContext, URL aLineupURL )
   {
      mDeviceController = aDeviceController;
      mContext = aContext;
      mLineupUrl = aLineupURL;
   }
   
   public PrimeChannelScanRunnable( DeviceController aDeviceController, Context aContext, InputStream aFile )
   {
      mDeviceController = aDeviceController;
      mContext = aContext;
      mFile = aFile;
   }

   @Override
   public void run()
   {
      DeviceResponse theResponse = new DeviceResponse( DeviceResponse.SUCCESS );
      theResponse.putString( DeviceResponse.KEY_ACTION, "Prime Device Channel Scan" );
      try
      {
         if( mLineupUrl != null )
         {
            downloadChannelList( mLineupUrl );
         }
         else
         {
            downloadChannelList( mFile );
         }
         //now process the xml
         ProgramsList thePrograms = processChannelList();
         
         if( mDeviceController != null )
         {
            //send program list changed
            mDeviceController.notifyObserversProgramListChanged( thePrograms, -1 );
         }
      }
      catch( MalformedURLException e )
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Malformed URL: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Malformed URL: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Malformed URL" );
         }
      }   
      catch( ProtocolException e )
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "ProtocolException: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "ProtocolException: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "ProtocolException" );
         }         
      }
      catch( FileNotFoundException e)
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "File not found: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "File not found: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "File not found" );
         }             
      }         
      catch( XmlPullParserException e )
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "XML Parsing error: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "XML Parsing error: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "XML Parsing error" );
         }         
      }
      catch( IOException e )
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "IO Error: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "IO Error: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "IO Error" );
         }
      }
      catch( URISyntaxException e )
      {
         theResponse.setStatus( DeviceResponse.FAILURE );
         if( e.getLocalizedMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Bad URI Syntax: " + e.getLocalizedMessage() );
         }
         else if( e.getMessage() != null )
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Bad URI Syntax: " + e.getMessage() );
         }
         else
         {
            theResponse.putString( DeviceResponse.KEY_ERROR, "Bad URI Syntax" );
         }
      }
      finally
      {
         if( mDeviceController != null )
         {
            mDeviceController.setProgressBarBusy( false );
            
            //send channel scan complete
            mDeviceController.notifyChannelScanComplete( theResponse );
         }
      }

   }

   private ProgramsList processChannelList() throws XmlPullParserException, IOException
   {      
      LineupXMLParser theParser = new LineupXMLParser();
   
      File theLineupFile = new File( mContext.getFilesDir(), FILENAME );
   
      InputStream theStream = new FileInputStream( theLineupFile );
               
      return theParser.parse( theStream );
   }

   private void downloadChannelList( InputStream aFile ) throws FileNotFoundException, IOException
   {
      FileOutputStream fileOutput = mContext.openFileOutput( FILENAME, Context.MODE_PRIVATE );
      
      //long totalSize = aFile.length();

      //int downloadedSize = 0;
      
      byte[] buffer = new byte[1024];
      int bufferLength = 0; // used to store a temporary size of the buffer

      while( ( bufferLength = aFile.read( buffer ) ) > 0 )
      {

         fileOutput.write( buffer, 0, bufferLength );

         //downloadedSize += bufferLength;
      }

      fileOutput.close();       
   }

   private void downloadChannelList( URL aLineupUrl ) throws MalformedURLException, IOException, ProtocolException, FileNotFoundException, URISyntaxException
   {
      HDHomerunLogger.d( "DownloadChannelList: URL: " + aLineupUrl );
      
      boolean theUseApache = true;
      
      InputStream theInputStream;
      
      if( theUseApache )
      {
         HDHomerunLogger.d( "DownloadChannelList: Using Apache HTTP client" );
         
         HttpClient httpClient = new DefaultHttpClient();
         HttpGet pageGet = new HttpGet( aLineupUrl.toURI() );
         HttpResponse response = httpClient.execute( pageGet );
         
         theInputStream = response.getEntity().getContent();
      }
      else
      {
         HttpURLConnection urlConnection = (HttpURLConnection) aLineupUrl.openConnection();
   
         urlConnection.setRequestMethod( "GET" );
         urlConnection.setDoOutput( true );   
         urlConnection.connect();
         
         theInputStream = urlConnection.getInputStream();
      }
      

      FileOutputStream fileOutput = mContext.openFileOutput( FILENAME, Context.MODE_PRIVATE );

      

      //int totalSize = urlConnection.getContentLength();

      //int downloadedSize = 0;

      byte[] buffer = new byte[1024];
      int bufferLength = 0; // used to store a temporary size of the buffer

      while( ( bufferLength = theInputStream.read( buffer ) ) > 0 )
      {

         fileOutput.write( buffer, 0, bufferLength );

         //downloadedSize += bufferLength;
      }

      fileOutput.close();
   }

}
