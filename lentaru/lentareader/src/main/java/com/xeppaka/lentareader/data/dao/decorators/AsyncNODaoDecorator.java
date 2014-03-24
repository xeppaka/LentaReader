package com.xeppaka.lentareader.data.dao.decorators;

import android.database.Cursor;
import android.os.AsyncTask;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;

import java.util.List;

public class AsyncNODaoDecorator<T extends NewsObject> extends AsyncDaoDecorator<T> implements AsyncNODao<T> {
	private NODao<T> decoratedDao;
	
	protected class AsyncReadMultiForRubricTask extends AsyncTask<Rubrics, Void, List<T>> {
		private AsyncListener<List<T>> listener;
		
		public AsyncReadMultiForRubricTask(AsyncListener<List<T>> listener) {
			this.listener = listener;
		}

		@Override
		protected List<T> doInBackground(Rubrics... rubric) {
			return read(rubric[0]);
		}

		@Override
		protected void onPostExecute(List<T> result) {
			listener.onSuccess(result);
		}
	}

    protected class AsyncReadMultiBriefForRubricTask extends AsyncTask<Rubrics, Void, List<T>> {
        private AsyncListener<List<T>> listener;

        public AsyncReadMultiBriefForRubricTask(AsyncListener<List<T>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<T> doInBackground(Rubrics... rubric) {
            return readBrief(rubric[0]);
        }

        @Override
        protected void onPostExecute(List<T> result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncReadAllIdsForRubric extends AsyncTask<Rubrics, Void, List<Long>> {
        private AsyncListener<List<Long>> listener;

        public AsyncReadAllIdsForRubric(AsyncListener<List<Long>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<Long> doInBackground(Rubrics... rubric) {
            return readAllIds(rubric[0]);
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            listener.onSuccess(result);
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
        private AsyncListener<T> listener;

        public AsyncReadSingleWOImageForRubricTask(AsyncListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T doInBackground(ReadSingleWOImageForRubricParam... param) {
            return readLatestWOImage(param[0].rubric, param[0].limit);
        }

        @Override
        protected void onPostExecute(T result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncReadSingleLatestForRubricTask extends AsyncTask<Rubrics, Void, T> {
        private AsyncListener<T> listener;

        public AsyncReadSingleLatestForRubricTask(AsyncListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected T doInBackground(Rubrics... param) {
            return readLatest(param[0]);
        }

        @Override
        protected void onPostExecute(T result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncClearLatestFlagTask extends AsyncTask<Rubrics, Void, Integer> {
        private AsyncListener<Integer> listener;

        public AsyncClearLatestFlagTask(AsyncListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Rubrics... rubric) {
            return clearUpdatedFromLatestFlag(rubric[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncClearUpdatedInBackgroundFlagTask extends AsyncTask<Void, Void, Integer> {
        private AsyncListener<Integer> listener;

        public AsyncClearUpdatedInBackgroundFlagTask(AsyncListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Void... param) {
            return clearUpdatedInBackgroundFlag();
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncClearRecentFlagTask extends AsyncTask<Void, Void, Integer> {
        private AsyncListener<Integer> listener;

        public AsyncClearRecentFlagTask(AsyncListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Void... param) {
            return clearRecentFlag();
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncSetLatestFlagTask extends AsyncTask<Rubrics, Void, Integer> {
        private AsyncListener<Integer> listener;

        public AsyncSetLatestFlagTask(AsyncListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Rubrics... rubric) {
            return setUpdatedFromLatestFlag(rubric[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.onSuccess(result);
        }
    }

    protected class AsyncDeleteOlderOrEqualTask extends AsyncTask<DeleteOlderOrEqualParam, Void, Integer> {
        private AsyncListener<Integer> listener;

        public AsyncDeleteOlderOrEqualTask(AsyncListener<Integer> listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(DeleteOlderOrEqualParam... params) {
            return deleteOlderOrEqual(params[0].rubric, params[0].date);
        }

        @Override
        protected void onPostExecute(Integer result) {
            listener.onSuccess(result);
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
	public AsyncTask<Rubrics, Void, List<T>> readAsync(Rubrics rubric, AsyncListener<List<T>> listener) {
		return new AsyncReadMultiForRubricTask(listener).execute(rubric);
	}

    @Override
    public AsyncTask<Void, Void, Cursor> readCursorAsync(AsyncListener<Cursor> listener) {
        return new AsyncReadCursorTask(listener).execute();
    }

    @Override
    public AsyncTask<Long, Void, Cursor> readCursorAsync(long id, AsyncListener<Cursor> listener) {
        return new AsyncReadCursorIdTask(listener).execute(id);
    }

    @Override
    public AsyncTask<Rubrics, Void, List<T>> readBriefAsync(Rubrics rubric, AsyncListener<List<T>> listener) {
        return new AsyncReadMultiBriefForRubricTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask<Rubrics, Void, T> readLatestAsync(Rubrics rubric, AsyncListener<T> listener) {
        return new AsyncReadSingleLatestForRubricTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask readLatestWOImageAsync(Rubrics rubric, int limit, AsyncListener<T> listener) {
        return new AsyncReadSingleWOImageForRubricTask(listener).execute(new ReadSingleWOImageForRubricParam(rubric, limit));
    }

    @Override
    public AsyncTask<Rubrics, Void, List<Long>> readAllIdsAsync(Rubrics rubric, AsyncListener<List<Long>> listener) {
        return new AsyncReadAllIdsForRubric(listener).execute(rubric);
    }

    @Override
    public AsyncTask<DeleteOlderOrEqualParam, Void, Integer> deleteOlderOrEqualAsync(Rubrics rubric, long date, AsyncListener<Integer> listener) {
        return new AsyncDeleteOlderOrEqualTask(listener).execute(new DeleteOlderOrEqualParam(rubric, date));
    }

    @Override
    public AsyncTask<Rubrics, Void, Integer> clearUpdatedFromLatestFlagAsync(Rubrics rubric, AsyncListener<Integer> listener) {
        return new AsyncClearLatestFlagTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask<Rubrics, Void, Integer> setUpdatedFromLatestFlagAsync(Rubrics rubric, AsyncListener<Integer> listener) {
        return new AsyncSetLatestFlagTask(listener).execute(rubric);
    }

    @Override
    public AsyncTask<Void, Void, Integer> clearUpdatedInBackgroundFlagAsync(AsyncListener<Integer> listener) {
        return new AsyncClearUpdatedInBackgroundFlagTask(listener).execute();
    }

    @Override
    public AsyncTask<Void, Void, Integer> clearRecentFlagAsync(AsyncListener<Integer> listener) {
        return new AsyncClearRecentFlagTask(listener).execute();
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
    public int clearUpdatedFromLatestFlag(Rubrics rubric) {
        return getDecoratedDao().clearUpdatedFromLatestFlag(rubric);
    }

    @Override
    public int setUpdatedFromLatestFlag(Rubrics rubric) {
        return getDecoratedDao().setUpdatedFromLatestFlag(rubric);
    }

    @Override
    public int clearRecentFlag() {
        return getDecoratedDao().clearRecentFlag();
    }

    @Override
    public int clearUpdatedInBackgroundFlag() {
        return getDecoratedDao().clearUpdatedInBackgroundFlag();
    }

    @Override
    public List<Long> readAllIds(Rubrics rubric) {
        return getDecoratedDao().readAllIds(rubric);
    }

    @Override
    public int deleteSmallerIds(long id) {
        return getDecoratedDao().deleteSmallerIds(id);
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
