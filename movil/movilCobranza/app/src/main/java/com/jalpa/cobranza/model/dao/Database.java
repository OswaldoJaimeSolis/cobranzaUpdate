package com.jalpa.cobranza.model.dao;

import android.content.Context;

import androidx.room.Room;

public class Database {

    private Context mCtx;
    private static Database mInstance;

    //our app database object
    private AppDataBase appDatabase;

    private Database(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDataBase.class, "cobranza").fallbackToDestructiveMigration().build();
    }

    public static synchronized Database getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new Database(mCtx);
        }
        return mInstance;
    }

    public AppDataBase getAppDatabase() {
        return appDatabase;
    }
}
