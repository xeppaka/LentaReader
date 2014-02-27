package com.xeppaka.lentareader.parser.comments;

import android.util.Log;

import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.parser.exceptions.ParseException;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by nnm on 2/26/14.
 */
public class CommentsParser {
    private final String datePattern = "EEE, dd MMM yyyy HH:mm:ss Z";
    private final SimpleDateFormat commentsDateParser = new SimpleDateFormat(datePattern, Locale.US);

    public static final String KEY_RESULT = "result";
    public static final String VALUE_RESULT_SUCCESS = "result";

    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "id";
    public static final String KEY_PARENT_ID = "parent_id";
    public static final String KEY_ROOT_ID = "root_id";
    public static final String KEY_TEXT = "text";
    public static final String KEY_STATE = "state";
    public static final String KEY_NICK = "nick";
    public static final String KEY_ACCOUNT_ID = "acc_id";
    public static final String KEY_VOTE_UP = "vote_up";
    public static final String KEY_VOTE_DOWN = "vote_dn";

    public Comments parse(String commentsJson) throws ParseException {
        try {
            JSONObject json = new JSONObject(commentsJson);
            final String result = json.getString(KEY_RESULT);

            if (!result.equalsIgnoreCase(VALUE_RESULT_SUCCESS)) {
                throw new ParseException("Error while parsing JSON with comments: server returned result = '" + result + "\'");
            }

            final JSONArray jsonArray = json.getJSONArray("comments");

            if (jsonArray.length() <= 0) {
                return Comments.getEmptyComments();
            }

            final Comments comments = new Comments();
            JSONObject jsonComment;
            for (int i = 0; i < jsonArray.length(); ++i) {
                jsonComment = jsonArray.getJSONObject(i);
                comments.addComment(parseComment(jsonComment));
            }

            return comments;
        } catch (JSONException e) {
            Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing JSON with comments.", e);
            throw new ParseException("Error while parsing JSON with comments.", e);
        }
    }

    private Comment parseComment(JSONObject jsonComment) throws JSONException, ParseException {
        final String timeStr = jsonComment.getString(KEY_TIME);
        long time;
        try {
            time = commentsDateParser.parse(timeStr).getTime();
        } catch (java.text.ParseException e) {
            throw new ParseException("Error while parsing time in JSON with comments. Time string: '" + timeStr + "'", e);
        }
        final String id = jsonComment.getString(KEY_ID);
        final String parent_id = jsonComment.getString(KEY_PARENT_ID);
        final String root_id = jsonComment.getString(KEY_ROOT_ID);
        final String text = jsonComment.getString(KEY_TEXT);
        final int state = jsonComment.getInt(KEY_STATE);
        final String nick = jsonComment.getString(KEY_NICK);
        final String acc_id = jsonComment.getString(KEY_ACCOUNT_ID);
        final int vote_up = jsonComment.getInt(KEY_VOTE_UP);
        final int vote_dn = jsonComment.getInt(KEY_VOTE_DOWN);

        return new Comment(id, root_id, parent_id, nick, time, acc_id, vote_up, vote_dn, state, text);
    }
}
