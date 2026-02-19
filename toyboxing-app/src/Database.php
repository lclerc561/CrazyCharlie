<?php

class Database {
    private static $host = '127.0.0.1';
    private static $db_name = 'crazy_charly_day';
    private static $username = 'root';
    private static $password = '';
    
    private static $conn = null;
    public static function getConnection() {
        if (self::$conn === null) {
            try {
                self::$conn = new PDO(
                    "mysql:host=" . self::$host . ";dbname=" . self::$db_name . ";charset=utf8mb4", 
                    self::$username, 
                    self::$password
                );
                self::$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
                self::$conn->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
                
            } catch(PDOException $exception) {
                die("<div style='color:red; font-weight:bold; padding:20px;'>Erreur de connexion à la base de données : " . $exception->getMessage() . "</div>");
            }
        }
        return self::$conn;
    }
}