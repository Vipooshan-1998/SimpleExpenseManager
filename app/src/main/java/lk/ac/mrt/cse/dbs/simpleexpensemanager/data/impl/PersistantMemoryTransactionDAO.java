package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class PersistantMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String DATABASE_NAME = "180668D.db";
    public static final String TABLE_NAME = "Transaction_Table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "ACCOUNT_NO";
    public static final String COL_3 = "TYPE";
    public static final String COL_4 = "AMOUNT";
    public static final String COL_5 = "DATE";

    private List<Transaction> transactions;

    public PersistantMemoryTransactionDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ACCOUNT_NO TEXT, TYPE TEXT, AMOUNT DOUBLE, DATE TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

//    public boolean insertData (String account_no, String type, int amount, String date ){
//
//    }

    // interface methods implementation

    @Override
    public void logTransaction(String date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);

        String account_no = transaction.getAccountNo();
        ExpenseType type = transaction.getExpenseType();
        String strtype = type.toString();
        double amount1 = transaction.getAmount();
        String strDate = transaction.getDate();
  //      String strDate = date1.toString();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, account_no);
        contentValues.put(COL_3, strtype);
        contentValues.put(COL_4, amount1);
        contentValues.put(COL_5, strDate);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            System.out.println("Values NOT Inserted, ERROR");
        else
            System.out.println("Values Inserted into DB");

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions.clear();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);

        //res.moveToFirst();

        while(res.moveToNext()){
             String account_no = res.getString(1);
             String stype = res.getString(2);
             ExpenseType type;
             System.out.println((ExpenseType.INCOME).toString());
             if (stype.equals((ExpenseType.INCOME).toString())) type = ExpenseType.INCOME;
             else type = ExpenseType.EXPENSE;

             String date = res.getString(4);
             double amount = res.getInt(3);

             transactions.add(new Transaction(date, account_no, type, amount));

//             res.moveToNext();
        }

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
