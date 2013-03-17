package cz.fit.lentaruand.rss;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cz.fit.lentaruand.Page;
import cz.fit.lentaruand.PageDownloader;
import cz.fit.lentaruand.URLHelper;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

public class RssReader {
	private final static String ITEM_PATH = "/rss/channel/item";
	private final static String GUID = "guid";
	private final static String TITLE = "title";
	private final static String LINK = "link";
	private final static String DESCRIPTION = "description";
	private final static String PUBDATE = "pubDate";
	private final static String IMAGEURL = "enclosure/@url";
	
	private final static ThreadLocal<XPathExpression> items;
	private final static ThreadLocal<XPathExpression> guid;
	private final static ThreadLocal<XPathExpression> title;
	private final static ThreadLocal<XPathExpression> link;
	private final static ThreadLocal<XPathExpression> description;
	private final static ThreadLocal<XPathExpression> pubDate;
	private final static ThreadLocal<XPathExpression> imageUrl;
	
	static {
		final ThreadLocal<XPath> localXPath = new ThreadLocal<XPath>() {
			@Override
			protected XPath initialValue() {
				return XPathFactory.newInstance().newXPath();
			}
		};
		
		items = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(ITEM_PATH);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + ITEM_PATH);
				}
			}
		};
		
		guid = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(GUID);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + GUID);
				}
			}
		};
		
		title = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(TITLE);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + TITLE);
				}
			}
		};
		
		link = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(LINK);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + LINK);
				}
			}
		};
		
		description = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(DESCRIPTION);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + DESCRIPTION);
				}
			}
		};
		
		pubDate = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(PUBDATE);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + PUBDATE);
				}
			}
		};
		
		imageUrl = new ThreadLocal<XPathExpression>() {
			@Override
			protected XPathExpression initialValue() {
				XPath xp = localXPath.get();
				try {
					return xp.compile(IMAGEURL);
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					throw new RuntimeException("Error compiling XPath expression: " + IMAGEURL);
				}
			}
		};
	}
	
	public static Collection<RssItem> readItems(Rubrics rubric, NewsType newsType) throws IOException, XPathExpressionException {
		URL url = URLHelper.getRssForRubric(rubric, newsType);
		Page xmlPage = PageDownloader.downloadPage(url);
		
		return parseItems(xmlPage, rubric, newsType);
	}
	
	private static Collection<RssItem> parseItems(Page page, Rubrics rubric, NewsType newsType) throws XPathExpressionException {
		XPathExpression itemsExpr = items.get();
		StringReader sr = new StringReader(page.getText());
		List<RssItem> resultItems = new ArrayList<RssItem>();
		
		try {
			NodeList itemNodes = (NodeList) itemsExpr.evaluate(new InputSource(sr), XPathConstants.NODESET);
			
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				Node itemNode = itemNodes.item(i);
				
				String guidStr = (String) guid.get().evaluate(itemNode, XPathConstants.STRING);
				String titleStr = (String) title.get().evaluate(itemNode, XPathConstants.STRING);
				String linkStr = (String) link.get().evaluate(itemNode, XPathConstants.STRING);
				String descriptionStr = (String) description.get().evaluate(itemNode, XPathConstants.STRING);
				String pubDateStr = (String) pubDate.get().evaluate(itemNode, XPathConstants.STRING);
				String imageUrlStr = (String) imageUrl.get().evaluate(itemNode, XPathConstants.STRING);
				
				Date date;
				try {
					date = RssDateParser.parseDate(pubDateStr);
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				
				resultItems.add(new RssItem(guidStr, newsType, titleStr, linkStr, descriptionStr, date, imageUrlStr, rubric));
			}
			
			return resultItems;
		} finally {
			sr.close();
		}
	}
}
