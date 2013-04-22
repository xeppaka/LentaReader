package cz.fit.lentaruand.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public enum Rubrics {
	ROOT("root", "", true, null), 
	RUSSIA("russia", "/russia", true, ROOT),
	RUSSIA_POLITICS("russia_politics", "/russia/politics", false, RUSSIA),
	RUSSIA_SOCIETY("russia_society", "/russia/society", false, RUSSIA),
	RUSSIA_CRIME("russia_crime", "/russia/crime", false, RUSSIA),
	RUSSIA_ROADS("russia_roads", "/russia/roads", false, RUSSIA),
	RUSSIA_ACCIDENT("russia_accident", "/russia/accident", false, RUSSIA),
	RUSSIA_CATASTROPHE("russia_catastrophe", "/russia/catastrophe", false, RUSSIA),
	
	WORLD("world", "/world", true, ROOT),
	WORLD_POLITIC("world_politic", "/world/politic", false, WORLD),
	WORLD_SOCIETY("world_society", "/world/society", false, WORLD),
	WORLD_CRIME("world_crime", "/world/crime", false, WORLD),
	WORLD_ACCIDENT("world_accident", "/world/accident", false, WORLD),
	WORLD_CATASTROPHE("world_catastrophe", "/world/catastrophe", false, WORLD),
	WORLD_CONFLICT("world_conflict", "/world/conflict", false, WORLD),
	
	USSR("ussr", "/ussr", true, ROOT),
	USSR_POLITIC("ussr_politic", "/ussr/politic", false, USSR),
	USSR_SOCIETY("ussr_society", "/ussr/society", false, USSR),
	USSR_CRIME("ussr_crime", "/ussr/crime", false, USSR),
	USSR_ACCIDENT("ussr_accident", "/ussr/accident", false, USSR),
	USSR_CATASTROPHE("ussr_catastrophe", "/ussr/catastrophe", false, USSR),
	
	ECONOMICS("economics", "/economics", true, ROOT),
	ECONOMICS_ECONOMY("economics_economy", "/economics/politic", false, ECONOMICS),
	ECONOMICS_SOCIETY("economics_resources", "/economics/resources", false, ECONOMICS),
	ECONOMICS_COMPANIES("economics_companies", "/economics/companies", false, ECONOMICS),
	ECONOMICS_BANKS("economics_banks", "/economics/banks", false, ECONOMICS),
	ECONOMICS_MARKETS("economics_markets", "/economics/markets", false, ECONOMICS),
	ECONOMICS_FINANCE("economics_finance", "/economics/finance", false, ECONOMICS),

	SCIENCE("science", "/science", true, ROOT),
	SCIENCE_MIL("science_mil", "/science/mil", false, SCIENCE),
	SCIENCE_GADGET("science_gadget", "/science/gadget", false, SCIENCE),
	SCIENCE_DIGITAL("science_digital", "/science/digital", false, SCIENCE),
	SCIENCE_SCIENCE("science_science", "/science/science", false, SCIENCE),
	SCIENCE_COSMOS("science_cosmos", "/science/cosmos", false, SCIENCE),
	
	SPORT("sport", "/sport", true, ROOT),
	SPORT_("sport_football", "/sport/football", false, SPORT),
	SPORT_SOCIETY("sport_hockey", "/sport/hockey", false, SPORT),
	SPORT_TENNIS("sport_tennis", "/sport/tennis", false, SPORT),
	SPORT_BOXING("sport_boxing", "/sport/boxing", false, SPORT),
	SPORT_BASKETBALL("sport_basketball", "/sport/basketball", false, SPORT),
	SPORT_BIATHLON("sport_biathlon", "/sport/biathlon", false, SPORT),
	SPORT_WINTER("sport_winter", "/sport/winter", false, SPORT),
	SPORT_SUMMER("sport_summer", "/sport/summer", false, SPORT),
	
	CULTURE("culture", "/culture", true, ROOT),
	CULTURE_KINO("culture_kino", "/culture/kino", false, CULTURE),
	CULTURE_MUSIC("culture_music", "/culture/music", false, CULTURE),
	CULTURE_THEATRE("culture_theatre", "/culture/theatre", false, CULTURE),
	CULTURE_BOOKS("culture_books", "/culture/books", false, CULTURE),
	CULTURE_ARCHITECTURE("culture_architecture", "/culture/architecture", false, CULTURE),
	CULTURE_ART("culture_art", "/culture/art", false, CULTURE),
	CULTURE_GAMES("culture_games", "/culture/games", false, CULTURE),
	
	MEDIA("media", "/media", true, ROOT),
	MEDIA_PRESS("media_press", "/media/press", false, MEDIA),
	MEDIA_TV("media_tv", "/media/tv", false, MEDIA),
	MEDIA_INTERNET("media_internet", "/media/internet", false, MEDIA),
	MEDIA_SOC_NETWORK("media_soc_network", "/media/soc_network", false, MEDIA),
	MEDIA_POLITIC("media_politic", "/media/politic", false, MEDIA),
	MEDIA_CRIME("media_crime", "/media/crime", false, MEDIA),
	MEDIA_SCANDAL("media_scandal", "/media/scandal", false, MEDIA),
	
	LIFE("life", "/life", true, ROOT),
	LIFE_PEOPLE("life_people", "/life/people", false, LIFE),
	LIFE_ANIMALS("life_animals", "/life/animals", false, LIFE),
	LIFE_FOOD("life_food", "/life/food", false, LIFE),
	LIFE_ACCIDENT("life_accident", "/life/accident", false, LIFE),
	LIFE_SCANDAL("life_scandal", "/life/scandal", false, LIFE),
	LIFE_EVENTS("life_events", "/life/events", false, LIFE),
	LIFE_PROGRESS("life_progress", "/life/progress", false, LIFE),
	LIFE_HOLIDAYS("life_holidays", "/life/holidays", false, LIFE),
	LIFE_STUFF("life_stuff", "/life/stuff", false, LIFE);

	private static final String RSS_PATH = "/rss";
	private static Map<Rubrics, Set<Rubrics>> rubricToSubrubrics = new HashMap<Rubrics, Set<Rubrics>>();
	
	static {
		for (Rubrics rubric : Rubrics.values()) {
			Rubrics parent = rubric.getParent();
			if (rubric.getParent() != null) {
				Set<Rubrics> subRubrics = rubricToSubrubrics.get(parent);
				
				if (subRubrics == null) {
					subRubrics = new HashSet<Rubrics>();
					rubricToSubrubrics.put(parent, subRubrics);
				}
				
				subRubrics.add(rubric);
			}
		}
		
		for (Entry<Rubrics, Set<Rubrics>> entry : rubricToSubrubrics.entrySet()) {
			entry.setValue(Collections.unmodifiableSet(entry.getValue()));
		}
	}
	
	private String name;
	private String path;
	private String[] rssPaths = new String[NewsType.values().length];
	private Rubrics parent;
	private boolean rubricUpdateNeed;
	
	private Rubrics(String name, String path, boolean rubricUpdateNeed, Rubrics parent) {
		this.name = name;
		this.path = RSS_PATH + path;
		
		for (NewsType nt : NewsType.values()) {
			rssPaths[nt.ordinal()] = RSS_PATH + nt.getRssPath() + path;
		}
		
		this.rubricUpdateNeed = rubricUpdateNeed;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public Rubrics getParent() {
		return parent;
	}

	public boolean isRubricUpdateNeed() {
		return rubricUpdateNeed;
	}
	
	public String getRssPath(NewsType type) {
		return rssPaths[type.ordinal()];
	}
	
	public static Set<Rubrics> getSubrubrics(Rubrics rubric) {
		return rubricToSubrubrics.get(rubric);
	}
}
