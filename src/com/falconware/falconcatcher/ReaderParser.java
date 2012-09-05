package com.falconware.falconcatcher;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

//Yes I know my code duplication here is evil...shut up
public class ReaderParser {	
	private String mFeedUrl;
	private String mTitle;
	private Database mDb;
	private XmlPullParser mParser;
	
	
	public ReaderParser(String urlString, Context context, Database db) throws XmlPullParserException, IOException
	{
		mFeedUrl = urlString;
		mTitle = "";
		mDb = db;
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
	
	public void readFeed() throws IOException, XmlPullParserException {
		int eventType = mParser.getEventType();
		long time1 = System.currentTimeMillis();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_TAG) {
				//System.out.println("Start Tag: " + parser.getName());
				if (mParser.getName().equals("object")) {
					parseTopObject();
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
	
	public void parseTopObject() throws IOException, XmlPullParserException {
		int eventType = mParser.next();
		//When getText() returns non-null, getName() returns null
		String imageUrl = "";
		while (eventType != XmlPullParser.END_TAG || !mParser.getName().equals("channel")) {
			if (eventType == XmlPullParser.START_TAG) {
				//System.out.println("Start Tag: " + parser.getName());
				String tagName = mParser.getName();
//				if (tagName.equals("title")) {
//					mTitle = mParser.nextText();
//				}
//				else if (tagName.equals("itunes:image")) {
//					imageUrl = getAttribute("href");
//				}
//				else if (tagName.equals("item")) {
//					parseItem();
//				}
			}
			eventType = mParser.next();
		}	
	}

}
