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
    public static final String VAL_RESULT_SUCCESS = "success";

    public static final String KEY_CMD = "cmd";
    public static final String VAL_CMD_AUTH_INFO = "authInfo";
    public static final String VAL_CMD_STREAM_INFO = "streamInfo";

    public static final String KEY_COMMENTS = "comments";

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

            if (!result.equalsIgnoreCase(VAL_RESULT_SUCCESS)) {
                throw new ParseException("Error while parsing JSON with comments: server returned result = '" + result + "\'");
            }

            final JSONArray jsonData = json.getJSONArray("data");

            if (jsonData.length() <= 0) {
                return Comments.getEmptyComments();
            }

            final Comments comments = new Comments();

            String cmd;
            for (int i = 0; i < jsonData.length(); ++i) {
                final String val = jsonData.optString(i);

                if (val == null) {
                    continue;
                }

                final JSONObject object = new JSONObject(val);
                cmd = object.getString(KEY_CMD);

                if (cmd == null || cmd.isEmpty()) {
                    continue;
                }

                try {
                    if (cmd.equalsIgnoreCase(VAL_CMD_AUTH_INFO)) {
                        parseAuthInfo(object, comments);
                    } else if (cmd.equalsIgnoreCase(VAL_CMD_STREAM_INFO)) {
                        parseComments(object, comments);
                    }
                } catch (JSONException e) {
                    Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing comments.", e);
                    continue;
                } catch (ParseException e) {
                    Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing comments.", e);
                    continue;
                }
            }

            return comments;
        } catch (JSONException e) {
            Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing JSON with comments.", e);
            throw new ParseException("Error while parsing JSON with comments.", e);
        }
    }

    private void parseAuthInfo(JSONObject jsonAuthInfo, Comments comments) throws JSONException, ParseException {
    }

    private void parseComments(JSONObject jsonComments, Comments comments) throws JSONException, ParseException {
        JSONArray jsonCommentaArray = jsonComments.getJSONArray(KEY_COMMENTS);

        if (jsonCommentaArray.length() <= 0) {
            return;
        }

        JSONObject jsonComment;
        for (int i = 0; i < jsonCommentaArray.length(); ++i) {
            jsonComment = jsonCommentaArray.getJSONObject(i);

            try {
                comments.addComment(parseComment(jsonComment));
            } catch (JSONException e) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing comment.", e);
                continue;
            } catch (ParseException e) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error while parsing comment.", e);
                continue;
            }
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
        final String parent_id = jsonComment.optString(KEY_PARENT_ID);
        final String root_id = jsonComment.optString(KEY_ROOT_ID);
        final String text = jsonComment.getString(KEY_TEXT);
        final int state = jsonComment.optInt(KEY_STATE, Comment.STATE_ACCEPTED);
        final String nick = jsonComment.optString(KEY_NICK);
        final String acc_id = jsonComment.optString(KEY_ACCOUNT_ID);
        final int vote_up = jsonComment.optInt(KEY_VOTE_UP);
        final int vote_dn = jsonComment.optInt(KEY_VOTE_DOWN);

        return new Comment(id, root_id, parent_id, nick, time, acc_id, vote_up, vote_dn, state, text);
    }
}
