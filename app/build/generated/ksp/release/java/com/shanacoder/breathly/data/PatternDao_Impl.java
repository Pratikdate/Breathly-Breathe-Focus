package com.shanacoder.breathly.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PatternDao_Impl implements PatternDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PatternEntity> __insertionAdapterOfPatternEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePattern;

  public PatternDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPatternEntity = new EntityInsertionAdapter<PatternEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `patterns` (`id`,`name`,`inhale`,`hold1`,`exhale`,`hold2`,`cycles`,`colorHex`,`isFavorite`,`description`,`benefits`,`methods`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PatternEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getInhale());
        statement.bindDouble(4, entity.getHold1());
        statement.bindDouble(5, entity.getExhale());
        statement.bindDouble(6, entity.getHold2());
        statement.bindLong(7, entity.getCycles());
        statement.bindLong(8, entity.getColorHex());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getDescription());
        statement.bindString(11, entity.getBenefits());
        statement.bindString(12, entity.getMethods());
      }
    };
    this.__preparedStmtOfDeletePattern = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM patterns WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPattern(final PatternEntity pattern,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPatternEntity.insert(pattern);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePattern(final int id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePattern.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePattern.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PatternEntity>> getAllPatterns() {
    final String _sql = "SELECT * FROM patterns";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"patterns"}, new Callable<List<PatternEntity>>() {
      @Override
      @NonNull
      public List<PatternEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfInhale = CursorUtil.getColumnIndexOrThrow(_cursor, "inhale");
          final int _cursorIndexOfHold1 = CursorUtil.getColumnIndexOrThrow(_cursor, "hold1");
          final int _cursorIndexOfExhale = CursorUtil.getColumnIndexOrThrow(_cursor, "exhale");
          final int _cursorIndexOfHold2 = CursorUtil.getColumnIndexOrThrow(_cursor, "hold2");
          final int _cursorIndexOfCycles = CursorUtil.getColumnIndexOrThrow(_cursor, "cycles");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "colorHex");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfBenefits = CursorUtil.getColumnIndexOrThrow(_cursor, "benefits");
          final int _cursorIndexOfMethods = CursorUtil.getColumnIndexOrThrow(_cursor, "methods");
          final List<PatternEntity> _result = new ArrayList<PatternEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PatternEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final float _tmpInhale;
            _tmpInhale = _cursor.getFloat(_cursorIndexOfInhale);
            final float _tmpHold1;
            _tmpHold1 = _cursor.getFloat(_cursorIndexOfHold1);
            final float _tmpExhale;
            _tmpExhale = _cursor.getFloat(_cursorIndexOfExhale);
            final float _tmpHold2;
            _tmpHold2 = _cursor.getFloat(_cursorIndexOfHold2);
            final int _tmpCycles;
            _tmpCycles = _cursor.getInt(_cursorIndexOfCycles);
            final long _tmpColorHex;
            _tmpColorHex = _cursor.getLong(_cursorIndexOfColorHex);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpBenefits;
            _tmpBenefits = _cursor.getString(_cursorIndexOfBenefits);
            final String _tmpMethods;
            _tmpMethods = _cursor.getString(_cursorIndexOfMethods);
            _item = new PatternEntity(_tmpId,_tmpName,_tmpInhale,_tmpHold1,_tmpExhale,_tmpHold2,_tmpCycles,_tmpColorHex,_tmpIsFavorite,_tmpDescription,_tmpBenefits,_tmpMethods);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
