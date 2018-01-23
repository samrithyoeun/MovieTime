package yoeun.samrith.movie_time;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

/**
 * Created by Mister_Brown on 11/21/2016.
 */

public class Database extends SQLiteAssetHelper {
    private final static String DB_NAME = "DBMovie.sqlite";
    private static Database instance;

    public static Database getData(Context context){
        if(instance==null){
            instance = new Database(context);
        }
        return instance;
    }

    public Database(Context context) {
        super(context, DB_NAME,null,1);
    }

    public ArrayList<Cinema> getData(){
        ArrayList<Cinema> data = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cu = db.rawQuery("select * from tblMovie",null);
        cu.moveToFirst();

        while(!cu.isAfterLast()){

            data.add(new Cinema(
                    cu.getInt(0),
                    cu.getString(1),
                    Float.valueOf(cu.getString(2)),
                    Float.valueOf(cu.getString(3)),
                    cu.getString(4),
                    cu.getString(5),
                    cu.getString(6),
                    cu.getString(7),
                    cu.getString(8)

                    ));


            cu.moveToNext();
        }
        Log.d("getCOunt",""+cu.getCount());

        return data;
    }

    public Cursor getCursorData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cu = db.rawQuery("select * from tblMovie",null);
        return cu;
    }




}