// package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class InventoryService {
    
    /** Buat folder data jika belum ada */
    public void initDataDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            System.out.println("Folder data/ berhasil dibuat");
        }
    }
    
    /** Load daftar produk dari CSV */
    public List<Product> loadProducts(Path csvPath) throws IOException {
        List<Product> products = new ArrayList<>();
        
        // jika file ada, baca header, loop setiap baris
        if (Files.exists(csvPath)) {
            List<String> lines = Files.readAllLines(csvPath);
            
            // Skip header jika ada
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    try {
                        Product product = Product.fromCsvLine(line);
                        products.add(product);
                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
        
        return products;
    }
    
    /** Simpan daftar produk ke CSV */
    public void saveProducts(List<Product> products, Path csvPath) throws IOException {
        List<String> lines = new ArrayList<>();
        
        // tulis header: "id,name,category,price,quantity"
        lines.add("id,name,category,price,quantity");
        
        // loop products, tulis setiap toCsvLine()
        for (Product product : products) {
            lines.add(product.toCsvLine());
        }
        
        Files.write(csvPath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    /** Pencarian nama produk (substring match) */
    public List<Product> searchProducts(List<Product> products, String keyword) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /** Sortir produk berdasarkan "price" atau "quantity" */
    public void sortProducts(List<Product> products, String criteria) {
        if ("price".equalsIgnoreCase(criteria)) {
            products.sort(Comparator.comparingDouble(Product::getPrice));
        } else if ("quantity".equalsIgnoreCase(criteria)) {
            products.sort(Comparator.comparingInt(Product::getQuantity));
        }
    }
    
    /** Filter produk berdasarkan rentang harga [min..max] */
    public List<Product> filterByPrice(List<Product> products, double min, double max) {
        return products.stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .collect(Collectors.toList());
    }
    
    /** Menu interaktif */
    public void runMenu(List<Product> products) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\n=== INVENTORY MANAGER ===");
            System.out.println("1. Lihat semua");
            System.out.println("2. Tambah produk");
            System.out.println("3. Update stok");
            System.out.println("4. Hapus produk");
            System.out.println("5. Cari produk");
            System.out.println("6. Sort produk");
            System.out.println("7. Filter harga");
            System.out.println("8. Simpan & Keluar");
            System.out.print("Pilih: ");
            
            String choice = sc.nextLine();
            
            switch (choice) {
                case "1":
                    viewAll(products);
                    break;
                case "2":
                    addProduct(products, sc);
                    break;
                case "3":
                    updateQuantity(products, sc);
                    break;
                case "4":
                    deleteProduct(products, sc);
                    break;
                case "5":
                    System.out.print("Masukkan kata kunci pencarian: ");
                    String keyword = sc.nextLine();
                    List<Product> searchResults = searchProducts(products, keyword);
                    System.out.println("\nHasil pencarian (" + searchResults.size() + " produk):");
                    viewProducts(searchResults);
                    break;
                case "6":
                    System.out.print("Sortir berdasarkan (price/quantity): ");
                    String criteria = sc.nextLine();
                    sortProducts(products, criteria);
                    System.out.println("\nProduk berhasil diurutkan berdasarkan " + criteria + ":");
                    viewAll(products);
                    break;
                case "7":
                    System.out.print("Harga minimum: ");
                    double min = Double.parseDouble(sc.nextLine());
                    System.out.print("Harga maksimum: ");
                    double max = Double.parseDouble(sc.nextLine());
                    List<Product> filterResults = filterByPrice(products, min, max);
                    System.out.println("\nHasil filter harga (" + filterResults.size() + " produk):");
                    viewProducts(filterResults);
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    private void viewAll(List<Product> products) {
        System.out.println("\n=== DAFTAR PRODUK ===");
        if (products.isEmpty()) {
            System.out.println("Tidak ada produk dalam inventory.");
        } else {
            viewProducts(products);
        }
    }
    
    private void viewProducts(List<Product> products) {
        System.out.println("-".repeat(80));
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println("-".repeat(80));
        System.out.println("Total: " + products.size() + " produk");
    }
    
    private void addProduct(List<Product> products, Scanner sc) {
        System.out.println("\n=== TAMBAH PRODUK BARU ===");
        
        // Generate ID baru
        int newId = products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
        
        System.out.print("Nama produk: ");
        String name = sc.nextLine();
        
        System.out.print("Kategori: ");
        String category = sc.nextLine();
        
        System.out.print("Harga: ");
        double price = Double.parseDouble(sc.nextLine());
        
        System.out.print("Kuantitas: ");
        int quantity = Integer.parseInt(sc.nextLine());
        
        Product newProduct = new Product(newId, name, category, price, quantity);
        products.add(newProduct);
        
        System.out.println("Produk berhasil ditambahkan dengan ID: " + newId);
    }
    
    private void updateQuantity(List<Product> products, Scanner sc) {
        System.out.println("\n=== UPDATE STOK PRODUK ===");
        
        System.out.print("Masukkan ID produk: ");
        int id = Integer.parseInt(sc.nextLine());
        
        Product product = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (product == null) {
            System.out.println("Produk dengan ID " + id + " tidak ditemukan.");
            return;
        }
        
        System.out.println("Produk ditemukan: " + product.getName());
        System.out.println("Stok saat ini: " + product.getQuantity());
        System.out.print("Stok baru: ");
        int newQuantity = Integer.parseInt(sc.nextLine());
        
        product.setQuantity(newQuantity);
        System.out.println("Stok berhasil diperbarui.");
    }
    
    private void deleteProduct(List<Product> products, Scanner sc) {
        System.out.println("\n=== HAPUS PRODUK ===");
        
        System.out.print("Masukkan ID produk yang akan dihapus: ");
        int id = Integer.parseInt(sc.nextLine());
        
        Product product = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (product == null) {
            System.out.println("Produk dengan ID " + id + " tidak ditemukan.");
            return;
        }
        
        System.out.println("Produk yang akan dihapus: " + product.getName());
        System.out.print("Yakin ingin menghapus? (y/n): ");
        String confirm = sc.nextLine();
        
        if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
            products.remove(product);
            System.out.println("Produk berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
}