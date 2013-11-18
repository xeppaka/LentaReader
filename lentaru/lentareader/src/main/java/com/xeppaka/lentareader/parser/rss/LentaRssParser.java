package com.xeppaka.lentareader.parser.rss;

import android.util.Log;

import com.xeppaka.lentareader.downloader.Page;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * LentaRssParser is used for parsing RSS page from Lenta.ru site. It could be
 * any type of the news: news, photo, article, etc.
 * 
 * @author kacpa01
 * 
 * 
 */
public class LentaRssParser {
	private final static String ITEM_PATH = "/rss/channel/item";
//	private final static String GUID = "(//guid)[1]";
//	private final static String TITLE = "(//title)[1]";
//	private final static String LINK = "(//link)[1]";
//	private final static String AUTHOR = "(//author)[1]";
//	private final static String DESCRIPTION = "(//description)[1]";
//	private final static String PUBDATE = "(//pubDate)[1]";
//	private final static String IMAGEURL = "(//enclosure/@url)[1]";
	
	private final XPathExpression items;
//	private final XPathExpression guid;
//	private final XPathExpression title;
//	private final XPathExpression link;
//	private final XPathExpression author;
//	private final XPathExpression description;
//	private final XPathExpression pubDate;
//	private final XPathExpression imageUrl;

	private final String datePattern = "EEE, dd MMM yyyy HH:mm:ss Z";
	private final SimpleDateFormat lentaDateSDF = new SimpleDateFormat(datePattern, Locale.US);
	
	/**
	 * Default constructor. Instantiates all internal XPath related objects.
	 */
	public LentaRssParser() {
		XPath xp = XPathFactory.newInstance().newXPath();
		try {
			items = xp.compile(ITEM_PATH);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + ITEM_PATH);
		}

//		try {
//			guid =  xp.compile(GUID);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + GUID);
//		}
//		
//		try {
//			title = xp.compile(TITLE);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + TITLE);
//		}
//		
//		try {
//			link = xp.compile(LINK);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + LINK);
//		}
//		
//		try {
//			author = xp.compile(AUTHOR);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + AUTHOR);
//		}
//		
//		try {
//			description = xp.compile(DESCRIPTION);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + DESCRIPTION);
//		}
//		
//		try {
//			pubDate = xp.compile(PUBDATE);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + PUBDATE);
//		}
//		
//		try {
//			imageUrl = xp.compile(IMAGEURL);
//		} catch (XPathExpressionException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Error compiling XPath expression: " + IMAGEURL);
//		}
	}
	
	/**
	 * Read and parse all items from RSS page.
	 * 
	 * @param page
	 *            is the page to parse.
	 * @return Collection of the parsed LentaRssItem objects. Some fields in
	 *         that objects can be null. For example if Article RSS is parsed,
	 *         then Author field is set, but for News RSS Author field will be
	 *         null.
	 * @throws XPathExpressionException if some error occurred during parsing with XPath.
	 */
	public Collection<LentaRssItem> parseItems(Page page) throws ParseWithXPathException {
		if (page.getRubric() == null)
			throw new IllegalArgumentException("page.getRubric() must not be null.");
		
		if (page.getType() == null)
			throw new IllegalArgumentException("page.getType() must not be null.");
		
		StringReader sr = new StringReader(page.getText());
		List<LentaRssItem> resultItems = new ArrayList<LentaRssItem>();
		
		try {
			NodeList itemNodes;
			try {
				itemNodes = (NodeList) items.evaluate(new InputSource(sr), XPathConstants.NODESET);
			} catch(XPathExpressionException e) {
				Log.e(LentaConstants.LoggerMainAppTag, "Error occured during parsing RSS, on the page: " + page.getUrl(), e);
				throw new ParseWithXPathException(page.getUrl(), ITEM_PATH);
			}
			final int itemsLength = itemNodes.getLength();
			
			for (int i = 0; i < itemsLength; ++i) {
				Node itemNode = itemNodes.item(i);
				
				// NOTE: XPath for some reason is very slow. Commented out, implemented looking through the nodes manually.
				
//				itemNode.getParentNode().removeChild(itemNode);
//				
//				String guidStr = (String) guid.evaluate(itemNode, XPathConstants.STRING);
//				String titleStr = (String) title.evaluate(itemNode, XPathConstants.STRING);
//				String linkStr = (String) link.evaluate(itemNode, XPathConstants.STRING);
//				String authorStr = (String) author.evaluate(itemNode, XPathConstants.STRING);
//				String descriptionStr = (String) description.evaluate(itemNode, XPathConstants.STRING);
//				String pubDateStr = (String) pubDate.evaluate(itemNode, XPathConstants.STRING);
//				String imageUrlStr = (String) imageUrl.evaluate(itemNode, XPathConstants.STRING);
//				
//				Date date; 
//				try {
//					date = lentaDateSDF.parse(pubDateStr);
//				} catch (ParseException e) {
//					logger.log(Level.SEVERE, "Error occured suring parsing date: " + pubDateStr + ", on the page: " + page.getUrl().toString());
//					continue;
//				}
//				
//				resultItems.add(new LentaRssItem(guidStr, page.getType(), titleStr, linkStr, authorStr, descriptionStr, date, imageUrlStr, page.getRubric()));

				String guidStr = null;
				String titleStr = null;
				String linkStr = null;
				String authorStr = null;
				String descriptionStr = null;
				String pubDateStr = null;
				String imageUrlStr = null;
				
				NodeList itemChildren = itemNode.getChildNodes();
				final int childrenLength = itemChildren.getLength();
				for (int j = 0; j < childrenLength; ++j) {
					Node child = itemChildren.item(j);
					String childNodeName = child.getNodeName();
					
					if (childNodeName.equals("guid")) {
						guidStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("title")) {
						titleStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("link")) {
						linkStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("description")) {
						descriptionStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("pubDate")) {
						pubDateStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("author")) {
						authorStr = child.getTextContent();
						continue;
					}
					
					if (childNodeName.equals("enclosure")) {
						NamedNodeMap nnm = child.getAttributes();
						Node n = nnm.getNamedItem("url");
						imageUrlStr = n.getTextContent();
						continue;
					}
				}
				
				Date date;
				try {
					date = lentaDateSDF.parse(pubDateStr);
				} catch (ParseException e) {
					Log.e(LentaConstants.LoggerMainAppTag, "Error occured during parsing date: " + pubDateStr + ", on the page: " + page.getUrl(), e);
					continue;
				}
				
				resultItems.add(new LentaRssItem(guidStr, page.getType(), titleStr, linkStr, authorStr, descriptionStr, date, imageUrlStr, page.getRubric()));
			}

			return resultItems;
		} finally {
			sr.close();
		}
	}
}
