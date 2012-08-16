package com.falconware.falconcatcher;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndImage;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;
import com.sun.syndication.feed.module.itunes.FeedInformation;
import com.sun.syndication.feed.module.mediarss.MediaModule;

public class FeedParser {
	private SyndFeed mFeed;
	private String mFeedUrl;
	
	public FeedParser(String url) throws FeedException, IOException
	{
		mFeedUrl = url;
		mFeed = null;
		URL feedUrl = new URL(url);
		System.out.println("url");
		SyndFeedInput input = new SyndFeedInput();
		System.out.println("input");
		mFeed = input.build(new XmlReader(feedUrl));
		System.out.println("mfeed");
	}
	
	private String getFeedUrl() {
		String imageUrl = null;
		int attempt = 0;
		outerloop:
		while (imageUrl == null) {
			try {
				switch (attempt) {
				case 0:
					imageUrl = ((SyndImage)mFeed.getImage()).getUrl();
					break outerloop;
				case 1:
					imageUrl = ((MediaModule)mFeed.getModule(MediaModule.URI)).getMetadata().getThumbnail()[0].getUrl().toString();
					break outerloop;
				case 2:
					imageUrl = ((FeedInformation)mFeed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd")).getImage().toString();
				default:
					break outerloop;
				}
			} catch (Exception e) {
				//We'll try the next one
				System.out.println("Failed " + attempt + " time(s) through loop");
			}
			attempt++;
		}
		if (imageUrl == null) {
			return "";
		}
		return imageUrl;
	}
	
	private String getEntryAuthor(SyndEntry entry) {
		String author = entry.getAuthor();
		if (author == null || author.isEmpty()) {
			author = ((FeedInformation)mFeed.getModule("http://www.itunes.com/dtds/podcast-1.0.dtd")).getAuthor();
		}
		@SuppressWarnings("rawtypes")
		List authors = entry.getAuthors();
		if (authors == null || authors.isEmpty()) {
			authors = entry.getContributors();
		}
		if (authors != null && !authors.isEmpty()) {
			for (Object auth : authors) {
				author = author + (String)auth + ", ";
			}
			author = author.substring(0, author.length()-2);
		}
		if (author == null) {
			return "";
		}
		return author;
	}
	
	private String getEntryUrl(SyndEntry entry) {
		String url;
		
		//Attempt #1
		url = entry.getLink();
		if (url != null && isAudioFile(url)) {
			return url;
		}
		
		//Attempt #2
		url = entry.getUri();
		if (url != null && isAudioFile(url)) {
			return url;
		}

		//Attempt #3
		url = ((SyndEnclosure)entry.getEnclosures().get(0)).getUrl();
		if (url != null && isAudioFile(url)) {
			return url;
		}
		
		return "";
	}
		
	private boolean isAudioFile(String url) {
		return url.endsWith(".mp3");
	}
	public void parseFeedProperties(Database db) throws Exception {  //TODO: Cleanup top level exception
		//System.out.println(feed);
		String title = mFeed.getTitle();
		String imageUrl = getFeedUrl();
		
		db.addFeed(mFeedUrl, title, imageUrl);
		System.out.println("Title: " + title);
		System.out.println("Image: " + imageUrl);
		db.addFeed(mFeedUrl, title, imageUrl);
	}
	
	public void parseEpisodeList(Database db) throws Exception {  //TODO: Cleanup top level exception
		//System.out.println(mFeed);
		SyndEntry entry = (SyndEntry)mFeed.getEntries().get(0);
		System.out.println(entry);
		
		String author = getEntryAuthor(entry);
		String url = getEntryUrl(entry);
		String title = entry.getTitle();
		String description = entry.getDescription().getValue();
		//String length = 

	
		
		Date publishedDate = entry.getPublishedDate();
		String localFile = null;
		
		System.out.println("Url: " + url);
		System.out.println("title: " + title);
		System.out.println("description: " + description);
		System.out.println("author: " + author);
		System.out.println("published date: " + publishedDate);
		
		db.addEpisode(mFeedUrl, url, title, description, author, publishedDate.toString());
		
		
//		System.out.println("Authors: " + entry.getAuthors());
//		System.out.println("Authors: " + entry.getContributors());
//		System.out.println("Authors: " + entry.getAuthor());
		

		
	}

//	public static void main(String[] args) {
//		String[] feeds = new String[] {
//			"http://localhost:8080/podcast1.xml"	
//		};
////				"http://leo.am/podcasts/aaa",
////				"http://marketplace.publicradio.org/podcast/podcast_mmr_first.php",
////				"http://americanpublicmedia.publicradio.org/podcasts/xml/performance_today/piano_puzzler.xml",
////				"http://feeds.feedburner.com/coderradiomp3",
////				"http://feeds.feedburner.com/dancarlin/commonsense?format=xml",
////				"http://www.econlib.org/library/EconTalk.xml",
////				"http://feeds.feedburner.com/freakonomicsradio",
////				"http://downloads.bbc.co.uk/podcasts/worldservice/globalnews/rss.xml",
////				"http://feeds.conversationsnetwork.org/channel/itc",
////				"http://www.npr.org/rss/podcast.php?id=1090",
////				"http://leoville.tv/podcasts/sn.xml",
////				"http://feeds.feedburner.com/TheSoundOpinionsPodcast?format=xml",
////				"http://leo.am/podcasts/tnt",
////				"http://feeds.feedburner.com/javaposse",
////				"http://feeds2.feedburner.com/TheLinuxActionShow",
////				"http://feeds.thisamericanlife.org/talpodcast"};
//				
//		for (String feedName : feeds) {
//			System.out.println(feedName);
//			FeedParser parser = null;
//			try {
//				parser = new FeedParser(feedName);
//				//parser.parseFeedProperties(null);//.parseGlobalProperties();
//				parser.parseEpisodeList(null);
//			} catch (NullPointerException e) {
//				
//			}
//			catch (Exception e) {
//				System.out.println("Cannot parse feed!");
//				return;
//			}
//
//			System.out.println("\n");
//			
//		}
//	}
}
