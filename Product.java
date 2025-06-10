// package src;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    
    // TODO: 1) Buat constructor lengkap (id, name, category, price, quantity)
    public Product(int id, String name, String category, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }
    
    // TODO: 2) Buat getters & setters untuk semua field
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    /** Konversi objek ke baris CSV: id,name,category,price,quantity */
    public String toCsvLine() {
        // TODO: kembalikan: id + "," + name + "," + category + "," + price + "," + quantity
        return id + "," + name + "," + category + "," + price + "," + quantity;
    }
    
    /** Buat objek Product dari satu baris CSV */
    public static Product fromCsvLine(String line) {
        // TODO: split(",", limit=5), parsing tiap elemen, lalu return new Product(...)
        String[] parts = line.split(",", 5);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Format CSV tidak valid: " + line);
        }
        
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String category = parts[2].trim();
        double price = Double.parseDouble(parts[3].trim());
        int quantity = Integer.parseInt(parts[4].trim());
        
        return new Product(id, name, category, price, quantity);
    }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %-20s | %-15s | Rp %.2f | Stok: %d", 
                           id, name, category, price, quantity);
    }
}