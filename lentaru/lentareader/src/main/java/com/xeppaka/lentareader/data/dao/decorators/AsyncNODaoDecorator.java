package com.xeppaka.lentareader.data.dao.decorators;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;

import java.util.List;

public class AsyncNODaoDecorator<T extends NewsObject> extends AsyncDaoDecorator<T> implements AsyncNODao<T> {
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

    protected class AsyncReadMultiBriefForRubricTask extends AsyncTask<Rubrics, Void, List<T>> {
        private AsyncDao.DaoReadMultiListener<T> listener;

        public AsyncReadMultiBriefForRubricTask(AsyncDao.DaoReadMultiListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected List<T> doInBackground(Rubrics... rubric) {
            return readBrief(rubric[0]);
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

    private static class DeleteOlderOrEqualParam {
        private Rubrics rubric;
        private long date;

        private DeleteOlderOrEqualParam(Rubrics rubric, long date) {
            this.rubric = rubric;
            this.date = date;
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

    protected class AsyncReadSingleLatestForRubricTask extends AsyncTask<Rubrics, Void, T> {
        private AsyncDao.DaoReadSingleListener<T> listener;

        public AsyncReadSingleLatestForRubricTask(AsyncDao.DaoReadSingleListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T doInBackground(Rubrics... param) {
            return readLatest(param[0]);
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

    protected class AsyncSetLatestFlagTask extends AsyncTask<Rubrics, Void, Integer> {
        private DaoUpdateListener listener;

        public AsyncSetLatestFlagTask(DaoUpdateListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Rubrics... rubric) {
            return setLatestFlag(rubric[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.finished(result);
        }
    }

    protected class AsyncDeleteOlderOrEqualTask extends AsyncTask<DeleteOlderOrEqualParam, Void, Integer> {
        private AsyncDao.DaoDeleteListener listener;

        public AsyncDeleteOlderOrEqualTask(AsyncDao.DaoDeleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(DeleteOlderOrEqualParam... params) {
            return deleteOlderOrEqual(params[0].rubric, params[0].date);
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
	public AsyncTask<Rubrics, Void, List<T>> readAsync(Rubrics rubric, DaoReadMultiListener<T> listener) {
		return new AsyncReadMultiForRubricTask(listener).execute(rubric);
	}

    @Override
    public AsyncTask<Rubrics, Void, List<T>> readBriefAsync(Rubrics rubric, DaoReadMultiListener<T> listener) {
        return new AsyncReadMultiBriefForRubricTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask<Rubrics, Void, T> readLatestAsync(Rubrics rubric, DaoReadSingleListener<T> listener) {
        return new AsyncReadSingleLatestForRubricTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask readLatestWOImageAsync(Rubrics rubric, int limit, DaoReadSingleListener<T> listener) {
        return new AsyncReadSingleWOImageForRubricTask(listener).execute(new ReadSingleWOImageForRubricParam(rubric, limit));
    }

    @Override
    public AsyncTask<DeleteOlderOrEqualParam, Void, Integer> deleteOlderOrEqualAsync(Rubrics rubric, long date, AsyncDao.DaoDeleteListener listener) {
        return new AsyncDeleteOlderOrEqualTask(listener).execute(new DeleteOlderOrEqualParam(rubric, date));
    }

    @Override
    public AsyncTask<Rubrics, Void, Integer> clearLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener) {
        return new AsyncClearLatestFlagTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask<Rubrics, Void, Integer> setLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener) {
        return new AsyncSetLatestFlagTask(listener).execute(rubric);
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
    public List<T> readBrief(Rubrics rubric) {
        return getDecoratedDao().readBrief(rubric);
    }

    @Override
    public T readLatest(Rubrics rubric) {
        return getDecoratedDao().readLatest(rubric);
    }

    @Override
    public int deleteOlderOrEqual(Rubrics rubric, long date) {
        return getDecoratedDao().deleteOlderOrEqual(rubric, date);
    }

    @Override
    public int clearLatestFlag(Rubrics rubric) {
        return getDecoratedDao().clearLatestFlag(rubric);
    }

    @Override
    public int setLatestFlag(Rubrics rubric) {
        return getDecoratedDao().setLatestFlag(rubric);
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
