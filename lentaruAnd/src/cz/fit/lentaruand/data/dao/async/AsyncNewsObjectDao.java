package cz.fit.lentaruand.data.dao.async;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;

public interface AsyncNewsObjectDao<T extends NewsObject> extends AsyncDao<T> {
	void readAsyncForRubric(Rubrics rubric, DaoReadMultiListener<T> listener);
}
