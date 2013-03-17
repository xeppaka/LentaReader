package cz.fit.lentaruand.data;

public enum Rubrics {
	ROOT("root", "", null), 
	RUSSIA("russia", "/russia", ROOT),
	RUSSIA_POLITICS("russia_politics", "/russia/politics", RUSSIA),
	RUSSIA_SOCIETY("russia_society", "/russia/society", RUSSIA),
	RUSSIA_CRIME("russia_crime", "/russia/crime", RUSSIA),
	RUSSIA_ROADS("russia_roads", "/russia/roads", RUSSIA),
	RUSSIA_ACCIDENT("russia_accident", "/russia/accident", RUSSIA),
	RUSSIA_CATASTROPHE("russia_catastrophe", "/russia/catastrophe", RUSSIA),
	
	WORLD("world", "/world", ROOT),
	WORLD_POLITIC("world_politic", "/world/politic", WORLD),
	WORLD_SOCIETY("world_society", "/world/society", WORLD),
	WORLD_CRIME("world_crime", "/world/crime", WORLD),
	WORLD_ACCIDENT("world_accident", "/world/accident", WORLD),
	WORLD_CATASTROPHE("world_catastrophe", "/world/catastrophe", WORLD),
	WORLD_CONFLICT("world_conflict", "/world/conflict", WORLD),
	
	USSR("ussr", "/ussr", ROOT),
	USSR_POLITIC("ussr_politic", "/ussr/politic", USSR),
	USSR_SOCIETY("ussr_society", "/ussr/society", USSR),
	USSR_CRIME("ussr_crime", "/ussr/crime", USSR),
	USSR_ACCIDENT("ussr_accident", "/ussr/accident", USSR),
	USSR_CATASTROPHE("ussr_catastrophe", "/ussr/catastrophe", USSR),
	
	ECONOMICS("economics", "/economics", ROOT),
	ECONOMICS_ECONOMY("economics_economy", "/economics/politic", ECONOMICS),
	ECONOMICS_SOCIETY("economics_resources", "/economics/resources", ECONOMICS),
	ECONOMICS_COMPANIES("economics_companies", "/economics/companies", ECONOMICS),
	ECONOMICS_BANKS("economics_banks", "/economics/banks", ECONOMICS),
	ECONOMICS_MARKETS("economics_markets", "/economics/markets", ECONOMICS),
	ECONOMICS_FINANCE("economics_finance", "/economics/finance", ECONOMICS),

	SCIENCE("science", "/science", ROOT),
	SCIENCE_MIL("science_mil", "/science/mil", SCIENCE),
	SCIENCE_GADGET("science_gadget", "/science/gadget", SCIENCE),
	SCIENCE_DIGITAL("science_digital", "/science/digital", SCIENCE),
	SCIENCE_SCIENCE("science_science", "/science/science", SCIENCE),
	SCIENCE_COSMOS("science_cosmos", "/science/cosmos", SCIENCE),
	
	SPORT("sport", "/sport", ROOT),
	SPORT_("sport_football", "/sport/football", SPORT),
	SPORT_SOCIETY("sport_hockey", "/sport/hockey", SPORT),
	SPORT_TENNIS("sport_tennis", "/sport/tennis", SPORT),
	SPORT_BOXING("sport_boxing", "/sport/boxing", SPORT),
	SPORT_BASKETBALL("sport_basketball", "/sport/basketball", SPORT),
	SPORT_BIATHLON("sport_biathlon", "/sport/biathlon", SPORT),
	SPORT_WINTER("sport_winter", "/sport/winter", SPORT),
	SPORT_SUMMER("sport_summer", "/sport/summer", SPORT),
	
	CULTURE("culture", "/culture", ROOT),
	CULTURE_KINO("culture_kino", "/culture/kino", CULTURE),
	CULTURE_MUSIC("culture_music", "/culture/music", CULTURE),
	CULTURE_THEATRE("culture_theatre", "/culture/theatre", CULTURE),
	CULTURE_BOOKS("culture_books", "/culture/books", CULTURE),
	CULTURE_ARCHITECTURE("culture_architecture", "/culture/architecture", CULTURE),
	CULTURE_ART("culture_art", "/culture/art", CULTURE),
	CULTURE_GAMES("culture_games", "/culture/games", CULTURE),
	
	MEDIA("media", "/media", ROOT),
	MEDIA_PRESS("media_press", "/media/press", MEDIA),
	MEDIA_TV("media_tv", "/media/tv", MEDIA),
	MEDIA_INTERNET("media_internet", "/media/internet", MEDIA),
	MEDIA_SOC_NETWORK("media_soc_network", "/media/soc_network", MEDIA),
	MEDIA_POLITIC("media_politic", "/media/politic", MEDIA),
	MEDIA_CRIME("media_crime", "/media/crime", MEDIA),
	MEDIA_SCANDAL("media_scandal", "/media/scandal", MEDIA),
	
	LIFE("life", "/life", ROOT),
	LIFE_PEOPLE("life_people", "/life/people", LIFE),
	LIFE_ANIMALS("life_animals", "/life/animals", LIFE),
	LIFE_FOOD("life_food", "/life/food", LIFE),
	LIFE_ACCIDENT("life_accident", "/life/accident", LIFE),
	LIFE_SCANDAL("life_scandal", "/life/scandal", LIFE),
	LIFE_EVENTS("life_events", "/life/events", LIFE),
	LIFE_PROGRESS("life_progress", "/life/progress", LIFE),
	LIFE_HOLIDAYS("life_holidays", "/life/holidays", LIFE),
	LIFE_STUFF("life_stuff", "/life/stuff", LIFE);

	private static final String RSS_PATH = "/rss";
	private String name;
	private String path;
	private String[] rssPaths = new String[NewsType.values().length];
	private Rubrics parent;
	
	private Rubrics(String name, String path, Rubrics parent) {
		this.name = name;
		this.path = RSS_PATH + path;
		
		for (NewsType nt : NewsType.values()) {
			rssPaths[nt.ordinal()] = RSS_PATH + nt.getRssPath() + path;
		}
		
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

	public String getRssPath(NewsType type) {
		return rssPaths[type.ordinal()];
	}
}
