// Fichier de test pour vérifier la détection de vulnérabilités
public class TestVulnerabilities {

    // Test 1: Injection SQL - DEVRAIT ÊTRE DÉTECTÉ
    public String unsafeSearch(String name) {
        String sql = "SELECT * FROM users WHERE name = '" + name + "'";
        return sql;
    }

    // Test 2: XSS - DEVRAIT ÊTRE DÉTECTÉ
    @GetMapping("/echo")
    public String echo(@RequestParam String input) {
        return "<h1>" + input + "</h1>";
    }

    // Test 3: Secret exposé - DEVRAIT ÊTRE DÉTECTÉ
    private String apiKey = "sk_live_1234567890abcdef";

    // Test 4: Injection de commandes - DEVRAIT ÊTRE DÉTECTÉ
    public void executeCommand(String userInput) {
        Runtime.getRuntime().exec(userInput);
    }
}
