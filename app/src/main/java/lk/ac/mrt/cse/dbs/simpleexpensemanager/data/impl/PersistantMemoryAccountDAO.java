package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistantMemoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private final Map<String, Account> accounts;

    public static final String DATABASE_NAME = "180668D.db";
    public static final String TABLE_NAME = "Account_Table";
    public static final String COL_1 = "ACCOUNT_NO";
    public static final String COL_2 = "BANK";
    public static final String COL_3 = "ACCOUNT_HOLDER";
    public static final String COL_4 = "BALANCE";

    public PersistantMemoryAccountDAO(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.accounts = new HashMap<>();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ACCOUNT_NO TEXT PRIMARY KEY, BANK TEXT, ACCOUNT_HOLDER TEXT, BALANCE INTEGER)");
        db.execSQL("create table " + "Transaction_Table" + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ACCOUNT_NO TEXT, TYPE TEXT, AMOUNT DOUBLE, DATE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> transactionsNew = new LinkedList<>();
        transactionsNew.clear();

//        accounts.put(account.getAccountNo(), account);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        System.out.println(res);
        //res.moveToFirst();

        while(res.moveToNext()) {
            String account_no = res.getString(0);
            String bank = res.getString(1);
            String account_holder = res.getString(2);
            double balance = res.getDouble(3);

            accounts.put(account_no, new Account(account_no, bank, account_holder, balance));
            transactionsNew.add(account_no);
//            System.out.println(account_no);
        }
//        try {
//            removeAccount("vip20");
//        } catch (InvalidAccountException e) {
//            e.printStackTrace();
//        }
        for (String i: accounts.keySet()) System.out.println(i);
        return transactionsNew;
    }

    @Override
    public List<Account> getAccountsList() {


        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {

        String account_no = account.getAccountNo();
        String bank = account.getBankName();
        String account_holder = account.getAccountHolderName();
        double balance = account.getBalance();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, account_no);
        contentValues.put(COL_2, bank);
        contentValues.put(COL_3, account_holder);
        contentValues.put(COL_4, balance);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            System.out.println("Values NOT Inserted, ERROR");
        else
            System.out.println("Values Inserted into DB");

        accounts.put(account.getAccountNo(), account);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        accounts.remove(accountNo);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(" DELETE FROM " + "Account_Table" + " WHERE " + "ACCOUNT_NO" + "=\"" + accountNo + "\";" );
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);
    }


}
