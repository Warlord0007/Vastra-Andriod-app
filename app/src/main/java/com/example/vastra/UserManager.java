package com.example.vastra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class UserManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";

    public UserManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "full_name TEXT,"
                + "username TEXT UNIQUE,"
                + "email TEXT,"
                + "phone TEXT,"
                + "password TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create products table
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT,"
                + "description TEXT,"
                + "image BLOB,"
                + "price REAL" + ")"; // Removed isSimilar column
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Upgrade logic from version 1 to version 2
            String ADD_PRICE_COLUMN = "ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN price REAL";
            db.execSQL(ADD_PRICE_COLUMN);
        }
    }

    // User Management
    public boolean registerUser(String fullName, String username, String email, String phone, String password) {
        if (fullName == null || username == null || email == null || phone == null || password == null) {
            return false; // Ensure no null values
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("username", username);
            values.put("email", email);
            values.put("phone", phone);
            values.put("password", password);

            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public String validateLogin(String username, String password) {
        if ("admin".equals(username) && "admin123".equals(password)) {
            return "admin"; // Special case for admin
        }

        if (username == null || password == null) {
            return null; // Ensure no null values
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE "
                    + "username = ? AND "
                    + "password = ?";
            cursor = db.rawQuery(query, new String[]{username, password});

            if (cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndex("username");
                if (usernameIndex != -1) {
                    return cursor.getString(usernameIndex); // Return the username
                } else {
                    Log.e("UserManager", "Column 'username' not found in query result.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null; // Return null if the user is not found
    }

    public boolean isValidLogin(String username, String password) {
        return validateLogin(username, password) != null;
    }

    public User getUserByUsername(String username) {
        if (username == null) {
            return null; // Ensure no null values
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USERS + " WHERE username = ?";
            cursor = db.rawQuery(query, new String[]{username});

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                int fullNameIndex = cursor.getColumnIndex("full_name");
                int emailIndex = cursor.getColumnIndex("email");
                int phoneIndex = cursor.getColumnIndex("phone");

                if (idIndex != -1 && fullNameIndex != -1 && emailIndex != -1 && phoneIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String fullName = cursor.getString(fullNameIndex);
                    String email = cursor.getString(emailIndex);
                    String phone = cursor.getString(phoneIndex);

                    return new User(id, fullName, username, email, phone);
                } else {
                    Log.e("UserManager", "One or more columns not found in query result.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    public boolean updateUser(int id, String fullName, String username, String email, String phone, String password) {
        if (id <= 0 || fullName == null || username == null || email == null || phone == null || password == null) {
            return false; // Ensure valid ID and no null values
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("full_name", fullName);
            values.put("username", username);
            values.put("email", email);
            values.put("phone", phone);
            values.put("password", password);

            int rowsAffected = db.update(TABLE_USERS, values, "id = ?", new String[]{String.valueOf(id)});
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public boolean deleteUser(int id) {
        if (id <= 0) {
            return false; // Ensure valid ID
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rowsDeleted = db.delete(TABLE_USERS, "id = ?", new String[]{String.valueOf(id)});
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_USERS,           // Table name
                    new String[]{"id", "full_name", "username", "email", "phone"}, // Columns to return
                    null,                     // No selection criteria
                    null,                     // No selection arguments
                    null,                     // No group by
                    null,                     // No having
                    null                      // No order by
            );

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
                    String username = cursor.getString(cursor.getColumnIndex("username"));
                    String email = cursor.getString(cursor.getColumnIndex("email"));
                    String phone = cursor.getString(cursor.getColumnIndex("phone"));

                    User user = new User(id, fullName, username, email, phone);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return userList;
    }

    // Product Management
    public boolean addProduct(String name, String description, byte[] image, double price) {
        if (name == null || description == null || image == null || price <= 0) {
            return false; // Ensure no null values and valid price
        }

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("description", description);
            values.put("image", image);
            values.put("price", price);

            long result = db.insert(TABLE_PRODUCTS, null, values);
            return result != -1;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
            return false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(
                    TABLE_PRODUCTS,        // Table name
                    new String[]{"id", "name", "description", "image", "price"}, // Columns to return
                    null,                   // No selection criteria
                    null,                   // No selection arguments
                    null,                   // No group by
                    null,                   // No having
                    null                    // No order by
            );

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                    double price = cursor.getDouble(cursor.getColumnIndex("price"));

                    Product product = new Product(id, name, description, image, price);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return productList;
    }
}
