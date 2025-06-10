// package src;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        Path dataDir = Paths.get("data");
        Path csvFile = dataDir.resolve("products.csv");
        
        InventoryService service = new InventoryService();
        
        // TODO: 1) Inisialisasi folder data
        service.initDataDirectory(dataDir);
        
        // TODO: 2) Load data dari CSV
        List<Product> products = service.loadProducts(csvFile);
        System.out.println("Data produk berhasil dimuat: " + products.size() + " produk");
        
        // TODO: 3) Jalankan menu interaktif
        service.runMenu(products);
        
        // TODO: 4) Simpan data kembali ke CSV sebelum keluar
        service.saveProducts(products, csvFile);
        System.out.println("Data berhasil disimpan ke " + csvFile);
    }
}