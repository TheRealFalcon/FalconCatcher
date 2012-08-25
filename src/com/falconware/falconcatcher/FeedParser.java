package com.falconware.falconcatcher;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

public class FeedParser {
	private String mFeedUrl;
	private Database mDb;
	private XmlPullParser mParser;
	
	public FeedParser(String urlString, Context context) throws XmlPullParserException, IOException
	{
		mFeedUrl = urlString;
		mDb = new Database(context);
		mParser = Xml.newPullParser();
		mParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
			mParser.setInput(in, null);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		readFeed();
	}
	
	public void readFeed() throws XmlPullParserException, IOException {
		int eventType = mParser.getEventType();
		long time1 = System.currentTimeMillis();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				//System.out.println("Start Tag: " + parser.getName());
				if (mParser.getName().equals("channel")) {
					parseChannel();
				}				
			}
			else if (eventType == XmlPullParser.END_TAG) {
				//System.out.println("End Tag: " + parser.getName());
			}
			else if (eventType == XmlPullParser.TEXT){
				//System.out.println("Text: " + parser.getText());
			}
			eventType = mParser.next();
		}
		long time2 = System.currentTimeMillis();
		System.out.println("Time: " + (time2-time1));
	}
	
	private void parseChannel() throws XmlPullParserException, IOException {
		//System.out.println("In parse channel");
		int eventType = mParser.next();
		//When getText() returns non-null, getName() returns null
		String title = "";
		String imageUrl = "";
		while (eventType != XmlPullParser.END_TAG || !mParser.getName().equals("channel")) {
			if (eventType == XmlPullParser.START_TAG) {
				//System.out.println("Start Tag: " + parser.getName());
				String tagName = mParser.getName();
				if (tagName.equals("title")) {
					title = mParser.nextText();
				}
				else if (tagName.equals("itunes:image")) {
					imageUrl = getAttribute("href");
				}
				else if (tagName.equals("item")) {
					parseItem();
				}
			}
			eventType = mParser.next();
		}
		mDb.addFeed(mFeedUrl, title, imageUrl);
		
	}
	
	private void parseItem() throws XmlPullParserException, IOException {
		//System.out.println("In parse item");
		int eventType = mParser.next();
		
		String episodeUrl = "";
		String title = "";
		String description = "";
		String author = "";
		String pubDate = "";
		
		while (eventType != XmlPullParser.END_TAG || !mParser.getName().equals("item")) {
			if (eventType == XmlPullParser.START_TAG) {
				String tagName = mParser.getName();
				if (tagName.equals("media:content")) {
					episodeUrl = getAttribute("url");
				}
				else if (tagName.equals("title")) {
					title = mParser.nextText();
				}
				else if (tagName.equals("description")) {
					description = mParser.nextText();
				}
				else if (tagName.equals("itunes:author")) {
					author = mParser.nextText();
				}
				else if (tagName.equals("pubDate")) {
					pubDate = mParser.nextText(); 
				}
				//System.out.println("Start Tag: " + parser.getName());
			}
			else if (eventType == XmlPullParser.TEXT) {
				//System.out.println("Text: " + parser.getText());
			}
			eventType = mParser.next();
		}
		mDb.addEpisode(mFeedUrl, episodeUrl, title, description, author, pubDate);
	}
	
	private String getAttribute(String attribute) {
		for (int i=0; i<mParser.getAttributeCount(); i++) {
			if (mParser.getAttributeName(i).equals(attribute)) {
				return mParser.getAttributeValue(i);
			}
		}
		return "";
	}
}
	
//	public static void main(String[] args) {
//		String[] feeds = new String[] {
//				"http://localhost:8080/freakonomics.xml"	
//		};
//		//			"http://leo.am/podcasts/aaa",
//		//			"http://marketplace.publicradio.org/podcast/podcast_mmr_first.php",
//		//			"http://americanpublicmedia.publicradio.org/podcasts/xml/performance_today/piano_puzzler.xml",
//		//			"http://feeds.feedburner.com/coderradiomp3",
//		//			"http://feeds.feedburner.com/dancarlin/commonsense?format=xml",
//		//			"http://www.econlib.org/library/EconTalk.xml",
//		//			"http://feeds.feedburner.com/freakonomicsradio",
//		//			"http://downloads.bbc.co.uk/podcasts/worldservice/globalnews/rss.xml",
//		//			"http://feeds.conversationsnetwork.org/channel/itc",
//		//			"http://www.npr.org/rss/podcast.php?id=1090",
//		//			"http://leoville.tv/podcasts/sn.xml",
//		//			"http://feeds.feedburner.com/TheSoundOpinionsPodcast?format=xml",
//		//			"http://leo.am/podcasts/tnt",
//		//			"http://feeds.feedburner.com/javaposse",
//		//			"http://feeds2.feedburner.com/TheLinuxActionShow",
//		//			"http://feeds.thisamericanlife.org/talpodcast"};
//
//		for (String feedName : feeds) {
//			System.out.println(feedName);
//			FeedParser parser = null;
//			try {
//				parser = new FeedParser(feedName);
//				//parser.parseFeedProperties(null);//.parseGlobalProperties();
//				//.parseEpisodeList(null);
//			} catch (NullPointerException e) {
//
//			}
//			catch (Exception e) {
//				System.out.println("Cannot parse feed!");
//				e.printStackTrace();
//				return;
//			}
//
//			System.out.println("\n");
//
//		}
//	}
//}
//}
	
//	private boolean isAudioFile(String url) {
//		return url.endsWith(".mp3");
//	}
//	

////STORE AND GET IMAGES TO/FROM DATABASE
////where we want to download it from
//URL url = new URL(IMAGE_URL);  //http://example.com/image.jpg
////open the connection
//URLConnection ucon = url.openConnection();
////buffer the download
//InputStream is = ucon.getInputStream();
//BufferedInputStream bis = new BufferedInputStream(is,128);
//ByteArrayBuffer baf = new ByteArrayBuffer(128);
////get the bytes one by one
//int current = 0;
//while ((current = bis.read()) != -1) {
//      baf.append((byte) current);
//}
//
////GET IMAGE BACK OUT
////store the data as a ByteArray
////db is a SQLiteDatabase object
//ContentValues dataToInsert = new ContentValues();                          
//dataToInsert.put(TABLE_FIELD,baf.toByteArray());
//db.insert(TABLE_NAME, null, dataToInsert);
//And this is how you get the data back and convert it into a Bitmap:
//
////select the data
//Cursor cursor = db.query(TABLE_STATIONLIST, new String[] {TABLE_FIELD},
//                                              null, null, null, null, null);
////get it as a ByteArray
//byte[] imageByteArray=cursor.getBlob(1);
////the cursor is not needed anymore
//cursor.close();
//
////convert it back to an image
//ByteArrayInputStream imageStream = new ByteArrayInputStream(mybyte);
//Bitmap theImage = BitmapFactory.decodeStream(imageStream));
