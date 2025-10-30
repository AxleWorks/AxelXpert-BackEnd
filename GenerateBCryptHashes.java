import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHashes {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String[] passwords = {
            "pass",
            "123456",
            "12345678",
            "Chathumal@12",
            "abcdefghij",
            "hashinupass",
            "12345789",
            "admin1234",
            "#Sjn123*",
            "nimhansineth"
        };
        
        System.out.println("-- BCrypt hashes for migration script:\n");
        
        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("-- '" + password + "'");
            System.out.println("UPDATE user SET password = '" + hash + "' WHERE password = '" + password + "';");
            System.out.println();
        }
    }
}
