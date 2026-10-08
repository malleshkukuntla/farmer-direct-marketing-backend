package Farmer.Direct.Marketing.System.resource;

import Farmer.Direct.Marketing.System.entity.Person;
import java.util.LinkedHashMap;
import java.util.Map;

// Small helper methods: validation + safe user data (never returns the password)
public class Check {

    public static void required(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new ApiException(400, field + " is required");
        }
    }

    public static String t(String s) {
        return s == null ? null : s.trim();
    }

    public static void clean(Person p) {
        p.name = t(p.name);
        p.mobile = t(p.mobile);
        p.email = p.email == null ? null : p.email.trim().toLowerCase();
        p.state = t(p.state);
        p.district = t(p.district);
        p.mandal = t(p.mandal);
        p.village = t(p.village);
    }

    public static void person(Person p) {
        required(p.name, "Name");
        if (!p.name.matches("[A-Za-z ]+")) {
            throw new ApiException(400, "Name must contain only letters and spaces");
        }
        required(p.mobile, "Mobile number");
        if (!p.mobile.matches("\\d{10}")) {
            throw new ApiException(400, "Mobile number must be exactly 10 digits");
        }
        required(p.email, "Email");
        if (!p.email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ApiException(400, "Enter a valid email address");
        }
        required(p.password, "Password");
        if (p.password.length() < 6) {
            throw new ApiException(400, "Password must be at least 6 characters");
        }
        required(p.state, "State");
        required(p.district, "District");
        required(p.mandal, "Mandal");
        required(p.village, "Village");
    }

    public static String location(Person p) {
        return p.village + ", " + p.mandal + ", " + p.district + ", " + p.state;
    }

    public static Map<String, Object> safe(Person p, String role) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.id);
        m.put("name", p.name);
        m.put("mobile", p.mobile);
        m.put("email", p.email);
        m.put("state", p.state);
        m.put("district", p.district);
        m.put("mandal", p.mandal);
        m.put("village", p.village);
        m.put("role", role);
        return m;
    }
}
