package com.gdufs.studyplatform.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gdufs.studyplatform.bean.Comment;
import com.gdufs.studyplatform.bean.Question;
import com.gdufs.studyplatform.bean.ResFile;
import com.gdufs.studyplatform.bean.User;
import com.gdufs.studyplatform.config.Constants;
import com.gdufs.studyplatform.util.LogUtils;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DATABASE = Constants.APP_NAME + ".db";
	private static final String TAG = "DBHelper";

	private static int version = 1;

	public DBHelper(Context context) {
		super(context, DATABASE, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	public void createTable(SQLiteDatabase db) {
		//user表创建语句
		String user_sql = "CREATE TABLE IF NOT EXISTS " + User.TABLE_NAME + "(" //
				+ User.ID + " INTEGER PRIMARY KEY, " //
				+ User.NICKNAME + " VARCHAR(20), " //
				+ User.IMEI + " VARCHAR(30), " //
				+ User.UPLOADCOUNT + " INTEGER, " //
				+ User.CREATETIME + " VARCHAR(20))";
		//resfile表创建语句
		String resfile_sql = "CREATE TABLE IF NOT EXISTS " + ResFile.TABLE_NAME
				+ "(" //
				+ ResFile.ID + " INTEGER PRIMARY KEY, "//
				+ ResFile.NICKNAME + " VARCHAR(20), "//
				+ ResFile.FILENAME + " VARCHAR(30), "//
				+ ResFile.URL + " VARCHAR(30)," //
				+ ResFile.TIME + " VARCHAR(20))";
		//question表创建语句
		String question_sql = "CREATE TABLE IF NOT EXISTS "
				+ Question.TABLE_NAME
				+ "(" //
				+ Question.ID
				+ " INTEGER PRIMARY KEY,  "//
				+ Question.USER_ID
				+ " INTEGER ," //
				+ Question.NICKNAME + " VARCHAR(20), " + Question.CONTENT
				+ " VARCHAR(60), " //
				+ Question.TIMESTAMP + " VARCHAR(20), " //
				+ Question.LASTRETIME + " VARCHAR(20), " //
				+ Question.FILE + " VARCHAR(3), " //
				+ Question.FILE_TYPE + " VARCHAR(2), "//
				+ Question.RECOUNT + " INT )";
		//comment表创建语句
		String comment_sql = "CREATE TABLE IF NOT EXISTS "
				+ Comment.TABLE_NAME
				+ "(" //
				+ Comment.ID
				+ " INTEGER PRIMARY KEY, " //
				+ Comment.QUESTION_ID
				+ " INTEGER, " //
				+ Comment.USER_ID
				+ " INTEGER, " //
				+ Comment.CONTENT
				+ " VARCHAR(60), " //
				+ Comment.NICKNAME + " VARCHAR(20), " + Comment.RANK
				+ " VARCHAR(6), " //
				+ Comment.TIMESTAMP + " VARCHAR(20))";
		db.execSQL(user_sql);
		db.execSQL(resfile_sql);
		db.execSQL(question_sql);
		db.execSQL(comment_sql);
		LogUtils.i(TAG, "create Database");
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public User queryUser() {
		LogUtils.i("TAG", "查询用户信息");
		User u = null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + User.TABLE_NAME, null);
		while (c.moveToNext()) {
			u = new User();
			u.setId(c.getInt(c.getColumnIndex(User.ID)));
			u.setNickname(c.getString(c.getColumnIndex(User.NICKNAME)));
			u.setImei(c.getString(c.getColumnIndex(User.IMEI)));
			u.setCreateTime(c.getString(c.getColumnIndex(User.CREATETIME)));
			u.setUploadCount(c.getInt(c.getColumnIndex(User.UPLOADCOUNT)));
		}
		c.close();
		db.close();
		return u;
	}

	public void saveUser(User user) {
		LogUtils.i("TAG", "保存用户信息");
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(
				"INSERT INTO " + User.TABLE_NAME + " (" + User.ID + ","
						+ User.NICKNAME + "," + User.IMEI + ","
						+ User.UPLOADCOUNT + "," + User.CREATETIME
						+ ") VALUES (?,?,?,?,?)",
				new String[] { String.valueOf(user.getId()),
						user.getNickname(), user.getImei(),
						String.valueOf(user.getUploadCount()),
						user.getCreateTime() });
		db.close();
	}
	/**
	 * 
	 * @param ques
	 */
	public void saveQuestion(Question ques) {
		LogUtils.i("TAG", "保存问题信息");
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("INSERT INTO " + Question.TABLE_NAME + " (" //
				+ Question.ID + "," + Question.USER_ID + ","//
				+ Question.NICKNAME + "," + Question.CONTENT + ","//
				+ Question.TIMESTAMP + "," + Question.LASTRETIME + ","//
				+ Question.FILE + "," //
				+ Question.FILE_TYPE + "," + Question.RECOUNT//
				+ ") VALUES (?,?,?,?,?,?,?,?,?)", new String[] { //
				String.valueOf(ques.getId()), String.valueOf(ques.getUserId()),//
						ques.getNickname(), ques.getContent(),//
						ques.getTimestamp(), ques.getLastretime(),//
						ques.getFile(),//
						ques.getFileType(), String.valueOf(ques.getRecount()) //
				});
		db.close();
	}
	/**
	 * 保存问题信息
	 * @param list
	 * @param userId
	 */
	public void saveQuestionList(List<Question> list,int userId){
		deleteQuestionList(userId);
		LogUtils.i("TAG", "保存一堆问题信息");
		SQLiteDatabase db = getWritableDatabase();
		for(Question ques : list){
			db.execSQL("INSERT INTO " + Question.TABLE_NAME + " (" //
					+ Question.ID + "," + Question.USER_ID + ","//
					+ Question.NICKNAME + "," + Question.CONTENT + ","//
					+ Question.TIMESTAMP + "," + Question.LASTRETIME + ","//
					+ Question.FILE + "," //
					+ Question.FILE_TYPE + "," + Question.RECOUNT//
					+ ") VALUES (?,?,?,?,?,?,?,?,?)", new String[] { //
					String.valueOf(ques.getId()), String.valueOf(ques.getUserId()),//
							ques.getNickname(), ques.getContent(),//
							ques.getTimestamp(), ques.getLastretime(),//
							ques.getFile(),//
							ques.getFileType(), String.valueOf(ques.getRecount()) //
					});
		}
		db.close();
		
	}
	/**
	 * 获取所有问题信息
	 * @return
	 */
	public List<Question> queryQuestionList() {
		return queryQuestionList(0);
	}
	/**
	 * 获取问题信息
	 * @param userId
	 * @return
	 */
	public List<Question> queryQuestionList(int userId) {
		List<Question> list = new ArrayList<Question>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = null;
		if (userId == 0) {
			cur = db.rawQuery("select * from " + Question.TABLE_NAME + " order by id desc" , null);
			
//			cur = db.query(Question.TABLE_NAME,
//					new String[] {//
//					Question.ID,
//							Question.USER_ID,
//							Question.NICKNAME,//
//							Question.CONTENT,
//							Question.FILE,
//							Question.FILE_TYPE,//
//							Question.TIMESTAMP, Question.LASTRETIME,
//							Question.RECOUNT }, null, null, null, null,
//					" id desc");
			
		} else {
			cur = db.query(Question.TABLE_NAME, new String[] {//
					Question.ID,
							Question.USER_ID,
							Question.NICKNAME,//
							Question.CONTENT,
							Question.FILE,
							Question.FILE_TYPE,//
							Question.TIMESTAMP, Question.LASTRETIME,
							Question.RECOUNT }, " userId = ? ", new String[]{String.valueOf(userId)}, null, null, " id desc");
		}
		while (cur.moveToNext()) {
			Question question = new Question();
			question.setId(cur.getInt(cur.getColumnIndex(Question.ID)));
			question.setUserId(cur.getInt(cur.getColumnIndex(Question.USER_ID)));
			question.setNickname(cur.getString(cur
					.getColumnIndex(Question.NICKNAME)));
			question.setFile(cur.getString(cur.getColumnIndex(Question.FILE)));
			question.setFileType(cur.getString(cur
					.getColumnIndex(Question.FILE_TYPE)));
			question.setContent(cur.getString(cur
					.getColumnIndex(Question.CONTENT)));
			question.setTimestamp(cur.getString(cur
					.getColumnIndex(Question.TIMESTAMP)));
			question.setLastretime(cur.getString(cur
					.getColumnIndex(Question.LASTRETIME)));
			question.setRecount(cur.getInt(cur.getColumnIndex(Question.RECOUNT)));
			list.add(question);
		}
		cur.close();
		db.close();
		return list;
	}
	/**
	 * 删除一些问题
	 * @param id
	 */
	public void deleteQuestionList(int id) {
		SQLiteDatabase db = getWritableDatabase();
		if (id == 0) {
			db.execSQL("DELETE FROM " + Question.TABLE_NAME);
		} else {
			db.execSQL("DELETE FROM " + Question.TABLE_NAME + " WHERE userId = " + id );
		}
		db.close();
	}
	/**
	 * 删除一个问题
	 * @param q_id
	 */
	public void deleQuestionById(int q_id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + Question.TABLE_NAME + " WHERE id = " + q_id);
		db.close();
	}
	/**
	 * 根据问题id获取回复
	 * @param q_id
	 * @return
	 */
	public List<Comment> queryCommentByQid(int q_id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = db.query(Comment.TABLE_NAME, new String[] {//
				Comment.ID,
				Comment.USER_ID,
				Comment.NICKNAME,//
				Comment.CONTENT,
				Comment.QUESTION_ID,
				Comment.TIMESTAMP,  }, " questionId = ? ", new String[]{String.valueOf(q_id)}, null, null, " id desc");
		return null;
	}
	/**
	 * 保存回复信息
	 * @param data
	 * @param id
	 */
	public void saveComment(List<Comment> data, int id) {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + Comment.TABLE_NAME + " WHERE questionId = " + id);
		for(Comment c : data){
			LogUtils.i("TAG", "保存问题信息");
			db.execSQL("INSERT INTO " + Comment.TABLE_NAME + " (" //
					+ Comment.ID + "," + Comment.USER_ID + ","//
					+ Comment.NICKNAME + "," + Comment.CONTENT + ","//
					+ Comment.TIMESTAMP + "," //
					+ Comment.QUESTION_ID 
					+ ") VALUES (?,?,?,?,?,?)", new String[] { //
					String.valueOf(c.getId()), String.valueOf(c.getUserId()),//
							c.getNickname(), c.getContent(),//
							c.getTimestamp(), String.valueOf(c.getQuestionId()) //
					});
		}
		db.close();
	}

	public List<ResFile> queryResFileList(String nickname) {
		List<ResFile> list = new ArrayList<ResFile>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cur = null;
		if(nickname == null){
			cur = db.rawQuery("select * from " + ResFile.TABLE_NAME + " order by id desc" , null);
		} else {
			cur = db.query(ResFile.TABLE_NAME, new String[] {//
					ResFile.ID,//
					ResFile.NICKNAME,//
					ResFile.FILENAME,//
					ResFile.URL,//
					ResFile.TIME }, " nickname = ? ", new String[]{nickname}, null, null, " id desc");
		}
		while (cur.moveToNext()) {
			ResFile resFile = new ResFile();
			resFile.setId(cur.getInt(cur.getColumnIndex(ResFile.ID)));
			resFile.setNickname(cur.getString(cur.getColumnIndex(ResFile.NICKNAME)));
			resFile.setFilename(cur.getString(cur.getColumnIndex(ResFile.FILENAME)));
			resFile.setUrl(cur.getString(cur.getColumnIndex(ResFile.URL)));
			resFile.setTime(cur.getString(cur.getColumnIndex(ResFile.TIME)));
			list.add(resFile);
		}
		if(cur!= null){
			cur.close();
		}
		db.close();
		return list;
	}

	public void saveResfileList(List<ResFile> list) {
		deleResFile();
		LogUtils.i("TAG", "保存资源信息");
		SQLiteDatabase db = getWritableDatabase();
		for(ResFile r : list){
			db.execSQL("INSERT INTO " + ResFile.TABLE_NAME + " (" //
					+ ResFile.ID + "," + ResFile.NICKNAME +","
					+ ResFile.FILENAME + "," + ResFile.URL + ","
					+ ResFile.TIME
					+ ") VALUES (?,?,?,?,?)", new String[] { //
					String.valueOf(r.getId()),r.getNickname(),//
					r.getFilename(),r.getUrl(),//
					r.getTime()//
					});
		}
		db.close();
	}

	private void deleResFile() {
		LogUtils.i("TAG", "删除资源信息");
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM " + ResFile.TABLE_NAME );
		db.close();
	}

}
