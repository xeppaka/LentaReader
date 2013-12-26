package com.xeppaka.lentareader.data.dao.decorators;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;

import java.util.List;

public class AsyncNODaoDecorator<T extends NewsObject> extends AsyncDaoDecorator<T> implements AsyncNODao<T>, NODao<T> {
	private NODao<T> decoratedDao;
	
	protected class AsyncReadMultiForRubricTask extends AsyncTask<Rubrics, Void, List<T>> {
		private AsyncDao.DaoReadMultiListener<T> listener;
		
		public AsyncReadMultiForRubricTask(AsyncDao.DaoReadMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected List<T> doInBackground(Rubrics... rubric) {
			return read(rubric[0]);
		}

		@Override
		protected void onPostExecute(List<T> result) {
			listener.finished(result);
		}
	}

    private static class ReadSingleWOImageForRubricParam {
        private Rubrics rubric;
        private int limit;

        private ReadSingleWOImageForRubricParam(Rubrics rubric, int limit) {
            this.rubric = rubric;
            this.limit = limit;
        }
    }

    protected class AsyncReadSingleWOImageForRubricTask extends AsyncTask<ReadSingleWOImageForRubricParam, Void, T> {
        private AsyncDao.DaoReadSingleListener<T> listener;

        public AsyncReadSingleWOImageForRubricTask(AsyncDao.DaoReadSingleListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T doInBackground(ReadSingleWOImageForRubricParam... param) {
            return readLatestWOImage(param[0].rubric, param[0].limit);
        }

        @Override
        protected void onPostExecute(T result) {
            listener.finished(result);
        }
    }

    protected class AsyncClearLatestFlagTask extends AsyncTask<Rubrics, Void, Integer> {
        private DaoUpdateListener listener;

        public AsyncClearLatestFlagTask(DaoUpdateListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Rubrics... rubric) {
            return clearLatestFlag(rubric[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.finished(result);
        }
    }

	public AsyncNODaoDecorator(NODao<T> decoratedDao) {
		super(decoratedDao);
		
		this.decoratedDao = decoratedDao;
	}

    @Override
    public boolean hasImage(long id) {
        return getDecoratedDao().hasImage(id);
    }

    @Override
    public boolean hasImage(String key) {
        return getDecoratedDao().hasImage(key);
    }

    @Override
	public void readAsync(Rubrics rubric, DaoReadMultiListener<T> listener) {
		new AsyncReadMultiForRubricTask(listener).execute(rubric);
	}

    @Override
    public void readLatestWOImageAsync(Rubrics rubric, int limit, DaoReadSingleListener<T> listener) {
        new AsyncReadSingleWOImageForRubricTask(listener).execute(new ReadSingleWOImageForRubricParam(rubric, limit));
    }

    @Override
    public T readLatestWOImage(Rubrics rubric, int limit) {
        return getDecoratedDao().readLatestWOImage(rubric, limit);
    }

    @Override
	public List<T> read(Rubrics rubric) {
		return getDecoratedDao().read(rubric);
	}

    @Override
    public int clearLatestFlag(Rubrics rubric) {
        return getDecoratedDao().clearLatestFlag(rubric);
    }

    @Override
    public void clearLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener) {
        new AsyncClearLatestFlagTask(listener).execute(rubric);
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
