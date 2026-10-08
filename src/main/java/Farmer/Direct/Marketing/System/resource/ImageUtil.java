package Farmer.Direct.Marketing.System.resource;

import java.util.LinkedHashMap;
import java.util.Map;

// Picks an image automatically from the product name.
// Images live in the frontend folder: farmer-frontend/public/images/
public class ImageUtil {
    private static final Map<String, String> KEYWORDS = new LinkedHashMap<>();
    static {
        // order matters: "pineapple" must be checked before "apple"
        KEYWORDS.put("mango", "mango");
        KEYWORDS.put("tomato", "tomato");
        KEYWORDS.put("potato", "potato");
        KEYWORDS.put("onion", "onion");
        KEYWORDS.put("banana", "banana");
        KEYWORDS.put("pineapple", "pineapple");
        KEYWORDS.put("watermelon", "watermelon");
        KEYWORDS.put("apple", "apple");
        KEYWORDS.put("orange", "orange");
        KEYWORDS.put("grape", "grapes");
        KEYWORDS.put("papaya", "papaya");
        KEYWORDS.put("carrot", "carrot");
        KEYWORDS.put("paddy", "rice");
        KEYWORDS.put("rice", "rice");
        KEYWORDS.put("maize", "corn");
        KEYWORDS.put("corn", "corn");
        KEYWORDS.put("wheat", "wheat");
        KEYWORDS.put("chilli", "chilli");
        KEYWORDS.put("chili", "chilli");
        KEYWORDS.put("spinach", "spinach");
        KEYWORDS.put("cauliflower", "cauliflower");
        KEYWORDS.put("cabbage", "cabbage");
    }

    public static String forName(String name) {
        String n = name == null ? "" : name.toLowerCase().trim();
        for (Map.Entry<String, String> e : KEYWORDS.entrySet()) {
            if (n.contains(e.getKey())) {
                return "/images/" + e.getValue() + ".svg";
            }
        }
        return "/images/generic.svg";
    }
}
