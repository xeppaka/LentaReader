package cz.fit.lentaruand.data.dao.async;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NODao;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
	void readAsyncForRubric(Rubrics rubric, DaoReadMultiListener<T> listener);
}
