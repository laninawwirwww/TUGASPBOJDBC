import java.sql.*; // Mengimpor library JDBC
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class KasirProgram {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/kasir_database";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean loginSuccess = false;

        while (!loginSuccess) {
            System.out.println("+------------------------------------+");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            String captcha = generateCaptcha();
            System.out.print("Captcha [" + captcha + "]: ");
            String userCaptcha = scanner.nextLine();

            if (username.equalsIgnoreCase("Lani") && password.equals("170305") && userCaptcha.equals(captcha)) {
                loginSuccess = true;
                System.out.println("Login berhasil!");
            } else {
                System.out.println("Login gagal, silakan coba lagi.");
            }
        }

        System.out.println("\nSelamat Datang di GERAI IMOET!");
        Date now = new Date();
        System.out.println("Tanggal dan Waktu: " + formatDate(now));
        System.out.println("+------------------------------------+");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Koneksi ke database berhasil!");

            System.out.print("No Faktur: ");
            String noFaktur = scanner.nextLine();

            System.out.print("Kode Barang: ");
            String kodeBarang = scanner.nextLine();

            System.out.print("Nama Barang: ");
            String namaBarang = scanner.nextLine();

            System.out.print("Harga Barang: ");
            double hargaBarang = scanner.nextDouble();

            System.out.print("Jumlah Beli: ");
            int jumlahBeli = scanner.nextInt();

            scanner.nextLine();
            System.out.print("Nama Kasir: ");
            String namaKasir = capitalizeName(scanner.nextLine());

            // Create: Menyimpan data ke database
            String insertSQL = "INSERT INTO faktur (no_faktur, kode_barang, nama_barang, harga_barang, jumlah_beli, nama_kasir) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
                preparedStatement.setString(1, noFaktur);
                preparedStatement.setString(2, kodeBarang);
                preparedStatement.setString(3, namaBarang);
                preparedStatement.setDouble(4, hargaBarang);
                preparedStatement.setInt(5, jumlahBeli);
                preparedStatement.setString(6, namaKasir);
                preparedStatement.executeUpdate();
                System.out.println("Data faktur berhasil disimpan!");
            }

            // Read: Menampilkan data dari database
            System.out.println("\nData Faktur di Database:");
            String selectSQL = "SELECT * FROM faktur";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(selectSQL)) {
                while (resultSet.next()) {
                    System.out.println("No Faktur: " + resultSet.getString("no_faktur") + ", Kode Barang: " + resultSet.getString("kode_barang") + ", Nama Barang: " + resultSet.getString("nama_barang") + ", Harga Barang: " + resultSet.getDouble("harga_barang") + ", Jumlah Beli: " + resultSet.getInt("jumlah_beli") + ", Nama Kasir: " + resultSet.getString("nama_kasir"));
                }
            }

            // Update: Mengubah data di database
            System.out.print("\nMasukkan No Faktur untuk diupdate: ");
            String updateFaktur = scanner.nextLine();
            System.out.print("Nama Barang Baru: ");
            String newNamaBarang = scanner.nextLine();
            String updateSQL = "UPDATE faktur SET nama_barang = ? WHERE no_faktur = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, newNamaBarang);
                preparedStatement.setString(2, updateFaktur);
                preparedStatement.executeUpdate();
                System.out.println("Data berhasil diupdate!");
            }

            // Delete: Menghapus data dari database
            System.out.print("\nMasukkan No Faktur untuk dihapus: ");
            String deleteFaktur = scanner.nextLine();
            String deleteSQL = "DELETE FROM faktur WHERE no_faktur = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {
                preparedStatement.setString(1, deleteFaktur);
                preparedStatement.executeUpdate();
                System.out.println("Data berhasil dihapus!");
            }

        } catch (SQLException e) {
            System.out.println("Kesalahan: " + e.getMessage());
        }

        scanner.close();
        System.out.println("Program selesai.");
    }

    private static String generateCaptcha() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            captcha.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return captcha.toString();
    }

    private static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss");
        return formatter.format(date);
    }

    private static String capitalizeName(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
